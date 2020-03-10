/**
 * 
 */
package loadbalancingbroker;

import loadbalancingbroker.broker.LBBroker;

/**
 * @author hamzahassan
 *
 */
public class BrokerThread implements Runnable {
	
	private static final String FRONTEND_URL = "tcp://localhost:5555";
	private static final String BACKEND_URL = "tcp://localhost:6666";

	
	
	
	@Override
	public void run() {
		
		while (!Thread.currentThread().isInterrupted()) {
			final LBBroker b = new LBBroker(FRONTEND_URL, BACKEND_URL);
			b.start();
			
		}

	}
	
	

}
