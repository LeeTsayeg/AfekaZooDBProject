package lee_tsayeg_rotem_boltanski;

public class Tiger extends Predator {

    public Tiger(String name, int age, double weight, boolean isFemale) {
        super(name, age, weight, isFemale);
        this.foodAmount =  weight * age * (isFemale? 0.03 : 0.02);
    }


    @Override
    public String getAnimalDetails() {
        return "\nThe tiger name is: " + name
                + "\nThe tiger age is:  " + age
                + "\nThe tiger weight: " + weight
                + "\nThe tiger is" + (isFemale ? " a female" : " a male")
                + "\nHappiness level " + happiness + "\n\n";
    }

    @Override
    public String makeNoise() { return "roar"; }

    @Override
    public double feed() { return foodAmount; }

    public static boolean setTigerName(String name) {
        return name.length() >= 2;
    }

    public boolean setTigerAge(int age) {
        return age > 0;
    }

    public boolean setTigerWeight(double weight) {
        return weight > 0;
    }

}
