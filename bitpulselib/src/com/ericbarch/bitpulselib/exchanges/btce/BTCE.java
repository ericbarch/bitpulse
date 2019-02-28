package com.ericbarch.bitpulselib.exchanges.btce;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import com.ericbarch.bitpulselib.exchanges.ExchangeManager;
import com.ericbarch.bitpulselib.exchanges.IHTTPExchange;
import com.ericbarch.bitpulselib.remote.ExchangeRequest;
import com.ericbarch.bitpulselib.remote.IResponseRunnable;
import com.ericbarch.bitpulselib.util.Utilities;

@SuppressLint("DefaultLocale")
public class BTCE implements IHTTPExchange {
	private ArrayList<ExchangeRequest> requests = new ArrayList<ExchangeRequest>(1);
	
	public BTCE(final int currency, final ExchangeManager mgr) {
		final String currencyString = Utilities.currencyNameForConstant(currency).toLowerCase();
		
		// ticker request
		requests.add(new ExchangeRequest("https://btc-e.com/api/3/ticker/btc_" + currencyString, new IResponseRunnable() {
			@Override
			public void onResult(String data) {
				mgr.onConnect();
				
				// parse JSON, feed results to manager
				JSONObject json;
				try {
					// parse out the data
					json = new JSONObject(data);
					final double bid = json.getJSONObject("btc_" + currencyString).getDouble("sell");
					final double ask = json.getJSONObject("btc_" + currencyString).getDouble("buy");
					final double last = json.getJSONObject("btc_" + currencyString).getDouble("last");
					final double vol = json.getJSONObject("btc_" + currencyString).getDouble("vol_cur");
					
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