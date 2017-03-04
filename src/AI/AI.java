package AI;

import Assets.Asset;
import Assets.Unit;
import Main.Model;

public interface AI extends Runnable {
	
	public void determineAction(Asset a);
	
}
