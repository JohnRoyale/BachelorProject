package Main;

import java.util.LinkedList;
import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedQueue;

import AI.AI;
import AI.randomAI;

public class Controller{
	private AI player1, player2;
	private Model model;
	private ConcurrentLinkedQueue<Order> orderQueue;
	Thread p1;
	Thread p2;
	//private View view;
	
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
		
		this.update();
	}
	
	public void addOrderToQueue(Order o){
		orderQueue.add(o);
	}
	
	public void update(){
		int size=orderQueue.size();
		for(int i=0;i<size;i++){
			Order o=orderQueue.poll();
			
			//Execute order
			
		}
	}
}
