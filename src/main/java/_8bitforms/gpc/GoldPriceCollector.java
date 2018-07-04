package _8bitforms.gpc;


import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.util.EC2MetadataUtils;

public class GoldPriceCollector {

    public static void main(String[] args) throws Exception {

//        EventSource eventSource = new HistoryFileEventPublisher("history")
//                .withPrimaryEventStore((timestamp, price) -> {});

        LocalFilePriceConsumer localFilePriceConsumer = new LocalFilePriceConsumer("gold_price");
//        IgnitePriceConsumer ignitePriceConsumer = new IgnitePriceConsumer("gold_price");

        PricePublisher pricePublisher = new GoldPricePublisher(args[0], Integer.parseInt(args[1]))
                .withPrimaryConsumer(localFilePriceConsumer)
//                .withSecondaryConsumer(ignitePriceConsumer)
                ;

        pricePublisher.startPublishing();

        try{
            if (System.getProperty("environment").equals("AWS")) {
                System.out.println("Stopping this AWS instance!..." );
                final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
                ec2.stopInstances(new StopInstancesRequest().withInstanceIds(EC2MetadataUtils.getInstanceId()));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
