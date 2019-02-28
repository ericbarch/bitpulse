package com.ericbarch.bitpulselib.remote;

public interface IRemoteManager {
	public void begin(int updateInterval);
	public void singleShot();
	public void end();
}
