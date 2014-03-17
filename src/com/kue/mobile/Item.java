package com.kue.mobile;

public class Item {
	private int id;
	private String name;
	private boolean inRange;
	private int signalStrength;
	private float batteryLife;
	
	Item(int mId, String mName){
		id = mId;
		name = mName;
	}
	Item(int mId, String mName, boolean mInRange){
		id = mId;
		name = mName;
		inRange = mInRange;
	}
	Item(int mId, String mName, boolean mInRange, int mSignalStrength){
		id = mId;
		name = mName;
		inRange = mInRange;
		signalStrength = mSignalStrength;
	}
	
	
	public boolean inRange(){
		return inRange;
	}
	public void setRange(boolean mInRange){
		inRange = mInRange;
	}
	
	public int getId(){
		return id;
	}	
	
	public String getName(){
		return name;
	}
	public void setName(String mName){
		name = mName;
	}
	
	public int getSignalStrength(){
		return signalStrength;
	}
	public void setSignalStrength(int mSignalStrength){
		signalStrength = mSignalStrength-12;
	}
	
	public float getBatteryLife(){
		return batteryLife;
	}
	public void setBatteryLife(float mBatteryLife){
		batteryLife = mBatteryLife;
	}
}
