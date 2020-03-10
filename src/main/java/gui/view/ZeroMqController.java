/**
 * 
 */
package gui.view;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

import gui.main.ServiceRequester2;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import loadbalancingbroker.WorkerThread;
import loadbalancingbroker.zmqutils.MadelBrotRequest;

/**
 * @author hamzahassan
 *
 */
public class ZeroMqController {
	
	
	@FXML
	private Spinner<Integer>  workerSpinner = new Spinner<Integer>();
	
	@FXML 
	private TextField widthtxt = new TextField();
	
	@FXML 
	private TextField heigthtxt= new TextField();
	
	@FXML 
	private TextField interationtxt = new TextField();
	
	@FXML
	private ImageView madelBrotImageView = new ImageView();
	
	@FXML
	private ScrollPane scrolllPane = new ScrollPane();
	
	
	private Pane imageViewerPane;
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	double zoomFactor = 0;
	
	
	public ZeroMqController() {
//		this.mainApp = mainApp;
		initialize();
	}
	
	
	public void initialize() {
		
		SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 30, 1);
		workerSpinner.setValueFactory(valueFactory);
		
		
		UnaryOperator<Change> filter = change ->{
			String text = change.getText();

		    if (text.matches("[0-9]*")) {
		        return change;
		    }

		    return null;
		};
		
		
		TextFormatter<String> widthFormatter = new TextFormatter<>(filter);
		TextFormatter<String> heigthFormatter = new TextFormatter<>(filter);
		TextFormatter<String> iterationFormatter = new TextFormatter<>(filter);
		
		widthtxt.setTextFormatter(widthFormatter);		
		heigthtxt.setTextFormatter(heigthFormatter);		
		interationtxt.setTextFormatter(iterationFormatter);
		
		imageViewerPane = new Pane();
		scrolllPane.setContent(imageViewerPane);
		
	}
	
	
	
	@FXML
	private void onGenerateMadelBrot(ActionEvent event) {
		
		// create the request 
		MadelBrotRequest request = new MadelBrotRequest();
		try {
			request.width = Integer.valueOf(widthtxt.getText());
		}catch (Exception e) {
			showError("Please set the width of the image", "Width has not been set", AlertType.ERROR);
			return;
		}
		
		try {
			request.heigth = Integer.valueOf(heigthtxt.getText());
		}catch (Exception e) {
			showError("Please set the height of the image", "Height has not been set", AlertType.ERROR);
			return;
		}
		
		try {
			request.maxIteration = Integer.valueOf(interationtxt.getText());
		}catch (Exception e) {
			showError("Please set the number of iteration to make for the madelbrot", "has not been set", AlertType.ERROR);
			return;
		}
		
		createMadelBrotImage(request);

	}

	
	private void createMadelBrotImage(MadelBrotRequest request) {
		
		int numberOfWorkers = workerSpinner.getValue();
		// Create workers base on number of workers
		for(int i = 0; i < numberOfWorkers; i++) {
			WorkerThread worker = new WorkerThread();
			executor.execute(worker);
		}

		ServiceRequester2 requester = new ServiceRequester2(request);
		
		Future<Canvas>  madelbrotImage = executor.submit(requester);
		
		while(!madelbrotImage.isDone()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		

		try {
			if(madelbrotImage.isDone()) {
				Canvas result = madelbrotImage.get(100, TimeUnit.MILLISECONDS);
				
				ContextMenu menu = new ContextMenu();
				
				
				
				MenuItem zoomIn = new MenuItem("zoom in");
				zoomIn.setOnAction(ActionEvent  -> {
					zoomFactor++;
					
					result.getGraphicsContext2D().scale(zoomFactor, zoomFactor);
					
				});
				
				
				MenuItem zoomOut = new MenuItem("zoom out");
				zoomIn.setOnAction(ActionEvent  -> {
					zoomFactor--;
					if(zoomFactor < 0){
						zoomFactor = 0;
					}
					
					result.getGraphicsContext2D().scale(zoomFactor, zoomFactor);
					
				});
				
				menu.getItems().add(zoomIn);
				menu.getItems().add(zoomOut);
				result.setOnContextMenuRequested(ctxmenu -> {
					menu.show(result, ctxmenu.getScreenX(), ctxmenu.getScreenY());
				});

				imageViewerPane.getChildren().clear();
				imageViewerPane.getChildren().add(result);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}

	private void showError(String errorMessage,String title, AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setContentText(errorMessage);
		alert.showAndWait();
	}
	
	
	

}
