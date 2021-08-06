package fr.zeevoker2vex.voicerange.common;

import fr.zeevoker2vex.voicerange.common.network.NetworkHandler;
import fr.zeevoker2vex.voicerange.server.ServerProxy;
import fr.zeevoker2vex.voicerange.server.commands.VoiceRangeCommand;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

@Mod(modid = VoiceRangeAddon.MOD_ID, name = VoiceRangeAddon.NAME, version = VoiceRangeAddon.VERSION)
public class VoiceRangeAddon {

    public static final String MOD_ID = "z-voice-range";
    public static final String NAME = "Z-VoiceRange";
    public static final String VERSION = "1.0.1";

    public static final String CONFIG_FOLDER = "Z-MVC-Addons";

    @SidedProxy(clientSide = "fr.zeevoker2vex.voicerange.client.ClientProxy", serverSide = "fr.zeevoker2vex.voicerange.server.ServerProxy", modId = VoiceRangeAddon.MOD_ID)
    public static CommonProxy proxy;

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        NetworkHandler.getInstance().registerPackets();

        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event){
        event.registerServerCommand(new VoiceRangeCommand());
    }

    @EventHandler
    public void onServerStopping(FMLServerStoppingEvent event){
        if(event.getSide().equals(Side.SERVER)) ServerProxy.getConfig().saveToConfig();
    }

    public static Logger getLogger() {
        return logger;
    }
}