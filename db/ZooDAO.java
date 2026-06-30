package lee_tsayeg_rotem_boltanski.db;

import java.sql.*;

/**
 * Data-Access Object for the Zoo database.
 *
 * Provides:
 *   - INSERT operations  (insertPredator, insertPenguin, insertFish)
 *   - UPDATE operations  (updateHappiness, updateAge, updateWeight)
 *   - DELETE operations  (deleteAnimal)
 *   - SEARCH operations  (searchByName, searchBySpecies, searchByAgeRange)
 *   - 12 SQL query methods  (q1 … q12)
 */
public class ZooDAO {

    // ── helpers ──────────────────────────────────────────────

    /** Returns the species_id for a given species name. */
    private int speciesId(Connection c, String name) throws SQLException {
        String sql = "SELECT species_id FROM species WHERE species_name = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        throw new SQLException("Unknown species: " + name);
    }

    /**
     * Pretty-prints a ResultSet as a padded ASCII table.
     * Column widths are determined by the data.
     */
    private void printTable(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        int[] widths = new int[cols + 1];

        // seed with header widths
        for (int i = 1; i <= cols; i++) {
            widths[i] = meta.getColumnLabel(i).length();
        }

        // buffer all rows so we can measure widths
        java.util.List<String[]> rows = new java.util.ArrayList<>();
        while (rs.next()) {
            String[] row = new String[cols + 1];
            for (int i = 1; i <= cols; i++) {
                String val = rs.getString(i);
                row[i] = (val == null) ? "NULL" : val;
                if (row[i].length() > widths[i]) widths[i] = row[i].length();
            }
            rows.add(row);
        }

        // build separator line
        StringBuilder sep = new StringBuilder("+");
        for (int i = 1; i <= cols; i++) sep.append("-".repeat(widths[i] + 2)).append("+");

        System.out.println(sep);
        // header
        StringBuilder hdr = new StringBuilder("|");
        for (int i = 1; i <= cols; i++) {
            hdr.append(String.format(" %-" + widths[i] + "s |", meta.getColumnLabel(i)));
        }
        System.out.println(hdr);
        System.out.println(sep);
        // data
        for (String[] row : rows) {
            StringBuilder line = new StringBuilder("|");
            for (int i = 1; i <= cols; i++) {
                line.append(String.format(" %-" + widths[i] + "s |", row[i]));
            }
            System.out.println(line);
        }
        System.out.println(sep);
        System.out.printf("  %d row(s)%n%n", rows.size());
    }

