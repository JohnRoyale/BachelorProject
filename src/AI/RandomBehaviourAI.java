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

	Model model;
	int playerID;
	int enemy;
	ConcurrentLinkedQueue<Order> orderQueue;
	Random random = new Random(System.currentTimeMillis());
	ResistancePathFinder p;
	ShortestPathFinder sp;
	List<String> actions = Arrays.asList("defendBase", "defensiveInvade", "evasiveInvade", "hunt");
	List<Character> production = Arrays.asList('n', 's', 'a', 'c');

	public RandomBehaviourAI(ConcurrentLinkedQueue<Order> orderQueue, Model m, int player) {
		this.playerID = player;
		if (playerID == 1) {
			enemy = 2;
		} else {
			enemy = 1;
		}
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
				action = b.getInProduction(); // signals unit ready for
												// production
				int next = random.nextInt(production.size()); // get new random
																// for choosing
																// a unit
				b.setInProduction(production.get(next)); // set new unit in
															// production

				switch (action) {
				case 's': {
					b.setProductionTimer(Spearman.buildtime);
					break;
				}
				case 'a': {
					b.setProductionTimer(Archer.buildtime);
					break;
				}
				case 'c': {
					b.setProductionTimer(Cavalry.buildtime);
					break;
				}
				case 'n': {
					b.setProductionTimer(10);
				}
				}
			}
		} else {

			Unit u = (Unit) a;
			if (u.getState().equals("idle")) {
				u.setState(actions.get(random.nextInt(actions.size())));
			}
			action = u.determineAction(p, sp, model, enemy);
		}
		a.setIdle(false);
		orderQueue.add(new Order(a, action));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {

		// Generate actions maybe add timer to prevent overloading the queue

		for (Asset a : model.getPlayerList().get(playerID).getAssets()) {
			this.determineAction(a);
		}

	}

}
