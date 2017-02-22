package Assets;

public abstract class Unit extends Asset {
	int attackPower;
	float speed;
	
	
	public Unit(float x, float y, int hp, int ap, float speed) {
		super(x, y, hp);
		attackPower = ap;
		this.speed = speed;
	}

	
	
}
