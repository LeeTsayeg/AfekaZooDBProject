package lee_tsayeg_rotem_boltanski;

import java.util.Random;

public abstract class Animal {
    public int age;
    public int happiness;

    public Animal(int age) {
        this.age = age;
        this.happiness = 100;
    }

    public abstract String makeNoise();
    public abstract double feed();
    public abstract String getAnimalDetails();
    public abstract int getAnimalMaxAge();
    public int getAnimalAge() {
        return age;
    };

    public int resetAnimalHappiness() {
        this.happiness = 100;
        return happiness;
    };

    public int getAnimalHappiness() {
        return happiness;
    };

    public void ageOneYear() {
        Random r = new Random();
        int happinessDecrease = r.nextInt(16) + 15;
        this.age = this.age + 1;
        this.happiness = this.happiness - happinessDecrease;
    }
}


