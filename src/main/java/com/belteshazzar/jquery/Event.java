package com.belteshazzar.jquery;

import org.w3c.dom.Element;

import netscape.javascript.JSObject;

public class Event {

	JSObject js;

	public Event(JSObject js) {
		this.js = js;
	}
	
	public Element currentTarget() {
		return (Element)js.getMember("currentTarget");
	}
	
	public Object data() {
		return js.getMember("data");
	}
	
	public Element delegateTarget() {
		return (Element)js.getMember("delegateTarget");
	}

	public boolean isDefaultPrevented() {
		return (boolean)js.call("isDefaultPrevented");
	}
	
	public boolean isImmediatePropogationStopped() {
		return (boolean)js.call("isImmediatePropogationStopped");
	}
	
	public boolean isPropogationStopped() {
		return (boolean)js.call("isPropogationStopped");
	}
	
	public String meta() {
		return (String)js.getMember("meta");
	}
	
	public String namespace() {
		return (String)js.getMember("namespace");
	}
	
	public int pageX() {
		return (int)js.getMember("pageX");
	}
	
	public int pageY() {
		return (int)js.getMember("pageY");
	}
	
	public void preventDefault() {
		js.call("preventDefault");
	}
	
	public Element relatedTarget() {
		return (Element)js.getMember("relatedTarget");
	}
	
	public Object result() {
		return js.getMember("result");
	}
	
	public void stopImmediatePropogation() {
		js.call("stopImmediatePropogation");
	}
	
	public void stopPropogation() {
		js.call("stopPropogation");
	}
	
	public Element target() {
		return (Element)js.getMember("target");
	}

	public long timeStamp() {
		return (long)(double)js.getMember("timeStamp");
	}
	
	public String type() {
		return (String)js.getMember("type");
	}
	
	public int which() {
		return (int)js.getMember("which");
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Event[");
		sb.append("currentTarget=");
		sb.append(currentTarget());
		sb.append(",data=");
		sb.append(data());
		sb.append(",delegateTarget=");
		sb.append(delegateTarget());
		sb.append(",meta=");
		sb.append(meta());
		sb.append(",namespace=");
		sb.append(namespace());
		sb.append(",pageX=");
		sb.append(pageX());
		sb.append(",pageY=");
		sb.append(pageY());
		sb.append(",relatedTarget=");
		sb.append(relatedTarget());
		sb.append(",result=");
		sb.append(result());
		sb.append(",target=");
		sb.append(target());
		sb.append(",timeStamp=");
		sb.append(timeStamp());
		sb.append(",type=");
		sb.append(type());
		sb.append(",which=");
		sb.append(which());
		sb.append("]");
		return sb.toString();
	}
}
