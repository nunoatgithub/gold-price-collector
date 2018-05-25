package _8bitforms.gpc;

public interface EventStore {

    void send(String timestamp, String price) throws EventStoreException;

    void shutdown();
}
