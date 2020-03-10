// Doc: http://zguide.zeromq.org/page:all#A-Load-Balancing-Message-Broker
// Source: http://zguide.zeromq.org/java:lbbroker

package loadbalancingbroker.broker;

import java.util.SortedMap;
import java.util.TreeMap;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

public class LBBroker {
	private final String fronendURL;
	private final String backendURL;
	private boolean started = false;

	public LBBroker(String frontendURL, String backendURL) {
		this.fronendURL = frontendURL;
		this.backendURL = backendURL;
	}

	/**
	 * This is the broker's main task. It routes messages between clients and
	 * workers. Workers signal READY when they start; after that we treat them as
	 * ready when they reply with a response back to a client. The load-balancing
	 * data structure is just a queue of next available workers.
	 */
	public synchronized void start() {
		if (started) {
			throw new IllegalStateException("Broker already started.");
		}
		started = true;
		try (Context context = ZMQ.context(1);
				// Prepare our context and sockets
				Socket frontend = context.socket(SocketType.ROUTER);
				Socket backend = context.socket(SocketType.ROUTER)) {
			frontend.bind(fronendURL);
			backend.bind(backendURL);

			// Here is the main loop for the least-recently-used queue. It has two sockets:
			// - a frontend for clients and
			// - a backend for workers.
			//
			// It polls the backend in all cases, and polls the frontend only when there
			// are one or more workers ready. This is a neat way to use 0MQ's own
			// queues to hold messages if we're not ready to process them yet.
			//
			// When we get a client request, we pop the next available worker from the
			// queue of available workers, and send the request to this worker. The request
			// message includes the originating client identity.
			// When a worker replies, we re-queue that worker, and we forward the reply
			// to the original client, using the reply envelope.

			// Queue of available workers
			// Queue<String> workerQueue = new LinkedList<String>();
			// SortedSet<String> worketSpeed = new TreeSet<>();
			SortedMap<Double, String> workerQueueBasedOnSpeed = new TreeMap<>();

			while (!Thread.currentThread().isInterrupted()) {
				// Initialize poll set
				Poller items = context.poller(2);
				// Always poll for worker activity on backend
				int backendPollerId = items.register(backend, Poller.POLLIN);
				// Poll front-end only if we have available workers
				int frontendPollerId = -1;
				if (workerQueueBasedOnSpeed.size() > 0)
					frontendPollerId = items.register(frontend, Poller.POLLIN);

				if (items.poll() < 0)
					break;

				// handle worker activity on backend
				if (items.pollin(backendPollerId)) {
					final String workerId;
					{
						// queue the worker's address for last recently used (LRU) routing
						workerId = backend.recvStr();
						// workerQueue.add(workerId);
					}

					{
						// second frame is always empty
						final String empty = backend.recvStr();
						assert (empty.length() == 0);
					}

					{

						// third frame is "WorkerSpeed"
						final String workerSd = backend.recvStr();
						double workerSpeed = Double.valueOf(workerSd);

						// queue the worker's address for fastest working routing
						workerQueueBasedOnSpeed.put(workerSpeed, workerId);

						// forth frame is "READY" or else a client reply ID
						final String clientId = backend.recvStr();

						// if client reply, send rest of message back to frontend
						if (!clientId.equals("READY")) {
							{
								// fifth frame is empty
								final String empty = backend.recvStr();
								assert (empty.length() == 0);
							}

							// Six frame is worker's reply to be passed on to client
							final String reply = backend.recvStr();

							// pass worker's reply on the client with given ID
							// The empty delimiter is removed
							// because we are using Dealer socket in client
							frontend.sendMore(clientId);
							// frontend.sendMore("");
							frontend.send(reply);
						}
					}
				}

				if (items.pollin(frontendPollerId)) {
					// Now get next client request and route it to LRU worker;
					// Client request is [address][empty][request]

					final String clientId = frontend.recvStr();

					// Second frame is always the Request
					// because we are using Dealer Socket
					final String request = frontend.recvStr();
					assert (request.length() != 0);

					// get next available worker
					// final String workerId = workerQueue.poll();

					// get next available worker based on speed
					double lowSpeed = workerQueueBasedOnSpeed.firstKey();
					final String workerId = workerQueueBasedOnSpeed.get(lowSpeed);

					// pass client's request on this worker
					backend.sendMore(workerId);
					backend.sendMore("");
					backend.sendMore(clientId);
					backend.sendMore("");
					backend.send(request);

					// remove worker from queue
					workerQueueBasedOnSpeed.remove(lowSpeed);
				}
			}
		} finally {
			started = false;
		}
	}
}
