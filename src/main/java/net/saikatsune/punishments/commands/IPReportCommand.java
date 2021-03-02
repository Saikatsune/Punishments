package net.saikatsune.punishments.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.saikatsune.punishments.Punishments;
import net.saikatsune.punishments.manager.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class IPReportCommand implements CommandExecutor {

    private final Punishments plugin = Punishments.getInstance();
    private final DatabaseManager databaseManager = plugin.getDatabaseManager();

    private final String prefix = plugin.getPrefix();

    private final ArrayList<Player> banEvadingPlayers = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("ipreport")) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                sender.sendMessage(prefix + ChatColor.GRAY + "Scanning " + ChatColor.RED +
                        Bukkit.getOnlinePlayers().size() + ChatColor.GRAY + " total player(s)...");

                for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                    if(databaseManager.isIPAddressBanned(allPlayers)) {
                        banEvadingPlayers.add(allPlayers);

                        TextComponent textComponent = new TextComponent();
                        textComponent.setText(prefix + ChatColor.DARK_RED + allPlayers.getName() + ChatColor.GRAY +
                                " is ban-evading. " + ChatColor.GREEN + "§l[Click Here]");
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(ChatColor.GREEN + "Click here to ban " + allPlayers.getName() + ".").create()));
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ban " + allPlayers.getName() + " Ban Evading"));

                        if(sender instanceof Player) {
                            Player player = (Player) sender;

                            player.spigot().sendMessage(textComponent);
                        } else {
                            sender.sendMessage(prefix + ChatColor.DARK_RED + allPlayers.getName() + ChatColor.GRAY +
                                    " is ban-evading. ");
                        }
                    }
                }

                sender.sendMessage(prefix + ChatColor.GRAY + "Found " + ChatColor.RED +
                        banEvadingPlayers.size() + ChatColor.GRAY + " total ban-evading player(s).");

                banEvadingPlayers.clear();
            });
        }
        return false;
    }
}
