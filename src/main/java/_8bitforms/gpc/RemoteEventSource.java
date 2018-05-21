package _8bitforms.gpc;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jasypt.util.text.StrongTextEncryptor;

import java.io.IOException;

public class RemoteEventSource extends AbstractEventSource {

    private static final String URL_ENCRYPTED = "NWtz5ZoMl6tB3xAaeHkOeaE9IwjqkNak0/tMoydsEa1zjhoCExmaUj5xQaVbXqxblJMQC0C0ONzX27FOGsxO4q9B3nRAPKeV/8lJxL795Vh2jF64D4ZUNRhbSKtvtiMWeQqQ0utyxlCwMJIDT9GDcS8tOhK2IuWmgPztAe2Q4YrQ8kSDZKOOGQ==";
    private String url = null;
    private int count = 0;

    public RemoteEventSource(String urlDecryptionPassword) {

        StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
        textEncryptor.setPassword(urlDecryptionPassword);
        this.url = textEncryptor.decrypt(URL_ENCRYPTED);
    }

    public void startConsuming() {

        while (true) {
            try {

                String[] priceAndTimestamp = getPriceAndTimestamp(this.url);
                String timestamp = priceAndTimestamp[1];
                String price = priceAndTimestamp[0];

                publishToEventStores(timestamp, price);

                if (++count % 10 == 0) {
                    System.out.println(count + " published");
                }

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String[] getPriceAndTimestamp(String url) throws IOException {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;

        String[] result = new String[2];
        try {
            response = httpclient.execute(httpGet);
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
