package com.ericbarch.bitpulselib.exchanges.bitfinex;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.ericbarch.bitpulselib.Currencies;
import com.ericbarch.bitpulselib.exchanges.ExchangeManager;
import com.ericbarch.bitpulselib.exchanges.IHTTPExchange;
import com.ericbarch.bitpulselib.remote.ExchangeRequest;
import com.ericbarch.bitpulselib.remote.IResponseRunnable;

public class Bitfinex implements IHTTPExchange {
	private ArrayList<ExchangeRequest> requests = new ArrayList<ExchangeRequest>(1);
	
	public Bitfinex(final int currency, final ExchangeManager mgr) {
		// ticker request
		requests.add(new ExchangeRequest("https://api.bitfinex.com/v1/ticker/btcusd", new IResponseRunnable() {
			@Override
			public void onResult(String data) {
				mgr.onConnect();
				
				// parse JSON, feed results to manager
				JSONObject json;
				try {
					// parse out the data
					json = new JSONObject(data);
					final double bid = Double.parseDouble(json.getString("bid"));
					final double ask = Double.parseDouble(json.getString("ask"));
					final double last = Double.parseDouble(json.getString("last_price"));
					
					// feed results to manager
					mgr.onBid(bid, Currencies.USD);
					mgr.onAsk(ask, Currencies.USD);
					mgr.onPrice(last, Currencies.USD);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(int statusCode) {
				mgr.onConnectionError();
			}
		}));
		
		// volume request
		requests.add(new ExchangeRequest("https://api.bitfinex.com/v1/today/btcusd", new IResponseRunnable() {
			@Override
			public void onResult(String data) {
				mgr.onConnect();
				
				// parse JSON, feed results to manager
				JSONObject json;
				try {
					// parse out the data
					json = new JSONObject(data);
					final double vol = Double.parseDouble(json.getString("volume"));
					
					// feed results to manager
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