package geometry.geom2d.algorithm;

import geometry.collections.Map2D;
import geometry.geom2d.Point2D;
import geometry.math.AngleUtil;
import geometry.math.RandomUtil;


public class PerlinNoise {
	
	public static int RESOLUTION = 200;
	
	Map2D<Point2D> gradients;
	
	public PerlinNoise() {
		gradients = new Map2D<>(RESOLUTION, RESOLUTION);
		for(int i = 0; i<RESOLUTION*RESOLUTION; i++)
			gradients.set(i, Point2D.ORIGIN.getTranslation(RandomUtil.next()*AngleUtil.FULL, 1));
	}

	
	public double noise(Point2D p, int octaves, double persistence) {
	    double total = 0;
	    double frequency = 1;
	    double amplitude = 1;
	    double maxValue = 0;  // Used for normalizing result to 0.0 - 1.0
	    for(int i = 0; i < octaves; i++) {
	        total += noise(p.getMult(frequency)) * amplitude;
	        
	        maxValue += amplitude;
	        
	        amplitude *= persistence;
	        frequency *= 2;
	    }
	    return total/maxValue;
	}
	
	public double noise(Point2D p){
		
		int x = (int)p.x;
		int y = (int)p.y;
		
		Point2D sw = new Point2D(x, y);
		Point2D se = new Point2D(x+1, y);
		Point2D nw = new Point2D(x, y+1);
		Point2D ne = new Point2D(x+1, y+1);
		
		Point2D g1 = gradients.get(x%RESOLUTION, y%RESOLUTION);
		Point2D g2 = gradients.get((x+1)%RESOLUTION, y%RESOLUTION);
		Point2D g3 = gradients.get(x%RESOLUTION, (y+1)%RESOLUTION);
		Point2D g4 = gradients.get((x+1)%RESOLUTION, (y+1)%RESOLUTION);
		
		Point2D sub1 = p.getSubtraction(sw);
		Point2D sub2 = p.getSubtraction(se);
		Point2D sub3 = p.getSubtraction(nw);
		Point2D sub4 = p.getSubtraction(ne);
		
		double s = g1.getDotProduct(sub1);
		double t = g2.getDotProduct(sub2);
		double u = g3.getDotProduct(sub3);
		double v = g4.getDotProduct(sub4);
		
		double Sx = ease(p.x);
		double a = s+Sx*(t-s);
		double b = u+Sx*(v-u);
		
		double Sy = ease(p.y);
		double res = a+Sy*(b-a);
		return (res+1)/2;
	}
	
	private double ease(double val){
		// s-curve or ease curve : 3p^2-2p^3
		return 3*Math.pow(val-(int)val, 2) - 2*Math.pow(val-(int)val, 3);
	}
}
