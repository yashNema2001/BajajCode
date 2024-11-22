package bajajProject.bajajProject;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Hello world!
 *
 */
public class JsonHashGenerator 
{
    public static void main( String[] args )
    {
    	if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashApp.jar <roll_number> <path_to_json>");
            return;
        }

        String rollNumber = args[0].toLowerCase().trim();
        String jsonFilePath = args[1];

        try {
            // Read and parse the JSON file
            File jsonFile = new File(jsonFilePath);
            if (!jsonFile.exists()) {
                System.out.println("Error: JSON file not found.");
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonFile);

            // Find the first instance of "destination"
            String destinationValue = findDestination(rootNode);
            if (destinationValue == null) {
                System.out.println("Error: Key 'destination' not found in the JSON file.");
                return;
            }

            // Generate a random 8-character alphanumeric string
            String randomString = generateRandomString(8);

            // Concatenate roll number, destination value, and random string
            String inputString = rollNumber + destinationValue + randomString;

            // Generate MD5 hash
            String md5Hash = generateMD5Hash(inputString);

            // Output the result
            System.out.println(md5Hash + ";" + randomString);

        } catch (IOException e) {
            System.out.println("Error reading the JSON file: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error generating MD5 hash: " + e.getMessage());
        }
    }

    private static String findDestination(JsonNode node) {
        if (node.isObject()) {
            for (JsonNode childNode : node) {
                if (node.has("destination")) {
                    return node.get("destination").asText();
                }
                String result = findDestination(childNode);
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode childNode : node) {
                String result = findDestination(childNode);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder randomString = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            randomString.append(characters.charAt(random.nextInt(characters.length())));
        }
        return randomString.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder hashString = new StringBuilder();
        for (byte b : hashBytes) {
            hashString.append(String.format("%02x", b));
        }
        return hashString.toString();
    }
    }

