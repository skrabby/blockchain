package blockchain;

//import org.springframework.util.SerializationUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Main {
    // Configuration
    private static final String FILE_NAME = "BlockChain.data";
    private static final int MINERS_NUMBER = 10;
    private static final int START_COMPLEXITY = 0;
    private static final long INCREASE_TRIGGER = 1L; // complexity-increase trigger (in seconds, exclusive)
    private static final long DECREASE_TRIGGER = 2L; // complexity-decrease trigger (in seconds, exclusive)

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
        if (!createdBlock)
            blockChain.addBlock(block);
        Main.setCreatedBlock(true);
    }

    public static void main(String[] args) {
        int curComplexity = START_COMPLEXITY;
        String prevHash;
        int bcSize;
        // Deserialization
        try {
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

        /* To pass HyperSkill Online-Checker
        blockChain = new BlockChain(new ArrayList<Block>(), START_COMPLEXITY); */

        bcSize = blockChain.getBlockChain().size();
        if (bcSize == 0)
            prevHash = "0";
        else
            prevHash = blockChain.getBlockChain().get(bcSize - 1).getHashOfThisBlock();
        // Suppose we have 10 miners (threads) in the network
        Miner[] miners = new Miner[MINERS_NUMBER];
        for (int i = 0; i < MINERS_NUMBER; i++) {
            miners[i] = new Miner();
        }
        // Creating/Adding Blocks (+5 blocks)
        for (int i = bcSize; i < bcSize + 5; i++) {
            // Some Random Data (for instance, there could be some transactions data)
            String blockData = String.valueOf(new Random().nextLong());
            Block block = new Block(i, prevHash, curComplexity);
            // Miners invocation, Hash Brute-force
            for (int j = 0; j < miners.length; j++) {
                miners[j].getExecutor().submit(new BruteForceHash(j, curComplexity, blockData, block));
            }
            // Wait until the block is forged and all miners to be returned from the task
            while (!createdBlock || minersDone != MINERS_NUMBER) {}
            // Complexity adjustment
            if (blockChain.getBlockChain().size() != 0){
                if (blockChain.getBlockChain().get(blockChain.getBlockChain().size() - 1).getCreationTime() < INCREASE_TRIGGER)
                    blockChain.setComplexity(curComplexity + 1);
                else if (blockChain.getBlockChain().get(blockChain.getBlockChain().size() - 1).getCreationTime() > DECREASE_TRIGGER)
                    blockChain.setComplexity(curComplexity - 1);
                curComplexity = blockChain.getComplexity();
                blockChain.getBlockChain().get(blockChain.getBlockChain().size() - 1).setCurComplexity(curComplexity);
            }
            prevHash = blockChain.getBlockChain().get(blockChain.getBlockChain().size() - 1).getHashOfThisBlock();
            minersDone = 0;
            createdBlock = false;
            generated.clear();
        }
        // Shutdown threads
        for (int j = 0; j < miners.length; j++) {
            miners[j].getExecutor().shutdown();
        }

        // Validation + printing 5 blocks
        if (BCValidation.validateTheList(blockChain.getBlockChain()))
            System.out.println(blockChain.toString());
        else
            System.out.println("BlockChain is corrupted!");

        // Serialization
        try { SerializationUtils.serialize(blockChain, FILE_NAME);
            System.out.println( "=======================================\n" +
                                "BlockChain has been saved successfully!\n" +
                                "=======================================\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}