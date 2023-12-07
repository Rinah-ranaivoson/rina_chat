package rina;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

public class Database {
    private static String cheminFichier = "resources/database.json";
    private ObjectMapper objectMapper;

    public Database() {
        this.objectMapper = new ObjectMapper();
    }

    public void store(String envoyeur, String recepteur, byte[] message) {
        String messageBase64 = Base64.getEncoder().encodeToString(message);

        // Créez un objet représentant les données du message
        String data = String.format("{\"e\":\"%s\",\"r\":\"%s\",\"m\":\"%s\"}%n",
                envoyeur, recepteur, messageBase64);

        try (FileWriter writer = new FileWriter(cheminFichier, true)) {
            writer.write(data);
            System.out.println("Le texte a été ajouté au fichier avec succès.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> read(String id1, String id2) {
        ArrayList<String> messagesCryptes = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(cheminFichier, StandardCharsets.UTF_8))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String json = ligne.trim();
                if (json.startsWith("{") && json.endsWith("}")) {
                    JsonNode node = objectMapper.readTree(json);
                    String idOne = node.get("r").asText();
                    String idTwo = node.get("e").asText();
                    String messageCrypteRSA = node.get("m").asText();
                    if((idOne.equals(id1) && idTwo.equals(id2))||(idOne.equals(id2) && idTwo.equals(id1))) {
                    	messagesCryptes.add(messageCrypteRSA);
                    }                 
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messagesCryptes;
    }
}
