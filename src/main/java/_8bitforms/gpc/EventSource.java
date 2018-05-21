package _8bitforms.gpc;

public interface EventSource {

    EventSource withBlockingEventStore(EventStore eventStore);

    void setOptionalEventStore(EventStore eventStore);

    void startConsuming();
}
