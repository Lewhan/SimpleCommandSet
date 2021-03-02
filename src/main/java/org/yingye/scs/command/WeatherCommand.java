package org.yingye.scs.command;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.yingye.scs.status.Weather;

import java.util.HashMap;

public class WeatherCommand implements CommandExecutor {
  public static final HashMap<World, Weather> LOCKED_WORLD = new HashMap<>();
  public static final HashMap<String, String> WEATHER_TYPE = new HashMap<>();
  public static final HashMap<World, BukkitRunnable> MONITORS = new HashMap<>();

  static {
    WEATHER_TYPE.put("clear", "晴天");
    WEATHER_TYPE.put("rain", "雨天");
    WEATHER_TYPE.put("thunder", "雷雨");
  }


  private final Plugin plugin;

  public WeatherCommand(Plugin plugin) {
    this.plugin = plugin;
  }

  @SuppressWarnings("all")
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
    switch (weather) {
      case "clear":
        LOCKED_WORLD.get(world).setClear(true);
        break;
      case "rain":
        LOCKED_WORLD.get(world).setRain(true);
        break;
      case "thunder":
        LOCKED_WORLD.get(world).setThunder(true);
        break;
    }
    monitor(world);
    sender.sendMessage(ChatColor.GREEN + "已将世界(" + ChatColor.AQUA + world.getName() + ChatColor.GREEN + ")的天气设为: " + WEATHER_TYPE.get(weather));
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
    Weather weather = LOCKED_WORLD.get(world);
    if (weather == null) {
      weather = new Weather();
      LOCKED_WORLD.put(world, weather);
    }
    weather.setClear(false);
    weather.setRain(false);
    weather.setThunder(false);
    BukkitRunnable runnable = MONITORS.remove(world);
    if (runnable != null) {
      runnable.cancel();
    }
    sender.sendMessage(ChatColor.GREEN + "已解除世界(" + ChatColor.AQUA + world.getName() + ChatColor.GREEN + ")的天气锁定");
  }

  private void monitor(World world) {
    world.setWeatherDuration(20);
    BukkitRunnable runnable = createRunnable(world);
    runnable.runTaskLater(plugin, world.getWeatherDuration());
    MONITORS.put(world, runnable);
  }

  private void monitor(World world, int tick) {
    BukkitRunnable runnable = createRunnable(world);
    runnable.runTaskLater(plugin, tick);
    System.out.println("下一次天气时间重置在: " + tick / 20 + " 秒之后");
    MONITORS.put(world, runnable);
  }

  private BukkitRunnable createRunnable(World world) {
    return new BukkitRunnable() {
      @Override
      public void run() {
        Weather weather = LOCKED_WORLD.get(world);
        if (weather.isClear()) {
          world.setThundering(false);
          world.setStorm(false);
        } else if (weather.isRain()) {
          world.setThundering(false);
          world.setStorm(true);
        } else if (weather.isThunder()) {
          world.setStorm(true);
          world.setThundering(true);
        }
        world.setWeatherDuration(1200 * 20);
        monitor(world, world.getWeatherDuration());
      }
    };
  }

}
