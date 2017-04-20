package Assets;

public class Spearman extends Unit{
	public static final int buildtime = 30;
	
	public Spearman(int owner, double x, double y,int mapSize) {
		super(owner, x, y, 10, 2, 2, 2, 0.6/mapSize,mapSize);
		type = 's';
		counter = 'c';
	}
	
	public String toString() {
		return "Spearman" + owner;
	}
}
