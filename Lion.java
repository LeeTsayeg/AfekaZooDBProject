package lee_tsayeg_rotem_boltanski;

public class Lion extends Predator {

    public Lion(String name, int age, double weight, boolean isFemale) {
        super(name, age, weight, isFemale);
        this.foodAmount =  weight * age * (isFemale? 0.03 : 0.02);
        this.foodAmount = foodAmount > 25 ? 25 : foodAmount;
    }

    @Override
    public String getAnimalDetails() {
        return "\nThe lion name is: " + name
                + "\nThe lion age is:  " + age
                + "\nThe lion weight: " + weight
                + "\nThe lion is" + (isFemale ? " a female" : " a male")
                + "\nHappiness level " + happiness + "\n\n";
    }

    @Override
    public String makeNoise() {
        return "ROAR";
    }

    @Override
    public double feed(){
        return foodAmount;
    }

}
