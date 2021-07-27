package fr.zeevoker2vex.voicerange.common.network;

import fr.zeevoker2vex.voicerange.common.VoiceRangeAddon;
import fr.zeevoker2vex.voicerange.common.network.server.PlayerChangeSpeakModePacket;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {

    private SimpleNetworkWrapper network;

    private static NetworkHandler instance;

    private int nextID = 0;

    public static NetworkHandler getInstance() {
        if (instance == null) instance = new NetworkHandler();
        return instance;
    }

    public SimpleNetworkWrapper getNetwork() {
        return network;
    }

    public void registerPackets() {
        this.network = NetworkRegistry.INSTANCE.newSimpleChannel(VoiceRangeAddon.MOD_ID);

        registerPacket(PlayerChangeSpeakModePacket.ServerHandler.class, PlayerChangeSpeakModePacket.class, Side.SERVER);
    }

    private <REQ extends IMessage, REPLY extends IMessage> void registerPacket(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
        network.registerMessage(messageHandler, requestMessageType, nextID, side);
        nextID++;
    }
}