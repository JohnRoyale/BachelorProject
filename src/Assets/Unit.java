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
	
	public char determineAction() {
		char a = 'n';

		switch (state) {
		//Patrol around base
		case "defendBase":
			//Move randomly around base, if enemy comes in range (within x tiles) target enemy
			break;
		//Attack enemy through route with most resistance
		case "defensiveInvade":
			//Find a short path with most resistance
			break;
		//Attack enemy through route with least resistance
		case "evasiveInvade":
			//Find a short path with least resistance to enemy
			break;
		//Attack closest enemy unit, maybe make it only attack its preferred enemy.
		case "hunt":
			//find closest enemy
			//find action for shortest path
			
			break;
		}

		return a;
	}

}
