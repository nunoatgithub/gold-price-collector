package _8bitforms.gpc;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.util.EC2MetadataUtils;

public class Main {

    public static void main(String[] args) throws Exception {

//        EventSource eventSource = new HistoryFileEventSource("history")
//                .withPrimaryEventStore((timestamp, price) -> {});

        LocalFileEventStore localFileEventStore = new LocalFileEventStore();
        IgniteEventStore igniteEventStore = new IgniteEventStore(); // never throws exceptions
        EventSource eventSource = new RemoteEventSource(args[0], Integer.parseInt(args[1]))
                .withPrimaryEventStore(localFileEventStore)
                .withSecondaryEventStore(igniteEventStore);

        eventSource.startConsuming(() -> {
            try{
                localFileEventStore.shutdown();
            }catch(Exception e) {
                e.printStackTrace();
            }
            try {
                igniteEventStore.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try{
                if (System.getProperty("environment").equals("AWS")) {
                    System.out.println("Stopping this AWS instance!..." );
                    stopAwsEC2Instance();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void stopAwsEC2Instance() {

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        ec2.stopInstances(new StopInstancesRequest().withInstanceIds(EC2MetadataUtils.getInstanceId()));
    }
}
