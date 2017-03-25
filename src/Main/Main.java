package Main;
import java.io.FileNotFoundException;


public class Main {

	public static void main(String[] args) {
		
		Controller controller=new Controller();
		long ctime=System.currentTimeMillis();
		while(!controller.gameOver()){
			if(System.currentTimeMillis()-ctime>20){
				ctime=System.currentTimeMillis();
				controller.update();
			}
		}
	}

}
