package cc;

import java.util.concurrent.TimeUnit;

public class Main {
    /**
     * Launches a client pointed towards localhost 8000 and a UI process
     * @param args Command line arguments, unused
     */
    public static void main(String[] args) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        TCommsClient commsClient = new TCommsClient("localhost", 8000);
        commsClient.start();
//        GUI.setGUILook("GTK+");
//        GUI gui = new GUI(commsClient);
//        gui.start();
        commsClient.startSim(
                4,
                0,
                4,
                100,
                100,
                100,
                100
        );
    }

}
