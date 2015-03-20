package com.asus.example.wifi_connect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiEnterpriseConfig.Phase2;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Build;
import android.util.Log;

public class WifiAdmin {
	// å®šä¹‰WifiManagerå¯¹è±¡
	private WifiManager mWifiManager;
	// æ‰«æ��å‡ºçš„ç½‘ç»œè¿žæŽ¥åˆ—è¡¨
	private List<ScanResult> mWifiList;
	// å®šä¹‰ä¸€ä¸ªWifiLock
	private WifiLock mWifiLock;
	private Context mContext;

	public static enum AUTH_TYPE {
		NO_PASS, WEP, WPA, EAP
	}
	
	public static enum PROXY_SETTING {
		PROXY_NO, PROXY_TW1, PROXY_TW2
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public WifiAdmin(Context context) {
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mContext = context;
		mHiddenSsidPass = LoginData.getHiddenSsidPass();
	}

	// å¾—åˆ°WifiInfo
	public WifiInfo getWifiInfo() {
		return mWifiManager.getConnectionInfo();
	}

	// æ£€æŸ¥å½“å‰�WIFIçŠ¶æ€�
	public int checkWifiState() {
		return mWifiManager.getWifiState();
	}

	public boolean isWifiEnabled() {
		return mWifiManager.isWifiEnabled();
	}

	// å¾—åˆ°ç½‘ç»œåˆ—è¡¨
	public List<ScanResult> getWifiList() {
		openWifi();
		startScan();
		return mWifiList;
	}

	// æ‰“å¼€WIFI
	public void openWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	// æ‰«æ��Wifi
	private void startScan() {
		mWifiManager.startScan();
		// å¾—åˆ°æ‰«æ��ç»“æžœ
		mWifiList = mWifiManager.getScanResults();
	}

	// å…³é—­WIFI
	public void closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	// é”�å®šWifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// è§£é”�WifiLock
	public void releaseWifiLock() {
		// åˆ¤æ–­æ—¶å€™é”�å®š
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// åˆ›å»ºä¸€ä¸ªWifiLock
	public void creatWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("Test");
	}

	private WifiConfiguration createWifiInfo(String ssid, String password, AUTH_TYPE auth_type,
			PROXY_SETTING proxy_setting) {
		// é…�ç½®ç½‘ç»œä¿¡æ�¯ç±»
		WifiConfiguration wc = new WifiConfiguration();
		// è®¾ç½®é…�ç½®ç½‘ç»œå±žæ€§
		wc.allowedAuthAlgorithms.clear();
		wc.allowedGroupCiphers.clear();
		wc.allowedKeyManagement.clear();
		wc.allowedPairwiseCiphers.clear();
		wc.allowedProtocols.clear();

		// wifiè¿žæŽ¥
		wc.SSID = ("\"" + ssid + "\"");
		switch (auth_type)
			{
			// æ— å¯†ç �
			case NO_PASS:
				wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
				break;
			// WEPåŠ å¯† | è¾ƒè€�ï¼Œä¸�å®‰å…¨
			case WEP:
				wc.allowedKeyManagement.set(KeyMgmt.NONE);
				wc.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
				wc.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
				wc.wepKeys[0] = password;
				wc.hiddenSSID = true;
				Log.d("createConfig", "WEP " + ssid);
				break;
			// WPAåŠ å¯† | è¾ƒå¤š
			case WPA:
				if (password != null) {
					if (password.matches("[0-9A-Fa-f]{64}")) {
						wc.preSharedKey = password;
					} else {
						wc.preSharedKey = '"' + password + '"';
					}
					wc.hiddenSSID = true;
					Log.d("createConfig", "WPA " + ssid);
				}
				break;
			// 802.1x EAPåŠ å¯†
			case EAP:
				wc.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
				wc.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
				// wc.enterpriseConfig = new WifiEnterpriseConfig();
				wc.enterpriseConfig.setEapMethod(0);
				wc.enterpriseConfig.setPhase2Method(Phase2.NONE);

				wc.enterpriseConfig.setIdentity("asuscn\\kim_bai");
				wc.enterpriseConfig.setAnonymousIdentity("");
				wc.enterpriseConfig.setPassword(password);

				if (Build.VERSION.SDK_INT > 20) {
					switch (proxy_setting)
						{
						case PROXY_NO:
							return unsetWifiProxySettingsForAndroidL(wc);
						case PROXY_TW1:
							return setWifiProxySettingsForAndroidL(wc, "192.168.56.30", 80);
						case PROXY_TW2:
							return setWifiProxySettingsForAndroidL(wc, "192.168.56.31", 80);
						default:
							return wc;
						}
				} else {
					switch (proxy_setting)
						{
						case PROXY_NO:
							return unsetWifiProxySettings(wc);
						case PROXY_TW1:
							return setWifiProxySettings(wc, "192.168.56.30", 80);
						case PROXY_TW2:
							return setWifiProxySettings(wc, "192.168.56.31", 80);
						default:
							return wc;
						}
				}

			default:
				break;
			}

		return wc;
	}

