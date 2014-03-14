package com.kue.mobile;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnClickListener {

	private ListView mForgottenListView;
	private MissingItemsAdapter mForgottenListAdapter;
	private ArrayList<String> mForgottenList;

	static private final int[] PERSONAL_ITEMS_LAYOUT_IDS = {
		//		R.id.missing_items, R.id.x_button
		R.id.personal_belongings_list, R.id.edit_button, R.id.switch_toggle
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); //instantiating
		setContentView(R.layout.settings); //slapping XML onto screen
		mForgottenListView = (ListView) findViewById(R.id.forgotten_items_list); //cast
		mForgottenList = new ArrayList<String>();
		mForgottenListAdapter = new MissingItemsAdapter(this, mForgottenList); //constructor
		mForgottenListView.setAdapter(mForgottenListAdapter); //setting the adapter

		//hardcoded names of objects
		mForgottenList.add("Car Keys");
		mForgottenList.add("Water Bottle");
		mForgottenList.add("Umbrella");
		mForgottenList.add("Tablet");
		mForgottenList.add("Charger");
		mForgottenList.add("Lunch Box");
		mForgottenList.add("Wallet");
		mForgottenList.add("Travel Mug");
		mForgottenList.add("Bus Pass");
		mForgottenList.add("Kindle");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add_item:
			//TO DO: Need to find new tag, user uses refresh button
			//If found new tag, show first tag id that is found (or show a list)
			//Then edit name (same as onClick)
			Toast.makeText(this, "Add Item Clicked", Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public class MissingItemsAdapter extends ViewAdapterBase<String>{

		public MissingItemsAdapter(Activity a, ArrayList<String> list) {
			super(a, R.layout.settings_list, PERSONAL_ITEMS_LAYOUT_IDS, list);
		}

		@Override
		protected void setWidgetValues(int position, String item, View[] elements, View layout) {
			TextView textview = (TextView) elements[0];
			textview.setText(item);
			Button editButton = (Button) elements[1];
			editButton.setOnClickListener(SettingsActivity.this);
			editButton.setTag(position);
			CompoundButton tgbutton = (CompoundButton) elements[2];
			tgbutton.setOnCheckedChangeListener(new toggleCheckedListener());
		}
	}


	@Override
	public void onClick(View v) {
		Button editButton = (Button) v;
		Log.i("Michelle", v.toString());
		final int position = (Integer) editButton.getTag();

		ViewGroup parent = (ViewGroup) v.getParent();
		final TextView itemName = (TextView) parent.findViewById(R.id.personal_belongings_list);

		//Creates alert
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Please enter object name");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setText(itemName.getText());
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				mForgottenList.set(position, value);
				mForgottenListAdapter.notifyDataSetChanged();
				Log.i("Eura", value); // way to see what happens when button is pressed
			}
		});

		alert.setNegativeButton("Cancel", null);

		alert.show();
	}

    public class toggleCheckedListener implements OnCheckedChangeListener {

    	@Override
    	public void onCheckedChanged(CompoundButton tgbutton, boolean checked) {
    		// TODO Auto-generated method stub
    		if(checked){
    			showToast("Checked");
    		}
    	}

    }
    
    public void showToast(String message){
    	Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}