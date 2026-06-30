-- ============================================================
--  Zoo Database Schema
--  PostgreSQL 18+  |  Normalized to 3NF
-- ============================================================
--
--  3NF JUSTIFICATION
--  -----------------
--  1NF: Every cell holds an atomic value.
--       Multi-valued colours (Fish) are split into fish_color.
--
--  2NF: All PKs are single-column (no composite PKs), so partial
--       dependency is impossible by definition.
--
--  3NF: No non-key attribute depends on another non-key attribute.
--       - species.max_age could look like it depends on
--         species.category, but category does NOT determine max_age
--         (Gold/Clown/Aquarium fish belong to the same category yet
--         have different max ages), so there is no transitive FD.
--       - fish.pattern CAN vary for AquariumFish, so pattern is
--         not determined by fish_type in general; the fixed pattern
--         for Gold/Clown is enforced via CHECK constraints instead.
--       All other tables are trivially in 3NF: every non-key column
--       depends only on the PK.
--
-- ============================================================
--
--  SETUP
--  -----
--  Run once from psql as the postgres superuser:
--
--      DROP DATABASE IF EXISTS zoo_db;
--      CREATE DATABASE zoo_db;
--      \c zoo_db
--      \i schema.sql
--      \i data.sql
--      \i triggers.sql
--
-- ============================================================

-- ── 1. zoo ───────────────────────────────────────────────────
-- Top-level container.  All animals and feeding events belong to
-- a zoo.  zoo_name is a candidate key (UNIQUE).

CREATE TABLE zoo (
    zoo_id     INT           GENERATED ALWAYS AS IDENTITY,
    name       VARCHAR(100)  NOT NULL,
    address    VARCHAR(200)  NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_zoo       PRIMARY KEY (zoo_id),
    CONSTRAINT uq_zoo_name  UNIQUE      (name)
);

-- ── 2. species ───────────────────────────────────────────────
-- Lookup / reference table.  Replaces the Java hardcoded constants
-- (MAX_AGE, food type) with normalised rows.
-- species_name is a candidate key (UNIQUE).

CREATE TABLE species (
    species_id   INT         GENERATED ALWAYS AS IDENTITY,
    species_name VARCHAR(50) NOT NULL,
    category     VARCHAR(20) NOT NULL,
    max_age      INT         NOT NULL,
    food_type    VARCHAR(50) NOT NULL,
    CONSTRAINT pk_species       PRIMARY KEY (species_id),
    CONSTRAINT uq_species_name  UNIQUE      (species_name),
    CONSTRAINT chk_max_age      CHECK       (max_age > 0),
    CONSTRAINT chk_category     CHECK       (category IN ('predator','penguin','fish'))
);

-- ── 3. animal ────────────────────────────────────────────────
-- Central "parent" entity (Table-Inheritance pattern).
-- Every Lion, Tiger, Penguin, Gold/Clown/AquariumFish has exactly
-- one row here plus one row in the matching subtype table.

CREATE TABLE animal (
    animal_id   INT           GENERATED ALWAYS AS IDENTITY,
    zoo_id      INT           NOT NULL,
    species_id  INT           NOT NULL,
    age         INT           NOT NULL,
    happiness   INT           NOT NULL DEFAULT 100,
    is_alive    BOOLEAN       NOT NULL DEFAULT TRUE,
    food_amount DECIMAL(8,2)  NOT NULL DEFAULT 0.00,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_animal            PRIMARY KEY (animal_id),
    CONSTRAINT fk_animal_zoo        FOREIGN KEY (zoo_id)     REFERENCES zoo(zoo_id)     ON DELETE CASCADE,
    CONSTRAINT fk_animal_species    FOREIGN KEY (species_id) REFERENCES species(species_id),
    CONSTRAINT chk_animal_age       CHECK (age >= 0),
    CONSTRAINT chk_animal_happiness CHECK (happiness >= 0 AND happiness <= 100)
);

-- ── 4. predator ──────────────────────────────────────────────
-- Subtype for Lions and Tigers.
-- UNIQUE(animal_id) enforces the 1-to-1 relationship with animal.

CREATE TABLE predator (
    predator_id   INT          GENERATED ALWAYS AS IDENTITY,
    animal_id     INT          NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    weight        DECIMAL(6,2) NOT NULL,
    is_female     BOOLEAN      NOT NULL,
    predator_type VARCHAR(10)  NOT NULL,
    CONSTRAINT pk_predator        PRIMARY KEY (predator_id),
    CONSTRAINT fk_predator_animal FOREIGN KEY (animal_id) REFERENCES animal(animal_id) ON DELETE CASCADE,
    CONSTRAINT uq_predator_animal UNIQUE      (animal_id),
    CONSTRAINT chk_predator_weight CHECK (weight > 0),
    CONSTRAINT chk_predator_type   CHECK (predator_type IN ('Lion','Tiger'))
);

