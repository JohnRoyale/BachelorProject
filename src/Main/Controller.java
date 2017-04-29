package Main;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import AI.*;
import Assets.*;

public class Controller extends Observable {
	private AI player1, player2;
	private Model model;
	private ConcurrentLinkedQueue<Order> orderQueue;
	Thread p1;
	Thread p2;
	JFrame frame;
	private View view;
	Random r = new Random(System.currentTimeMillis());

	public Controller(boolean q, boolean c, String file) {
		model = new Model("map2",c);
		model.levelMap.printMap();
		orderQueue = new ConcurrentLinkedQueue<Order>();
		//player1 = new RandomBehaviourAI(orderQueue, model, 1);
		player1 = new RandomBehaviourAI(orderQueue, model, 1);
		player2 = new NeuralNetworkAI(orderQueue, model, 2,q,file);

		p1 = new Thread(player1);
		p2 = new Thread(player2);

		p1.start();
		p2.start();

		frame = new JFrame("Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel emptyLabel = new JLabel("");
		frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);

		view = new View(model);
		this.addObserver(view);

		frame.add(view);
		// frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.pack();
		frame.setSize(1024, 1000);
		frame.setVisible(true);

		this.setChanged();
		this.notifyObservers();

		this.update(true);
	}

	public void addOrderToQueue(Order o) {
		orderQueue.add(o);
	}

	public void update(boolean draw) {
		int size = orderQueue.size();
		// System.out.println(size);
		for (int i = 0; i < size; i++) {
			Order o = orderQueue.poll();
			if (o.a instanceof Building) {
				model.order((Building) o.a, o.action);
			} else {
				model.order((Unit) o.a, o.action);
				((Unit) o.a).incTurns();
			}
			if (draw) {
				this.setChanged();
			}
		}
		if (r.nextBoolean()) {
			player2.run();
			player1.run();
		} else {
			player1.run();
			player2.run();
		}
		if (draw) {
			this.notifyObservers();
		}

	}

	public boolean gameOver() {
		return model.gameOver();
	}

	public int winner() {
		return model.getWinner();
	}

	public void backProp(boolean b, int epoch) throws IOException {
		if (player1 instanceof NeuralNetworkAI) {
			((NeuralNetworkAI) player1).learn();
			((NeuralNetworkAI) player1).incChance();
			if(b)((NeuralNetworkAI) player1).writeToFile(epoch);
		}

		if (player2 instanceof NeuralNetworkAI) {
			((NeuralNetworkAI) player2).learn();
			((NeuralNetworkAI) player2).incChance();
			if(b)((NeuralNetworkAI) player2).writeToFile(epoch);
		}

	}
	
	public int getEpoch(){
		if (player1 instanceof NeuralNetworkAI) {
			return ((NeuralNetworkAI) player1).getEpoch();
		}

		if (player2 instanceof NeuralNetworkAI) {
			return ((NeuralNetworkAI) player2).getEpoch();
		}
		return 0;
	}

	public void reset() {
		model.reset();
		orderQueue.clear();
		player1.run();
		player2.run();
	}

	public boolean queueFull() {
		return orderQueue.size() >= model.getPlayerList().get(1).getAssets().size()
				+ model.getPlayerList().get(2).getAssets().size();
	}
}
