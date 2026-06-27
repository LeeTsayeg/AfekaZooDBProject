package lee_tsayeg_rotem_boltanski.db;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Interactive database menu (invoked from Main as option 11).
 *
 * Covers all JDBC requirements:
 *   ● INSERT  – add predator / penguin / fish
 *   ● UPDATE  – change happiness or predator weight
 *   ● DELETE  – remove an animal
 *   ● SEARCH  – by name, species, or age range
 *   ● QUERIES – 12 analytical SQL queries (Q1–Q12)
 */
public class DBMenu {

    private static final Scanner s   = new Scanner(System.in);
    private static final ZooDAO  dao = new ZooDAO();
    private static final int     ZOO_ID = 1;

    private static final String MENU =
        "\n╔══════════════════════════════════════╗\n" +
        "║         DATABASE OPERATIONS          ║\n" +
        "╠══════════════════════════════════════╣\n" +
        "║  ─── CRUD ───                        ║\n" +
        "║  1 Insert predator (Lion / Tiger)    ║\n" +
        "║  2 Insert penguin                    ║\n" +
        "║  3 Insert fish                       ║\n" +
        "║  4 Update animal happiness           ║\n" +
        "║  5 Update predator weight            ║\n" +
        "║  6 Delete animal                     ║\n" +
        "║  ─── SEARCH ───                      ║\n" +
        "║  7 Search by name                    ║\n" +
        "║  8 Search by species                 ║\n" +
        "║  9 Search by age range               ║\n" +
        "║  ─── SQL QUERIES ───                 ║\n" +
        "║  10 Q1  All living animals           ║\n" +
        "║  11 Q2  Predator food ranking        ║\n" +
        "║  12 Q3  Penguins by height           ║\n" +
        "║  13 Q4  Fish colour frequency        ║\n" +
        "║  14 Q5  Low-happiness animals        ║\n" +
        "║  15 Q6  Count by species             ║\n" +
        "║  16 Q7  Avg happiness by category    ║\n" +
        "║  17 Q8  Oldest animal per species    ║\n" +
        "║  18 Q9  Death records                ║\n" +
        "║  19 Q10 Fish with colours            ║\n" +
        "║  20 Q11 Zoo statistics               ║\n" +
        "║  21 Q12 Feeding history              ║\n" +
        "║  ─────────────────────────────────── ║\n" +
        "║  0 Back to main menu                 ║\n" +
        "╚══════════════════════════════════════╝\n";

    /** Entry point called from Main. */
    public static void run() {
        if (!DBConnection.isAvailable()) {
            System.out.println("[DB] Cannot connect to zoo_db. Check DBConnection credentials.");
            return;
        }
        int choice;
        do {
            System.out.print(MENU + "Choice: ");
            choice = readInt();
            switch (choice) {
                case 1  -> insertPredator();
                case 2  -> insertPenguin();
                case 3  -> insertFish();
                case 4  -> updateHappiness();
                case 5  -> updateWeight();
                case 6  -> deleteAnimal();
                case 7  -> searchByName();
                case 8  -> searchBySpecies();
                case 9  -> searchByAge();
                case 10 -> dao.q1AllLivingAnimals();
                case 11 -> dao.q2PredatorFoodRanking();
                case 12 -> dao.q3PenguinsByHeight();
                case 13 -> dao.q4FishColorFrequency();
                case 14 -> dao.q5LowHappinessAnimals();
                case 15 -> dao.q6AnimalCountBySpecies();
                case 16 -> dao.q7AvgHappinessByCategory();
                case 17 -> dao.q8OldestAnimalPerSpecies();
                case 18 -> dao.q9DeathRecords();
                case 19 -> dao.q10FishWithColors();
                case 20 -> dao.q11ZooStatistics();
                case 21 -> dao.q12FeedingHistory();
                case 0  -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid choice. Enter 0-21.");
            }
        } while (choice != 0);
    }

    // ─── CRUD handlers ───────────────────────────────────────

    private static void insertPredator() {
        System.out.print("Name: ");
        String name = s.nextLine().trim();
        System.out.print("Age (1-15): ");
        int age = readInt();
        System.out.print("Weight (kg): ");
        double weight = readDouble();
        System.out.print("Gender (female/male): ");
        boolean female = s.nextLine().trim().equalsIgnoreCase("female");
        System.out.print("Type (1=Tiger, 2=Lion): ");
        String type = readInt() == 2 ? "Lion" : "Tiger";
        dao.insertPredator(name, age, weight, female, type, ZOO_ID);
    }

    private static void insertPenguin() {
        System.out.print("Name: ");
        String name = s.nextLine().trim();
        System.out.print("Age (1-6): ");
        int age = readInt();
        System.out.print("Height (cm): ");
        double height = readDouble();
        System.out.print("Is leader? (yes/no): ");
        boolean leader = s.nextLine().trim().equalsIgnoreCase("yes");
        dao.insertPenguin(name, age, height, leader, ZOO_ID);
    }

    private static void insertFish() {
        System.out.print("Fish type (1=Gold, 2=Clown, 3=Aquarium): ");
        int typeNum = readInt();
        String fishType = switch (typeNum) {
            case 2 -> "Clown";
            case 3 -> "Aquarium";
            default -> "Gold";
        };
        System.out.print("Age: ");
        int age = readInt();
        System.out.print("Length (cm): ");
        double length = readDouble();

        String pattern;
        if (fishType.equals("Aquarium")) {
            System.out.print("Pattern (1=Dots, 2=Stripes, 3=Spots, 4=Smooth): ");
            pattern = switch (readInt()) {
                case 2 -> "Stripes";
                case 3 -> "Spots";
                case 4 -> "Smooth";
                default -> "Dots";
            };
        } else {
            pattern = fishType.equals("Gold") ? "Smooth" : "Stripes";
        }

        String[] colors;
        if (fishType.equals("Clown")) {
            colors = new String[]{"ORANGE", "BLACK", "WHITE"};
        } else {
            System.out.print("Colours (comma-separated, e.g. ORANGE,BLUE): ");
            colors = Arrays.stream(s.nextLine().trim().split(","))
                           .map(String::trim)
                           .filter(c -> !c.isEmpty())
                           .toArray(String[]::new);
        }
        dao.insertFish(age, length, pattern, fishType, colors, ZOO_ID);
    }

    private static void updateHappiness() {
        System.out.print("Animal ID: ");
        int id = readInt();
        System.out.print("New happiness (0-100): ");
        int h = readInt();
        dao.updateHappiness(id, h);
    }

    private static void updateWeight() {
        System.out.print("Animal ID (predator): ");
        int id = readInt();
        System.out.print("New weight (kg): ");
        double w = readDouble();
        dao.updatePredatorWeight(id, w);
    }

    private static void deleteAnimal() {
        System.out.print("Animal ID to delete: ");
        dao.deleteAnimal(readInt());
    }

    // ─── Search handlers ─────────────────────────────────────

    private static void searchByName() {
        System.out.print("Name keyword: ");
        dao.searchByName(s.nextLine().trim());
    }

    private static void searchBySpecies() {
        System.out.print("Species name (e.g. Lion, GoldFish): ");
        dao.searchBySpecies(s.nextLine().trim());
    }

    private static void searchByAge() {
        System.out.print("Min age: ");
        int min = readInt();
        System.out.print("Max age: ");
        int max = readInt();
        dao.searchByAgeRange(min, max);
    }

    // ─── Input helpers ───────────────────────────────────────

    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(s.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a whole number: ");
            }
        }
    }

    private static double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(s.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a number: ");
            }
        }
    }
}
