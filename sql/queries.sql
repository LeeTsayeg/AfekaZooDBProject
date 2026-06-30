-- ============================================================
--  Zoo Database – 12 Meaningful SQL Queries
-- ============================================================

-- ──────────────────────────────────────────────────────────────
--  Q1: All living animals with species name and category
--  Purpose: full inventory of every animal currently in the zoo.
-- ──────────────────────────────────────────────────────────────
SELECT
    a.animal_id,
    s.species_name,
    s.category,
    a.age,
    a.happiness,
    ROUND(a.food_amount, 2) AS daily_food
FROM animal a
JOIN species s ON a.species_id = s.species_id
WHERE a.is_alive = TRUE
ORDER BY s.category, s.species_name, a.age;

-- ──────────────────────────────────────────────────────────────
--  Q2: Predator food-consumption ranking
--  Purpose: who eats the most? Useful for budgeting meat supply.
-- ──────────────────────────────────────────────────────────────
SELECT
    p.name,
    p.predator_type,
    CASE WHEN p.is_female THEN 'female' ELSE 'male' END AS gender,
    p.weight                      AS weight_kg,
    a.age,
    ROUND(a.food_amount, 2)       AS daily_food_kg
FROM predator p
JOIN animal a ON p.animal_id = a.animal_id
WHERE a.is_alive = TRUE
ORDER BY a.food_amount DESC;

-- ──────────────────────────────────────────────────────────────
--  Q3: Penguins sorted by height (tallest first)
--  Purpose: identify the current leader and height order.
-- ──────────────────────────────────────────────────────────────
SELECT
    pg.name,
    pg.height                                            AS height_cm,
    CASE WHEN pg.is_leader THEN 'YES' ELSE 'no' END     AS is_leader,
    a.age,
    a.happiness
FROM penguin pg
JOIN animal a ON pg.animal_id = a.animal_id
WHERE a.is_alive = TRUE
ORDER BY pg.height DESC;

-- ──────────────────────────────────────────────────────────────
--  Q4: Fish colour frequency – two most common colours
--  Purpose: reproduces the Java colour-analytics feature in SQL.
-- ──────────────────────────────────────────────────────────────
SELECT
    fc.color_name,
    COUNT(*) AS fish_count
FROM fish_color fc
JOIN fish      f ON fc.fish_id    = f.fish_id
JOIN animal    a ON f.animal_id   = a.animal_id
WHERE a.is_alive = TRUE
GROUP BY fc.color_name
ORDER BY fish_count DESC
LIMIT 2;

-- ──────────────────────────────────────────────────────────────
--  Q5: Animals at risk – happiness below 50
--  Purpose: alert keepers before an animal's happiness hits 0.
-- ──────────────────────────────────────────────────────────────
SELECT
    a.animal_id,
    s.species_name,
    s.category,
    COALESCE(p.name, pg.name, 'Fish #' || f.fish_id) AS name,
    a.age,
    a.happiness,
    s.max_age - a.age AS years_to_natural_end
FROM animal a
JOIN species  s  ON a.species_id  = s.species_id
LEFT JOIN predator p  ON a.animal_id = p.animal_id
LEFT JOIN penguin  pg ON a.animal_id = pg.animal_id
LEFT JOIN fish     f  ON a.animal_id = f.animal_id
WHERE a.is_alive = TRUE
  AND a.happiness < 50
ORDER BY a.happiness ASC;

-- ──────────────────────────────────────────────────────────────
--  Q6: Living vs dead breakdown by species
--  Purpose: track population health over time.
-- ──────────────────────────────────────────────────────────────
SELECT
    s.species_name,
    s.category,
    SUM(CASE WHEN a.is_alive THEN 1 ELSE 0 END) AS living,
    SUM(CASE WHEN NOT a.is_alive THEN 1 ELSE 0 END) AS dead,
    COUNT(*)                                      AS total
FROM animal  a
JOIN species s ON a.species_id = s.species_id
GROUP BY s.species_id, s.species_name, s.category
ORDER BY s.category, s.species_name;

-- ──────────────────────────────────────────────────────────────
--  Q7: Average, min, and max happiness by category
--  Purpose: compare welfare across predators, penguins, and fish.
-- ──────────────────────────────────────────────────────────────
SELECT
    s.category,
    ROUND(AVG(a.happiness), 1)  AS avg_happiness,
    MIN(a.happiness)             AS min_happiness,
    MAX(a.happiness)             AS max_happiness,
    COUNT(*)                     AS animal_count
