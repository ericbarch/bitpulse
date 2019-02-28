package com.ericbarch.bitpulselib.exchanges.bitcoinaverage;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.ericbarch.bitpulselib.exchanges.ExchangeManager;
import com.ericbarch.bitpulselib.exchanges.IHTTPExchange;
import com.ericbarch.bitpulselib.remote.ExchangeRequest;
import com.ericbarch.bitpulselib.remote.IResponseRunnable;
import com.ericbarch.bitpulselib.util.Utilities;

public class BitcoinAverage implements IHTTPExchange {
	private ArrayList<ExchangeRequest> requests = new ArrayList<ExchangeRequest>(1);
	
	public BitcoinAverage(final int currency, final ExchangeManager mgr) {
		final String currencyString = Utilities.currencyNameForConstant(currency);
		
		// ticker request
		requests.add(new ExchangeRequest("https://api.bitcoinaverage.com/ticker/" + currencyString, new IResponseRunnable() {
			@Override
			public void onResult(String data) {
				mgr.onConnect();
				
				// parse JSON, feed results to manager
				JSONObject json;
				try {
					// parse out the data
					json = new JSONObject(data);
					final double bid = json.getDouble("bid");
					final double ask = json.getDouble("ask");
					final double last = json.getDouble("last");
					final double vol = json.getDouble("total_vol");
					
					// feed results to manager
					mgr.onBid(bid, currency);
					mgr.onAsk(ask, currency);
					mgr.onPrice(last, currency);
					mgr.onVolume(vol);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(int statusCode) {
				mgr.onConnectionError();
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