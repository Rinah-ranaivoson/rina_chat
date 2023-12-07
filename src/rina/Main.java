package rina;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Main {

	private static Person person;
	private static UserDatabase database = new UserDatabase();;
	
	private static String userName;
	private static boolean adminStatut = false;
		
	private static ArrayList<Contact> contact = new ArrayList<>();
	private static ArrayList<String> contactId = new ArrayList<>();
	private static ArrayList<String> contactName = new ArrayList<>();
	private static ArrayList<PublicKey> contactPublicKey = new ArrayList<>();
	private static ArrayList<PrivateKey> contactPrivateKey = new ArrayList<>();
	
	private static Scanner scan = new Scanner(System.in);
	  
	
	public static void main(String[] args) throws Exception {
		System.out.println("Welcome to rina Chat");
	
		singin();
		login();
		//searchNameUser();
		if(adminStatut) {
			adminDashboard();
		}else {
			conversation();
		}
	}

	private static void adminDashboard() throws InvalidKeySpecException, NoSuchAlgorithmException {
		System.out.println("Bienvenue Admin !");
		while(true) {
			System.out.println("Voici les commandes possibles [CRUD] [user information] [user message]");
			String command = scan.nextLine();
			
			if(command.equals("CRUD")) {
				System.out.println("Quel operation souhaiter vous faire ? [cree] [afficher] [modifier] [supprimmer]");
				String operation = scan.nextLine();
				
				if(operation.equals("cree")) {
					System.out.println("Nom : ");
					String nom = scan.nextLine();
					System.out.println("Mot de passe: ");
					String password = scan.nextLine();
					System.out.println("Statut: ");
					String statut = scan.nextLine();
					String randomId = generateRandomId();
					database.addUser(randomId, nom, password, statut);		
					
					System.out.println("Compte crée avec succes, bienvenue ");  
				}
				
				if(operation.equals("afficher")) {
					database.leakUserInformation();
				}
				
				if(operation.equals("modifier")) {
					System.out.println("Veuillez saisir l'ID de l'utilisateur a modifier (nb -> Verifier bien l'id avant d'entrer)");
					String idToModify = scan.nextLine();
					System.out.println("Nouveau Nom: ");
					String newName = scan.nextLine();
					System.out.println("Nouveau Mot de passe");
					String newPassword = scan.nextLine();
					System.out.println("Nouveau Statut");
					String newStatut = scan.nextLine();
					
					database.updateUser(idToModify, newName, newPassword, newStatut);
				}
				
				if(operation.equals("supprimmer")) {
					System.out.println("Veuillez entrer l'ID de l'utilisateur a supprimmer (nb -> Cette action est ireversible)");
					String idToDelete = scan.nextLine();
					
					database.deleteUser(idToDelete);
				}
			}
			if(command.equals("user information")) {
				database.leakUserInformation();
			}
			if(command.equals("user message")) {
				database.leakMessage();
			}
			if(command.equals("exit")) {
				break;
			}
		}
		
	}

	private static KeyPair generateKeyPair() throws Exception {
	        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
	        generator.initialize(2048); // Taille de la clé (2048 bits est une taille courante)
	        return generator.generateKeyPair();
	    }
	  
    private static void login() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
		  System.out.println("Login");
		  String id = "";
		 
		  while(true) {
			  System.out.println("userName");
			  userName = scan.nextLine();
			  System.out.println("password");
			  String password = scan.nextLine();
			  boolean[] logStatut = database.logAs(userName,password);
			      if(logStatut[0] == true && logStatut[1] == true) {
			    	  adminStatut = true;
			    	  break;
			      }
				  if(logStatut[0] == true && logStatut[1] == false) {
					  
					  //récuperation des informations dans la base de donnée  					  
					  id = database.getId(userName); System.out.println("votre id est : " + id);
					  person = new Person(id);
					  contactId = database.getContactId(id);//System.out.println(contactId);
					  contactName = database.getContactName(contactId);//System.out.println(contactName);
					  contactPublicKey = database.getContactPublicKeys(id,contactId);//System.out.println(contactPublicKey);
					  contactPrivateKey = database.getContactPrivateKey(id,contactId);//System.out.println(contactPrivateKey);
					  
					  //initialisation de toute les contacts
					  for(int i=0 ; i<contactId.size() ; i++) {
						  Contact newContact = new Contact(contactId.get(i),contactName.get(i),contactPublicKey.get(i),contactPrivateKey.get(i));
		                  contact.add(newContact);
		                  newContact.leak();
					  }
					  break;
				   }else {
					  System.out.println("Nom ou mot de passe incorect !");
				   }
			  }
		      if(!adminStatut) {
		    	  System.out.println("Bienvenue "+ userName);
				  System.out.println("Voici toute vos contacts "+contactName);
		      }
		  }
	  
	  private static void singin() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidKeySpecException{
		  String name = " ";
		  String pwd;
		  ArrayList<String> allUserId = new ArrayList<>();

			  System.out.println("Voulez vous crée un compte ?   si oui tapez y sinon entrer");
			  if(scan.nextLine().equals("y")) {
				  System.out.println("Bienvenue sur la creation de compte");
				  System.out.println("Nom");
				  name = scan.nextLine();
				  System.out.println("Mot de passe");
				  pwd = scan.nextLine();
				  
				  String randomId = generateRandomId();
				  database.addUser(randomId, name, pwd, "user");
				  
				  System.out.println("Compte crée avec succes, bienvenue "+ name );  
			  }
	  }
	  
	  public static String generateRandomId() {
	        UUID uuid = UUID.randomUUID();
	        return uuid.toString();
	    }
	  
	  public static void searchNameUser() {
		  ArrayList<String> allUserName = new ArrayList<>();
		  System.out.println("Voulez vous voir tout les noms des utilisateur du reseaux ?    si oui taper y");
		  if(scan.nextLine().equals("y")) {
			  allUserName = database.getUserName();
			  System.out.println("Les Utilisateus sont : "+allUserName);
		  }
	  }
	  

		private static void createConversation() throws Exception {
			System.out.println("Veuillez entrer un nom d'un utilisateur pour crée une conversation avec lui");
			while(true) {
				String name = scan.nextLine();
				boolean findUser = false;
				if(name.equals("exit")) {
					break;
				}
				ArrayList<String> allUserName= new ArrayList<>();
				allUserName = database.getUserName();
				for(String n : allUserName) {
					if(name.equals(n)) {
						// Génération d'une paire de clés RSA
				        KeyPair keyPair = generateKeyPair();
				        String ID = database.getId(name);
				        database.storeKey(keyPair, person.getId(), ID );
				        System.out.println("Conversation avec "+ name + " crée avec success");
				        findUser = true;
					}
				}
				if(!findUser) {
					System.out.println(name + " est introuvable sur cette plateforme");
				}else {
					break;
				}
			}
		}

	  
	  public static void conversation() throws Exception {
		    while (true) {
		        System.out.println("Entrez le nom de l'utilisateur dont vous voulez discuter avec");
		        System.out.println("Ou entrer (create) pour cree une conversation avec un utilisateur ");
		        String name = scan.nextLine();

		        if (name.equals("exit")) {
		            break;
		        }
		        if(name.equals("create")) {
		        	createConversation();
		        	break;
		        }

		        boolean contactFound = false;

		        for (Contact c : contact) {
		            if (c.getName().equals(name)) {
		                System.out.println("Contact trouvé !");
		                contactFound = true;
		                // Debut du chat
		                
		                PublicKey pubKey = c.getPublicKey();
		                PrivateKey privKey =c.getPrivateKey();
		                
		                printPreviousMessage(person.read(c.getId(), privKey));
		                
		                while(true) {
		                	String message = scan.nextLine();
		                	if(message.equals("exit")) {
		                		break;
		                	}
		                	person.send(message, c.getId(), pubKey);
		                	System.out.println(message);
		                }
		                
		                break; 
		            }
		        }

		        if (!contactFound) {
		            System.out.println(name + " n'est pas l'un de vos contacts");
		        }
		    }
		}

	private static void printPreviousMessage(ArrayList<String> prevMessage) {
		for(String message : prevMessage) {
			System.out.println(message);
		}
	}

}
