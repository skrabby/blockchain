package blockchain.core;

public class Client {
    private String name = "Guest";
    private long virtualCoins = 100;

    public Client(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getVirtualCoins() {
        return virtualCoins;
    }

    public void addVirtualCoins(long virtualCoins) {
        this.virtualCoins += virtualCoins;
    }

    public String sendCoins(Client destination, long amount) {
        if (amount > virtualCoins)
            return "Transaction is not possible. Not enough resources.";
        else {
            destination.addVirtualCoins(amount);
            virtualCoins -= amount;
            return this.name + " sent " + amount + " VC to " + destination.getName();
        }
    }
}
