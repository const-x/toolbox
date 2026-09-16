package idv.const_x.http;

import idv.const_x.utils.StringExtUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class HttpInvoker {

	public static final String GET = "GET";

	public static final String HEAD = "HEAD";

	public static final String DELETE = "DELETE";

	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String PATCH = "PATCH";



	public static void invoke(String baseUrl, String method, Map<String,Object> params, Map<String,String> hearder, HttpInvokeResultHandler handler)  {
		invoke(baseUrl, method, params, hearder, null,null, "UTF-8",null,null, handler);
	}

	public static void invoke(String baseUrl,String method,HttpInvokeResultHandler handler) throws IOException {
		invoke(baseUrl, method, null, null, null, null,"UTF-8",null,null, handler);
	}

	public static void invoke(HttpInvokerParams builder) {
		invoke(builder.baseUrl, builder.method, builder.params, builder.headers, builder.requestBody,builder.bodyContentType,
				builder.encoding,builder.connectTimeout,builder.readTimeout, builder.handler);
	}

	private static void invoke(String baseUrl,String method,Map<String,Object> params,Map<String,String> header,String body,String bodyContentType,String encoding,Integer connectTimeout,Integer readTimeout,HttpInvokeResultHandler handler) {

		HttpURLConnection connection = null;
		BufferedReader reader = null;
		if (StringExtUtils.isBlank(encoding)) {
			encoding = "UTF-8";
		}
		try{
			if (!StringExtUtils.isBlank(body)) {
				if (StringExtUtils.isBlank(bodyContentType)) {
					bodyContentType = header == null?null:header.get("Content-Type");
				}
				if (StringExtUtils.isBlank(bodyContentType)) {
					bodyContentType ="application/json; charset="+encoding;
				}
			}

			if(method.equalsIgnoreCase(POST) || method.equalsIgnoreCase(PUT) || method.equalsIgnoreCase(PATCH)){
				connection = invokeByPost(baseUrl,method,params,body,bodyContentType,header,encoding,connectTimeout,readTimeout);
			}else{
				connection = invokeByGet(baseUrl,method,params,body,bodyContentType,header,encoding,connectTimeout,readTimeout);
			}
		    int code = connection.getResponseCode();
			InputStream responseStream = null;
            if (code >= 400) {
				responseStream = connection.getErrorStream();
				if (responseStream != null) {
					reader = new BufferedReader(new InputStreamReader(responseStream, encoding));
				}
            }
            if (reader == null) {
				responseStream = connection.getInputStream();
				if (responseStream != null) {
					reader = new BufferedReader(new InputStreamReader(responseStream, encoding));
				}
            }
			String redirectUrl = connection.getHeaderField("Location");
			Map<String, String> cookies = extractCookies(connection);
			handler.handle(connection, code, reader, redirectUrl, cookies);
            if (reader != null) {
				reader.close();
            }
		}catch (Exception e){
			handler.handleException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ignored) {}
			}
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private static Map<String, String> extractCookies(HttpURLConnection connection) {
		Map<String, String> cookies = new HashMap<>();
		for (Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
			if (header.getKey() == null || !"Set-Cookie".equalsIgnoreCase(header.getKey())
					|| header.getValue() == null) {
				continue;
			}
			for (String setCookie : header.getValue()) {
			if (StringExtUtils.isBlank(setCookie)) {
				continue;
			}
			String[] cookieParts = setCookie.split(";", 2);
			String[] nameAndValue = cookieParts[0].split("=", 2);
			if (nameAndValue.length != 2 || StringExtUtils.isBlank(nameAndValue[0])) {
				continue;
			}
			cookies.put(nameAndValue[0].trim(), nameAndValue[1].trim());
			}
		}
		return cookies;
	}

	private static HttpURLConnection invokeByGet(String baseUrl,String method,Map<String,Object> params,String reqBody,String bodyContentType,Map<String,String> headers,String encoding,Integer connectTimeout,Integer readTimeout) throws IOException{

		String content = joinParams(params, encoding);

		StringBuilder urlString = new StringBuilder(baseUrl);
		if(StringExtUtils.isNotBlank(content.toString())){
			if(baseUrl.contains("?")){
				urlString.append("&");
			}else{
				urlString.append("?");
			}
			urlString.append(content);
		}

		URL url = new URL(urlString.toString());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if (connectTimeout != null) {
			connection.setConnectTimeout(connectTimeout);
		}
		if (readTimeout != null) {
			connection.setReadTimeout(readTimeout);
		}
		// 设置请求方式，默认为GET
		if (method != null) {
			connection.setRequestMethod(method);
		}
		if(headers != null){
			for(Entry<String,String> entry:headers.entrySet()){
				connection.addRequestProperty(entry.getKey(), entry.getValue());
			}
		}

		if (!StringExtUtils.isBlank(reqBody)) {
			connection.setRequestProperty("Content-Type", bodyContentType);
			connection.setDoOutput(true);
			// 写入请求体
			try (OutputStream os = connection.getOutputStream()) {
				byte[] input = reqBody.getBytes(encoding);
				os.write(input, 0, input.length);
				os.flush();
				os.close();
			}
		}else {
			// 建立与服务器的连接，并未发送数据
			connection.connect();
		}

		return connection;

	}

	private static String joinParams(Map<String, Object> params, String encoding) {
		// 使用URLEncoder.encode对特殊和不可见字符进行编码
		StringBuilder content = new StringBuilder();
		if(params != null && params.size() > 0 ){
			for(Entry<String,Object> entry: params.entrySet()){
				String encode = null;
				try {
					encode = URLEncoder.encode(entry.getValue().toString(), encoding);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
				content.append(entry.getKey()).append("=").append(encode).append("&");
			}
			content.deleteCharAt(content.length() - 1);
		}
		return content.toString();
	}

	private static HttpURLConnection invokeByPost(String baseUrl,String method,Map<String,Object> params,String reqBody,String bodyContentType,Map<String,String> headers,String encoding,Integer connectTimeout,Integer readTimeout) throws IOException{
		StringBuilder urlString = new StringBuilder(baseUrl);
		//既有请求体 又有参数 把参数放path上
		if (reqBody != null && params != null && !params.isEmpty()) {
			String content = joinParams(params, encoding);
			if(StringExtUtils.isNotBlank(content.toString())){
				if(baseUrl.contains("?")){
					urlString.append("&");
				}else{
					urlString.append("?");
				}
				urlString.append(content);
			}
		}
		URL url = new URL(urlString.toString());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if (connectTimeout != null) {
			connection.setConnectTimeout(connectTimeout);
		}
		if (readTimeout != null) {
			connection.setReadTimeout(readTimeout);
		}
		// 打开读写属性，默认均为false
		connection.setDoOutput(true);
		connection.setDoInput(true);
		// 设置请求方式，默认为GET
		connection.setRequestMethod(method);
		// Post 请求不能使用缓存
		connection.setUseCaches(false);
		
		connection.setInstanceFollowRedirects(true);

		//setRequestProperty主要是设置HttpURLConnection请求头里面的属性  比如Cookie、User-Agent（浏览器类型）等
		if(headers != null){
			for(Entry<String,String> entry:headers.entrySet()){
				connection.addRequestProperty(entry.getKey(), entry.getValue());
			}
			// 配置连接的Content-type，配置为application/x-www-form-urlencoded的意思是正文是urlencoded编码过的form参数
			if (!headers.containsKey("Content-Type")) {
				connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			}
		}

		if (!StringExtUtils.isBlank(reqBody)) {
			connection.setRequestProperty("Content-Type", bodyContentType);
			// 写入请求体
			try (OutputStream os = connection.getOutputStream()) {
				byte[] input = reqBody.getBytes(encoding);
				os.write(input, 0, input.length);
				os.flush();
				os.close();
			}
		}else {
			String content = joinParams(params, encoding);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// 写入请求体
			try (OutputStream os = connection.getOutputStream()) {
				byte[] input = content.getBytes(encoding);
				os.write(input, 0, input.length);
				os.flush();
				os.close();
			}
		}

		return connection;
	}
	
	
	
	
}
