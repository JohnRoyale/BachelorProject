package Assets;

public class Building extends Asset {

	public Building(int owner, double x, double y, int hp, int mapSize) {
		super(owner, x, y, 0.8/mapSize, hp);
	}
}
