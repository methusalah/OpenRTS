package geometry.geom2d.algorithm;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class AlgoException extends RuntimeException {
	
	private Map<String, Object> data = new HashMap<String, Object>();
	
	public AlgoException(String s, Throwable e) {
		super(s, e);
	}

	public AlgoException(Exception e) {
		super(e);
	}

	public void add(String s, Object o) {
		data.put(s, o);
	}
	
	@Override
	public String toString() {
		String res = super.toString();
		for (String s : data.keySet())
			res += "\n" + s + ":" + data.get(s).toString();
		return res;
	}
	
}
