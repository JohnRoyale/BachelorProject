package PathFinder;

import java.util.PriorityQueue;

import Main.Map;

public class PathFinder {
	
	class State implements Comparable<State>{
		int x,y;
		int travelled;
		int heuristic;
		int total;
		char direction;
		
		public State(int x, int y,char direction, int travelled,int heuristic){
			this.x=x;
			this.y=y;
			this.direction=direction;
			this.travelled=travelled;
			this.heuristic=heuristic;
			total=heuristic+travelled;
		}
		public void set(char direction, int travelled){
			if(travelled<this.travelled){
				this.direction=direction;
				this.travelled=travelled;
				total=heuristic+travelled;
				addState(this);
			}
		}
		@Override
		public int compareTo(State s) {	            
			return this.total-s.total;
		}
	}
	
	private PriorityQueue<State> queue;
	private Map m;
	private void addState(State s){
		if(m.getTile(s.x, s.y)!= '#'){
			queue.offer(s);
		}
	}
	
	public char findPath(int x1,int y1,int x2,int y2, Map m){
		this.m=m;
		char direction='n';
		queue= new PriorityQueue<State>();
		State[][] map=new State[m.size][m.size];
		
		//System.out.println(x1 +" "+y1 +" " + x2 +" "+y2);
		
		//if already on correct tile return 'no move'
		if(this.heuristic(x1, y1, x2, y2)==0)return 'n';
		
		for(int i=0;i<m.size;i++){
			for(int j=0;j<m.size;j++){
				map[i][j]=new State(i,j,'n',1000*m.size,this.heuristic(i, j, x2, y2));
			}
		}
		if(y1>0)map[x1][y1-1].set('u', 1);
		if(x1<m.size-1)map[x1+1][y1].set('r', 1);
		if(x1>0)map[x1-1][y1].set('l', 1);
		if(y1<m.size-1)map[x1][y1+1].set('d', 1);
		
		State s;
		while(queue.size()>0){
			
			s = queue.poll();
			//System.out.println(s.x +" "+s.y+" "+x2+" "+y2+" "+s.travelled+" "+queue.size()+" "+s.direction);
			if(s.x==x2 && s.y==y2){
				//System.out.println(s.direction);
				return s.direction;
			}
			if(s.x<m.size){
				map[s.x+1][s.y].set(s.direction, s.travelled+1);
			}
			if(s.x > 0){
				map[s.x-1][s.y].set(s.direction, s.travelled+1);
			}
			if(s.y<m.size){
				map[s.x][s.y+1].set(s.direction, s.travelled+1);
			}
			if(s.y>0){
				map[s.x][s.y-1].set(s.direction, s.travelled+1);
			}
		}
		//System.out.println(direction);
		return direction;
	}
	
	private int heuristic(int x1,int y1,int x2,int y2){
		return (Math.abs(x2-x1)+Math.abs(y2-y1));
	}
	
	
}
