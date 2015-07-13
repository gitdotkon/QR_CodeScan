package com.example.qr_codescan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;

public class MainActivity extends Activity {
	private final static int SCANNIN_GREQUEST_CODE = 1;
	/**
	 * 显示扫描结果
	 */
	private TextView mTextView;
	private EditText sSIDTextView;
	private EditText passTextView;
	/**
	 * 显示扫描拍的图片
	 */
	private ImageView mImageView;

	private LocationManager manager;
	private Location location;
	private String address;
	private String TAG = "LOC_QR";

	// private String locProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTextView = (TextView) findViewById(R.id.result);
		sSIDTextView=(EditText)findViewById(R.id.SSID);
		passTextView=(EditText)findViewById(R.id.PASSWORD);
		mImageView = (ImageView) findViewById(R.id.qrcode_bitmap);
		

		Button mButton = (Button) findViewById(R.id.btnScan);
		Button gButton = (Button) findViewById(R.id.btnGen);
		

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
		gButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String ssidString= sSIDTextView.getText().toString().trim();
				String passString=passTextView.getText().toString().trim();
				Test test = new Test();
				Bitmap bmTmp=null;
				try {
					bmTmp= (Bitmap)test.Create2DCode(ssidString+","+passString);
				} catch (WriterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mImageView.setImageBitmap(bmTmp);
				
			}
		});

		mTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Set<Entry<String,String>> set= codeList.entrySet();
				String pinDetail = "";

				mTextView.setText(pinDetail);
			}

		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				// 显示扫描到的内容
				String codeString = bundle.getString("result");
				mTextView.setText(codeString);
				// codeList.put(bundle.getString("result"),sdf.format(new
				// Date()));
				// 显示

				Bitmap bmTmp = (Bitmap) data.getParcelableExtra("bitmap");
				mImageView.setImageBitmap(bmTmp);
				
				String[] code=codeString.split(","); 
				sSIDTextView.setText(code[0]);
				passTextView.setText(code[1]);
				WifiAdmin wifiAdmin = new WifiAdmin(this);
				wifiAdmin.openWifi();
				wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(code[0], code[1], 3));
				// MyTask mTask = new MyTask();
				// glist.add(bar);
				// mTask.execute();
			}
			break;
		}
	}

/*	private void updateToNewLocation(Location location) {
		// System.out.println("--------zhixing--2--------");
		// String latLongString;
		double lat = 0;
		double lng = 0;

		if (location != null) {
			lat = location.getLatitude();
			lng = location.getLongitude();
			address = lat + "," + lng;
			System.out.println(lng + "," + lat);
		} else {
			address = "location can not be detected";
		}
		if (lat != 0) {
			// System.out.println("--------反馈信息----------"+
			// String.valueOf(lat));
		}

		Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT)
				.show();
		mTextView.setText(address);

		// return location;

	}
*/
	/*public final LocationListener locListerner = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			updateToNewLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			updateToNewLocation(null);
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};
*/
}
