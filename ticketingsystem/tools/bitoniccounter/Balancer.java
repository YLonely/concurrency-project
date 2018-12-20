package ticketingsystem.tools.bitoniccounter;

import java.util.concurrent.atomic.AtomicBoolean;

class Balancer {
    private AtomicBoolean toggle;

    public Balancer() {
        toggle = new AtomicBoolean(true);
    }

    public int traverse() {
        while (true) {
            boolean old = toggle.get();
            if (toggle.compareAndSet(old, !old))
                return (old ? 0 : 1);
        }
    }
}