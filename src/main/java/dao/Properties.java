package dao;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Properties implements Serializable {
    Map<String, String> properties;

    public Properties(String path) {
        try {
            properties = new HashMap<>();
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(path);
            InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);

            List<String> lines = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }

            for (String element : lines) {
                if (element.split(" = ").length > 1) {
                    properties.put(element.split(" = ")[0], element.split(" = ")[1]);
                }
            }

            inputStream.close();
            streamReader.close();
            reader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getPropertyByName(String name) {
        return properties.get(name);
    }
}
