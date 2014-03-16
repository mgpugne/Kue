package com.kue.mobile;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


public abstract class ViewAdapterBase<TItem> extends ArrayAdapter<TItem>{
    protected ArrayList<TItem> mEntries;
    private final Activity mActivity;
    private final int mWidgetLayout;
    private final int[] mResources;

    public ViewAdapterBase(Activity a, int widgetResourceLayout, int[] viewResourceIdListInWidget, ArrayList<TItem> list) {
        super(a, 0, list);
        mEntries = list;
        mActivity = a;
        mResources = viewResourceIdListInWidget;
        mWidgetLayout = widgetResourceLayout;
    }

    protected abstract void setWidgetValues(int position, TItem item, View[] elements, View layout);

    protected Activity getActivity() {
        return mActivity;
    }

    public ArrayList<TItem> getList() {
        return mEntries;
    }

    protected void onCreateListItem(int position, View item, ViewGroup parent) {
    }

    protected TItem remove(int position) {
        if (mEntries == null) {
            return null;
        }
        return mEntries.remove(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("Michelle", "Get View");
    	View viewObj = convertView;
        View[] elements = null;
        if (viewObj == null) {
            LayoutInflater inflator = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewObj = inflator.inflate(mWidgetLayout, null);
            int size = mResources.length;
            elements = new View[size];
            for (int i = 0; i < size; i++) {
                elements[i] = viewObj.findViewById(mResources[i]);
            }
            viewObj.setTag(elements);
            onCreateListItem(position, viewObj, parent);
        } else {
            elements = (View[]) viewObj.getTag();
        }
        final TItem item = mEntries.get(position);
        for (View v: elements) {
            v.setVisibility(View.VISIBLE);
        }
        setWidgetValues(position, item, elements, viewObj);
        return viewObj;
    }
}
