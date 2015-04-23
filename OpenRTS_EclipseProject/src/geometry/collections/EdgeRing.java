package geometry.collections;

import geometry.geom2d.Segment2D;

import java.util.Collection;


public class EdgeRing extends Ring<Segment2D> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EdgeRing() {
	}

	public EdgeRing(Collection<Segment2D> col) {
		super(col);
	}
	/*
	 * The Segment2D to add must have its limit in common with neighbors, and be in the same direction. (non-Javadoc)
	 * @see java.util.ArrayList#add(int, java.lang.Object)
	 */
	@Override
	public void add(int arg0, Segment2D arg1) {
		// TODO
		if(!isEmpty() && !arg1.getStart().equals(getPrevious(arg0).getEnd()));
		super.add(arg0, arg1);
	}
	
	public boolean loop() {
		for (int i = 0; i < size(); i++)
			if(!get(i).getStart().equals(getPrevious(i).getEnd()))
				return false;
		return true;
	}
}
