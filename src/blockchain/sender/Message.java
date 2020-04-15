package blockchain.sender;

import java.io.*;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {
	private List<byte[]> list;
	private long identifier;

	//The constructor of Message class builds the list that will be written to the file. The list consists of the message and the signature.
	public Message(String data, String keyFile, long identifier) throws Exception {
		this.identifier = identifier;
		data = "[ID:" + identifier + "] " + data;
		list = new ArrayList<>();
		list.add(data.getBytes());
		list.add(sign(data, keyFile));
	}

	public static Message sendMessage(String userName, long identifier, String msg) throws Exception {
		String data = msg;
		Message message = new Message(data, "UserKeys/" + userName + "/privateKey", identifier);
		message.writeToFile("DataLogs/" + userName + "/SignedData.txt");
		return message;
	}

	public long getIdentifier() {
		return identifier;
	}

	//The method that signs the data using the private key that is stored in keyFile path
	public byte[] sign(String data, String keyFile) throws Exception {
		Signature dsa = Signature.getInstance("SHA1withRSA");
		dsa.initSign(getPrivate(keyFile));
		dsa.update(data.getBytes());
		return dsa.sign();
	}

	//Method to retrieve the Private Key from a file
	public PrivateKey getPrivate(String filename) throws Exception {
		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}

	//Method to write the List of byte[] to a file
	private void writeToFile(String filename) throws IOException {
		File f = new File(filename);
		f.getParentFile().mkdirs();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
		out.writeObject(list);
		out.close();
		//	System.out.println("File is ready.");
	}
}
