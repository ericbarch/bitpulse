package com.ericbarch.bitpulselib.exchanges;

import android.app.Activity;

import com.ericbarch.bitpulselib.exception.CurrencyNotFoundException;
import com.ericbarch.bitpulselib.exchanges.history.bitcoinaverage.BitcoinAverageHistory;
import com.ericbarch.bitpulselib.receivers.BTCHistoryReceiver.Listener;
import com.ericbarch.bitpulselib.remote.HTTPManager;
import com.ericbarch.bitpulselib.remote.IRemoteManager;
import com.ericbarch.bitpulselib.util.Utilities;

public class HistoryManager {
	private Listener mListener;
	private IRemoteManager remoteManager;
	private Activity mActivity;
	
	public HistoryManager(Activity activity, int period, int currency, Listener listener)
		throws CurrencyNotFoundException {
			String currencyString = Utilities.currencyNameForConstant(currency);
		
			if (currencyString == null) {
				throw new CurrencyNotFoundException();
			} else if (listener == null || activity == null) {
				throw new NullPointerException();
			}
			
			mListener = listener;
			mActivity = activity;
			
			remoteManager = new HTTPManager(new BitcoinAverageHistory(currency, period, this));
	}
	
	public void refresh() {
		if (remoteManager != null) {
			remoteManager.singleShot();
		}
	}
	
	public void onData(final String csvData) {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mListener.onSuccess(csvData);
			}
		});
	}
	
	public void onFail(final int errorCode) {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mListener.onFail(errorCode);
			}
		});
	}
}