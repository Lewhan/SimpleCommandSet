package org.yingye.scs.tabcomplete;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;
import java.util.stream.Collectors;

public class WorldCompleter implements TabCompleter {

  @SuppressWarnings("all")
  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    if (alias.equals("weatherlock") || alias.equals("weatherunlock")) {
      return weatherComplete(sender, alias, args);
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
        list = List.of(arr);
      }
    }
    return list;
  }
}
