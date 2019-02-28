package com.ericbarch.bitpulselib.exchanges;

import android.app.Activity;

import com.ericbarch.bitpulselib.Exchanges;
import com.ericbarch.bitpulselib.exception.CurrencyNotFoundException;
import com.ericbarch.bitpulselib.exception.ExchangeNotFoundException;
import com.ericbarch.bitpulselib.exchanges.anx.ANX;
import com.ericbarch.bitpulselib.exchanges.bitcoinaverage.BitcoinAverage;
import com.ericbarch.bitpulselib.exchanges.bitfinex.Bitfinex;
import com.ericbarch.bitpulselib.exchanges.bitstamp.Bitstamp;
import com.ericbarch.bitpulselib.exchanges.btcchina.BtcChina;
import com.ericbarch.bitpulselib.exchanges.btce.BTCE;
import com.ericbarch.bitpulselib.exchanges.campbx.Campbx;
import com.ericbarch.bitpulselib.exchanges.coinbase.Coinbase;
import com.ericbarch.bitpulselib.exchanges.kraken.Kraken;
import com.ericbarch.bitpulselib.exchanges.vaultofsatoshi.VaultOfSatoshi;
import com.ericbarch.bitpulselib.receivers.BTCExchangeReceiver.Listener;
import com.ericbarch.bitpulselib.remote.HTTPManager;
import com.ericbarch.bitpulselib.remote.IRemoteManager;
import com.ericbarch.bitpulselib.util.CurrencyFormatter;
import com.ericbarch.bitpulselib.util.Utilities;


public class ExchangeManager {
	private boolean active = false;
	private Listener mListener;
	private IRemoteManager remoteManager;
	private Activity mActivity;
	private double lastPrice = -1;
	private double lastAsk = -1;
	private double lastBid = -1;
	private double lastVolume = -1;
	
	public ExchangeManager(Activity activity, int exchangeCode, int currency, Listener listener)
		throws ExchangeNotFoundException, CurrencyNotFoundException {
			String currencyString = Utilities.currencyNameForConstant(currency);
		
			if (currencyString == null) {
				throw new CurrencyNotFoundException();
			} else if (listener == null || activity == null) {
				throw new NullPointerException();
			}
			
			mListener = listener;
			mActivity = activity;
			
			if (exchangeCode == Exchanges.COINBASE) { 
				remoteManager = new HTTPManager(new Coinbase(currency, this));
			} else if (exchangeCode == Exchanges.BITCOIN_AVERAGE) { 
				remoteManager = new HTTPManager(new BitcoinAverage(currency, this));
			} else if (exchangeCode == Exchanges.BITFINEX) { 
				remoteManager = new HTTPManager(new Bitfinex(currency, this));
			} else if (exchangeCode == Exchanges.BITSTAMP) { 
				remoteManager = new HTTPManager(new Bitstamp(currency, this));
			} else if (exchangeCode == Exchanges.BTCCHINA) { 
				remoteManager = new HTTPManager(new BtcChina(currency, this));
			} else if (exchangeCode == Exchanges.BTCE) { 
				remoteManager = new HTTPManager(new BTCE(currency, this));
			} else if (exchangeCode == Exchanges.CAMPBX) {
				remoteManager = new HTTPManager(new Campbx(currency, this));
			} else if (exchangeCode == Exchanges.KRAKEN) {
				remoteManager = new HTTPManager(new Kraken(currency, this));
			} else if (exchangeCode == Exchanges.VAULTOFSATOSHI) {
				remoteManager = new HTTPManager(new VaultOfSatoshi(currency, this));
			} else if (exchangeCode == Exchanges.ANX) {
				remoteManager = new HTTPManager(new ANX(currency, this));
			} else {
				throw new ExchangeNotFoundException();
			}
	}
	
	public void begin(int updateInterval) {
		if (active) {
			end();
		}
		
		if (remoteManager != null) {
			active = true;
			
			remoteManager.begin(updateInterval);
		}
	}
	
	public void end() {
		if (remoteManager != null) {
			active = false;
			
			remoteManager.end();
		}
		
		lastPrice = -1;
		lastAsk = -1;
		lastBid = -1;
		lastVolume = -1;
	}
	
	// callbacks for exchanges
	public void onPrice(final double price, final int currencyCode) {
		if (active) {
			double btcPrice = Utilities.roundToTwoDecimals(price);
			if (btcPrice != lastPrice) {
				final String formattedPrice = CurrencyFormatter.localizePrice(currencyCode, btcPrice);
				
				final boolean priceUp;
				if (btcPrice > lastPrice)
					priceUp = true;
				else
					priceUp = false;
				
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mListener.onPrice(formattedPrice, price, priceUp);
					}
				});
				
				lastPrice = btcPrice;
			}
		}
	}
	
	public void onAsk(final double price, final int currencyCode) {
		if (active) {
			double btcAsk = Utilities.roundToTwoDecimals(price);
			if (btcAsk != lastAsk) {
				final String formattedAsk = CurrencyFormatter.localizePrice(currencyCode, btcAsk);
				
				final boolean priceUp;
				if (btcAsk > lastAsk)
					priceUp = true;
				else
					priceUp = false;
				
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mListener.onAsk(formattedAsk, price, priceUp);
					}
				});
				
				lastAsk = btcAsk;
			}
		}
	}
	
	public void onBid(final double price, final int currencyCode) {
		if (active) {
			double btcBid = Utilities.roundToTwoDecimals(price);
			if (btcBid != lastBid) {
				final String formattedBid = CurrencyFormatter.localizePrice(currencyCode, btcBid);
				
				final boolean priceUp;
				if (btcBid > lastBid)
					priceUp = true;
				else
					priceUp = false;
				
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mListener.onBid(formattedBid, price, priceUp);
					}
				});
				
				lastBid = btcBid;
			}
		}
	}
	
	// btc traded on this exchange in the last 24 hours
	public void onVolume(final double btcAmount) {
		if (active) {
			double btcVolume = Utilities.roundToTwoDecimals(btcAmount);
			if (btcVolume != lastVolume) {
				final String formattedVolume = "\u0E3F" + Utilities.roundTwoDecimalString(btcAmount);
				
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mListener.onVolume(formattedVolume, btcAmount);
					}
				});
				
				lastVolume = btcVolume;
			}
		}
	}
	
	public void onVolumeUnsupported() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mListener.onVolume("n/a", -1);
			}
		});
	}
	
	// if the websocket DCs or we get an HTTP error on a request
	public void onConnectionError() {
		if (active) {
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mListener.onConnectionError();
				}
			});
		}
	}
	
	// this gets called when the websocket connects or the first HTTP request is successful
	public void onConnect() {
		if (active) {
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mListener.onConnectionAlive();
				}
			});
		}
	}
}