	private static Object getField(Object obj, String name) throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field f = obj.getClass().getField(name);
		Object out = f.get(obj);
		return out;
	}

	public static void setEnumField(Object obj, String value, String name) throws SecurityException,
			NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field f = obj.getClass().getField(name);
		f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
	}

	private static void setProxySettings(String assign, WifiConfiguration wc) throws SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		setEnumField(wc, assign, "proxySettings");
	}

	/**
	 * Set Proxy
	 * 
	 * @param wc
	 * @param proxy_host
	 * @param proxy_port
	 * @return
	 */
	private WifiConfiguration setWifiProxySettings(WifiConfiguration wc, String proxy_host, int proxy_port) {
		// get the current wifi configuration

		try {
			// get the link properties from the wifi configuration
			Object linkProperties = getField(wc, "linkProperties");
			if (null == linkProperties)
				return wc;

			// get the setHttpProxy method for LinkProperties
			Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
			Class[] setHttpProxyParams = new Class[1];
			setHttpProxyParams[0] = proxyPropertiesClass;
			Class linkProertiesClass = Class.forName("android.net.LinkProperties");
			Method setHttpProxy = linkProertiesClass.getDeclaredMethod("setHttpProxy", setHttpProxyParams);
			setHttpProxy.setAccessible(true);

			// get ProxyProperties constructor
			Class[] proxyPropertiesCtorParamTypes = new Class[3];
			proxyPropertiesCtorParamTypes[0] = String.class;
			proxyPropertiesCtorParamTypes[1] = int.class;
			proxyPropertiesCtorParamTypes[2] = String.class;

			Constructor proxyPropertiesCtor = proxyPropertiesClass.getConstructor(proxyPropertiesCtorParamTypes);

			// create the parameters for the constructor
			Object[] proxyPropertiesCtorParams = new Object[3];
			proxyPropertiesCtorParams[0] = proxy_host;
			proxyPropertiesCtorParams[1] = proxy_port;
			proxyPropertiesCtorParams[2] = null;

			// create a new object using the params
			Object proxyPropertiesObject = proxyPropertiesCtor.newInstance(proxyPropertiesCtorParams);

			// pass the new object to setHttpProxy
			Object[] params = new Object[1];
			params[0] = proxyPropertiesObject;
			setHttpProxy.invoke(linkProperties, params);

			setProxySettings("STATIC", wc);
			
			return wc;
		} catch (Exception e) {
		}
		return wc;
	}
	
	/**
	 * Set Proxy For Android L
	 * 
	 * @param wc
	 * @param proxy_host
	 * @param proxy_port
	 * @return
	 */
	private WifiConfiguration setWifiProxySettingsForAndroidL(WifiConfiguration wc, String proxy_host, int proxy_port) {
		// get the current wifi configuration
		try {
			// get IpConfigurationClass
			Class ipConfigurationClass = Class.forName("android.net.IpConfiguration");

			// get IpAssignmentClass :1
			Class ipAssignmentClass = Class.forName("android.net.IpConfiguration$IpAssignment");
			Object ipAssignmentUnassigned = Enum.valueOf(ipAssignmentClass, "UNASSIGNED");

			// get ProxySettingsClass :2
			Class proxySettingsClass = Class.forName("android.net.IpConfiguration$ProxySettings");
			Object proxySettingsStatic = Enum.valueOf(proxySettingsClass, "STATIC");

			// get StaticIpConfigurationClass :3
			Class staticIpConfigurationClass = Class.forName("android.net.StaticIpConfiguration");

			// get ProxyInfoClass :4
			Class proxyInfoClass = Class.forName("android.net.ProxyInfo");
			Class[] proxyInfoConstructorParamTypes = new Class[3];
			proxyInfoConstructorParamTypes[0] = String.class;
			proxyInfoConstructorParamTypes[1] = int.class;
			proxyInfoConstructorParamTypes[2] = String.class;

			Constructor proxyInfoConstructor = proxyInfoClass.getConstructor(proxyInfoConstructorParamTypes);
			Object[] proxyInfoConstructorParams = new Object[3];
			proxyInfoConstructorParams[0] = proxy_host;
			proxyInfoConstructorParams[1] = proxy_port;
			proxyInfoConstructorParams[2] = null;

			Object proxyInfoObject = proxyInfoConstructor.newInstance(proxyInfoConstructorParams);

			// get ipConfiguration constructor
			Class[] ipConfigurationConstructorParamTypes = new Class[4];
			ipConfigurationConstructorParamTypes[0] = ipAssignmentClass;
			ipConfigurationConstructorParamTypes[1] = proxySettingsClass;
			ipConfigurationConstructorParamTypes[2] = staticIpConfigurationClass;
			ipConfigurationConstructorParamTypes[3] = proxyInfoClass;

			Constructor ipConfigurationConstructor = ipConfigurationClass
					.getConstructor(ipConfigurationConstructorParamTypes);

			// create the parameters for the constructor
			Object[] ipConfigurationConstructorParams = new Object[4];
			ipConfigurationConstructorParams[0] = ipAssignmentUnassigned;
			ipConfigurationConstructorParams[1] = proxySettingsStatic;
			ipConfigurationConstructorParams[2] = null;
			ipConfigurationConstructorParams[3] = proxyInfoObject;

			// create a new ipConfiguration object using the params
			Object ipConfigurationObject = ipConfigurationConstructor.newInstance(ipConfigurationConstructorParams);

			// pass the new object to setHttpProxy
			// get WifiConfiguration Class
			Class wifiConfigurationClass = Class.forName("android.net.wifi.WifiConfiguration");

			Class[] setIpConfigurationParams = new Class[1];
			setIpConfigurationParams[0] = ipConfigurationClass;
			Method setIpConfiguration = wifiConfigurationClass.getDeclaredMethod("setIpConfiguration",
					setIpConfigurationParams);
			setIpConfiguration.setAccessible(true);

			setIpConfiguration.invoke(wc, ipConfigurationObject);

			setProxySettings("STATIC", wc);

			return wc;
		} catch (Exception e) {
		}
		return wc;
	}

	/**
	 * Unset Proxy For Android L
	 * 
	 * @param wc
	 * @return
	 */
	private WifiConfiguration unsetWifiProxySettingsForAndroidL(WifiConfiguration wc) {
		try {
			// get IpConfigurationClass
			Class ipConfigurationClass = Class.forName("android.net.IpConfiguration");

			// pass the new object to setHttpProxy
			// get WifiConfiguration Class
			Class wifiConfigurationClass = Class.forName("android.net.wifi.WifiConfiguration");

			Class[] setIpConfigurationParams = new Class[1];
			setIpConfigurationParams[0] = ipConfigurationClass;
			Method setIpConfiguration = wifiConfigurationClass.getDeclaredMethod("setIpConfiguration",
					setIpConfigurationParams);
			setIpConfiguration.setAccessible(true);

			setIpConfiguration.invoke(wc, null);

			setProxySettings("NONE", wc);
		} catch (Exception e) {
			return wc;
		}
		return wc;
	}

	/**
	 * Unset Proxy
	 * 
	 * @param wc
	 * @return
	 */
	private WifiConfiguration unsetWifiProxySettings(WifiConfiguration wc) {
		try {
			// get the link properties from the wifi configuration
			Object linkProperties = getField(wc, "linkProperties");
			if (null == linkProperties)
				return wc;

			// get the setHttpProxy method for LinkProperties
			Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
			Class[] setHttpProxyParams = new Class[1];
			setHttpProxyParams[0] = proxyPropertiesClass;
			Class lpClass = Class.forName("android.net.LinkProperties");
			Method setHttpProxy = lpClass.getDeclaredMethod("setHttpProxy", setHttpProxyParams);
			setHttpProxy.setAccessible(true);

			// pass null as the proxy
			Object[] params = new Object[1];
			params[0] = null;
			setHttpProxy.invoke(linkProperties, params);

			setProxySettings("NONE", wc);
		} catch (Exception e) {
			return wc;
		}
		return wc;
	}

    /**
     *
     * @param ssid
     * @param password
     * @param auth_type
     * @param proxy_setting
     * @return
     */
	public boolean enableNetwork(String ssid, String password, AUTH_TYPE auth_type, PROXY_SETTING proxy_setting) {
		// List<WifiConfiguration> existingConfigs =
		// mWifiManager.getConfiguredNetworks();
		WifiConfiguration wc = createWifiInfo(ssid, password, auth_type, proxy_setting);
		/*
		 * if (existingConfigs != null) { for (WifiConfiguration ec :
		 * existingConfigs) { // Need to add "" if (ec.SSID.equals("\"" + ssid +
		 * "\"")) { wc = ec; break; } } }
		 */

		int netID = mWifiManager.addNetwork(wc);
		// ä¿�å­˜APè¿žç½‘æ•°æ�®
		// mWifiManager.saveConfiguration();
		return mWifiManager.enableNetwork(netID, true);
	}

	private Map<String, String> mHiddenSsidPass;

	public void addHiddenNetwork(String ssid) {
		WifiConfiguration wc = createWifiInfo(ssid, mHiddenSsidPass.get(ssid), AUTH_TYPE.WEP, PROXY_SETTING.PROXY_NO);
		mWifiManager.addNetwork(wc);
		// ä¿�å­˜APè¿žç½‘æ•°æ�®
		mWifiManager.saveConfiguration();
	}

	/**
	 * Remove Network
	 */
	public void removeNetwork(String ssid) {
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		for (WifiConfiguration ec : existingConfigs) {
			Log.d("wifiConfig", "exist " + ec.SSID);
			// Need to add ""
			if (ec.SSID.equals("\"" + ssid + "\"")) {
				Log.d("wifiConfig", "remove " + ec.SSID);
				mWifiManager.removeNetwork(ec.networkId);
				// break;
			}
		}
	}

	/**
	 * Disable Network
	 * 
	 * @param ssid
	 */
	public void disableNetwork(String ssid) {
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		for (WifiConfiguration ec : existingConfigs) {
			// Need to add ""
			if (ec.SSID.equals("\"" + ssid + "\"")) {
				mWifiManager.disableNetwork(ec.networkId);
				break;
			}
		}
	}

}
