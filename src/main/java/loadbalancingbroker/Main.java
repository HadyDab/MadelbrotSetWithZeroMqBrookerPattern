package loadbalancingbroker;


import loadbalancingbroker.zmqutils.MadelBrotRequest;

public class Main {

	private static final int NBR_WORKERS = 10;

	private static MadelBrotRequest req;


	public static void main(String[] args) throws InterruptedException {
		
//		req = new MadelBrotRequest();
//		req.width = 1024;
//		req.heigth = 980;
//		req.maxIteration = 3000;
//		
//		BrokerThread brokerThread = new BrokerThread();
//		brokerThread.start();
//
//		ClientThread clientThread = new ClientThread(req);
//		clientThread.start();
//
//		WorkerThread[] workerThreads = new WorkerThread[NBR_WORKERS];
//		for (int workerNbr = 0; workerNbr < NBR_WORKERS; workerNbr++) {
//			final WorkerThread workerThread = new WorkerThread();
//			workerThreads[workerNbr] = workerThread;
//			workerThread.start();
//		}
//
//		for (int workerNbr = 0; workerNbr < NBR_WORKERS; workerNbr++) {
//			final WorkerThread workerThread = workerThreads[workerNbr];
//			workerThread.join();
//		}
//
//		clientThread.join();
//		brokerThread.join();

	}
}
