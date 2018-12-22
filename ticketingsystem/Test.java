package ticketingsystem;

import java.util.ArrayList;
import java.util.Random;

public class Test {
	private final static int ROUTE_NUM = 5;
	private final static int COACH_NUM = 8;
	private final static int SEAT_NUM = 100;
	private final static int STATION_NUM = 10;

	private final static int TEST_NUM = 1000000;
	private final static int refund = 10;
	private final static int buy = 40;
	private final static int query = 100;

	static String passengerName() {
		Random rand = new Random();
		long uid = rand.nextInt(TEST_NUM);
		return "passenger" + uid;
	}

	public static void main(String[] args) throws InterruptedException {
		final int[] threadNums = { 4, 8, 16, 32, 64, 128 };
		int p;
		for (p = 0; p < 6; ++p) {
			final TicketingDS tds = new TicketingDS(ROUTE_NUM, COACH_NUM, SEAT_NUM, STATION_NUM, threadNums[p]);
			Thread[] threads = new Thread[threadNums[p]];
			for (int i = 0; i < threadNums[p]; i++) {
				threads[i] = new Thread(new Runnable() {
					public void run() {
						Random rand = new Random();
						Ticket ticket = new Ticket();
						ArrayList<Ticket> soldTicket = new ArrayList<Ticket>();
						for (int i = 0; i < TEST_NUM; i++) {
							int sel = rand.nextInt(query);
							if (0 <= sel && sel < refund && soldTicket.size() > 0) { // refund ticket 0-10
								int select = rand.nextInt(soldTicket.size());
								if ((ticket = soldTicket.remove(select)) != null) {
									if (tds.refundTicket(ticket)) {
										;
									} else {
										System.out.println("ErrOfRefund1");
									}
								} else {
									System.out.println("ErrOfRefund2");
								}
							} else if (refund <= sel && sel < buy) { // buy ticket 10-40
								String passenger = passengerName();
								int route = rand.nextInt(ROUTE_NUM) + 1;
								int departure = rand.nextInt(STATION_NUM - 1) + 1;
								int arrival = departure + rand.nextInt(STATION_NUM - departure) + 1;
								if ((ticket = tds.buyTicket(passenger, route, departure, arrival)) != null) {
									soldTicket.add(ticket);
								} else {
									;
								}
							} else if (buy <= sel && sel < query) { // inquiry ticket 40-100
								int route = rand.nextInt(ROUTE_NUM) + 1;
								int departure = rand.nextInt(STATION_NUM - 1) + 1;
								int arrival = departure + rand.nextInt(STATION_NUM - departure) + 1;
								int leftTicket = tds.inquiry(route, departure, arrival);
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
			long time = System.currentTimeMillis() - start; // 单位是ms
			long t = (long) (threadNums[p] * TEST_NUM / (double) (time)) * 1000; // 1000是从ms转换为s
			System.out.println(
					String.format("ThreadNum: %d TotalTime(ms): %d ThroughOut(t/s): %d", threadNums[p], time, t));

		}
	}
}
