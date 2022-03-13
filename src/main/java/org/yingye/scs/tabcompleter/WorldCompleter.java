package org.yingye.scs.tabcompleter;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WorldCompleter implements TabCompleter {

    @SuppressWarnings("all")
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (alias.equals("weatherlock") || alias.equals("weatherunlock")) {
            return weatherComplete(sender, alias, args);
        } else if (alias.equals("world")) {
            return worldComplete(sender, args);
        }
        return null;
    }

    private List<String> weatherComplete(CommandSender sender, String cmd, String[] args) {
        List<String> list = new ArrayList<>();
        ;
        if (args.length == 1) {
            list = sender.getServer().getWorlds().stream().map(World::getName).collect(Collectors.toList());
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
            case 1 -> {
                list = Arrays.asList("create", "delete", "tp");
            }
            case 2 -> {
                if (args[0].equals("delete") || args[0].equals("tp")) {
                    list = sender.getServer().getWorlds().stream().map(World::getName).collect(Collectors.toList());
                }
            }
            case 3 -> {
                if (args[0].equals("create")) {
                    list = Arrays.asList("normal", "nether", "end");
                } else if (args[0].equals("delete")) {
                    list = Arrays.asList("true", "false");
                }
            }
        }
        return list;
    }

}
