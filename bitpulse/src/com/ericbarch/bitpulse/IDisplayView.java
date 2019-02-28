package com.ericbarch.bitpulse;

public interface IDisplayView {
	public void onLastChange(String tradeString, boolean valueUp);
	public void onBidChange(String bidString);
	public void onAskChange(String askString);
	public void onVolumeChange(String volString);
	public void onDisconnect();
	public void onConnect();
}
