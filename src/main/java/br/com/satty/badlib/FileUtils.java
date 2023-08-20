package br.com.satty.badlib;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FileUtils {
    protected static StringBuilder getStringConentFromFile(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String ln;
        while ((ln = br.readLine()) != null) {
            sb.append(ln).append("\n");
        }
        br.close();
        return sb;
    }

    protected static Map<String,String> getAppplicationProperties(){
        String propertiesFilePath = "application.properties";
        Map<String,String> props = new HashMap<>();
        Properties properties = new Properties();
        File prop = new File(propertiesFilePath);
        if (prop.exists()){
            try (FileInputStream fileInputStream = new FileInputStream(propertiesFilePath)) {
                properties.load(fileInputStream);
                for (String propertyName : properties.stringPropertyNames()) {
                    String propertyValue = properties.getProperty(propertyName);
                    props.put(propertyName, propertyValue );
                }
            } catch (IOException e) {
                //like a ninja
            }
        }
        return props;
    }
}
