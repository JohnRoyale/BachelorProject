package Main;
import java.io.FileNotFoundException;


public class Main {

	public static void main(String[] args) {
		
		Controller controller=new Controller();
		long ctime=System.currentTimeMillis();
		while(true){
			if(System.currentTimeMillis()-ctime>20){
				ctime=System.currentTimeMillis();
				controller.update();
			}
		}
	}

}
