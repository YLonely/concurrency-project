package ticketingsystem;

import java.util.concurrent.ConcurrentHashMap;
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

	private ConcurrentHashMap<Long, Ticket> record;

	private void initSide() {
		range = new SectionRange(routeNum, coachNum, seatNum, stationNum);
		counter = new BitonicCounter((int) BitHelper.floor2power(threadNum));
		record = new ConcurrentHashMap<>(16, 0.75f, threadNum);
	}

	public TicketingDS()

	{
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
		if (isIllegal(passenger, route, departure, arrival))
			return null;
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
		if (isIllegal("Inquiry", route, departure, arrival))
			return 0;
		return range.countAvailables(route, departure, arrival);
	}

	public boolean refundTicket(Ticket ticket) {
		long tid = ticket.tid;
		int route = ticket.route;
		int coach = ticket.coach;
		int seat = ticket.seat;
		int departure = ticket.departure;
		int arrival = ticket.arrival;
		if (isIllegal(ticket) || range.isAvailable(route, coach, seat, departure, arrival) || !record.containsKey(tid))
			return false;
		if (!isEqual(ticket, record.get(tid)))
			return false;
		record.remove(tid);
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
		record.put(t.tid, t);
		return t;
	}

	private boolean isEqual(Ticket a, Ticket b) {
		if (a.tid != b.tid || a.passenger != b.passenger || a.route != b.route || a.coach != b.coach || a.seat != b.seat
				|| a.departure != b.departure || a.arrival != b.arrival)
			return false;
		return true;
	}

	private boolean isIllegal(Ticket t) {
		String passenger = t.passenger;
		long tid = t.tid;
		int route = t.route;
		int coach = t.coach;
		int seat = t.seat;
		int departure = t.departure;
		int arrival = t.arrival;
		if (tid < 0 || passenger == null || passenger == "" || coach <= 0 || coach > coachNum || seat <= 0
				|| seat > seatNum || route <= 0 || route > routeNum || departure >= arrival)
			return true;
		return false;
	}

	private boolean isIllegal(String passenger, int route, int departure, int arrival) {
		if (passenger == null || passenger == "" || route <= 0 || route > routeNum || departure <= 0
				|| departure > stationNum || arrival <= 0 || arrival > stationNum || departure >= arrival)
			return true;
		return false;
	}
}
