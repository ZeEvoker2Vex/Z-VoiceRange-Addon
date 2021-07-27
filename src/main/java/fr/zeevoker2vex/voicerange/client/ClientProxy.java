package fr.zeevoker2vex.voicerange.client;

import fr.zeevoker2vex.voicerange.common.CommonProxy;
import fr.zeevoker2vex.voicerange.common.VoiceRangeAddon;
import fr.zeevoker2vex.voicerange.common.network.NetworkHandler;
import fr.zeevoker2vex.voicerange.common.network.server.PlayerChangeSpeakModePacket;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy {

    public static final KeyBinding CHANGE_VOICE_RANGE = new KeyBinding("key." + VoiceRangeAddon.MOD_ID + ".voicerange", Keyboard.KEY_H, "key.categories." + VoiceRangeAddon.MOD_ID);

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        ClientRegistry.registerKeyBinding(CHANGE_VOICE_RANGE);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @SubscribeEvent
    public void onEvent(InputEvent.KeyInputEvent event) {
        if (ClientProxy.CHANGE_VOICE_RANGE.isPressed()) {
            NetworkHandler.getInstance().getNetwork().sendToServer(new PlayerChangeSpeakModePacket(FMLClientHandler.instance().getCurrentLanguage()));
        }
    }
}