package Assets;

public abstract class Asset {
	double xCor,yCor;
	int hitPoints;
	double diameter;


	boolean idle;
	/**
	 * 
	 * @param x diameter as factor of the map tile size
	 * @param y
	 * @param d
	 * @param hp
	 */
	public Asset(double x, double y,double d, int hp) {
		xCor = x;
		yCor = y;
		idle=true;
		this.diameter=d;
		hitPoints = hp;
	}
	
	public void setCoordinates(float x, float y) {
		xCor = x;
		yCor = y;
	}
	
	public double getX() {
		return xCor;
	}
	
	public double getY() {
		return yCor;
	}
	
	public void damage(int hit) {
		hitPoints -= hit;
	}
	
	public boolean destroyed() {
		return hitPoints <= 0;
	}

	public boolean isIdle() {
		return idle;
	}

	public void setIdle(boolean idle) {
		this.idle = idle;
	}
	
	public double getDiameter() {
		return diameter;
	}
}
