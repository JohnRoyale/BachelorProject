package AI;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import Assets.*;
import Main.Model;
import Main.Order;

public class RandomAI implements AI {

	Model model;
	int playerID;
	ConcurrentLinkedQueue<Order> orderQueue;
	List<Character> actions = Arrays.asList('u', 'd', 'l', 'r', 'n');
	List<Character> production = Arrays.asList('n', 's', 'a', 'c');

	public RandomAI(ConcurrentLinkedQueue<Order> orderQueue, Model m, int player) {
		this.playerID = player;
		this.orderQueue = orderQueue;
		model = m;
	}

	public void determineAction(Asset a) {
		Random random = new Random();
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
			action = actions.get(random.nextInt(actions.size()));
		}
		a.setIdle(false);
		orderQueue.add(new Order(a, action));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {

		for (Asset a : model.getPlayerList().get(playerID).getAssets()) {
			this.determineAction(a);
		}

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
