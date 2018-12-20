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

    public void lock(int route, int coach, int seat) {
        int index = this.getIndex(route, coach, seat);
        sectionSeats[index].lock();
    }

    public void unlock(int route, int coach, int seat) {
        int index = this.getIndex(route, coach, seat);
        sectionSeats[index].unlock();
    }

    public void occupy(int route, int coach, int seat) throws IllegalStateException {
        int index = this.getIndex(route, coach, seat);
        sectionSeats[index].occupy();
        bitMap.set(index);
    }

    public void free(int route, int coach, int seat) throws IllegalStateException {
        int index = this.getIndex(route, coach, seat);
        sectionSeats[index].free();
        bitMap.reset(index);
    }

    public boolean isAvailable(int route, int coach, int seat) {
        return sectionSeats[this.getIndex(route, coach, seat)].isAvailable();
    }

    public long[] snapshot() {
        return bitMap.rawSnapshot();
    }

    private int getIndex(int route, int coach, int seat) {
        route -= 1;
        coach -= 1;
        seat -= 1;
        return route * coachTotal * seatTotal + coach * seatTotal + seat;
    }

}