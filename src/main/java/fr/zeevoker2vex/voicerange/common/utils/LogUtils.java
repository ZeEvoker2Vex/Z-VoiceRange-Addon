package fr.zeevoker2vex.voicerange.common.utils;

import fr.zeevoker2vex.voicerange.common.VoiceRangeAddon;

public class LogUtils {

    public static void successLog(String message){
        VoiceRangeAddon.getLogger().info("§2"+message);
    }

    public static void errorLog(String message){
        VoiceRangeAddon.getLogger().error(message);
    }

    public static void basicLog(String message){
        VoiceRangeAddon.getLogger().info("§7"+message);
    }
}