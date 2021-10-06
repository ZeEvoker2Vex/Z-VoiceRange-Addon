package fr.zeevoker2vex.voicerange.server.config;

import com.google.gson.*;
import fr.nathanael2611.modularvoicechat.util.Helpers;
import fr.zeevoker2vex.voicerange.common.utils.LogUtils;
import fr.zeevoker2vex.voicerange.server.voice.SpeakMode;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddonConfig {

    /**
     * translateType : how the translation is managed : user or forced.
     * user > according to the current user language
     * forced > according to the forcedLang defined in the config
     */
    private String translateType;
    /**
     * forcedLang : if translateType is set to "forced", which language is selected in the translation.
     */
    private String forcedLang;
    /**
     * defaultSpeakMode : the speak mode which is set to player on joining and if there is a problm in the speak mode selection
     */
    private String defaultSpeakMode;
    /**
     * speakModes : the speak modes with the config file
     */
    private final HashMap<String, SpeakMode> speakModes = new HashMap<>();

    /**
     * GSON : the gson instance to write json (saveConfig)
     */
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    /**
     * The config file .json
     */
    private final File configFile;
    public AddonConfig(File configFile) {
        this.configFile = configFile;
        // Create new file only if doesn't exist, even if there is a problem into file, don't erase data but print errors in read !
        if(!configFile.exists() || configFile.length()==0) constructConfigFile();
        else readConfigFile();
    }

    /**
     * Create the default file if doesn't exist. Also set default config values above, equivalent to read file.
     */
    public void constructConfigFile(){
        try {
            LogUtils.basicLog("Config file doesn't exist, trying to create it with default values..");
            this.configFile.createNewFile();

            translateType = "user";
            forcedLang = "en_us";
            defaultSpeakMode = "SPEAK";

            HashMap<String, String> translations = new HashMap<>();
            translations.put("fr_fr", "Vous chuchottez");
            translations.put("en_us", "You whisper");

            SpeakMode whisperMode = new SpeakMode("WHISPER", 5, translations);

            translations = new HashMap<>();
            translations.put("fr_fr", "Vous parlez normalement");
            translations.put("en_us", "You speak");
            SpeakMode speakMode = new SpeakMode("SPEAK", 15, translations);

            translations = new HashMap<>();
            translations.put("fr_fr", "Vous criez");
            translations.put("en_us", "You shout");
            SpeakMode shoutMode = new SpeakMode("SHOUT", 30, translations);

            speakModes.clear();
            speakModes.put(whisperMode.getKey(), whisperMode);
            speakModes.put(speakMode.getKey(), speakMode);
            speakModes.put(shoutMode.getKey(), shoutMode);

            saveToConfig();
            LogUtils.successLog("The default config file has been created with default values!");
        } catch (IOException e) {
            LogUtils.errorLog("An error occurred on trying to create config file.");
            e.printStackTrace();
        }
    }

    /**
     * Read the whole json file and transform it to string and objects. Used also for reloading config in game.
     * @return True if successfully read the config, false otherwise.
     */
    public boolean readConfigFile(){
        /* TODO
            If invalid config : try to remove only the problem, or just don't read it & print error
            *
            Error if key-value doesn't exist
         */
        speakModes.clear();
        LogUtils.basicLog("Starting to read config file..");
        if(!isConfigValid()){
            LogUtils.errorLog("Error when reading file, it appears that isn't valid (not json syntax, empty file, or don't exist)\nTrying to delete & recreate file to default values.");
            configFile.delete();
            constructConfigFile();
            return false;
        }
        JsonObject configJson = getConfigAsJsonObject();
        // translationConfig part
        JsonObject translationConfig = configJson.getAsJsonObject("translationConfig");
        translateType = translationConfig.get("translateType").getAsString();
        forcedLang = translationConfig.get("forcedLang").getAsString();
        // defaultSpeakMode part
        defaultSpeakMode = configJson.get("defaultSpeakMode").getAsString();
        // speakModes part
        JsonObject speakModesObject = configJson.getAsJsonObject("speakModes");
        for(Map.Entry<String, JsonElement> entry : speakModesObject.entrySet()){
            String KEY = entry.getKey();

            JsonObject speakModeObject = (JsonObject) entry.getValue();
            int distance = speakModeObject.get("distance").getAsInt();
            // Translations part
            HashMap<String, String> translations = new HashMap<>();
            JsonObject smTranslations = speakModeObject.getAsJsonObject("translations");
            for(Map.Entry<String, JsonElement> translationsEntry : smTranslations.entrySet()){
                translations.put(translationsEntry.getKey(), translationsEntry.getValue().getAsString());
            }
            SpeakMode speakMode = new SpeakMode(KEY, distance, translations);
            speakModes.put(KEY, speakMode);
        }
        LogUtils.successLog("The config file has been successfully read!");
        return true;
    }

    /**
     * Save the code values to the config file. Used for easily create the file and save values or to edit the speak modes in game then save them.
     * @return True if successfully save to config, false otherwise.
     */
    public boolean saveToConfig(){
        LogUtils.basicLog("§7Starting to save values to config file..");
        JsonObject configJson = new JsonObject();
        // translationConfig part
        JsonObject translationConfig = new JsonObject();
        translationConfig.add("translateType", new JsonPrimitive(translateType));
        translationConfig.add("forcedLang", new JsonPrimitive(forcedLang));
        configJson.add("translationConfig", translationConfig);
        // defaultSpeakMode part
        configJson.add("defaultSpeakMode", new JsonPrimitive(defaultSpeakMode));
        // speakModes part
        JsonObject speakModesObject = new JsonObject();
        for(Map.Entry<String, SpeakMode> entry : speakModes.entrySet()){
            SpeakMode speakMode = entry.getValue();

            JsonObject speakModeObject = new JsonObject();
            speakModeObject.add("distance", new JsonPrimitive(speakMode.getDistance()));
            // Translations part
            JsonObject smTranslations = new JsonObject();
            for(Map.Entry<String, String> translationsEntry : speakMode.getTranslations().entrySet()){
                smTranslations.add(translationsEntry.getKey(), new JsonPrimitive(translationsEntry.getValue()));
            }
            speakModeObject.add("translations", smTranslations);
            speakModesObject.add(entry.getKey(), speakModeObject);
        }
        configJson.add("speakModes", speakModesObject);
        // Write this JsonObject to file
        try {
            FileUtils.writeStringToFile(getConfigFile(), GSON.toJson(configJson), Charset.defaultCharset(), false);
        } catch (IOException e) {
            LogUtils.errorLog("An error occurred on saving config in file!");
            e.printStackTrace();
            return false;
        }
        LogUtils.successLog("Values has been saved in config file!");
        return true;
    }

    /**
     * @return If the configFile is valid : file exists, can be parse to a valid json
     */
    private boolean isConfigValid() {
        return getConfigFile().exists() && getConfigFile().length()>0 && getConfigAsJsonObject() != null;
    }

    /**
     * @return The config as a Json Object. Return null if can't parse.
     */
    private JsonObject getConfigAsJsonObject(){
        try {
            return (new JsonParser()).parse(Helpers.readFileToString(getConfigFile())).getAsJsonObject();
        }
        catch(JsonParseException exception){
            return null;
        }
    }

    /**
     * @return The config file used everywhere
     */
    public File getConfigFile(){
        return this.configFile;
    }

    /**
     * Change the translateType used for send action bar
     * @param translateType A translation type : "user" or "forced"
     */
    public void setTranslateType(String translateType) {
        this.translateType = translateType;
    }

    /**
     * @return Which translation type is used for send action bar
     */
    public String getTranslateType() {
        return translateType;
    }

    /**
     * @return True if the translateType is "forced"
     */
    public boolean isForcedTranslation(){
        return getTranslateType().equals("forced");
    }

    /**
     * Change the forcedLang used for send action bar
     * @param forcedLang A langCode as "en_us"
     */
    public void setForcedLang(String forcedLang) {
        this.forcedLang = forcedLang;
    }

    /**
     * @return The language used for the action bar message
     */
    public String getForcedLang() {
        return forcedLang;
    }

    /**
     * Change the defaultSpeakMode used by default on player join
     * @param defaultSpeakMode A SpeakMode Key
     */
    public void setDefaultSpeakMode(String defaultSpeakMode) {
        this.defaultSpeakMode = defaultSpeakMode;
    }

    /**
     * @return The speak mode which used by default on player join
     */
    public String getDefaultSpeakMode() {
        return defaultSpeakMode;
    }

    /**
     * @return All the speak modes
     */
    public HashMap<String, SpeakMode> getSpeakModes() {
        return speakModes;
    }

    /**
     * @return All speak modes' keys
     */
    public List<String> getSpeakModeKeys(){
        return new ArrayList<>(getSpeakModes().keySet());
    }

    /**
     * @return If there is no speak modes :O
     */
    public boolean hasNoneSpeakMode(){
        return getSpeakModes().size()==0 || getDefaultSpeakMode().equals("");
    }

    /**
     * @param KEY The Speak mode key
     * @return If a speakMode exist with this KEY
     */
    public boolean speakModeExist(String KEY){
        return getSpeakModes().containsKey(KEY);
    }

    /**
     * Get the value of the KEY.
     * @param KEY The Speak Mode key
     * @return The SpeakMode object linked to the KEY
     */
    public SpeakMode getSpeakMode(String KEY){
        return getSpeakModes().get(KEY);
    }

    /**
     * Add a new speak mode into the map
     * @param KEY The key of the new SpeakMode
     * @param speakMode The SpeakMode object (initied before this with correct values)
     */
    public void addSpeakMode(String KEY, SpeakMode speakMode){
        speakModes.putIfAbsent(KEY, speakMode);
    }

    /**
     * Delete the SpeakMode with the key from the map
     * @param KEY The key of a SpeakMode
     */
    public void removeSpeakMode(String KEY){
        speakModes.remove(KEY);
    }

    /**
     *  Edit the SpeakMode with this KEY
     * @param KEY The key of the SpeakMode
     * @param newSpeakMode The new SpeakMode object
     */
    public void editSpeakMode(String KEY, SpeakMode newSpeakMode){
        speakModes.replace(KEY, newSpeakMode);
    }

    /**
     *  Edit the SpeakMode's distance for the SpeakMode with this KEY
     * @param KEY The key of the SpeakMode
     * @param newDistance The new distance
     */
    public void editSpeakModeDistance(String KEY, int newDistance){
        SpeakMode newSpeakMode = getSpeakMode(KEY);
        newSpeakMode.setDistance(newDistance);
        speakModes.replace(KEY, newSpeakMode);
    }
}