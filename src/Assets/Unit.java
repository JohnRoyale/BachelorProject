package Assets;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import Main.Model;
import Main.Player;
import PathFinder.*;

public abstract class Unit extends Asset {
	int attackPower;
	double speed;
	String state;
	char type;
	char counter;
	int turnCount;
	double attackRange;
	double offsetX=1,offsetY=1;
	Random random;
	Player p;
	
	final List<Character> actions = Arrays.asList('u', 'd', 'l', 'r', 'n');
	
	public Unit(int owner, double x, double y, int hp, int ap, double d, double attackRange, int mapSize) {
		super(owner, x, y, 0.6 / mapSize, hp);
		attackPower = ap;
		this.attackRange = attackRange;
		this.speed = d;
		state = "idle";
		turnCount=0;
		random=new Random(System.currentTimeMillis());
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

	public char determineAction(ResistancePathFinder rp, ShortestPathFinder sp, Model m, int enemy) {
		char a = 'n';
		Player e = m.getPlayerList().get(enemy);
		p = m.getPlayerList().get(this.owner);
		double mapSize = m.getMapSize();
		//System.out.print(state +" ");
		
	
		while(p.baseX/(double)mapSize+offsetX<0 || p.baseX/(double)mapSize+offsetX>1){
			offsetX=random.nextDouble()*6/mapSize-1.0*3/mapSize;
		}
		while(p.baseY/(double)mapSize+offsetY<0 || p.baseY/(double)mapSize+offsetY>1){
			offsetY=random.nextDouble()*6/mapSize-1.0*3/mapSize;
		}
		
		switch (state) {
		// Patrol around base
		case "defendBase":
			if (Math.abs(this.xCor * mapSize- p.baseX)+Math.abs(this.yCor * mapSize - p.baseY) > 5) {
				//System.out.println("walk back ");
				a = rp.findPath(this.xCor, this.yCor, p.baseX / mapSize, p.baseY / mapSize, this.getDiameter(),
						this.owner, false);
			} else {
				double minDist=3;
				Asset target=null;
				for (Asset enemyAsset : e.getAssets()) {
					double dist=Math.abs(enemyAsset.getX()-this.xCor)+Math.abs(enemyAsset.getY()-this.yCor);
					if(dist<minDist){
						minDist=dist;
						target=enemyAsset;
					}
				}
				if(target != null && minDist*m.getMapSize()<5){
					//System.out.println("Enemy Close");
					a = sp.findPath(this.xCor, this.yCor, target.getX(), target.getY(), this.getDiameter());
					turnCount=0;
				}else{
					//System.out.println("patrolling");
					a = sp.findPath(this.xCor, this.yCor, p.baseX / mapSize+offsetX, p.baseY / mapSize+offsetY, this.getDiameter());
					
					//a=actions.get(random.nextInt(actions.size()));
					if(turnCount++ > 1000){
						state="idle";
					}
				}
			}

			// Move randomly around base, if enemy comes in range (within x
			// tiles) target enemy
			break;
		// Attack enemy through route with most resistance
		case "defensiveInvade":
			a = rp.findPath(this.xCor, this.yCor, e.baseX/m.getMapSize(), e.baseY/m.getMapSize(), this.getDiameter(),
					this.owner, false);
			// Find a short path with most resistance
			break;
		// Attack enemy through route with least resistance
		case "evasiveInvade":
			a = rp.findPath(this.xCor, this.yCor, e.baseX/m.getMapSize(), e.baseY/m.getMapSize(), this.getDiameter(),
					this.owner, true);
			// Find a short path with least resistance to enemy
			break;
		// Attack closest enemy unit, maybe make it only attack its preferred
		// enemy.
		case "hunt":
			// find closest enemy
			// find action for shortest path
			
			double minDist=5;
			Asset target=null;
			for (Asset enemyAsset : e.getAssets()) {
				double dist=Math.abs(enemyAsset.getX()-this.xCor)+Math.abs(enemyAsset.getY()-this.yCor);
				if(dist<minDist){
					minDist=dist;
					target=enemyAsset;
				}
			}
			if(target != null){
				a = rp.findPath(this.xCor, this.yCor, target.getX(), target.getY(), this.getDiameter(),
						this.owner, true);
				turnCount=0;
			}else{
				state="idle";
			}
			
			break;
		}

		return a;
	}

}
