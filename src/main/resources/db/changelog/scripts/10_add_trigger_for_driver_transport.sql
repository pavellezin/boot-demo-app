CREATE OR REPLACE FUNCTION enforce_transport_limit() RETURNS trigger AS $$
DECLARE
max_transport_count INTEGER := 3;
transport_count INTEGER := 0;
    must_check BOOLEAN := false;
BEGIN
    IF TG_OP = 'INSERT' THEN
        must_check := true;
END IF;

    IF TG_OP = 'UPDATE' THEN
        IF (NEW.driver_id != OLD.driver_id) THEN
            must_check := true;
END IF;
END IF;

    IF must_check THEN
        -- prevent concurrent inserts from multiple transactions
        LOCK TABLE driver_transport IN EXCLUSIVE MODE;

SELECT INTO transport_count COUNT(*)
FROM driver_transport
WHERE driver_id = NEW.driver_id AND status = 'ACTIVE';

IF transport_count >= max_transport_count THEN
            RAISE EXCEPTION 'Cannot add more than % transports for each driver.', max_transport_count;
END IF;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER enforce_transport_limit BEFORE INSERT OR UPDATE ON driver_transport
    FOR EACH ROW EXECUTE FUNCTION enforce_transport_limit();