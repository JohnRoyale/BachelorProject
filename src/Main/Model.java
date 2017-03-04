package Main;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Observable;

import Assets.*;

public class Model extends Observable {

	final int populationCap = 11;

	Map levelMap;

	private ArrayList<Player> playerList;

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
		
		
		playerList.get(1).addAsset(new Building(1, playerList.get(1).baseX / 16.0, playerList.get(1).baseY / 16.0, 10, levelMap.size));
		playerList.get(2).addAsset(new Building(2, playerList.get(2).baseX / 16.0, playerList.get(2).baseY / 16.0, 10, levelMap.size));
	}

	public void order(Unit u, char action) {
		// moves unit to new position
		switch (action) {
		case ('u'): {
			if (!notAccessable(u.getX(), u.getY() - 0.1 / levelMap.size, u))
				u.setCoordinates(u.getX(), u.getY() - 0.05 / levelMap.size);
			break;
		}
		case ('d'): {
			if (!notAccessable(u.getX(), u.getY() + 0.1 / levelMap.size, u))
				u.setCoordinates(u.getX(), u.getY() + 0.05 / levelMap.size);
			break;
		}
		case ('l'): {
			if (!notAccessable(u.getX() - 0.1 / levelMap.size, u.getY(), u))
				u.setCoordinates(u.getX() - 0.05 / levelMap.size, u.getY());
			break;
		}
		case ('r'): {
			if (!notAccessable(u.getX() + 0.1 / levelMap.size, u.getY(), u))
				u.setCoordinates(u.getX() + 0.05 / levelMap.size, u.getY());
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
			if (p.playerId == u.getOwner()) {
				continue;
			} else {
				for (Asset a : p.getAssets()) {
					if (u.collides(a))
						a.damage(u.getAttackPower());
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

	public ArrayList<Player> getPlayerList() {
		return this.playerList;
	}
}
