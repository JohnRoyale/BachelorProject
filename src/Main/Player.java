package Main;

import java.util.ArrayList;

import Assets.Asset;

public class Player {
	int playerId;
	ArrayList<Asset> assets;
	ArrayList<Asset> lostAssets;
	
	public Player(int playerId) {
		this.playerId = playerId;
		assets = new ArrayList<Asset>();
		lostAssets = new ArrayList<Asset>();
		
	}
	
	

}
