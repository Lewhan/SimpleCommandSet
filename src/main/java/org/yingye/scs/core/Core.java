package org.yingye.scs.core;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.yingye.scs.command.*;
import org.yingye.scs.listener.*;
import org.yingye.scs.tabcomplete.FlyTabCompleter;
import org.yingye.scs.tabcomplete.HomeTabCompleter;
import org.yingye.scs.tabcomplete.TeleportTabCompleter;
import org.yingye.scs.tabcomplete.WorldCompleter;

import java.io.File;

@SuppressWarnings("all")
public class Core extends JavaPlugin {

  private Logger log = getSLF4JLogger();

  @Override
  public void onEnable() {
    if (new File("./plugins/SimpleCommandSet/config.yml").exists() == false) {
      saveDefaultConfig();
    }
    try {
      log.info(ChatColor.GREEN + " 配置加载中...");
      Config.loadConfig();

      log.info(ChatColor.GREEN + " 命令加载中...");
      loadCommand();

      log.info(ChatColor.GREEN + " 命令提示加载中...");
      loadTabCompleter();

      log.info(ChatColor.GREEN + " 监听器加载中...");
      loadListener();

      log.info(ChatColor.GREEN + " 加载完毕");
    } catch (Exception e) {
      log.error(ChatColor.RED + " 初始化过程中出现异常，停用本插件", e);
      getServer().getPluginManager().disablePlugin(this);
    }
  }

  @Override
  public void onDisable() {
    log.info(ChatColor.GREEN + "[SimpleCommandSet] 插件卸载");
  }

  private void loadCommand() {
    // tpa
    getCommand("tpa").setExecutor(new TeleportCommand(this));
    getCommand("tpahere").setExecutor(new TeleportCommand(this));
    getCommand("accept").setExecutor(new TeleportCommand(this));
    getCommand("tpclear").setExecutor(new TeleportCommand(this));
    getCommand("deaccept").setExecutor(new TeleportCommand(this));

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
    getCommand("wtr").setExecutor(new DebugCommand());

    // world
    getCommand("weatherlock").setExecutor(new WeatherCommand(this));
    getCommand("weatherunlock").setExecutor(new WeatherCommand(this));
  }

  private void loadTabCompleter() {
    getCommand("tpa").setTabCompleter(new TeleportTabCompleter());
    getCommand("tpahere").setTabCompleter(new TeleportTabCompleter());

    getCommand("fly").setTabCompleter(new FlyTabCompleter());

    // 不应该有选项提示
    getCommand("back").setTabCompleter(new HomeTabCompleter());

    getCommand("home").setTabCompleter(new HomeTabCompleter());
    getCommand("sethome").setTabCompleter(new HomeTabCompleter());
    getCommand("delhome").setTabCompleter(new HomeTabCompleter());

    getCommand("weatherlock").setTabCompleter(new WorldCompleter());
    getCommand("weatherunlock").setTabCompleter(new WorldCompleter());
  }

  private void loadListener() {
    getServer().getPluginManager().registerEvents(new TeleportListener(), this);
    getServer().getPluginManager().registerEvents(new DeadListener(), this);
    getServer().getPluginManager().registerEvents(new GodListener(), this);
    getServer().getPluginManager().registerEvents(new CommandListener(), this);

    getServer().getPluginManager().registerEvents(new DebugCommandListener(), this);
  }

}
