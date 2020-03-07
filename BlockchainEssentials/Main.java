package blockchain;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Block> BCList = new ArrayList<Block>();
        String prevHash = "0";
        // Creating BlockChain with the size of 10
        for (int i = 0; i < 10; i++) {
            String thisHash = new StringUtil().applySha256(String.valueOf(i));
            BCList.add(new Block(new Date().getTime(), i, prevHash, thisHash));
            prevHash = thisHash;
        }
        BlockChain blocks = new BlockChain(BCList);
        // validation + printing 5 elements
        if (blocks.validateTheList())
            System.out.println(blocks.toNString(5));
        else
            System.out.println("BlockChain is corrupted!");
    }
}