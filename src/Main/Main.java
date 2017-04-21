package Main;

import java.io.FileNotFoundException;

public class Main {

	public static void main(String[] args) {

		Controller controller = new Controller();
		long startTime=System.currentTimeMillis();
		long ctime = System.currentTimeMillis();

		for (int i = 0; i < 1000; i++) {
			int k=0;
			while (!controller.gameOver() && k< 300*50) {
				if (System.currentTimeMillis() - ctime > 20) {
					ctime = System.currentTimeMillis();
					controller.update();
					k++;
				}
			}
			ctime = System.currentTimeMillis();
			controller.backProp();
			System.out.println("backProp: " + (System.currentTimeMillis()-ctime));
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			controller.reset();
			startTime=System.currentTimeMillis();
			ctime = System.currentTimeMillis();
		}
	}

}
