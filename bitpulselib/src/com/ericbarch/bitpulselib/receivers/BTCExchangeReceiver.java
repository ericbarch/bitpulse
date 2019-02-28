package com.ericbarch.bitpulselib.receivers;

import android.app.Activity;

import com.ericbarch.bitpulselib.exception.CurrencyNotFoundException;
import com.ericbarch.bitpulselib.exception.ExchangeNotFoundException;
import com.ericbarch.bitpulselib.exchanges.ExchangeManager;

public class BTCExchangeReceiver {
	private ExchangeManager xManager;
	
	public BTCExchangeReceiver(Activity activity, int exchange, int currency, Listener listener)
		throws ExchangeNotFoundException, CurrencyNotFoundException {
			xManager = new ExchangeManager(activity, exchange, currency, listener);
	}
	
	public void beginUpdates(int secondsUpdateInterval) {
		xManager.begin(secondsUpdateInterval);
	}
	
	public void endUpdates() {
		xManager.end();
	}
	
	public interface Listener {
		public void onPrice(String formattedPrice, double price, boolean priceUp);
		public void onAsk(String formattedPrice, double price, boolean priceUp);
		public void onBid(String formattedPrice, double price, boolean priceUp);
		
		// btc traded on this exchange in the last 24 hours
		public void onVolume(String formattedAmount, double btcAmount);
		
		// if the websocket DCs or we get an HTTP error on a request
		public void onConnectionError();
		
		// this gets called when the websocket connects or the first HTTP request is successful
		public void onConnectionAlive();
	}
}