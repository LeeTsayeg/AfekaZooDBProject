-- ============================================================
--  Zoo Database – Initial Data
--  Mirrors the Java init() method exactly:
--    4 Lions, 3 Penguins, 10 Fish (3 Gold, 3 Clown, 4 Aquarium)
-- ============================================================

USE zoo_db;

-- ── Zoo ──────────────────────────────────────────────────────
INSERT INTO zoo (name, address) VALUES ('Ramat Gan Zoo', 'Afeka');

-- ── Species (reference data) ─────────────────────────────────
--   species_id  1 = Lion
--   species_id  2 = Tiger
--   species_id  3 = Penguin
--   species_id  4 = GoldFish
--   species_id  5 = ClownFish
--   species_id  6 = AquariumFish
INSERT INTO species (species_name, category, max_age, food_type) VALUES
    ('Lion',         'predator', 15, 'meat'),
    ('Tiger',        'predator', 15, 'meat'),
    ('Penguin',      'penguin',   6, 'fish'),
    ('GoldFish',     'fish',     12, 'algae'),
    ('ClownFish',    'fish',      8, 'algae'),
    ('AquariumFish', 'fish',     25, 'algae');

-- ═══════════════════════════════════════════════════════════
--  LIONS
--  food = MIN(weight * age * (isFemale ? 0.03 : 0.02), 25)
-- ═══════════════════════════════════════════════════════════

-- Lion 1: Lior  – female, age 14, weight 150 → 150×14×0.03 = 63 → capped 25
INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount)
    VALUES (1, 1, 14, 100, 25.00);
SET @a = LAST_INSERT_ID();
INSERT INTO predator (animal_id, name, weight, is_female, predator_type)
    VALUES (@a, 'Lior', 150.00, 1, 'Lion');

-- Lion 2: Lidor – male,   age 15, weight 120 → 120×15×0.02 = 36 → capped 25
INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount)
    VALUES (1, 1, 15, 100, 25.00);
SET @a = LAST_INSERT_ID();
INSERT INTO predator (animal_id, name, weight, is_female, predator_type)
    VALUES (@a, 'Lidor', 120.00, 0, 'Lion');

-- Lion 3: Lila  – female, age 7,  weight 100 → 100×7×0.03  = 21 → not capped
INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount)
    VALUES (1, 1, 7, 100, 21.00);
SET @a = LAST_INSERT_ID();
INSERT INTO predator (animal_id, name, weight, is_female, predator_type)
    VALUES (@a, 'Lila', 100.00, 1, 'Lion');

-- Lion 4: Liam  – male,   age 12, weight 190 → 190×12×0.02 = 45.6 → capped 25
INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount)
    VALUES (1, 1, 12, 100, 25.00);
SET @a = LAST_INSERT_ID();
INSERT INTO predator (animal_id, name, weight, is_female, predator_type)
    VALUES (@a, 'Liam', 190.00, 0, 'Lion');

-- ═══════════════════════════════════════════════════════════
--  PENGUINS
--  food: leader = 2 fish, regular = 1 fish
-- ═══════════════════════════════════════════════════════════

-- Penguin 1: Pini – leader, age 6, height 200
INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount)
    VALUES (1, 3, 6, 100, 2.00);
SET @a = LAST_INSERT_ID();
INSERT INTO penguin (animal_id, name, height, is_leader) VALUES (@a, 'Pini', 200.00, 1);

-- Penguin 2: Pnina – age 5, height 100
INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount)
    VALUES (1, 3, 5, 100, 1.00);
SET @a = LAST_INSERT_ID();
INSERT INTO penguin (animal_id, name, height, is_leader) VALUES (@a, 'Pnina', 100.00, 0);

-- Penguin 3: Pinit – age 2, height 150
INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount)
    VALUES (1, 3, 2, 100, 1.00);
SET @a = LAST_INSERT_ID();
INSERT INTO penguin (animal_id, name, height, is_leader) VALUES (@a, 'Pinit', 150.00, 0);

-- ═══════════════════════════════════════════════════════════
--  GOLD FISH  (pattern always Smooth, food_amount = 1)
-- ═══════════════════════════════════════════════════════════

INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount) VALUES (1, 4, 4, 100, 1.00);
SET @a = LAST_INSERT_ID();
INSERT INTO fish (animal_id, length, pattern, fish_type) VALUES (@a, 30.00, 'Smooth', 'Gold');
SET @f = LAST_INSERT_ID();
INSERT INTO fish_color (fish_id, color_name) VALUES (@f,'ORANGE'), (@f,'GOLD');

INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount) VALUES (1, 4, 6, 100, 1.00);
SET @a = LAST_INSERT_ID();
INSERT INTO fish (animal_id, length, pattern, fish_type) VALUES (@a, 45.00, 'Smooth', 'Gold');
SET @f = LAST_INSERT_ID();
INSERT INTO fish_color (fish_id, color_name) VALUES (@f,'YELLOW'), (@f,'BLACK');

INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount) VALUES (1, 4, 2, 100, 1.00);
SET @a = LAST_INSERT_ID();
INSERT INTO fish (animal_id, length, pattern, fish_type) VALUES (@a, 22.00, 'Smooth', 'Gold');
SET @f = LAST_INSERT_ID();
INSERT INTO fish_color (fish_id, color_name) VALUES (@f,'ORANGE');

-- ═══════════════════════════════════════════════════════════
--  CLOWN FISH  (pattern always Stripes, food_amount = 2)
-- ═══════════════════════════════════════════════════════════

INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount) VALUES (1, 5, 3, 100, 2.00);
SET @a = LAST_INSERT_ID();
INSERT INTO fish (animal_id, length, pattern, fish_type) VALUES (@a, 18.00, 'Stripes', 'Clown');
SET @f = LAST_INSERT_ID();
INSERT INTO fish_color (fish_id, color_name) VALUES (@f,'ORANGE'), (@f,'BLACK'), (@f,'WHITE');

INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount) VALUES (1, 5, 5, 100, 2.00);
SET @a = LAST_INSERT_ID();
INSERT INTO fish (animal_id, length, pattern, fish_type) VALUES (@a, 20.00, 'Stripes', 'Clown');
SET @f = LAST_INSERT_ID();
INSERT INTO fish_color (fish_id, color_name) VALUES (@f,'ORANGE'), (@f,'BLACK'), (@f,'WHITE');

INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount) VALUES (1, 5, 7, 100, 2.00);
SET @a = LAST_INSERT_ID();
INSERT INTO fish (animal_id, length, pattern, fish_type) VALUES (@a, 25.00, 'Stripes', 'Clown');
SET @f = LAST_INSERT_ID();
INSERT INTO fish_color (fish_id, color_name) VALUES (@f,'ORANGE'), (@f,'BLACK'), (@f,'WHITE');

-- ═══════════════════════════════════════════════════════════
--  AQUARIUM FISH  (food = age>=3 ? length+3 : 3)
-- ═══════════════════════════════════════════════════════════

-- age=3, length=60 → food=63
INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount) VALUES (1, 6, 3, 100, 63.00);
SET @a = LAST_INSERT_ID();
INSERT INTO fish (animal_id, length, pattern, fish_type) VALUES (@a, 60.00, 'Dots', 'Aquarium');
SET @f = LAST_INSERT_ID();
INSERT INTO fish_color (fish_id, color_name) VALUES (@f,'BLUE'), (@f,'WHITE');

-- age=1, length=80 → food=3
INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount) VALUES (1, 6, 1, 100, 3.00);
SET @a = LAST_INSERT_ID();
INSERT INTO fish (animal_id, length, pattern, fish_type) VALUES (@a, 80.00, 'Stripes', 'Aquarium');
SET @f = LAST_INSERT_ID();
INSERT INTO fish_color (fish_id, color_name) VALUES (@f,'GREEN'), (@f,'YELLOW');

-- age=7, length=110 → food=113
INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount) VALUES (1, 6, 7, 100, 113.00);
SET @a = LAST_INSERT_ID();
INSERT INTO fish (animal_id, length, pattern, fish_type) VALUES (@a, 110.00, 'Spots', 'Aquarium');
SET @f = LAST_INSERT_ID();
INSERT INTO fish_color (fish_id, color_name) VALUES (@f,'RED'), (@f,'CYAN'), (@f,'ORANGE');

-- age=4, length=50 → food=53
INSERT INTO animal (zoo_id, species_id, age, happiness, food_amount) VALUES (1, 6, 4, 100, 53.00);
SET @a = LAST_INSERT_ID();
INSERT INTO fish (animal_id, length, pattern, fish_type) VALUES (@a, 50.00, 'Smooth', 'Aquarium');
SET @f = LAST_INSERT_ID();
INSERT INTO fish_color (fish_id, color_name) VALUES (@f,'BLUE'), (@f,'GREEN');
