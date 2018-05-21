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

public class HistoryFileEventSource extends AbstractEventSource {

    private BufferedReader bfr;

    public HistoryFileEventSource(String filePath) throws IOException {

        this.bfr = new BufferedReader(new FileReader(new File(filePath)));
    }

    public void startConsuming() {
        try {

            String line = null;
            while ((line = bfr.readLine()) != null) {
                String[] timestampAndPrice = line.split(":");
                String timestamp = timestampAndPrice[0];
                String price = timestampAndPrice[1];

                publishToEventStores(timestamp, price);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
