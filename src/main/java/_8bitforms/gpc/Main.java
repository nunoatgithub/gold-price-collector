package _8bitforms.gpc;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jasypt.util.text.StrongTextEncryptor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

public class Main {

    private static final String URL_ENCRYPTED = "NWtz5ZoMl6tB3xAaeHkOeaE9IwjqkNak0/tMoydsEa1zjhoCExmaUj5xQaVbXqxblJMQC0C0ONzX27FOGsxO4q9B3nRAPKeV/8lJxL795Vh2jF64D4ZUNRhbSKtvtiMWeQqQ0utyxlCwMJIDT9GDcS8tOhK2IuWmgPztAe2Q4YrQ8kSDZKOOGQ==";
    private static String URL = null;

    public static void main(String[] args) throws IOException {

        StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
        textEncryptor.setPassword(args[0]);
        URL = textEncryptor.decrypt(URL_ENCRYPTED);

        Files.createDirectories(Paths.get("wrkdir"));
        try (PrintWriter printWriter =
                     new PrintWriter(new FileWriter(new File("wrkdir/live_gold_price"), true))) {

            while(true) {
                String[] priceAndTimestamp = getPrice();
                printWriter.println(priceAndTimestamp[1] + ":" + priceAndTimestamp[0]);
                printWriter.flush();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String[] getPrice() throws IOException {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(URL);
        CloseableHttpResponse response = null;

        String[] result = new String[2];
        try {
            response = httpclient.execute(httpGet);
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            String contentTail = responseBody.substring(responseBody.lastIndexOf("price"));
            //e.g. price":42623.370045,"timestamp":1520518607991}}
            String[] tokens = contentTail.split(":");
            // [0] = price", [1] = 42623.370045,"timestamp", [2] = 1520518607991}}
            Double price = Double.valueOf(tokens[1].split(",")[0]); // 42623.370045
            result[0] = String.valueOf(price / 32.1507); // price in troy onces
            result[1] = tokens[2].split("}")[0]; // 1520518607991
            return result;

        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
