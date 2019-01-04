package ticketingsystem.tools;

import java.util.concurrent.atomic.AtomicLongArray;

class BitMap {
    private AtomicLongArray map;
    private static final int longSize = Long.SIZE;
    private int size;

    public BitMap(int size) {
        this.size = size;
        int mapSize = (size + longSize - 1) / longSize;
        map = new AtomicLongArray(mapSize);
        int remainSize = mapSize * longSize - size;
        int begin = longSize - remainSize;
        long old = map.get(map.length() - 1);
        map.set(map.length() - 1, BitHelper.setRange(old, begin, longSize));
    }

    public static int elementSize() {
        return longSize;
    }

    public void set(int index) throws ArrayIndexOutOfBoundsException {
        if (index >= size || index < 0)
            throw new ArrayIndexOutOfBoundsException();
        int mapIndex = index / longSize;
        int i = index % longSize;
        while (true) {
            long oldValue = map.get(mapIndex);
            long newValue = BitHelper.set(oldValue, i);
            if (map.compareAndSet(mapIndex, oldValue, newValue))
                break;
        }
    }

    public void reset(int index) throws ArrayIndexOutOfBoundsException {
        if (index >= size || index < 0)
            throw new ArrayIndexOutOfBoundsException();
        int mapIndex = index / longSize;
        int i = index % longSize;
        while (true) {
            long oldValue = map.get(mapIndex);
            long newValue = BitHelper.reset(oldValue, i);
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