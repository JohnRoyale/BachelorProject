package Main;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		String fileName = JOptionPane.showInputDialog("Input neural network id to store network to.");
		String[] choices = { "Q-learning", "Monte-Carlo" };
		String[] reward = { "Individual", "Shared" };
		String[] opponent = { "Random", "Classic" };
		String[] speeds ={"1","2","4","8","16","32","64"};

		String input = (String) JOptionPane.showInputDialog(null, "Choose the learning algorithm", "Algorithm choice",
				JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);

		boolean qlearning = input.equals(choices[0]);

		input = (String) JOptionPane.showInputDialog(null, "Choose the reward function", "Reward choice",
				JOptionPane.QUESTION_MESSAGE, null, reward, reward[0]);
		boolean capitalist = input.equals(reward[0]);
		
		input = (String) JOptionPane.showInputDialog(null, "Choose the opponent", "Opponent choice",
				JOptionPane.QUESTION_MESSAGE, null, opponent, opponent[0]);
		boolean enemy =input.equals(opponent[0]);


		int n = JOptionPane.showConfirmDialog(null, "Do you want the games displayed", "Draw games",
				JOptionPane.YES_NO_OPTION);

		boolean draw = n == JOptionPane.YES_OPTION;
		int speed = 1;
		if (draw) {
			speed= Integer.parseInt((String) JOptionPane.showInputDialog(null, "Choose speed of the simulation", "Speed choice",
					JOptionPane.QUESTION_MESSAGE, null, speeds, speeds[0]));
		}

		Controller controller = new Controller(qlearning, capitalist, true, fileName,enemy);
		System.out.println();
		long startTime = System.currentTimeMillis();
		long ctime = System.currentTimeMillis();
		long gameTime;
		long wins = 0;
		long equal = 0;
		int j = 1;
		for (int t = controller.getTrial(); t < 100; t++) {

			for (int i = controller.getEpoch() + 1; i <= 1; i++) {
				int k = 0;
				gameTime = System.currentTimeMillis();

				while (!controller.gameOver() && k < 90 * 50) {
					if ((System.currentTimeMillis() - ctime > 20)
							|| (controller.queueFull() && (!draw || (System.currentTimeMillis() - ctime > 50/speed)))) {
						// draw = System.currentTimeMillis() - ctime > 20 &&
						// draw;
						ctime = System.currentTimeMillis();
						controller.update(draw);
						k++;
					}
				}
				ctime = System.currentTimeMillis();
				try {
					controller.backProp(draw, i, t);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (i % 1 == 0)
					System.out.println("Epoch: " + i + ", Round time: " + (System.currentTimeMillis() - gameTime)
							+ "ms, Frames: " + k + ", backProp time: " + (System.currentTimeMillis() - ctime)
							+ ", Player " + controller.winner() + " won");
				if (controller.winner() == 0) {
					equal++;
				} else {
					wins += controller.winner() - 1;
				}
				if (i % 1 == 0)
					System.out.println("Neural AI won " + wins + " games drawed " + equal + " games out of the last "
							+ j + " games played");

				System.out.println();

				controller.reset();
				startTime = System.currentTimeMillis();
				// Thread.sleep(1000);
				ctime = System.currentTimeMillis();
				j++;
				if (j > 1000) {
					j = 1;
					equal = 0;
					wins = 0;
				}
			}
			controller.nextTrial();
			j = 1;
			equal = 0;
			wins = 0;

		}
		System.exit(0);
	}

}
