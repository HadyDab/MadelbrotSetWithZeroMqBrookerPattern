/**
 * 
 */
package loadbalancingbroker;

import loadbalancingbroker.client.Client;
import loadbalancingbroker.zmqutils.MadelBrotRequest;

/**
 * @author hamzahassan
 *
 */
public class ClientThread implements Runnable{
	
	private static final String FRONTEND_URL = "tcp://localhost:5555";
	
	private MadelBrotRequest request;
	
	
	public ClientThread(MadelBrotRequest request) {
		this.request = request;
	}
	
	
	public void run() {
		final Client c = new Client(request, FRONTEND_URL);
		c.start();
	}

}
