package geometry.geom2d.algorithm;

import java.util.HashMap;

import geometry.geom2d.Point2D;
import geometry.math.Angle;
import geometry.math.MyRandom;


public class PerlinNoise {
	
	public PerlinNoise() {
	}

	
	public static double noise(Point2D p){
		
		Point2D nw = new Point2D((int)p.x, (int)(p.y+1));
		Point2D ne = new Point2D((int)(p.x+1), (int)(p.y+1));
		Point2D sw = new Point2D((int)p.x, (int)p.y);
		Point2D se = new Point2D((int)(p.x+1), (int)p.y);
		
		MyRandom.changeSeed((int)(p.x*p.y));
		
		Point2D g1 = Point2D.ORIGIN.getTranslation(MyRandom.next()*Angle.FULL, 1);
		Point2D g2 = Point2D.ORIGIN.getTranslation(MyRandom.next()*Angle.FULL, 1);
		Point2D g3 = Point2D.ORIGIN.getTranslation(MyRandom.next()*Angle.FULL, 1);
		Point2D g4 = Point2D.ORIGIN.getTranslation(MyRandom.next()*Angle.FULL, 1);
		
		Point2D sub1 = p.getSubtraction(sw);
		Point2D sub2 = p.getSubtraction(se);
		Point2D sub3 = p.getSubtraction(nw);
		Point2D sub4 = p.getSubtraction(ne);
		
		double s = g1.getDotProduct(sub1);
		double t = g2.getDotProduct(sub2);
		double u = g3.getDotProduct(sub3);
		double v = g4.getDotProduct(sub4);
		
		// s-curve : 3p^2-2p^3
		double Sx = 3*Math.pow(p.x-(int)p.x, 2) - 2*Math.pow(p.x-(int)p.x, 3);
		double a = s+Sx*(t-s);
		double b = u+Sx*(v-u);
		
		double Sy = 3*Math.pow(p.y-(int)p.y, 2) - 2*Math.pow(p.y-(int)p.y, 3);
		double res = a+Sy*(b-a);
		return (res+1)/2;
	}
}
