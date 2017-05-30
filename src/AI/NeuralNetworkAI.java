package AI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
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
	int inputs = 14;
	int[] size = { inputs, 15, 8, actions.size() };
	int range = 2;
	double gamma = 0.95;
	double chanceInc = 0.08;
	double chanceMax = 98.0;
	double chanceStart = 90.0;
	double[] input;
	double[][] activation;
	double chance;
	boolean qlearning;
	boolean enabled;
	File dir;

	public NeuralNetworkAI(ConcurrentLinkedQueue<Order> orderQueue, Model m, int player, boolean q, boolean en,
			String file) {
		qlearning = q;
		enabled = en;
		playerID = player;
		this.orderQueue = orderQueue;
		model = m;
		p = new ResistancePathFinder(m);
		sp = new ShortestPathFinder(m.getLevelMap());

		dir = new File("NeuralNetworks");
		dir.mkdirs();

		if (new File(dir, file).exists()) {
			try {
				readFromFile(file, true);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
		if (net == null) {
			System.out.println("test1");
			net = new NeuralNetwork(size, file);
			chance = chanceStart;
			try {
				this.writeToFile(0,0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (!net.sameSize(size)) {
			System.out.println("test2");
			net = new NeuralNetwork(size, file);
			chance = chanceStart;
			try {
				this.writeToFile(0,0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			chance = net.getChance();
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

					double mapsize = model.getMapSize();
					input[11] = sp.findDistance(u.getX(), u.getY(), self.baseX / mapsize, self.baseY / mapsize,
							u.getDiameter());
					input[12] = sp.findDistance(u.getX(), u.getY(), enemy.baseX / mapsize, enemy.baseY / mapsize,
							u.getDiameter());

					input[13] = Double.MAX_VALUE;

					for (Asset as : enemy.getAssets()) {
						input[13] = Math.min(input[13], sp.findDistance(as.getX(), as.getY(), self.baseX / mapsize,
								self.baseY / mapsize, as.getDiameter()));
					}

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
		chance = Math.min(chanceMax, chance);
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

	public void qlearn() {
		ArrayList<Asset> assets = new ArrayList<Asset>();
		assets.addAll(self.getAssets());
		assets.addAll(self.getLostAssets());

		for (Asset a : assets) {
			if (a instanceof Unit) {
				Unit u = (Unit) a;
				if (u.getHistory().size() == 0)
					continue;
				State s = u.getHistory().get(0);
				State next = null;

				double[][] activation;
				double[] expectedOutput;
				double[] nextExpected;

				double max;

				for (int i = 0; i < u.getHistory().size(); i++) {
					if (i + 1 < u.getHistory().size()) {
						next = u.getHistory().get(i);
						activation = net.forwardProp(next.input);
						nextExpected = activation[size.length - 1].clone();
						max = Double.NEGATIVE_INFINITY;
						for (int j = 0; j < nextExpected.length; j++) {
							String str = String.format("%1.2f", nextExpected[j]) + " ";
							// System.out.print(nextExpected[j]);
							if (nextExpected[j] > max) {
								max = nextExpected[j];
							}
						}
					} else {
						max = 0;
					}
					activation = net.forwardProp(s.input);
					expectedOutput = activation[size.length - 1].clone();
					expectedOutput[s.output] = s.reward + gamma * max;

					// System.out.println(expectedOutput[s.output]+"
					// "+activation[size.length-1][s.output]);
					// System.out.println(max);
					if (!(max > Double.NEGATIVE_INFINITY)) {
						System.err.println("Max value equal to -inf error " + max);
					}

					net.backProp(activation, expectedOutput);

					if (i + 1 < u.getHistory().size()) {
						s = next;
					}
				}
			}
		}
	}

	public void alternativeLearn() {
		ArrayList<Asset> assets = new ArrayList<Asset>();
		assets.addAll(self.getAssets());
		assets.addAll(self.getLostAssets());

		for (Asset a : assets) {
			if (a instanceof Unit) {
				Unit u = (Unit) a;
				Collections.reverse(u.getHistory());
				double lastReward = 0;
				for (State s : u.getHistory()) {
					double[][] activation = net.forwardProp(s.input);
					double[] expectedOutput = activation[size.length - 1].clone();
					expectedOutput[s.output] = s.reward + gamma * lastReward;

					// for(int i=0;i<expectedOutput.length;i++){
					// System.out.println(expectedOutput[s.output]+"
					// "+activation[size.length-1][s.output]);
					// }

					lastReward = expectedOutput[s.output];
					net.backProp(activation, expectedOutput);
				}
			}
		}
	}

	public void learn() {
		if (!enabled)
			return;

		// net.printWeights();
		if (qlearning) {
			qlearn();
		} else {
			alternativeLearn();
		}
		net.degradeRate();
		// net.printWeights();
	}

	public void writeToFile(int e, int t) throws IOException {
		net.setVariables(chance, e, t);
		File f = new File(dir, t + net.id + e);
		System.out.println("Saved neuralnet to file: " + f.getPath());
		System.out.println(net.getChance());
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
		oos.writeObject(net);
		oos.flush();
		oos.close();
	}

	public void readFromFile(String fileName, boolean b) throws IOException, ClassNotFoundException {
		File f = new File(dir, fileName);
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f.getAbsoluteFile()));
		Object obj = ois.readObject();
		//System.out.println("Loaded neuralnet from file: " + f.getPath());
		net = (NeuralNetwork) obj;
		if (b) {
			this.chance = net.getChance();
		} else {
			this.chance = 100;
		}
		ois.close();
	}

	public int getEpoch() {
		return net.getEpoch();
	}

	public int getTrial() {
		return net.getTrial();
	}
	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	public void nextTrial() {
		int t=net.getTrial()+1;
		net=new NeuralNetwork(size,net.id);
		net.setVariables(90, 0, t);
		chance = chanceStart;
		
		try {
			writeToFile(0,t);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
