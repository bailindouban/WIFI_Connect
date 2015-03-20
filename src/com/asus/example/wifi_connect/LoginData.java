package com.asus.example.wifi_connect;

import java.util.HashMap;
import java.util.Map;

public class LoginData {
	private static Map<String, String> ssid_pass;
	private static Map<String, String> ssid_pass_h;

	public static Map<String, String> getSsidPass() {
		if (ssid_pass == null) {
			ssid_pass = new HashMap<String, String>();

			// Oracle_Lei@asus.com
			ssid_pass.put("ORACLE-TEST", "1128@qwer");
			// Jane_Guan@asus.com
			ssid_pass.put("TP-9527", "athz-sec1");
			ssid_pass.put("SEC_1", "athz-sec1");
			ssid_pass.put("SEC_1-5G", "athz-sec1");
			// Molly_Zhu@asus.com
			ssid_pass.put("SWRD-51229", "135792468");
			// Will_Li@asus.com
			ssid_pass.put("UETEST", "uetest12345");
			// Emmanual_Chen@asus.com
			ssid_pass.put("WIN-3UPF7HGR347_Network", "057151138");
			// Mars
			ssid_pass.put("SWRD4-51137", "057151137");

			// Second Floor - 卓凱美食城
			ssid_pass.put("zkmsc", "zkmsc2013");

			// 隱藏的 WEP wifi
			ssid_pass.put("ATHZ-TEST0571(15S)", "13579246813579246813579246");
			ssid_pass.put("ATHZ-TEST0571(15N)", "13579246813579246813579246");

			// 802.1x EAP
			ssid_pass.put("CJ86GJI4", "bailin234@");
			
			// 家烧面馆
			ssid_pass.put("jiashaocanyin", "88853676");
		}
		
		return ssid_pass;
	}	
	
	public static Map<String, String> getHiddenSsidPass() {
		if (ssid_pass_h == null) {
			ssid_pass_h = new HashMap<String, String>();

			// 隱藏的 WEP wifi
			ssid_pass_h.put("ATHZ-TEST0571(15S)", "13579246813579246813579246");
			ssid_pass_h.put("ATHZ-TEST0571(15N)", "13579246813579246813579246");
		}

		return ssid_pass_h;
	}
}
