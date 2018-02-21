package client.classes.genes;

import client.classes.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class GeneCollection {

    HashMap<String, byte[][]> data;
	
    public GeneCollection(String resourceName) {
		try {
            System.out.println(resourceName);

		    data = new HashMap<>();

            InputStream inputStream = new FileInputStream("datafiles/" + resourceName);
            InputStreamReader streamReader = new InputStreamReader(inputStream, "UTF-8");
            String[] lines = new BufferedReader(streamReader).lines().toArray(String[]::new);

            for (int i = 0; i < lines.length; i += 3) {
                String key = lines[i];
                String[] creepsString = lines[i + 1].split(",");
                String[] herosString = lines[i + 2].split(",");

                byte[] creeps = new byte[creepsString.length];
                byte[] heros = new byte[herosString.length];

                if (lines[i + 1].length() > 0)
                    for (int j = 0; j < creepsString.length; j++)
                        creeps[j] = Byte.parseByte(creepsString[j]);
                if (lines[i + 2].length() > 0)
                    for (int j = 0; j < herosString.length; j++)
                        heros[j] = Byte.parseByte(herosString[j]);

                byte[][] val = new byte[][] { creeps, heros };

                data.put(key, val);
            }
        } catch (UnsupportedEncodingException e) {
            Logger.println("Can't load resource " + resourceName);
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            Logger.println("Can't find resource " + resourceName);
            e.printStackTrace();
        }
    }

    public Collection<byte[][]> getAllValues() {
        return data.values();
    }
}
