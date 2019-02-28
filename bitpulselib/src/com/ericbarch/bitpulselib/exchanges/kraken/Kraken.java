package com.ericbarch.bitpulselib.exchanges.kraken;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.ericbarch.bitpulselib.exchanges.ExchangeManager;
import com.ericbarch.bitpulselib.exchanges.IHTTPExchange;
import com.ericbarch.bitpulselib.remote.ExchangeRequest;
import com.ericbarch.bitpulselib.remote.IResponseRunnable;
import com.ericbarch.bitpulselib.util.Utilities;

public class Kraken implements IHTTPExchange {
	private ArrayList<ExchangeRequest> requests = new ArrayList<ExchangeRequest>(1);
	
	public Kraken(final int currency, final ExchangeManager mgr) {
		final String currencyString = Utilities.currencyNameForConstant(currency).toUpperCase();
		
		// ticker request
		requests.add(new ExchangeRequest("https://api.kraken.com/0/public/Ticker?pair=XXBTZ" + currencyString, new IResponseRunnable() {
			@Override
			public void onResult(String data) {
				mgr.onConnect();
				
				// parse JSON, feed results to manager
				JSONObject json;
				try {
					// parse out the data
					json = new JSONObject(data);
					
					JSONObject marketData = json.getJSONObject("result").getJSONObject("XXBTZ" + currencyString);
					
					final double ask = Double.parseDouble(marketData.getJSONArray("a").getString(0));
					final double bid = Double.parseDouble(marketData.getJSONArray("b").getString(0));
					final double last = Double.parseDouble(marketData.getJSONArray("c").getString(0));
					final double vol = Double.parseDouble(marketData.getJSONArray("t").getString(1));

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