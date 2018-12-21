package ticketingsystem;

import java.util.concurrent.ThreadLocalRandom;

import ticketingsystem.tools.BitHelper;
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
	private SectionRange range;

	private void initSide() {
		range = new SectionRange(routeNum, coachNum, seatNum, stationNum);
		counter = new BitonicCounter((int) BitHelper.floor2power(threadNum));
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
		ThreadLocalRandom rand = ThreadLocalRandom.current();
		int coach = 0, seat = 0;
		for (int i = 0; i < fallbackThreshold; ++i) {
			coach = rand.nextInt(coachNum) + 1;
			seat = rand.nextInt(seatNum) + 1;
			if (tryBuyTicket(route, coach, seat, departure, arrival))
				return construtTicket(passenger, route, coach, seat, departure, arrival);
		}
		int seatTotal = coachNum * seatNum - 1;
		coach -= 1;
		seat -= 1;
		for (int i = 0; i < seatTotal; ++i) {
			seat = (seat + 1) % seatNum;
			if (seat == 0)
				coach = (coach + 1) % coachNum;
			if (tryBuyTicket(route, coach + 1, seat + 1, departure, arrival))
				return construtTicket(passenger, route, coach + 1, seat + 1, departure, arrival);
		}
		return null;
	}

	public int inquiry(int route, int departure, int arrival) {
		return range.countAvailables(route, departure, arrival);
	}

	public boolean refundTicket(Ticket ticket) {
		int route = ticket.route;
		int coach = ticket.coach;
		int seat = ticket.seat;
		int departure = ticket.departure;
		int arrival = ticket.arrival;
		if (coach <= 0 || coach > coachNum || seat <= 0 || seat > seatNum || route <= 0 || route > routeNum
				|| departure >= arrival || range.isAvailable(route, coach, seat, departure, arrival))
			return false;
		range.free(route, coach, seat, departure, arrival);
		return true;
	}

	private boolean tryBuyTicket(int route, int coach, int seat, int departure, int arrival) {
		if (range.isAvailable(route, coach, seat, departure, arrival)) {
			try {
				range.lock(route, coach, seat, departure, arrival);
				if (!range.isAvailable(route, coach, seat, departure, arrival))
					return false;
				range.occupy(route, coach, seat, departure, arrival);
				return true;
			} finally {
				range.unlock(route, coach, seat, departure, arrival);
			}
		}
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
