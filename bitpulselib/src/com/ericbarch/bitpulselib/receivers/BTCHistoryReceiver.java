package com.ericbarch.bitpulselib.receivers;

import com.ericbarch.bitpulselib.exception.CurrencyNotFoundException;
import com.ericbarch.bitpulselib.exchanges.HistoryManager;

import android.app.Activity;

public class BTCHistoryReceiver {
	private HistoryManager hManager;
	
	public BTCHistoryReceiver(Activity activity, int period, int currency, Listener listener) throws CurrencyNotFoundException {
		hManager = new HistoryManager(activity, period, currency, listener);
		refresh();
	}
	
	public void refresh() {
		hManager.refresh();
	}
	
	public interface Listener {
		public void onSuccess(String csvData);
		public void onFail(int errorCode);
	}
}