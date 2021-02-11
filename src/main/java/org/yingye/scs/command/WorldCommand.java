package org.yingye.scs.command;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class WorldCommand implements CommandExecutor {

  public static final List<World> CLEAR = new ArrayList<>();
  public static final List<World> RAIN = new ArrayList<>();
  public static final List<World> THUNDER = new ArrayList<>();
  public static final HashMap<String, String> WEATHER = new HashMap<>();

  static {
    WEATHER.put("clear", "晴天");
    WEATHER.put("rain", "雨天");
    WEATHER.put("thunder", "雷雨");
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (label.equals("weatherlock")) {
      weatherLock(sender, args);
    } else if (label.equals("weatherunlock")) {
      if (args.length <= 0) {
        if (sender instanceof Player) {
          weatherUnlock(sender, ((Player) sender).getWorld());
        }
      } else {
        weatherUnlock(sender, args[0]);
      }
    }
    return true;
  }

  private void weatherLock(CommandSender sender, String[] args) {
    if (sender instanceof Player) {
      weatherLockByOperator((Player) sender, args);
    } else {
      weatherLockByConsole(sender, args);
    }
  }

  private void weatherLockByConsole(CommandSender sender, String[] args) {
    if (args.length <= 0) {
      sender.sendMessage(ChatColor.RED + "缺少必要参数: " + ChatColor.GREEN + "worldName [weather]");
      return;
    }

    if (args.length == 1) {
      World world = sender.getServer().getWorld(args[0]);
      if (world == null) {
        sender.sendMessage(ChatColor.RED + "输入的世界名无效，未找到该世界");
        return;
      }
      weatherLock(sender, world);
    } else {
      World world = sender.getServer().getWorld(args[0]);
      if (world == null) {
        sender.sendMessage(ChatColor.RED + "输入的世界名无效，未找到该世界");
        return;
      }
      weatherLock(sender, world, args[1]);
    }
  }

  private void weatherLockByOperator(Player player, String[] args) {
    World world;
    if (args.length <= 0) {
      world = player.getWorld();
    } else {
      world = player.getServer().getWorld(args[0]);
      if (world == null) {
        player.sendMessage(ChatColor.RED + "输入的世界名无效，未找到该世界");
        return;
      }
    }

    if (args.length == 0 || args.length == 1) {
      weatherLock(player, world);
    } else {
      weatherLock(player, world, args[1]);
    }
  }

  private void weatherLock(CommandSender sender, World world) {
    if (world.isClearWeather()) {
      weatherLock(sender, world, "clear");
    } else {
      if (world.isThundering()) {
        weatherLock(sender, world, "thunder");
      } else {
        weatherLock(sender, world, "rain");
      }
    }
  }

  private void weatherLock(CommandSender sender, World world, String weather) {
    weatherUnlock(sender, world);
    if (weather.equalsIgnoreCase("clear")) {
      CLEAR.add(world);
      world.setStorm(false);
    } else if (weather.equalsIgnoreCase("rain")) {
      RAIN.add(world);
      world.setStorm(true);
    } else if (weather.equalsIgnoreCase("thunder")) {
      THUNDER.add(world);
      world.setStorm(true);
      world.setThundering(true);
    } else {
      return;
    }
    world.setWeatherDuration(10);
    sender.sendMessage(ChatColor.GREEN + "已将世界(" + ChatColor.AQUA + world.getName() + ChatColor.GREEN + ")的天气设为: " + WEATHER.get(weather));
  }

  private void weatherUnlock(CommandSender sender, String worldName) {
    World world = sender.getServer().getWorld(worldName);
    if (world == null) {
      sender.sendMessage(ChatColor.RED + "输入的世界名无效，未找到该世界");
    } else {
      weatherUnlock(sender, world);
    }
  }

  private void weatherUnlock(CommandSender sender, World world) {
    CLEAR.remove(world);
    RAIN.remove(world);
    THUNDER.remove(world);
    sender.sendMessage(ChatColor.GREEN + "已解除世界(" + ChatColor.AQUA + world.getName() + ChatColor.GREEN + ")的天气锁定");
  }

}
