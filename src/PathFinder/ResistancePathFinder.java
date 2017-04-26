package PathFinder;

import java.util.PriorityQueue;

import Main.Map;
import Main.Model;

public class ResistancePathFinder {

	class Position implements Comparable<Position> {
		int x, y;
		int travelled;
		double resistance;
		int heuristic;
		int total;
		char direction;

		public Position(int x, int y, char direction, int travelled, int resistance, int heuristic) {
			this.x = x;
			this.y = y;
			this.resistance = resistance;
			this.direction = direction;
			this.travelled = travelled;
			this.heuristic = heuristic;
			total = heuristic + travelled;
		}

		public void set(char direction, int travelled, double d) {
			// System.out.println(resistance);
			if (travelled < this.travelled) {
				this.resistance = d;
				this.direction = direction;
				this.travelled = travelled;
				total = heuristic + travelled;
				addState(this);
			}
		}

		@Override
		public int compareTo(Position s) {

			
			if (this.total > s.total + 3){
					return 1;
			} else 	if (s.total > this.total + 3){
					return -1;
			}

			double value;
			if (evade) {
				value = this.resistance - s.resistance;
			} else {
				value = s.resistance - this.resistance;
			}

			if (value == 0) {
				return this.total-s.total;
			}else if(value>0){
				return 1;
			}else{
				return -1;
			}

		}
	}

	private PriorityQueue<Position> queue;
	Position[][] map;
	private Map m;
	Model model;
	private boolean evade;

	private void addState(Position s) {
		if (m.getTile(s.x, s.y) != '#') {
			queue.offer(s);
		}
	}

	public ResistancePathFinder(Model model) {
		this.m = model.getLevelMap();
		this.model = model;
		queue = new PriorityQueue<Position>();
	}

	private double getTileResistance(int p, int x, int y, char t) {
		if (x < 0 || x > m.size - 1 || y < 0 || y > m.size - 1)
			return 0;
		if (map[x][y].resistance < 10000) {
			return map[x][y].resistance;
		} else {
			return model.getTileResistance(p, x, y, t);
		}
	}

	public char findPath(double x1, double y1, double x2, double y2, double diameter, int p, boolean evade, char t) {
		this.evade = evade;
		char direction = 'n';
		queue.clear();

		map = new Position[m.size][m.size];

		int startX = (int) (x1 * m.size);
		int startY = (int) (y1 * m.size);
		int goalX = (int) (x2 * m.size);
		int goalY = (int) (y2 * m.size);

		// if already on correct tile return 'no move'
		if (this.heuristic(startX, startY, goalX, goalY) == 0) {
			if (Math.abs(x1 - x2) > Math.abs(y1 - y2)) {
				// System.out.println(unitX-targetX);
				if (x1 - x2 < 0) {
					return 'r';
				} else {
					return 'l';
				}
			} else {
				if (y1 - y2 < 0) {
					return 'd';
				} else {
					return 'u';
				}
			}
		}

		for (int i = 0; i < m.size; i++) {
			for (int j = 0; j < m.size; j++) {
				map[i][j] = new Position(i, j, 'n', 1000 * m.size, 10000, this.heuristic(i, j, goalX, goalY));
			}
		}

		double resistance = this.getTileResistance(p, startX, startY, t)
				+ this.getTileResistance(p, startX + 1, startY, t) + this.getTileResistance(p, startX - 1, startY, t)
				+ this.getTileResistance(p, startX, startY + 1, t) + this.getTileResistance(p, startX, startY - 1, t);
		if (startY > 0)
			map[startX][startY - 1].set('u', 1,
					resistance + this.getTileResistance(p, startX, startY - 2, t)
							+ this.getTileResistance(p, startX - 1, startY - 1, t)
							+ this.getTileResistance(p, startX + 1, startY - 1, t));
		if (startX < m.size - 1)
			map[startX + 1][startY].set('r', 1,
					resistance + this.getTileResistance(p, startX + 2, startY, t)
							+ this.getTileResistance(p, startX + 1, startY + 1, t)
							+ this.getTileResistance(p, startX + 1, startY - 1, t));
		if (startX > 0)
			map[startX - 1][startY].set('l', 1,
					resistance + this.getTileResistance(p, startX - 2, startY, t)
							+ this.getTileResistance(p, startX - 1, startY + 1, t)
							+ this.getTileResistance(p, startX - 1, startY - 1, t));
		if (startY < m.size - 1)
			map[startX][startY + 1].set('d', 1,
					resistance + this.getTileResistance(p, startX, startY + 2, t)
							+ this.getTileResistance(p, startX + 1, startY + 1, t)
							+ this.getTileResistance(p, startX - 1, startY + 1, t));

		Position s;
		while (queue.size() > 0) {

			s = queue.poll();
			if (s.x == goalX && s.y == goalY) {
				direction = s.direction;
				continue;
			}
			if (s.x < m.size) {
				map[s.x + 1][s.y].set(s.direction, s.travelled + 1,
						s.resistance + this.getTileResistance(p, s.x + 2, s.y, t)
								+ this.getTileResistance(p, s.x + 1, s.y + 1, t)
								+ this.getTileResistance(p, s.x + 1, s.y - 1, t));
			}
			if (s.x > 0) {
				map[s.x - 1][s.y].set(s.direction, s.travelled + 1,
						s.resistance + this.getTileResistance(p, s.x - 2, s.y, t)
								+ this.getTileResistance(p, s.x - 1, s.y + 1, t)
								+ this.getTileResistance(p, s.x - 1, s.y - 1, t));
			}
			if (s.y < m.size) {
				map[s.x][s.y + 1].set(s.direction, s.travelled + 1,
						s.resistance + this.getTileResistance(p, s.x, s.y + 2, t)
								+ this.getTileResistance(p, s.x + 1, s.y + 1, t)
								+ this.getTileResistance(p, s.x - 1, s.y + 1, t));
			}
			if (s.y > 0) {
				map[s.x][s.y - 1].set(s.direction, s.travelled + 1,
						s.resistance + this.getTileResistance(p, startX, s.y - 2, t)
								+ this.getTileResistance(p, s.x - 1, s.y - 1, t)
								+ this.getTileResistance(p, s.x + 1, s.y - 1, t));
			}
		}

		if (direction == 'd' || direction == 'u') {
			if (startX != (int) ((x1 + diameter) * m.size)) {
				direction = 'l';
			}
		} else {
			if (startY != (int) ((y1 + diameter) * m.size)) {
				direction = 'u';
			}
		}
		return direction;
	}

	private int heuristic(int x1, int y1, int x2, int y2) {
		return (Math.abs(x2 - x1) + Math.abs(y2 - y1));
	}

}
