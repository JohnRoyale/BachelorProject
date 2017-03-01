package Assets;

public class Cavalry extends Unit{
	final int buildTime = 6;
	
	public Cavalry(int owner, double x, double y,int mapSize) {
		super(owner, x, y, 6, 4, 4,mapSize);
	}
}
