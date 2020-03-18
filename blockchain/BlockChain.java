package blockchain;

import java.io.Serializable;
import java.util.List;

class BCValidation {
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

    public static boolean validateTheList(List<Block> BlockChain, int complexity) {
        for (int i = 0; i < BlockChain.size() - 1; i++) {
            if (!(BlockChain.get(i + 1).getHashOfPrevBlock().equals(BlockChain.get(i).getHashOfThisBlock())) ||
                    !tryHash(BlockChain.get(i).getHashOfThisBlock(), complexity))
                return false;
        }
        return !tryHash(BlockChain.get(BlockChain.size() - 1).getHashOfThisBlock(), complexity) ? false : true;
    }
}

class Block implements Serializable {
    private static final long serialVersionUID = 1L;

    private long timeStamp;
    private long id;
    private long magicNumber;
    private long creationTime;
    private String HashOfPrevBlock;
    private String HashOfThisBlock;

    public Block(long timeStamp, long id, String hashOfPrevBlock, String hashOfThisBlock, long magicNumber, long creationTime) {
        this.timeStamp = timeStamp;
        this.id = id;
        this.magicNumber = magicNumber;
        this.creationTime = creationTime;
        HashOfPrevBlock = hashOfPrevBlock;
        HashOfThisBlock = hashOfThisBlock;
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

    @Override
    public String toString() {
        return "Block:\n" +
                "Id: " + id +
                "\nTimestamp: " + timeStamp +
                "\nMagic number: " + magicNumber +
                "\nHash of the previous block:\n" + HashOfPrevBlock +
                "\nHash of the block:\n" + HashOfThisBlock +
                "\nBlock was generating for " + creationTime + " second" + (creationTime == 1 ? "" : "s");
    }
}

class BlockChain implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Block> BlockChain;

    public BlockChain(List<Block> blockChain) {
        BlockChain = blockChain;
    }

    public List<Block> getBlockChain() {
        return BlockChain;
    }

    public void setBlockChain(List<Block> blockChain) { BlockChain = blockChain; }

    public void addBlock(Block block) {
        if (block != null)
            this.BlockChain.add(block);
    }
    @Override
    public String toString() {
        String ret = "";
        for (int i = 0; i < BlockChain.size(); i++) {
            ret += BlockChain.get(i).toString();
            if (i != BlockChain.size() - 1)
                ret += "\n\n";
        }
        return ret;
    }

    public String toNString(long n) {
        if (n > BlockChain.size())
            return "Error, N is bigger than BlockChain size";
        String ret = "";
        for (int i = 0; i < n; i++) {
            ret += BlockChain.get(i).toString();
            if (i != n - 1)
                ret += "\n\n";
        }
        return ret;
    }
}