    private void run(String header, String sql) {
        System.out.println("\n=== " + header + " ===");
        try (Connection c  = DBConnection.getConnection();
             Statement  st = c.createStatement();
             ResultSet  rs = st.executeQuery(sql)) {
            printTable(rs);
        } catch (SQLException e) {
            System.err.println("[DB] Query error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  INSERT operations
    // ══════════════════════════════════════════════════════════

    /**
     * Inserts a predator (Lion or Tiger) into the database.
     *
     * @param name         predator name
     * @param age          current age
     * @param weight       weight in kg
     * @param isFemale     true = female
     * @param type         "Lion" or "Tiger"
     * @param zooId        target zoo (usually 1)
     * @return the new animal_id, or -1 on error
     */
    public int insertPredator(String name, int age, double weight,
                              boolean isFemale, String type, int zooId) {
        double food;
        if (type.equals("Lion")) {
            food = Math.min(weight * age * (isFemale ? 0.03 : 0.02), 25.0);
        } else {
            food = weight * age * (isFemale ? 0.03 : 0.02);
        }

        String sqlAnimal  = "INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount) VALUES (?,?,?,100,?)";
        String sqlPred    = "INSERT INTO predator (animal_id, name, weight, is_female, predator_type) VALUES (?,?,?,?,?)";

        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            int animalId;
            try (PreparedStatement ps = c.prepareStatement(sqlAnimal, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, zooId);
                ps.setInt(2, speciesId(c, type));
                ps.setInt(3, age);
                ps.setDouble(4, food);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                animalId = keys.getInt(1);
            }
            try (PreparedStatement ps = c.prepareStatement(sqlPred)) {
                ps.setInt(1, animalId);
                ps.setString(2, name);
                ps.setDouble(3, weight);
                ps.setBoolean(4, isFemale);
                ps.setString(5, type);
                ps.executeUpdate();
            }
            c.commit();
            System.out.printf("[DB] Predator '%s' inserted (animal_id=%d).%n", name, animalId);
            return animalId;
        } catch (SQLException e) {
            System.err.println("[DB] insertPredator error: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Inserts a penguin into the database.
     * If isLeader=true and another leader already exists,
     * the trigger trg_penguin_single_leader will raise an error.
     */
    public int insertPenguin(String name, int age, double height,
                             boolean isLeader, int zooId) {
        double food = isLeader ? 2.0 : 1.0;
        String sqlAnimal = "INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount) VALUES (?,?,?,100,?)";
        String sqlPeng   = "INSERT INTO penguin (animal_id, name, height, is_leader) VALUES (?,?,?,?)";

        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            int animalId;
            try (PreparedStatement ps = c.prepareStatement(sqlAnimal, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, zooId);
                ps.setInt(2, speciesId(c, "Penguin"));
                ps.setInt(3, age);
                ps.setDouble(4, food);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                animalId = keys.getInt(1);
            }
            try (PreparedStatement ps = c.prepareStatement(sqlPeng)) {
                ps.setInt(1, animalId);
                ps.setString(2, name);
                ps.setDouble(3, height);
                ps.setBoolean(4, isLeader);
                ps.executeUpdate();
            }
            c.commit();
            System.out.printf("[DB] Penguin '%s' inserted (animal_id=%d).%n", name, animalId);
            return animalId;
        } catch (SQLException e) {
            System.err.println("[DB] insertPenguin error: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Inserts a fish into the database including its colours.
     *
     * @param age       age in years
     * @param length    body length in cm
     * @param pattern   e.g. "Smooth", "Stripes", "Dots", "Spots"
     * @param fishType  "Gold", "Clown", or "Aquarium"
     * @param colors    array of colour strings (e.g. {"ORANGE","BLACK","WHITE"})
     * @param zooId     target zoo
     */
    public int insertFish(int age, double length, String pattern,
                          String fishType, String[] colors, int zooId) {
        double food;
        switch (fishType) {
            case "Clown"    -> food = 2.0;
            case "Aquarium" -> food = (age >= 3) ? length + 3 : 3;
            default         -> food = 1.0;  // Gold
        }

        String sqlAnimal = "INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount) VALUES (?,?,?,100,?)";
        String sqlFish   = "INSERT INTO fish (animal_id, length, pattern, fish_type) VALUES (?,?,?,?)";
        String sqlColor  = "INSERT INTO fish_color (fish_id, color_name) VALUES (?,?)";

        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            int animalId, fishId;
            try (PreparedStatement ps = c.prepareStatement(sqlAnimal, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, zooId);
                ps.setInt(2, speciesId(c, fishType + "Fish"));
                ps.setInt(3, age);
                ps.setDouble(4, food);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                animalId = keys.getInt(1);
            }
            try (PreparedStatement ps = c.prepareStatement(sqlFish, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, animalId);
                ps.setDouble(2, length);
                ps.setString(3, pattern);
                ps.setString(4, fishType);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                fishId = keys.getInt(1);
            }
            try (PreparedStatement ps = c.prepareStatement(sqlColor)) {
                for (String color : colors) {
                    ps.setInt(1, fishId);
                    ps.setString(2, color);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            c.commit();
            System.out.printf("[DB] Fish (type=%s) inserted (animal_id=%d).%n", fishType, animalId);
            return animalId;
        } catch (SQLException e) {
            System.err.println("[DB] insertFish error: " + e.getMessage());
            return -1;
        }
    }

    // ══════════════════════════════════════════════════════════
    //  UPDATE operations
    // ══════════════════════════════════════════════════════════

    /** Sets a new happiness value (clamped to [0,100]). */
    public void updateHappiness(int animalId, int happiness) {
        int clamped = Math.max(0, Math.min(100, happiness));
        String sql  = "UPDATE animal SET happiness = ? WHERE animal_id = ?";
        try (Connection c  = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, clamped);
            ps.setInt(2, animalId);
            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.printf("[DB] animal_id=%d happiness → %d%n", animalId, clamped);
            else
                System.out.println("[DB] No animal found with id=" + animalId);
        } catch (SQLException e) {
            System.err.println("[DB] updateHappiness error: " + e.getMessage());
        }
    }

    /** Increments age by 1 and decreases happiness by a given amount. */
    public void ageAnimal(int animalId, int happinessDecrease) {
        String sql = "UPDATE animal SET age = age + 1, happiness = GREATEST(0, happiness - ?) WHERE animal_id = ? AND is_alive = TRUE";
        try (Connection c  = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, happinessDecrease);
            ps.setInt(2, animalId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB] ageAnimal error: " + e.getMessage());
        }
    }

    /** Resets happiness to 100 after feeding (mirrors resetAnimalHappiness()). */
    public void feedAnimal(int animalId) {
        updateHappiness(animalId, 100);
    }

    /** Updates the weight of a predator. */
    public void updatePredatorWeight(int animalId, double weight) {
        String sql = "UPDATE predator SET weight = ? WHERE animal_id = ?";
        try (Connection c  = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, weight);
            ps.setInt(2, animalId);
            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.printf("[DB] predator (animal_id=%d) weight → %.2f%n", animalId, weight);
            else
                System.out.println("[DB] No predator found with animal_id=" + animalId);
        } catch (SQLException e) {
            System.err.println("[DB] updateWeight error: " + e.getMessage());
        }
    }

    /**
     * Logs a feeding event.  The trigger trg_feeding_auto_total will
     * override the total_food value, so we pass 0.
     */
    public void logFeedingEvent(int zooId, String notes) {
        String sql = "INSERT INTO feeding_event (zoo_id, total_food, notes) VALUES (?, 0, ?)";
        try (Connection c  = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, zooId);
            ps.setString(2, notes);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next())
                System.out.printf("[DB] Feeding event logged (event_id=%d).%n", keys.getInt(1));
        } catch (SQLException e) {
            System.err.println("[DB] logFeedingEvent error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  DELETE operations
    // ══════════════════════════════════════════════════════════

    /**
     * Removes an animal and all its subtype rows (CASCADE handles FK tables).
     */
    public void deleteAnimal(int animalId) {
        String sql = "DELETE FROM animal WHERE animal_id = ?";
        try (Connection c  = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, animalId);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.printf("[DB] animal_id=%d deleted.%n", animalId);
            else          System.out.println("[DB] No animal found with id=" + animalId);
        } catch (SQLException e) {
            System.err.println("[DB] deleteAnimal error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  SEARCH operations
    // ══════════════════════════════════════════════════════════

    /** Searches predators and penguins whose name contains the given keyword. */
    public void searchByName(String keyword) {
        String sql =
            "SELECT a.animal_id, s.species_name, n.name, a.age, a.happiness " +
            "FROM animal a " +
            "JOIN species s ON a.species_id = s.species_id " +
            "JOIN ( " +
            "    SELECT animal_id, name FROM predator " +
            "    UNION ALL " +
            "    SELECT animal_id, name FROM penguin " +
            ") n ON a.animal_id = n.animal_id " +
            "WHERE a.is_alive = TRUE AND n.name LIKE ? " +
            "ORDER BY s.category, n.name";
        System.out.println("\n=== Search by name: '" + keyword + "' ===");
        try (Connection c  = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            printTable(ps.executeQuery());
        } catch (SQLException e) {
            System.err.println("[DB] searchByName error: " + e.getMessage());
        }
    }

    /** Searches all living animals belonging to the given species name. */
    public void searchBySpecies(String speciesName) {
        String sql =
            "SELECT a.animal_id, s.species_name, a.age, a.happiness, a.food_amount " +
            "FROM animal a " +
            "JOIN species s ON a.species_id = s.species_id " +
            "WHERE a.is_alive = TRUE AND s.species_name LIKE ? " +
            "ORDER BY a.age";
        System.out.println("\n=== Search by species: '" + speciesName + "' ===");
        try (Connection c  = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + speciesName + "%");
            printTable(ps.executeQuery());
        } catch (SQLException e) {
            System.err.println("[DB] searchBySpecies error: " + e.getMessage());
        }
    }

    /** Returns all living animals whose age is within [minAge, maxAge]. */
    public void searchByAgeRange(int minAge, int maxAge) {
        String sql =
            "SELECT a.animal_id, s.species_name, a.age, a.happiness " +
            "FROM animal a " +
            "JOIN species s ON a.species_id = s.species_id " +
            "WHERE a.is_alive = TRUE AND a.age BETWEEN ? AND ? " +
            "ORDER BY a.age, s.species_name";
        System.out.println("\n=== Search by age range [" + minAge + " – " + maxAge + "] ===");
        try (Connection c  = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, minAge);
            ps.setInt(2, maxAge);
            printTable(ps.executeQuery());
        } catch (SQLException e) {
            System.err.println("[DB] searchByAgeRange error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  SQL QUERIES  (Q1 – Q12)
    // ══════════════════════════════════════════════════════════

    /** Q1: All living animals with species and daily food. */
    public void q1AllLivingAnimals() {
        run("Q1: All living animals",
            "SELECT a.animal_id, s.species_name, s.category, a.age, " +
            "a.happiness, ROUND(a.food_amount,2) AS daily_food " +
            "FROM animal a JOIN species s ON a.species_id=s.species_id " +
            "WHERE a.is_alive=TRUE ORDER BY s.category, s.species_name, a.age");
    }

    /** Q2: Predator food-consumption ranking. */
    public void q2PredatorFoodRanking() {
        run("Q2: Predator food ranking",
            "SELECT p.name, p.predator_type, " +
            "CASE WHEN p.is_female THEN 'female' ELSE 'male' END AS gender, " +
            "p.weight AS weight_kg, a.age, ROUND(a.food_amount,2) AS daily_food_kg " +
            "FROM predator p JOIN animal a ON p.animal_id=a.animal_id " +
            "WHERE a.is_alive=TRUE ORDER BY a.food_amount DESC");
    }

    /** Q3: Penguins sorted by height (tallest first). */
    public void q3PenguinsByHeight() {
        run("Q3: Penguins by height",
            "SELECT pg.name, pg.height AS height_cm, " +
            "CASE WHEN pg.is_leader THEN 'YES' ELSE 'no' END AS is_leader, " +
            "a.age, a.happiness " +
            "FROM penguin pg JOIN animal a ON pg.animal_id=a.animal_id " +
            "WHERE a.is_alive=TRUE ORDER BY pg.height DESC");
    }

    /** Q4: Two most common fish colours. */
    public void q4FishColorFrequency() {
        run("Q4: Fish colour frequency (top 2)",
            "SELECT fc.color_name, COUNT(*) AS fish_count " +
            "FROM fish_color fc " +
            "JOIN fish   f ON fc.fish_id   = f.fish_id " +
            "JOIN animal a ON f.animal_id  = a.animal_id " +
            "WHERE a.is_alive=TRUE " +
            "GROUP BY fc.color_name ORDER BY fish_count DESC LIMIT 2");
    }

    /** Q5: Animals at risk – happiness below 50. */
    public void q5LowHappinessAnimals() {
        run("Q5: At-risk animals (happiness < 50)",
            "SELECT a.animal_id, s.species_name, " +
            "COALESCE(p.name, pg.name, 'Fish#' || f.fish_id) AS name, " +
            "a.age, a.happiness, s.max_age - a.age AS years_left " +
            "FROM animal a " +
            "JOIN species  s  ON a.species_id = s.species_id " +
            "LEFT JOIN predator p  ON a.animal_id = p.animal_id " +
            "LEFT JOIN penguin  pg ON a.animal_id = pg.animal_id " +
            "LEFT JOIN fish     f  ON a.animal_id = f.animal_id " +
            "WHERE a.is_alive=TRUE AND a.happiness < 50 " +
            "ORDER BY a.happiness ASC");
    }

    /** Q6: Living vs dead breakdown by species. */
    public void q6AnimalCountBySpecies() {
        run("Q6: Animal count by species",
            "SELECT s.species_name, s.category, " +
            "SUM(CASE WHEN a.is_alive THEN 1 ELSE 0 END) AS living, " +
            "SUM(CASE WHEN NOT a.is_alive THEN 1 ELSE 0 END) AS dead, " +
            "COUNT(*) AS total " +
            "FROM animal a JOIN species s ON a.species_id=s.species_id " +
            "GROUP BY s.species_id, s.species_name, s.category " +
            "ORDER BY s.category, s.species_name");
    }

    /** Q7: Average happiness by category. */
    public void q7AvgHappinessByCategory() {
        run("Q7: Avg happiness by category",
            "SELECT s.category, ROUND(AVG(a.happiness),1) AS avg, " +
            "MIN(a.happiness) AS min, MAX(a.happiness) AS max, COUNT(*) AS count " +
            "FROM animal a JOIN species s ON a.species_id=s.species_id " +
            "WHERE a.is_alive=TRUE GROUP BY s.category ORDER BY avg ASC");
    }

    /** Q8: Oldest living animal per species (age as % of lifespan). */
    public void q8OldestAnimalPerSpecies() {
        run("Q8: Oldest animal per species",
            "SELECT s.species_name, MAX(a.age) AS current_max_age, " +
            "s.max_age AS species_max_age, " +
            "ROUND(100.0*MAX(a.age)/s.max_age,1) AS pct_of_lifespan " +
            "FROM animal a JOIN species s ON a.species_id=s.species_id " +
            "WHERE a.is_alive=TRUE " +
            "GROUP BY s.species_id, s.species_name, s.max_age " +
            "ORDER BY pct_of_lifespan DESC");
    }

    /** Q9: Full death records with cause. */
    public void q9DeathRecords() {
        run("Q9: Death records",
            "SELECT dr.death_id, s.species_name, " +
            "COALESCE(p.name, pg.name, 'Fish#' || f.fish_id) AS name, " +
            "dr.cause, dr.age_at_death, dr.died_at " +
            "FROM death_record dr " +
            "JOIN animal   a  ON dr.animal_id = a.animal_id " +
            "JOIN species  s  ON a.species_id = s.species_id " +
            "LEFT JOIN predator p  ON a.animal_id = p.animal_id " +
            "LEFT JOIN penguin  pg ON a.animal_id = pg.animal_id " +
            "LEFT JOIN fish     f  ON a.animal_id = f.animal_id " +
            "ORDER BY dr.died_at DESC");
    }

    /** Q10: Each fish with all its colours (STRING_AGG). */
    public void q10FishWithColors() {
        run("Q10: Fish with colours",
            "SELECT f.fish_id, f.fish_type, f.pattern, f.length AS length_cm, " +
            "a.age, a.happiness, " +
            "STRING_AGG(fc.color_name, ', ' ORDER BY fc.color_name) AS colors " +
            "FROM fish f " +
            "JOIN animal a ON f.animal_id = a.animal_id " +
            "LEFT JOIN fish_color fc ON f.fish_id = fc.fish_id " +
            "WHERE a.is_alive=TRUE " +
            "GROUP BY f.fish_id, f.fish_type, f.pattern, f.length, a.age, a.happiness " +
            "ORDER BY f.fish_type, f.fish_id");
    }

    /** Q11: Zoo-level overview statistics. */
    public void q11ZooStatistics() {
        run("Q11: Zoo statistics",
            "SELECT z.name AS zoo_name, z.address, " +
            "COUNT(DISTINCT a.animal_id) AS total_animals, " +
            "SUM(CASE WHEN a.is_alive THEN 1 ELSE 0 END) AS living, " +
            "SUM(CASE WHEN NOT a.is_alive THEN 1 ELSE 0 END) AS dead, " +
            "ROUND(AVG(CASE WHEN a.is_alive THEN a.happiness END),1) AS avg_happiness, " +
            "ROUND(SUM(CASE WHEN a.is_alive THEN a.food_amount ELSE 0 END),2) AS total_daily_food " +
            "FROM zoo z LEFT JOIN animal a ON z.zoo_id=a.zoo_id " +
            "GROUP BY z.zoo_id, z.name, z.address");
    }

    /** Q12: Feeding-event history with running total (window function). */
    public void q12FeedingHistory() {
        run("Q12: Feeding event history",
            "SELECT fe.event_id, z.name AS zoo_name, fe.fed_at, " +
            "ROUND(fe.total_food,2) AS total_food, " +
            "ROUND(SUM(fe.total_food) OVER (PARTITION BY fe.zoo_id ORDER BY fe.fed_at),2) AS running_total, " +
            "fe.notes " +
            "FROM feeding_event fe JOIN zoo z ON fe.zoo_id=z.zoo_id " +
            "ORDER BY fe.fed_at DESC");
    }
}
