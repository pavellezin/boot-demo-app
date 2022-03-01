package pro.paullezin.jwtdemo.util;

import lombok.experimental.UtilityClass;
import pro.paullezin.jwtdemo.error.IllegalRequestDataException;
import pro.paullezin.jwtdemo.model.BaseEntity;


import java.util.Objects;

@UtilityClass
//todo This class can be converted to ConstraintValidator, and invoking of this class will perform automatically by
// spring. Try not to use any static methods in non-static multi-threaded processes
public class ValidationUtil {

    public static void checkNew(BaseEntity entity) {
        if (!entity.isNew()) {
            throw new IllegalRequestDataException(entity.getClass().getSimpleName() + "must be new");
        }
    }

    public static void assertConsistent(BaseEntity entity, Integer id) {
        if (entity.isNew()) {
            entity.setId(id);
        } else if (!Objects.equals(entity.getId(), id)) {
            throw new IllegalRequestDataException(entity.getClass().getSimpleName() + " must have id=" + id);
        }
    }
}
