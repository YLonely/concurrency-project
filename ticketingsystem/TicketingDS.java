package ticketingsystem;

public class TicketingDS implements TicketingSystem {

	// ToDo
	private int routeNum = 5;
	private int coachNum = 8;
	private int seatNum = 100;
	private int stationNum = 10;
	private int threadNum = 16;

	public TicketingDS(int routeNum, int coachNum, int seatNum, int stationNum, int threadNum) {
		this.routeNum = routeNum;
		this.coachNum = coachNum;
		this.seatNum = seatNum;
		this.stationNum = stationNum;
		this.threadNum = threadNum;
	}

	public Ticket buyTicket(String passenger, int route, int departure, int arrival) {

	}

	public int inquiry(int route, int departure, int arrival) {

	}

	public boolean refundTicket(Ticket ticket) {

	}
}
