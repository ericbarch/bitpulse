package com.ericbarch.bitpulse;

import android.app.Activity;

import com.ericbarch.bitpulselib.receivers.BTCExchangeReceiver;

public class UpdateManager {
	private IDisplayView mDisplay;
	private BTCExchangeReceiver exchangeReceiver;
	private boolean active = false;
	private Activity mActivity;
	
	public UpdateManager(Activity activity, IDisplayView display) {
		mDisplay = display;
		mActivity = activity;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void beginUpdates(int exchange, int currency) {
		if (!active) {
			active = true;
			try {
				exchangeReceiver = new BTCExchangeReceiver(mActivity, exchange, currency, new BTCExchangeReceiver.Listener() {
					@Override
					public void onPrice(String formattedPrice, double price, boolean priceUp) {
						mDisplay.onLastChange(formattedPrice, priceUp);
					}

					@Override
					public void onAsk(String formattedPrice, double price, boolean priceUp) {
						mDisplay.onAskChange(formattedPrice);
					}

					@Override
					public void onBid(String formattedPrice, double price, boolean priceUp) {
						mDisplay.onBidChange(formattedPrice);
					}

					@Override
					public void onVolume(String formattedAmount, double btcAmount) {
						mDisplay.onVolumeChange(formattedAmount);
					}

					@Override
					public void onConnectionError() {
						mDisplay.onDisconnect();
					}

					@Override
					public void onConnectionAlive() {
						mDisplay.onConnect();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			// grab price every 30 seconds
			exchangeReceiver.beginUpdates(30);
		}
	}
	
	public void endUpdates() {
		if (exchangeReceiver != null && active) {
			// to end...
			exchangeReceiver.endUpdates();
			exchangeReceiver = null;
			active = false;
		}
	}
}
