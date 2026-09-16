package idv.const_x.http;


import java.util.HashMap;
import java.util.Map;

public class HttpInvokerParams {

	public HttpInvokerParams(String baseUrl, HttpInvokeResultHandler handler) {
		this.baseUrl = baseUrl;
		this.handler = handler;
	}

	protected String baseUrl = "";
	protected String method = "GET";
	protected Map<String, Object> params = new HashMap<String, Object>();
	protected Map<String, String> headers = new HashMap<String, String>();

	protected String encoding = "UTF-8";
	protected String requestBody = null;
	String bodyContentType = null;

	protected HttpInvokeResultHandler handler;

	protected Integer connectTimeout, readTimeout;


 
	public HttpInvokerParams setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		return this;
				
	}

	public HttpInvokerParams setMethod(String method) {
		this.method = method;
		return this;
	}


	public HttpInvokerParams setParams(Map<String, Object> params) {
		this.params = params;
		return this;
	}
	
	public HttpInvokerParams addParams(String name, Object value) {
		this.params.put(name, value);
		return this;
	}

	public HttpInvokerParams setHeaders(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}

	public HttpInvokerParams addHeaders(String name, String value) {
		this.headers.put(name, value);
		return this;
	}
	
	public HttpInvokerParams setEncoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	public HttpInvokerParams setHandler(HttpInvokeResultHandler handler) {
		this.handler = handler;
		return this;
	}

	public HttpInvokerParams setRequestBody(String requestBody,String contentType) {
		this.requestBody = requestBody;
		this.bodyContentType = contentType;
		return this;
	}

	public HttpInvokerParams setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public HttpInvokerParams setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getMethod() {
		return method;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getEncoding() {
		return encoding;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public HttpInvokeResultHandler getHandler() {
		return handler;
	}

	public Integer getConnectTimeout() {
		return connectTimeout;
	}

	public Integer getReadTimeout() {
		return readTimeout;
	}
}
