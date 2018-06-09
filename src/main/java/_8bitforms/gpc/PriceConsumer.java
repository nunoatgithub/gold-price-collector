package _8bitforms.gpc;

public interface PriceConsumer {

    void consume(String timestamp, String price) throws PriceConsumerException;

    void shutdown();
}
