package AI;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import Assets.*;
import Main.Model;
import Main.Order;

public class randomAI implements AI {

	Model model;
	ConcurrentLinkedQueue<Order> orderQueue;
	List<Character> actions = Arrays.asList('u', 'd', 'l', 'r', 'n');
	List<Character> production = Arrays.asList('n', 's', 'a', 'c');

	public randomAI(ConcurrentLinkedQueue<Order> orderQueue, Model m) {
		this.orderQueue = orderQueue;
		model = m;
	}

	public void determineAction(Asset a, Model model) {
		Random random = new Random();
		char action = 'n';
		if (a.getClass().equals("Building")) {
			Building b = (Building)a;	
			if(b.getProductionTimer() > 0) {
				action = 'b'; //busy producing unit
				b.setProductionTimer(b.getProductionTimer()-1);
			} else {
				action = b.getInProduction(); //signals unit ready for production
				int next = random.nextInt(production.size()); //get new random for choosing a unit
				b.setInProduction(production.get(next)); //set new unit in production
				b.setProductionTimer(next + 3); //sets training time
			}
		} else {
			action = actions.get(random.nextInt(actions.size()));
		}
		a.setIdle(false);
		orderQueue.add(new Order(a, action));
	}

	@Override
	public void run() {
		// Generate actions maybe add timer to prevent overloading the queue
		while (true) {
			//System.out.println("I'm running " + this.toString());
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
