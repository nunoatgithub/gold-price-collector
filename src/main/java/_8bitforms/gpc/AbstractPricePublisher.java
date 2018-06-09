package _8bitforms.gpc;

public abstract class AbstractPricePublisher implements PricePublisher {

    private PriceConsumer primaryConsumer;
    private PriceConsumer secondaryConsumer;

    public PricePublisher withPrimaryConsumer(PriceConsumer consumer) {
        primaryConsumer = consumer;
        return this;
    }

    public PricePublisher withSecondaryConsumer(PriceConsumer consumer) {
        secondaryConsumer = consumer;
        return this;
    }

    protected void publishToConsumers(String timestamp, String price) throws PriceConsumerException {

        primaryConsumer.consume(timestamp, price);
        if (secondaryConsumer != null) {
            try {
                secondaryConsumer.consume(timestamp, price);
            } catch (PriceConsumerException e) {
                e.printStackTrace();
            }
        }
    }

    protected void shutdownConsumers() {

        try {
            primaryConsumer.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (secondaryConsumer != null) {
            try {
                secondaryConsumer.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
