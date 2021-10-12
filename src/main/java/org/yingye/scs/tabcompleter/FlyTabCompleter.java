package org.yingye.scs.tabcompleter;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FlyTabCompleter implements TabCompleter {

  /**
   * 参数提示
   *
   * @param sender  触发者
   * @param command 命令实体
   * @param alias   命令名
   * @param args    命令参数, 1:要提示的参数一,2:要提示的参数二
   * @return 用于提示的参数
   */
  @SuppressWarnings("all")
  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    ArrayList<String> list = new ArrayList<>();
    if (args.length == 1) {
      Server server = sender.getServer();
      ArrayList<? extends Player> players = new ArrayList<>(server.getOnlinePlayers());
      for (Player player : players) {
        list.add(player.getName());
      }
    } else if (args.length == 2) {
      list.add("on");
      list.add("off");
    }
    return list;
  }

}