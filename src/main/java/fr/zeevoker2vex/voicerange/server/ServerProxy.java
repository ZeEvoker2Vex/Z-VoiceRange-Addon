package fr.zeevoker2vex.voicerange.server;

import fr.nathanael2611.modularvoicechat.api.HearDistanceEvent;
import fr.zeevoker2vex.voicerange.common.CommonProxy;
import fr.zeevoker2vex.voicerange.common.VoiceRangeAddon;
import fr.zeevoker2vex.voicerange.server.config.AddonConfig;
import fr.zeevoker2vex.voicerange.server.voice.VoiceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.io.File;

public class ServerProxy extends CommonProxy {

    private static AddonConfig addonConfig;
    public File configFile;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        File configFolder = new File(event.getModConfigurationDirectory().getPath(), VoiceRangeAddon.CONFIG_FOLDER);
        if(!configFolder.exists()) configFolder.mkdirs();

        this.configFile = new File(configFolder.getPath(), "VoiceRangeAddon.json");
        addonConfig = new AddonConfig(this.configFile);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @SubscribeEvent
    public void hearDistance(HearDistanceEvent event){
        if(!getConfig().hasNoneSpeakMode() && VoiceManager.playersSpeakMode.containsKey(event.getSpeaker())) event.setDistance(VoiceManager.getPlayerSpeakMode(event.getSpeaker()).getDistance());
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        VoiceManager.connectPlayer(event.player);
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event){
        VoiceManager.disconnectPlayer(event.player);
    }

    public static AddonConfig getConfig() {
        return addonConfig;
    }
}