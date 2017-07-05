package AI;

import Assets.Asset;

public interface AI extends Runnable {

	public void determineAction(Asset a);
	
	public void reset();

	public void interrupt();
	
}
