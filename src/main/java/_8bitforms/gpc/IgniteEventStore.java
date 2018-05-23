package _8bitforms.gpc;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.s3.TcpDiscoveryS3IpFinder;

public class IgniteEventStore implements EventStore {

    private IgniteCache goldPrices;
    private static final SqlFieldsQuery SQL_FIELDS_QUERY =
            new SqlFieldsQuery("INSERT INTO GOLD_PRICES (event_time, price) VALUES (?, ?)");

    public IgniteEventStore() throws EventStoreException {

        Ignition.setClientMode(true);

        try{
            TcpDiscoverySpi spi = new TcpDiscoverySpi();

            TcpDiscoveryS3IpFinder ipFinder = new TcpDiscoveryS3IpFinder();
            ipFinder.setAwsCredentialsProvider(new InstanceProfileCredentialsProvider(false));
            ipFinder.setBucketName("8bitforms-ignite");
            ipFinder.setBucketEndpoint("s3.us-east-1.amazonaws.com");

            spi.setIpFinder(ipFinder);
            IgniteConfiguration cfg = new IgniteConfiguration();
            cfg.setDiscoverySpi(spi);

            Ignite ignite = Ignition.start(cfg);
            goldPrices = ignite.cache("GOLD_PRICES");

        }catch(Exception e) {
            throw new EventStoreException(e);
        }
    }

    public void send(String timestamp, String price) throws EventStoreException {

        try {

            goldPrices.query(SQL_FIELDS_QUERY.setArgs(Long.parseLong(timestamp), Double.parseDouble(price))).getAll();

        }catch (Exception e) {
            throw new EventStoreException(e);
        }
    }
}
