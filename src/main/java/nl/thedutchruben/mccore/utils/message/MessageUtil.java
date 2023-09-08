package nl.thedutchruben.mccore.utils.message;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;


@UtilityClass
public class MessageUtil {

    public void sendClickableCommand(CommandSender player, String message, String command) {
        TextComponent component = new TextComponent(
                TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));

        player.spigot().sendMessage(component);
    }

    public void sendClickableCommandHover(CommandSender player, String message, String command, String description) {
        TextComponent component = new TextComponent(
                TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(description)));

        player.spigot().sendMessage(component);
    }

    public void sendUrlMessage(CommandSender player, String message, String url, String description) {
        TextComponent component = new TextComponent(
                TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(description)));

        player.spigot().sendMessage(component);
    }

    public TextComponent getUrlMessage(String message, String url, String description) {
        TextComponent component = new TextComponent(
                TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatColor.translateAlternateColorCodes('&', description))));
        return component;
    }

    public String translateHexColorCodes(String startTag, String endTag, String message)
    {
        String[] split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        if(Integer.parseInt(split[1]) >= 16) {
            final Pattern hexPattern = Pattern.compile(startTag + "#?([A-Fa-f0-9]{6})" + endTag);
            Matcher matcher = hexPattern.matcher(message);
            StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
            while (matcher.find())
            {
                String group = matcher.group(1).replace("#", "");
                matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                        + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                        + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                        + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
                );
            }
            return matcher.appendTail(buffer).toString();
        }else{
            return message;
        }

    }

}
