package com.ericbarch.bitpulselib.remote;

public interface IResponseRunnable {
	public void onResult(String data);
	public void onError(int statusCode);
}