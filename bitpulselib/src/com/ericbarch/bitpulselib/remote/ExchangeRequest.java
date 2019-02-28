package com.ericbarch.bitpulselib.remote;

public class ExchangeRequest {
	private String url;
	private IResponseRunnable runnable;
	
	public ExchangeRequest(String url, IResponseRunnable runnable) {
		this.url = url;
		this.runnable = runnable;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void result(String data) {
		runnable.onResult(data);
	}
	
	public void error(int statusCode) {
		runnable.onError(statusCode);
	}
}