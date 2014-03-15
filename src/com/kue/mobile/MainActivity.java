package com.kue.mobile;

import java.util.ArrayList;

import slickdevlabs.apps.usb2seriallib.AdapterConnectionListener;
import slickdevlabs.apps.usb2seriallib.SlickUSB2Serial;
import slickdevlabs.apps.usb2seriallib.USB2SerialAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements AdapterConnectionListener, USB2SerialAdapter.DataListener{

	private ListView mForgottenListView;
	private MissingItemsAdapter mForgottenListAdapter;
	private ArrayList<Pair<String,Boolean>> mForgottenList;

	static private final int[] MISSING_ITEMS_LAYOUT_IDS = {
		R.id.missing_items, R.id.x_button, R.id.signal_strength
	};
	
	static private final int EZ430_VENDOR_ID = 1105;
	static private final int EZ430_PRODUCT_ID = 62514;
	
	static private final long START_TIME = 5*1000;
	static private final long INTERVAL_TIME = 1*1000;
	
	private int signal = 0;
	private Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); //instantiating
		setContentView(R.layout.activity_main); //slapping XML onto screen
		mForgottenListView = (ListView) findViewById(R.id.forgotten_items_list); //cast
		mForgottenList = new ArrayList<Pair<String,Boolean>>();
		mForgottenListAdapter = new MissingItemsAdapter(this, mForgottenList); //constructor
		mForgottenListView.setAdapter(mForgottenListAdapter); //setting the adapter		

		//hardcoded names of objects
		mForgottenList.add(new Pair<String,Boolean>("Car Keys",true));
		mForgottenList.add(new Pair<String,Boolean>("Water Bottle",true));
		mForgottenList.add(new Pair<String,Boolean>("Umbrella",true));
		/*mForgottenList.add(new Pair<String,Boolean>("Tablet",true));
		mForgottenList.add(new Pair<String,Boolean>("Charger",true));
		mForgottenList.add(new Pair<String,Boolean>("Lunch Box",true));
		mForgottenList.add(new Pair<String,Boolean>("Wallet",true));
		mForgottenList.add(new Pair<String,Boolean>("Travel Mug",true));
		mForgottenList.add(new Pair<String,Boolean>("Bus Pass",true));
		mForgottenList.add(new Pair<String,Boolean>("Kindle",true));*/
		
		//USB stuff
		SlickUSB2Serial.initialize(this);
		SlickUSB2Serial.connectProlific(this);
		

		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_connect:
			SlickUSB2Serial.connectProlific(this);
            return true;
		case R.id.action_settings:
			// opens settings activity
			Intent myIntent = new Intent(this, SettingsActivity.class);
			startActivityForResult(myIntent, 0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public class MissingItemsAdapter extends ViewAdapterBase<Pair<String,Boolean>>{

		public MissingItemsAdapter(Activity a, ArrayList<Pair<String,Boolean>> list) {
			super(a, R.layout.missing_item_list_item, MISSING_ITEMS_LAYOUT_IDS, list);
		}

		@Override
		protected void setWidgetValues(int position, Pair<String,Boolean> item, View[] elements, View layout) {
			TextView textview = (TextView) elements[0];
			textview.setText(item.first);

			ImageView xButton = (ImageView) elements[1];
			if (item.second){
				xButton.setImageDrawable(getResources().getDrawable(R.drawable.badge_square_check));
			}
			else{
				xButton.setImageDrawable(getResources().getDrawable(R.drawable.alert_square_red));
			}
			
		}

	}

	
	@Override
	public void onAdapterConnected(USB2SerialAdapter adapter) {
		adapter.setDataListener(this);
		Toast.makeText(this, "Adapter "+adapter.getDeviceId()+" Connected!", Toast.LENGTH_SHORT).show();
		
		//Notification Countdown stuff
		//Need to do this for each tag
		//If notification preference is on
		//AND if usb is plugged in (i.e. if adapter connected or error)
		//start countdown
	}
	
	@Override
	public void onAdapterConnectionError(int error, String msg) {
		if(error==AdapterConnectionListener.ERROR_UNKNOWN_IDS){
			final AlertDialog dialog = new AlertDialog.Builder(this)
			.setIcon(0)
			.setTitle("Choose Adapter Type")
			.setItems(new String[]{"Prolific", "FTDI"}, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(which==0)
						SlickUSB2Serial.connectProlific(MainActivity.this);
					else 
						SlickUSB2Serial.connectFTDI(MainActivity.this);
				}
			}).create();
			dialog.show();
			return;
		}
		//TO DO: Add if already connected
		
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onDataReceived(int id, byte[] data) {
		
		//final String newText = mOutput.getText().toString()+" "+new String(data);
		runOnUiThread(new Runnable(){
			public void run(){
				//mOutput.setText(newText);
				//mOutput.setSelection(newText.length());
				signal = signal+1;
			}
		});
		// TO DO: reset time count from here
	}
	
	@Override
	public void onDestroy() {
		SlickUSB2Serial.cleanup(this);
		super.onDestroy();
	}
	
	public class MyCountDownTimer extends CountDownTimer {
		public MyCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
		}
		@Override
		public void onFinish() {
			//Fire notification from here
		}
		@Override
		public void onTick(long arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
	
}
