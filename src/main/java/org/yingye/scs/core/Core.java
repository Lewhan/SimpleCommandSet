package org.yingye.scs.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.yingye.scs.command.*;
import org.yingye.scs.listener.*;
import org.yingye.scs.tabcompleter.*;

import java.io.File;
import java.util.List;

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

        if (!new File("./plugins/SimpleCommandSet/config.yml").exists()) {
            saveDefaultConfig();
        }
        try {
            printSuccess("配置加载中...");

            Config.loadConfig();

            printSuccess("命令加载中...");
            loadCommand();

            printSuccess("命令提示加载中...");
            loadTabCompleter();

            printSuccess("监听器加载中...");
            loadListener();

            printSuccess("加载完毕");
        } catch (Exception e) {
            printErr("初始化过程中出现异常，停用本插件");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        printSuccess("插件卸载完成");
    }

    @SuppressWarnings("all")
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
        getCommand("showdata").setExecutor(new DebugCommand());

        // weather
        getCommand("weatherinfo").setExecutor(new WeatherCommand());
        getCommand("weatherlock").setExecutor(new WeatherCommand());
        getCommand("weatherunlock").setExecutor(new WeatherCommand());

        // world
        getCommand("world").setExecutor(new WorldCommand());

        // other
        getCommand("remake").setExecutor(new OtherCommand());
    }

    @SuppressWarnings("all")
    private void loadTabCompleter() {
        getCommand("tpa").setTabCompleter(new TeleportTabCompleter());
        getCommand("tpahere").setTabCompleter(new TeleportTabCompleter());
        getCommand("accept").setTabCompleter(new TeleportTabCompleter());
        getCommand("deaccept").setTabCompleter(new TeleportTabCompleter());
        getCommand("tpclear").setTabCompleter(new EmptyTabCompleter());

        getCommand("fly").setTabCompleter(new GodAndFlyTabCompleter());

        getCommand("god").setTabCompleter(new GodAndFlyTabCompleter());

        getCommand("back").setTabCompleter(new EmptyTabCompleter());

        getCommand("home").setTabCompleter(new HomeTabCompleter());
        getCommand("sethome").setTabCompleter(new HomeTabCompleter());
        getCommand("delhome").setTabCompleter(new HomeTabCompleter());

        getCommand("weatherlock").setTabCompleter(new WorldTabCompleter());
        getCommand("weatherunlock").setTabCompleter(new WorldTabCompleter());

        getCommand("world").setTabCompleter(new WorldTabCompleter());

        getCommand("showdata").setTabCompleter(new DebugTabCompleter());
        getCommand("hb").setTabCompleter(new DebugTabCompleter());
        getCommand("ohk").setTabCompleter(new DebugTabCompleter());
        getCommand("cl").setTabCompleter(new DebugTabCompleter());

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

    public static void printSuccess(String message) {
        TextComponent text = Component.empty()
                .children(List.of(Component.text(LOG_PREFIX, TextColor.fromCSSHexString("#FFFFFF")),
                                Component.text(message, TextColor.fromCSSHexString("#55FF55"))
                        )
                );
        sender.sendMessage(text);
    }

    public static void printWarn(String message) {
        TextComponent text = Component.empty()
                .children(List.of(Component.text(LOG_PREFIX, TextColor.fromCSSHexString("#FFFFFF")),
                                Component.text(message, TextColor.fromCSSHexString("#FFFF55"))
                        )
                );
        sender.sendMessage(text);
    }

    public static void printErr(String message) {
        TextComponent text = Component.empty()
                .children(List.of(Component.text(LOG_PREFIX, TextColor.fromCSSHexString("#FFFFFF")),
                                Component.text(message, TextColor.fromCSSHexString("#FF5555"))
                        )
                );
        sender.sendMessage(text);
    }

    public static void sendInfo(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(message);
        } else {
            printInfo(message);
        }
    }

    public static void sendSuccess(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(Component.text(message, TextColor.fromCSSHexString("#55FF55")));
        } else {
            printSuccess(message);
        }
    }

    public static void sendWarn(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(Component.text(message, TextColor.fromCSSHexString("#FFFF55")));
        } else {
            printWarn(message);
        }
    }

    public static void sendErr(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(Component.text(message, TextColor.fromCSSHexString("#FF5555")));
        } else {
            printErr(message);
        }
    }

    public static void sendTextComponent(CommandSender sender, TextComponent component) {
        sender.sendMessage(component);
    }

}
