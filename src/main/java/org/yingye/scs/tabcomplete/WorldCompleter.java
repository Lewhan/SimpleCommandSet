package org.yingye.scs.tabcomplete;

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
    List<String> list = null;
    if (args.length == 1) {
      list = sender.getServer().getWorlds().stream().map(World::getName).collect(Collectors.toList());
    } else if (args.length == 2) {
      if (cmd.equals("weatherlock")) {
        String[] arr = {"clear", "rain", "thunder"};
        list = Arrays.asList(arr);
      }
    } else {
      list = new ArrayList<>();
    }
    return list;
  }

  private List<String> worldComplete(CommandSender sender, String[] args) {
    List<String> list;
    if (args.length == 1) {
      String[] arr = {"create", "delete", "tp"};
      list = Arrays.asList(arr);
    } else if (args.length == 2) {
      if (args[0].equals("delete") || args[0].equals("tp")) {
        list = sender.getServer().getWorlds().stream().map(World::getName).collect(Collectors.toList());
      } else {
        list = new ArrayList<>();
      }
    } else if (args.length == 3) {
      if (args[0].equals("create")) {
        String[] arr = {"normal", "nether", "end"};
        list = Arrays.asList(arr);
      } else if (args[0].equals("delete")) {
        String[] arr = {"true", "false"};
        list = Arrays.asList(arr);
      } else {
        list = new ArrayList<>();
      }
    } else {
      list = new ArrayList<>();
    }
    return list;
  }

}
