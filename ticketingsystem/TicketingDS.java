package ticketingsystem;

import java.util.Random;

import ticketingsystem.tools.SectionRange;
import ticketingsystem.tools.bitoniccounter.BitonicCounter;

public class TicketingDS implements TicketingSystem {

	private int routeNum = 5;
	private int coachNum = 8;
	private int seatNum = 100;
	private int stationNum = 10;
	private int threadNum = 16;
	private BitonicCounter counter;
	private static final int fallbackThreshold = 3;
	private Random rand;
	private SectionRange range;

	private void initSide() {
		rand = new Random();
		range = new SectionRange(routeNum, coachNum, seatNum, stationNum);
		counter = new BitonicCounter(threadNum);
	}

	public TicketingDS() {
		initSide();
	}

	public TicketingDS(int routeNum, int coachNum, int seatNum, int stationNum, int threadNum) {
		this.routeNum = routeNum;
		this.coachNum = coachNum;
		this.seatNum = seatNum;
		this.stationNum = stationNum;
		this.threadNum = threadNum;
		initSide();
	}

	public Ticket buyTicket(String passenger, int route, int departure, int arrival) {
		// Randomly choose a ticket
		for (int i = 0; i < fallbackThreshold; ++i) {
			int coach = rand.nextInt(coachNum);
			int seat = rand.nextInt(seatNum);

		}
		return null;
	}

	public int inquiry(int route, int departure, int arrival) {
		return 0;
	}

	public boolean refundTicket(Ticket ticket) {
		return false;
	}

	private Ticket construtTicket(String passenger, int route, int coach, int seat, int departure, int arrival) {
		Ticket t = new Ticket();
		t.tid = counter.getNext();
		t.passenger = passenger;
		t.route = route;
		t.coach = coach;
		t.seat = seat;
		t.departure = departure;
		t.arrival = arrival;
		return t;
	}
}
