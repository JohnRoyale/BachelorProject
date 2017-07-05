package AI;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import Assets.*;
import Main.Model;
import Main.Order;
import Main.Player;
import PathFinder.*;

public class RandomBehaviourAI implements AI {
	boolean inter =false;
	Model model;
	int playerID;
	int enemyID;
	Player enemy, self;
	ConcurrentLinkedQueue<Order> orderQueue;
	Random random = new Random(System.currentTimeMillis());
	ResistancePathFinder p;
	ShortestPathFinder sp;
	List<String> actions = Arrays.asList("defendBase", "defensiveInvade", "evasiveInvade", "hunt");
	List<Character> production = Arrays.asList('n', 's', 'a', 'c');

	public RandomBehaviourAI(ConcurrentLinkedQueue<Order> orderQueue, Model m, int player) {
		this.playerID = player;
		
		this.orderQueue = orderQueue;
		p = new ResistancePathFinder(m);
		sp = new ShortestPathFinder(m.getLevelMap());
		model = m;
	}

	public void determineAction(Asset a) {
		
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
		} else {

			Unit u = (Unit) a;
			if (u.getState().equals("idle")) {
				u.setState(actions.get(random.nextInt(actions.size())));
			}
			action = u.determineAction(p, sp, model, enemyID);
		}
		a.setIdle(false);
		orderQueue.add(new Order(a, action));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {

		inter=false;self = model.getPlayerList().get(playerID);
		if (playerID == 1) {
			enemyID = 2;
			enemy = model.getPlayerList().get(2);
		} else {
			enemyID = 1;
			enemy = model.getPlayerList().get(1);
		}
		// Generate actions maybe add timer to prevent overloading the queue

		for (Asset a : model.getPlayerList().get(playerID).getAssets()) {

			if (inter) {
				inter=false;
				break;
			}
			this.determineAction(a);
		}

	}

	@Override
	public void reset() {
		inter=false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interrupt() {
		inter=true;
		// TODO Auto-generated method stub
		
	}

}
