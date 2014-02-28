package com.minecraftapi.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

/**
 * The key manager class for Minecraft-API,
 * Read, write and generate new keys for the Minecraft-API.com protocol.
 * 
 * @author Alexis Tan
 */
public class KeyManager
{

    private File directory;
    private String key = null;

    public KeyManager(File directory)
    {
        this.directory = directory;
    }

    /**
     * Loads the private key from the disk, if its already loaded it will just return it.
     * 
     * @return The protocol key
     */
    public String getKey()
    {
        if (key == null) {
            try {
                readKey();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return key;
    }

    /**
     * Read the key from the disk, if it doesn't exists it will generate a new 512-bit key.
     * 
     * @throws Exception 
     */
    private void readKey() throws Exception
    {
        File keyFile = new File(directory + "/secure.key");
        if (keyFile.exists()) {
            FileReader fr = new FileReader(keyFile.getAbsoluteFile());
            try (BufferedReader bw = new BufferedReader(fr)) {
                key = bw.readLine();
            }
            if(key != null && key.length() == 512) {
                return;
            }
        }

        // Write the new file
        key = generateKey();
        keyFile.delete();
        keyFile.createNewFile();
        FileWriter fw = new FileWriter(keyFile.getAbsoluteFile());
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(key);
            bw.newLine();
            bw.write("----------------------------------------------------------------");
            bw.newLine();
            bw.write("The confusing bit of text above is your randomly generated key.");
            bw.newLine();
            bw.write("If you want to genrate a new key, simple remove this file and a new ");
            bw.newLine();
            bw.write("key will be generated for you on your next server startup.");
            bw.newLine();
            bw.newLine();
            bw.write("Example of usage:");
            bw.newLine();
            bw.write("http://minecraft-api.com/v1/plugin/?server=Your-Server-Goes-Here&key=Your-Key-Goes-Here");
            bw.newLine();
            bw.newLine();
            bw.write("For more information, check out the link below.");
            bw.newLine();
            bw.write("http://minecraft-api.com/documentation#plugin");
        }
    }

    private String generateKey()
    {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-!()[]{}+";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(512);
        for (int i = 0; i < 512; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }
}
