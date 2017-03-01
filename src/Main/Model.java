package Main;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Observable;

import Assets.*;
public class Model extends Observable  {
	
	final int populationCap=11;
	
	Map levelMap;

	private ArrayList<Player> playerList; 
	
	boolean gameOver=false;

	public Model(String fileName) {
		try {
			levelMap = new Map(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		playerList = new ArrayList<Player>();
		playerList.add(new Player(0)); //nature
		playerList.add(new Player(1)); //player 1
		playerList.add(new Player(2)); //player 2	
		playerList.get(1).assets.add(new Building(1, 2.0/16, 15.0/16,10,levelMap.size));
		playerList.get(2).assets.add(new Building(2, 15.0/16, 2.0/16,10,levelMap.size));
	}

	public void order(Unit u, char action) {
		// moves unit to new position
		switch(action) {
			case('u'): if(!notAccessable(u.getX(), u.getY()-0.1/levelMap.size, u)) {u.setCoordinates(u.getX(), u.getY()-0.1/levelMap.size);}
			case('d'): if(!notAccessable(u.getX(), u.getY()+0.1/levelMap.size, u)) {u.setCoordinates(u.getX(), u.getY()+0.1/levelMap.size);}
			case('l'): if(!notAccessable(u.getX()-0.1/levelMap.size, u.getY(), u)) {u.setCoordinates(u.getX()-0.1/levelMap.size, u.getY());}
			case('r'): if(!notAccessable(u.getX()+0.1/levelMap.size, u.getY(), u)) {u.setCoordinates(u.getX()+0.1/levelMap.size, u.getY());}
			case('n'): System.out.println("none");
		}
		this.checkCollisions(u);
	}
	
	public void order(Building b, char action){
		// produce new unit at location of building
		switch(action){
			case('s'): playerList.get(b.getOwner()).assets.add(new Spearman(b.getOwner(), b.getX(), b.getY(),levelMap.size));
			case('c'): playerList.get(b.getOwner()).assets.add(new Cavalry(b.getOwner(), b.getX(), b.getY(),levelMap.size));
			case('a'): playerList.get(b.getOwner()).assets.add(new Archer(b.getOwner(), b.getX(), b.getY(),levelMap.size));
			case('n'): System.out.println("none");
			case('b'): System.out.println("busy");
		}
	}
	
	public boolean notAccessable(double x, double y, Unit u) {
		//out of bounds check
		if((x >= levelMap.size || x <= 0 ) || (y >= levelMap.size || y <= 0)) {
			return false;
		}
		
		//natural obstacle check
//		for() { 
//		if()
//		}
		
		//other unit check
		for(Player p: playerList) {
			for(Asset a: p.assets) {
				if(x == a.getX() && y == a.getY() && a != u) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void checkCollisions(Unit u) {
		for(Player p: playerList) {
			if(p.playerId==u.getOwner()) {
				continue;
			} else {
				for(Asset a: p.assets) {
				u.collides(a);
				a.damage(u.getAttackPower());
				}
			}
		}
	}
	
	public void updateAll(){
		this.setChanged();
		this.notifyObservers();
	}
	
	
	public boolean gameOver(){
		return gameOver;
	}

	public Map getLevelMap() {
		return levelMap;
	}
	
	public ArrayList<Player> getPlayerList() {
		return this.playerList;
	}
}
