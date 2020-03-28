package blockchain;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Miner {
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public ExecutorService getExecutor() {
        return executor;
    }
}

class BruteForceHash implements Runnable {
    private Block block;
    private String blockData;
    private int complexity;
    private int minerID;

    BruteForceHash(int minerID, int complexity, String blockData, Block block) {
        this.block = block;
        this.blockData = blockData;
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
            hashToTry = blockData + block.getHashOfPrevBlock() + magicNumber;
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
