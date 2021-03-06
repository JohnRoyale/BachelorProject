package Main;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Observable;

import Assets.*;

public class Model extends Observable {

	final int populationCap = 20;

	Map levelMap;

	

	// reward function
	int damageReward = 0;
	int killReward = 100;
	int baseKillReward = 1000;
	int baseDestroyedReward = -1000;
	int deathReward = -100;
	double timeReward = -1;

	boolean capitalist;
	private ArrayList<Player> playerList;
	double mapSize;

	int winner;
	boolean gameOver = false;

	public Model(String fileName, boolean c) {
		capitalist = c;
		try {
			levelMap = new Map(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


		playerList = new ArrayList<Player>();
		playerList.add(new Player(0)); // nature
		playerList.add(new Player(1)); // player 1
		playerList.add(new Player(2)); // player 2

		mapSize = (double) levelMap.size;

		for (int x = 0; x < levelMap.size; x++) {
			for (int y = 0; y < levelMap.size; y++) {
				if (levelMap.getTile(y, x) == '1') {
					playerList.get(1).baseX = x;
					playerList.get(1).baseY = y;
				} else if (levelMap.getTile(y, x) == '2') {
					playerList.get(2).baseX = x;
					playerList.get(2).baseY = y;
				}
			}
		}

		initBase();
	}

	private void initBase() {
		playerList.get(1).addAsset(new Building(1, playerList.get(1).baseX / (float) levelMap.size,
				playerList.get(1).baseY / (float) levelMap.size, 10, levelMap.size));
		playerList.get(2).addAsset(new Building(2, playerList.get(2).baseX / (float) levelMap.size,
				playerList.get(2).baseY / (float) levelMap.size, 10, levelMap.size));
	}

	public void order(Unit u, char action) {
		// moves unit to new position
		u.reward(timeReward);
		double movement = u.getSpeed() * 0.05 / levelMap.size;
		switch (action) {
		case ('u'): {
			if (!notAccessable(u.getX(), u.getY() - movement, u)
					&& !notAccessable(u.getX() + u.getDiameter(), u.getY() - movement, u))
				u.setCoordinates(u.getX(), u.getY() - movement);
			break;
		}
		case ('d'): {
			if (!notAccessable(u.getX(), u.getY() + u.getDiameter() + movement, u)
					&& !notAccessable(u.getX() + u.getDiameter(), u.getY() + u.getDiameter() + movement, u))
				u.setCoordinates(u.getX(), u.getY() + movement);
			break;
		}
		case ('l'): {
			if (!notAccessable(u.getX() - movement, u.getY(), u)
					&& !notAccessable(u.getX() - movement, u.getY() + u.getDiameter(), u))
				u.setCoordinates(u.getX() - movement, u.getY());
			break;
		}
		case ('r'): {
			if (!notAccessable(u.getX() + u.getDiameter() + movement, u.getY(), u)
					&& !notAccessable(u.getX() + u.getDiameter() + movement, u.getY() + u.getDiameter(), u))
				u.setCoordinates(u.getX() + movement, u.getY());
			break;
		}
		// case('n'): System.out.println("none");
		}
		this.checkCollisions(u);
	}

	public void order(Building b, char action) {
		// produce new unit at location of building
		if (playerList.get(b.getOwner()).getAssets().size() < this.populationCap) {
			switch (action) {
			case 's': {
				playerList.get(b.getOwner()).addAsset(new Spearman(b.getOwner(), b.getX(), b.getY(), levelMap.size));
				break;
			}
			case 'c': {
				playerList.get(b.getOwner()).addAsset(new Cavalry(b.getOwner(), b.getX(), b.getY(), levelMap.size));
				break;
			}
			case 'a': {
				playerList.get(b.getOwner()).addAsset(new Archer(b.getOwner(), b.getX(), b.getY(), levelMap.size));
			}
			// case('n'): System.out.println("none");
			// case('b'): System.out.println("busy");
			}
		}
	}

	public boolean notAccessable(double x, double y, Unit u) {
		// out of bounds check
		if ((x >= 1 || x < 0) || (y >= 1 || y < 0)) {
			return true;
		}

		// natural obstacle check
		int tileX = (int) (x * (levelMap.size));
		int tileY = (int) (y * (levelMap.size));
		if (levelMap.getTile(tileX, tileY) == '#')
			return true;

		// other unit check
		// for(Player p: playerList) {
		// for(Asset a: p.assets) {
		// if(x == a.getX() && y == a.getY() && a != u) {
		// return true;
		// }
		// }
		// }
		return false;
	}

	public void checkCollisions(Unit u) {
		for (Player p : playerList) {
			if (p.playerId != u.getOwner()) {
				for (Asset a : p.getAssets()) {
					if (u.collides(a)) {
						// System.out.println(u.toString() + " Attacking " +
						// a.toString());
						int damage = u.getAttackPower();

						if (a instanceof Unit && ((Unit) a).getType() == u.getCounter()) {
							damage = (int) Math.round(damage * 1.5);
						}

						if (u.getCoolDown() == 0) {
							a.damage(damage);
							u.setCoolDown(u.getAttackSpeed());
							if(!capitalist) {
								for (Asset as : playerList.get(u.getOwner()).getAssets()) {
									if (as instanceof Unit) {
										((Unit) as).reward(damage*damageReward);
									}
								}
							} else {
								u.reward(damage * damageReward);
							}
						} else {
							u.setCoolDown(u.getCoolDown() - 1);
						}

						if (a.getHitPoints() <= 0) {
							u.incKills();

							if (a instanceof Unit) {
								if (!capitalist) {
									for (Asset as : playerList.get(u.getOwner()).getAssets()) {
										if (as instanceof Unit) {
											((Unit) as).reward(killReward);
										}
									}
									for (Asset as : playerList.get(a.getOwner()).getAssets()) {
										if (as instanceof Unit) {
											((Unit) as).reward(deathReward);
										}
									}
								} else {
									((Unit) a).reward(deathReward);
									u.reward(killReward);
								}
							} else {
								winner = u.getOwner();
								gameOver = true;

								Player win = playerList.get(winner);

								if (!capitalist) {
									for (Asset as : win.getAssets()) {
										if (as instanceof Unit) {
											((Unit) as).reward(baseKillReward);
										}
									}
								} else {
									u.reward(baseKillReward);
								}
								
								for (Asset as : p.getAssets()) {
									if (as instanceof Unit) {
										((Unit) as).reward(baseDestroyedReward);
									}
								}
							}
							if (u.getState().equals("hunt"))
								u.setState("idle");

							p.getLostAssets().add(a);
							p.getAssets().remove(a);
						}
					}
				}
			}
		}
	}

	public double getTileResistance(int playerID, int x, int y, char type) {
		double resistance = 0;

		for (Player p : playerList) {
			for (Asset a : p.getAssets()) {
				if ((int) (a.getX() * mapSize) == x && (int) (a.getY() * mapSize) == y) {
					if (p.playerId != playerID) {
						if (a instanceof Unit) {
							Unit u = (Unit) a;
							if (type == 'a' && u.getType() == 's') {
								resistance += .5;
							} else if (type == 's' && u.getType() == 'c') {
								resistance += .5;
							} else if (type == 'c' && u.getType() == 'a') {
								resistance += .5;
							} else {
								resistance++;
							}
						}
					} else {
						resistance--;
					}
				}
			}
		}

		return resistance;
	}

	public void updateAll() {
		this.setChanged();
		this.notifyObservers();
	}

	public boolean gameOver() {
		return gameOver;
	}

	public int getWinner() {
		return winner;
	}

	public Map getLevelMap() {
		return levelMap;
	}

	public double getMapSize() {
		return mapSize;
	}

	public ArrayList<Player> getPlayerList() {
		return this.playerList;
	}

	public void reset() {
		gameOver = false;
		winner = 0;
		for (Player p : playerList) {
			p.getAssets().clear();
			p.getLostAssets().clear();
		}
		initBase();

	}
}
