package com.test;

import java.io.IOException;
import java.net.Socket;

public class TCPClient {
	private static String mIP = "172.20.106.56";
	private static final int PORT = 2008;
	private static final String SALT = "8f56db1fb17c8c725ccea9d5fba1dc44";

	private static final int HEART_BEAT_INTERVAL = 5 * 1000;
	private static final int ALIVE_CHECK_INTERVAL = 15 * 1000;
	private static Socket mSocket;
	private static boolean mIsAlive;
	private static ReceiveThread mReceiveThread;
	private static CheckAliveThread mCheckAliveThread;
	private static HeartBeatThread mHeartBeatThread;
	private static FaceRecognizeRequestThread mFaceRecognizeRequestThread;
	private static byte[] mHeadBuffer = new byte[13];
	private static byte[] mJsonDataBuffer;
	private static boolean mIsStoped;

	private static class ReceiveThread extends Thread {
		@Override
		public void run() {
			while (!mIsStoped && !Thread.currentThread().isInterrupted()) {
				try {
					stickPackage(mHeadBuffer);

					String protocal = ByteConvert.bytesToString(mHeadBuffer, 0, 7); // 协议头
					int version = ByteConvert.byteToInt(mHeadBuffer[7]);
					int encryptType = ByteConvert.byteToInt(mHeadBuffer[8]);
					int jsonLen = ByteConvert.bytesToInt(mHeadBuffer, 9);
					System.out.println("protocal: " + protocal + "，version: " + version + "，encryptType: " + encryptType
							+ "，jsonLen: " + jsonLen);
					if (!"SENSEID".equals(protocal)) {
						System.out.println("datagram protocal error -> " + new String(mHeadBuffer));
						continue;
					}

					if (jsonLen > Integer.MAX_VALUE) {
						continue;
					}
					long limitMemory = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory()
							- 12 * 1024 * 1024;
					if (jsonLen > limitMemory) {
						continue;
					}

					mIsAlive = true;
					mJsonDataBuffer = new byte[jsonLen];
					stickPackage(mJsonDataBuffer);
					String jsonStr = new String(mJsonDataBuffer);
					
					System.out.println("json:" + jsonStr);
					System.out.println("jsonLen:" + jsonLen);
					System.out.println("json received:" + jsonStr.getBytes().length);

					// TODO : parse json object

					// JSONObject jsonObject = new JSONObject(jsonStr);
					// if (!jsonObject.has("cmd")) {
					// saveLog(TAG + ":datagram protocal error -> " + jsonStr);
					// continue;
					// }
					//
					// if (!mIsStoped)
					// parseData(jsonObject);

				} catch (IOException e) {
				}
				// catch (JSONException e) {
				// e.printStackTrace();
				// }
			}
		}
	}

	private static class CheckAliveThread extends Thread {
		@Override
		public void run() {
			while (!mIsStoped && !Thread.currentThread().isInterrupted()) {
				System.out.println("check alive: " + mIsAlive);
				if (!mIsAlive) {
					System.out.println("is not alive, reconnect");
					reconnect();
				} else {
					mIsAlive = !mIsAlive;
				}
				try {
					Thread.sleep(ALIVE_CHECK_INTERVAL);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static class HeartBeatThread extends Thread {
		@Override
		public void run() {
			byte[] heartBeat = new byte[13 + 14];

			String cmd = "{\"cmd\":\"1000\"}";
			System.arraycopy(SENSEIDProtocal.HEAD.getBytes(), 0, heartBeat, 0, 7);
			heartBeat[7] = SENSEIDProtocal.VERSION;
			heartBeat[8] = SENSEIDProtocal.ENCRYPT_TYPE;

			System.arraycopy(ByteConvert.intToBytes(14), 0, heartBeat, 9, 4);
			System.arraycopy(cmd.getBytes(), 0, heartBeat, 13, 14);
			while (!mIsStoped && !Thread.currentThread().isInterrupted()) {
				System.out.println("send heart beat");
				sendMsg(heartBeat);
				try {
					sleep(HEART_BEAT_INTERVAL);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private static class FaceRecognizeRequestThread extends Thread {
		@Override
		public void run() {
			String json = "{\"cmd\": \"1001\",\"data\": {\"image\": \"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\"}}";
			int jsonLen = json.getBytes().length;
			byte[] data = new byte[13 + jsonLen];

			System.arraycopy(SENSEIDProtocal.HEAD.getBytes(), 0, data, 0, 7);
			data[7] = SENSEIDProtocal.VERSION;
			data[8] = SENSEIDProtocal.ENCRYPT_TYPE;

			System.arraycopy(ByteConvert.intToBytes(jsonLen), 0, data, 9, 4);
			System.arraycopy(json.getBytes(), 0, data, 13, jsonLen);
			while (!mIsStoped && !Thread.currentThread().isInterrupted()) {
				System.out.println("send image");
				sendMsg(data);
				try {
					sleep(5000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private static void reconnect() {
		System.out.println("reconnect");
		close();
		connect();
	}

	private static void connect() {
		System.out.println("connect");
		try {
			mSocket = new Socket(mIP, PORT);

			byte[] nonce = new byte[32];
			stickPackage(nonce);

			String nonceStr = ByteConvert.bytesToString(nonce);

			System.out.println("nonce: " + nonceStr);

			String passwd = MD5.md5(nonceStr + SALT);

			System.out.println("passwd: " + passwd);

			sendMsg(passwd.getBytes());

			byte[] ack = new byte[32];
			stickPackage(ack);

			System.out.println("ack: " + new String(ack));

			if (new String(ack).equals(passwd)) {
				mIsAlive = true;
				mIsStoped = false;
				mReceiveThread = new ReceiveThread();
				mReceiveThread.start();

				mHeartBeatThread = new HeartBeatThread();
				mHeartBeatThread.start();

				mFaceRecognizeRequestThread = new FaceRecognizeRequestThread();
				mFaceRecognizeRequestThread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void close() {
		System.out.println("close");
		mIsStoped = true;
		mIsAlive = false;
		try {
			if (mSocket != null) {
				if (!mSocket.isInputShutdown()) {
					mSocket.shutdownInput();
				}
				if (!mSocket.isOutputShutdown()) {
					mSocket.shutdownOutput();
				}
				mSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			mSocket = null;
		}
		if (mReceiveThread != null) {
			mReceiveThread.interrupt();
			mReceiveThread = null;
		}
		if (mHeartBeatThread != null) {
			mHeartBeatThread.interrupt();
			mHeartBeatThread = null;
		}

		if (mFaceRecognizeRequestThread != null) {
			mFaceRecognizeRequestThread.interrupt();
			mFaceRecognizeRequestThread = null;
		}
	}

	private static void stickPackage(byte[] bs) throws IOException {
		int count = 0;
		int recieveDataLen = 0;
		int len = bs.length;
		if (mSocket == null || !mSocket.isConnected() || mSocket.isInputShutdown()) {
			return;
		}
		while (true) {
			if ((count = mSocket.getInputStream().read(bs, recieveDataLen, len - recieveDataLen)) > 0) {
				recieveDataLen += count;
				if (recieveDataLen == len) {
					break;
				}
			}
		}
	}

	public static void sendMsg(byte[] msg) {
		if (mSocket != null && mSocket.isConnected()) {
			try {
				mSocket.getOutputStream().write(msg);
				mSocket.getOutputStream().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		if (args != null && args.length > 0)
			mIP = args[0];

		mCheckAliveThread = new CheckAliveThread();
		mCheckAliveThread.start();
	}

}
