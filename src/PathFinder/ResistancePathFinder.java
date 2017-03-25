package PathFinder;

import java.util.PriorityQueue;

import Main.Map;
import Main.Model;

public class ResistancePathFinder {

	class State implements Comparable<State> {
		int x, y;
		int travelled;
		int resistance;
		int heuristic;
		int total;
		char direction;

		public State(int x, int y, char direction, int travelled, int resistance, int heuristic) {
			this.x = x;
			this.y = y;
			this.resistance = resistance;
			this.direction = direction;
			this.travelled = travelled;
			this.heuristic = heuristic;
			total = heuristic + travelled;
		}

		public void set(char direction, int travelled, int resistance) {
			// System.out.println(resistance);
			if (travelled < this.travelled) {
				this.resistance = resistance;
				this.direction = direction;
				this.travelled = travelled;
				total = heuristic + travelled;
				addState(this);
			}
		}

		@Override
		public int compareTo(State s) {

			if (this.total > s.total) {
				if (this.total > s.total + 3)
					return 1;
			} else if (s.total > this.total) {
				if (s.total > this.total + 3)
					return -1;
			}
			if (evade) {
				return this.resistance - s.resistance;
			} else {
				return s.resistance - this.resistance;
			}

		}
	}

	private PriorityQueue<State> queue;
	private Map m;
	private boolean evade;

	private void addState(State s) {
		if (m.getTile(s.x, s.y) != '#') {
			queue.offer(s);
		}
	}

	public char findPath(double x1, double y1, double x2, double y2, double diameter, Map m, Model model, int p,
			boolean evade) {
		long ctime=System.currentTimeMillis();
		this.m = m;
		this.evade = evade;
		char direction = 'n';
		queue = new PriorityQueue<State>();
		State[][] map = new State[m.size][m.size];

		int startX = (int) (x1 * m.size);
		int startY = (int) (y1 * m.size);
		int goalX = (int) (x2 * m.size);
		int goalY = (int) (y2 * m.size);

		// System.out.println(startX +" "+startY +" " + goalX +" "+goalY);

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
				map[i][j] = new State(i, j, 'n', 1000 * m.size, 10000, this.heuristic(i, j, goalX, goalY));
			}
		}
		int resistance = model.getTileResistance(p, startX, startY) + model.getTileResistance(p, startX + 1, startY)
				+ model.getTileResistance(p, startX - 1, startY) + model.getTileResistance(p, startX, startY + 1)
				+ model.getTileResistance(p, startX, startY - 1);
		if (startY > 0)
			map[startX][startY - 1].set('u', 1,
					resistance + model.getTileResistance(p, startX, startY - 2)
							+ model.getTileResistance(p, startX - 1, startY - 1)
							+ model.getTileResistance(p, startX + 1, startY - 1));
		if (startX < m.size - 1)
			map[startX + 1][startY].set('r', 1,
					resistance + model.getTileResistance(p, startX + 2, startY)
							+ model.getTileResistance(p, startX + 1, startY + 1)
							+ model.getTileResistance(p, startX + 1, startY - 1));
		if (startX > 0)
			map[startX - 1][startY].set('l', 1,
					resistance + model.getTileResistance(p, startX - 2, startY)
							+ model.getTileResistance(p, startX - 1, startY + 1)
							+ model.getTileResistance(p, startX - 1, startY - 1));
		if (startY < m.size - 1)
			map[startX][startY + 1].set('d', 1,
					resistance + model.getTileResistance(p, startX, startY + 2)
							+ model.getTileResistance(p, startX + 1, startY + 1)
							+ model.getTileResistance(p, startX - 1, startY + 1));

		State s;
		while (queue.size() > 0) {

			s = queue.poll();
			if (s.x == goalX && s.y == goalY) {
				direction = s.direction;
				continue;
			}
			if (s.x < m.size) {
				map[s.x + 1][s.y].set(s.direction, s.travelled + 1,
						s.resistance + model.getTileResistance(p, s.x + 2, s.y)
								+ model.getTileResistance(p, s.x + 1, s.y + 1)
								+ model.getTileResistance(p, s.x + 1, s.y - 1));
			}
			if (s.x > 0) {
				map[s.x - 1][s.y].set(s.direction, s.travelled + 1,
						s.resistance + model.getTileResistance(p, s.x - 2, s.y)
								+ model.getTileResistance(p, s.x - 1, s.y + 1)
								+ model.getTileResistance(p, s.x - 1, s.y - 1));
			}
			if (s.y < m.size) {
				map[s.x][s.y + 1].set(s.direction, s.travelled + 1,
						s.resistance + model.getTileResistance(p, s.x, s.y + 2)
								+ model.getTileResistance(p, s.x + 1, s.y + 1)
								+ model.getTileResistance(p, s.x - 1, s.y + 1));
			}
			if (s.y > 0) {
				map[s.x][s.y - 1].set(s.direction, s.travelled + 1,
						s.resistance + model.getTileResistance(p, startX, s.y - 2)
								+ model.getTileResistance(p, s.x - 1, s.y - 1)
								+ model.getTileResistance(p, s.x + 1, s.y - 1));
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
