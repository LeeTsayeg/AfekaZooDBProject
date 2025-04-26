package lee_tsayeg_rotem_boltanski;

import java.util.Comparator;

public class CompareHeight implements Comparator<Penguin> {
    @Override
    public int compare(Penguin p1, Penguin p2) {
        return (int) ((p2.getPenguinHeight() - p1.getPenguinHeight()) * 100);
    }
}