FROM animal  a
JOIN species s ON a.species_id = s.species_id
WHERE a.is_alive = TRUE
GROUP BY s.category
ORDER BY avg_happiness ASC;

-- ──────────────────────────────────────────────────────────────
--  Q8: Oldest living animal per species (age vs max-age %)
--  Purpose: find which species has individuals closest to death.
-- ──────────────────────────────────────────────────────────────
SELECT
    s.species_name,
    MAX(a.age)                                    AS current_max_age,
    s.max_age                                      AS species_max_age,
    ROUND(100.0 * MAX(a.age) / s.max_age, 1)      AS pct_of_lifespan
FROM animal  a
JOIN species s ON a.species_id = s.species_id
WHERE a.is_alive = TRUE
GROUP BY s.species_id, s.species_name, s.max_age
ORDER BY pct_of_lifespan DESC;

-- ──────────────────────────────────────────────────────────────
--  Q9: Full death records with cause
--  Purpose: audit trail – who died, when, why, at what age.
-- ──────────────────────────────────────────────────────────────
SELECT
    dr.death_id,
    s.species_name,
    COALESCE(p.name, pg.name, 'Fish #' || f.fish_id) AS name,
    dr.cause,
    dr.age_at_death,
    dr.died_at
FROM death_record dr
JOIN animal   a  ON dr.animal_id = a.animal_id
JOIN species  s  ON a.species_id = s.species_id
LEFT JOIN predator p  ON a.animal_id = p.animal_id
LEFT JOIN penguin  pg ON a.animal_id = pg.animal_id
LEFT JOIN fish     f  ON a.animal_id = f.animal_id
ORDER BY dr.died_at DESC;

-- ──────────────────────────────────────────────────────────────
--  Q10: Each fish with its full colour list (STRING_AGG)
--  Purpose: replaces the Java Arrays.toString(colors) report.
-- ──────────────────────────────────────────────────────────────
SELECT
    f.fish_id,
    f.fish_type,
    f.pattern,
    f.length                                                   AS length_cm,
    a.age,
    a.happiness,
    STRING_AGG(fc.color_name, ', ' ORDER BY fc.color_name)     AS colors
FROM fish      f
JOIN animal    a  ON f.animal_id = a.animal_id
LEFT JOIN fish_color fc ON f.fish_id = fc.fish_id
WHERE a.is_alive = TRUE
GROUP BY f.fish_id, f.fish_type, f.pattern, f.length, a.age, a.happiness
ORDER BY f.fish_type, f.fish_id;

-- ──────────────────────────────────────────────────────────────
--  Q11: Zoo-level overview statistics
--  Purpose: replaces the Java "show zoo details" menu option.
-- ──────────────────────────────────────────────────────────────
SELECT
    z.name                                                            AS zoo_name,
    z.address,
    COUNT(DISTINCT a.animal_id)                                        AS total_animals,
    SUM(CASE WHEN a.is_alive THEN 1 ELSE 0 END)                       AS living_animals,
    SUM(CASE WHEN NOT a.is_alive THEN 1 ELSE 0 END)                   AS dead_animals,
    ROUND(AVG(CASE WHEN a.is_alive THEN a.happiness END), 1)          AS avg_happiness,
    ROUND(SUM(CASE WHEN a.is_alive THEN a.food_amount ELSE 0 END), 2) AS total_daily_food
FROM zoo   z
LEFT JOIN animal a ON z.zoo_id = a.zoo_id
GROUP BY z.zoo_id, z.name, z.address;

-- ──────────────────────────────────────────────────────────────
--  Q12: Feeding-event history with running total
--  Purpose: see cumulative food cost over all feeding sessions.
-- ──────────────────────────────────────────────────────────────
SELECT
    fe.event_id,
    z.name                                       AS zoo_name,
    fe.fed_at,
    ROUND(fe.total_food, 2)                      AS total_food,
    ROUND(SUM(fe.total_food) OVER (
              PARTITION BY fe.zoo_id
              ORDER BY fe.fed_at
          ), 2)                                  AS running_total,
    fe.notes
FROM feeding_event fe
JOIN zoo z ON fe.zoo_id = z.zoo_id
ORDER BY fe.fed_at DESC;
