package geometry.geom2d;

public class Transform2D {

	double angle = 0;
	Point2D pivot = Point2D.ORIGIN;
	Point2D translation = Point2D.ORIGIN;
	Point2D scale = Point2D.UNIT_XY;
	
	public Transform2D() {
	}
	
	public Transform2D(Point2D translation) {
		this(translation, 0, new Point2D(1, 1));
	}
	public Transform2D(double x, double y) {
		this(new Point2D(x, y), 0, 1, 1);
	}
	public Transform2D(double angle) {
		this(Point2D.ORIGIN, angle, 1, 1);
	}
	public Transform2D(double angle, Point2D pivot) {
		this(Point2D.ORIGIN, angle, 1, 1);
		this.pivot = pivot;
	}
	public Transform2D(Point2D translation, double angle) {
		this(translation, angle, 1, 1);
	}
	public Transform2D(double x, double y, double angle) {
		this(new Point2D(x, y), angle, 1, 1);
	}
	public Transform2D(Point2D translation, double angle, double scale) {
		this(translation, angle, scale, scale);
	}
	public Transform2D(Point2D translation, double angle, double scaleX, double scaleY) {
		this(translation, angle, new Point2D(scaleX, scaleY));
	}
	public Transform2D(Point2D translation, double angle, Point2D scale) {
		this.translation = translation;
		this.angle = angle;
		this.scale = scale;
	}
	
	public void setTranslation(Point2D translation){
		this.translation = translation;
	}
	public void setTranslation(double x, double y){
		setTranslation(new Point2D(x, y));
	}
	
	public void setRotation(double angle){
		this.angle = angle;
	}
	public void setRotation(double angle, Point2D pivot){
		this.angle = angle;
		this.pivot = pivot;
	}
	public void setPivot(Point2D pivot){
		this.pivot = pivot;
	}
	
	
	public void setScale(Point2D scale){
		this.scale = scale;
	}
	public void setScale(double x, double y){
		setScale(new Point2D(x, y));
	}
	
	public Point2D getTransformed(Point2D p){
		Point2D res = p.getMult(scale);
		res = res.getRotation(angle, pivot);
		res = res.getAddition(translation);
		return res;
	}
	public Point2D getRestored(Point2D p){
		Point2D res = p.getSubtraction(translation);
		res = res.getRotation(-angle, pivot);
		res = res.getDivision(scale);
		return res;
	}

	public Transform2D getInverse() {
		Transform2D res = new Transform2D();
		res.setRotation(-angle, pivot);
		res.setTranslation(translation.getNegation());
		res.setScale(Point2D.UNIT_XY.getDivision(scale));
		return res;
	}
	
}
