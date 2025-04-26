//lee tsayeg and rotem boltanski - zoo task 1
package lee_tsayeg_rotem_boltanski;
import lee_tsayeg_rotem_boltanski.exceptions.*;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static Scanner s = new Scanner(System.in);
    private static Zoo afekaZoo = new Zoo("Ramat Gan Zoo","Afeka");
    public final static String Menu = "\n0- Exit zoo.\n" +
            "1- Show zoo details.\n" +
            "2- Add new penguin.\n" +
            "3- Add new predator.\n" +
            "4- Add new fish.\n" +
            "5- Show zoo penguins.\n" +
            "6- Show zoo predators.\n" +
            "7- Show zoo fish and colors.\n" +
            "8- Feed all zoo animals.\n" +
            "9- Show zoo animals noise.\n" +
            "10- Age zoo animals in one year.\n";

    public static void main(String[] args) {
        init();
        run();
    }
    private static void run() {
        int choice;
        do {
            choice = showMenu();
            switch (choice){
                case 0 -> exitProgram();
                case 1 -> showZooDetails();
                case 2 -> addNewPenguin();
                case 3 -> addNewPredator();
                case 4 -> addNewFish();
                case 5 -> showPenguin();
                case 6 -> showPredators();
                case 7 -> showFishDetails();
                case 8 -> feedZooAnimals();
                case 9 -> animalMakeNoise();
                case 10 -> animalsAgeOneYear();
            }
        } while (choice != 0);
    }


    //------case 0------
    private static void exitProgram() {
        System.out.println("Thanks for visiting the zoo, byeeee");
    }
    //------case 1------
    private static void showZooDetails() {
        System.out.println("The zoo name is: " + afekaZoo.getZooName() +
                "\nThe zoo address is: " + afekaZoo.getZooAddress() +
                "\nThe lions amount is: " + afekaZoo.getZooLionAmount() +
                "\nThe tigers amount is: " + afekaZoo.getZooTigerAmount() +
                "\nThe penguins amount is: " + afekaZoo.getZooPenguinsAmount() +
                "\nThe aquarium fish amount is: " + afekaZoo.getZooAquariumAmount() +
                "\nThe gold fish amount is: " + afekaZoo.getZooGoldAmount() +
                "\nThe clown fish amount is: " + afekaZoo.getZooClownAmount());
    }
    //------case 2------
    private static void addNewPenguin() {
        System.out.println("what is the penguin name?");
        String penguinName = s.nextLine();
        System.out.println("what is the penguin age?");
        int penguinAge = validateAge(Penguin.MAX_AGE);
        System.out.println("what is the penguin height?");
        double penguinHeight;
        while (true) {
            try {
                penguinHeight = validateHeight();
                if (penguinHeight >= afekaZoo.getLeaderHeight() && afekaZoo.getZooPenguinsAmount() > 0) {
                    throw new HigherThanPenguinLeaderException(" Entered height: " + penguinHeight);
                }
                break;
            } catch (HigherThanPenguinLeaderException e) {
                System.out.println(e.getMessage());
                System.out.println("Please enter a valid penguin height lower than the leader - " + afekaZoo.getLeaderHeight());
            }
        }
        afekaZoo.addPenguin(penguinName, penguinAge, penguinHeight, afekaZoo.getZooPenguinsAmount() == 0 ? true : false);
        System.out.println("Penguin " + penguinName + " added to zoo.");
    }


    //------case 3------
    private static void addNewPredator() {
        System.out.println("what is the predator name?");
        String lionName = s.nextLine();
        System.out.println("what is the predator age?");
        int lionAge = validateAge(Predator.MAX_AGE);
        System.out.println("what is the predator weight?");
        double lionWeight = validateWeight();
        System.out.println("enter the gender (female/male)");
        boolean isFemale = validateGender();
        System.out.println("choose the predator type: 1- Tiger, 2- Lion (enter the number):");
        int type = validateChoice(1,2);
        if (afekaZoo.addPredator(type, lionName, lionAge, lionWeight, isFemale)){
            System.out.println("predator " + lionName + " added to zoo.");
        }
        else {
            System.out.println("predator " + lionName + " was not added to zoo, please check your values.");
        }
    }
    //------case 4------
    private static String[] colorChoice(String color, String[] colors) {
        if(afekaZoo.isExistsInArr(colors,color) != -1) {
            System.out.println("color was already chosen, please enter again.");
        }
        else {
            colors = Arrays.copyOf(colors, colors.length + 1);
            colors[colors.length - 1] = color;
        }
        return colors;
    }

    private static void addNewFish() {
        System.out.println("what is the fish type? \n1- Gold, 2- Clown, 3- Aquarium");
        int typeChoice = validateChoice(1,3);
        String fishType = "";
        int MAX_AGE = 0;
        switch(typeChoice){
            case 1 :
                fishType = "Gold";
                MAX_AGE = GoldFish.MAX_AGE;
                break;
            case 2 :
                fishType = "Clown";
                MAX_AGE = ClownFish.MAX_AGE;
                break;
            case 3 :
                fishType = "Aquarium";
                MAX_AGE = AquariumFish.MAX_AGE;
                break;
        }
        System.out.println("what is the fish age?");
        int fishAge = validateAge(MAX_AGE);
        System.out.println("what is the fish length?");
        double fishLength = validateLength();
        int patternChoice;
        String fishPattern= "";
        if (fishType.equals("Aquarium")) {
            System.out.println("what is the fish Pattern? \n1- Dots, 2- Stripes, 3- Spots, 4- Smooth");
            patternChoice = validateChoice(1,4);
            switch (patternChoice) {
                case 1 -> fishPattern = "Dots";
                case 2 -> fishPattern = "Stripes";
                case 3 -> fishPattern = "Spots";
                case 4 -> fishPattern = "Smooth";
            }
        } else if (fishType.equals("Gold")) {
            fishPattern = "Smooth";
        } else {
            fishPattern = "Stripes";
        }
        String[] colors = new String[0];
        int colorChoice;
        if (fishType.equals("Aquarium")) {
            do {
                System.out.println("what is the fish Colors?");
                System.out.println("1-WHITE, 2-GREEN, 3-ORANGE, 4-BLUE, 5-YELLOW, 6-BROWN, 7-GOLD, 8-RED, 9-CYAN, 10-No more colors, ");
                colorChoice = validateChoice(1,9);
                switch (colorChoice) {
                    case 1 -> colors = colorChoice("WHITE", colors);
                    case 2 -> colors = colorChoice("GREEN", colors);
                    case 3 -> colors = colorChoice("ORANGE", colors);
                    case 4 -> colors = colorChoice("BLUE", colors);
                    case 5 -> colors = colorChoice("YELLOW", colors);
                    case 6 -> colors = colorChoice("BROWN", colors);
                    case 7 -> colors = colorChoice("GOLD", colors);
                    case 8 -> colors = colorChoice("RED", colors);
                    case 9 -> colors = colorChoice("CYAN", colors);
                }
            } while (colorChoice != 10 && colors.length > 0 && colors.length < AquariumFish.getAquariumColorsOptions().length);
        } else if (fishType.equals("Gold")) {
            do {
                System.out.println("1-ORANGE, 2-YELLOW, 3-BLACK, 4-GOLD, 5-No more colors, ");
                colorChoice = validateChoice(1,5);
                switch (colorChoice) {
                    case 1 -> colors = colorChoice("ORANGE", colors);
                    case 2 -> colors = colorChoice("YELLOW", colors);
                    case 3 -> colors = colorChoice("BLACK", colors);
                    case 4 -> colors = colorChoice("GOLD", colors);
                }
            } while (colorChoice != 5 && colors.length > 0 && colors.length < GoldFish.getGoldColorsOptions().length);
        } else {
            colors = Arrays.copyOf(colors, colors.length + 3);
            colors[0] = "ORANGE";
            colors[1] = "BLACK";
            colors[2] = "WHITE";
            }
        if (afekaZoo.addAFish(fishType, fishAge, fishLength, fishPattern, colors)) {
            System.out.println("Fish added to zoo.");
        }
        else {
            System.out.println("Fish was not added to zoo, please check your values.");
        }
    }

    //------case 5------
    private static void showPenguin() {
        if (afekaZoo.getZooPenguinsAmount() != 0) {
            System.out.println("how do you like to sort the penguins? \n1- by name, 2- by height, 3- by age");
            int compareChoice = validateChoice(1,3);
            String compareType = "";
            switch (compareChoice) {
                case 1 -> compareType = "1";
                case 2 -> compareType = "2";
                case 3 -> compareType = "3";
            }
            System.out.println("The zoo penguins details:\n");
            System.out.println(afekaZoo.getZooPenguinDetails(afekaZoo.getSortedPenguins(compareType)));
        }
        else {
            System.out.println("There are no penguins in the zoo :( .");
        }
    }

    //------case 6------
    private static void showPredators() {
        showLion();
        showTiger();
    }

    private static void showLion() {
        if (afekaZoo.getZooLionAmount() != 0) {
            System.out.println("The zoo Lions details:\n");
            System.out.println(afekaZoo.getZooAnimalDetails(Lion.class, afekaZoo.getZooLionAmount(), "Lion"));
        }
        else {
            System.out.println("There are no lions in the zoo :( .");
        }
    }

    private static void showTiger() {
        if (afekaZoo.getZooTigerAmount() > 0) {
            System.out.println("The zoo Tigers details:\n");
            System.out.println(afekaZoo.getZooAnimalDetails(Tiger.class, afekaZoo.getZooTigerAmount(), "Tiger"));
        }
        else {
            System.out.println("There are no tigers in the zoo :( .");
        }
    }

    //------case 7------
    private static void showFishDetails() {
        if (afekaZoo.getZooClownAmount() != 0 && afekaZoo.getZooGoldAmount() != 0 && afekaZoo.getZooAquariumAmount() != 0) {
            System.out.println("The zoo Aquarium Fish details:");
            System.out.println(afekaZoo.getZooFishDetails());
        }
        else {
            System.out.println("There are no fishes in the zoo :( .");
        }
    }
    //------case 8------
    private static void feedZooAnimals() {
        System.out.println("The penguins ate: " + afekaZoo.foodForAllAnimals(Penguin.class) + " fishes.");
        System.out.println("\nThe Lion ate: " + afekaZoo.foodForAllAnimals(Lion.class) + " kg.");
        System.out.println("\nThe Tiger ate: " + afekaZoo.foodForAllAnimals(Tiger.class) + " kg.");
        System.out.println("\nThe Aquarium fish ate: " + afekaZoo.foodForAllAnimals(AquariumFish.class) + " dishes.");
        System.out.println("\nThe Clown fish ate: " + afekaZoo.foodForAllAnimals(ClownFish.class) + " dishes.");
        System.out.println("\nThe Gold fish ate: " + afekaZoo.foodForAllAnimals(GoldFish.class) + " dishes.");
    }
    //------case 9------
    private static void animalMakeNoise() {
        System.out.println(afekaZoo.getZooNoise());
    }

    //------case 10------
    private static void animalsAgeOneYear() {
        int numOfDeadAnimals = afekaZoo.ageOneYear();
        System.out.println("All animals aged in one year.");
        if (numOfDeadAnimals > 0) {
            System.out.println(numOfDeadAnimals + " left the zoo :( .");
        }
    }

    //-------Exception------
    private static int validateAge(int MAX_AGE) {
        String Age;
        int numAge;
        while (true) {
            try {
                Age = s.nextLine();
                numAge = Integer.parseInt(Age);
                if (numAge <= 0 || numAge > MAX_AGE) {
                    throw new InvalidAgeException(" Entered age " + numAge);
                }
                return numAge;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            } catch (InvalidAgeException e) {
                System.out.println(e.getMessage());
                System.out.println("Please enter a valid age number between 0 to " + MAX_AGE);
            }
        }
    }

    private static Double validateHeight() {
        String Height;
        double numHeight;
        while (true) {
            try {
                Height = s.nextLine();
                numHeight = Double.parseDouble(Height);
                if (numHeight <= 0) {
                    throw new InvalidHeightException(" Entered height: " + Height);
                }
                return numHeight;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            } catch (InvalidHeightException e) {
                System.out.println(e.getMessage());
                System.out.println("Please enter a valid height");
            }
        }
    }

    private static double validateLength() {
        String Length;
        double numLength;
        while (true) {
            try {
                Length = s.nextLine();
                numLength = Double.parseDouble(Length);
                if (numLength <= 0) {
                    throw new InvalidLengthException(" Entered height: " + Length);
                }
                return numLength;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            } catch (InvalidLengthException e) {
                System.out.println(e.getMessage());
                System.out.println("Please enter a valid length");
            }
        }
    }

    private static double validateWeight() {
        String Weight;
        double numWeight;
        while (true) {
            try {
                Weight = s.nextLine();
                numWeight = Double.parseDouble(Weight);
                if (numWeight <= 0) {
                    throw new InvalidWeightException(" Entered weight: " + Weight);
                }
                return numWeight;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            } catch (InvalidWeightException e) {
                System.out.println(e.getMessage());
                System.out.println("Please enter a valid weight");
            }
        }
    }

    private static int validateInputInt() {
        String input;
        int numInput;
        while (true) {
            try {
                input = s.nextLine();
                numInput = Integer.parseInt(input);
                return numInput;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static boolean validateGender() {
        String input;
        while (true) {
            try {
                input = s.nextLine();
                if (!input.equals("female") && !input.equals("male")) {
                  throw new InvalidGenderException(" you entered " + input);
                }
                if (input.equals("female")) {
                    return true;
                }
                else {
                    return false;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid gender.");
            } catch (InvalidGenderException e) {
                System.out.println(e.getMessage());
                System.out.println("Please enter a valid gender");
            }
        }
    }

    private static int validateChoice(int min , int max) {
        String Choice;
        int numChoice;
        while (true) {
            try {
                Choice = s.nextLine();
                numChoice = Integer.parseInt(Choice);
                if (numChoice < min  || numChoice > max) {
                    throw new InvalidChoiceException(" Entered Choice " + numChoice);
                }
                return numChoice;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid choice.");
            } catch (InvalidChoiceException e) {
                System.out.println(e.getMessage());
                System.out.println("Please enter a valid Choice between " + min + " to " + max);
            }
        }
    }

//-------Main------


    private static int showMenu() {
        System.out.println(Menu);
        System.out.println("Please enter your choice");
        return validateChoice(0,10);
    }


    private static void init() {
        afekaZoo.animals = new Animal[1];
        afekaZoo.addPredator( 2, "Lior", 14, 150, true);
        afekaZoo.addPredator( 2, "Lidor", 15, 120, false);
        afekaZoo.addPredator( 2, "Lila", 7, 100, true);
        afekaZoo.addPredator( 2, "Liam", 12, 190, false);
        afekaZoo.addPenguin("Pini", 6, 200, true);
        afekaZoo.addPenguin("Pnina", 5, 100, false);
        afekaZoo.addPenguin("Pinit", 2, 150, false);
        afekaZoo.addFishInAmount(10);
    }
}
