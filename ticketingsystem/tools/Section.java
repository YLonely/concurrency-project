package ticketingsystem.tools;

class Section {
    private int routeTotal;
    private int coachTotal;
    private int seatTotal;
    private Seat[] sectionSeats;
    private BitMap[] bitMap;
    private boolean isChanged;

    public Section(int routeTotal, int coachTotal, int seatTotal) {
        this.coachTotal = coachTotal;
        this.routeTotal = routeTotal;
        this.seatTotal = seatTotal;
        this.isChanged = false;
        sectionSeats = new Seat[routeTotal * coachTotal * seatTotal];
        for (int i = 0; i < sectionSeats.length; ++i)
            sectionSeats[i] = new Seat();
        bitMap = new BitMap[routeTotal];
        for (int i = 0; i < bitMap.length; ++i)
            bitMap[i] = new BitMap(coachTotal * seatTotal);
    }

    public static int bitMapElementSize() {
        return BitMap.elementSize();
    }

    public void lock(int route, int coach, int seat) {
        int index = this.getSeatIndex(route, coach, seat);
        sectionSeats[index].lock();
    }

    public void unlock(int route, int coach, int seat) {
        int index = this.getSeatIndex(route, coach, seat);
        sectionSeats[index].unlock();
    }

    public void occupy(int route, int coach, int seat) throws IllegalStateException {
        int index = this.getSeatIndex(route, coach, seat);
        sectionSeats[index].occupy();
        bitMap[route - 1].set(getBitIndex(coach, seat));
        isChanged = true;
    }

    public void free(int route, int coach, int seat) throws IllegalStateException {
        int index = this.getSeatIndex(route, coach, seat);
        bitMap[route - 1].reset(getBitIndex(coach, seat));
        sectionSeats[index].free();
        isChanged = true;
    }

    public boolean isAvailable(int route, int coach, int seat) {
        return sectionSeats[this.getSeatIndex(route, coach, seat)].isAvailable();
    }

    public long[] snapshot(int route, boolean clear) {
        if (clear)
            isChanged = false;
        return bitMap[route - 1].rawSnapshot();
    }

    public boolean isChanged() {
        return isChanged;
    }

    private int getSeatIndex(int route, int coach, int seat) {
        route -= 1;
        coach -= 1;
        seat -= 1;
        return route * coachTotal * seatTotal + coach * seatTotal + seat;
    }

    private int getBitIndex(int coach, int seat) {
        coach -= 1;
        seat -= 1;
        return coach * seatTotal + seat;
    }

}