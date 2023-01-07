package org.yingye.scs.listener;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.yingye.scs.core.Config;
import org.yingye.scs.core.Core;

public class DeadListener implements Listener {

    @EventHandler
    public void onDead(PlayerDeathEvent deathEvent) {
        // 获取触发死亡监听的玩家信息
        Player player = deathEvent.getEntity();
        try {
            YamlConfiguration config = Config.getHomeConfig(player);
            ConfigurationSection root = config.getConfigurationSection("back");
            assert root != null;
            root.set("back", player.getLocation());
            Config.saveHomeConfig(config, player);
            Core.sendInfo(player, ChatColor.GREEN + "输入 " + ChatColor.RED + "/back " + ChatColor.GREEN + "返回死亡地点");
        } catch (Exception e) {
            Core.sendErr(player, "back地点创建失败");
            e.printStackTrace();
        }
    }
}
