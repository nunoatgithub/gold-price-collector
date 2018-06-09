package _8bitforms.gpc;

public interface PricePublisher {

    PricePublisher withPrimaryConsumer(PriceConsumer eventStore);

    PricePublisher withSecondaryConsumer(PriceConsumer eventStore);

    void startPublishing();
}
