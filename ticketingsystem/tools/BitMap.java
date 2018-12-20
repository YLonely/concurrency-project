package ticketingsystem.tools;

import java.util.concurrent.atomic.AtomicLongArray;

public class BitMap {
    private AtomicLongArray map;
    private static final int longSize = Long.SIZE;
    private int size;
    private static long[] bitSetter;
    static {
        bitSetter = new long[longSize];
        for (int i = 0; i < longSize; ++i)
            bitSetter[i] = (1 << i);
    }

    public BitMap(int size) {
        this.size = size;
        int mapSize = (size + longSize - 1) / longSize;
        map = new AtomicLongArray(mapSize);
    }

    public void set(int index) throws ArrayIndexOutOfBoundsException {
        if (index >= size || index < 0)
            throw new ArrayIndexOutOfBoundsException();
        int mapIndex = index / longSize;
        int setterIndex = index % longSize;
        while (true) {
            long oldValue = map.get(mapIndex);
            long newValue = oldValue | bitSetter[setterIndex];
            if (map.compareAndSet(mapIndex, oldValue, newValue))
                break;
        }
    }

    public void reset(int index) throws ArrayIndexOutOfBoundsException {
        if (index >= size || index < 0)
            throw new ArrayIndexOutOfBoundsException();
        int mapIndex = index / longSize;
        int setterIndex = index % longSize;
        while (true) {
            long oldValue = map.get(mapIndex);
            long newValue = oldValue & (~bitSetter[setterIndex]);
            if (map.compareAndSet(mapIndex, oldValue, newValue))
                break;
        }
    }

    public long[] rawSnapshot() {
        long[] res = new long[map.length()];
        for (int i = 0; i < res.length; ++i)
            res[i] = map.get(i);
        return res;
    }

}