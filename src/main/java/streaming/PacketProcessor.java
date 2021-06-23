package streaming;

import dao.LimitDAO;
import dao.LimitDAOImpl;
import dao.Properties;
import kafka.KafkaAlertProducer;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.io.Serializable;
import java.util.List;

public class PacketProcessor implements Serializable {
    List<PcapNetworkInterface> allDevs;
    Logger log = Logger.getLogger(PacketProcessor.class.getName());

    public PacketProcessor() {
        try {
            allDevs = Pcaps.findAllDevs();
        } catch (PcapNativeException e) {
            log.error(e.getMessage());
        }

        if (allDevs == null || allDevs.isEmpty()) {
            log.error("No NIF to capture.");
        }
    }

    public void process(String address) throws Exception {
        SparkConf conf = new SparkConf().setAppName("PacketProcessing").setMaster("local[3]").set("spark.executor.memory", "1g");
        log.info("Starting streaming context");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, new Duration(1000));
        log.info("Starting packets");

        Properties propertiesLoader = new Properties("properties/app.properties");
        LimitDAO limitDAO = new LimitDAOImpl(propertiesLoader);


        JavaReceiverInputDStream<Long> packetsSize = jssc.receiverStream(new PacketsReceiver(address));
        packetsSize.reduceByWindow(Long::sum, new Duration(300000), new Duration(300000)).foreachRDD(el -> {
            el.foreach(el1 ->
            {

                long min = limitDAO.findMin();
                long max = limitDAO.findMax();
                if (el1 <= min || el1 >= max) {
                    KafkaAlertProducer.runProducer(1, el1);
                }
                System.out.println(el1);
            });
        });

        jssc.start();
        jssc.awaitTermination();
    }
}
