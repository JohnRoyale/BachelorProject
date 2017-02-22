package Main;

import Assets.Asset;

public class Order {
	public Asset a;
	public char action;
	
	public Order(Asset a, char action){
		this.a=a;
		this.action=action;
	}
}
