package blockchain.core;

import blockchain.sender.Message;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class BCValidation {

    public static boolean identifierIsBiggerMaxPrevBlock(List<Message> list, long maxIdentifier) {
        for (Message tmp : list) {
            if (tmp.getIdentifier() <= maxIdentifier)
                return false;
        }
        return true;
    }

    public static boolean identifierIsBigger(long msgIdentifier, long bcIdentifier) {
        return msgIdentifier >= bcIdentifier ? true : false;
    }

    public static long parseIdentifier(String message) {
        String id = message.substring(message.indexOf('[') + 1, message.indexOf(']'));
        return Long.parseLong(id.replaceAll("ID:", ""));
    }

    public static boolean tryHash(String hash, int nZero) {
        for (int i = 0; i <= nZero; i++) {
            if (i == nZero){
                if (hash.charAt(i) == '0')
                    return false;
            }
            else if (hash.charAt(i) != '0')
                return false;
        }
        return true;
    }

    public static boolean validateTheList(List<Block> BlockChain) {
        for (int i = 0; i < BlockChain.size() - 1; i++) {
            if (!(BlockChain.get(i + 1).getHashOfPrevBlock().equals(BlockChain.get(i).getHashOfThisBlock())) ||
                !(identifierIsBiggerMaxPrevBlock(BlockChain.get(i + 1).getMessages(), BlockChain.get(i).getMaxIdentifier())))
                return false;
        }
        return true;
    }
}

class Block implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final long MinerReward = 100L;

    private long timeStamp;
    private long id;
    private long magicNumber;
    private long creationTime;
    private Miner minerCreated;
    private long maxIdentifier = 0;
    private int curComplexity = -1;
    private int prevComplexity;
    private List<Message> messages = new ArrayList<>();
    private String blockData;
    private String HashOfPrevBlock;
    private String HashOfThisBlock;

    public Block(long id, int prevComplexity, String hashOfPrevBlock, String blockData, long maxIdentifier) {
        this.id = id;
        this.prevComplexity = prevComplexity;
        this.HashOfPrevBlock = hashOfPrevBlock;
        this.blockData = blockData;
        if (maxIdentifier > 1)
        this.maxIdentifier = maxIdentifier;
    }


    public void maxIdentifierIncrement() { maxIdentifier++; }

    public long getMaxIdentifier() { return maxIdentifier; }

    public List<Message> getMessages() { return messages; }

    public void addMessages(Message message) {
        messages.add(message);
    }

    public String getBlockData() { return blockData; }

    public int getCurComplexity() {
        return curComplexity;
    }

    public void setCurComplexity(int curComplexity) {
        this.curComplexity = curComplexity;
    }

    public int getPrevComplexity() {
        return prevComplexity;
    }


    public long getTimeStamp() {
        return timeStamp;
    }

    public long getId() {
        return id;
    }

    public String getHashOfPrevBlock() {
        return HashOfPrevBlock;
    }

    public String getHashOfThisBlock() {
        return HashOfThisBlock;
    }

    public long getMagicNumber() { return magicNumber; }

    public long getCreationTime() { return creationTime; }

    public void setHashOfThisBlock(String hashOfThisBlock) {
        HashOfThisBlock = hashOfThisBlock;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setMagicNumber(long magicNumber) {
        this.magicNumber = magicNumber;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public Miner getMinerCreated() {
        return minerCreated;
    }

    public void setMinerCreated(Miner minerCreated) {
        this.minerCreated = minerCreated;
    }

    @Override
    public String toString() {
        return "Block:" +
                "\nCreated by miner # " + minerCreated.getID() +
                "\nminer # " + minerCreated.getID() + " gets " + Main.MINER_REWARD + " VC" +
                "\nId: " + id +
                "\nTimestamp: " + timeStamp +
                "\nMagic number: " + magicNumber +
                "\nHash of the previous block:\n" + HashOfPrevBlock +
                "\nHash of the block:\n" + HashOfThisBlock +
                "\nBlock data:\n" + blockData +
                "Block was generating for " + creationTime + " second" + (creationTime == 1 ? "" : "s") +
                (curComplexity == prevComplexity ? "\nN stays the same" : (curComplexity > prevComplexity ?
                "\nN was increased to " + curComplexity : "\nN was decreased by " + curComplexity)) + "\n";
    }
}
class BlockChain implements Serializable {
    private static final long serialVersionUID = 1L;
    private long identifier = 1;
    private List<Block> BlockChain;
    private int complexity;

    public BlockChain(List<Block> blockChain, int complexity) {
        BlockChain = blockChain;
        this.complexity = complexity;
    }

    public long getIdentifier() { return identifier; }
    public long getIdentifierIncrement() { return identifier++; }

    public List<Block> getBlockChain() {
        return BlockChain;
    }

    public int getComplexity() {
        return complexity;
    }

    public void setComplexity(int curComplexity) {
        this.complexity = curComplexity;
    }

    public void setBlockChain(List<Block> blockChain) { BlockChain = blockChain; }

    public synchronized void addBlock(Block block) {
        if (block != null)
            this.BlockChain.add(block);
    }
    @Override
    public String toString() {
        String ret = "";
        for (int i = 0; i < BlockChain.size(); i++) {
            ret += BlockChain.get(i).toString();
            if (i != BlockChain.size() - 1)
                ret += "\n";
        }
        return ret;
    }

    public String toNString(int n) {
        if (n > BlockChain.size())
            return "Error, N is bigger than BlockChain size";
        String ret = "";
        for (int i = 0; i < n; i++) {
            ret += BlockChain.get(i).toString();
            if (i != n - 1)
                ret += "\n";
        }
        return ret;
    }
}