package Main;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import AI.AI;
import AI.randomAI;

public class Controller extends Observable{
	private AI player1, player2;
	private Model model;
	private ConcurrentLinkedQueue<Order> orderQueue;
	Thread p1;
	Thread p2;
	JFrame frame;
	private View view;
	
	public Controller() {
		model=new Model("map1");
		model.levelMap.printMap();
		orderQueue=new ConcurrentLinkedQueue<Order>();
		player1=new randomAI(orderQueue,model);
		player2=new randomAI(orderQueue,model);
		
		p1=new Thread(player1);
		p2=new Thread(player2);
		
		p1.start();
		p2.start();
		
		frame=new JFrame("Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel emptyLabel = new JLabel(""); 
		frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);
		
		view=new View(model);
		this.addObserver(view);
		
		frame.add(view);
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.pack();
		frame.setSize(1024, 1000);
		frame.setVisible(true);
		
		this.setChanged();
		this.notifyObservers();
		
		this.update();
	}

	public void addOrderToQueue(Order o){
		orderQueue.add(o);
	}
	
	public void update(){
		int size=orderQueue.size();
	 
		for(int i=0;i<size;i++){
			Order o=orderQueue.poll();		
			this.setChanged();
		}
		this.notifyObservers();
		
	}
}
