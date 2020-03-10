/**
 * 
 */
package gui.main;

import java.util.concurrent.Callable;

import javafx.scene.canvas.Canvas;
import loadbalancingbroker.MadelBrotService;
import loadbalancingbroker.client.Client;
import loadbalancingbroker.zmqutils.MadelBrotRequest;

/**
 * @author hamzahassan
 *
 */
public class ServiceRequester2 implements Callable<Canvas> {
	
	private static final String FRONTEND_URL = "tcp://localhost:5555";

	private MadelBrotRequest request;
	
	public ServiceRequester2(MadelBrotRequest request) {
		this.request = request;
	}
	

	@Override
	public Canvas call() throws Exception {
		Client client = new Client(request, FRONTEND_URL);
		client.start();
		Canvas canvas = MadelBrotService.getMadelBrotCanvas(client.getResponce());
		return canvas;
	}

}
