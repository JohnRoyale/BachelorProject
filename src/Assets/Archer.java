package Assets;

public class Archer extends Unit {
	public static final int buildtime = 40;
	
	public Archer(int owner, double x, double y, int mapSize) {
		super(owner, x, y, 6, 3, 3, 3, 1.2/mapSize,mapSize);
		type = 'a';
		counter = 's';
	}
	
	public String toString() {
		return "Archer" + owner;
	}
}
