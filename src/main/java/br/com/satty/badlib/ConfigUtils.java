package br.com.satty.badlib;

import br.com.satty.badlib.dto.UserConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ConfigUtils {
    protected static UserConfig saveUserConfig(UserConfig userConfig) {
        String dir = System.getProperty("user.dir");
        String json = "{\"uid\":" + userConfig.getUid() + "prKey\":\"" + userConfig.getPrKey() + "\",\"pbkey\":\"" + userConfig.getPbkey() + "\"}";
        String base64Content = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(dir + "/.user.config"));
            bw.write(base64Content);
            bw.close();
            return userConfig;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static UserConfig readConfig() {
        String config = System.getProperty("user.dir") + "/.user.config";
        File file = new File(config);
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    content.append(line);
                }
                br.close();
                String base64Content = content.toString();
                byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
                String json = new String(decodedBytes, StandardCharsets.UTF_8);
                return parseUserConfigFromJson(json);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    private static UserConfig parseUserConfigFromJson(String json) {
        UserConfig userConfig = new UserConfig();
        String[] parts = json.split(",");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("\"", "");
                String value = keyValue[1].trim().replaceAll("\"", "");
                if (key.equals("prKey")) {
                    userConfig.setPrKey(value);
                } else if (key.equals("pbkey")) {
                    userConfig.setPbkey(value);
                } else if (key.equals("uid")){
                    userConfig.setUid(value);
                }
            }
        }
        return userConfig;
    }
}