package cc;

public class Main {

    /**
     * Launches a client instance with a GUI
     * Port and host to connect to must be established during runtime
     * 
     * @param args command line arguments, unused
     */
    public static void main(String[] args) throws InterruptedException {
        GUI.setGUILook(new String[] { "GTK+", "Nimbus" });
        new GUI();
    }

}
