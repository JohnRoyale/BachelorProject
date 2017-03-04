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
import Main.Model;
import Main.Order;
import Main.Player;
import PathFinder.PathFinder;

public class ClassicAI implements AI {
	
	Model model;
	int playerID;
	Player enemy,self;
	ConcurrentLinkedQueue<Order> orderQueue;
	PathFinder p;
	List<Character> actions = Arrays.asList('u', 'd', 'l', 'r', 'n');
	List<Character> production = Arrays.asList('n', 's', 'a', 'c');

	public ClassicAI(ConcurrentLinkedQueue<Order> orderQueue, Model m, int player) {
		playerID=player;
		this.orderQueue = orderQueue;
		model = m;
		p=new PathFinder();
	}
	
	

	@Override
	public void determineAction(Asset a) {
		Random random = new Random();
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
					b.setInProduction('c');
					b.setProductionTimer(Cavalry.buildtime);
				}
				
			}
		} else {
			int x = (int) (a.getX() * (model.getLevelMap().size));
			int y = (int) (a.getY() * (model.getLevelMap().size));
			action = p.findPath(x, y, enemy.baseX, enemy.baseY, model.getLevelMap());
			
			//if on same square move directly towards goal
			if(action == 'n'){
				if(Math.abs(a.getX()-enemy.baseX/16.0)>Math.abs(a.getX()-enemy.baseY/16.0)){
					if(a.getX()-enemy.baseX/16.0<0){
						action='r';
					}else{
						action='l';
					}
				}else{
					if(a.getY()-enemy.baseY/16.0<0){
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
