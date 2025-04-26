package lee_tsayeg_rotem_boltanski;

public abstract class Predator extends Animal {
    public static int MAX_AGE = 15;
    protected String name;
    protected double weight;
    protected boolean isFemale;
    protected double foodAmount;

    public Predator(String name, int age, double weight, boolean isFemale) {
        super(age);
        this.name = name;
        this.weight = weight;
        this.isFemale = isFemale;
        this.foodAmount = 0;
    }

    @Override
    public int getAnimalAge() {
        return this.age;
    }

    @Override
    public int getAnimalMaxAge() {
        return MAX_AGE;
    }

    public abstract String getAnimalDetails();

}
