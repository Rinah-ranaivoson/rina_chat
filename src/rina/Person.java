package rina;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;

import javax.crypto.Cipher;
import java.util.ArrayList;
import java.util.Base64;


public class Person {
	private String myId;   
    private Database db = new Database();

    public Person(String id) {
    	this.myId = id;
    }
    
    public void send(String message, String receiverId, PublicKey publicKey) throws Exception {
    	byte[] ciphertext = encrypt(message, publicKey);
    	LocalDateTime currentDate = LocalDateTime.now();
        String dateStr = currentDate.toString();
        byte[] cipherdate = dateStr.getBytes();
        db.store(myId, receiverId, ciphertext ,cipherdate);
    }
    
    public ArrayList<String> read(String receiverId , PrivateKey privateKey) {
        ArrayList<String> messagesCryptesBase64 = db.read(myId, receiverId);
        ArrayList<String> decryptedMessages = new ArrayList<>();

        for (String messageCrypteBase64 : messagesCryptesBase64) {
            try {
                String decryptedText = decrypt(messageCrypteBase64, privateKey);
                decryptedMessages.add(decryptedText);
            	//System.out.println(messageCrypteBase64);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return decryptedMessages;
    }


    //-----------------------------------------------------------------------
    private static byte[] encrypt(String plaintext, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plaintext.getBytes("UTF-8"));
    }
    
    private static String decrypt(String base64Ciphertext, PrivateKey privateKey) {
        try {
            byte[] ciphertext = Base64.getDecoder().decode(base64Ciphertext);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] decryptedBytes = cipher.doFinal(ciphertext);
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Gérer les erreurs de déchiffrement de manière appropriée
        }
    }


    
    public String getId() {
    	return this.myId;
    }
    
}
