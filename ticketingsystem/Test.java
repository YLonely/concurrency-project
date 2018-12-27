package ticketingsystem;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
	private final static int ROUTE_NUM = 5;
	private final static int COACH_NUM = 8;
	private final static int SEAT_NUM = 100;
	private final static int STATION_NUM = 10;

	private final static int TEST_NUM = 10000;
	private final static int refund = 10;
	private final static int buy = 40;
	private final static int query = 100;
	private final static int thread = 64;
	private final static long[] buyTicketTime = new long[thread];
	private final static long[] refundTime = new long[thread];
	private final static long[] inquiryTime = new long[thread];

	private final static long[] buyTotal = new long[thread];
	private final static long[] refundTotal = new long[thread];
	private final static long[] inquiryTotal = new long[thread];

	private final static AtomicInteger threadId = new AtomicInteger(0);

	static String passengerName() {
		Random rand = new Random();
		long uid = rand.nextInt(TEST_NUM);
		return "passenger" + uid;
	}

	public static void main(String[] args) throws InterruptedException {
		final int[] threadNums = { 4, 8, 16, 32, 64 };
		int p;
		for (p = 0; p < threadNums.length; ++p) {
			final TicketingDS tds = new TicketingDS(ROUTE_NUM, COACH_NUM, SEAT_NUM, STATION_NUM, threadNums[p]);
			Thread[] threads = new Thread[threadNums[p]];
			for (int i = 0; i < threadNums[p]; i++) {
				threads[i] = new Thread(new Runnable() {
					public void run() {
						Random rand = new Random();
						Ticket ticket = new Ticket();
						int id = threadId.getAndIncrement();
						ArrayList<Ticket> soldTicket = new ArrayList<>();
						for (int i = 0; i < TEST_NUM; i++) {
							int sel = rand.nextInt(query);
							if (0 <= sel && sel < refund && soldTicket.size() > 0) { // refund ticket 0-10
								int select = rand.nextInt(soldTicket.size());
								if ((ticket = soldTicket.remove(select)) != null) {
									long s = System.nanoTime();
									tds.refundTicket(ticket);
									long e = System.nanoTime();
									refundTime[id] += e - s;
									refundTotal[id] += 1;
								} else {
									System.out.println("ErrOfRefund2");
								}
							} else if (refund <= sel && sel < buy) { // buy ticket 10-40
								String passenger = passengerName();
								int route = rand.nextInt(ROUTE_NUM) + 1;
								int departure = rand.nextInt(STATION_NUM - 1) + 1;
								int arrival = departure + rand.nextInt(STATION_NUM - departure) + 1;
								long s = System.nanoTime();
								ticket = tds.buyTicket(passenger, route, departure, arrival);
								long e = System.nanoTime();
								buyTicketTime[id] += e - s;
								buyTotal[id] += 1;
								if (ticket != null) {
									soldTicket.add(ticket);
								}
							} else if (buy <= sel && sel < query) { // inquiry ticket 40-100
								int route = rand.nextInt(ROUTE_NUM) + 1;
								int departure = rand.nextInt(STATION_NUM - 1) + 1;
								int arrival = departure + rand.nextInt(STATION_NUM - departure) + 1;
								long s = System.nanoTime();
								tds.inquiry(route, departure, arrival);
								long e = System.nanoTime();
								inquiryTime[id] += e - s;
								inquiryTotal[id] += 1;
							}
						}
					}
				});
			}
			long start = System.currentTimeMillis();
			for (int i = 0; i < threadNums[p]; ++i)
				threads[i].start();

			for (int i = 0; i < threadNums[p]; i++) {
				threads[i].join();
			}
			long end = System.currentTimeMillis();
			long buyTotalTime = calculateTotal(buyTicketTime, threadNums[p]);
			long refundTotalTime = calculateTotal(refundTime, threadNums[p]);
			long inquiryTotalTime = calculateTotal(inquiryTime, threadNums[p]);

			double bTotal = (double) calculateTotal(buyTotal, threadNums[p]);
			double rTotal = (double) calculateTotal(refundTotal, threadNums[p]);
			double iTotal = (double) calculateTotal(inquiryTotal, threadNums[p]);

			long buyAvgTime = (long) (buyTotalTime / bTotal);
			long refundAvgTime = (long) (refundTotalTime / rTotal);
			long inquiryAvgTime = (long) (inquiryTotalTime / iTotal);

			long time = end - start;

			long t = (long) (threadNums[p] * TEST_NUM / (double) time) * 1000; // 1000是从ms转换为s
			System.out.println(String.format(
					"ThreadNum: %d BuyAvgTime(ns): %d RefundAvgTime(ns): %d InquiryAvgTime(ns): %d ThroughOut(t/s): %d",
					threadNums[p], buyAvgTime, refundAvgTime, inquiryAvgTime, t));
			clear();
		}
	}

	private static long calculateTotal(long[] array, int threadNums) {
		long res = 0;
		for (int i = 0; i < threadNums; ++i)
			res += array[i];
		return res;
	}

	private static void clear() {
		threadId.set(0);
		long[][] arrays = { buyTicketTime, refundTime, inquiryTime, buyTotal, refundTotal, inquiryTotal };
		for (int i = 0; i < arrays.length; ++i)
			for (int j = 0; j < arrays[i].length; ++j)
				arrays[i][j] = 0;
	}

}
