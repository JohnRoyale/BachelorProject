package Main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JOptionPane;

public class MainTest {
	final static int epochs = 100;
	final static int inc = 10;

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		String fileName = JOptionPane
				.showInputDialog("Input file prefix to test");
		long ctime = System.currentTimeMillis();
		Controller controller = new Controller(false, true, false, fileName);

		int postfix = 0;
		String name = fileName + postfix;
		PrintStream stdout = System.out;
		PrintStream out = new PrintStream(new FileOutputStream(fileName
				+ "_performance.txt"));
		System.setOut(out);
		while (controller.load(name)) {
			int equal = 0;
			int j = 0;
			for (int i = 0; i < epochs; i++) {
				int k = 0;
				while (!controller.gameOver() && k < 90 * 50) {
					if (controller.queueFull()
							|| (System.currentTimeMillis() - ctime > 20)) {
						ctime = System.currentTimeMillis();
						controller.update(false);
						k++;
						// System.out.print(k+" ");
					}
				}
				if (controller.winner() == 0) {
					equal++;
				} else {
					j += controller.winner() - 1;
				}
				// if ((i+1) % 10 == 0)
				// System.out.println(j + "/" + (i + 1));
				controller.reset();
				// System.out.print(i);
			}

			System.out.println("After " + postfix
					+ " epochs of training Neural AI won " + j
					+ " games, drawed " + equal + " games out of " + epochs
					+ " games");
			// System.setOut(stdout);
			if (postfix == 0) {
				postfix++;
			} else {
				postfix += inc;
			}
			name = fileName + postfix;
		}
		System.exit(0);
	}

}
