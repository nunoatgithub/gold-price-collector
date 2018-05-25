package _8bitforms.gpc;

import java.time.LocalDate;

public interface EventSource {

    EventSource withPrimaryEventStore(EventStore eventStore);

    EventSource withSecondaryEventStore(EventStore eventStore);

    void startConsuming(Runnable onExit);
}
