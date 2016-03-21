package com.belteshazzar.jquery;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.belteshazzar.jquery.functions.PromiseBooleanVoidFunction;
import com.belteshazzar.jquery.functions.PromiseIntIntVoidFunction;
import com.belteshazzar.jquery.functions.PromiseVoidFunction;

import java.util.Set;

import netscape.javascript.JSObject;

public class PlainObject {
	
	private Map<String,Object> values;
	
	public PlainObject() {
		values = new HashMap<String,Object>();
	}

	public PlainObject(JSObject js) {
		this();
		final JSObject names = (JSObject)js.eval("(function (obj) { var names = []; for (var name in obj) names.push(name); return names; })(this)");
		final int count = (int)names.getMember("length");
		for (int i=0 ; i<count ; i++) {
			String name = (String)names.getSlot(i);
			values.put(name, js.getMember(name));
		}
	}

	public PlainObject set(String key, Object value) {
		values.put(key, value);
		return this;
	}
	
	public Object get(String key) {
		return values.get(key);
	}
	
	public Set<String> keys() {
		return values.keySet();
	}
	
	public Collection<Object> values() {
		return values.values();
	}
	
	public Set<Map.Entry<String,Object>> entries() {
		return values.entrySet();
	}
	
	public PlainObject remove(String key) {
		values.remove(key);
		return this;
	}
	
	public PlainObject empty() {
		values.clear();
		return this;
	}

	public JSObject toJSObject() {
		JSObject js = JQuery.createObject();
		for (Map.Entry<String,Object> entry : values.entrySet()) {
			String name = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof PromiseVoidFunction) {
				js.setMember(name, JQuery.createFunction((PromiseVoidFunction)value));
			} else if (value instanceof PromiseBooleanVoidFunction) {
				js.setMember(name, JQuery.createFunction((PromiseBooleanVoidFunction)value));
			} else if (value instanceof PromiseIntIntVoidFunction) {
				js.setMember(name, JQuery.createFunction((PromiseIntIntVoidFunction)value));
			} else {
				js.setMember(name, value);
			}
		}
		return js;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PlainObject[");
		Iterator<Entry<String, Object>> entries = entries().iterator();
		if (entries.hasNext()) {
			Entry<String,Object> entry = entries.next();
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
			while (entries.hasNext()) {
				entry = entries.next();
				sb.append(",");
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());
			}
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlainObject other = (PlainObject) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
}
