package _8bitforms.gpc;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jasypt.util.text.StrongTextEncryptor;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class RemoteEventSource extends AbstractEventSource {

    private static final String URL_ENCRYPTED = "NWtz5ZoMl6tB3xAaeHkOeaE9IwjqkNak0/tMoydsEa1zjhoCExmaUj5xQaVbXqxblJMQC0C0ONzX27FOGsxO4q9B3nRAPKeV/8lJxL795Vh2jF64D4ZUNRhbSKtvtiMWeQqQ0utyxlCwMJIDT9GDcS8tOhK2IuWmgPztAe2Q4YrQ8kSDZKOOGQ==";
    private final CountDownLatch countDownLatch;
    private int count = 0;
    private ConsumingThread executingThread;
    private int upTimeInMinutes;

    public RemoteEventSource(String urlDecryptionPassword, int upTimeInMinutes) {

        StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
        textEncryptor.setPassword(urlDecryptionPassword);
        this.executingThread = new ConsumingThread(textEncryptor.decrypt(URL_ENCRYPTED));
        this.countDownLatch = new CountDownLatch(1);
        this.upTimeInMinutes = upTimeInMinutes;
    }

    public void startConsuming(Runnable onExit) {

        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                executingThread.exit();
                shutdownEventStores();
                onExit.run();
                countDownLatch.countDown();
            }
        }, upTimeInMinutes*60*1000L);

        executingThread.start();

        try {
            this.countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ConsumingThread extends Thread {

        private String url;
        private boolean stop;

        public ConsumingThread(String url) {
            super();
            this.url = url;
            this.stop = false;
        }

        public void exit() {
            this.stop = true;
            this.interrupt();
        }

        @Override
        public void run() {

            while (!stop) {
                try {

                    String[] priceAndTimestamp = getPriceAndTimestamp(this.url);
                    String timestamp = priceAndTimestamp[1];
                    String price = priceAndTimestamp[0];

                    RemoteEventSource.this.publishToEventStores(timestamp, price);

                    if (++count % 10 == 0) {
                        System.out.println(count + " published");
                    }

                    Thread.sleep(10000);

                } catch (InterruptedException e) {
                    break;
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private String[] getPriceAndTimestamp(String url) throws IOException {

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
}
