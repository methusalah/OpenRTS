package geometry.collections;

import geometry.geom2d.Facet;
import geometry.geom2d.Point2D;


@SuppressWarnings("serial")
public class FacetSerie extends Chain<Facet> {

	Point2D firstPoint;
	public void addPoint(Point2D p){
		if(isEmpty())
			if(firstPoint == null)
				firstPoint = p;
			else
				add(new Facet(firstPoint, p));
		else {
			Point2D start = getLast().getEnd();
			add(new Facet(start, p));
		}
	}

	public void addPoint(double x, double y){
		addPoint(new Point2D(x, y));
	}
	
	public void smoothNormals(){
		for(int i=0; i<size()-1; i++)
			get(i).smoothNormal(get(i+1));
		
		if(getFirst().getStart().equals(getLast().getEnd()))
			getFirst().smoothNormal(getLast());
	}
	
	public Point2D getLastPoint(){
		if(isEmpty())
			if(firstPoint == null)
				throw new RuntimeException("this"+this.getClass().getSimpleName()+" is empty.");
			else
				return firstPoint;
		else
			return getLast().getEnd();
	}
	
	public void close(){
		add(new Facet(getLast().getEnd(), getFirst().getStart()));
	}
}
