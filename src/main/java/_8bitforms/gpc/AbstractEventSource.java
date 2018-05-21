package _8bitforms.gpc;

public abstract class AbstractEventSource implements EventSource {

    EventStore blockingEventStore;
    EventStore optionalEventStore;

    @Override
    public EventSource withBlockingEventStore(EventStore eventStore) {
        blockingEventStore = eventStore;
        return this;
    }

    @Override
    public void setOptionalEventStore(EventStore eventStore) {
        optionalEventStore = eventStore;
    }

    void publishToEventStores(String timestamp, String price) throws EventStoreException {

        blockingEventStore.send(timestamp, price);
        if (optionalEventStore != null) {
            try {
                optionalEventStore.send(timestamp, price);
            } catch (EventStoreException e) {
                e.printStackTrace();
            }
        }
    }

}
