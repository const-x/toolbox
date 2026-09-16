package idv.const_x.http;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.util.Map;

public interface HttpInvokeResultHandler {

	void handle(HttpURLConnection connection, int code, BufferedReader reader,
			String redirectUrl, Map<String, String> cookies);

	void handleException(Exception e);
	
}
