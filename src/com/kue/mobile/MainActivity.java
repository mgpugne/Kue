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
	private ArrayList<Item> mForgottenList;

	static private final int[] MISSING_ITEMS_LAYOUT_IDS = {
		R.id.missing_items, R.id.x_button, R.id.signal_strength
	};
	
	static private final long START_TIME = 5*1000;
	static private final long INTERVAL_TIME = 1*1000;
	
	private Handler mHandler;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); //instantiating
		setContentView(R.layout.activity_main); //slapping XML onto screen
		mForgottenListView = (ListView) findViewById(R.id.forgotten_items_list); //cast
		mForgottenList = new ArrayList<Item>();
		mForgottenListAdapter = new MissingItemsAdapter(this, mForgottenList); //constructor
		mForgottenListView.setAdapter(mForgottenListAdapter); //setting the adapter		

		//hardcoded names of objects
		mForgottenList.add(new Item(1, "Car Keys", true, 15));
		mForgottenList.add(new Item(2, "Water Bottle", true, 28));
		mForgottenList.add(new Item(3, "Umbrella", true, 20));
		mForgottenList.add(new Item(4, "Tablet", true, 20));
		mForgottenList.add(new Item(5, "Charger", true, 20));
		mForgottenList.add(new Item(6, "Lunch Box", true, 20));
		mForgottenList.add(new Item(7, "Travel Mug", true, 20));
		mForgottenList.add(new Item(8, "Bus Pass", true, 20));
		
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
		
		mHandler = new Handler();
		startRepeatingTask();
		

		
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

	public class MissingItemsAdapter extends ViewAdapterBase<Item>{

		public MissingItemsAdapter(Activity a, ArrayList<Item> list) {
			super(a, R.layout.missing_item_list_item, MISSING_ITEMS_LAYOUT_IDS, list);
		}

		@Override
		protected void setWidgetValues(int position, Item item, View[] elements, View layout) {
			TextView textview = (TextView) elements[0];
			textview.setText(item.getName());

			ImageView xButton = (ImageView) elements[1];
			if (item.inRange()){
				xButton.setImageDrawable(getResources().getDrawable(R.drawable.badge_square_check));
			}
			else{
				xButton.setImageDrawable(getResources().getDrawable(R.drawable.alert_square_red));
			}
			
			ProgressBar signalStrength = (ProgressBar) elements[2];
			signalStrength.setIndeterminate(false);
			signalStrength.setProgress(item.getSignalStrength());
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

		if (data.length>1){
			String stringData = SlickUSB2Serial.convertByte2String(data)
					.replace("00", "")
					.replace("0a", "")
					.replace("0d", "")
					.replace(" ", "");
			stringData = convertHexToString(stringData);
			String[] params = {stringData.substring(7, 8), //Tag number
			                   stringData.substring(11, 13), //Signal strength
			                   stringData.substring(15, 18)}; //Battery life
			final int tagNo = Integer.parseInt(params[0]);
			final int signalStrength = Integer.parseInt(params[1]);
			final float batteryLife = Float.parseFloat(params[2]);
			
			Item currentItem = mForgottenList.get(tagNo-1);
			currentItem.setSignalStrength(signalStrength);
			currentItem.setBatteryLife(batteryLife);
			mForgottenList.set(tagNo-1, currentItem);

		}
		
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
	
	public String convertHexToString(String hex){
		 
		  StringBuilder sb = new StringBuilder();
		  StringBuilder temp = new StringBuilder();
	 
		  //49204c6f7665204a617661 split into two characters 49, 20, 4c...
		  for( int i=0; i<hex.length()-1; i+=2 ){
	 
		      //grab the hex in pairs
		      String output = hex.substring(i, (i + 2));
		      //convert hex to decimal
		      int decimal = Integer.parseInt(output, 16);
		      //convert the decimal to character
		      sb.append((char)decimal);
	 
		      temp.append(decimal);
		  }
		  System.out.println("Decimal : " + temp.toString());
	 
		  return sb.toString();
	}
	
	
	Runnable mStatusChecker = new Runnable() {
	    @Override 
	    public void run() {
	      updateStatus(); //this function can change value of mInterval.
	      mHandler.postDelayed(mStatusChecker, 1000);
	    }
	  };
	
	private void updateStatus(){
		runOnUiThread(new Runnable(){
			public void run(){
				mForgottenListAdapter.notifyDataSetChanged();
				
			}
		});
	}
	void startRepeatingTask() {
	    mStatusChecker.run(); 
	  }

	void stopRepeatingTask() {
		mHandler.removeCallbacks(mStatusChecker);
	}
		
	
}
