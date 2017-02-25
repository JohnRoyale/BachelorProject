package Assets;

public abstract class Unit extends Asset {
	int attackPower;
	float speed;
	
	
	public Unit(double x, double y, int hp, int ap, float speed) {
		super(x, y, 0.6, hp);
		attackPower = ap;
		this.speed = speed;
	}

	
	
}
