package com.ericbarch.bitpulselib.exchanges;

import java.util.ArrayList;

import com.ericbarch.bitpulselib.remote.ExchangeRequest;

public interface IHTTPExchange {
	public ArrayList<ExchangeRequest> getRequests();
	public void onDestroy();
}