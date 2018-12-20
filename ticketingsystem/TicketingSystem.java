package ticketingsystem;

class Ticket{
	long tid;
	String passenger;
	int route;
	int coach;
	int seat;
	int departure;
	int arrival;
}


public interface TicketingSystem {
	Ticket buyTicket(String passenger, int route, int departure, int arrival);
	int inquiry(int route, int departure, int arrival);
	boolean refundTicket(Ticket ticket);
}
