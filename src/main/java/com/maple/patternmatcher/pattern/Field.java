package com.maple.patternmatcher.pattern;

import java.util.HashMap;
import java.util.Map;

public class Field {

	public static final String WILDCARD = "*";
	public static final String TERMINATOR = "**";
	
	private final String value;
	private Map<String, Field> fields;
	
	public Field(String fieldName) {
		this.value = fieldName;
		this.fields = new HashMap<String, Field>();
	}
	
	public boolean hasField(String fieldName) {
		return fields.containsKey(fieldName);
	}
	
	public void addField(String fieldName) {
		fields.put(fieldName, new Field(fieldName));
	}
	
	public Field getField(String fieldName) {
		return fields.get(fieldName);
	}
	
	public boolean hasFields() {
		return !fields.isEmpty();
	}
	
	public boolean hasTerminator() {
		return fields.containsKey(TERMINATOR);
	}
	
	public String getValue() {
		return this.value;
	}
	
	public Map<String, Field> getFields() {
		return this.fields;
	}
	
	@Override
	public boolean equals(Object o) {
		return this.value.equals(o);
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
	@Override
	public String toString() {
		return this.value + "->" + this.fields.toString();
	}
}
