package ru.javaprojects.passwordresetservice.util;

import org.slf4j.Logger;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.NonNull;
import ru.javaprojects.passwordresetservice.util.exception.ErrorType;

import javax.servlet.http.HttpServletRequest;

public class ValidationUtil {
    private ValidationUtil() {
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