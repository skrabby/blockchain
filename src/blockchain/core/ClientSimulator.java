package blockchain.core;

public class ClientSimulator {
    String name = "Guest";

    public ClientSimulator(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
