package org.yingye.scs.listener;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.yingye.scs.core.Config;

public class DeadListener implements Listener {

    @EventHandler
    public void onDead(PlayerDeathEvent deathEvent) {
        // 获取触发死亡监听的玩家信息
        Player player = deathEvent.getEntity();
//    Location location = player.getLocation();
//    player.sendMessage(ChatColor.RED + "死亡地点" + ChatColor.WHITE + "[x: " + location.getBlockX() + " , y: " + location.getBlockY() + " , z: " + location.getBlockZ() + "]");
        try {
            YamlConfiguration config = Config.getHomeConfig(player);
            ConfigurationSection root = config.getConfigurationSection("back");
            assert root != null;
            root.set("back", player.getLocation());
            Config.saveHomeConfig(config, player);
            player.sendMessage(ChatColor.GREEN + "输入 " + ChatColor.RED + "/back " + ChatColor.GREEN + "返回死亡地点");
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "back地点创建失败");
            e.printStackTrace();
        }
    }
}
