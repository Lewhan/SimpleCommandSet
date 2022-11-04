package org.yingye.scs.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class TeleportTabCompleter implements TabCompleter {

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return new ArrayList<>();
        }
        if (args.length > 1) {
            return new ArrayList<>();
        }
        ArrayList<? extends Player> players = new ArrayList<>(sender.getServer().getOnlinePlayers());
        ArrayList<String> names = new ArrayList<>();
        for (Player value : players) {
            if (!player.getName().equals(value.getName())) {
                names.add(value.getName());
            }
        }
        return names;
    }
}
