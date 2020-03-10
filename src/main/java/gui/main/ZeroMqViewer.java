/**
 * 
 */
package gui.main;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gui.view.ZeroMqController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import loadbalancingbroker.BrokerThread;

/**
 * @author hamzahassan
 *
 */
public class ZeroMqViewer extends Application{


	private ExecutorService brokerExecutor = Executors.newSingleThreadExecutor();
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		try {
			
			

			URL filepath = getClass().getResource("/gui/view/mainview.fxml");
			
			
			FXMLLoader loader = new FXMLLoader(filepath);
			ZeroMqController controler = new ZeroMqController();
			loader.setController(controler);
			
			BorderPane root = (BorderPane) loader.load();
			Scene scene = new Scene(root);
			
			scene.getStylesheets().add(getClass().getResource("/gui/main/view.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setOnCloseRequest(e -> handleExit());
			primaryStage.show();
			
			
			// Start a broker on System start as a service
			
			BrokerThread broker = new BrokerThread();
			brokerExecutor.execute(broker);
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	private void handleExit(){
	//handle disposing of all thread
	Platform.exit();
	System.exit(0);

	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
