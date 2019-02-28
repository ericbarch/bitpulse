package com.ericbarch.bitpulselib.exchanges.history.bitcoinaverage;

import java.util.ArrayList;

import com.ericbarch.bitpulselib.History;
import com.ericbarch.bitpulselib.exchanges.HistoryManager;
import com.ericbarch.bitpulselib.exchanges.IHTTPExchange;
import com.ericbarch.bitpulselib.remote.ExchangeRequest;
import com.ericbarch.bitpulselib.remote.IResponseRunnable;
import com.ericbarch.bitpulselib.util.Utilities;

public class BitcoinAverageHistory implements IHTTPExchange {
	private ArrayList<ExchangeRequest> requests = new ArrayList<ExchangeRequest>(1);
	
	public BitcoinAverageHistory(final int currency, final int historyType, final HistoryManager mgr) {
		final String currencyString = Utilities.currencyNameForConstant(currency);
		
		String requestUrl;
		
		if (historyType == History.ONE_DAY)
			requestUrl = "https://api.bitcoinaverage.com/history/" + currencyString + "/per_minute_24h_sliding_window.csv";
		else
			requestUrl = "https://api.bitcoinaverage.com/history/" + currencyString + "/per_hour_monthly_sliding_window.csv";
		
		// ticker request
		requests.add(new ExchangeRequest(requestUrl, new IResponseRunnable() {
			@Override
			public void onResult(String data) {
				mgr.onData(data);
			}
			
			@Override
			public void onError(int statusCode) {
				mgr.onFail(statusCode);
			}
		}));
	}
	
	@Override
	public ArrayList<ExchangeRequest> getRequests() {
		return requests;
	}
	
	@Override
	public void onDestroy() {

	}
}