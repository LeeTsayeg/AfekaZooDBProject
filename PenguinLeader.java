package lee_tsayeg_rotem_boltanski;

public class PenguinLeader extends Penguin {
    private double foodAmount;

    public PenguinLeader(String name, int age, double height, boolean isLeader) {
        super(name, age, height, isLeader);
        this.foodAmount = 2;
        this.isLeader = true;
    }

    @Override
    public String getAnimalDetails() {
        return "\nThe penguin name is: " + name
                + "\nThe penguin age is:  " + age
                + "\nThe penguin height: " + height
                + "\nThe penguin is" + (isLeader? " a leader" : " not a leader")
                + "\nHappiness level " + happiness
                + "\nI can lead the group!\n\n";
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
    public int getAnimalMaxAge() {
        return this.MAX_AGE;
    }

}
