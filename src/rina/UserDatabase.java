package rina;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.Cipher;

public class UserDatabase {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static String masterPublicKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArOKyvYKrW0xjqWPUMKGzjrkTnse7jHCdE41rOFeVQPTCEDGUud5D8qAIwDlTcq4FE4aSlqnZOyRF8Z+EvnGPkH2fCnLql5AflTLGUkmomL/RaE4eZ5Lx1Mk/B9MW/UErqBS07OiO3CphdzVqYV8BtQSN4+QqYU5keAeI+g8rpkdCKUA6r/3p0l9Lwp83Au8iOJoo2nt6/V5qYWVgIggbCcJvl2tAieSWf/vnh16yLfwmn+CXEJnE71l5k3/Sv54NUD9CvCjyjoS1JgdbhoHGEi3zLelmFCqL2MZ1gUENKyjIODyk/lAfQTkaA0S37RDxxjhGoLZABn+raFQjir406wIDAQAB";
    private static String masterPrivateKeyStr = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCs4rK9gqtbTGOpY9QwobOOuROex7uMcJ0TjWs4V5VA9MIQMZS53kPyoAjAOVNyrgUThpKWqdk7JEXxn4S+cY+QfZ8KcuqXkB+VMsZSSaiYv9FoTh5nkvHUyT8H0xb9QSuoFLTs6I7cKmF3NWphXwG1BI3j5CphTmR4B4j6DyumR0IpQDqv/enSX0vCnzcC7yI4mijae3r9XmphZWAiCBsJwm+Xa0CJ5JZ/++eHXrIt/Caf4JcQmcTvWXmTf9K/ng1QP0K8KPKOhLUmB1uGgcYSLfMt6WYUKovYxnWBQQ0rKMg4PKT+UB9BORoDRLftEPHGOEagtkAGf6toVCOKvjTrAgMBAAECggEAIWj588ehx9X+WIjTQ8asBYPct82Pf/dvU4z70HqUfGYQbsK2JClA5k1pA4TQjJP2pSxXfcg7E8PRxHz4UTlu7hRovMMydLHEeLfEPbjAXSyMkmSuWYO7Okzd4mw3wB9tCWFfCX1zxjZmeXwYH9MVaF8F8KlySzW0EsIaBYETBWHkfmWZLCF/DEbHnZVAUZs69tmeFqm0CrnoC1Qe2HmI28A2VpSirTL4SPGstTWTwCEj8+4DUNWCtdrbWZr/u0JjymNJ6mzv53OBCehFYRgpZwr5fpJS+cbpUC4/pIebBkIdjX8aHKFMr4GQmqzM9cowGtWJr6tnn2WhpuhXHqpY2QKBgQDf1Ohr25Msqzr+97jfQ+BTYA63vZO/XXjeQD5fWSomcx8W7Q814pF/w8aU1fGvCGc6tZdS0cmtJrZChIReqxLXr67MUOnCLOhhJVSTeOb5LX5AnUoLggUAFBsZ0RwzIOOHDEj3wKiwN044DQ+QdaPGS3U4py+vpEttGERDBlhA9QKBgQDFu2h+FWOKFCpD2dw3ysplIRkoYVS/o2ow4ebnTdJYqHwhhzBqlrdq0uNn8IFjU7sKvSadwKAdsuWcepxDQptwjZpM3cxFNpa4HD4gGCpNzxIf98Ym7JUue4bWJN69vpG+vmxwLqXFFxdo+yGn45/oNakHmnYQav3ec29smC1yXwKBgEOJ28PH0oXU+Q08NFVyBrtBrc81beJ9Ut5FJkzg+730WLozN7a7scmKil4YnD+DWpq1dn6v/Jm5p580mzhxiFL6mNTfyVkk8c12VSnWfmNLnd/jVhdY2Sdn6bGFjmvDDcSPKWlQgdYCKiDxl/Ov+4X2+NzqXZIzEetOOjK7NGANAoGBAKTu4Ahbuv3QODkEnqaDJzQX2a5PEawQmWFQ7ZP+2/hMAc7Nv1sJgNFAO6PVf3fkvd4FTRLFPEnJkbFmrLWF97Z4xz5avCr3j9ze4gtPXOouUYYM3WJWcVGmhCBX/Nv0/AlJOZaRsoCl9EcDQ12Rhy7go6/N7bFgLYbCbyz7+tytAoGAXidwaTbjfc+os3EjfvsMs1RE3/n9jhlnZBoUILZHo0t7DWC4I+DRQEtvuWJqFCAsXwRYT4Npslh+fTmAeGStyfqjDw1SObu7D0JUlU407ZMX4+LCzpXFBve0n6fIO9E4QP7mP/WXUCosHCi+3zU7O6KliAY1PyT4FAeWUQjbIQo=";
    // Initialisation de la configuration de l'ObjectMapper
    static {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public UserDatabase() {

    }
    
    private static PublicKey masterPublicKey;
    private static PrivateKey masterPrivateKey;

    static {
        try {
            // Initialize master public key
            byte[] masterPublicKeyBytes = Base64.getDecoder().decode(masterPublicKeyStr);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(masterPublicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            masterPublicKey = keyFactory.generatePublic(publicKeySpec);

            // Initialize master private key
            byte[] masterPrivateKeyBytes = Base64.getDecoder().decode(masterPrivateKeyStr);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(masterPrivateKeyBytes);
            masterPrivateKey = keyFactory.generatePrivate(privateKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing master keys: " + e.getMessage());
        }
    }
    

    private String encryptWithMasterPublicKey(String data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, masterPublicKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error encrypting data: " + e.getMessage());
            return "";
        }
    }

    private String decryptWithMasterPrivateKey(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, masterPrivateKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error decrypting data: " + e.getMessage());
            return "";
        }
    }
    
    public void updateUser(String id, String newName, String newPassword, String newStatut) {
        try {
            // Lire le fichier JSON existant
            File file = new File("resources/userDatabase.json");
            JsonNode rootNode = objectMapper.readTree(file);

            // Vérifier si le nœud lu est un tableau JSON
            if (rootNode instanceof ArrayNode) {
                ArrayNode arrayNode = (ArrayNode) rootNode;

                // Parcourir la liste JSON pour trouver l'utilisateur avec l'ID donné
                for (JsonNode userNode : arrayNode) {
                    if (userNode.has("i") && userNode.get("i").asText().equals(id)) {
                        // Mettre à jour les informations de l'utilisateur
                        ((ObjectNode) userNode).put("n", encryptWithMasterPublicKey(newName));
                        ((ObjectNode) userNode).put("p", encryptWithMasterPublicKey(newPassword));
                        ((ObjectNode) userNode).put("s", encryptWithMasterPublicKey(newStatut));

                        // Écrire la liste mise à jour dans le fichier JSON
                        objectMapper.writeValue(file, arrayNode);

                        System.out.println("Mise à jour dans le fichier JSON réussie !");
                        return;
                    }
                }

                System.out.println("Utilisateur non trouvé avec l'ID : " + id);
            } else {
                System.out.println("Le fichier JSON ne contient pas un tableau d'utilisateurs.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la mise à jour dans le fichier JSON : " + e.getMessage());
        }
    }

    public void deleteUser(String id) {
        try {
            // Lire le fichier JSON existant
            File file = new File("resources/userDatabase.json");
            JsonNode rootNode = objectMapper.readTree(file);

            // Vérifier si le nœud lu est un tableau JSON
            if (rootNode instanceof ArrayNode) {
                ArrayNode arrayNode = (ArrayNode) rootNode;

                // Recherche de l'index de l'utilisateur avec l'ID donné
                int indexToDelete = -1;
                for (int i = 0; i < arrayNode.size(); i++) {
                    JsonNode userNode = arrayNode.get(i);
                    if (userNode.has("i") && userNode.get("i").asText().equals(id)) {
                        indexToDelete = i;
                        break;
                    }
                }

                // Supprimer l'utilisateur de la liste JSON s'il a été trouvé
                if (indexToDelete != -1) {
                    arrayNode.remove(indexToDelete);

                    // Écrire la liste mise à jour dans le fichier JSON
                    objectMapper.writeValue(file, arrayNode);

                    System.out.println("Suppression dans le fichier JSON réussie !");
                } else {
                    System.out.println("Utilisateur non trouvé avec l'ID : " + id);
                }
            } else {
                System.out.println("Le fichier JSON ne contient pas un tableau d'utilisateurs.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la suppression dans le fichier JSON : " + e.getMessage());
        }
    }



    public void addUser(String id, String name, String password, String statut) {
    	// Chiffrer le mot de passe en MD5
    	String encryptedName = encryptWithMasterPublicKey(name);
        String encryptedPassword = encryptWithMasterPublicKey(password);
        String encryptedStatut = encryptWithMasterPublicKey(statut);
    	
        // Créer un objet représentant l'utilisateur
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("i", id);
        userNode.put("n", encryptedName);
        userNode.put("p", encryptedPassword);
        userNode.put("s", encryptedStatut);

        // Appeler une méthode pour ajouter l'objet au fichier JSON
        addToJsonFile(userNode, "resources/userDatabase.json");
    }

    // Méthode pour ajouter un objet JSON dans un fichier existant
    private void addToJsonFile(JsonNode newNode, String fileName) {
        try {
            // Lire le fichier JSON existant
            File file = new File(fileName);
            ArrayNode arrayNode;

            if (file.exists()) {
                // Si le fichier existe, lisez son contenu
                JsonNode rootNode = objectMapper.readTree(file);

                // Vérifier si le nœud lu est un tableau JSON
                if (rootNode instanceof ArrayNode) {
                    arrayNode = (ArrayNode) rootNode;
                } else {
                    // Si ce n'est pas un tableau, créez une nouvelle liste JSON
                    arrayNode = objectMapper.createArrayNode();
                }
            } else {
                // Si le fichier n'existe pas, créez une nouvelle liste JSON
                arrayNode = objectMapper.createArrayNode();
            }

            // Ajouter le nouvel objet à la liste JSON
            arrayNode.add(newNode);

            // Écrire la liste mise à jour dans le fichier JSON
            objectMapper.writeValue(file, arrayNode);

            System.out.println("Ajout dans le fichier JSON réussi !");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'écriture dans le fichier JSON : " + e.getMessage());
        }
    }

    
   /* private String hashPassword(String password) {
        try {
            // Créer une instance de MessageDigest avec l'algorithme MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Mettre le mot de passe dans le MessageDigest
            md.update(password.getBytes());

            // Récupérer le hachage en bytes
            byte[] bytes = md.digest();

            // Convertir les bytes en format hexadécimal
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }

            // Retourner le hachage MD5 en format hexadécimal
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // Gérer l'exception appropriée
            return null;
        }
    }*/
    

    //------------------------------------------------------------------------------------
    public void storeKey(KeyPair keyPair, String userId1, String userId2) {
        ObjectNode keyPairNode = objectMapper.createObjectNode();
        keyPairNode.put("d1", userId1);
        keyPairNode.put("d2", userId2);

        // Encoder la clé publique en base64
        String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        keyPairNode.put("pu", publicKeyBase64);

        // Encoder la clé privée en base64
        String privateKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        keyPairNode.put("pr", privateKeyBase64);

        // Ajouter l'objet au fichier JSON
        addToJsonFile(keyPairNode, "resources/keypairs.json");
    }

    
    
    public boolean[] logAs(String userName, String password) {
    	boolean[] logState = {false,false};
        try {
            // Lire le fichier JSON existant
            File file = new File("resources/userDatabase.json");
            ArrayNode arrayNode;

            if (file.exists()) {
                // Si le fichier existe, lisez son contenu
                JsonNode rootNode = objectMapper.readTree(file);

                // Vérifier si le nœud lu est un tableau JSON
                if (rootNode instanceof ArrayNode) {
                    arrayNode = (ArrayNode) rootNode;
                } else {
                    // Si ce n'est pas un tableau, retourner false (aucun utilisateur à vérifier)
                    return logState;
                }

                // Parcourir les utilisateurs dans le fichier JSON
                for (JsonNode userNode : arrayNode) {
                    // Récupérer les informations de l'utilisateur
                    String storedEncryptedUserName = userNode.get("n").asText();
                    String storedEncryptedPassword = userNode.get("p").asText();
                    String storedEncryptedStatut = userNode.get("s").asText();
                    String storedPassword = decryptWithMasterPrivateKey(storedEncryptedPassword);
                    String storedUserName = decryptWithMasterPrivateKey(storedEncryptedUserName);
                    String storedUserState = decryptWithMasterPrivateKey(storedEncryptedStatut);


                    // Vérifier les identifiants
                    if (userName.equals(storedUserName) && storedPassword.equals(storedPassword)) {
                        System.out.println("Connexion réussie !");
                        logState[0] = true;
                        if(storedUserState.equals("admin")) {
                        	logState[1] = true;
                        }
                        return logState; // Identifiants valides
                    }
                }
            }

            // Aucun utilisateur correspondant trouvé
            System.out.println("Identifiants incorrects.");
            return logState;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la lecture du fichier JSON : " + e.getMessage());
            return logState;
        }
    }

    
    public String getId(String userName) {
        try {
            // Lire le fichier JSON existant
            File file = new File("resources/userDatabase.json");
            ArrayNode arrayNode;

            if (file.exists()) {
                // Si le fichier existe, lisez son contenu
                JsonNode rootNode = objectMapper.readTree(file);

                // Vérifier si le nœud lu est un tableau JSON
                if (rootNode instanceof ArrayNode) {
                    arrayNode = (ArrayNode) rootNode;
                } else {
                    // Si ce n'est pas un tableau, retourner null (aucun utilisateur à vérifier)
                    return null;
                }

                // Parcourir les utilisateurs dans le fichier JSON
                for (JsonNode userNode : arrayNode) {
                    // Récupérer les informations de l'utilisateur
                    String storedUserName = userNode.get("n").asText();
                    String storedUserId = userNode.get("i").asText();

                    // Vérifier si le nom d'utilisateur correspond
                    if (userName.equals(decryptWithMasterPrivateKey(storedUserName))) {
                        return storedUserId; // Retourner l'ID correspondant 
                    }
                }
            }

            // Aucun utilisateur correspondant trouvé
            System.out.println("Utilisateur non trouvé.");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la lecture du fichier JSON : " + e.getMessage());
            return null;
        }
    }


    public ArrayList<String> getContactId(String id) {
        ArrayList<String> contactIds = new ArrayList<>();

        try {
            // Lire le fichier JSON des paires de clés
            File keyPairsFile = new File("resources/keypairs.json");
            ArrayNode keyPairsArray;

            if (keyPairsFile.exists()) {
                // Si le fichier existe, lisez son contenu
                JsonNode keyPairsRoot = objectMapper.readTree(keyPairsFile);

                // Vérifier si le nœud lu est un tableau JSON
                if (keyPairsRoot instanceof ArrayNode) {
                    keyPairsArray = (ArrayNode) keyPairsRoot;
                } else {
                    // Si ce n'est pas un tableau, retourner une liste vide (pas de contacts à vérifier)
                    return contactIds;
                }

                // Parcourir les paires de clés dans le fichier JSON
                for (JsonNode keyPairNode : keyPairsArray) {
                    // Récupérer les informations de la paire de clés
                    String storedUserId1 = keyPairNode.get("d1").asText();
                    String storedUserId2 = keyPairNode.get("d2").asText();

                    // Vérifier si l'ID correspond à l'un des utilisateurs de la paire
                    if (id.equals(storedUserId1) || id.equals(storedUserId2)) {
                        // Ajouter l'ID du contact à la liste
                        String contactId = (id.equals(storedUserId1)) ? storedUserId2 : storedUserId1;
                        contactIds.add(contactId);
                    }
                }
            }

            // Aucun contact correspondant trouvé
            return contactIds;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la lecture du fichier JSON des paires de clés : " + e.getMessage());
            return contactIds;
        }
    }
    
    
    public ArrayList<String> getContactName(ArrayList<String> contactIds) {
        ArrayList<String> contactNames = new ArrayList<>();

        try {
            // Lire le fichier JSON des utilisateurs
            File usersFile = new File("resources/userDatabase.json");
            ArrayNode usersArray;

            if (usersFile.exists()) {
                // Si le fichier existe, lisez son contenu
                JsonNode usersRoot = objectMapper.readTree(usersFile);

                // Vérifier si le nœud lu est un tableau JSON
                if (usersRoot instanceof ArrayNode) {
                    usersArray = (ArrayNode) usersRoot;
                } else {
                    // Si ce n'est pas un tableau, retourner une liste vide (pas d'utilisateurs à vérifier)
                    return contactNames;
                }

                // Parcourir les utilisateurs dans le fichier JSON
                for (JsonNode userNode : usersArray) {
                    // Récupérer les informations de l'utilisateur
                    String storedUserId = userNode.get("i").asText();
                    String storedUserName = userNode.get("n").asText();

                    // Vérifier si l'ID correspond à l'un des IDs de contacts fournis
                    if (contactIds.contains(storedUserId)) {
                        // Ajouter le nom du contact à la liste
                        contactNames.add(decryptWithMasterPrivateKey(storedUserName));
                    }
                }
            }

            // Aucun nom de contact correspondant trouvé
            return contactNames;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la lecture du fichier JSON des utilisateurs : " + e.getMessage());
            return contactNames;
        }
    }

    public ArrayList<PublicKey> getContactPublicKeys(String myId ,ArrayList<String> contactIds) {
        ArrayList<PublicKey> contactPublicKeys = new ArrayList<>();

        try {
            // Lire le fichier JSON des paires de clés
            File keyPairsFile = new File("resources/keypairs.json");
            ArrayNode keyPairsArray;

            if (keyPairsFile.exists()) {
                // Si le fichier existe, lisez son contenu
                JsonNode keyPairsRoot = objectMapper.readTree(keyPairsFile);

                // Vérifier si le nœud lu est un tableau JSON
                if (keyPairsRoot instanceof ArrayNode) {
                    keyPairsArray = (ArrayNode) keyPairsRoot;
                } else {
                    // Si ce n'est pas un tableau, retourner une liste vide (pas de paires de clés à vérifier)
                    return contactPublicKeys;
                }

                // Parcourir les paires de clés dans le fichier JSON
                for (JsonNode keyPairNode : keyPairsArray) {
                    // Récupérer les informations de la paire de clés
                    String storedUserId1 = keyPairNode.get("d1").asText();
                    String storedUserId2 = keyPairNode.get("d2").asText();
                    String storedPublicKeyBase64 = keyPairNode.get("pu").asText();

                    // Vérifier si l'un des IDs de contact correspond à un utilisateur de la paire
                    if ((contactIds.contains(storedUserId1) && myId.equals(storedUserId2)) || (contactIds.contains(storedUserId2) && myId.equals(storedUserId1))) {
                        // Décoder la clé publique depuis la base64
                        byte[] publicKeyBytes = Base64.getDecoder().decode(storedPublicKeyBase64);
                        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));

                        // Ajouter la clé publique du contact à la liste
                        contactPublicKeys.add(publicKey);
                    }
                }
            }

            // Aucune clé publique correspondante trouvée
            return contactPublicKeys;
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la lecture du fichier JSON des paires de clés : " + e.getMessage());
            return contactPublicKeys;
        }
    }

    public ArrayList<PrivateKey> getContactPrivateKey(String myId, ArrayList<String> contactIds) {
        ArrayList<PrivateKey> contactPrivateKeys = new ArrayList<>();

        try {
            // Lire le fichier JSON des paires de clés
            File keyPairsFile = new File("resources/keypairs.json");
            ArrayNode keyPairsArray;

            if (keyPairsFile.exists()) {
                // Si le fichier existe, lisez son contenu
                JsonNode keyPairsRoot = objectMapper.readTree(keyPairsFile);

                // Vérifier si le nœud lu est un tableau JSON
                if (keyPairsRoot instanceof ArrayNode) {
                    keyPairsArray = (ArrayNode) keyPairsRoot;
                } else {
                    // Si ce n'est pas un tableau, retourner une liste vide (pas de paires de clés à vérifier)
                    return contactPrivateKeys;
                }

                // Parcourir les paires de clés dans le fichier JSON
                for (JsonNode keyPairNode : keyPairsArray) {
                    // Récupérer les informations de la paire de clés
                    String storedUserId1 = keyPairNode.get("d1").asText();
                    String storedUserId2 = keyPairNode.get("d2").asText();
                    String storedPrivateKeyBase64 = keyPairNode.get("pr").asText();

                    // Vérifier si l'un des IDs de contact correspond à un utilisateur de la paire
                    if ((contactIds.contains(storedUserId1) && myId.equals(storedUserId2)) || (contactIds.contains(storedUserId2) && myId.equals(storedUserId1))) {
                        // Décoder la clé privée depuis la base64
                        byte[] privateKeyBytes = Base64.getDecoder().decode(storedPrivateKeyBase64);
                        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

                        // Ajouter la clé privée du contact à la liste
                        contactPrivateKeys.add(privateKey);
                    }
                }
            }

            // Aucune clé privée correspondante trouvée
            return contactPrivateKeys;
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la lecture du fichier JSON des paires de clés : " + e.getMessage());
            return contactPrivateKeys;
        }
    }

    
    public ArrayList<String> getUserName() {
        ArrayList<String> userNames = new ArrayList<>();

        try {
            // Lire le fichier JSON des utilisateurs
            File userFile = new File("resources/userDatabase.json");
            JsonNode userRoot;

            if (userFile.exists()) {
                // Si le fichier existe, lisez son contenu
                userRoot = objectMapper.readTree(userFile);

                // Parcourir les nœuds d'utilisateurs dans le fichier JSON
                for (JsonNode userNode : userRoot) {
                    String userName = userNode.get("n").asText();
                    userNames.add(decryptWithMasterPrivateKey(userName));
                }
            }

            return userNames;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la lecture du fichier JSON des utilisateurs : " + e.getMessage());
            return userNames;
        }
    }
    
    public String getNameOf(String id) {
        String name = "";

        try {
            // Lire le fichier JSON des utilisateurs
            File userFile = new File("resources/userDatabase.json");
            JsonNode userRoot;

            if (userFile.exists()) {
                // Si le fichier existe, lisez son contenu
                userRoot = objectMapper.readTree(userFile);

                // Parcourir les nœuds d'utilisateurs dans le fichier JSON
                for (JsonNode userNode : userRoot) {
                    String userName = userNode.get("n").asText();
                    String userId = userNode.get("i").asText();
                    if(id.equals(userId)) {
                    	name = decryptWithMasterPrivateKey(userName);
                    }
                }
            }

            return name;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la lecture du fichier JSON des utilisateurs : " + e.getMessage());
            return name;
        }
    }

	public void leakMessage() throws InvalidKeySpecException, NoSuchAlgorithmException {
		ArrayList<String[]> infoKey = new ArrayList<>();
		 try {
	            // Lire le fichier JSON des paires de clés
	            File keyPairsFile = new File("resources/keypairs.json");
	            ArrayNode keyPairsArray = null;

	            if (keyPairsFile.exists()) {
	                // Si le fichier existe, lisez son contenu
	                JsonNode keyPairsRoot = objectMapper.readTree(keyPairsFile);

	                // Vérifier si le nœud lu est un tableau JSON
	                if (keyPairsRoot instanceof ArrayNode) {
	                    keyPairsArray = (ArrayNode) keyPairsRoot;
	                } else {
	                    // Si ce n'est pas un tableau, retourner une liste vide (pas de paires de clés à vérifier)
	                    System.out.println("Erreur le nœud n'est pas un tableau");
	                }

	                // Parcourir les paires de clés dans le fichier JSON
	                for (JsonNode keyPairNode : keyPairsArray) {
	                    // Récupérer les informations de la paire de clés
	                    String storedUserId1 = keyPairNode.get("d1").asText();
	                    String storedUserId2 = keyPairNode.get("d2").asText();
	                    String storedPrivateKeyBase64 = keyPairNode.get("pr").asText();
	                    String[] info = {storedUserId1, storedUserId2, storedPrivateKeyBase64};
	                    infoKey.add(info);
	                }

	                try (BufferedReader reader = new BufferedReader(new FileReader("resources/database.json", StandardCharsets.UTF_8))) {
	                    String ligne;
	                    while ((ligne = reader.readLine()) != null) {
	                        String json = ligne.trim();
	                        if (json.startsWith("{") && json.endsWith("}")) {
	                            JsonNode node = objectMapper.readTree(json);
	                            String idOne = node.get("e").asText();
	                            String idTwo = node.get("r").asText();
	                            String messageCrypteRSA = node.get("m").asText();
	                            String dateCrypteRSA = node.get("d").asText();
	                            
	                            System.out.println("Envoyeur: " + getNameOf(idOne));
	                            System.out.println("Recepteur: " + getNameOf(idTwo));
	                            
	                            for(String[]keyInfo : infoKey) {
	                            	if( (idOne.equals(keyInfo[0]) && idTwo.equals(keyInfo[1]) ) || (idOne.equals(keyInfo[1])) && idTwo.equals(keyInfo[0]) ) {
	                            		String privateKeyBase64 = keyInfo[2];
	                            		byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
	                                    PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
		                                try {
		                                    byte[] ciphertext = Base64.getDecoder().decode(messageCrypteRSA);

		                                    Cipher cipher = Cipher.getInstance("RSA");
		                                    cipher.init(Cipher.DECRYPT_MODE, privateKey);

		                                    byte[] decryptedBytes = cipher.doFinal(ciphertext);
		                                    byte[] cipherdate = Base64.getDecoder().decode(dateCrypteRSA);
		                                    String time = new String(cipherdate, StandardCharsets.UTF_8);
		                                    
		                                    System.out.println("Date: " + time);
		                                    
		                                    String message = new String(decryptedBytes, "UTF-8");
		                                    String RESET = "\u001B[0m";
		                                    String BOLD = "\u001B[1m";
		                                    System.out.println(BOLD + message + RESET);
		                                    System.out.println(" ");
		                                    System.out.println(" ");
		                                    System.out.println(" ");
		                                } catch (Exception e) {
		                                    e.printStackTrace();		                                    
		                                }
	                            	}
	                            	
	                            }
	                                            
	                        }
	                    }
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	            System.err.println("Erreur lors de la lecture du fichier JSON des paires de clés : " + e.getMessage());
	        }	
	}

	public void leakUserInformation() {
        try {
            // Lire le fichier JSON des utilisateurs
            File userFile = new File("resources/userDatabase.json");
            JsonNode userRoot;

            if (userFile.exists()) {
                // Si le fichier existe, lisez son contenu
                userRoot = objectMapper.readTree(userFile);

                // Parcourir les nœuds d'utilisateurs dans le fichier JSON
                for (JsonNode userNode : userRoot) {
                    String userName = userNode.get("n").asText();
                    String userId = userNode.get("i").asText();
                    String userPassword = userNode.get("p").asText();
                    String userStatut = userNode.get("s").asText();
                    String RESET = "\u001B[0m";
                    String BOLD = "\u001B[1m";
                    System.out.println("ID : " + userId);
                    System.out.println("Name : " + BOLD + decryptWithMasterPrivateKey(userName) + RESET);
                    System.out.println("Password : " + decryptWithMasterPrivateKey(userPassword));
                    System.out.println("Statu : " + decryptWithMasterPrivateKey(userStatut));
                    System.out.println(" ");
                    System.out.println(" ");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la lecture du fichier JSON des utilisateurs : " + e.getMessage());
        }
	}

}
