package _8bitforms.gpc;

public class PriceConsumerException extends Exception {

    public PriceConsumerException(Exception cause) {
        super(cause);
    }

    public PriceConsumerException(String s) {
        super(s);
    }
}
