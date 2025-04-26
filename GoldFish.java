package lee_tsayeg_rotem_boltanski;

public class GoldFish extends Fish {
    public static int MAX_AGE = 12;
    private static String[] colorsOptions = { "ORANGE", "YELLOW", "BLACK", "GOLD"};
    public GoldFish(int age, double length, String[] colors, String pattern) {
        super(age, length, pattern, colors);
        this.pattern = "Smooth";
        this.colors = colors;
    }

    public static String[] getGoldColorsOptions() {
        return colorsOptions;
    }

    @Override
    public int getAnimalMaxAge() {
        return MAX_AGE;
    }
}

