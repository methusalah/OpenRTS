package geometry.geom2d.algorithm;

import geometry.collections.Ring;
import geometry.geom2d.Polygon;

import java.util.ArrayList;


public class SetbackOperator {
	// inputs
	Polygon p;
	
	// outputs
	Polygon remainder;
	public ArrayList<Polygon> borders = new ArrayList<Polygon>();
	
	// internal data
	Ring<Double> offsets;
	boolean computed = false;
	
	public SetbackOperator(Polygon p) {
		this.p = p;
		reset();
	}
	
	private void reset() {
		borders = new ArrayList<Polygon>();
		offsets = new Ring<Double>();
		computed = false;
	}
	
	public void setIShape(double frontWidth) {
		reset();
		
		EdgeSelector selector = new EdgeSelector(p);
		selector.setRelativeNormal();
		selector.selectByNormal(EdgeSelector.Direction.FRONT, frontWidth);
		
		offsets = selector.edgeValues;
	}

	public void setLShape(double frontWidth, double leftWidth) {
		reset();

		EdgeSelector selector = new EdgeSelector(p);
		selector.setRelativeNormal();
		selector.selectByNormal(EdgeSelector.Direction.FRONT, frontWidth);
		selector.selectByNormal(EdgeSelector.Direction.LEFT, leftWidth);
		
		offsets = selector.edgeValues;
	}

	public void setUShape(double frontWidth, double leftWidth, double rightWidth) {
		reset();
		
		EdgeSelector selector = new EdgeSelector(p);
		selector.setRelativeNormal();
		selector.selectByNormal(EdgeSelector.Direction.FRONT, frontWidth);
		selector.selectByNormal(EdgeSelector.Direction.LEFT, leftWidth);
		selector.selectByNormal(EdgeSelector.Direction.RIGHT, rightWidth);
		
		offsets = selector.edgeValues;
	}

	public ArrayList<Polygon> getBorders() {
		compute();
		return borders;
	}

	public Polygon getUniqueBorder() {
		compute();
		// TODO : unified the border polygons
		return borders.get(0);
	}
	
	public Polygon getRemainder() {
		compute();
		return remainder;
		
	}
	
	private void compute() {
		if(computed)
			return;
		
		OffsetOperator op = new OffsetOperator(p);
		op.setOffsets(offsets);
		remainder = op.getRemainder();
		borders = op.getBorders();
		computed = true;
	}
}
