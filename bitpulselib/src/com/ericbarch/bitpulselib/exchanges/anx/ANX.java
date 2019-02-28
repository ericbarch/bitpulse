package com.ericbarch.bitpulselib.exchanges.anx;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.ericbarch.bitpulselib.exchanges.ExchangeManager;
import com.ericbarch.bitpulselib.exchanges.IHTTPExchange;
import com.ericbarch.bitpulselib.remote.ExchangeRequest;
import com.ericbarch.bitpulselib.remote.IResponseRunnable;
import com.ericbarch.bitpulselib.util.Utilities;

public class ANX implements IHTTPExchange {
	private ArrayList<ExchangeRequest> requests = new ArrayList<ExchangeRequest>(1);
	
	public ANX(final int currency, final ExchangeManager mgr) {
		final String currencyString = Utilities.currencyNameForConstant(currency);
		
		// ticker request
		requests.add(new ExchangeRequest("https://anxpro.com/api/2/BTC" + currencyString + "/money/ticker", new IResponseRunnable() {
			@Override
			public void onResult(String data) {
				mgr.onConnect();
				
				// parse JSON, feed results to manager
				JSONObject json;
				try {
					// parse out the data
					json = (new JSONObject(data)).getJSONObject("data");
					final double bid = Double.parseDouble(json.getJSONObject("buy").getString("value"));
					final double ask = Double.parseDouble(json.getJSONObject("sell").getString("value"));
					final double last = Double.parseDouble(json.getJSONObject("last").getString("value"));
					final double vol = Double.parseDouble(json.getJSONObject("vol").getString("value"));
					
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