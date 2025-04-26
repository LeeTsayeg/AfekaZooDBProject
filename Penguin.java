package lee_tsayeg_rotem_boltanski;

public class Penguin extends Animal implements Comparable<Penguin> {
    public static int MAX_AGE = 6;
    protected String name;
    protected double height;
    public boolean isLeader;
    protected double foodAmount;

    public Penguin(String name, int age, double height, boolean isLeader) {
        super(age);
        this.name = name;
        this.height = height;
        this.isLeader = false;
        this.foodAmount = 1;
    }

    @Override
    public int compareTo(Penguin o) {
        return 0;
    }

    @Override
    public String getAnimalDetails() {
        return "\nThe penguin name is: " + name
                + "\nThe penguin age is:  " + age
                + "\nThe penguin height: " + height
                + "\nThe penguin is" + (isLeader? " a leader" : " not a leader")
                + "\nHappiness level " + happiness + "\n\n";
    }

    public double getPenguinHeight() {
        return this.height;
    }

    public boolean getPenguinIsLeader() {
        return this.isLeader;
    }
    public void setPenguinIsLeader() { this.isLeader = true; }
    public String getPenguinName() {
        return this.name;
    }

    @Override
    public double feed() {
        return foodAmount;
    }

    @Override
    public String makeNoise() {
        return "squack";
    }

    @Override
    public int getAnimalAge() {
        return this.age;
    }

    @Override
    public int getAnimalMaxAge() {
        return this.MAX_AGE;
    }

}
