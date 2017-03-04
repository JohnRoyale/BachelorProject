package Main;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import Assets.*;

public class Player {
	int playerId;
	public int archerCount,spearmanCount,cavalryCount;
	public int baseX,baseY;
	private CopyOnWriteArrayList<Asset> assets;
	private CopyOnWriteArrayList<Asset> lostAssets;
	
	public Player(int playerId) {
		this.playerId = playerId;
		archerCount = spearmanCount=cavalryCount=0;
		assets = new CopyOnWriteArrayList<Asset>();
		lostAssets = new CopyOnWriteArrayList<Asset>();
		
	}
	
	public CopyOnWriteArrayList<Asset> getAssets(){
		return assets;
	}
	
	public void addAsset(Asset a){
		assets.add(a);
		if(a instanceof Archer){
			archerCount++;
		}else if(a instanceof Spearman){
			spearmanCount++;
		}else if(a instanceof Cavalry){
			cavalryCount++;
		}
	}

}
