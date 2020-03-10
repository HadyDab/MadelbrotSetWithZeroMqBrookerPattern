/**
 * 
 */
package loadbalancingbroker.zmqutils;

import java.util.Random;

/**
 * @author User
 *
 */
public class WorkerDetails {
	private final Random random = new Random();
	private final String readyMessage = "READY";
	private final double speed = random.nextDouble();
}
