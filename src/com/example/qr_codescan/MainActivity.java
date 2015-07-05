package com.example.qr_codescan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private final static int SCANNIN_GREQUEST_CODE = 1;
	/**
	 * 显示扫描结果
	 */
	private TextView mTextView;
	/**
	 * 显示扫描拍的图片
	 */
	private ImageView mImageView;

	private HashSet<ScanPin> codeSet = new HashSet<ScanPin>();
	private LocationManager manager;
	private Location location;
	private String address;
	private String TAG="LOC_QR";
//	private String locProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTextView = (TextView) findViewById(R.id.result);
		mImageView = (ImageView) findViewById(R.id.qrcode_bitmap);
		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		try{
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locListerner);
//			manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, locListerner);

	    }catch(java.lang.SecurityException ex){
	        Log.i("","fail to request  location update, ignore", ex);
	    }catch(IllegalArgumentException ex){
	        Log.d(TAG,"GPS provider does not exist, "+ ex.getMessage());
	    }
		try{
//			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locListerner);
			manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, locListerner);

	    }catch(java.lang.SecurityException ex){
	        Log.i("","fail to request location update, ignore", ex);
	    }catch(IllegalArgumentException ex){
	        Log.d(TAG,"network provider does not exist, "+ ex.getMessage());
	    }
		codeSet.add(new ScanPin("12345567", address));

		Button mButton = (Button) findViewById(R.id.button1);
		Button uButton = (Button) findViewById(R.id.upload);
        Button gButton =(Button) findViewById(R.id.btnGen);


		uButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String pinDetail = "";
				for (ScanPin sp : codeSet) {
					pinDetail += sp.toString();
				}

				// mTextView.setText(pinDetail);
				MyTask mTask = new MyTask();
				mTask.execute(pinDetail);
				// }
			}

		});

		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						MipcaActivityCapture.class);
				// intent.setClass(MainActivity.this,
				// MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});

		mTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Set<Entry<String,String>> set= codeList.entrySet();
				String pinDetail = "";
				for (ScanPin sp : codeSet) {
					pinDetail += sp.toString() + "\n";
				}

				mTextView.setText(pinDetail);
			}

		});

		// 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
		// manager.requestLocationUpdates(minTime, minDistance, criteria,
		// intent)
			}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				// 显示扫描到的内容
				String bar = bundle.getString("result");
				mTextView.setText(bar);
				// codeList.put(bundle.getString("result"),sdf.format(new
				// Date()));
				// 显示

				codeSet.add(new ScanPin(bar, address));
				Bitmap bmTmp = (Bitmap) data.getParcelableExtra("bitmap");
				mImageView.setImageBitmap(bmTmp);
				MediaStore.Images.Media.insertImage(getContentResolver(),
						bmTmp, "", "");
				// MyTask mTask = new MyTask();
				// glist.add(bar);
				// mTask.execute();
			}
			break;
		}
	}

	private class MyTask extends AsyncTask<String, Integer, String> {
		// onPreExecute方法用于在执行后台任务前做一些UI操作
		@Override
		protected void onPreExecute() {
			// Log.i(TAG, "onPreExecute() called");
			mTextView.setText("loading...");
		}

		// doInBackground方法内部执行后台任务,不可在此方法内修改UI
		@Override
		protected String doInBackground(String... par) {
			// Log.i(TAG, "doInBackground(Params... params) called");
			HttpURLConnection conn = null;
			String responseContent = null;

			String u = "https://ufo.deere.com/servlet/UFO?11990=1";
			// JDBCTest jt = new JDBCTest();
			String code = "UTF-8";

			Map<String, String> pMap = new HashMap<String, String>();
			String barcode = par[0];
			String time = new Date().toString();
			pMap.put("Dealer", "3B00322");
			pMap.put("Pin", barcode);
			pMap.put("Location", "LL");
			pMap.put("Time", time);
			try {
				StringBuffer params = new StringBuffer();
				Set<Entry<String, String>> set = pMap.entrySet();
				for (Entry<String, String> ent : set) {
					params.append(ent.getKey());
					params.append("=");
					params.append(URLEncoder.encode(ent.getValue(), code));
					params.append("&");
				}

				if (params.length() > 0) {
					params = params.deleteCharAt(params.length() - 1);
				}
				URL url = new URL(u);
				HttpURLConnection url_con = (HttpURLConnection) url
						.openConnection();
				url_con.setRequestMethod("POST");
				// System.setProperty("sun.net.client.defaultConnectTimeout",
				// String
				// .valueOf(HttpRequestProxy.connectTimeOut));//
				// 锛堝崟浣嶏細姣锛塲dk1.4鎹㈡垚杩欎釜,杩炴帴瓒呮椂
				// System.setProperty("sun.net.client.defaultReadTimeout",
				// String
				// .valueOf(HttpRequestProxy.readTimeOut)); //
				// 锛堝崟浣嶏細姣锛塲dk1.4鎹㈡垚杩欎釜,璇绘搷浣滆秴鏃�
				url_con.setConnectTimeout(5000);// 锛堝崟浣嶏細姣锛塲dk
				// 1.5鎹㈡垚杩欎釜,杩炴帴瓒呮椂
				url_con.setReadTimeout(5000);// 锛堝崟浣嶏細姣锛塲dk 1.5鎹㈡垚杩欎釜,璇绘搷浣滆秴鏃�
				url_con.setDoOutput(true);
				byte[] b = params.toString().getBytes();
				url_con.getOutputStream().write(b, 0, b.length);
				url_con.getOutputStream().flush();
				url_con.getOutputStream().close();

				InputStream in = url_con.getInputStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						in, code));
				String tempLine = rd.readLine();
				StringBuffer tempStr = new StringBuffer();
				String crlf = System.getProperty("line.separator");
				while (tempLine != null) {
					tempStr.append(tempLine);
					tempStr.append(crlf);
					tempLine = rd.readLine();
				}
				responseContent = tempStr.toString();
				rd.close();
				in.close();
			} catch (Exception e) {
//				e.printStackTrace();
				mTextView.setText(e.getMessage());
				Toast.makeText(getApplicationContext(), "Network Lost!!", Toast.LENGTH_LONG).show();
				
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}
			return responseContent;
		}

		// onProgressUpdate方法用于更新进度信息
		@Override
		protected void onProgressUpdate(Integer... progresses) {
			// Log.i(TAG, "onProgressUpdate(Progress... progresses) called");
			// progressBar.setProgress(progresses[0]);
			mTextView.setText("loading..." + progresses[0] + "%");
		}

		// onPostExecute方法用于在执行完后台任务后更新UI,显示结果
		@Override
		protected void onPostExecute(String result) {
			// Log.i(TAG, "onPostExecute(Result result) called");
			mTextView.setText(result);

			// execute.setEnabled(true);
			// cancel.setEnabled(false);
		}

		// onCancelled方法用于在取消执行中的任务时更改UI
		@Override
		protected void onCancelled() {
			// Log.i(TAG, "onCancelled() called");
			// textView.setText("cancelled");
			// progressBar.setProgress(0);

			// execute.setEnabled(true);
			// cancel.setEnabled(false);
		}
	}
	private void updateToNewLocation(Location location) {
//		  System.out.println("--------zhixing--2--------");
//		   String latLongString;
		   double lat = 0;
		   double lng=0;
		  
		   if (location != null) {
		    lat = location.getLatitude();
		    lng = location.getLongitude();
		    address =  lat + "," + lng;
		    System.out.println(lng+","+lat);
		   } else {
		    	address = "location can not be detected";
		   }
		   if(lat!=0){
//		    System.out.println("--------反馈信息----------"+ String.valueOf(lat));
		   }
		  
		   Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
		   mTextView.setText(address);
		 
//		   return location;
		  
		 }

	
	public final LocationListener locListerner = new LocationListener() {
	        @Override
	        public void onLocationChanged(Location location) {
	            updateToNewLocation(location);
	        }
	   

	        @Override
	        public void onProviderDisabled(String provider) {
	        	updateToNewLocation(null);
	        }

	        @Override
	        public void onProviderEnabled(String provider) {}

	        @Override
	        public void onStatusChanged(String provider, int status, Bundle extras) {}
	};



}
