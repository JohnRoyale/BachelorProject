package Assets;

public abstract class Unit extends Asset {
	int attackPower;
	double speed;
	String state;
	char type;
	char counter;
	double attackRange;

	public Unit(int owner, double x, double y, int hp, int ap, double d, double attackRange, int mapSize) {
		super(owner, x, y, 0.6 / mapSize, hp);
		attackPower = ap;
		this.attackRange = attackRange;
		this.speed = d;
	}

	public int getAttackPower() {
		return attackPower;
	}

	public char getType() {
		return type;
	}

	public char getCounter() {
		return counter;
	}

	public boolean collides(Asset other) {
		double distX = this.xCor - other.getX();
		double distY = this.yCor - other.getY();
		double distance = Math.sqrt(distX * distX + distY * distY);

		return distance < ((this.attackRange / 2) + (other.getDiameter() / 2));
	}

	public double getSpeed() {
		return speed;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
