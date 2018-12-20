package ticketingsystem.tools.bitoniccounter;

class Bitonic {
    private Bitonic[] half;
    private Merger merger;
    private final int width;

    public Bitonic(int width) {
        this.width = width;
        this.merger = new Merger(this.width);
        if (width > 2)
            half = new Bitonic[] { new Bitonic(width / 2), new Bitonic(width / 2) };
    }

    public int traverse(int input) {
        int output = 0;
        if (width > 2)
            output = half[input / (width / 2)].traverse(input / 2);
        return merger.traverse((input >= (width / 2) ? (width / 2) : 0) + output);
    }
}