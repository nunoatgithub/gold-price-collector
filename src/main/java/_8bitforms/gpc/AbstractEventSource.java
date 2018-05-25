package _8bitforms.gpc;

public abstract class AbstractEventSource implements EventSource {

    private EventStore primaryEventStore;
    private EventStore secondaryEventStore;

    @Override
    public EventSource withPrimaryEventStore(EventStore eventStore) {
        primaryEventStore = eventStore;
        return this;
    }

    @Override
    public EventSource withSecondaryEventStore(EventStore eventStore) {
        secondaryEventStore = eventStore;
        return this;
    }

    void publishToEventStores(String timestamp, String price) throws EventStoreException {

        primaryEventStore.send(timestamp, price);
        if (secondaryEventStore != null) {
            try {
                secondaryEventStore.send(timestamp, price);
            } catch (EventStoreException e) {
                e.printStackTrace();
            }
        }
    }

    void shutdownEventStores() {

        try {
            primaryEventStore.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (secondaryEventStore != null) {
            try {
                secondaryEventStore.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
