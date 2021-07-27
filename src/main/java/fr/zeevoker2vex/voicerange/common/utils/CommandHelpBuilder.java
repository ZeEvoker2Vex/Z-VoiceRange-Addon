package fr.zeevoker2vex.voicerange.common.utils;

import java.util.List;

public class CommandHelpBuilder {

    public String cmdName;
    public List<String> args;

    public CommandHelpBuilder(String cmdName, List<String> args) {
        this.cmdName = cmdName;
        this.args = args;
    }

    public MessageBuilder.ComponentMessage build(){
        String prefix = "cmd."+cmdName+".help.";
        MessageBuilder.ComponentMessage helpMessage = new MessageBuilder.ComponentMessage(prefix+"title", this.cmdName) // Le "header" du msg
                .addTranslationLine(prefix+"syntax") // La syntaxe de la commande (usage)
                .addTranslationLine(prefix+"argstitle"); // La phrase d'intro aux explications
        // Toutes les arguments sans détails
        for(String arg : this.args){
            String argKey = prefix+arg;
            helpMessage.addLine("    ").addTranslation(argKey);
        }
        return helpMessage;
    }

    public static class ArgumentBuilder {

        public String cmdName, arg;
        public int numberOfDetails;

        public ArgumentBuilder(String cmdName, String arg, int numberOfDetails) {
            this.cmdName = cmdName;
            this.arg = arg;
            this.numberOfDetails = numberOfDetails;
        }

        public MessageBuilder.ComponentMessage build(){
            String prefix = "cmd."+cmdName+".help.";
            String argKey = prefix+arg;
            MessageBuilder.ComponentMessage helpMessage = new MessageBuilder.ComponentMessage(prefix+"details.title", this.cmdName, this.arg).addTranslationLine(argKey);
            if(numberOfDetails>0){
                for(int i = 1; i < numberOfDetails+1; i++){
                    String argDetailKey = argKey+"."+i;
                    helpMessage.addTranslationLine(argDetailKey);
                }
            }
            return helpMessage;
        }
    }
}