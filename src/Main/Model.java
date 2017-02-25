package Main;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Observable;

import Assets.*;
public class Model extends Observable  {
	
	final int populationCap=11;
	
	Map levelMap;

	ArrayList<Asset> player1;
	ArrayList<Asset> lostPlayer1;
	ArrayList<Asset> player2;
	ArrayList<Asset> lostPlayer2;
	
	boolean gameOver=false;

	public Model(String fileName) {
		try {
			levelMap = new Map(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		player1 = new ArrayList<Asset>();
		lostPlayer1 = new ArrayList<Asset>();
		player2 = new ArrayList<Asset>();
		lostPlayer2 = new ArrayList<Asset>();
		player1.add(new Building(15.0/16,2.0/16,10));
		player1.add(new Building(2.0/16,15.0/16,10));
		player2.add(new Archer(14.5/16,3.5/16));
		player2.add(new Spearman(12.25/16,4.16/16));
		player2.add(new Cavalry(13.875/16,5.43/16));
	}

	public void order(Unit u, char action) {
		// moves unit to new position
	}
	
	public void order(Building b, char action){
		// produce new unit at location of building
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

	public ArrayList<Asset> getPlayer1() {
		return player1;
	}

	public ArrayList<Asset> getPlayer2() {
		return player2;
	}
}
