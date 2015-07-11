/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders.entity;

import java.util.logging.Logger;

import model.builders.entity.definitions.Definition;

/**
 *
 * @author Benoît
 */
public abstract class Builder {

	private static final Logger logger = Logger.getLogger(Builder.class.getName());
	protected Definition def;

	public Builder(Definition def) {
		this.def = def;
	}

	public abstract void readFinalizedLibrary();

	public String getId(){
		return def.getId();
	}

	public void printUnknownElement(String elementName){
		logger.warning("Element '" + elementName + "' unknown in definition '" + getId() + "'.");
	}

	public void printUnknownArgument(String elementName, String argumentName){
		logger.warning("Argument '" + argumentName + "' unknown for element '" + elementName + "' in definition '" + getId() + "'.");
	}

	public void printUnknownValue(String elementName, String value){
		logger.warning("value '" + value + "' unknown for element '" + elementName + "' in definition '" + getId() + "'.");
	}
}
