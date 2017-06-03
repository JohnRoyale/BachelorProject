package Main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.JOptionPane;

public class MainTest {
	final static int epochs = 50;
	final static int inc = 2;

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		String fileName = JOptionPane.showInputDialog("Input file id of network to test");
		long ctime = System.currentTimeMillis();
		Controller controller = new Controller(false, true, false, fileName);

		int prefix = 0;
		int postfix = 0;
		String subname = prefix + fileName;
		String name = subname + postfix;
		PrintStream stdout = System.out;
		PrintStream out = new PrintStream(new FileOutputStream(fileName + "_performance.csv"));
		//System.setOut(out);
		System.setOut(out);
		System.out.println("trial,epoch,wins");
		System.setOut(stdout);

		for (int t = 0; t < 1000; t++) {

			while (controller.load(name)) {
				int equal = 0;
				double j = 0;
				for (int i = 0; i < epochs; i++) {
					int k = 0;
					while (!controller.gameOver() && k < 90 * 50) {
						if (controller.queueFull() || (System.currentTimeMillis() - ctime > 20)) {
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
					System.out.println(j + "/" + (i + 1)+" ");
					controller.reset();
					//System.out.print(i);
				}
				System.setOut(out);
				System.out.println(prefix+","+postfix+","+j/epochs);
				System.setOut(stdout);
//				System.out.println("After " + postfix + " epochs of training Neural AI won " + j + " games, drawed "
//						+ equal + " games out of " + epochs + " games");
				// System.setOut(stdout);
				postfix += inc;

				name = subname + postfix;
			}
			prefix=t+1;
			postfix=0;
			subname=prefix+fileName;
			name=subname+postfix;
		}
		System.exit(0);
	}

}
