package nl.thedutchruben.mccore.commands.mccore;

import nl.thedutchruben.mccore.commands.Command;
import nl.thedutchruben.mccore.commands.SubCommand;
import org.bukkit.command.CommandSender;


@Command(command = "tdrmccore",description = "See info of the tdrcore",
        aliases = "tdrcore",console = true,
        permission = "tdrmccore.command.tdrmcccore")
public class TdrMcCoreCommand {

    @SubCommand(subCommand = "info")
    public void main(CommandSender commandSender,String[] args){
        commandSender.sendMessage("Tdrmccore version 1.0");
    }
}
