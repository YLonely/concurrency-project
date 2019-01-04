package ticketingsystem;

public class InnerTicket {
    public int route, coach, seat, departure, arrival;

    public InnerTicket(int route, int coach, int seat, int departure, int arrival) {
        this.route = route;
        this.coach = coach;
        this.seat = seat;
        this.departure = departure;
        this.arrival = arrival;
    }

    public InnerTicket(Ticket t) {
        this.route = t.route;
        this.coach = t.coach;
        this.seat = t.seat;
        this.departure = t.departure;
        this.arrival = t.arrival;
    }

    public InnerTicket(int route, int coach, int seat) {
        this.route = route;
        this.coach = coach;
        this.seat = seat;
    }

    public Ticket toTicket(long tid, String passenger) {
        Ticket t = new Ticket();
        t.tid = tid;
        t.passenger = passenger;
        t.route = route;
        t.coach = coach;
        t.seat = seat;
        t.departure = departure;
        t.arrival = arrival;
        return t;
    }
}