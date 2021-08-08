package fr.neutronstars.database.api.exception;

public class MissingAnnotationException extends Exception
{
    public MissingAnnotationException() {
    }

    public MissingAnnotationException(String s) {
        super(s);
    }

    public MissingAnnotationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public MissingAnnotationException(Throwable throwable) {
        super(throwable);
    }

    public MissingAnnotationException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
