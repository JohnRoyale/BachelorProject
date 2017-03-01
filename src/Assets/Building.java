package Assets;

public class Building extends Asset {
	int productionTimer;
	char inProduction;

	public Building(int owner, double x, double y, int hp, int mapSize) {
		super(owner, x, y, 0.8/mapSize, hp);
		productionTimer = 2; //initial production delay
		inProduction = 'n'; //initial production state
	}

	public int getProductionTimer() {
		return productionTimer;
	}

	public void setProductionTimer(int productionTimer) {
		this.productionTimer = productionTimer;
	}

	public char getInProduction() {
		return inProduction;
	}

	public void setInProduction(char inProduction) {
		this.inProduction = inProduction;
	}
	
	
}
