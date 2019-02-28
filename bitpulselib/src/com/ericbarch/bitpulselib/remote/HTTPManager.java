package com.ericbarch.bitpulselib.remote;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;

import com.ericbarch.bitpulselib.exchanges.IHTTPExchange;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

public class HTTPManager implements IRemoteManager {
	private IHTTPExchange exchange;
	private boolean active = false;
	private Timer reloadTimer;
	
	public HTTPManager(IHTTPExchange exchange) {
		this.exchange = exchange;
	}
	
	public void begin(int updateInterval) {
		active = true;
		
		if (reloadTimer != null) {
			end();
		}
		
		reloadTimer = new Timer();
		reloadTimer.scheduleAtFixedRate(new reloadAll(), 0, updateInterval*1000);
	}
	
	public void end() {
		// tell the exchange it's being destroyed
		exchange.onDestroy();
		
		active = false;
		
		reloadTimer.cancel();
		reloadTimer = null;
	}
	
	private class reloadAll extends TimerTask {
        public void run() {
        	if (active)
        		processRequests();
        }
	}
	
	private void processRequests() {
		// get a list of requests that this exchange specifies
		ArrayList<ExchangeRequest> requests = exchange.getRequests();
		
		// process each request
		for (int i=0; i<requests.size(); i++) {
			// grab each request
			final ExchangeRequest request = requests.get(i);
			
			// grab the URL we need to hit
			String requestUrl = request.getUrl();
			
			// http client
			SyncHttpClient client = new SyncHttpClient(true, 80, 443);
			client.get(requestUrl, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] response) {
					try {
						String responseString = new String(response, "UTF-8");
						
						// request was a success, call the result method
				    	request.result(responseString);
					} catch (UnsupportedEncodingException e) {
						// request failed, call the error method
				    	request.error(statusCode);
					}
				}
				
			    @Override
			    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
			    	// request failed, call the error method
			    	request.error(statusCode);
			    }
			});
		}
	}

	@Override
	public void singleShot() {
		processRequests();
	}
}
