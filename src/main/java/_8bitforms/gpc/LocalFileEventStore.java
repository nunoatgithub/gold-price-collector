package _8bitforms.gpc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LocalFileEventStore implements EventStore {

    private PrintWriter printWriter;

    public LocalFileEventStore() throws EventStoreException {
        try {
            printWriter =
                    new PrintWriter(new FileWriter(new File("wrkdir/live_gold_price"), true));
            Files.createDirectories(Paths.get("wrkdir"));
        }catch (Exception e) {
            throw new EventStoreException(e);
        }
    }

    public void send(String timestamp, String price) throws EventStoreException {

        try {
            printWriter.println(timestamp + ":" + price);
            printWriter.flush();
        } catch(Exception e) {
            throw new EventStoreException(e);
        }
    }

    public void shutdown() {
        printWriter.close();
    }
}
