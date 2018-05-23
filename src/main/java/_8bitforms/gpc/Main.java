package _8bitforms.gpc;

public class Main {

    public static void main(String[] args) throws Exception {

        long start = System.currentTimeMillis();

//        EventSource eventSource = new HistoryFileEventSource("history")
//                .withBlockingEventStore((timestamp, price) -> {});

        EventSource eventSource = new RemoteEventSource(args[0])
                .withBlockingEventStore(new LocalFileEventStore());

        try {
            eventSource.setOptionalEventStore(new IgniteEventStore());
        } catch (EventStoreException e) {
            System.out.println("Unable to start Ignite EventStore. Will proceed without sending events there. Caused by: \n");
            e.printStackTrace();
        }

        eventSource.startConsuming();

        long end = System.currentTimeMillis();
        System.out.println((end-start)/1000 + " secs spent!");
    }
}
