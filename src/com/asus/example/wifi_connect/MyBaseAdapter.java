package com.asus.example.wifi_connect;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.asus.example.wifi_connect.WifiAdmin.AUTH_TYPE;
import com.asus.example.wifi_connect.WifiAdmin.PROXY_SETTING;

public class MyBaseAdapter extends BaseAdapter {
	private Activity mActivity;
	private Context mContext;
	private LayoutInflater mInflator;
	private List<Map<String, Object>> dataList;
	private WifiAdmin mWifiAdmin;
	private static Map<String, String> mSsidPass;
	private static String mConnectSsid = "";
	private String mConnectAuth = "";
	private int colorTextGrey;
	private int colorConnectedGreen;
	private PROXY_SETTING mProxySetting = PROXY_SETTING.PROXY_NO;
	private int proxyCheckedId = R.id.proxy_no;

	public MyBaseAdapter(Activity activity, List<Map<String, Object>> dataList, LayoutInflater inflator) {
		this.mActivity = activity;
		this.mContext = activity.getApplicationContext();
		this.dataList = dataList;
		this.mInflator = inflator;
		init();
	}

	private void init() {
		mWifiAdmin = new WifiAdmin(mContext);
		mSsidPass = LoginData.getSsidPass();
		mWifiDataCur = new WifiData();
		colorTextGrey = mContext.getResources().getColor(R.color.text_grey);
		colorConnectedGreen = mContext.getResources().getColor(R.color.connected_green);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	@Override
	public Map<String, Object> getItem(int position) {
		// TODO Auto-generated method stub
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/**
	 * ViewHolder类用以储存item中控件的引用
	 */
	final class ViewHolder {
		TextView ssid;
		TextView auth;
		ImageView check;
		ImageView signal;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
			holder.ssid = (TextView) convertView.findViewById(R.id.ssid);
			holder.auth = (TextView) convertView.findViewById(R.id.auth);
			holder.check = (ImageView) convertView.findViewById(R.id.check);
			holder.signal = (ImageView) convertView.findViewById(R.id.signal);

			// 将holder绑定到convertView
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 向ViewHolder中填入的数据
		holder.ssid.setText((String) getItem(position).get("ssid"));
		holder.ssid.setTextColor(colorTextGrey);
		if (mConnectSsid.equals("\"" + (String) getItem(position).get("ssid") + "\"")) {
			holder.auth.setText(mConnectAuth);
			holder.auth.setTextColor(colorConnectedGreen);
		} else {
			holder.auth.setText((String) getItem(position).get("auth"));
			holder.auth.setTextColor(colorTextGrey);
		}

		if (getItem(position).get("check") != null) {
			holder.check.setImageResource((Integer) getItem(position).get("check"));
		} else {
			holder.check.setImageResource(-1);
		}
		holder.signal.setImageResource((Integer) getItem(position).get("signal"));

		convertView.setOnClickListener(new ItemClickEvent());

		return convertView;
	}

	private static WifiData mWifiDataCur;
	private static TextView mTextViewAuth;

	/**
	 * Item Click Event
	 * 
	 * @author Kim_Bai
	 * 
	 */
	private View layout;

	private class ItemClickEvent implements android.view.View.OnClickListener {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			mTextViewAuth = (TextView) view.findViewById(R.id.auth);
			mWifiDataCur.setmSsid(((TextView) view.findViewById(R.id.ssid)).getText().toString());
			mWifiDataCur.setmAuth(mTextViewAuth.getText().toString());

			if (mWifiDataCur.getmSsid().equals("CJ86GJI4")) {
				layout = mInflator.inflate(R.layout.dialog_proxy, null);
			} else {
				layout = mInflator.inflate(R.layout.dialog_wifi, null);
				((TextView) layout.findViewById(R.id.auth_value)).setText(mWifiDataCur.getmAuth());
				((ImageView) layout.findViewById(R.id.signal_value)).setImageDrawable(((ImageView) view
						.findViewById(R.id.signal)).getDrawable());
			}
			
			if (mWifiDataCur.getmAuth().equals(mContext.getResources().getString(R.string.connected))) {
				new AlertDialog.Builder(mActivity).setTitle(mWifiDataCur.getmSsid()).setView(layout)
						.setPositiveButton(R.string.remove, new WifiRemoveClick())
						.setNegativeButton(R.string.cancel, null).show();
				if (mWifiDataCur.getmSsid().equals("CJ86GJI4")) {
					RadioGroup rg = (RadioGroup) layout.findViewById(R.id.proxy_setting);
					((RadioButton) rg.findViewById(proxyCheckedId)).setChecked(true);
				}
			} else {
				if (mWifiDataCur.getmSsid().equals("CJ86GJI4")) {
					RadioGroup rg = (RadioGroup) layout.findViewById(R.id.proxy_setting);
					((RadioButton) rg.findViewById(proxyCheckedId)).setChecked(true);
					rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(RadioGroup group, int checkedId) {
							// TODO Auto-generated method stub
							proxyCheckedId = checkedId;
							switch (checkedId)
								{
								case R.id.proxy_no:
									mProxySetting = PROXY_SETTING.PROXY_NO;
									break;
								case R.id.proxy_tw1:
									mProxySetting = PROXY_SETTING.PROXY_TW1;
									break;
								case R.id.proxy_tw2:
									mProxySetting = PROXY_SETTING.PROXY_TW2;
									break;
								default:
									mProxySetting = PROXY_SETTING.PROXY_NO;
									break;
								}
						}
					});
				}

				new AlertDialog.Builder(mActivity).setTitle(mWifiDataCur.getmSsid()).setView(layout)
						.setPositiveButton(R.string.ok, new WifiConnectClick())
						.setNegativeButton(R.string.cancel, null).show();
			}
		}
	}

	/**
	 * Dialog Select "Remove"
	 * 
	 * @author Kim_Bai
	 * 
	 */
	private class WifiRemoveClick implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stubbled
			mWifiAdmin.removeNetwork(mWifiDataCur.getmSsid());
			mConnectSsid = "";
			MyBaseAdapter.this.notifyDataSetChanged();
			Toast.makeText(mActivity, mWifiDataCur.getmSsid(), Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * Dialog Select OK"
	 * 
	 * @author Kim_Bai
	 * 
	 */
	private class WifiConnectClick implements OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			String ssid = mWifiDataCur.getmSsid();
			boolean isEnabled = false;
			mWifiAdmin.openWifi();
			if (mWifiDataCur.getmAuth().contains("EAP")) {
				isEnabled = mWifiAdmin.enableNetwork(ssid, mSsidPass.get(ssid), AUTH_TYPE.EAP, mProxySetting);
			} else if (mWifiDataCur.getmAuth().contains("WPA")) {
				isEnabled = mWifiAdmin.enableNetwork(ssid, mSsidPass.get(ssid), AUTH_TYPE.WPA, mProxySetting);
			} else if (mWifiDataCur.getmAuth().contains("WEP")) {
				isEnabled = mWifiAdmin.enableNetwork(ssid, mSsidPass.get(ssid), AUTH_TYPE.WEP, mProxySetting);
			} else {
				isEnabled = mWifiAdmin.enableNetwork(ssid, "", AUTH_TYPE.NO_PASS, mProxySetting);
			}

			if (isEnabled) {
				Toast.makeText(mActivity, "Connect to \"" + ssid + "\" Successfully!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mActivity, "Sorry, not support \"" + ssid + "\" yet!", Toast.LENGTH_LONG).show();
			}
		}

	}

	// Setters & Getters
	public void setmConnectSsid(String mConnectSsid) {
		MyBaseAdapter.mConnectSsid = mConnectSsid;
	}

	public void setmConnectAuth(String mConnectAuth) {
		this.mConnectAuth = mConnectAuth;
	}

}
