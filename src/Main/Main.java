package Main;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Main {

	final static boolean fastForward = true;
	final static boolean qlearning = true;
	final static boolean capitalist = true;

	public static void main(String[] args) throws InterruptedException {
		String fileName = JOptionPane.showInputDialog("Input file to load neural network from");
		Controller controller = new Controller(qlearning, capitalist, true, fileName);
		long startTime = System.currentTimeMillis();
		long ctime = System.currentTimeMillis();
		long gameTime;
		long wins = 0;
		boolean draw;
		long equal = 0;
		int j = 1;
		for (int i = controller.getEpoch(); i < 10000000; i++) {
			int k = 0;
			gameTime = System.currentTimeMillis();
			draw = (i % 10) == 0;

			while (!controller.gameOver() && k < 90 * 50) {
				if ((System.currentTimeMillis() - ctime > 20) || (controller.queueFull() && (!draw || fastForward))) {
					// draw = System.currentTimeMillis() - ctime > 20 && draw;
					ctime = System.currentTimeMillis();
					controller.update(draw);
					k++;
				}
			}
			ctime = System.currentTimeMillis();
			try {
				controller.backProp(draw, i);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (i % 1 == 0)
				System.out.println("Epoch: " + i + ", Round time: " + (System.currentTimeMillis() - gameTime)
						+ "ms, Frames: " + k + ", backProp time: " + (System.currentTimeMillis() - ctime) + ", Player "
						+ controller.winner() + " won");
			if (controller.winner() == 0) {
				equal++;
			} else {
				wins += controller.winner() - 1;
			}
			if (i % 1 == 0)
				System.out.println("Neural AI won " + wins + " games drawed " + equal + " games out of the last " + j
						+ " games played\n");

			controller.reset();
			startTime = System.currentTimeMillis();
			// Thread.sleep(1000);
			ctime = System.currentTimeMillis();
			j++;
			if (j > 10) {
				j = 1;
				equal = 0;
				wins = 0;
			}
		}
		System.exit(0);
	}

}
