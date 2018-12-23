package ticketingsystem.tools.bitoniccounter;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLongArray;

public class BitonicCounter {
    private Bitonic bc;
    private AtomicLongArray counter;
    private int width;

    public BitonicCounter(int width) {
        this.width = width;
        bc = new Bitonic(width);
        counter = new AtomicLongArray(width);
        for (int i = 0; i < counter.length(); ++i)
            counter.set(i, i);
    }

    public long getNext() {
        int input = ThreadLocalRandom.current().nextInt(width);
        int output = bc.traverse(input);
        return counter.getAndAdd(output, width);
    }
}