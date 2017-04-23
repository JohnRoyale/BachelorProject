package Main;

import java.io.FileNotFoundException;

public class Main {

	public static void main(String[] args) {

		Controller controller = new Controller();
		long startTime = System.currentTimeMillis();
		long ctime = System.currentTimeMillis();
		long gameTime;
		boolean draw;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			int k = 0;
			gameTime = System.currentTimeMillis();
			draw = i % 100 == 0;
			while (!controller.gameOver() && k < 300 * 50) {
				if (System.currentTimeMillis() - ctime > 20 || (!draw && controller.queueFull())) {
					if (k % 1000 == 0)
						System.out.println(System.currentTimeMillis() - ctime);
					draw = System.currentTimeMillis() - ctime > 20 && draw;
					ctime = System.currentTimeMillis();
					controller.update(draw);
					k++;
				}
			}
			ctime = System.currentTimeMillis();
			controller.backProp();
			System.out.println("Epoch: " + i + ", Round time: " + (System.currentTimeMillis() - gameTime) + "ms, Frames: "
					+ k + ", backProp time: " + (System.currentTimeMillis() - ctime));

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			controller.reset();
			startTime = System.currentTimeMillis();
			ctime = System.currentTimeMillis();
		}
	}

}
