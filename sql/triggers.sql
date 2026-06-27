-- ============================================================
--  Zoo Database – Triggers
-- ============================================================

USE zoo_db;

-- ──────────────────────────────────────────────────────────────
--  TRIGGER 1: trg_animal_auto_die
--
--  WHEN:    AFTER UPDATE on animal
--  WHY:     The Java ageOneYear() method decreases happiness
--           by a random amount each year and removes animals
--           whose happiness <= 0 OR age > MAX_AGE.
--           This trigger replicates that logic at the database
--           level: whenever age or happiness is updated, the DB
--           automatically marks the animal as dead and creates
--           a death_record – so the audit trail is always
--           consistent regardless of which client writes to the DB.
--
--  RECURSION GUARD: the UPDATE inside the trigger sets is_alive=0.
--           The trigger re-fires but the condition OLD.is_alive=1
--           AND NEW.is_alive=1 will be FALSE for that second call,
--           so the body is skipped – no infinite recursion.
-- ──────────────────────────────────────────────────────────────

DELIMITER //

CREATE TRIGGER trg_animal_auto_die
AFTER UPDATE ON animal
FOR EACH ROW
BEGIN
    DECLARE v_max_age INT;
    DECLARE v_cause   VARCHAR(20);

    -- Act only on transitions from alive to (still) alive
    IF NEW.is_alive = 1 AND OLD.is_alive = 1 THEN

        SELECT max_age INTO v_max_age
        FROM   species
        WHERE  species_id = NEW.species_id;

        IF NEW.happiness <= 0 THEN
            SET v_cause = 'low_happiness';
        ELSEIF NEW.age > v_max_age THEN
            SET v_cause = 'old_age';
        END IF;

        IF v_cause IS NOT NULL THEN
            UPDATE animal
            SET    is_alive = 0
            WHERE  animal_id = NEW.animal_id;

            INSERT INTO death_record (animal_id, died_at, cause, age_at_death)
            VALUES (NEW.animal_id, NOW(), v_cause, NEW.age);
        END IF;
    END IF;
END //

DELIMITER ;


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
--           a SQLSTATE 45000 error so the caller knows why the
--           insert was refused.
-- ──────────────────────────────────────────────────────────────

DELIMITER //

CREATE TRIGGER trg_penguin_single_leader
BEFORE INSERT ON penguin
FOR EACH ROW
BEGIN
    DECLARE v_zoo_id         INT;
    DECLARE v_leaders_exist  INT DEFAULT 0;

    IF NEW.is_leader = 1 THEN
        SELECT a.zoo_id
        INTO   v_zoo_id
        FROM   animal a
        WHERE  a.animal_id = NEW.animal_id;

        SELECT COUNT(*)
        INTO   v_leaders_exist
        FROM   penguin  pg
        JOIN   animal   a  ON pg.animal_id = a.animal_id
        WHERE  a.zoo_id    = v_zoo_id
          AND  pg.is_leader = 1;

        IF v_leaders_exist > 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT =
                'A penguin leader already exists in this zoo. Only one leader is allowed per zoo.';
        END IF;
    END IF;
END //

DELIMITER ;


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

DELIMITER //

CREATE TRIGGER trg_feeding_auto_total
BEFORE INSERT ON feeding_event
FOR EACH ROW
BEGIN
    -- Override whatever the application sent; compute from live data.
    SET NEW.total_food = (
        SELECT COALESCE(SUM(a.food_amount), 0)
        FROM   animal a
        WHERE  a.zoo_id  = NEW.zoo_id
          AND  a.is_alive = 1
    );
END //

DELIMITER ;
