package Main;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Observable;

import Assets.*;

public class Model extends Observable {

	final int populationCap = 5;

	Map levelMap;

	private ArrayList<Player> playerList;
	double mapSize;
	boolean gameOver = false;

	public Model(String fileName) {
		try {
			levelMap = new Map(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		playerList = new ArrayList<Player>();
		playerList.add(new Player(0)); // nature
		playerList.add(new Player(1)); // player 1
		playerList.add(new Player(2)); // player 2
		
		mapSize = (double)levelMap.size;
		
		for(int x=0;x<levelMap.size;x++){
			for(int y=0;y<levelMap.size;y++){
				if(levelMap.getTile(y, x)=='1'){
					playerList.get(1).baseX=x;
					playerList.get(1).baseY=y;
				} else if(levelMap.getTile(y, x)=='2'){
					playerList.get(2).baseX=x;
					playerList.get(2).baseY=y;
				}
			}
		}
		
		
		playerList.get(1).addAsset(new Building(1, playerList.get(1).baseX / (float)levelMap.size, playerList.get(1).baseY / (float)levelMap.size, 10, levelMap.size));
		playerList.get(2).addAsset(new Building(2, playerList.get(2).baseX / (float)levelMap.size, playerList.get(2).baseY / (float)levelMap.size, 10, levelMap.size));
	}

	public void order(Unit u, char action) {
		// moves unit to new position
		double movement=u.getSpeed()*0.05/levelMap.size;
		switch (action) {
		case ('u'): {
			if (!notAccessable(u.getX(), u.getY() - movement, u) &&
					!notAccessable(u.getX()+u.getDiameter(), u.getY() - movement, u))
				u.setCoordinates(u.getX(), u.getY() - movement);
			break;
		}
		case ('d'): {
			if (!notAccessable(u.getX(), u.getY() +u.getDiameter() + movement, u) &&
					!notAccessable(u.getX()+u.getDiameter(), u.getY() +u.getDiameter() + movement, u))
				u.setCoordinates(u.getX(), u.getY() + movement);
			break;
		}
		case ('l'): {
			if (!notAccessable(u.getX()  - movement, u.getY(), u)&&
					!notAccessable(u.getX()  - movement, u.getY()+u.getDiameter(), u))
				u.setCoordinates(u.getX() - movement, u.getY());
			break;
		}
		case ('r'): {
			if (!notAccessable(u.getX() +u.getDiameter() + movement, u.getY(), u)&&
					!notAccessable(u.getX() +u.getDiameter()  + movement, u.getY()+u.getDiameter(), u))
				u.setCoordinates(u.getX() + movement, u.getY());
			break;
		}
		// case('n'): System.out.println("none");
		}
		this.checkCollisions(u);
	}

	public void order(Building b, char action) {
		// produce new unit at location of building
		if (playerList.get(b.getOwner()).getAssets().size() < this.populationCap) {
			switch (action) {
			case 's': {
				playerList.get(b.getOwner()).addAsset(new Spearman(b.getOwner(), b.getX(), b.getY(), levelMap.size));
				break;
			}
			case 'c': {
				playerList.get(b.getOwner()).addAsset(new Cavalry(b.getOwner(), b.getX(), b.getY(), levelMap.size));
				break;
			}
			case 'a': {
				playerList.get(b.getOwner()).addAsset(new Archer(b.getOwner(), b.getX(), b.getY(), levelMap.size));
			}
			// case('n'): System.out.println("none");
			// case('b'): System.out.println("busy");
			}
		}
	}

	public boolean notAccessable(double x, double y, Unit u) {
		// out of bounds check
		if ((x >= 1 || x < 0) || (y >= 1 || y < 0)) {
			return true;
		}

		// natural obstacle check
		int tileX = (int) (x * (levelMap.size));
		int tileY = (int) (y * (levelMap.size));
		if (levelMap.getTile(tileX, tileY) == '#')
			return true;

		// other unit check
		// for(Player p: playerList) {
		// for(Asset a: p.assets) {
		// if(x == a.getX() && y == a.getY() && a != u) {
		// return true;
		// }
		// }
		// }
		return false;
	}

	public void checkCollisions(Unit u) {
		for (Player p : playerList) {
			if (p.playerId != u.getOwner()) {
				for (Asset a : p.getAssets()) {
					if (u.collides(a)){
						//System.out.println(u.toString() + " Attacking " + a.toString());
						a.damage(u.getAttackPower());
						if(a.getHitPoints()<=0){
							p.getLostAssets().add(a);
							p.getAssets().remove(a);
						}
					}
				}
			}
		}
	}

	public void updateAll() {
		this.setChanged();
		this.notifyObservers();
	}

	public boolean gameOver() {
		return gameOver;
	}

	public Map getLevelMap() {
		return levelMap;
	}
	
	public double getMapSize() {
		return mapSize;
	}

	public ArrayList<Player> getPlayerList() {
		return this.playerList;
	}
}
