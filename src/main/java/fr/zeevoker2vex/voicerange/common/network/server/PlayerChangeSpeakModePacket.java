package fr.zeevoker2vex.voicerange.common.network.server;

import fr.zeevoker2vex.voicerange.common.utils.MessageBuilder;
import fr.zeevoker2vex.voicerange.server.ServerProxy;
import fr.zeevoker2vex.voicerange.server.config.AddonConfig;
import fr.zeevoker2vex.voicerange.server.voice.SpeakMode;
import fr.zeevoker2vex.voicerange.server.voice.VoiceManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlayerChangeSpeakModePacket implements IMessage {

    public String languageCode;

    public PlayerChangeSpeakModePacket(String languageCode){
        this.languageCode = languageCode;
    }
    public PlayerChangeSpeakModePacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.languageCode = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.languageCode);
    }

    public static class ServerHandler implements IMessageHandler<PlayerChangeSpeakModePacket, IMessage> {
        @Override
        @SideOnly(Side.SERVER)
        public IMessage onMessage(PlayerChangeSpeakModePacket message, MessageContext ctx) {

            EntityPlayerMP player = ctx.getServerHandler().player;

            // Send error message to player if no speak mode exists.
            if(ServerProxy.getConfig().hasNoneSpeakMode()){
                MessageBuilder.ComponentMessage messageBuilder = new MessageBuilder.ComponentMessage("§c").addTranslation("voicerange.nospeakmode.change");
                player.sendStatusMessage(messageBuilder.getComponent(), true);
                return null;
            }

            // All is do in this method
            SpeakMode newSpeakMode = VoiceManager.setNextSpeakMode(player);

            // Get the lang according to the user or forced language
            AddonConfig config = ServerProxy.getConfig();
            String lang = config.isForcedTranslation() ? config.getForcedLang() : message.languageCode;

            // Send the translation
            player.sendStatusMessage(new TextComponentString(newSpeakMode.getTranslation(lang).replaceAll("&", "§")), true);
            return null;
        }
    }
}