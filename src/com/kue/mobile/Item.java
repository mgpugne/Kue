package com.kue.mobile;

public class Item {
	private int id;
	private String name;
	private boolean inRange;
	
	Item(int mId, String mName){
		id = mId;
		name = mName;
	}
	Item(int mId, String mName, boolean mInRange){
		id = mId;
		name = mName;
		inRange = mInRange;
	}
	
	
	public boolean inRange(){
		return inRange;
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
}
