/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.editor.tools;

import java.util.Arrays;
import java.util.List;

import model.editor.AssetSet;
import model.editor.Pencil;

/**
 * @author Benoît
 */
public abstract class Tool {
	private List<String> operations;
	protected AssetSet set = null;
	protected String actualOp;

	public Pencil pencil;

	public Tool(String... operationsArray) {
		operations = Arrays.asList(operationsArray);
		actualOp = operations.get(0);
		createPencil();
	}

	protected abstract void createPencil();

	public abstract void primaryAction();

	public abstract void secondaryAction();

	public void toggleOperation() {
		int index = operations.indexOf(actualOp) + 1;
		if (index >= operations.size()) {
			index = 0;
		}
		actualOp = operations.get(index);
	}

	public final String getOperation(int index) {
		if (index >= 0 && index < operations.size()) {
			return operations.get(index);
		}
		return "";
	}

	public String getActualOperation() {
		return actualOp;
	}

	public void setOperation(int index) {
		if (index >= 0 && index < operations.size()) {
			actualOp = operations.get(index);
		}
	}

	public boolean isAnalog() {
		return true;
	}

	public boolean hasSet() {
		return set != null;
	}

	public AssetSet getSet() {
		return set;
	}

}
