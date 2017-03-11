package AI;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import Assets.Archer;
import Assets.Asset;
import Assets.Building;
import Assets.Cavalry;
import Assets.Spearman;
import Assets.Unit;
import Main.Model;
import Main.Order;
import Main.Player;
import PathFinder.PathFinder;

public class ClassicAI implements AI {
	
	Model model;
	int playerID;
	Player enemy,self;
	ConcurrentLinkedQueue<Order> orderQueue;
	PathFinder p, t;
	List<Character> actions = Arrays.asList('u', 'd', 'l', 'r', 'n');
	List<Character> production = Arrays.asList('n', 's', 'a', 'c');

	public ClassicAI(ConcurrentLinkedQueue<Order> orderQueue, Model m, int player) {
		playerID=player;
		this.orderQueue = orderQueue;
		model = m;
		p=new PathFinder();
		t=new PathFinder();
	}
	

	@Override
	public void determineAction(Asset a) {
		//Random random = new Random();
		char action = 'n';
		
		
		if (a instanceof Building) {
			Building b = (Building)a;	
			if(b.getProductionTimer() > 0) {
				action = 'b'; //busy producing unit
				b.tic();
			} else {
				action = b.getInProduction();

				if(self.archerCount<enemy.spearmanCount){
					b.setInProduction('a');
					b.setProductionTimer(Archer.buildtime);
				} else if(self.spearmanCount<enemy.cavalryCount){
					b.setInProduction('s');
					b.setProductionTimer(Spearman.buildtime);
				} else if(self.cavalryCount<enemy.archerCount){
					b.setInProduction('c');
					b.setProductionTimer(Cavalry.buildtime);
				} else {
					b.setInProduction('s');
					b.setProductionTimer(Spearman.buildtime);
				}
				
			}
		} else if(a instanceof Unit){
			Unit b = (Unit)a;
			int x = (int) (a.getX() * (model.getLevelMap().size));
			int y = (int) (a.getY() * (model.getLevelMap().size));
			
			//unit position
			double unitX = a.getX()/model.getMapSize();
			double unitY = a.getY()/model.getMapSize();
			
//			System.out.println("x="+ x + " y=" + y);
//			System.out.println("x double=" + a.getX() + " y double=" + a.getY());
//			System.out.println("x double=" + unitX + " y double=" + unitY);
			
			//initial target
			double targetX = enemy.baseX/model.getMapSize();
			double targetY = enemy.baseY/model.getMapSize();
			action = p.findPath(a.getX(), a.getY(), enemy.baseX/model.getMapSize(), enemy.baseY/model.getMapSize(),a.getDiameter(), model.getLevelMap());
			
			//if close enemy assets that can be countered by unit
			double distance = 2.0/model.getMapSize();
			int cnt = 0;
			for(Asset enemyAsset: enemy.getAssets()) {
				if(enemyAsset instanceof Unit) {
					Unit e = (Unit)enemyAsset;	
					double interceptDistance = Math.abs(e.getX()-a.getX())+Math.abs(e.getY()-a.getY()); //check if in range
					if(distance >= interceptDistance && e.getType() == b.getCounter()) {				
						cnt++;
						if(cnt == 1) { 
							//first enemy that can be countered -> new target location 
							targetX = e.getX()/model.getMapSize();
							targetY = e.getY()/model.getMapSize();
							action = 'n';
							//System.out.println("New target");
						}
					}
				}
			}
			
			//if on same square move directly towards target
			if(action == 'n'){
				if(Math.abs(unitX-targetX)>Math.abs(unitY-targetY)){
					//System.out.println(unitX-targetX);
					if(unitX-targetX<0){
						action='r';
					}else{
						action='l';
					}
				}else{
					if(unitY-targetY<0){
						action='d';
					}else{
						action='u';
					}
				}
				
			}
			
		}
		a.setIdle(false);
		orderQueue.add(new Order(a, action));
		
	}
	
	@Override
	public void run() {
		self=model.getPlayerList().get(playerID);
		if(playerID == 1){
			enemy=model.getPlayerList().get(2);
		}else{
			enemy=model.getPlayerList().get(1);
		}
		long ctime;
		// Generate actions maybe add timer to prevent overloading the queue
		while (true) {
			ctime = System.currentTimeMillis();
			
			for (Iterator<Asset> iter = self.getAssets().iterator(); iter.hasNext(); ) {
				Asset a  = iter.next();
				this.determineAction(a);
			}
			
			try {
				Thread.sleep(Math.max(20-(System.currentTimeMillis()-ctime),0));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}


}
