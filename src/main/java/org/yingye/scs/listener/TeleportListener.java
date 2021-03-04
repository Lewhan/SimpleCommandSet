package org.yingye.scs.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.yingye.scs.core.Config;

public class TeleportListener implements Listener {

  @EventHandler
  public void onTeleport(PlayerTeleportEvent teleportEvent) {
    Location location = teleportEvent.getFrom();
    Player player = teleportEvent.getPlayer();
    try {
      YamlConfiguration config = Config.getHomeConfig(player.getDisplayName());
      ConfigurationSection root = config.createSection("back");
      root.set("back", location);
      Config.saveHomeConfig(config, player);
    } catch (Exception e) {
      player.sendMessage(ChatColor.RED + "back地点记录失败");
      e.printStackTrace();
    }
  }
}
