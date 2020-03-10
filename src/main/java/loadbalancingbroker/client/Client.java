// Doc: http://zguide.zeromq.org/page:all#A-Load-Balancing-Message-Broker
// Source: http://zguide.zeromq.org/java:lbbroker

package loadbalancingbroker.client;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

import com.google.gson.reflect.TypeToken;

import loadbalancingbroker.zmqutils.JsonCreator;
import loadbalancingbroker.zmqutils.MadelBrotRequest;
import loadbalancingbroker.zmqutils.MadelBrotResponce;
import loadbalancingbroker.zmqutils.MadelBrotSet;
import loadbalancingbroker.zmqutils.MadelBrotTask;
import loadbalancingbroker.zmqutils.MadelBrotTaskCollection;
import loadbalancingbroker.zmqutils.ZHelper;

/**
 * Basic request-reply client using REQ socket
 */
public class Client {
	private final String url;
	private boolean started = false;
	private MadelBrotRequest req;
	private MadelBrotResponce responce;
	

	public Client(String url) {
		this.url = url;
	}

	public Client(MadelBrotRequest req, String url) {
		this(url);
		this.req = req;
	}

	public synchronized void start() {
		if (started) {
			throw new IllegalStateException("Client already started.");
		}
		started = true;
		try (Context context = ZMQ.context(1); //
				Socket client = context.socket(SocketType.DEALER)) {
			ZHelper.setId(client); // Set a printable identity
			final String id = new String(client.getIdentity());
			System.out.println("Client thread " + id + " started");

			// connect to front-end
			client.connect(url);

			Poller poller = context.poller(1);
			poller.register(client, Poller.POLLIN);

			int requestNbr = 0;
			int replyNumber = 0;
			
			
			long startTime = System.currentTimeMillis();
			

			List<MadelBrotTaskCollection> madelBrotTaskCollections = createCollectionsOfTask(req);
			for (MadelBrotTaskCollection madelBrotTaskCollection : madelBrotTaskCollections) {
				String requestMessage = JsonCreator.getJsonBuilder().toJson(madelBrotTaskCollection);
				client.send(requestMessage, 0);
				requestNbr++;
			}

			responce = new MadelBrotResponce();
			responce.height = req.heigth;
			responce.width = req.width;
			responce.maxIteration = req.maxIteration;
			while (!Thread.currentThread().isInterrupted()) {
				// Tick once per second, pulling in arriving messages
				for (int centitick = 0; centitick < 100; centitick++) {
					poller.poll(10);
					if (poller.pollin(0)) {
						String reply = client.recvStr();
						Type madelBrotSetListType = new TypeToken<Collection<MadelBrotSet>>() {
						}.getType();
						List<MadelBrotSet> receivedMadelBrotSet = JsonCreator.getJsonBuilder().fromJson(reply,
								madelBrotSetListType);
						responce.madelBrotSets.addAll(receivedMadelBrotSet);
						replyNumber++;
					}
				}

				if (requestNbr == replyNumber) {
					break;
				}

			}

//			try {
//				createAndSaveMadelBrotImage(responce);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			

			long totalTimeElepsed = System.currentTimeMillis() - startTime;

			System.out.println("MadelBrot has been created");
			System.out.println("Client thread " + id + " terminated");
			
			System.out.println("Total time elepsed was  " + totalTimeElepsed + " milliseconds");
		} finally {
			started = false;
		}
		
		
		
	}

	public static List<MadelBrotTaskCollection> createCollectionsOfTask(MadelBrotRequest req) {
		List<MadelBrotTaskCollection> madelBrotTaskCollections = new ArrayList<>();

		int width = req.width;
		int height = req.heigth;
		int maxInteration = req.maxIteration;

		List<MadelBrotTask> madelBrotTasks = new ArrayList<>();
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				double c_re = (col - width / 2) * 4.0 / width;
				double c_im = (row - height / 2) * 4.0 / height;
				MadelBrotTask task = new MadelBrotTask();
				task.creal = c_re;
				task.cimg = c_im;
				task.col = col;
				task.row = row;
				task.maxiterarion = maxInteration;
				madelBrotTasks.add(task);
			}
		}

		int partitionSize = 20000;
		for (int i = 0; i < madelBrotTasks.size(); i += partitionSize) {
			MadelBrotTaskCollection madelBrotTaskCollection = new MadelBrotTaskCollection();
			madelBrotTaskCollection.taskNr = i;
			madelBrotTaskCollection.madelBrotTasks
					.addAll(madelBrotTasks.subList(i, Math.min(i + partitionSize, madelBrotTasks.size())));
			madelBrotTaskCollections.add(madelBrotTaskCollection);
		}

		return madelBrotTaskCollections;
	}

	private static void createAndSaveMadelBrotImage(MadelBrotResponce madelBrotResponce) throws IOException {
		if (madelBrotResponce == null) {
			System.out.println("No responce");
			return;
		}


		BufferedImage madelBrotImage = new BufferedImage(madelBrotResponce.width, madelBrotResponce.height, BufferedImage.TYPE_INT_RGB);
		int[] colors = new int[madelBrotResponce.maxIteration];
		for (int i = 0; i < madelBrotResponce.maxIteration; i++) {
			colors[i] = Color.HSBtoRGB(i / 256f * 20, 1, i / (i + 40f) * 80);
		}

		for (MadelBrotSet ms : madelBrotResponce.madelBrotSets) {
			if (ms.iteration < madelBrotResponce.maxIteration) {
				madelBrotImage.setRGB(ms.col, ms.row, colors[ms.iteration]);
			} else {
				madelBrotImage.setRGB(ms.col, ms.row, Color.YELLOW.getRGB());
			}
		}

		ImageIO.write(madelBrotImage, "png", new File("mandelbrot.png"));
	}
	
	
	public MadelBrotResponce getResponce() {
		return this.responce;
	}

}
