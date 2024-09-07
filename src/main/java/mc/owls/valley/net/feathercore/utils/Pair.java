package mc.owls.valley.net.feathercore.utils;

public class Pair<First, Second> {
    public First first = null;
    public Second second = null;

    public Pair(final First first, final Second second) {
        this.first = first;
        this.second = second;
    }

    public static <First, Second> Pair<First, Second> of(final First first, final Second second) {
        return new Pair<First, Second>(first, second);
    }
}
