package rina;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class Contact {

	private String id;
	private String userName;
	private PublicKey publicKey;
	private PrivateKey privateKey;
	
	public Contact(String id, String userName, PublicKey publicKey, PrivateKey privateKey) {
		this.id = id;
		this.userName = userName;
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}
	
	public String getName() {
		return this.userName;
	}
	public PublicKey getPublicKey() {
		return this.publicKey;
	}
	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}
	public String getId() {
		return this.id;
	}
	
	public void leak() {
		System.out.println("***********************");
		System.out.println(id);
		System.out.println(userName);
		System.out.println("publicKey "+ Base64.getEncoder().encodeToString(publicKey.getEncoded()));
		System.out.println("privateKey "+ Base64.getEncoder().encodeToString(privateKey.getEncoded()));
		System.out.println("***********************");
	}


}
