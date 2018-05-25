package _8bitforms.gpc;

public class EventStoreException extends Exception {

    public EventStoreException(Exception cause) {
        super(cause);
    }

    public EventStoreException(String s) {
        super(s);
    }
}
