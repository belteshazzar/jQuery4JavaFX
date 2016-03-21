package com.belteshazzar.jquery.functions;

import org.w3c.dom.Element;

@FunctionalInterface
public interface IntElementStringFunction {
	
	String apply(int index, Element el);

}
