package ticketingsystem;

public class Test {

	public static void main(String[] args) throws InterruptedException {

		// final TicketingDS tds = new TicketingDS(routenum, coachnum, seatnum,
		// stationnum, threadnum);
		// ToDo
		try {

			Bitonic b = new Bitonic(4);
			for (int i = 0; i < 100; ++i)
				System.out.println(b.traverse(i % 4));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
