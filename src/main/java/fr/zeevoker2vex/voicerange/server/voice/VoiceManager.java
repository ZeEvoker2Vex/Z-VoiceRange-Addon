package fr.zeevoker2vex.voicerange.server.voice;

import fr.zeevoker2vex.voicerange.common.utils.MessageBuilder;
import fr.zeevoker2vex.voicerange.server.ServerProxy;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.List;

public class VoiceManager {

    /**
     * Key : the player entity object
     * Value : the KEY of the speak mode which player has chosen.
     */
    public static HashMap<EntityPlayer, String> playersSpeakMode = new HashMap<>();

    /**
     * Add player with the default speak mode on joining server
     * @param player The targeted player
     */
    public static void connectPlayer(EntityPlayer player){
        if(ServerProxy.getConfig().hasNoneSpeakMode()){
            new MessageBuilder.ComponentMessage("§c").addTranslation("voicerange.nospeakmode.connect").sendTo(player);
        }
        else playersSpeakMode.putIfAbsent(player, ServerProxy.getConfig().getDefaultSpeakMode());
    }
    /**
     * Remove player in the map on leaving server
     * @param player The targeted player
     */
    public static void disconnectPlayer(EntityPlayer player) {
        playersSpeakMode.remove(player);
    }

    /**
     * Get the current speak mode index and determine the next with the index (index had been determined on reading config file, in the order of lines.
     * So the first speak mode written in the config is the first index, normally).
     * @param player The targeted player
     * @return the next speak mode
     */
    public static SpeakMode setNextSpeakMode(EntityPlayer player){
        SpeakMode current = getPlayerSpeakMode(player);

        // Transformation du keySet en list pour pouvoir récupérer des index
        List<String> keys = ServerProxy.getConfig().getSpeakModeKeys();
        int currentIndex = keys.indexOf(current.getKey());

        // On définit le prochain index (0 si on est au dernier)
        int nextIndex = keys.size() == currentIndex+1 ? 0 : currentIndex+1;
        String nextKey = keys.get(nextIndex);

        setPlayerSpeakMode(player, nextKey);
        return ServerProxy.getConfig().getSpeakMode(nextKey);
    }

    /**
     * Change the player's speakMode to the give one
     * @param player The targeted player
     * @param speakMode The Speak Mode object
     */
    public static void setPlayerSpeakMode(EntityPlayer player, SpeakMode speakMode){
        setPlayerSpeakMode(player, speakMode.getKey());
    }
    /**
     * Change the player's speakMode to the give one
     * @param player The targeted player
     * @param speakModeKey The Speak Mode key
     */
    public static void setPlayerSpeakMode(EntityPlayer player, String speakModeKey){
        playersSpeakMode.replace(player, speakModeKey);
    }

    /**
     * @param player An EntityPlayer
     * @return The speakMode in which player belong to it.
     */
    public static SpeakMode getPlayerSpeakMode(EntityPlayer player){
        return ServerProxy.getConfig().getSpeakMode(playersSpeakMode.get(player));
    }
    /**
     * @param player An EntityPlayer
     * @return The key of the speakMode in which player belong to it.
     */
    public static String getPlayerSpeakModeKey(EntityPlayer player){
        return playersSpeakMode.get(player);
    }
}