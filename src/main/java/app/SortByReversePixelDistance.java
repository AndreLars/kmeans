package app;

import java.util.Comparator;

public class SortByReversePixelDistance implements Comparator<Pixel> {
    @Override
    public int compare(Pixel o1, Pixel o2) {
        return Double.compare(o2.getDistance(), o1.getDistance());
    }
}
