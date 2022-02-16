package ru.javaprojects.userservice.util;

import org.slf4j.Logger;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.NonNull;
import ru.javaprojects.userservice.model.User;
import ru.javaprojects.userservice.to.UserTo;
import ru.javaprojects.userservice.util.exception.IllegalRequestDataException;

import javax.lang.model.type.ErrorType;
import javax.servlet.http.HttpServletRequest;

public class ValidationUtil {
    private ValidationUtil() {
    }

    public static void checkNew(User user) {
        if (user.getId() != null) {
            throw new IllegalRequestDataException(user + " must be new (id=null)");
        }
    }

    public static void assureIdConsistent(UserTo userTo, long id) {
//      conservative when you reply, but accept liberally (http://stackoverflow.com/a/32728226/548473)
        if (userTo.getId() == null) {
            userTo.setId(id);
        } else if (userTo.id() != id) {
            throw new IllegalRequestDataException(userTo + " must be with id=" + id);
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