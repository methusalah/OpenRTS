package geometry.geom2d.algorithm;

import geometry.geom2d.Point2D;
import geometry.math.AngleUtil;
import geometry.math.RandomUtil;

import java.util.ArrayList;
import java.util.List;

public class PoissonDiscSampler {
	private static final int MAX_SAMPLE_COUNT = 30;
	
	double radius, radius2, R;
	int width, height;
	public List<Point2D> grid;
	List<Point2D> queue;
	
	public PoissonDiscSampler(int width, int height, double radius){
		this.width = width;
		this.height = height;
		this.radius = radius;
		radius2 = radius*radius;
		R = 3*radius2;
		grid = new ArrayList<>(width*height);
		queue = new ArrayList<>(width*height);
		computeSamples();
	}
	
	private void putSample(double x, double y){
		putSample(new Point2D(x, y));
	}
	private void putSample(Point2D p){
		queue.add(p);
		grid.add(p);
	}
	
	private void computeSamples(){
		putSample(RandomUtil.between(0, width), RandomUtil.between(0, height));
		
		while(!queue.isEmpty()){
			Point2D s = queue.get(RandomUtil.nextInt(queue.size()));
			boolean sampleFound = true;
			for(int i = 0; i<MAX_SAMPLE_COUNT; i++){
				Point2D c = s.getTranslation(RandomUtil.between(0, AngleUtil.FULL), RandomUtil.between(2*radius, 6*radius));
				if(c.x<0 || c.x>=width || c.y<0 || c.y>=height)
					continue;
				sampleFound = true;
				for(Point2D p : grid){
					if(p != null && p.getDistance(c) < 2*radius){
						sampleFound = false;
						break;
					}
				}
				if(sampleFound){
					putSample(c);
					break;
				}
			}
			if(!sampleFound)
				queue.remove(s);
		}
	}
}
