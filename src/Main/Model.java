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
