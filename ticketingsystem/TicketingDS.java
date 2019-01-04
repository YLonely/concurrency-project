package ticketingsystem;

import java.util.List;
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
	private static final int fallbackThreshold = 10;
	private SectionRange range;

	private ConcurrentHashMap<Long, Ticket> record;

	private void initSide() {
		range = new SectionRange(routeNum, coachNum, seatNum, stationNum, threadNum);
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
		// int coach = 0, seat = 0;
		InnerTicket it = new InnerTicket(route, 0, 0, departure, arrival);
		for (int i = 0; i < fallbackThreshold; ++i) {
			it.coach = rand.nextInt(coachNum) + 1;
			it.seat = rand.nextInt(seatNum) + 1;
			if (tryBuyTicket(it))
				return construtTicket(passenger, it);
		}
		List<InnerTicket> mayAvailables = range.locateAvailables(route, departure, arrival);
		for (InnerTicket t : mayAvailables)
			if (tryBuyTicket(t))
				return construtTicket(passenger, t);
		return null;
	}

	public int inquiry(int route, int departure, int arrival) {
		if (isIllegal("Inquiry", route, departure, arrival))
			return 0;
		return range.countAvailables(route, departure, arrival);
	}

	public boolean refundTicket(Ticket ticket) {
		if (ticket == null)
			return false;
		long tid = ticket.tid;
		InnerTicket it = new InnerTicket(ticket);
		if (isIllegal(ticket) || range.isAvailable(it) || !record.containsKey(tid))
			return false;
		if (!isEqual(ticket, record.get(tid)))
			return false;
		if (record.remove(tid) == null)
			return false;
		range.free(it);
		return true;
	}

	private boolean tryBuyTicket(InnerTicket it) {
		if (range.isAvailable(it)) {
			try {
				range.lock(it);
				if (!range.isAvailable(it))
					return false;
				range.occupy(it);
				return true;
			} finally {
				range.unlock(it);
			}
		}
		return false;
	}

	private Ticket construtTicket(String passenger, InnerTicket it) {
		Ticket t = it.toTicket(counter.getNext(), passenger);
		record.put(t.tid, t);
		return t;
	}

	private boolean isEqual(Ticket a, Ticket b) {
		if (a.tid != b.tid || a.passenger == null || b.passenger == null || !a.passenger.equals(b.passenger)
				|| a.route != b.route || a.coach != b.coach || a.seat != b.seat || a.departure != b.departure
				|| a.arrival != b.arrival)
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
		if (tid < 0 || coach <= 0 || coach > coachNum || seat <= 0 || seat > seatNum
				|| isIllegal(passenger, route, departure, arrival))
			return true;
		return false;
	}

	private boolean isIllegal(String passenger, int route, int departure, int arrival) {
		if (passenger == null || passenger.equals("") || route <= 0 || route > routeNum || departure <= 0
				|| departure > stationNum || arrival <= 0 || arrival > stationNum || departure >= arrival)
			return true;
		return false;
	}
}
