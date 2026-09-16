package idv.const_x.http;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultInvokeHandler extends AbsInvokeResultHandler{

	private String result = null;

	private int code = 200;

	private String redirectUrl = null;

	private String messsage = null;

	private String contentType = null;

	private Map<String, String> headerFields;

	private Map<String, String> setCookies;

	public String getResult() {
		return result;
	}

	public int getCode() {
		return code;
	}

	public String getMesssage() {
		return messsage;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public Map<String, String> getSetCookies() {
		return setCookies;
	}

	public String getContentType() {
		return contentType;
	}

	public Map<String, String> getHeaderFields() {
		return headerFields;
	}

	public String getHeaderField(String key) {
		return headerFields.get(key);
	}



	@Override
	protected void handResult(HttpURLConnection connection, int code, String messsage, String result,
                              String redirectUrl, Map<String, String> cookies) {
		this.code = code;
		this.messsage = messsage;
		this.result = result;
		this.redirectUrl = redirectUrl;
		this.setCookies = cookies;
		this.contentType = connection.getContentType();
		this.headerFields = new HashMap<>();
		Map<String, List<String>> headerFields1 = connection.getHeaderFields();
        if (headerFields1 != null) {
			for (Map.Entry<String, List<String>> entry : headerFields1.entrySet()) {
				this.headerFields.put(entry.getKey(),entry.getValue() == null || entry.getValue().size() == 0?null:entry.getValue().get(0));
			}
        }
	}

	@Override
	public void handleException(Exception e) {
		throw new RuntimeException(e);
	}
}
