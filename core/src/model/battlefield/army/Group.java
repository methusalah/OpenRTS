package model.battlefield.army;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.army.components.Unit;

public class Group extends ArrayList<Unit>{
	
	public Group(List<Unit> selection){
		this.addAll(selection);
	}
	
	public Group(){
		super();
	}
}
