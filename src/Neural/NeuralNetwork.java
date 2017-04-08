package Neural;

import java.util.Arrays;
import java.util.Random;

public class NeuralNetwork {
	static double[][][] weights;
	static double[][][] dWeights;
	static int[] size;
	static double[][] activation;
	static double startingLearningRate = 0.1;
	static double minLearningRate=0.05;
	static double degration = 0.9;
	static double momentum =0.1;
	static double maxError = 0.01;
	static int maxEpoch=100000000;
	static int printD = Math.max(maxEpoch/10000,10);
	static double bias = -1;
	
	
	public NeuralNetwork(int[] size){
		this.size=size;
		activation=new double[size.length][];
		
		init();
	}
	
	private void init(){
		weights = new double[size.length - 1][][];
		dWeights=new double [size.length -1][][];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = new double[size[i + 1]][size[i]];
			dWeights[i] = new double[size[i + 1]][size[i]];
		}

		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < weights.length; i++) {
			for (int j = 0; j < weights[i].length; j++) {
				for (int k = 0; k < weights[i][j].length; k++) {
					weights[i][j][k] = r.nextDouble() * 2 - 1;
				}
			}
		}
	}
	
	private static double sigmoid(double in) {
		return (double) (1 /( Math.exp(-in) + 1));
	}

	private static double dSigmoid(double in) {
		double t = sigmoid(in);
		return t * (1 - t);
	}
	
	public double[][] forwardProp(double[] input){
		activation = new double[size.length][];
		activation[0] = Arrays.copyOf(input, input.length);
		
		for (int k = 0; k < size.length - 1; k++) {
			activation[k+1]=new double[size[k+1]];
			for (int i = 0; i < weights[k].length; i++) {
				activation[k+1][i]=0;
				for (int j = 0; j < weights[k][i].length; j++) {
					activation[k+1][i] += activation[k][j] *weights[k][i][j];
				}
				if (k < size.length - 2) {
					activation[k+1][i]=sigmoid(activation[k+1][i]);
				}
			}
		}
		return activation;
	}
	
	
}
