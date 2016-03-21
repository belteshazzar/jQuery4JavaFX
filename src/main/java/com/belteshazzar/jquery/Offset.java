package com.belteshazzar.jquery;

import netscape.javascript.JSObject;

public class Offset {
	public double top;
	public double left;
	
	public Offset(JSObject js) {
		this.top = (double)js.getMember("top");
		this.left = (double)js.getMember("left");
	}
}