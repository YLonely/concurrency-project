package ticketingsystem.tools.bitoniccounter;

class Merger {
    private Merger[] half;
    private Balancer[] layer;
    private final int width;

    public Merger(int width) {
        this.width = width;
        layer = new Balancer[this.width / 2];
        for (int i = 0; i < layer.length; ++i)
            layer[i] = new Balancer();
        if (this.width > 2)
            this.half = new Merger[] { new Merger(this.width / 2), new Merger(this.width / 2) };
    }

    public int traverse(int input) {
        int output = 0;
        if (width <= 2)
            return layer[0].traverse();
        if (input < width / 2)
            output = half[input % 2].traverse(input / 2);
        else
            output = half[1 - (input % 2)].traverse(input / 2);
        return (2 * output) + layer[output].traverse();
    }
}