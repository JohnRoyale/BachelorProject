package AI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
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
import Neural.NeuralNetwork;
import PathFinder.ResistancePathFinder;
import PathFinder.ShortestPathFinder;

public class NeuralNetworkAI implements AI {
	Model model;
	int playerID, enemyID;
	Player enemy, self;
	ConcurrentLinkedQueue<Order> orderQueue;
	Random random = new Random(System.currentTimeMillis());
	ResistancePathFinder p;
	ShortestPathFinder sp;
	List<String> actions = Arrays.asList("defendBase", "defensiveInvade", "evasiveInvade", "hunt");
	List<Character> production = Arrays.asList('n', 's', 'a', 'c');
	NeuralNetwork net;
	int inputs = 13;
	int[] size = { inputs, 100, 50, actions.size() };
	int range = 2;
	double gamma = 0.95;
	double chanceInc = 0.01;
	double[] input;
	double[][] activation;
	double chance;

	public NeuralNetworkAI(ConcurrentLinkedQueue<Order> orderQueue, Model m, int player) {
		playerID = player;
		this.orderQueue = orderQueue;
		model = m;
		p = new ResistancePathFinder(m);
		sp = new ShortestPathFinder(m.getLevelMap());

		if (new File("neuralnet").exists()) {
			try {
				readFromFile();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
		chance = 0.0;
		if (net == null) {
			System.out.println("test");
			net = new NeuralNetwork(size);
		} else if (!net.sameSize(size)) {
			System.out.println("test");
			net = new NeuralNetwork(size);
		}else{
			chance=net.getChance();
		}
		System.out.println(chance);

		input = new double[inputs];
	}

	@Override
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
		} else if (a instanceof Unit) {
			Unit u = (Unit) a;
			if (u.getState().equals("idle")) {
				int best = 0;
				if (random.nextDouble() * 100 <= chance) {
					input[0] = u.getHitPoints();
					if (u.getType() == 's') {
						input[1] = 1;
						input[2] = 0;
						input[3] = 0;
					} else if (u.getType() == 'a') {
						input[1] = 0;
						input[2] = 1;
						input[3] = 0;
					} else {
						input[1] = 0;
						input[2] = 0;
						input[3] = 1;
					}

					input[4] = 0;
					for (int i = -range; i <= range; i++) {
						for (int j = -range; j <= range; j++) {
							input[4] += model.getTileResistance(playerID, (int) (u.getX() * model.getMapSize()) + i,
									(int) (u.getY() * model.getMapSize()) + j, u.getType());
						}
					}

					input[5] = enemy.archerCount;
					input[6] = enemy.cavalryCount;
					input[7] = enemy.spearmanCount;
					input[8] = self.attackers;
					input[9] = self.defenders;
					input[10] = self.hunters;
					Player enemy, owner;
					if (u.getOwner() == 1) {
						enemy = model.getPlayerList().get(2);
						owner = model.getPlayerList().get(1);
					} else {
						enemy = model.getPlayerList().get(1);
						owner = model.getPlayerList().get(2);
					}

					double mapsize = model.getMapSize();
					input[11] = sp.findDistance(u.getX(), u.getY(), owner.baseX / mapsize, owner.baseY / mapsize,
							u.getDiameter());
					input[12] = sp.findDistance(u.getX(), u.getY(), enemy.baseX / mapsize, enemy.baseY / mapsize,
							u.getDiameter());

					activation = net.forwardProp(input);

					double[] output = activation[size.length - 1];

					for (int i = 0; i < output.length; i++) {
						String str = String.format("%1.2f", output[i]) + " ";
						// System.out.print(str);
						if (output[i] > output[best]) {
							best = i;
						}
					}
				} else {
					best = random.nextInt(actions.size());
				}
				String s = actions.get(best);
				// System.out.println(s);
				u.addState(input, best);
				u.setState(s);
			}
			action = u.determineAction(p, sp, model, enemyID);

		}

		a.setIdle(false);
		orderQueue.add(new Order(a, action));

	}

	public void incChance() {
		chance += chanceInc;
		chance = Math.min(99, chance);
	}

	@Override
	public void run() {
		self = model.getPlayerList().get(playerID);
		if (playerID == 1) {
			enemyID = 2;
			enemy = model.getPlayerList().get(2);
		} else {
			enemyID = 1;
			enemy = model.getPlayerList().get(1);
		}

		for (Iterator<Asset> iter = self.getAssets().iterator(); iter.hasNext();) {
			Asset a = iter.next();
			this.determineAction(a);
		}

	}

	public void learn() {
		for (Asset a : self.getAssets()) {
			if (a instanceof Unit) {
				Unit u = (Unit) a;
				Collections.reverse(u.getHistory());
				double lastReward = 0;
				for (State s : u.getHistory()) {
					double[][] activation = net.forwardProp(s.input);
					double[] expectedOutput = activation[size.length - 1];
					expectedOutput[s.output] = s.reward + gamma * lastReward;
					lastReward = expectedOutput[s.output];
					net.backProp(activation, expectedOutput);
				}
			}
		}
		for (Asset a : self.getLostAssets()) {
			if (a instanceof Unit) {
				Unit u = (Unit) a;
				Collections.reverse(u.getHistory());
				double lastReward = 0;
				for (State s : u.getHistory()) {
					double[][] activation = net.forwardProp(s.input);
					double[] expectedOutput = activation[size.length - 1];
					expectedOutput[s.output] = s.reward + gamma * lastReward;
					lastReward = expectedOutput[s.output];
					net.backProp(activation, expectedOutput);
				}
			}
		}
	}

	public void writeToFile() throws IOException {
		net.setChance(chance);
		File f = new File("neuralnet");
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
		oos.writeObject(net);
		oos.flush();
		oos.close();
	}

	public void readFromFile() throws IOException, ClassNotFoundException {
		File f = new File("neuralnet");
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
		Object obj = ois.readObject();
		net=(NeuralNetwork) obj;
		ois.close();
	}

}
