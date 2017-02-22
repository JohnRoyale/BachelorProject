package Assets;

public abstract class Asset {
	float xCor,	yCor;
	int hitPoints;
	
	public Asset(float x, float y, int hp) {
		xCor = x;
		yCor = y;
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
}