-- ── 5. penguin ───────────────────────────────────────────────
-- Subtype for Penguins.
-- Business rule: at most one penguin per zoo may have is_leader = TRUE.
-- Enforced via trigger trg_penguin_single_leader (see triggers.sql).

CREATE TABLE penguin (
    penguin_id INT          GENERATED ALWAYS AS IDENTITY,
    animal_id  INT          NOT NULL,
    name       VARCHAR(50)  NOT NULL,
    height     DECIMAL(6,2) NOT NULL,
    is_leader  BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_penguin        PRIMARY KEY (penguin_id),
    CONSTRAINT fk_penguin_animal FOREIGN KEY (animal_id) REFERENCES animal(animal_id) ON DELETE CASCADE,
    CONSTRAINT uq_penguin_animal UNIQUE      (animal_id),
    CONSTRAINT chk_penguin_height CHECK (height > 0)
);

-- ── 6. fish ──────────────────────────────────────────────────
-- Subtype for Gold, Clown, and Aquarium fish.
-- CHECK constraints enforce the fixed pattern rules for Gold/Clown.

CREATE TABLE fish (
    fish_id   INT          GENERATED ALWAYS AS IDENTITY,
    animal_id INT          NOT NULL,
    length    DECIMAL(6,2) NOT NULL,
    pattern   VARCHAR(50)  NOT NULL,
    fish_type VARCHAR(10)  NOT NULL,
    CONSTRAINT pk_fish        PRIMARY KEY (fish_id),
    CONSTRAINT fk_fish_animal FOREIGN KEY (animal_id) REFERENCES animal(animal_id) ON DELETE CASCADE,
    CONSTRAINT uq_fish_animal UNIQUE      (animal_id),
    CONSTRAINT chk_fish_length  CHECK (length > 0),
    CONSTRAINT chk_fish_type    CHECK (fish_type IN ('Gold','Clown','Aquarium')),
    CONSTRAINT chk_fish_pattern CHECK (
        (fish_type = 'Gold'   AND pattern = 'Smooth')  OR
        (fish_type = 'Clown'  AND pattern = 'Stripes') OR
        (fish_type = 'Aquarium')
    )
);

-- ── 7. fish_color ────────────────────────────────────────────
-- One row per colour per fish.  Normalises the String[] colors
-- array from the Java code into a proper child table.
-- UNIQUE(fish_id, color_name) prevents duplicates.

CREATE TABLE fish_color (
    color_id   INT         GENERATED ALWAYS AS IDENTITY,
    fish_id    INT         NOT NULL,
    color_name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_fish_color      PRIMARY KEY (color_id),
    CONSTRAINT fk_fish_color_fish FOREIGN KEY (fish_id) REFERENCES fish(fish_id) ON DELETE CASCADE,
    CONSTRAINT uq_fish_color      UNIQUE (fish_id, color_name)
);

-- ── 8. feeding_event ─────────────────────────────────────────
-- Records each bulk-feeding action at the zoo level.
-- total_food is auto-calculated by trigger trg_feeding_auto_total.

CREATE TABLE feeding_event (
    event_id   INT           GENERATED ALWAYS AS IDENTITY,
    zoo_id     INT           NOT NULL,
    fed_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_food DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    notes      TEXT,
    CONSTRAINT pk_feeding_event PRIMARY KEY (event_id),
    CONSTRAINT fk_feeding_zoo   FOREIGN KEY (zoo_id) REFERENCES zoo(zoo_id) ON DELETE CASCADE
);

-- ── 9. death_record ──────────────────────────────────────────
-- Audit trail for animal deaths.
-- UNIQUE(animal_id) enforces that each animal dies at most once.

CREATE TABLE death_record (
    death_id     INT         GENERATED ALWAYS AS IDENTITY,
    animal_id    INT         NOT NULL,
    died_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cause        VARCHAR(20) NOT NULL,
    age_at_death INT         NOT NULL,
    CONSTRAINT pk_death_record  PRIMARY KEY (death_id),
    CONSTRAINT fk_death_animal  FOREIGN KEY (animal_id) REFERENCES animal(animal_id),
    CONSTRAINT uq_death_animal  UNIQUE (animal_id),
    CONSTRAINT chk_death_age    CHECK (age_at_death >= 0),
    CONSTRAINT chk_death_cause  CHECK (cause IN ('old_age','low_happiness'))
);
