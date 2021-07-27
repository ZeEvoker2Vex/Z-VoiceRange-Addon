package fr.zeevoker2vex.voicerange.common.utils;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class MessageBuilder {

    public static class ComponentMessage {

        private TextComponentBase component;

        public ComponentMessage(String text){
            component = new TextComponentString(text.replaceAll("&", "§"));
        }
        public ComponentMessage(String key, Object... parameters){
            component = new TextComponentTranslation(key, parameters);
        }

        public ComponentMessage addString(String text){
            component.appendText(text.replaceAll("&", "§"));
            return this;
        }

        public ComponentMessage addLine(String text){
            component.appendText("\n"+text.replaceAll("&", "§"));
            return this;
        }

        public ComponentMessage addTranslation(String key, Object... parameters){
            component.appendSibling(new TextComponentTranslation(key, parameters));
            return this;
        }

        public ComponentMessage addTranslationLine(String key, Object... parameters){
            component.appendText("\n").appendSibling(new TextComponentTranslation(key, parameters));
            return this;
        }

        public ComponentMessage reset(String text){
            component = new TextComponentString(text.replaceAll("&", "§"));
            return this;
        }

        public ComponentMessage reset(String key, Object... parameters){
            component = new TextComponentTranslation(key, parameters);
            return this;
        }

        public TextComponentBase getComponent() {
            return component;
        }

        public void sendTo(ICommandSender sender){
            sender.sendMessage(getComponent());
        }
    }
}