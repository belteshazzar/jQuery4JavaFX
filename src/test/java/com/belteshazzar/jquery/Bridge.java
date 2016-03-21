package com.belteshazzar.jquery;

public class Bridge {

	public void log(String msg) {
		System.err.println("Bridge: " + msg);
	}
	
	public void test(Object el) {
		System.err.println("test = " + el.getClass());
	}
}
