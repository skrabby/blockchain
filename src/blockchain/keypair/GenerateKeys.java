package blockchain.keypair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;

public class GenerateKeys {

    private KeyPairGenerator keyGen;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public GenerateKeys(int keylength) throws NoSuchAlgorithmException, NoSuchProviderException {

        this.keyGen = KeyPairGenerator.getInstance("RSA");
        this.keyGen.initialize(keylength);
    }

    public void createKeys() {
        this.pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();

    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public void writeToFile(String path, byte[] key) throws IOException {

        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();

    }

    public static void generateKeys(String userName) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        GenerateKeys myKeys = new GenerateKeys(1024);
        myKeys.createKeys();
        myKeys.writeToFile("UserKeys/" + userName + "/publicKey", myKeys.getPublicKey().getEncoded());
        myKeys.writeToFile("UserKeys/" + userName + "/privateKey", myKeys.getPrivateKey().getEncoded());
    }
}