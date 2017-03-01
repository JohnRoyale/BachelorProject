package Assets;

public class Spearman extends Unit{
	final int buildTime = 4;
	
	public Spearman(int owner, double x, double y,int mapSize) {
		super(owner, x, y, 5, 2, 2,mapSize);
	}
}
