package _8bitforms.gpc;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jasypt.util.text.StrongTextEncryptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class HistoryFileEventPublisher extends AbstractPricePublisher {

    private BufferedReader bfr;

    public HistoryFileEventPublisher(String filePath) throws IOException {

        this.bfr = new BufferedReader(new FileReader(new File(filePath)));
    }

    @Override
    public void startPublishing() {
        long start = System.currentTimeMillis();
        try {
            String line = null;
            while ((line = bfr.readLine()) != null) {
                String[] timestampAndPrice = line.split(":");
                String timestamp = timestampAndPrice[0];
                String price = timestampAndPrice[1];

                publishToConsumers(timestamp, price);
            }
        }catch(Exception e) {
            e.printStackTrace();
        } finally {
            long end = System.currentTimeMillis();
            System.out.println((end-start)/1000 + " secs spent!");
            shutdownConsumers();
        }
    }
}
