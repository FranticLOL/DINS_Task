import streaming.PacketProcessor;

public class App {

    public static void main(String[] args) {
        String address = "";

        if (args != null && args.length > 0) {
            address = args[0];
        }
        try {
            PacketProcessor packetProcessor = new PacketProcessor();
            packetProcessor.process(address);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }
}
