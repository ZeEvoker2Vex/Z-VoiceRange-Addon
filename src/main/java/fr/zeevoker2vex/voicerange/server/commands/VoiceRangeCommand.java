package fr.zeevoker2vex.voicerange.server.commands;

import com.google.common.collect.Maps;
import fr.zeevoker2vex.voicerange.common.utils.CommandHelpBuilder;
import fr.zeevoker2vex.voicerange.common.utils.MessageBuilder;
import fr.zeevoker2vex.voicerange.server.ServerProxy;
import fr.zeevoker2vex.voicerange.server.voice.SpeakMode;
import fr.zeevoker2vex.voicerange.server.voice.VoiceManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VoiceRangeCommand extends CommandBase {

    private final HashMap<String, Integer> commandArguments = new HashMap<>();
    private final List<String> speakModeArguments = new ArrayList<>();
    private final List<String> configArguments = new ArrayList<>();
    public VoiceRangeCommand(){
        // Argument, Details
        commandArguments.put("rlconfig", 0);
        commandArguments.put("save", 0);
        commandArguments.put("speakmodes", 0);
        commandArguments.put("help", 0);
        commandArguments.put("speakmode", 10);
        commandArguments.put("config", 7);
        // SpeakMode Arg (.ReplaceKey)
        speakModeArguments.add("create:");
        speakModeArguments.add("delete:");
        speakModeArguments.add("edit;distance:");
        speakModeArguments.add("edit;translation.langCode:");
        speakModeArguments.add("edit;defaultSpeakMode:");
        // Config Arg
        configArguments.add("defaultSpeakMode");
        configArguments.add("translateType");
        configArguments.add("forcedLang");
    }

    @Override
    public String getName() {
        return "voicerange";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "§cUsage: /voicerange <rlconfig/save/help/speakmodes/speakmode/config> [SpeakModeKey/configKey] [key:value/configValue]\n§cSee /voicerange help for details.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        MessageBuilder.ComponentMessage message = new MessageBuilder.ComponentMessage("");
        if(args.length==1){
            if(args[0].equals("rlconfig")){
                if(ServerProxy.getConfig().readConfigFile()) message.reset("§2").addTranslation("cmd.voicerange.rlconfig.success").sendTo(sender);
                else message.reset("§c").addTranslation("cmd.voicerange.rlconfig.failed").addString(" (see logs)").sendTo(sender);
                return;
            }
            else if(args[0].equals("save")){
                if(ServerProxy.getConfig().saveToConfig()) message.reset("§2").addTranslation("cmd.voicerange.save.success").sendTo(sender);
                else message.reset("§c").addTranslation("cmd.voicerange.save.failed").addString(" (see logs)").sendTo(sender);
                return;
            }
            else if(args[0].equals("speakmodes")){
                /*
                    Voici la liste des modes vocaux disponibles :
                    => KEY : porte jusqu'à X blocs. Traductions :
                        - fr_fr : traduction
                 */
                message = message.reset("cmd.voicerange.speakmodes.title", new Object[0]);
                for(Map.Entry<String, SpeakMode> entry : ServerProxy.getConfig().getSpeakModes().entrySet()){
                    SpeakMode speakMode = entry.getValue();
                    message = message.addTranslationLine("cmd.voicerange.speakmodes.mode", entry.getKey(), speakMode.getDistance());
                    for(Map.Entry<String, String> trEntry : speakMode.getTranslations().entrySet()){
                        message = message.addLine("    ").addTranslation("cmd.voicerange.speakmodes.translation", trEntry.getKey(), trEntry.getValue());
                    }
                }
                message.sendTo(sender);
                return;
            }
            else if(args[0].equals("help")){
                CommandHelpBuilder commandHelpBuilder = new CommandHelpBuilder(getName(), new ArrayList<>(commandArguments.keySet()));
                commandHelpBuilder.build().sendTo(sender);
                return;
            }
        }
        else if(args.length==2){
            if(args[0].equals("help")){
                String detailedArg = args[1];
                if(this.commandArguments.containsKey(detailedArg)){
                    CommandHelpBuilder.ArgumentBuilder argumentBuilder = new CommandHelpBuilder.ArgumentBuilder(getName(), detailedArg, this.commandArguments.get(detailedArg));
                    argumentBuilder.build().sendTo(sender);
                }
                else message.reset("§c").addTranslation("cmd.voicerange.help.details.failed", detailedArg, getName()).sendTo(sender);
                return;
            }
            else if(args[0].equals("speakmode")){
                String speakModeKey = args[1];
                if(ServerProxy.getConfig().speakModeExist(speakModeKey)){
                    SpeakMode speakMode = ServerProxy.getConfig().getSpeakMode(speakModeKey);
                    /*
                        Voici les détails du SpeakMode KEY :
                        => Portée de voix jusqu'à X blocs. Traductions :
                            - fr_fr : traduction
                     */
                    message = message.reset("cmd.voicerange.speakmode.title", speakModeKey).addTranslationLine("cmd.voicerange.speakmode.distance", speakMode.getDistance());
                    for(Map.Entry<String, String> trEntry : speakMode.getTranslations().entrySet()){
                        message = message.addLine("    ").addTranslation("cmd.voicerange.speakmode.translation", trEntry.getKey(), trEntry.getValue());
                    }
                    message.sendTo(sender);
                }
                else message.reset("§c").addTranslation("cmd.voicerange.speakmode.failed", speakModeKey, speakModeKey).sendTo(sender);
                return;
            }
            else if(args[0].equals("config")){
                String configKey = args[1];
                if(this.configArguments.contains(configKey)){
                    if(configKey.equals("defaultSpeakMode")){
                        message = message.reset("§7").addTranslation("cmd.voicerange.config.defaultSpeakMode.get", ServerProxy.getConfig().getDefaultSpeakMode());
                        message.sendTo(sender);
                    }
                    else if(configKey.equals("translateType")){
                        message = message.reset("§7").addTranslation("cmd.voicerange.config.translateType.get", ServerProxy.getConfig().getTranslateType()).addTranslation("voicerange.translateType.infos");
                        message.sendTo(sender);
                    }
                    else if(configKey.equals("forcedLang")){
                        message = message.reset("§7").addTranslation("cmd.voicerange.config.forcedLang.get.langCode", ServerProxy.getConfig().getForcedLang());
                        boolean isServerUsed = ServerProxy.getConfig().isForcedTranslation();
                        if(isServerUsed) message = message.addTranslationLine("cmd.voicerange.config.forcedLang.langServer");
                        else message = message.addTranslationLine("cmd.voicerange.config.forcedLang.langClient");
                        message.sendTo(sender);
                    }
                }
                else {
                    message = message.reset("§c").addTranslation("cmd.voicerange.config.failed", configKey);
                    message.sendTo(sender);
                }
                return;
            }
        }
        // Prevent bug, by instance with edit distance that length exactly 3.
        else if(args.length==3 && args[0].equals("config")){
            String configKey = args[1];
            if(this.configArguments.contains(configKey)){
                String configValue = args[2];
                if(configKey.equals("defaultSpeakMode")){
                    if(ServerProxy.getConfig().speakModeExist(configValue)){
                        ServerProxy.getConfig().setDefaultSpeakMode(configValue);
                        message = message.reset("§2").addTranslation("cmd.voicerange.config.defaultSpeakMode.set", configValue);
                        message.sendTo(sender);
                    }
                    else message.reset("§c").addTranslation("cmd.voicerange.speakmode.failed", configValue).sendTo(sender);
                }
                else if(configKey.equals("translateType")){
                    if(configValue.equals("user") || configValue.equals("forced")){
                        ServerProxy.getConfig().setTranslateType(configValue);
                        message = message.reset("§2").addTranslation("cmd.voicerange.config.translateType.set", configValue);
                    }
                    else {
                        message = message.reset("§c").addTranslation("cmd.voicerange.config.translateType.set.failed", configValue).addTranslationLine("voicerange.translateType.infos");
                    }
                    message.sendTo(sender);
                }
                else if(configKey.equals("forcedLang")){
                    ServerProxy.getConfig().setForcedLang(configValue);
                    message = message.reset("§7").addTranslation("cmd.voicerange.config.forcedLang.set", configValue);
                    boolean isServerUsed = ServerProxy.getConfig().isForcedTranslation();
                    if(isServerUsed) message = message.addTranslationLine("cmd.voicerange.config.forcedLang.langServer");
                    else message = message.addTranslationLine("cmd.voicerange.config.forcedLang.langClient");
                    message.sendTo(sender);
                }
            }
            else {
                message = message.reset("§c").addTranslation("cmd.voicerange.config.failed", configKey);
                message.sendTo(sender);
            }
            return;
        }
        else if(args.length>=3){
            if(args[0].equals("speakmode")){
                String speakModeKey = args[1];
                // Need this OR :/
                if(ServerProxy.getConfig().speakModeExist(speakModeKey) || args[2].startsWith("create:")){
                    String input = String.join(" ", args);

                    // get only the pair : ([^:]*) ([\w\.\;]+:.+)
                    Pattern pattern = Pattern.compile("([^:]*) ([\\w\\.\\;]+:.+)");
                    Matcher matcher = pattern.matcher(input);

                    if(matcher.matches()){
                        // Extracted the <key:value> pair
                        input = matcher.group(2);

                        // Split key and value : ([^:]*):(.*)
                        pattern = Pattern.compile("([^:]*):(.*)");
                        matcher = pattern.matcher(input);

                        if(matcher.matches()){
                            // Extracted key & value
                            String key = matcher.group(1);
                            String value = matcher.group(2);
                            String keyReplacement = "";

                            // Verify if key contains a key value (Key to find a particular thing, so also a value. Ex : create SpeakMode with it KEY in key var ; edit translation with langCode)
                            // Split real key & key value : (.+)\.(.+)
                            pattern = Pattern.compile("(.+)\\.(.+)");
                            matcher = pattern.matcher(key);

                            // With this, we affine the key. Actually we are not sure that the key is correct, so we have to replace key value with ReplaceKey, then check if key exists.
                            if(matcher.matches()){
                                String realKey = matcher.group(1);
                                keyReplacement = matcher.group(2); // The value which will be replaced by ReplaceKey, is necessary for after.

                                // Get all keys with ReplaceKey, and make the same operation with them to extract only the real key and then find the key that correspond with own.
                                // Then get the correct ReplaceKey and replace "key value" by it. Finally check if the key exists AND do correct operations (with previously "key value" if necessary.
                                for (String arg : this.speakModeArguments) {
                                    String[] keys = arg.split("\\.");
                                    // The arg has key value.
                                    if(keys.length==2){
                                        String argRealKey = keys[0];
                                        String argReplaceKey = keys[1];
                                        // We got it, the key we want.
                                        if(realKey.equals(argRealKey)){
                                            // Now we have juste to replace the part of "key" var that equals to "keyReplacement" var with the ReplaceKey.
                                            // Funfact : here, we are sure that the key exists because we find it in the speakModeArguments list !
                                            key = key.replaceAll(keyReplacement, argReplaceKey.replaceAll(":", ""));
                                            break;
                                        }
                                    }
                                }
                            }

                            // As not all the keys have key value, we have to check if the key exists anyway.
                            if(this.speakModeArguments.contains(key+":")) {
                                if(key.equals("create")){
                                    if(!ServerProxy.getConfig().speakModeExist(speakModeKey)){
                                        // Use SpeakModeKey for KEY. Use value for distance.
                                        int distance;
                                        try {
                                            distance = Integer.parseInt(value);
                                        }
                                        catch(NumberFormatException exception){
                                            message = message.reset("§c").addTranslation("cmd.voicerange.distance.NaN");
                                            message.sendTo(sender);
                                            return;
                                        }
                                        if(distance>0 && distance<10000){
                                            SpeakMode speakMode = new SpeakMode(speakModeKey, distance, Maps.newHashMap());
                                            speakMode.addTranslation("fr_fr", "Traduction par défaut. Modifiez-la avec /voicerange speakmode "+speakModeKey+" edit;translation.langCode:votre traduction");
                                            speakMode.addTranslation("en_us", "Default translation. Edit with /voicerange speakmode "+speakModeKey+" edit;translation.langCode:your translation");
                                            ServerProxy.getConfig().addSpeakMode(speakModeKey, speakMode);

                                            // That is the new first speakMode, there is none before. Set to defaultSpeakMode and "reconnect" all players.
                                            if(ServerProxy.getConfig().getSpeakModes().size()==1){
                                                ServerProxy.getConfig().setDefaultSpeakMode(speakModeKey);
                                                message = message.reset("§e").addTranslation("cmd.voicerange.speakmode.create.speakModeAvailable");
                                                for(EntityPlayerMP player : server.getPlayerList().getPlayers()){
                                                    VoiceManager.connectPlayer(player);
                                                    message.sendTo(player);
                                                }
                                            }
                                            message = message.reset("§2").addTranslation("cmd.voicerange.speakmode.create.success1", speakModeKey);
                                        }
                                        else {
                                            message = message.reset("§c").addTranslation("cmd.voicerange.distance.range");
                                        }
                                    }
                                    else {
                                        message = message.reset("§c").addTranslation("cmd.voicerange.speakmode.create.alreadyexist", speakModeKey);
                                    }
                                    message.sendTo(sender);
                                }
                                else if(key.equals("delete")){
                                    // Use SpeakModeKey for KEY. Value must be "confirm".
                                    if(value.equals("confirm")){
                                        // This is the last mode
                                        if(ServerProxy.getConfig().getSpeakModes().size()==1){
                                            ServerProxy.getConfig().setDefaultSpeakMode("");
                                            message = message.reset("§e").addTranslation("cmd.voicerange.speakmode.delete.noneSpeakModePlayers");
                                            for(EntityPlayerMP player : server.getPlayerList().getPlayers()){
                                                VoiceManager.disconnectPlayer(player);
                                                message.sendTo(player);

                                            }
                                            message = message.reset("§4").addTranslation("cmd.voicerange.speakmode.delete.noneSpeakModeSender", speakModeKey).addTranslationLine("cmd.voicerange.speakmode.create.success1");
                                            message.sendTo(sender);
                                        }
                                        else {
                                            String newSpeakMode = "";
                                            // This is the defaultSpeakMode
                                            if(ServerProxy.getConfig().getDefaultSpeakMode().equals(speakModeKey)){
                                                List<String> keys = ServerProxy.getConfig().getSpeakModeKeys();
                                                int index = 0;
                                                // Set to first mode except if it's the first, so set to the second.
                                                if(keys.indexOf(speakModeKey)==0) index = 1;
                                                newSpeakMode = keys.get(index);
                                                ServerProxy.getConfig().setDefaultSpeakMode(newSpeakMode);
                                                message = message.reset("§c").addTranslation("cmd.voicerange.speakmode.delete.defaultSpeakMode", newSpeakMode);
                                                message.sendTo(sender);
                                            }
                                            message = message.reset("§e").addTranslationLine("cmd.voicerange.speakmode.delete.playerMoveSpeakMode");
                                            for(EntityPlayerMP player : server.getPlayerList().getPlayers()){
                                                if(VoiceManager.getPlayerSpeakModeKey(player).equals(speakModeKey)) {
                                                    VoiceManager.setPlayerSpeakMode(player, newSpeakMode);
                                                    message.sendTo(player);
                                                }
                                            }
                                        }
                                        ServerProxy.getConfig().removeSpeakMode(speakModeKey);
                                        message = message.reset("§2").addTranslation("cmd.voicerange.speakmode.delete.success1", speakModeKey);
                                    }
                                    else {
                                        message = message.reset("§c").addTranslation("cmd.voicerange.speakmode.delete.failed", speakModeKey);
                                    }
                                    message.sendTo(sender);
                                }
                                else if(key.equals("edit;distance")){
                                    // Use SpeakModeKey for KEY. Use value for distance.
                                    int distance;
                                    try {
                                        distance = Integer.parseInt(value);
                                    }
                                    catch(NumberFormatException exception){
                                        message = message.reset("§c").addTranslation("cmd.voicerange.distance.NaN");
                                        message.sendTo(sender);
                                        return;
                                    }
                                    if(distance>0 && distance<10000){
                                        ServerProxy.getConfig().editSpeakModeDistance(speakModeKey, distance);
                                        message = message.reset("§2").addTranslation("cmd.voicerange.speakmode.edit.distance.success", speakModeKey, distance);
                                    }
                                    else {
                                        message = message.reset("§c").addTranslation("cmd.voicerange.distance.range");
                                    }
                                    message.sendTo(sender);
                                }
                                else if(key.equals("edit;translation.langCode")){
                                    // Use SpeakModeKey for KEY. Use value for translation. Use keyReplacement for langCode.
                                    SpeakMode speakMode = ServerProxy.getConfig().getSpeakMode(speakModeKey);

                                    message = message.reset("§2");
                                    if(value.equals("remove") || value.equals("delete")){
                                        speakMode.removeTranslation(keyReplacement);
                                        message = message.addTranslation("cmd.voicerange.speakmode.edit.translation.remove", keyReplacement, speakModeKey);
                                    }
                                    else {
                                        if(speakMode.hasTranslation(keyReplacement)){
                                            speakMode.editTranslation(keyReplacement, value);
                                            message = message.addTranslation("cmd.voicerange.speakmode.edit.translation.edit", keyReplacement, speakModeKey);
                                        }
                                        else {
                                            speakMode.addTranslation(keyReplacement, value);
                                            message = message.addTranslation("cmd.voicerange.speakmode.edit.translation.add", keyReplacement, speakModeKey);
                                        }
                                    }
                                    message.sendTo(sender);
                                }
                                else if(key.equals("edit;defaultSpeakMode")){
                                    // Use SpeakModeKey for defaultSpeakMode.
                                    if(value.equals("confirm")){
                                        ServerProxy.getConfig().setDefaultSpeakMode(speakModeKey);
                                        message = message.reset("§2").addTranslation("cmd.voicerange.config.defaultSpeakMode.set", speakModeKey);
                                    }
                                    else {
                                        message = message.reset("§c").addTranslation("cmd.voicerange.speakmode.edit.defaultSpeakMode.failed", speakModeKey, speakModeKey);
                                    }
                                    message.sendTo(sender);
                                }
                                // None else, because the key existence is tested in the parent "if".
                            }
                            else {
                                message = message.reset("§c").addTranslation("cmd.voicerange.speakmode.keyNoExist", key, speakModeKey);
                                message.sendTo(sender);
                            }
                        }
                        else {
                            message = message.reset("§c").addTranslation("cmd.voicerange.speakmode.pairNotFound").addLine("§4IMPOSSIBLE, ALREADY VERIFIED PREVIOUSLY !");
                            message.sendTo(sender);
                        }
                    }
                    else {
                        message = message.reset("§c").addTranslation("cmd.voicerange.speakmode.pairNotFound");
                        message.sendTo(sender);
                    }
                }
                else message.reset("§c").addTranslation("cmd.voicerange.speakmode.failed", speakModeKey, speakModeKey).sendTo(sender);
                return;
            }
        }
        sendMessage(sender, getUsage(sender));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if(args.length==1) return getListOfStringsMatchingLastWord(args, this.commandArguments.keySet());
        else if(args.length==2){
            switch (args[0]) {
                case "speakmode":
                    return getListOfStringsMatchingLastWord(args, ServerProxy.getConfig().getSpeakModeKeys());
                case "config":
                    return getListOfStringsMatchingLastWord(args, this.configArguments);
                case "help":
                    return getListOfStringsMatchingLastWord(args, this.commandArguments.keySet());
            }
        }
        else if(args.length==3){
            if(args[0].equals("speakmode") && ServerProxy.getConfig().speakModeExist(args[1])) return getListOfStringsMatchingLastWord(args, this.speakModeArguments);
            else if(args[0].equals("config")){
                if(args[1].equals("translateType")) return getListOfStringsMatchingLastWord(args, "forced", "user");
                else if(args[1].equals("defaultSpeakMode")) return getListOfStringsMatchingLastWord(args, ServerProxy.getConfig().getSpeakModeKeys());
            }
        }
        return new ArrayList<>();
    }

    public void sendMessage(ICommandSender sender, String message){
        sender.sendMessage(new TextComponentString(message));
    }
}