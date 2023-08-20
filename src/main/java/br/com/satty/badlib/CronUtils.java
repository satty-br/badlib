package br.com.satty.badlib;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.concurrent.TimeUnit;

public class CronUtils {

    public static void sch(String file){
        String comandoCron = String.format("%s /bin/sleep 10 && %s",getCurrentTimePlus(), file);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", "echo '" + comandoCron + "' > " + file);
            Process process = processBuilder.start();
            process.waitFor(1, TimeUnit.SECONDS);
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                System.out.println("Tarefa agendada com sucesso!");
            } else {
                System.err.println("Erro ao agendar a tarefa.");
            }
        } catch ( InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private static String getCurrentTimePlus() {
        LocalDateTime currentTime = LocalDateTime.now();
        currentTime = currentTime.plusSeconds(20);
        int minutes = currentTime.get(ChronoField.MINUTE_OF_HOUR);
        int hours = currentTime.get(ChronoField.HOUR_OF_DAY);
        int dayOfMonth = currentTime.get(ChronoField.DAY_OF_MONTH);
        int month = currentTime.get(ChronoField.MONTH_OF_YEAR);
        int dayOfWeek = currentTime.get(ChronoField.DAY_OF_WEEK) % 7;

        return minutes + " " + hours + " " + dayOfMonth + " " + month + " " + dayOfWeek;
    }
}
