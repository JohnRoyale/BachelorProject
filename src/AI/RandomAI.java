package AI;

import java.util.ArrayList;
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
		this.playerID=player;
		this.orderQueue = orderQueue;
		model = m;
	}

	public void determineAction(Asset a){
		Random random = new Random();
		char action = 'n';
		if (a instanceof Building) {
			Building b = (Building)a;	
			if(b.getProductionTimer() > 0) {
				action = 'b'; //busy producing unit
				b.tic();
			} else {
				action = b.getInProduction(); //signals unit ready for production
				int next = random.nextInt(production.size()); //get new random for choosing a unit
				b.setInProduction(production.get(next)); //set new unit in production
				
				switch(action){
					case 's':{
						b.setProductionTimer(Spearman.buildtime);
						break;
					}
					case 'a':{
						b.setProductionTimer(Archer.buildtime);
						break;
					}
					case 'c':{
						b.setProductionTimer(Cavalry.buildtime);
						break;
					}
					case 'n':{
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
		
		long ctime;
		// Generate actions maybe add timer to prevent overloading the queue
		while (true) {
			ctime = System.currentTimeMillis();
			for (Asset a : model.getPlayerList().get(playerID).getAssets()) {
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
