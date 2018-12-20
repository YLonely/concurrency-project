package ticketingsystem.tools;

public class Section {
    private int routeTotal;
    private int coachTotal;
    private int seatTotal;
    private Seat[] sectionSeats;
    private BitMap bitMap;

    public Section(int routeTotal, int coachTotal, int seatTotal) {
        this.coachTotal = coachTotal;
        this.routeTotal = routeTotal;
        this.seatTotal = seatTotal;
        int sectionSeatsTotal = coachTotal * routeTotal * seatTotal;
        sectionSeats = new Seat[sectionSeatsTotal];
        bitMap = new BitMap(sectionSeatsTotal);
    }

    public void occupy(int route, int coach, int seat) throws IllegalStateException {
        int index = this.getIndex(route, coach, seat);
        try {
            sectionSeats[index].lock();
            sectionSeats[index].occupy();
            bitMap.set(index);
        } finally {
            sectionSeats[index].unlock();
        }
    }

    public void free(int route, int coach, int seat) throws IllegalStateException {
        int index = this.getIndex(route, coach, seat);
        try {
            sectionSeats[index].lock();
            sectionSeats[index].free();
            bitMap.reset(index);
        } finally {
            sectionSeats[index].unlock();
        }
    }

    public boolean isAvailable(int route, int coach, int seat) {
        return sectionSeats[this.getIndex(route, coach, seat)].isAvailable();
    }

    private int getIndex(int route, int coach, int seat) {
        route -= 1;
        coach -= 1;
        seat -= 1;
        return route * coachTotal * seatTotal + coach * seatTotal + seat;
    }

}