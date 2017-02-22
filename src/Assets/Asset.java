package Assets;

public abstract class Asset {
	float xCor,	yCor;
	int hitPoints;
	float radius;
	boolean idle;
	
	public Asset(float x, float y,float radius, int hp) {
		xCor = x;
		yCor = y;
		idle=true;
		this.radius=radius;
		hitPoints = hp;
	}
	
	public void setCoordinates(float x, float y) {
		xCor = x;
		yCor = y;
	}
	
	public float getX() {
		return xCor;
	}
	
	public float getY() {
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
	
	
}
