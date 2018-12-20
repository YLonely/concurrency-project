package ticketingsystem.tools;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Seat {
    private Lock lock;
    private boolean available;

    public Seat() {
        this.lock = new ReentrantLock();
        this.available = true;
    }

    public void lock() {
        this.lock.lock();
    }

    public void unlock() {
        this.lock.unlock();
    }

    public boolean isAvailable() {
        return this.available;
    }

    public void occupy() throws IllegalStateException {
        if (!isAvailable())
            throw new IllegalStateException("Seat is unavailable!");
        this.available = false;
    }

    public void free() throws IllegalStateException {
        if (isAvailable())
            throw new IllegalStateException("Seat is available!");
        this.available = true;
    }
}