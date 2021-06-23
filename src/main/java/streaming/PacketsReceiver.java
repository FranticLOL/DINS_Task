package streaming;

import lombok.SneakyThrows;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PacketsReceiver extends Receiver<Long> implements PacketListener, Serializable {
    String address;

    public PacketsReceiver(String address) {
        super(StorageLevel.MEMORY_AND_DISK_2());
        this.address = address;
    }

    @Override
    public void onStart() {
        new Thread() {
            @SneakyThrows
            @Override
            public void run() {
                receive();
            }
        }.start();
    }

    @Override
    public void onStop() {
        // There is nothing much to do as the thread calling receive()
        // is designed to stop by itself if isStopped() returns false
    }

    private void receive() throws Exception {
        List<PcapNetworkInterface> allDevs = new ArrayList<>();
        List<PcapNetworkInterface> usingDevs = new ArrayList<>();

        try {
            allDevs = Pcaps.findAllDevs();
        } catch (PcapNativeException e) {
            System.out.println(e.getMessage());
        }

        if (allDevs == null || allDevs.isEmpty()) {
            System.out.println("No devices found.");
        }

        if (!(address == null || address.equals(""))) {
            for (PcapNetworkInterface dev : allDevs) {
                if (dev.getAddresses().stream().anyMatch(value -> value.getAddress().toString().equals(address))) {
                    usingDevs.add(dev);
                    break;
                }
            }
        } else {
            usingDevs = allDevs;
        }

        if (usingDevs.isEmpty()) {
            System.out.println("No device founded.");
        }

        int snapshotLength = 65536;
        int readTimeout = 50;
        List<PcapHandle> handles = new ArrayList<>();
        for (PcapNetworkInterface dev : usingDevs) {
            handles.add(dev.openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout));
        }

        try {
            while (true) {
                for (PcapHandle handle : handles) {
                    handle.loop(1000, this);
                }
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } catch (Throwable t) {
            System.out.println("Error receiving data");
            restart("Error receiving data", t);
        }

    }

    @Override
    public void gotPacket(Packet packet) {
        System.out.println("Received: " + packet.length() + " bytes");
        store(Long.valueOf(packet.length()));
    }
}
