package Assets;

public abstract class Unit extends Asset {
	int attackPower;
	float speed;
	
	
	public Unit(int owner, double x, double y, int hp, int ap, float speed, int mapSize) {
		super(owner, x, y, 0.6/mapSize, hp);
		attackPower = ap;
		this.speed = speed;
	}


	public int getAttackPower() {
		return attackPower;
	}

	
	
}
