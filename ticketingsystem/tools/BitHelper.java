package ticketingsystem.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BitHelper {
    private static final int longSize = Long.SIZE;
    private static long[] bitSetter;
    private static HashMap<Long, Integer> locateMap;
    static {
        locateMap = new HashMap<>(longSize);
        bitSetter = new long[longSize];
        for (int i = 0; i < longSize; ++i) {
            bitSetter[i] = (long) (1L << i);
            locateMap.put(bitSetter[i], i);
        }
    }

    public static long set(long l, int i) {
        return l | bitSetter[i];
    }

    public static long reset(long l, int i) {
        return l & (~bitSetter[i]);
    }

    public static long setRange(long l, int s, int e) {
        for (int i = s; i < e; ++i)
            l = set(l, i);
        return l;
    }

    public static int countOnes(long l) {
        int count = 0;
        while (l != 0) {
            l = l & (l - 1);
            ++count;
        }
        return count;
    }

    public static List<Integer> locateOnes(long l) {
        ArrayList<Integer> location = new ArrayList<>(longSize);
        while (l != 0) {
            long tmp = l & (l - 1);
            location.add(locateMap.get(l ^ tmp));
            l = tmp;
        }
        return location;
    }

    public static List<Integer> locateZeros(long l) {
        return locateOnes(~l);
    }

    public static int countZeros(long l) {
        return countOnes(~l);
    }

    public static long floor2power(long l) {
        int i = bitSetter.length - 1;
        for (; i >= 0; --i)
            if ((l & bitSetter[i]) > 0)
                break;
        return i == 0 ? 2L : bitSetter[i];
    }
}