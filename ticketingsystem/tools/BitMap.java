package ticketingsystem.tools;

public class BitMap {
    private long[] map;
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
        map = new long[mapSize];
    }

    public void set(int index) throws ArrayIndexOutOfBoundsException {
        if (index >= size || index < 0)
            throw new ArrayIndexOutOfBoundsException();
        int mapIndex = index / longSize;
        int setterIndex = index % longSize;
        map[mapIndex] |= bitSetter[setterIndex];
    }

    public void reset(int index) throws ArrayIndexOutOfBoundsException {
        if (index >= size || index < 0)
            throw new ArrayIndexOutOfBoundsException();
        int mapIndex = index / longSize;
        int setterIndex = index % longSize;
        map[mapIndex] &= (~bitSetter[setterIndex]);
    }

    public long[] snapshot() {
        return map.clone();
    }

}