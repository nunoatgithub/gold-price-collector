package _8bitforms.gpc;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.s3.TcpDiscoveryS3IpFinder;

public class IgnitePriceConsumer implements PriceConsumer {

    private static Ignite ignite;
    private static Thread initThread;

    private String cacheName;
    private IgniteCache cache;
    private SqlFieldsQuery insertPrice;
    private SqlFieldsQuery insertBuySell;

    static {
        initThread = new Thread(() -> {
            try {
                TcpDiscoverySpi spi = new TcpDiscoverySpi();

                TcpDiscoveryS3IpFinder ipFinder = new TcpDiscoveryS3IpFinder();
                ipFinder.setAwsCredentialsProvider(new InstanceProfileCredentialsProvider(false));
                ipFinder.setBucketName("8bitforms-ignite");
                ipFinder.setBucketEndpoint("s3.us-east-1.amazonaws.com");

                spi.setIpFinder(ipFinder);
                IgniteConfiguration cfg = new IgniteConfiguration();
                cfg.setDiscoverySpi(spi);

                cfg.setClientMode(true);

                ignite = Ignition.start(cfg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        initThread.setDaemon(true);
        initThread.start();
    }

    public IgnitePriceConsumer(String cacheName)  {

        Thread thread = new Thread(() -> {
            while (ignite == null) {
                System.out.println("Ignite cache '" + cacheName + "' is not available to receive events.");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.cacheName = cacheName;
            cache = ignite.cache(cacheName);
            insertPrice = new SqlFieldsQuery("INSERT INTO " + cacheName + " (event_time, price) VALUES (?, ?)");
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void consume(String timestamp, String price) throws PriceConsumerException {

        if (cache == null) {
            throw new PriceConsumerException("Ignite cache is not available to receive events");
        }

        try {
            cache.query(insertPrice.setArgs(Long.parseLong(timestamp), Double.parseDouble(price))).getAll();
        }catch (Exception e) {
            throw new PriceConsumerException(e);
        }
    }

    public void shutdown() {

        if (initThread != null) {
            try{
                initThread.interrupt();
            }catch(NullPointerException e) {
                // ignore
            }
            initThread = null;
        }

        if (ignite != null) {
            try{
                ignite.close();
            }catch(NullPointerException e) {
                // ignore
            }
        }
    }
}
