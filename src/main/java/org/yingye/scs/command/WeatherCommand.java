package org.yingye.scs.command;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.slf4j.Logger;
import org.yingye.scs.core.Config;
import org.yingye.scs.core.Core;
import org.yingye.scs.enums.WeatherStatus;
import org.yingye.scs.util.SimpleUtil;

import java.util.HashMap;

public class WeatherCommand implements CommandExecutor {
    private static final Logger log = Core.log;
    private static final Plugin plugin = Core.GLOBAL_PLUGIN;
    public static final HashMap<World, WeatherStatus> LOCKED_WORLD = new HashMap<>();
    public static final HashMap<String, String> WEATHER_TYPE = new HashMap<>();
    public static final HashMap<World, BukkitRunnable> MONITORS = new HashMap<>();

    static {
        WEATHER_TYPE.put("clear", "晴天");
        WEATHER_TYPE.put("rain", "雨天");
        WEATHER_TYPE.put("thunder", "雷雨");
    }

    /**
     * 命令触发器
     *
     * @param sender  操作者
     * @param command 触发的命令
     * @param label   命令名
     * @param args    参数
     * @return 是否回显
     */
    @SuppressWarnings("all")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("weatherlock") || label.equalsIgnoreCase("simplecommandset:weatherlock")) {
            weatherLock(sender, args);
        } else if (label.equals("weatherunlock") || label.equalsIgnoreCase("simplecommandset:weatherunlock")) {
            if (args.length <= 0) {
                if (sender instanceof Player) {
                    weatherUnlock(sender, ((Player) sender).getWorld());
                }
            } else {
                weatherUnlock(sender, args[0]);
            }
        } else if (label.equals("weatherinfo") || label.equalsIgnoreCase("simplecommandset:weatherinfo")) {
            weatherInfo(sender, args);
        }
        return true;
    }

    /**
     * 锁定天气的调配中心
     *
     * @param sender 操作者
     * @param args   参数
     */
    private void weatherLock(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            weatherLockByOperator((Player) sender, args);
        } else {
            weatherLockByConsole(sender, args);
        }
    }

    /**
     * 来自终端操作的天气锁定
     *
     * @param sender 终端对象
     * @param args   参数
     */
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

    /**
     * 来自管理员操作的天气锁定
     *
     * @param player 管理员对象
     * @param args   参数
     */
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

    /**
     * 锁定为当前天气
     *
     * @param sender 操作者
     * @param world  要锁定天气的世界
     */
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

    /**
     * 锁定为指定的天气
     *
     * @param sender  操作者
     * @param world   要锁定的世界
     * @param weather 要锁定的天气
     */
    private void weatherLock(CommandSender sender, World world, String weather) {
        weatherUnlock(sender, world);
        switch (weather) {
            case "clear" -> LOCKED_WORLD.put(world, WeatherStatus.Clear);
            case "rain" -> LOCKED_WORLD.put(world, WeatherStatus.Rain);
            case "thunder" -> LOCKED_WORLD.put(world, WeatherStatus.Thunder);
        }
        monitor(world);
        sender.sendMessage(ChatColor.GREEN + "已将世界(" + ChatColor.AQUA + world.getName() + ChatColor.GREEN + ")的天气设为: " + WEATHER_TYPE.get(weather));
        log.warn(SimpleUtil.getFormatDate() + " --- 管理员: " + sender.getName() + ",将世界: " + world.getName() + "的天气锁定为: " + WEATHER_TYPE.get(weather));
    }

    /**
     * 解除天气锁定
     *
     * @param sender    操作者
     * @param worldName 世界名
     */
    private void weatherUnlock(CommandSender sender, String worldName) {
        World world = sender.getServer().getWorld(worldName);
        if (world == null) {
            sender.sendMessage(ChatColor.RED + "输入的世界名无效，未找到该世界");
        } else {
            weatherUnlock(sender, world);
        }
    }

    /**
     * 接触天气锁定
     *
     * @param sender 操作者
     * @param world  要解除锁定的世界
     */
    private void weatherUnlock(CommandSender sender, World world) {
        LOCKED_WORLD.remove(world);
        BukkitRunnable runnable = MONITORS.remove(world);
        if (runnable != null) {
            runnable.cancel();
        }
        sender.sendMessage(ChatColor.GREEN + "已解除世界(" + ChatColor.AQUA + world.getName() + ChatColor.GREEN + ")的天气锁定");
        log.info(SimpleUtil.getFormatDate() + " --- 管理员: " + sender.getName() + ",解除了世界: " + world.getName() + "的天气锁定");
    }

    /**
     * 定时增加天气剩余时间
     *
     * @param world 要延长天气时间的世界
     */
    private void monitor(World world) {
        world.setWeatherDuration(20);
        int tick = Config.WEATHER.get("switchSecond") * 20;
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                WeatherStatus status = LOCKED_WORLD.get(world);
                if (status.equals(WeatherStatus.Clear)) {
                    world.setThundering(false);
                    world.setStorm(false);
                } else if (status.equals(WeatherStatus.Rain)) {
                    world.setThundering(false);
                    world.setStorm(true);
                } else if (status.equals(WeatherStatus.Thunder)) {
                    world.setStorm(true);
                    world.setThundering(true);
                }
                world.setWeatherDuration(tick);
            }
        };
        runnable.runTaskTimer(plugin, world.getWeatherDuration(), tick - 20);
        MONITORS.put(world, runnable);
    }


    private void weatherInfo(CommandSender sender, String[] args) {
        if (args.length <= 0) {
            currentWeatherInfo(sender);
        } else {
            worldWeatherInfo(sender, args[0]);
        }
    }

    private void currentWeatherInfo(CommandSender sender) {
        if (sender instanceof Player player) {
            World world = player.getWorld();
            player.sendMessage(worldWeatherInfo("当前世界为: ", world));
        } else {
            sender.sendMessage(ChatColor.RED + "缺少必要参数: " + ChatColor.GREEN + "worldName");
        }
    }

    private void worldWeatherInfo(CommandSender sender, String worldName) {
        World world = sender.getServer().getWorld(worldName);
        if (world != null) {
            sender.sendMessage(worldWeatherInfo("你查看的世界为: ", world));
        } else {
            sender.sendMessage(ChatColor.RED + "你所查看的世界不存在");
        }
    }

    private String worldWeatherInfo(String title, World world) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(ChatColor.GREEN).append(title).append(world.getName()).append("\n").append("当前");
        if (world.isClearWeather()) {
            sBuilder.append("天气为晴天,");
        } else {
            if (world.isThundering()) {
                sBuilder.append("天气为雷雨,");
            } else {
                sBuilder.append("天气为雨天,");
            }
        }
        sBuilder.append(" 下次天气切换在").append(ChatColor.AQUA).append(world.getWeatherDuration() / 20).append(ChatColor.GREEN).append("秒后");
        return sBuilder.toString();
    }

}
