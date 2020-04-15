package blockchain.core;

import blockchain.keypair.GenerateKeys;
import blockchain.receiver.VerifyMessage;
import blockchain.receiver.dataParser;
import blockchain.sender.Message;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    // Configuration
    private static final String FILE_NAME = "BlockChain.data";
    private static final int MINERS_NUMBER = 10;
    private static final int START_COMPLEXITY = 0;
    private static final long INCREASE_TRIGGER = -1L; // complexity-increase trigger (in seconds, inclusive)
    private static final long DECREASE_TRIGGER = 2L; // complexity-decrease trigger (in seconds, inclusive)
    public static final long MINER_REWARD = 100L;
    public static boolean EXIT_FLAG = false;
    // Generated Data to add to a new block
    public static volatile String GeneratedData = "";

    // Miners storage
    private static List<Miner> miners = new ArrayList<>();

    public static BlockChain blockChain = null;
    private static volatile boolean createdBlock = false;
    private static volatile long minersDone = 0;

    // A storage for used, but incorrect magicNumbers to optimize the search process
    public static volatile Set<Integer> generated = new HashSet<>();

    public static boolean isCreatedBlock() { return createdBlock; }

    public static synchronized void minersDoneIncrement() {
        minersDone++;
    }

    public static synchronized void invokeAdder(Block block) {
        if (!createdBlock) {
            if (BCValidation.tryHash(block.getHashOfThisBlock(), blockChain.getComplexity()))
                blockChain.addBlock(block);
            block.getMinerCreated().addVirtualCoins(MINER_REWARD);
            createdBlock = true;
        }
    }

    public static void main(String[] args) throws Exception {
        int curComplexity = START_COMPLEXITY;
        String prevHash;
        int bcSize;
        // Deserialization
        try {
            blockChain = (BlockChain) SerializationUtils.deserialize(FILE_NAME);
            curComplexity = blockChain.getComplexity();
            System.out.println( "========================================\n" +
                                "BlockChain has been loaded successfully!\n" +
                                "========================================\n");
            // Validation + printing the loaded BlockChain
            if (BCValidation.validateTheList(blockChain.getBlockChain()))
                System.out.println(blockChain.toString());
            else
                System.out.println("BlockChain is corrupted!");
        }
        catch (FileNotFoundException fnf) {
            blockChain = new BlockChain(new ArrayList<Block>(), START_COMPLEXITY);
            System.out.println("The BlockChain is not created yet, let's create a new one!\n");
        }
        catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }

        //To pass HyperSkill Online-Checker

        /* blockChain = new BlockChain(new ArrayList<Block>(), START_COMPLEXITY); */

        bcSize = blockChain.getBlockChain().size();
        if (bcSize == 0)
            prevHash = "0";
        else
            prevHash = blockChain.getBlockChain().get(bcSize - 1).getHashOfThisBlock();

        // Client simulator
        Client[] clients = new Client[5];
        for (int i = 0; i < 5; i++) {
            clients[i] = new Client("Client_" + i);
            GenerateKeys.generateKeys(clients[i].getName());
        }

        // Miners creation
        for (int i = 0; i < MINERS_NUMBER; i++) {
            miners.add(new Miner(i));
        }

        // Reader init
        ExecutorService reader = Executors.newSingleThreadExecutor();
        reader.submit(new dataParser());
        // Creating/Adding Blocks
        while (!EXIT_FLAG) {
            int maxSize = blockChain.getBlockChain().size();
            if (GeneratedData.equals(""))
                GeneratedData = "No transactions\n";
            Block block = new Block(blockChain.getBlockChain().size(), curComplexity, prevHash, GeneratedData,
                    maxSize > 0 ? blockChain.getBlockChain().get(maxSize - 1).getMaxIdentifier() : 0);
            // Miners invocation, Hash Brute-force
            for (int j = 0; j < miners.size(); j++) {
                miners.get(j).getExecutor().submit(new BruteForceHash(miners.get(j), curComplexity, block));
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

            // Fake transactions simulation with Private/Public Key pair and id validation

            /*  for (int k = 0; k < 3; k++) {
                int amount = ThreadLocalRandom.current().nextInt(5, 25);
                int minerInd = ThreadLocalRandom.current().nextInt(0, 10);
                int clientInd = ThreadLocalRandom.current().nextInt(0, 5);

                Client a, b;
                if (Math.random() < 0.5) { a = miners.get(minerInd); b = clients[clientInd]; }
                else { a = clients[clientInd]; b = miners.get(minerInd); }

                String logs = a.sendCoins(b, amount);
                Message message = Message.sendMessage(clients[k].getName(), blockChain.getIdentifierIncrement(), logs);
                block.maxIdentifierIncrement();
                //  Verification of message by public key
                String msg = VerifyMessage.verifyMessage(clients[k].getName()) + "\n";
                if (!msg.equals("") && !msg.equals("NotVerifiedError") &&
                        BCValidation.identifierIsBigger(BCValidation.parseIdentifier(msg), blockChain.getIdentifier() - 1)) {
                    GeneratedData += msg;
                    block.addMessages(message);
                }
            }*/

            // Clients fake chat data simulation

            /*  for (int k = 0; k < 3; k++) {
                Message message = Message.sendMessage(clients[k].getName(), blockChain.getIdentifierIncrement(), "some text");
                block.maxIdentifierIncrement();
                //  Verification of message by public key
                String msg = VerifyMessage.verifyMessage(clients[k].getName()) + "\n";
                if (!msg.equals("") && !msg.equals("NotVerifiedError") &&
                        BCValidation.identifierIsBigger(BCValidation.parseIdentifier(msg), blockChain.getIdentifier() - 1)) {
                    GeneratedData += clients[k].getName() + ": " + msg;
                    block.addMessages(message);
                }
            } */

            generated.clear();
            if (BCValidation.validateTheList(blockChain.getBlockChain())) {
                System.out.println(block.toString());
                // Serialization
                try { SerializationUtils.serialize(blockChain, FILE_NAME); }
                catch (IOException e) { e.printStackTrace(); }
            }
            else
                System.out.println("BlockChain is corrupted!");
             Thread.sleep(5000);
        }
        // Shutting down threads
        for (int j = 0; j < miners.size(); j++) {
            miners.get(j).getExecutor().shutdown();
        }
        reader.shutdown();
    }
}