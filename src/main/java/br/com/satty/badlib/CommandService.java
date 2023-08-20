package br.com.satty.badlib;

import java.io.*;

import static br.com.satty.badlib.FileUtils.getStringConentFromFile;

public class CommandService {


    protected static String getComandReturn(String uid) throws Exception{
        String dir = System.getProperty("user.dir");
        String out = String.format("%s/%s", dir, uid);
        File file = new File(out);
        StringBuilder sb = getStringConentFromFile(file);
        if (sb == null) return null;
        file.delete();
        File file2 = new File(out+".sh");
        file2.delete();
        return sb.toString();
    }


    protected static String directExecute(String exec) throws Exception {
        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", exec);
        return getCommandReturnString(builder);
    }

    protected static String directExecuteWin(String exec) throws Exception {
        ProcessBuilder builder = new ProcessBuilder("cmd", "/c", exec);
        return getCommandReturnString(builder);
    }
    private static String getCommandReturnString(ProcessBuilder builder) throws IOException, InterruptedException {
        builder.redirectErrorStream(true);
        Process process = builder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            return output.toString();
        } else {
            return "Error, exist code: " + exitCode;
        }
    }
    public static String comandCreator(String command, String uid) throws IOException {
        String dir = System.getProperty("user.dir");
        String out = String.format("%s/%s.sh", dir, uid);
        File sh = new File(out);
        command = String.format("%s >%s/%s 2>&1", command, dir, uid);
        FileWriter writer = new FileWriter(sh);
        writer.write("#!/bin/bash\n");
        writer.write(command + "\n");
        writer.close();
        return out;
    }
}
