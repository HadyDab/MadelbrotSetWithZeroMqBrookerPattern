package gui.main;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import loadbalancingbroker.MadelBrotService;
import loadbalancingbroker.client.Client;
import loadbalancingbroker.zmqutils.MadelBrotRequest;

import java.util.concurrent.Callable;

public class ServiceRequesterImage  implements Callable<Image> {

    private static final String FRONTEND_URL = "tcp://localhost:5555";

    private MadelBrotRequest request;

    public ServiceRequesterImage(MadelBrotRequest request) {
        this.request = request;
    }


    @Override
    public Image call() throws Exception {
        Client client = new Client(request, FRONTEND_URL);
        client.start();
        Image image = MadelBrotService.getMadelBrotImage(client.getResponce());
        return image;
    }
}
