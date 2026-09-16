package idv.const_x.http;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2023-02-10
 */
public abstract class AbsInvokeResultHandler implements HttpInvokeResultHandler {

    @Override
    public void handle(HttpURLConnection connection, int code, BufferedReader reader,
                       String redirectUrl, Map<String, String> cookies) {
        String message = null;
        try {
            message = connection.getResponseMessage();
        } catch (Exception ignored) {
            handleException(ignored);
        }

        String result = null;
        if (reader != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                result = sb.toString();
            } catch (Exception e) {
                handleException(e);
                return;
            }
        }

        handResult(connection,code, message, result, redirectUrl, cookies);
    }

    protected abstract void handResult(HttpURLConnection connection,int code, String messsage, String result,
                                       String redirectUrl, Map<String, String> cookies);

}
