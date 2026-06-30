-- ============================================================
--  Zoo Database – Triggers
-- ============================================================

-- ──────────────────────────────────────────────────────────────
--  TRIGGER 1: trg_animal_auto_die
--
--  WHEN:    BEFORE UPDATE on animal
--  WHY:     The Java ageOneYear() method decreases happiness
--           by a random amount each year and removes animals
--           whose happiness <= 0 OR age > MAX_AGE.
--           This trigger replicates that logic at the database
--           level: whenever age or happiness is updated, the DB
--           automatically marks the animal as dead and creates
--           a death_record – so the audit trail is always
--           consistent regardless of which client writes to the DB.
--
--  DESIGN:  Uses BEFORE UPDATE so it can modify NEW directly.
--           No recursive UPDATE is needed – setting NEW.is_alive
--           takes effect as part of the same statement.
-- ──────────────────────────────────────────────────────────────

CREATE OR REPLACE FUNCTION fn_animal_auto_die()
RETURNS TRIGGER AS $$
DECLARE
    v_max_age INT;
    v_cause   VARCHAR(20);
BEGIN
    IF NEW.is_alive AND OLD.is_alive THEN

        SELECT max_age INTO v_max_age
        FROM   species
        WHERE  species_id = NEW.species_id;

        IF NEW.happiness <= 0 THEN
            v_cause := 'low_happiness';
        ELSIF NEW.age > v_max_age THEN
            v_cause := 'old_age';
        END IF;

        IF v_cause IS NOT NULL THEN
            NEW.is_alive := FALSE;

            INSERT INTO death_record (animal_id, died_at, cause, age_at_death)
            VALUES (NEW.animal_id, NOW(), v_cause, NEW.age);
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_animal_auto_die
BEFORE UPDATE ON animal
FOR EACH ROW
EXECUTE FUNCTION fn_animal_auto_die();


-- ──────────────────────────────────────────────────────────────
--  TRIGGER 2: trg_penguin_single_leader
--
--  WHEN:    BEFORE INSERT on penguin
--  WHY:     The business rule is that exactly one penguin per zoo
--           can be the leader.  The Java code enforces this in the
--           addPenguin() method, but the DB has no such constraint
--           natively.  This trigger enforces the rule at the DB
--           level so it cannot be violated by any client.
--           Instead of silently ignoring the request, it raises
--           an exception so the caller knows why the insert was
--           refused.
-- ──────────────────────────────────────────────────────────────

CREATE OR REPLACE FUNCTION fn_penguin_single_leader()
RETURNS TRIGGER AS $$
DECLARE
    v_zoo_id         INT;
    v_leaders_exist  INT DEFAULT 0;
BEGIN
    IF NEW.is_leader THEN
        SELECT a.zoo_id
        INTO   v_zoo_id
        FROM   animal a
        WHERE  a.animal_id = NEW.animal_id;

        SELECT COUNT(*)
        INTO   v_leaders_exist
        FROM   penguin  pg
        JOIN   animal   a  ON pg.animal_id = a.animal_id
        WHERE  a.zoo_id    = v_zoo_id
          AND  pg.is_leader = TRUE;

        IF v_leaders_exist > 0 THEN
            RAISE EXCEPTION
                'A penguin leader already exists in this zoo. Only one leader is allowed per zoo.';
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_penguin_single_leader
BEFORE INSERT ON penguin
FOR EACH ROW
EXECUTE FUNCTION fn_penguin_single_leader();


-- ──────────────────────────────────────────────────────────────
--  TRIGGER 3: trg_feeding_auto_total
--
--  WHEN:    BEFORE INSERT on feeding_event
--  WHY:     The Java feedZooAnimals() method sums food_amount for
--           every living animal.  If the application passes
--           total_food = 0 (or forgets to compute it), the trigger
--           automatically calculates the correct total from the
--           current DB state.  This guarantees the feeding_event
--           record is always accurate, even if the Java program
--           evolves and forgets to send the total.
-- ──────────────────────────────────────────────────────────────

CREATE OR REPLACE FUNCTION fn_feeding_auto_total()
RETURNS TRIGGER AS $$
BEGIN
    NEW.total_food := (
        SELECT COALESCE(SUM(a.food_amount), 0)
        FROM   animal a
        WHERE  a.zoo_id   = NEW.zoo_id
          AND  a.is_alive  = TRUE
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_feeding_auto_total
BEFORE INSERT ON feeding_event
FOR EACH ROW
EXECUTE FUNCTION fn_feeding_auto_total();
