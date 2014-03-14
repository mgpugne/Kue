package com.kue.mobile;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ListView mForgottenListView;
	//	private ListView mObjectListView;
	private MissingItemsAdapter mForgottenListAdapter;
	//	private ArrayAdapter<String> mObjectListAdapter;
	private ArrayList<Pair<String,Boolean>> mForgottenList;

	static private final int[] MISSING_ITEMS_LAYOUT_IDS = {
		R.id.missing_items, R.id.x_button
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); //instantiating
		setContentView(R.layout.activity_main); //slapping XML onto screen
		mForgottenListView = (ListView) findViewById(R.id.forgotten_items_list); //cast
		mForgottenList = new ArrayList<Pair<String,Boolean>>();
		mForgottenListAdapter = new MissingItemsAdapter(this, mForgottenList); //constructor
		mForgottenListView.setAdapter(mForgottenListAdapter); //setting the adapter

		//		mObjectListView = (ListView) findViewById(R.id.items_list); //cast
		//		mObjectListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		//		mObjectListView.setAdapter(mObjectListAdapter); //setting the adapter

		//hardcoded names of objects
		mForgottenList.add(new Pair<String,Boolean>("Car Keys",true));
		mForgottenList.add(new Pair<String,Boolean>("Water Bottle",true));
		mForgottenList.add(new Pair<String,Boolean>("Umbrella",true));
		mForgottenList.add(new Pair<String,Boolean>("Tablet",true));
		mForgottenList.add(new Pair<String,Boolean>("Charger",true));
		mForgottenList.add(new Pair<String,Boolean>("Lunch Box",true));
		mForgottenList.add(new Pair<String,Boolean>("Wallet",true));
		mForgottenList.add(new Pair<String,Boolean>("Travel Mug",true));
		mForgottenList.add(new Pair<String,Boolean>("Bus Pass",true));
		mForgottenList.add(new Pair<String,Boolean>("Kindle",true));

		//		mObjectListAdapter.add("Charger");
		//		mObjectListAdapter.add("Lunch Box");
		//		mObjectListAdapter.add("Wallet");
		//		mObjectListAdapter.add("Travel Mug");

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

}
