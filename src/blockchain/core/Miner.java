package blockchain.core;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Miner {
    private int ID;

    Miner(int ID){
        this.ID = ID;
    }

    public int getID() { return ID; }

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public ExecutorService getExecutor() {
        return executor;
    }
}

class BruteForceHash implements Runnable {
    private Block block;
    private String blockData;
    private String prevBlockHash;
    private int complexity;
    private int minerID;

    BruteForceHash(int minerID, int complexity, Block block) {
        this.block = block;
        this.blockData = block.getBlockData();
        this.prevBlockHash = block.getHashOfPrevBlock();
        this.complexity = complexity;
        this.minerID = minerID;
    }

    public void run() {
        int magicNumber;
        String hashToTry;
        String thisHash;
        long startTime = System.nanoTime();
        while (!Main.isCreatedBlock()) {
            magicNumber = 0 + (int) (Math.random() * Integer.MAX_VALUE - 0);
            if (Main.generated.contains(magicNumber))
                continue;
            Main.generated.add(magicNumber);
            hashToTry = blockData + prevBlockHash + magicNumber;
            thisHash = StringUtil.applySha256(hashToTry);
            if (BCValidation.tryHash(thisHash, complexity)) {
                long endTime = System.nanoTime();
                block.setTimeStamp(new Date().getTime());
                block.setHashOfThisBlock(thisHash);
                block.setMagicNumber(magicNumber);
                block.setCreationTime((endTime - startTime) / 1_000_000_000);
                block.setMinerCreated(minerID);
                Main.invokeAdder(block);
            }
        }
        Main.minersDoneIncrement();
    }
}
