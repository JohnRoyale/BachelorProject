package Assets;

public class Cavalry extends Unit{
	public static final int buildtime = 60;
	
	public Cavalry(int owner, double x, double y,int mapSize) {
		super(owner, x, y, 12, 3, 4,0.6/mapSize,mapSize);
		type = 'c';
		counter = 'a';
	}
	
	public String toString() {
		return "Cavalry" + owner;
	}
}
