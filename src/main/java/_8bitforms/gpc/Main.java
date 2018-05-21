package _8bitforms.gpc;

public class Main {

    public static void main(String[] args) throws Exception {

//        EventSource eventSource = new HistoryFileEventSource("history")
//                .withBlockingEventStore((timestamp, price) -> {});

        EventSource eventSource = new RemoteEventSource(args[0])
                .withBlockingEventStore(new LocalFileEventStore());

        if (args.length > 1) {
            try {
                eventSource.setOptionalEventStore(new IgniteEventStore(args[1]));
            } catch (EventStoreException e) {
                System.out.println("Unable to start Ignite EventStore. Will proceed without sending events there. Caused by: \n");
                e.printStackTrace();
            }
        }

        eventSource.startConsuming();
    }
}
