package com.ericbarch.bitpulselib.exchanges.coinbase;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.ericbarch.bitpulselib.Currencies;
import com.ericbarch.bitpulselib.exchanges.ExchangeManager;
import com.ericbarch.bitpulselib.exchanges.IHTTPExchange;
import com.ericbarch.bitpulselib.remote.ExchangeRequest;
import com.ericbarch.bitpulselib.remote.IResponseRunnable;

public class Coinbase implements IHTTPExchange {
	private ArrayList<ExchangeRequest> requests = new ArrayList<ExchangeRequest>(1);
	
	public Coinbase(final int currency, final ExchangeManager mgr) {
		// coinbase does not provide volume data
		mgr.onVolumeUnsupported();
		
		// bid
		requests.add(new ExchangeRequest("https://coinbase.com/api/v1/prices/sell", new IResponseRunnable() {
			@Override
			public void onResult(String data) {
				mgr.onConnect();
				
				// parse JSON, feed results to manager
				JSONObject json;
				try {
					// parse out the data
					json = new JSONObject(data);
					final double bid = Double.parseDouble(json.getString("amount"));
					
					// feed results to manager
					mgr.onBid(bid, Currencies.USD);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(int statusCode) {
				mgr.onConnectionError();
			}
		}));
		
		// ask
		requests.add(new ExchangeRequest("https://coinbase.com/api/v1/prices/buy", new IResponseRunnable() {
			@Override
			public void onResult(String data) {
				mgr.onConnect();
				
				// parse JSON, feed results to manager
				JSONObject json;
				try {
					// parse out the data
					json = new JSONObject(data);
					final double sell = Double.parseDouble(json.getString("amount"));
					
					// feed results to manager
					mgr.onAsk(sell, Currencies.USD);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(int statusCode) {
				mgr.onConnectionError();
			}
		}));
				
		// spot
		requests.add(new ExchangeRequest("https://coinbase.com/api/v1/prices/spot_rate", new IResponseRunnable() {
			@Override
			public void onResult(String data) {
				mgr.onConnect();
				
				// parse JSON, feed results to manager
				JSONObject json;
				try {
					// parse out the data
					json = new JSONObject(data);
					final double spot = Double.parseDouble(json.getString("amount"));
					
					// feed results to manager
					mgr.onPrice(spot, Currencies.USD);
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