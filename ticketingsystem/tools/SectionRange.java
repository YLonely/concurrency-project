package ticketingsystem.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ticketingsystem.InnerTicket;

public class SectionRange {
    private Section[] sections;
    private int seatNum;

    public SectionRange(int routeNum, int coachNum, int seatNum, int stationNum, int threadNum) {
        this.seatNum = seatNum;
        sections = new Section[stationNum - 1];
        for (int i = 0; i < sections.length; ++i)
            sections[i] = new Section(routeNum, coachNum, seatNum);
    }

    public void lock(InnerTicket it) {
        int s = it.departure - 1;
        int e = it.arrival - 1;
        for (int i = s; i < e; ++i)
            sections[i].lock(it.route, it.coach, it.seat);
    }

    public void unlock(InnerTicket it) {
        int s = it.departure - 1;
        int e = it.arrival - 1;
        for (int i = s; i < e; ++i)
            sections[i].unlock(it.route, it.coach, it.seat);
    }

    public boolean isAvailable(InnerTicket it) {
        boolean res = true;
        int s = it.departure - 1;
        int e = it.arrival - 1;
        for (int i = s; i < e; ++i) {
            res &= sections[i].isAvailable(it.route, it.coach, it.seat);
            if (res == false)
                return false;
        }
        return res;
    }

    public void occupy(InnerTicket it) throws IllegalStateException {
        int s = it.departure - 1;
        int e = it.arrival - 1;
        for (int i = s; i < e; ++i)
            sections[i].occupy(it.route, it.coach, it.seat);
    }

    public void free(InnerTicket it) throws IllegalStateException {
        int s = it.departure - 1;
        int e = it.arrival - 1;
        for (int i = s; i < e; ++i)
            sections[i].free(it.route, it.coach, it.seat);
    }

    private long[] getCompressedBitMap(int route, int departure, int arrival) {
        int s = departure;
        int e = arrival - 1;
        long[] bitMap = sections[s - 1].snapshot(route);
        for (int i = s; i < e; ++i) {
            long[] bm = sections[i].snapshot(route);
            assert bitMap.length == bm.length : "Length of route bitMap is different between sections";
            for (int j = 0; j < bitMap.length; ++j)
                bitMap[j] |= bm[j];
        }
        return bitMap;
    }

    private InnerTicket toInnerTicket(int index, int route, int departure, int arrival) {
        int seat = index % seatNum + 1;
        int coach = index / seatNum + 1;
        return new InnerTicket(route, coach, seat, departure, arrival);
    }

    public List<InnerTicket> locateAvailables(int route, int departure, int arrival) {
        ArrayList<InnerTicket> location = new ArrayList<>();
        long[] bitMap = getCompressedBitMap(route, departure, arrival);
        int size = Section.bitMapElementSize();
        for (int i = 0; i < bitMap.length; ++i) {
            List<Integer> l = BitHelper.locateZeros(bitMap[i]);
            for (int index : l)
                location.add(toInnerTicket(index + i * size, route, departure, arrival));
        }
        Collections.shuffle(location);
        return location;
    }

    public int countAvailables(int route, int departure, int arrival) {
        int availables = 0;
        long[] bitMap = getCompressedBitMap(route, departure, arrival);
        for (int i = 0; i < bitMap.length; ++i)
            availables += BitHelper.countZeros(bitMap[i]);
        return availables;
    }
}