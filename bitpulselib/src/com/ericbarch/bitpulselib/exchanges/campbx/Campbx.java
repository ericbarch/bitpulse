package com.ericbarch.bitpulselib.exchanges.campbx;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.ericbarch.bitpulselib.Currencies;
import com.ericbarch.bitpulselib.exchanges.ExchangeManager;
import com.ericbarch.bitpulselib.exchanges.IHTTPExchange;
import com.ericbarch.bitpulselib.remote.ExchangeRequest;
import com.ericbarch.bitpulselib.remote.IResponseRunnable;

public class Campbx implements IHTTPExchange {
	private ArrayList<ExchangeRequest> requests = new ArrayList<ExchangeRequest>(1);
	
	public Campbx(final int currency, final ExchangeManager mgr) {
		// campbx does not give us volume
		mgr.onVolumeUnsupported();
		
		// ticker request
		requests.add(new ExchangeRequest("http://campbx.com/api/xticker.php", new IResponseRunnable() {
			@Override
			public void onResult(String data) {
				mgr.onConnect();
				
				// parse JSON, feed results to manager
				JSONObject json;
				try {
					// parse out the data
					json = new JSONObject(data);
					final double bid = Double.parseDouble(json.getString("Best Bid"));
					final double ask = Double.parseDouble(json.getString("Best Ask"));
					final double last = Double.parseDouble(json.getString("Last Trade"));
					
					// feed results to manager
					mgr.onBid(bid, Currencies.USD);
					mgr.onAsk(ask, currency);
					mgr.onPrice(last, currency);
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