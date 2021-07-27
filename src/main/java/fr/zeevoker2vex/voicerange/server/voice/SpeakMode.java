package fr.zeevoker2vex.voicerange.server.voice;

import java.util.HashMap;

public class SpeakMode {

    private final String KEY;
    private int distance;
    /**
     * Key : the language code, like "fr_fr" or "en_us".
     * Value : the translation for the key lang.
     */
    private HashMap<String, String> translations;

    /**
     *
     * @param KEY The KEY of the speak mode.
     * @param distance The voice range distance for this speak mode.
     * @param translations The translations on changing speak mode.
     */
    public SpeakMode(String KEY, int distance, HashMap<String, String> translations) {
        this.KEY = KEY;
        this.distance = distance;
        this.translations = translations;
    }

    /**
     * @return The KEY of this speak mode
     */
    public String getKey() {
        return KEY;
    }

    /**
     * @return The voice range for this speak mode
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Edit the voice range
     * @param distance The new distance
     */
    public void setDistance(int distance) {
        this.distance = distance;
    }

    /**
     * @return All translations of this speak mode
     */
    public HashMap<String, String> getTranslations() {
        return translations;
    }

    /**
     * @param langCode The language code that you want get the translation
     * @return The translation for this lang code
     */
    public String getTranslation(String langCode){
        return getTranslations().getOrDefault(langCode, "§cCannot get translation for langCode "+langCode+" in speak mode "+ getKey());
    }

    /**
     * @param langCode The language code that you want get the translation
     * @return If a translation exists for this SpeakMode with this langCode
     */
    public boolean hasTranslation(String langCode){
        return getTranslations().containsKey(langCode);
    }

    /**
     * Add a new translation for the speak mode, in the specified lang code
     * @param langCode The language code
     * @param translation The translation for this lang code
     */
    public void addTranslation(String langCode, String translation){
        translations.putIfAbsent(langCode, translation);
    }

    /**
     * Remove the translation with this langCode from the map.
     * @param langCode The language code
     */
    public void removeTranslation(String langCode){
        translations.remove(langCode);
    }

    /**
     *  Edit the translation of the lang code with the new
     * @param langCode The language code
     * @param newTranslation The new translation for this lang code
     */
    public void editTranslation(String langCode, String newTranslation){
        if(hasTranslation(langCode)) translations.replace(langCode, newTranslation);
        else addTranslation(langCode, newTranslation);
    }

    /**
     * Replace all the translations by the one in param
     * @param translations The new translations map
     */
    public void setTranslations(HashMap<String, String> translations) {
        this.translations = translations;
    }
}