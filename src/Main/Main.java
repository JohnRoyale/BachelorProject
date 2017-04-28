package Main;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
	
	final static boolean qlearning=true;
	final static boolean capitalist =true;
	
	
	public static void main(String[] args) {

		Controller controller = new Controller(qlearning,capitalist);
		long startTime = System.currentTimeMillis();
		long ctime = System.currentTimeMillis();
		long gameTime;
		long wins=0;
		boolean draw;

		int j=1;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			int k = 0;
			gameTime = System.currentTimeMillis();
			draw = i % 100 == 0;
			while (!controller.gameOver() && k < 300 * 50) {
				if ((System.currentTimeMillis() - ctime > 20)|| (!draw && controller.queueFull())) {
					draw = System.currentTimeMillis() - ctime > 20 && draw;
					ctime = System.currentTimeMillis();
					controller.update(draw);
					k++;
				}
			}
			ctime = System.currentTimeMillis();
			try {
				controller.backProp(draw);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Epoch: " + i + ", Round time: " + (System.currentTimeMillis() - gameTime) + "ms, Frames: "
					+ k + ", backProp time: " + (System.currentTimeMillis() - ctime)+", Player "+ controller.winner()+" won");
			wins += controller.winner() -1;
			System.out.println("Neural AI won "+ wins+ "/"+j+" last games\n");
			
			controller.reset();
			startTime = System.currentTimeMillis();
			ctime = System.currentTimeMillis();
			j++;
			if(j>100){
				j=1;
				wins=0;
			}
		}
	}

}
