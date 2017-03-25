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
import PathFinder.*;

public class ClassicAI implements AI {

	Model model;
	int playerID;
	Player enemy, self;
	ConcurrentLinkedQueue<Order> orderQueue;
	ResistancePathFinder p;
	List<Character> actions = Arrays.asList('u', 'd', 'l', 'r', 'n');
	List<Character> production = Arrays.asList('n', 's', 'a', 'c');

	public ClassicAI(ConcurrentLinkedQueue<Order> orderQueue, Model m, int player) {
		playerID = player;
		this.orderQueue = orderQueue;
		model = m;
		// p=new ShortestPathFinder();
		p = new ResistancePathFinder();
	}

	@Override
	public void determineAction(Asset a) {
		// Random random = new Random();
		char action = 'n';

		if (a instanceof Building) {
			Building b = (Building) a;
			if (b.getProductionTimer() > 0) {
				action = 'b'; // busy producing unit
				b.tic();
			} else {
				action = b.getInProduction();

				if (self.archerCount < enemy.spearmanCount) {
					b.setInProduction('a');
					b.setProductionTimer(Archer.buildtime);
				} else if (self.spearmanCount < enemy.cavalryCount) {
					b.setInProduction('s');
					b.setProductionTimer(Spearman.buildtime);
				} else if (self.cavalryCount < enemy.archerCount) {
					b.setInProduction('c');
					b.setProductionTimer(Cavalry.buildtime);
				} else {
					b.setInProduction('c');
					b.setProductionTimer(Cavalry.buildtime);
				}

			}
		} else if (a instanceof Unit) {
			Unit b = (Unit) a;

			// initial target
			double targetX = enemy.baseX / model.getMapSize();
			double targetY = enemy.baseY / model.getMapSize();

			// if close enemy assets that can be countered by unit
			double distance = 3.0 / model.getMapSize();
			int cnt = 0;
			for (Asset enemyAsset : enemy.getAssets()) {
				if (enemyAsset instanceof Unit) {
					Unit e = (Unit) enemyAsset;
					double interceptDistance = Math.abs(e.getX() - a.getX()) + Math.abs(e.getY() - a.getY()); 
					if (distance >= interceptDistance ) {
						cnt++;
						if (cnt == 1) {
							// first enemy that can be countered -> new target
							// location
							targetX = e.getX();
							targetY = e.getY();
							action = 'n';
							// System.out.println("New target");
						}
					}
				}
			}
			
			if (b.getType()=='c' && enemy.archerCount==0) {
				// if on same square move directly towards target choose path with least enemies
				action = p.findPath(a.getX(), a.getY(), targetX, targetY, a.getDiameter(), model.getLevelMap(), model,
						playerID, true);
			} else {
				// if on same square move directly towards target choose path with most enemies
				action = p.findPath(a.getX(), a.getY(), targetX, targetY, a.getDiameter(), model.getLevelMap(), model,
						playerID, false);
			}

		}
		a.setIdle(false);
		orderQueue.add(new Order(a, action));

	}

	@Override
	public void run() {
		self = model.getPlayerList().get(playerID);
		if (playerID == 1) {
			enemy = model.getPlayerList().get(2);
		} else {
			enemy = model.getPlayerList().get(1);
		}
		long ctime;
		// Generate actions maybe add timer to prevent overloading the queue
		while (true) {
			ctime = System.currentTimeMillis();

			for (Iterator<Asset> iter = self.getAssets().iterator(); iter.hasNext();) {
				Asset a = iter.next();
				this.determineAction(a);
			}

			try {
				Thread.sleep(Math.max(20 - (System.currentTimeMillis() - ctime), 0));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
