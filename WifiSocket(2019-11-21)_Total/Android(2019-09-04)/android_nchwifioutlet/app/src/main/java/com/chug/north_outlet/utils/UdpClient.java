package com.chug.north_outlet.utils;

import android.content.Intent;
import android.util.Log;

import com.chug.north_outlet.App;
import com.chug.north_outlet.Config;
import com.chug.north_outlet.bean.ControlDevice;
import com.chug.north_outlet.http.ThreadManger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Timer;

public class UdpClient implements Runnable{
	private static final String TAG = "UdpClient";
	private int						requestCode				= -1;
	private static final int UPDATE_DATA = 2220;
	private static final int CALLBACK = 2221;
	
	private static UdpClient client;
	
	private static final String REMOTE_IP = "255.255.255.255";//
	private static final int REMOTE_PORT = 5876;// remote port
	private static final int LOCAL_PORT = 9432;// location port
	
	private Runnable sthread = null;//
	private Thread rthread = null;//
//	private Runnable rthread = null;//
	private DatagramSocket sUdp = null;// send data Socket
	private DatagramSocket rUdp = null;// receive data Socket
	private DatagramPacket sPacket = null;
	private DatagramPacket rPacket = null;
	private byte[] rBuffer = new byte[60];//

	private boolean isThreadDisable = false;
	private String tiems;

	//private Handler mHandler;


	private UdpClient(){
		Timer timer=new Timer();
	}

	public static UdpClient getInstance(){
		synchronized (UdpClient.class) {
			if(client == null){
				synchronized (UdpClient.class) {
					if (client == null) {

						client = new UdpClient();

					}
				}
			}
		}
		return client;
	}

	boolean isZigbee=false;
	public boolean connectSocket(int length) {
		boolean result = false;
		try {
			if (rUdp == null){
				rUdp = new DatagramSocket(null);
				rUdp.setReuseAddress(true);
				rUdp.setBroadcast(true);
				rUdp.bind(new InetSocketAddress(LOCAL_PORT));
			}
			if (rPacket == null){
                if (length == 0) {

                    rPacket = new DatagramPacket(rBuffer, rBuffer.length);
                } else {
                    byte[]bytes=new byte[length];
                    rPacket = new DatagramPacket(bytes,length);
                }
            }
			if (length == 110) {
				isZigbee=true;
			}
			startThread();
			result = true;
		} catch (SocketException se) {
			disConnectSocket();
			LogUtil.e(TAG,"open udp port error:" + se.getMessage());
		}
		return result;
	}

	public void disConnectSocket() {
		if (rUdp != null) {
			rUdp.close();
			rUdp = null;
		}
		if (rPacket != null)
			rPacket = null;
		stopThread();
	}
	
	private void startThread() {
		if (rthread == null) {
			rthread = new Thread(this);
			rthread.start();
		}
	}

	private void stopThread() {
		if (rthread != null) {
			isThreadDisable = true;
			rthread.interrupt();
			rthread = null;
		}
	}
	
	public void sendData(final int requestCode, final String deviceIP, final byte[] sData, DataReceiveCallback callback) {
		try {
			this.requestCode=requestCode;
			if(callback != null){
				this.dataCallBack = callback;
			}
			if (sUdp == null){
				sUdp = new DatagramSocket();
			}
			sthread = new Runnable() {

				@Override
				public void run() {
					try {
//							LogUtil.e(TAG, XlinkUtils.getHexBinString(sData));
						sPacket = new DatagramPacket(sData, sData.length,InetAddress.getByName(deviceIP), REMOTE_PORT);

						if (sUdp == null) {
							sUdp = new DatagramSocket();
						}
						if (sPacket == null) {
							sPacket = new DatagramPacket(sData, sData.length,InetAddress.getByName(deviceIP), REMOTE_PORT);
						}
						sUdp.send(sPacket);
						Log.d(TAG,requestCode+ deviceIP+"=="+REMOTE_PORT+"=="+XlinkUtils.getHexBinString(sData));

						if (sUdp == null) {
							sUdp = new DatagramSocket();
							sUdp.close();
						} else {

							sUdp.close();
						}
						sUdp = null;
						sPacket = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			ThreadManger.execute(sthread);
		} catch (IOException ie) {
			sUdp.close();
			sUdp = null;
			sPacket = null;
		}
	}

	@Override
	public void run() {
		while (/*!isThreadDisable && */rthread != null && !rthread.isInterrupted()) {
			recvData();
		}
	}
	private void recvData() {
		try {
			rUdp.setSoTimeout(2000);
			rUdp.setBroadcast(true);
			Log.d(TAG, Thread.currentThread().getName());
			rUdp.receive(rPacket);
			if(rPacket.getData() != null){
				Log.d(TAG, Thread.currentThread().getName());
				//mHandler.obtainMessage(UPDATE_DATA, rPacket.getData()).sendToTarget();

				Log.d(TAG,"收到wifi设备数据：" + XlinkUtils.getHexBinString(rPacket.getData()));
			}
		} catch (Exception ie) {
//			connectSocket();
			this.disConnectSocket();
		}
	}

	public void sendPipeBroad(String action, ControlDevice device, byte[] data) {
		Intent intent = new Intent(action);
		intent.putExtra(Config.DEVICE_MAC, device.getMacAddress());
		intent.putExtra(Config.REQUEST_CODE_KEY, requestCode);
		if (data != null) {
			LogUtil.e(TAG, "当前接收页面码：" + requestCode + "收到pipe：" + XlinkUtils.getHexBinString(data) + "length"
					+ XlinkUtils.getHexBinString(data).substring(3, 21).trim());
			String s = XlinkUtils.getHexBinString(data);
			intent.putExtra(Config.DATA, data);
		}else {
			LogUtil.e(TAG, "当前接收页面码：是个");
		}
		App.getAppContext().sendBroadcast(intent);
	}

	private ReceiveCallback callback;

	public void setReceiveCallback(ReceiveCallback callback){
		this.callback = callback;
	}

	public interface ReceiveCallback{
		void onReceiveCallback(int deviceType, String deviceMac);
	}
	private DataReceiveCallback dataCallBack;

	public void setDataReceiveCallback(DataReceiveCallback dataCallBack){
		this.dataCallBack = dataCallBack;
	}

	public interface DataReceiveCallback{
		void onDataReceiveCallback(boolean isSuccess);
	}
}
