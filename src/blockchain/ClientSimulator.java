package blockchain;

public class ClientSimulator {
    String name = "Guest";

    public ClientSimulator(String name) {
        this.name = name;
    }

    public void sendMessageToBC(String msg) {
        Main.addToGeneratedData(name + ": " + msg + "\n");
    }
}
