package org.yingye.scs.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.yingye.scs.command.TeleportCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TeleportTabCompleter implements TabCompleter {

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender instanceof Player player) {
            return switch (alias) {
                case "tpa", "tpahere" -> tpa(player, args);
                case "accept", "deaccept" -> accept(player, args);
                default -> List.of();
            };
        } else {
            return List.of();
        }
    }

    private List<String> tpa(Player player, String[] args) {
        return args.length == 1
                ? player.getServer().getOnlinePlayers().stream().map(Player::getName).filter(s -> !s.equals(player.getName())).toList()
                : List.of();
    }

    private List<String> accept(Player player, String[] args) {
        Map<ArrayList<String>, BukkitRunnable> requests = TeleportCommand.getRequests();
        return switch (args.length) {
            case 1 -> requests.keySet().stream().filter(strings -> strings.get(1).equals(player.getName())).map(strings -> strings.get(0)).toList();
            case 2 -> requests.keySet().stream().filter(strings -> strings.get(0).equals(args[0]) && strings.get(1).equals(player.getName())).map(strings -> strings.get(2)).toList();
            default -> List.of();
        };
    }

}
