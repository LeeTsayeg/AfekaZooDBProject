package lee_tsayeg_rotem_boltanski;


public class AquariumFish extends Fish {
    public static int MAX_AGE = 25;
    private static String[] colorsOptions = {"WHITE", "GREEN", "ORANGE", "BLUE", "YELLOW", "BROWN", "GOLD", "RED", "CYAN"};
    private static String[] patternOptions = {"Dots", "Stripes", "Spots", "Smooth"};
    public AquariumFish(int age, double length, String pattern, String[] colors) {
        super(age, length, pattern, colors);
        this.foodAmount = age >= 3 ? length + 3 : 3;
        this.colors = colors;
        this.pattern = pattern;
    }

    public static String[] getAquariumColorsOptions() {
        return colorsOptions;
    }

    public static String[] getAquariumPatternsOptions(){
        return patternOptions;
    }

    @Override
    public int getAnimalMaxAge() {
        return MAX_AGE;
    }
}

