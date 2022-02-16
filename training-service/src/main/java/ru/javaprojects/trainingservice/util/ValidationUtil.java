package ru.javaprojects.trainingservice.util;

import org.slf4j.Logger;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.NonNull;
import ru.javaprojects.trainingservice.to.BaseTo;
import ru.javaprojects.trainingservice.util.exception.ErrorType;
import ru.javaprojects.trainingservice.util.exception.IllegalRequestDataException;

import javax.servlet.http.HttpServletRequest;

public class ValidationUtil {

    private ValidationUtil() {
    }

    public static void checkNew(BaseTo baseTo) {
        if (!(baseTo.getId() == null)) {
            throw new IllegalRequestDataException(baseTo + " must be new (id=null)");
        }
    }

    public static void assureIdConsistent(BaseTo baseTo, long id) {
//      conservative when you reply, but accept liberally (http://stackoverflow.com/a/32728226/548473)
        if (baseTo.getId() == null) {
            baseTo.setId(id);
        } else if (baseTo.id() != id) {
            throw new IllegalRequestDataException(baseTo + " must be with id=" + id);
        }
    }

    public static String getMessage(Throwable e) {
        return e.getMessage() != null ? e.getMessage() : e.getClass().getName();
    }

    //  https://stackoverflow.com/a/65442410/548473
    @NonNull
    public static Throwable getRootCause(@NonNull Throwable t) {
        Throwable rootCause = NestedExceptionUtils.getRootCause(t);
        return rootCause != null ? rootCause : t;
    }

    public static Throwable logAndGetRootCause(Logger log, HttpServletRequest req, Exception e, boolean logStackTrace, ErrorType errorType) {
        Throwable rootCause = getRootCause(e);
        if (logStackTrace) {
            log.error(errorType + " at request " + req.getRequestURL(), rootCause);
        } else {
            log.warn("{} at request  {}: {}", errorType, req.getRequestURL(), rootCause.toString());
        }
        return rootCause;
    }
}