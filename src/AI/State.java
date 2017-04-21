package AI;

public class State{
	int reward;
	double[] input;
	int output;
	
	public State(double[] in,int out){
		this.input=in;
		this.output=out;
		reward=0;
	}
	
	public void incrementReward(int i){
		reward += i;
	}
}
