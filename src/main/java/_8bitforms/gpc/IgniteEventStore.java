package _8bitforms.gpc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class IgniteEventStore implements EventStore {

    private PreparedStatement stmt;

    public IgniteEventStore(String clusterIPAddress) throws EventStoreException {

        try {
            Class.forName("org.apache.ignite.IgniteJdbcThinDriver");

            Connection connection = DriverManager.getConnection(
                    "jdbc:ignite:thin://" + clusterIPAddress + "/");

            this.stmt = connection.prepareStatement("INSERT INTO gold_prices (event_time, price) VALUES (?, ?)");

        }catch(Exception e) {
            throw new EventStoreException(e);
        }
    }

    public void send(String timestamp, String price) throws EventStoreException {

        try {
            this.stmt.setLong(1, Long.parseLong(timestamp));
            this.stmt.setDouble(2, Double.parseDouble(price));
            this.stmt.executeUpdate();
        }catch (Exception e) {
            throw new EventStoreException(e);
        }
    }
}
