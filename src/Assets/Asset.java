package Assets;


public abstract class Asset {
	int owner;
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
	public Asset(int owner, double x, double y,double d, int hp) {
		this.owner = owner;
		xCor = x;
		yCor = y;
		idle=true;
		this.diameter=d;
		hitPoints = hp;
	}
	
	public boolean collides (Asset other) 
	{
		double distX = this.xCor - other.getX();
		double distY = this.yCor - other.getY();
		double distance = Math.sqrt(distX * distX + distY * distY);
		
		return distance < ((this.diameter/2) + (other.getDiameter()/2));
	}
	
	public void setCoordinates(double x, double y) {
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
	
	public void setOwner(int owner) {
		this.owner = owner;
	}
	
	public int getOwner() {
		return owner;
	}
}
