package AI;

import java.util.Arrays;
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
import PathFinder.ResistancePathFinder;
import PathFinder.ShortestPathFinder;

public class ProbabilityBehaviourAI implements AI {

		Model model;
		int playerID;
		int enemy;
		ConcurrentLinkedQueue<Order> orderQueue;
		Random random = new Random(System.currentTimeMillis());
		ResistancePathFinder p;
		ShortestPathFinder sp;
		List<String> actions = Arrays.asList("defendBase", "defensiveInvade", "evasiveInvade", "hunt");
		List<Character> production = Arrays.asList('n', 's', 'a', 'c');

		public ProbabilityBehaviourAI(ConcurrentLinkedQueue<Order> orderQueue, Model m, int player) {
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
					Player enemy, owner;
					owner = model.getPlayerList().get(playerID);
					if(playerID == 2) {
						enemy = model.getPlayerList().get(1);
					} else {
						enemy = model.getPlayerList().get(2);
					}
					
					int randomFactor = (int)random.nextDouble() * 100;
					boolean baseDistance = sp.findDistance(u.getX(), u.getY(), owner.baseX / model.getMapSize(), owner.baseY / model.getMapSize(),u.getDiameter()) > 
										   sp.findDistance(u.getX(), u.getY(), enemy.baseX / model.getMapSize(), enemy.baseY / model.getMapSize(),u.getDiameter());
								//0 db, 1di, 2ei, 3h
					switch(u.getType()) {
					case 's': {
						//defensebase (10%) and defensive invade (70%) and hunt (20%)
						if(randomFactor > 90) {
							u.setState(actions.get(0)); 
						} else if(randomFactor > 70) {
							u.setState(actions.get(3));
						} else {
							u.setState(actions.get(1));
						}
						
					}
					case 'c': {
						//hunt(30%) + evasive invade (10%) + defensive invade (70%)
						if(randomFactor > 90) {
							u.setState(actions.get(2));
						} else if(randomFactor > 70) {
							u.setState(actions.get(3));
						} else {
							u.setState(actions.get(1));
						}
					}
					case 'a': {
						//hunt(40%) + defendbase(10%) + defensiveinvade(50%) (
						if(randomFactor > 90) {
							u.setState(actions.get(0));
						} else if(randomFactor > 50) {
							u.setState(actions.get(3));
						} else {
							u.setState(actions.get(1));
						}
					}
					}
					
					
					
					/* Add behaviour logic */
					//If defenders < x defend base && enemy units< Y
					//else if (enemy units> x) hunt
					//Else invade base
					
					
					
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

		@Override
		public void reset() {
			// TODO Auto-generated method stub
			
		}
}

