package blockchain;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.Random;

public class Main {
    private static final String FILE_NAME = "BlockChain.data";
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter how many zeros the hash has to start with: ");
        int complexity = scanner.nextInt();
        System.out.println();
        BlockChain blockChain = null;
        String prevHash;
        int bcSize;
        // Deserialization
        try { blockChain = (BlockChain) SerializationUtils.deserialize(FILE_NAME); }
        catch (FileNotFoundException fnf) {
            blockChain = new BlockChain(new ArrayList<Block>());
        }
        catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
        bcSize = blockChain.getBlockChain().size();
        if (bcSize == 0)
            prevHash = "0";
        else
            prevHash = blockChain.getBlockChain().get(bcSize - 1).getHashOfThisBlock();
        // Creating/Adding Blocks (+3 blocks)
        for (int i = bcSize; i < bcSize + 3; i++) {
            long startTime = System.nanoTime();
            String thisHash = "";
            String hashToTry;
            // Some Random Data
            String blockData = String.valueOf(new Random().nextLong());
            // Hash Brute-force
            for (long magicNumber = 0; magicNumber < Long.MAX_VALUE; magicNumber++) {
                hashToTry = blockData + prevHash + magicNumber;
                thisHash = StringUtil.applySha256(hashToTry);
                if (BCValidation.tryHash(thisHash, complexity)) {
                    long endTime = System.nanoTime();
                    blockChain.addBlock(new Block(new Date().getTime(), i, prevHash, thisHash, magicNumber, (endTime - startTime) / 1_000_000_000));
                    break;
                }
            }
            prevHash = thisHash;
        }
        // Serialization
        try { SerializationUtils.serialize(blockChain, FILE_NAME); }
        catch (IOException e) {
            e.printStackTrace();
        }

        // Validation + printing
        if (BCValidation.validateTheList(blockChain.getBlockChain(), complexity))
            System.out.println(blockChain.toString());
        else
            System.out.println("BlockChain is corrupted!");
    }
}