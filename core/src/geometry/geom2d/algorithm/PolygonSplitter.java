package geometry.geom2d.algorithm;

import geometry.geom2d.Line2D;
import geometry.geom2d.Polygon;
import geometry.math.AngleUtil;

import java.util.ArrayList;

public class PolygonSplitter extends Splitter{
	Polygon p;
	
	public PolygonSplitter(String axis, Polygon polygon) {
		super();
		p = polygon;
		if(axis == "x") {
			this.axis = p.getFirstEdgeAlignedBoundingBox().getEdges().getLast();
			totalWidth = p.getWidth();
		} else if(axis == "y") {
			// TODO ici si je ne fait rien, il va couper de haut en bas.
			this.axis = new Line2D(p.getEdges().getFirst());
			totalWidth = p.getHeight();
		} else throw new IllegalArgumentException("Invalid axis : "+axis);
	}

	@Override
	protected void compute() {
		if(computed)
			return;
		
		fitSplittings();
		for(Splitting s : allSplittings) {
			for(int i = 0; i<s.count; i++) {
				axis = axis.getTranslation(axis.getAngle()+AngleUtil.RIGHT, s.width);
				
				if(axis.isCollinear(p.getFirstEdgeAlignedBoundingBox().getEdges().get(1))){
					splits.get(s.label).add(p);
					p = null;
					continue;
				}
					
				ArrayList<Polygon> spl = p.getSplits(axis);
				
				splits.get(s.label).add(spl.get(0));
				p = spl.get(1);
			}
		}
		if(p != null)
			splits.get("remain").add(p);
		computed = true;	}

}
