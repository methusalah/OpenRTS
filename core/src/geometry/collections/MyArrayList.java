package geometry.collections;

import java.util.ArrayList;

@SuppressWarnings("hiding")
public class MyArrayList<Object> extends ArrayList<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6773331141693476040L;

	public Object getNext(int i) {
		if(i == this.size() - 1)
			return this.get(0);
		else
			return this.get(i+1);
	}
	
	public Object getPrevious(int i) {
		if(i == 0)
			return this.get(this.size() - 1);
		else
			return this.get(i - 1);
	}

	public Object getFirst() {
		return get(0);
	}

	public Object getLast() {
		return get(size() - 1);
	}

	public Object getPrevious(Object o) {
		return getPrevious(indexOf(o));
	}
	
	public Object getNext(Object o) {
		return getNext(indexOf(o));
	}
}
