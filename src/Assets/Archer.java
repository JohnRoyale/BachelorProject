package Assets;

public class Archer extends Unit {
	final int buildTime = 5;
	
	public Archer(int owner, double x, double y, int mapSize) {
		super(owner, x, y, 4, 3, 2, mapSize);		
	}
}
