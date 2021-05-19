package com.chug.north_outlet.callback;


public interface ISendCallback {
	
	public void onSendPre();

	public void onSendStart();
	
	public void onSendCancel();
	
	public void onSendEnd(boolean isSuccess);
}
