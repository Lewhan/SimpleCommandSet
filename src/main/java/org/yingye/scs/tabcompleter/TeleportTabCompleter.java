package org.yingye.scs.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class TeleportTabCompleter implements TabCompleter {

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    if (!(sender instanceof Player)) {
      return new ArrayList<>();
    }
    if (args.length > 1) {
      return new ArrayList<>();
    }
    Player player = (Player) sender;
    ArrayList<? extends Player> players = new ArrayList<>(sender.getServer().getOnlinePlayers());
    ArrayList<String> names = new ArrayList<>();
    for (Player value : players) {
      if (!player.getDisplayName().equals(value.getDisplayName())) {
        names.add(value.getDisplayName());
      }
    }
    return names;
  }
}
