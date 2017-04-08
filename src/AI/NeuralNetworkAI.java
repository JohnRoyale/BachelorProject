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
import Neural.NeuralNetwork;
import PathFinder.ResistancePathFinder;
import PathFinder.ShortestPathFinder;

public class NeuralNetworkAI implements AI {
	Model model;
	int playerID, enemyID;
	Player enemy, self;
	ConcurrentLinkedQueue<Order> orderQueue;
	ResistancePathFinder p;
	ShortestPathFinder sp;
	List<String> actions = Arrays.asList("defendBase", "defensiveInvade", "evasiveInvade", "hunt");
	List<Character> production = Arrays.asList('n', 's', 'a', 'c');
	NeuralNetwork net;
	int inputs = 9;
	int[] size = { inputs, 50, actions.size() };
	int range=1;
	double[] input;
	double[][] activation;

	public NeuralNetworkAI(ConcurrentLinkedQueue<Order> orderQueue, Model m, int player) {
		playerID = player;
		this.orderQueue = orderQueue;
		model = m;
		p = new ResistancePathFinder(m);
		sp = new ShortestPathFinder(m.getLevelMap());
		net = new NeuralNetwork(size);
		input=new double[inputs];
	}

	@Override
	public void determineAction(Asset a) {
		char action = 'n';

		Random random = new Random();
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
				input[0]=u.getHitPoints();
				if(u.getType()=='s'){
					input[1]=0;
				}else if(u.getType()=='a'){
					input[1]=1;
				}else{
					input[1]=2;
				}
				
				double resistance=0;
				for(int i=-range;i<=range;i++){
					for(int j=-range;i<=range;i++){
						resistance += model.getTileResistance(playerID, i, i,u.getType());
					}
				}
				input[2]=resistance;
				
				input[3]=enemy.archerCount;
				input[4]=enemy.cavalryCount;
				input[5]=enemy.spearmanCount;
				input[6]=self.attackers;
				input[7]=self.defenders;
				input[8]=self.hunters;
				
				activation = net.forwardProp(input);
				
				double[] output=activation[size.length-1];
				int best=0;
				for(int i=0;i<output.length;i++){
					String str = String.format("%1.2f", output[i])+" ";
					System.out.print(str);
					if(output[i]>output[best]){
						best=i;
					}
				}
				System.out.println(actions.get(best));
				u.setState(actions.get(best));
			}
			
			action = u.determineAction(p, sp, model, enemyID);

		}
		a.setIdle(false);
		orderQueue.add(new Order(a, action));

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
