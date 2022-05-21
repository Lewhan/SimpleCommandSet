package org.yingye.scs.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.yingye.scs.command.*;
import org.yingye.scs.listener.*;
import org.yingye.scs.tabcompleter.FlyTabCompleter;
import org.yingye.scs.tabcompleter.HomeTabCompleter;
import org.yingye.scs.tabcompleter.TeleportTabCompleter;
import org.yingye.scs.tabcompleter.WorldCompleter;

import java.io.File;

@SuppressWarnings("all")
public class Core extends JavaPlugin {

    private static CommandSender sender;

    /**
     * 全局信息通知标识
     */
    public static final String PLUGIN_TAG = "SimpleCommandSet";
    public static final String LOG_PREFIX = "[" + PLUGIN_TAG + "] ";

    @Override
    public void onEnable() {
        sender = getServer().getConsoleSender();

        if (new File("./plugins/SimpleCommandSet/config.yml").exists() == false) {
            saveDefaultConfig();
        }
        try {
            printInfo(ChatColor.GREEN + "配置加载中...");

            Config.loadConfig();

            printInfo(ChatColor.GREEN + "命令加载中...");
            loadCommand();

            printInfo(ChatColor.GREEN + "命令提示加载中...");
            loadTabCompleter();

            printInfo(ChatColor.GREEN + "监听器加载中...");
            loadListener();

            printInfo(ChatColor.GREEN + "加载完毕");
        } catch (Exception e) {
            printErr("初始化过程中出现异常，停用本插件");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        printInfo(ChatColor.GREEN + "插件卸载");
    }

    private void loadCommand() {
        // tpa
        getCommand("tpa").setExecutor(new TeleportCommand());
        getCommand("tpahere").setExecutor(new TeleportCommand());
        getCommand("accept").setExecutor(new TeleportCommand());
        getCommand("tpclear").setExecutor(new TeleportCommand());
        getCommand("deaccept").setExecutor(new TeleportCommand());

        // back
        getCommand("back").setExecutor(new BackCommand());

        // fly
        getCommand("fly").setExecutor(new FlyCommand());

        // god
        getCommand("god").setExecutor(new GodCommand());

        // home
        getCommand("home").setExecutor(new HomeCommand());
        getCommand("sethome").setExecutor(new HomeCommand());
        getCommand("delhome").setExecutor(new HomeCommand());

        // debug
        getCommand("ohk").setExecutor(new DebugCommand());
        getCommand("hb").setExecutor(new DebugCommand());
        getCommand("cl").setExecutor(new DebugCommand());

        // weather
        getCommand("weatherinfo").setExecutor(new WeatherCommand());
        getCommand("weatherlock").setExecutor(new WeatherCommand());
        getCommand("weatherunlock").setExecutor(new WeatherCommand());

        // world
        getCommand("world").setExecutor(new WorldCommand());

        // other
        getCommand("remake").setExecutor(new OtherCommand());
    }

    private void loadTabCompleter() {
        getCommand("tpa").setTabCompleter(new TeleportTabCompleter());
        getCommand("tpahere").setTabCompleter(new TeleportTabCompleter());

        getCommand("fly").setTabCompleter(new FlyTabCompleter());

        // 不应该有选项提示，随便注册一个提示器
        getCommand("back").setTabCompleter(new HomeTabCompleter());

        getCommand("home").setTabCompleter(new HomeTabCompleter());
        getCommand("sethome").setTabCompleter(new HomeTabCompleter());
        getCommand("delhome").setTabCompleter(new HomeTabCompleter());

        getCommand("weatherlock").setTabCompleter(new WorldCompleter());
        getCommand("weatherunlock").setTabCompleter(new WorldCompleter());

        getCommand("world").setTabCompleter(new WorldCompleter());
    }

    private void loadListener() {
        getServer().getPluginManager().registerEvents(new TeleportListener(), this);
        getServer().getPluginManager().registerEvents(new DeadListener(), this);
        getServer().getPluginManager().registerEvents(new GodListener(), this);
        getServer().getPluginManager().registerEvents(new CommandListener(), this);

        getServer().getPluginManager().registerEvents(new DebugCommandListener(), this);
    }

    /**
     * 获取全局插件对象
     */
    public static Plugin getPlugin() {
        return Bukkit.getServer().getPluginManager().getPlugin(PLUGIN_TAG);
    }

    public static void printInfo(String message) {
        sender.sendMessage(LOG_PREFIX + message);
    }

    public static void printWarn(String message) {
        sender.sendMessage(ChatColor.YELLOW + LOG_PREFIX + message);
    }

    public static void printErr(String message) {
        sender.sendMessage(ChatColor.RED + LOG_PREFIX + message);
    }

    public static void sendInfo(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

    public static void sendWarn(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.YELLOW + message);
    }

    public static void sendErr(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.RED + message);
    }

}
