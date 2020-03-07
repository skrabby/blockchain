package blockchain;

import java.util.List;

class Block {
    private long timeStamp;
    private long id;
    private String HashOfPrevBlock;
    private String HashOfThisBlock;

    public Block(long timeStamp, long id, String hashOfPrevBlock, String hashOfThisBlock) {
        this.timeStamp = timeStamp;
        this.id = id;
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

    @Override
    public String toString() {
        return "Block:\n" +
                "Id: " + id +
                "\nTimestamp: " + timeStamp +
                "\nHash of the previous block:\n" + HashOfPrevBlock +
                "\nHash of the block:\n" + HashOfThisBlock;
    }
}

class BlockChain {
    private List<Block> BlockChain;

    public BlockChain(List<Block> blockChain) {
        BlockChain = blockChain;
    }
    public List<Block> getBlockChain() {
        return BlockChain;
    }
    public boolean validateTheList() {
        for (int i = 1; i < BlockChain.size() - 1; i++){
            if (!(BlockChain.get(i + 1).getHashOfPrevBlock().equals(BlockChain.get(i).getHashOfThisBlock())))
                return false;
        }
        return true;
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