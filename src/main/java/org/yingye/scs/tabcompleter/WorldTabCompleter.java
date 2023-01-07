package org.yingye.scs.tabcompleter;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WorldTabCompleter implements TabCompleter {

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args){
        if (alias.equals("weatherlock") || alias.equals("weatherunlock")) {
            return weatherComplete(sender, alias, args);
        } else if (alias.equals("world")) {
            return worldComplete(sender, args);
        }
        return new ArrayList<>();
    }

    private List<String> weatherComplete(CommandSender sender, String cmd, String[] args) {
        List<String> list = new ArrayList<>();
        ;
        if (args.length == 1) {
            list = sender.getServer().getWorlds().stream().map(World::getName).toList();
        } else if (args.length == 2) {
            if (cmd.equals("weatherlock")) {
                list = Arrays.asList("clear", "rain", "thunder");
            }
        }
        return list;
    }

    private List<String> worldComplete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 1 -> list = Arrays.asList("create", "delete", "tp");
            case 2 -> {
                if (args[0].equals("delete") || args[0].equals("tp")) {
                    list = sender.getServer().getWorlds().stream().map(World::getName).toList();
                }
            }
            case 3 -> {
                switch (args[0]) {
                    case "create" -> list = Arrays.asList("normal", "nether", "end");
                    case "delete" -> list = Arrays.asList("true", "false");
                    case "tp" -> {
                        if (sender instanceof Player player) {
                            list = List.of(player.getLocation().getX() + "");
                        }
                    }
                }
            }
            case 4 -> {
                if(args[0].equals("tp")) {
                    if (sender instanceof Player player) {
                        list = List.of(player.getLocation().getY() + "");
                    }
                }
            }
            case 5 -> {
                if(args[0].equals("tp")) {
                    if (sender instanceof Player player) {
                        list = List.of(player.getLocation().getZ() + "");
                    }
                }
            }
        }
        return list;
    }

}
