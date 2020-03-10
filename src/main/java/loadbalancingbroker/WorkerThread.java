/**
 * 
 */
package loadbalancingbroker;

import loadbalancingbroker.worker.Worker;

/**
 * @author hamzahassan
 *
 */
public class WorkerThread implements Runnable{
	private static final String SERVER_URL = "tcp://localhost:6666";
	
	
	@Override
	public void run() {
		final Worker w = new Worker(SERVER_URL);
		w.start();
	}

}
