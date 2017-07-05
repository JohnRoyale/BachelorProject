package Main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.JOptionPane;

public class MainTest {
	final static int epochs = 40;
	final static int inc = 2;
	static double total=0;
	static int[] count=new int[4];

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub

		String[] opponent = { "Random", "Classic" };
		String fileName = JOptionPane.showInputDialog("Input id of network to test");
		long ctime = System.currentTimeMillis();
		String input = (String) JOptionPane.showInputDialog(null, "Choose the opponent", "Opponent choice",
				JOptionPane.QUESTION_MESSAGE, null, opponent, opponent[0]);
		boolean enemy =input.equals(opponent[0]);
		Controller controller = new Controller(false, true, false, fileName,enemy);

		int prefix = 0;
		int postfix = 0;
		String subname = prefix + fileName;
		String name = subname + postfix;
		PrintStream stdout = System.out;
		PrintStream out = new PrintStream(new FileOutputStream(fileName + "_performance.csv"));
		//System.setOut(out);
		System.setOut(out);
		System.out.println("trial,epoch,winRate,tieRate,lossRate,Defend,Defensive invade, Evasive invade, Hunt");
		System.setOut(stdout);

		for (int t = prefix; t < 1000; t++) {

			while (controller.load(name)) {
				double equal = 0;
				double j = 0;
				int[] tCount;
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
					tCount=controller.count();
					count[0]+=tCount[0];
					count[1]+=tCount[1];
					count[2]+=tCount[2];
					count[3]+=tCount[3];
					controller.reset();
					//System.out.print(i);
				}
				total=count[0]+count[1]+count[2]+count[3];
				System.setOut(out);
				System.out.println(prefix+","+postfix+","+j/epochs+","+equal/epochs+","+(epochs-j-equal)/epochs+","+count[0]/total+","+count[1]/total+","+count[2]/total+","+count[3]/total);
				System.setOut(stdout);
				count=new int[4];
				total=0;
//				System.out.println("After " + postfix + " epochs of training Neural AI won " + j + " games, drawed "
//						+ equal + " games out of " + epochs + " games");
				// System.setOut(stdout);
				postfix += inc;
				if(postfix>26){
					postfix=1000;
				}
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
