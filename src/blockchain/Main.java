package blockchain;

//import org.springframework.util.SerializationUtils;
import javax.annotation.processing.Generated;
import javax.xml.crypto.Data;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Main {
    // Configuration
    private static final String FILE_NAME = "BlockChain.data";
    private static final int MINERS_NUMBER = 10;
    private static final int START_COMPLEXITY = 0;
    private static final long INCREASE_TRIGGER = 1L; // complexity-increase trigger (in seconds, inclusive)
    private static final long DECREASE_TRIGGER = 1L; // complexity-decrease trigger (in seconds, inclusive)

    // Generated Data to add to a new block
    private static volatile String GeneratedData = "no messages\n";

    public static synchronized void setGeneratedData(String generatedData) { GeneratedData = generatedData; }

    public static synchronized void addToGeneratedData(String data) { setGeneratedData(getGeneratedData() + data); }

    public static synchronized String getGeneratedData() { return GeneratedData; }

    // Miners storage
    private static List<Miner> miners = new ArrayList<>();

    public static BlockChain blockChain = null;
    private static volatile boolean createdBlock = false;
    private static volatile long minersDone = 0;

    // A storage for used, but incorrect magicNumbers to optimize the search process
    public static volatile Set<Integer> generated = new HashSet<>();

    public static void setCreatedBlock(boolean createdBlock) {
        Main.createdBlock = createdBlock;
    }

    public static boolean isCreatedBlock() {
        return createdBlock;
    }

    public static synchronized void minersDoneIncrement() {
        minersDone++;
    }

    public static synchronized void invokeAdder(Block block) {
        if (!createdBlock) {
            if (BCValidation.tryHash(block.getHashOfThisBlock(), blockChain.getComplexity()))
                blockChain.addBlock(block);
            createdBlock = true;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int curComplexity = START_COMPLEXITY;
        String prevHash;
        int bcSize;
        // Deserialization
        /*try {
            blockChain = (BlockChain) SerializationUtils.deserialize(FILE_NAME);
            curComplexity = blockChain.getComplexity();
            System.out.println( "========================================\n" +
                                "BlockChain has been loaded successfully!\n" +
                                "========================================\n"
            );
        }
        catch (FileNotFoundException fnf) {
            blockChain = new BlockChain(new ArrayList<Block>(), START_COMPLEXITY);
            System.out.println("The BlockChain is not created yet, let's create a new one!\n");
        }
        catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
        */
        //To pass HyperSkill Online-Checker
        blockChain = new BlockChain(new ArrayList<Block>(), START_COMPLEXITY);
        bcSize = blockChain.getBlockChain().size();
        if (bcSize == 0)
            prevHash = "0";
        else
            prevHash = blockChain.getBlockChain().get(bcSize - 1).getHashOfThisBlock();

        // Client simulator
        ClientSimulator[] clients = new ClientSimulator[5];
        for (int i = 0; i < 5; i++) {
            clients[i] = new ClientSimulator("CLIENT_" + i);
        }

        // Miners creation
        for (int i = 0; i < MINERS_NUMBER; i++) {
            miners.add(new Miner(i));
        }
        // Creating/Adding Blocks (+5 blocks)
        for (int i = bcSize; i < bcSize + 5; i++) {
            Block block = new Block(i, curComplexity, prevHash, GeneratedData);
            // Miners invocation, Hash Brute-force
            for (int j = 0; j < miners.size(); j++) {
                miners.get(j).getExecutor().submit(new BruteForceHash(miners.get(j).getID(), curComplexity, block));
            }
            // Wait until the block is forged and all miners to be returned from the task
            while (!createdBlock || minersDone != MINERS_NUMBER) {}
            // Complexity adjustment
            if (blockChain.getBlockChain().get(blockChain.getBlockChain().size() - 1).getCreationTime() <= INCREASE_TRIGGER)
                blockChain.setComplexity(curComplexity + 1);
            else if (blockChain.getBlockChain().get(blockChain.getBlockChain().size() - 1).getCreationTime() >= DECREASE_TRIGGER)
                blockChain.setComplexity(curComplexity - 1);
            curComplexity = blockChain.getComplexity();
            blockChain.getBlockChain().get(blockChain.getBlockChain().size() - 1).setCurComplexity(curComplexity);
            prevHash = blockChain.getBlockChain().get(blockChain.getBlockChain().size() - 1).getHashOfThisBlock();
            minersDone = 0;
            createdBlock = false;
            GeneratedData = "";
            for (int k = 0; k < 5; k++) {
                clients[k].sendMessageToBC("Some message");
            }
            generated.clear();
            Thread.sleep(2000);
        }
        // Shutdown threads
        for (int j = 0; j < miners.size(); j++) {
            miners.get(j).getExecutor().shutdown();
        }

        // Validation + printing
        if (BCValidation.validateTheList(blockChain.getBlockChain()))
            System.out.println(blockChain.toString());
        else
            System.out.println("BlockChain is corrupted!");

        // Serialization
       /* try { SerializationUtils.serialize(blockChain, FILE_NAME);
            System.out.println( "=======================================\n" +
                                "BlockChain has been saved successfully!\n" +
                                "=======================================\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}