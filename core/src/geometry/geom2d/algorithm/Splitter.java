package geometry.geom2d.algorithm;

import geometry.geom2d.Line2D;
import geometry.geom2d.Polygon;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Splitter {
	
	protected Line2D axis;
	protected ArrayList<Splitting> allSplittings = new ArrayList<Splitting>();
	private ArrayList<Splitting> averageSplittings = new ArrayList<Splitting>();
	private ArrayList<Splitting> loneSplittings = new ArrayList<Splitting>();
	private ArrayList<Splitting> repeatedSplittings = new ArrayList<Splitting>();
	
	protected HashMap<String, ArrayList<Polygon>> splits = new HashMap<String, ArrayList<Polygon>>();
	protected boolean computed = false;
	protected double totalWidth = Double.NaN;
	
	public Splitter() {
		splits.put("remain", new ArrayList<Polygon>());
	}
	
	public void addSpliting(String label, double width, boolean repeated, boolean average){
		Splitting s = new Splitting(label, width, repeated, average);
		allSplittings.add(s);
		if(average)
			averageSplittings.add(s);
		if(repeated)
			repeatedSplittings.add(s);
		else
			loneSplittings.add(s);
		
		if(!splits.containsKey(label))
			splits.put(label, new ArrayList<Polygon>());
	}
	
	/**
	 * Adds a repeated and absolute split
	 * @param label
	 * @param value
	 */
	public void addSpliting(String label, double value){
		addSpliting(label, value, true, false);
	}
	
	public ArrayList<Polygon> getSplits(String label) {
		compute();
		return splits.get(label);
	}

	protected abstract void compute();
	
	protected void fitSplittings() {
		double remainingWidth = totalWidth;
		double floatingWidth = Double.NaN;
		
		// We first place the lone splittings
		for(Splitting s : loneSplittings)
			if(s.width <= remainingWidth){
				s.count++;
				remainingWidth-=s.width;
			} else {
				floatingWidth = addOrDiscard(s, remainingWidth);
				break;
			}
		// Then, we place one of each repeated splittings until
		// there is no remaining place
		if(!repeatedSplittings.isEmpty())
			while (Double.isNaN(floatingWidth))
				for(Splitting s : repeatedSplittings) {
					if(s.width <= remainingWidth) {
						s.count++;
						remainingWidth-=s.width;
					} else {
						floatingWidth = addOrDiscard(s, remainingWidth);
						break;
					}
				}
		else
			floatingWidth = remainingWidth;
		// we get the total average occupation and dispatch the floating width
		double totalAverageWidth = 0;
		for(Splitting s : averageSplittings)
			totalAverageWidth += s.width*s.count;
		for(Splitting s : averageSplittings){
			s.width += (s.width*floatingWidth)/totalAverageWidth;
		}
		
	}

	private double addOrDiscard(Splitting splitting, double availableWidth) {
		if(!splitting.average || availableWidth < splitting.width/2) {
			// discard
			return availableWidth;
		} else {
			// add
			splitting.count++;
			return availableWidth-splitting.width;
		}
	}
	
	public String toString() {
		String res = new String("total width : "+totalWidth);
		int cumul = 0;
		for(Splitting s : allSplittings)
			for(int i=0; i<s.count; i++) {
				res = res+System.getProperty("line.separator");
				res = res+""+s.label+" "+s.width+" ("+(cumul+=s.width)+")";
			}
		return res;
	}

	// inner class
	class Splitting {
		String label;
		double width;
		boolean repeated;
		boolean average;
		int count = 0;
		public Splitting(String label, double width, boolean repeated, boolean average) {
			if(width <= 0)
				throw new RuntimeException("incorrect splitting width : "+width);
			if(label == null || label.isEmpty())
				throw new RuntimeException("incorrect splitting label : "+label);
			this.label = label;
			this.width = width;
			this.repeated = repeated;
			this.average = average;
		}
	};
}
