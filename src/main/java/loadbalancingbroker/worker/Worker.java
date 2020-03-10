// Doc: http://zguide.zeromq.org/page:all#A-Load-Balancing-Message-Broker
// Source: http://zguide.zeromq.org/java:lbbroker

package loadbalancingbroker.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import loadbalancingbroker.zmqutils.Complex;
import loadbalancingbroker.zmqutils.JsonCreator;
import loadbalancingbroker.zmqutils.MadelBrotSet;
import loadbalancingbroker.zmqutils.MadelBrotTask;
import loadbalancingbroker.zmqutils.MadelBrotTaskCollection;
import loadbalancingbroker.zmqutils.ZHelper;

public class Worker {
	private final String url;
	private boolean started = false;

	public Worker(String url) {
		this.url = url;
	}

	public synchronized void start() {
		if (started) {
			throw new IllegalStateException("Worker already started.");
			
		}
		started = true;
		try (Context context = ZMQ.context(1); //
				Socket worker = context.socket(SocketType.REQ)) {
			// Prepare our context and sockets
			ZHelper.setId(worker); // Set a printable identity
			final String id = new String(worker.getIdentity());
			System.out.println("Worker thread " + id + " started");

			// connect to back-end
			worker.connect(url);

			// Tell back-end we're ready for work

			final Random ran = new Random();
			final double workerSpeedBench = ran.nextDouble();
			String workerSpeed = String.valueOf(workerSpeedBench);

			worker.sendMore(workerSpeed);
			worker.send("READY");

			while (!Thread.currentThread().isInterrupted()) {
				String address = worker.recvStr();
				String empty = worker.recvStr();
				assert (empty.length() == 0);

				// Get request, send reply
				String request = worker.recvStr();
				MadelBrotTaskCollection madelBrotTaskList = JsonCreator.getJsonBuilder().fromJson(request,
						MadelBrotTaskCollection.class);

				System.out.println("Worker " + id + " Working on " + madelBrotTaskList.taskNr);
				List<MadelBrotSet> madelBrotSets = getMadelBrotSets(madelBrotTaskList.madelBrotTasks);
				try {
					Thread.sleep(50 + ran.nextInt(100 * 2));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				String replyMessage = JsonCreator.getJsonBuilder().toJson(madelBrotSets);
				worker.sendMore(workerSpeed);
				worker.sendMore(address);
				worker.sendMore("");
				worker.send(replyMessage);
			}
		} finally {
			started = false;
		}
	}

	public static List<MadelBrotSet> getMadelBrotSets(List<MadelBrotTask> tasks) {
		List<MadelBrotSet> madelBrotSets = new ArrayList<>();
		for (MadelBrotTask task : tasks) {
			MadelBrotSet set = createMadelBrotSetFromTask(task);
			madelBrotSets.add(set);
		}
		return madelBrotSets;
	}

	private static MadelBrotSet createMadelBrotSetFromTask(MadelBrotTask task) {
		Complex c = new Complex(task.creal, task.cimg);
		int max = task.maxiterarion;
		Complex z0 = new Complex();
		int iterations = 0;
		while (z0.abs() < 4 && iterations < max) {
			Complex ztemp = Complex.add(z0.squared(z0), c);
			z0 = ztemp;
			iterations++;
		}
		MadelBrotSet set = new MadelBrotSet();
		set.col = task.col;
		set.row = task.row;
		set.iteration = iterations;
		return set;
	}
}
