package ru.javaprojects.mealservice.util;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.NonNull;
import ru.javaprojects.mealservice.to.MealTo;
import ru.javaprojects.mealservice.util.exception.IllegalRequestDataException;

public class ValidationUtil {

    private ValidationUtil() {
    }

    public static void checkNew(MealTo mealTo) {
        if (!(mealTo.getId() == null)) {
            throw new IllegalRequestDataException(mealTo + " must be new (id=null)");
        }
    }

    public static void assureIdConsistent(MealTo mealTo, long id) {
//      conservative when you reply, but accept liberally (http://stackoverflow.com/a/32728226/548473)
        if (mealTo.getId() == null) {
            mealTo.setId(id);
        } else if (mealTo.getId() != id) {
            throw new IllegalRequestDataException(mealTo + " must be with id=" + id);
        }
    }

//    public static String getMessage(Throwable e) {
//        return e.getMessage() != null ? e.getMessage() : e.getClass().getName();
//    }

    //  https://stackoverflow.com/a/65442410/548473
    @NonNull
    public static Throwable getRootCause(@NonNull Throwable t) {
        Throwable rootCause = NestedExceptionUtils.getRootCause(t);
        return rootCause != null ? rootCause : t;
    }

//    public static Throwable logAndGetRootCause(Logger log, HttpServletRequest req, Exception e, boolean logStackTrace, ErrorType errorType) {
//        Throwable rootCause = getRootCause(e);
//        if (logStackTrace) {
//            log.error(errorType + " at request " + req.getRequestURL(), rootCause);
//        } else {
//            log.warn("{} at request  {}: {}", errorType, req.getRequestURL(), rootCause.toString());
//        }
//        return rootCause;
//    }
}
