package Neural;

import java.util.Arrays;
import java.util.Random;

public class NeuralNetwork {
	static double[][][] weights;
	static double[][][] dWeights;
	static int[] size;
	static double[][] activation;
	static double startingLearningRate = 0.4;
	static double minLearningRate=0.2;
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
	
	private static double[] arraySub(double[] A, double[] B) {
		if (A.length != B.length)
			System.err.print("Arrays need to be off equals size to substract");
		double[] C = new double[A.length];
		for (int i = 0; i < A.length; i++)
			C[i] = A[i] - B[i];
		return C;
	}
	
	public void backProp(double[][] activation, double[] expectedOutput) {
		double[][] error = new double[activation.length][];

		for (int i = 0; i < activation.length; i++) {
			error[i] = new double[activation[i].length];
		}

		double[][] delta = new double[size.length][];
		for (int i = 0; i < delta.length; i++) {
			delta[i] = new double[size[i]];
		}

		error[error.length - 1] = arraySub(expectedOutput, activation[activation.length - 1]);

		for (int k = weights.length - 1; k > 0; k--) {
			if (k > 0) {
				error[k] = new double[error[k].length];
			}
			for (int i = 0; i < delta[k + 1].length; i++) {
				if (k != weights.length - 1) {
					delta[k + 1][i] = error[k + 1][i] * dSigmoid(activation[k + 1][i]);
				} else {
					delta[k + 1][i] = error[k + 1][i];
				}
				for (int j = 0; j < weights[k][i].length; j++) {
					error[k][j] += delta[k + 1][i] * weights[k][i][j];
				}
			}
		}

		for (int k = weights.length - 1; k > 0; k--) {
			for (int i = 0; i < weights[k].length; i++) {
				for (int j = 0; j < weights[k][i].length; j++) {
					dWeights[k][i][j] =startingLearningRate * delta[k + 1][i] * activation[k][j]+
							momentum*dWeights[k][i][j];
					weights[k][i][j] += dWeights[k][i][j];
				}
			}
		}

	}
	
	
}
