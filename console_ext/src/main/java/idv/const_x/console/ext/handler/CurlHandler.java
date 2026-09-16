package idv.const_x.console.ext.handler;

import com.alibaba.fastjson.JSON;
import idv.const_x.console.handler.AbsInputLineHandler;
import idv.const_x.console.handler.HelpBuilder;
import idv.const_x.console.io.ConsoleIOProxy;
import idv.const_x.utils.OSUtils;
import idv.const_x.utils.StringExtUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/** 根据 curl 命令构建 HTTP 请求并输出响应。 */
public class CurlHandler extends AbsInputLineHandler {

    @Override
    public void handleLine(String line, ConsoleIOProxy console, Map<String, String> options, String... params) {
        try {
            Request request = parse(tokenize(line));
            execute(request, console);
        } catch (Exception e) {
            console.error("curl 请求失败: " + e.getMessage());
        }
    }

    private Request parse(List<String> tokens) {
        if (tokens.isEmpty() || !"curl".equalsIgnoreCase(tokens.get(0))) {
            throw new IllegalArgumentException("命令必须以 curl 开头");
        }

        Request request = new Request();
        for (int i = 1; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if ("--url".equals(token) || "-u".equals(token) || "--user".equals(token)
                    || "-X".equals(token) || "--request".equals(token)
                    || "-A".equals(token) || "--user-agent".equals(token)
                    || "-e".equals(token) || "--referer".equals(token)
                    || "-H".equals(token) || "--header".equals(token)
                    || "-b".equals(token) || "--cookie".equals(token)
                    || "-d".equals(token) || "--data".equals(token)
                    || "--data-raw".equals(token) || "--data-binary".equals(token)
                    || "--connect-timeout".equals(token) || "--max-time".equals(token)) {
                if (++i >= tokens.size()) {
                    throw new IllegalArgumentException("选项 " + token + " 缺少值");
                }
                String value = tokens.get(i);
                if ("--url".equals(token)) {
                    request.url = value;
                } else if ("-X".equals(token) || "--request".equals(token)) {
                    request.method = value.toUpperCase(Locale.ROOT);
                } else if ("-H".equals(token) || "--header".equals(token)) {
                    addHeader(request, value);
                } else if ("-b".equals(token) || "--cookie".equals(token)) {
                    request.headers.add(new Header("Cookie", value));
                } else if ("-A".equals(token) || "--user-agent".equals(token)) {
                    request.headers.add(new Header("User-Agent", value));
                } else if ("-e".equals(token) || "--referer".equals(token)) {
                    request.headers.add(new Header("Referer", value));
                } else if ("-u".equals(token) || "--user".equals(token)) {
                    request.headers.add(new Header("Authorization", "Basic " +
                            java.util.Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.ISO_8859_1))));
                } else if ("--connect-timeout".equals(token)) {
                    request.connectTimeout = secondsToMillis(value, token);
                } else if ("--max-time".equals(token)) {
                    request.readTimeout = secondsToMillis(value, token);
                } else if ("-d".equals(token) || token.startsWith("--data")) {
                    request.data.add(value);
                    if (request.method == null) {
                        request.method = "POST";
                    }
                }
            } else if ("-G".equals(token) || "--get".equals(token)) {
                request.getData = true;
                request.method = "GET";
            } else if ("-L".equals(token) || "--location".equals(token)) {
                request.followRedirects = true;
            } else if ("-k".equals(token) || "--insecure".equals(token) || "--compressed".equals(token)
                    || "-s".equals(token) || "--silent".equals(token) || "-i".equals(token)) {
                if ("-k".equals(token) || "--insecure".equals(token)) {
                    request.insecure = true;
                }
                if ("--compressed".equals(token)) {
                    request.compressed = true;
                }
            } else if (token.startsWith("-")) {
                throw new IllegalArgumentException("暂不支持 curl 选项: " + token);
            } else if (request.url == null) {
                request.url = token;
            } else {
                throw new IllegalArgumentException("无法识别的参数: " + token);
            }
        }
        if (StringExtUtils.isBlank(request.url)) {
            throw new IllegalArgumentException("缺少请求 URL");
        }
        if (request.method == null) {
            request.method = request.data.isEmpty() ? "GET" : "POST";
        }
        return request;
    }

    private void execute(Request request, ConsoleIOProxy console) throws IOException {
        String url = request.url;
        String body = String.join("&", request.data);
        if (request.getData && !body.isEmpty()) {
            url += (url.contains("?") ? "&" : "?") + body;
            body = null;
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        if (request.insecure && connection instanceof HttpsURLConnection) {
            HttpsURLConnection https = (HttpsURLConnection) connection;
            https.setSSLSocketFactory(insecureSocketFactory());
            https.setHostnameVerifier(insecureHostnameVerifier());
        }
        connection.setRequestMethod(request.method);
        connection.setInstanceFollowRedirects(request.followRedirects);
        connection.setConnectTimeout(request.connectTimeout);
        connection.setReadTimeout(request.readTimeout);
        if (request.compressed) {
            connection.setRequestProperty("Accept-Encoding", "gzip");
        }
        for (Header header : request.headers) {
            connection.addRequestProperty(header.name, header.value);
        }
        if (body != null) {
            connection.setDoOutput(true);
            try (OutputStream output = connection.getOutputStream()) {
                output.write(body.getBytes(StandardCharsets.UTF_8));
            }
        }

        int status = connection.getResponseCode();
        console.info(connection.getResponseMessage() == null ? String.valueOf(status)
                : status + " " + connection.getResponseMessage());


        InputStream input = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
        if (input != null) {
            if ("gzip".equalsIgnoreCase(connection.getHeaderField("Content-Encoding"))) {
                input = new GZIPInputStream(input);
            }
            String result = formatJson(new String(readAll(input), detectCharset(connection)));
            if (result.length() > 100) {
                try {
                    OSUtils.setSysClipboardText(result);
                    console.println("返回结果超过100个字符，已复制到粘贴板");
                } catch (Exception e) {
                    console.error("返回结果超过100个字符，复制到粘贴板失败: " + e.getMessage());
                }
            } else {
                console.println(result);
            }
        }
        connection.disconnect();
    }

    private static String formatJson(String result) {
        try {
            return JSON.toJSONString(JSON.parse(result), true);
        } catch (Exception ignored) {
            return result;
        }
    }

    private static void addHeader(Request request, String value) {
        int separator = value.indexOf(':');
        if (separator <= 0) {
            throw new IllegalArgumentException("请求头格式错误: " + value);
        }
        request.headers.add(new Header(value.substring(0, separator).trim(), value.substring(separator + 1).trim()));
    }

    private static Charset detectCharset(HttpURLConnection connection) {
        String contentType = connection.getContentType();
        if (contentType != null) {
            for (String part : contentType.split(";")) {
                if (part.trim().toLowerCase(Locale.ROOT).startsWith("charset=")) {
                    try {
                        return Charset.forName(part.substring(part.indexOf('=') + 1).trim());
                    } catch (Exception ignored) {
                        return StandardCharsets.UTF_8;
                    }
                }
            }
        }
        return StandardCharsets.UTF_8;
    }

    private static byte[] readAll(InputStream input) throws IOException {
        try (InputStream in = input; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = in.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            return output.toByteArray();
        }
    }

    private static int secondsToMillis(String value, String option) {
        try {
            double seconds = Double.parseDouble(value);
            if (seconds < 0 || seconds > Integer.MAX_VALUE / 1000.0) {
                throw new NumberFormatException();
            }
            return (int) (seconds * 1000);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("选项 " + option + " 的值必须是非负秒数: " + value);
        }
    }

    private static SSLSocketFactory insecureSocketFactory() {
        try {
            TrustManager[] trustAll = {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[0]; }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
            }};
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustAll, new java.security.SecureRandom());
            return context.getSocketFactory();
        } catch (Exception e) {
            throw new IllegalStateException("无法启用 --insecure", e);
        }
    }

    private static HostnameVerifier insecureHostnameVerifier() {
        return (hostname, session) -> true;
    }

    static List<String> tokenize(String command) {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        char quote = 0;
        boolean escaped = false;
        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);
            if (escaped) {
                if (c != '\n' && c != '\r') {
                    token.append(c);
                }
                escaped = false;
            } else if (c == '\\' && quote != '\'') {
                escaped = true;
            } else if (quote != 0) {
                if (c == quote) {
                    quote = 0;
                } else {
                    token.append(c);
                }
            } else if (c == '\'' || c == '"') {
                quote = c;
            } else if (Character.isWhitespace(c)) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
            } else {
                token.append(c);
            }
        }
        if (escaped) {
            token.append('\\');
        }
        if (quote != 0) {
            throw new IllegalArgumentException("curl 引号未闭合");
        }
        if (token.length() > 0) {
            tokens.add(token.toString());
        }
        return tokens;
    }

    @Override
    public String getCmd() {
        return "curl";
    }

    @Override
    public HelpBuilder initHelpBuilder() {
        return new HelpBuilder(getCmd(), "根据 curl 命令发起 HTTP 请求并打印响应").build();
    }

    private static class Request {
        private String url;
        private String method;
        private boolean getData;
        private boolean followRedirects;
        private boolean compressed;
        private boolean insecure;
        private int connectTimeout = 30000;
        private int readTimeout = 120000;
        private final List<Header> headers = new ArrayList<>();
        private final List<String> data = new ArrayList<>();
    }

    private static class Header {
        private final String name;
        private final String value;

        private Header(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    public static void main(String[] args) {
        String line = "curl --url 'https://erp3.superboss.cc/trade/search/print' \\\n" +
                "  -H 'accept: application/json, text/javascript, */*; q=0.01' \\\n" +
                "  -H 'accept-language: zh-CN,zh;q=0.9,en;q=0.8' \\\n" +
                "  -H 'bx-v: 2.5.11' \\\n" +
                "  -H 'cache-control: no-cache' \\\n" +
                "  -H 'companyid: 441630' \\\n" +
                "  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \\\n" +
                "  -b '_ati=9891890236472; superuseragent1=4003a5039f0e8f7a5ba7cc0603669c089e9e77724e6c62093d768ecdbefa044b6f28f1cb9f111da020fe442e802bb9dd7db2083301dcb8d1ee8dcd062c830a8cb8bcb576a87cde656d1383b848ef44b23afa0cc116db0662096d81133f6128919ec592ef28fea29c092d40dfc472453185bcd8acca072660527823276f35b526; _tj_validating_staff=492791088400896; lastLoginTime_492791088400896=1788226965000; _tj_censeid=e8156480191f2e44fd3f5a910e5e1ed242e22b3a; _tj_cookie=492791088400896; hideDeadline=0; _tha_test_censeid=bdbb01d19b52fba8acab6007401f44efa7980f1d; ray-authentication=98ce32bb2df94a7da8e464af05cc0eb4_220.168.93.218_1789002049721_songyue.xyl; _new_tj_censeid=927eeabdb466e4381555cefb6003c70824ca2eaa; index_daoqi_dlg=89112; _tj_intercept_login_erp=1e6dc194-4645-476b-949e-d82046408aba; _censeid=0b7aff6266c88b5df16fffc06fac434f00cf2070; _shatg=c66dd6a1-5db9-44d8-9b43-bb719657d940; 3AB9D23F7A4B3C9B=LGTM4EXBUMU7A6ZPP2I6F266IFWIRJJ2AMLCZTYPRFHWAHZMFTTFTND7WH47XDP6YOIGURKTAFITVPRFK7IMITVDD4; super_memSessionId1=d1be5c1bc6c5ed4b81f8b01751b8df3ae5488847c783fa8cd9dca406e9f9c618d55e4192ceabc1807d47e74198f27a65c4165153c12155df9ffac6330cb940ab; tfstk=gVEZWz4wjmVIagfuU8mVa8uetyoO0mWCNjqjnx2YHKcijO30nxcBfFifBq04t-Kso1CTTiPUZPObnoM00WhxsngfWx-qt-DbC-GXo2cnjqUsGfGq0-FPd_s5VRetDcf5Ng_xnwVr2FxMjIiH-xmqIIG0dqwtDmfQ8-fBmRFNG1tZsmXExYM2Im0DjMvn9xTiiSci-BDxHmc0iSmntYDJsjxMmvxn9xmmiSmcLJcK3mc0imXUKfVTnR-EFVjkh92TzLO1MbyiTnx0jiniaZDIpXqEQVlo-6xy4tHZ7b2iT1Oq0Lu4_VrCbQHa-8PKQSIJXc04Szmg0_SZx8UQsvPcaEl3zlEqPl5yyfqtM7og86xZmcDz34aCnUlQoJEE8l7WHvE_nl3SqgdiGywz0xrAMMPzI5zorlAN4vdxK16UDPRDuVDKLb6FLX9e8yZJJXqBkE3hvvl5CATDoQ1rLb6iSELx-ckENOM1.' \\\n" +
                "  -H 'module-path: /trade/printv2/' \\\n" +
                "  -H 'origin: https://erp3.superboss.cc' \\\n" +
                "  -H 'pragma: no-cache' \\\n" +
                "  -H 'priority: u=1, i' \\\n" +
                "  -H 'referer: https://erp3.superboss.cc/index.html' \\\n" +
                "  -H 'sec-ch-ua: \"Chromium\";v=\"152\", \"Not?A_Brand\";v=\"24\", \"Google Chrome\";v=\"152\"' \\\n" +
                "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                "  -H 'sec-ch-ua-platform: \"macOS\"' \\\n" +
                "  -H 'sec-fetch-dest: empty' \\\n" +
                "  -H 'sec-fetch-mode: cors' \\\n" +
                "  -H 'sec-fetch-site: same-origin' \\\n" +
                "  -H 'trackid: trackid1789034259682_66207' \\\n" +
                "  -H 'user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/152.0.0.0 Safari/537.36' \\\n" +
                "  -H 'x-requested-with: XMLHttpRequest' \\\n" +
                "  --data-raw 'api_name=trade_search_print&queryId=77&module=printv2&_t=1789034259328&pageNo=1&orderIdTypeSelect=mixKey&deliverStatus=&expressStatus=&buyerNick=&userId=&secondShopQueryParams=%5B%5D&expressCompanyIds=&warehouseId=689295&remainTimeHours=&timeoutActionTimeAfter=&cnStartTime=&cnEndTime=&isCancel=&key=skuRemark&queryType=0&spcAuthorName=&spcAuthorId=&excludeSellerFlags=&sellerFlag=&itemTagIdsParamsStr=&text=&skuProp=&platSkuProp=&skuOuterId=&itemTitle=&highlight=false&field=pay_time&outerId=&onlyOuterIdAndSysSkuIdType=0&containMemo=&containOutSid=&inWave=&waveId=&hasMixKey=true&order=desc&_module=printv2&outerIdAndSysSkuIds=&mixKey=5972641489451168&pageSize=50&useHasNext=1&orderFields=id%2Coid%2Cskuid%2Ctitle%2CsysTitle%2CshortTitle%2CskuPropertiesName%2CsysSkuPropertiesAlias%2CsysOuterId%2CsysRemark%2CouterId%2CplatSkuPropertiesName%2CpicPath%2CsysPicPath%2Cnum%2CrefundStatus%2CmainOuterId%2CgiftNum%2Csource%2Ctype%2CplatOuterId%2CplatFormTitle%2CouterIid%2CskuShortTitle%2CorderExt%2CplatGiftOrder%2CgoodsSectionCodes&needOrder=0&useCompress=1&minutesAfterPaidOrderAreNotDisplayed=0'";
        new CurlHandler().handleLine(line, ConsoleIOProxy.getInstance("UTF-8"),null,null);
    }
}
