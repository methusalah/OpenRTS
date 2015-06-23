/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.entity.definitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Benoît
 */
public class DefElement {
	private static final String VALUE = "value";

	public String name;

	public Map<String, String> arguments = new HashMap<>();

	public DefElement(String name) {
		this.name = name;
	}

	public DefElement(String name, String val) {
		this(name);
		setVal(val);
	}

	public void setVal(String val) {
		arguments.put(VALUE, val);
	}

	public void addVal(String name, String val) {
		arguments.put(name, val);
	}

	public String getVal() {
		return getVal(VALUE);
	}

	public ArrayList<String> getAllVal() {
		return (ArrayList<String>) arguments.values();
	}

	public double getDoubleVal() {
		return Double.parseDouble(getVal());
	}

	public int getIntVal() {
		return Integer.parseInt(getVal());
	}

	public boolean getBoolVal() {
		return Boolean.parseBoolean(getVal());
	}

	public String getVal(String name) {
		return arguments.get(name);
	}

	public double getDoubleVal(String name) {
		return Double.parseDouble(getVal(name));
	}

	public int getIntVal(String name) {
		return Integer.parseInt(getVal(name));
	}

	public boolean getBoolVal(String name) {
		return Boolean.parseBoolean(getVal(name));
	}

	public boolean isSimple() {
		return arguments.size() <= 1;
	}

	public boolean equals(DefElement other) {
		if (!name.equals(other.name)) {
			return false;
		}
		for (String argName : arguments.keySet()) {
			if (other.arguments.get(argName) == null || other.arguments.get(argName) != arguments.get(argName)) {
				return false;
			}
		}
		return true;
	}
}
