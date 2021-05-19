package com.chug.north_outlet.callback;


import com.chug.north_outlet.bean.ControlDevice;

public interface IDeviceConnectCallback {
    public void connectCallback(boolean isSuccess, ControlDevice device);
}
