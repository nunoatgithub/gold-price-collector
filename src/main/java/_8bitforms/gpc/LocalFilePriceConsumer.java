package _8bitforms.gpc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LocalFilePriceConsumer implements PriceConsumer {

    private PrintWriter printWriter;

    public LocalFilePriceConsumer(String priceName) throws PriceConsumerException {
        try {
            printWriter = new PrintWriter(new FileWriter(new File("wrkdir/"+priceName), true));
            Files.createDirectories(Paths.get("wrkdir"));
        }catch (Exception e) {
            throw new PriceConsumerException(e);
        }
    }

    @Override
    public void consume(String timestamp, String price) throws PriceConsumerException {

        try {
            printWriter.println(timestamp + ":" + price);
            printWriter.flush();
        } catch(Exception e) {
            throw new PriceConsumerException(e);
        }
    }

    public void shutdown() {
        printWriter.close();
    }
}
