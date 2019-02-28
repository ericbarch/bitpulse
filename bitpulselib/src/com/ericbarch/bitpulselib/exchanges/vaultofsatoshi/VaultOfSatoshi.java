package com.ericbarch.bitpulselib.exchanges.vaultofsatoshi;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ericbarch.bitpulselib.exchanges.ExchangeManager;
import com.ericbarch.bitpulselib.exchanges.IHTTPExchange;
import com.ericbarch.bitpulselib.remote.ExchangeRequest;
import com.ericbarch.bitpulselib.remote.IResponseRunnable;
import com.ericbarch.bitpulselib.util.Utilities;

public class VaultOfSatoshi implements IHTTPExchange {
	private ArrayList<ExchangeRequest> requests = new ArrayList<ExchangeRequest>(1);
	
	public VaultOfSatoshi(final int currency, final ExchangeManager mgr) {
		final String currencyString = Utilities.currencyNameForConstant(currency).toUpperCase();
		
		// ticker request
		// THIS WILL ONLY GET US VOLUME & CLOSING PRICE
		requests.add(new ExchangeRequest("https://api.vaultofsatoshi.com/public/ticker?order_currency=BTC&payment_currency=" + currencyString, new IResponseRunnable() {
			@Override
			public void onResult(String data) {
				mgr.onConnect();
				
				// parse JSON, feed results to manager
				JSONObject json;
				try {
					// parse out the data
					json = new JSONObject(data);
					
					JSONObject marketData = json.getJSONObject("data");
					JSONObject closingPrice = marketData.getJSONObject("closing_price");
					JSONObject volumeData = marketData.getJSONObject("volume_1day");
					
					final double last = Double.parseDouble(closingPrice.getString("value"));
					final double vol = Double.parseDouble(volumeData.getString("value"));

					// feed results to manager
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
		
		// THIS WILL GET US BID AND ASK ARRAYS
		requests.add(new ExchangeRequest("https://api.vaultofsatoshi.com/public/orderbook?order_currency=BTC&payment_currency=" + currencyString, new IResponseRunnable() {
			@Override
			public void onResult(String data) {
				mgr.onConnect();
				
				// parse JSON, feed results to manager
				JSONObject json;
				try {
					// parse out the data
					json = new JSONObject(data);
					
					JSONObject marketData = json.getJSONObject("data");
					JSONArray marketBids = marketData.getJSONArray("bids");
					JSONArray marketAsks = marketData.getJSONArray("asks");
					
					final double ask = Double.parseDouble(marketAsks.getJSONObject(0).getJSONObject("price").getString("value"));
					final double bid = Double.parseDouble(marketBids.getJSONObject(0).getJSONObject("price").getString("value"));

					// feed results to manager
					mgr.onBid(bid, currency);
					mgr.onAsk(ask, currency);
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