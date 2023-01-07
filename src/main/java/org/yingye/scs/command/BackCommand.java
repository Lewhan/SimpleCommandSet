package org.yingye.scs.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yingye.scs.core.Config;
import org.yingye.scs.util.BukkitTool;

public class BackCommand implements CommandExecutor {

    public BackCommand() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // 判断是不是从控制台输入的
        if (sender instanceof Player player) {
            YamlConfiguration config = Config.getHomeConfig(player);
            if (config == null) {
                player.sendMessage(ChatColor.RED + "暂无返回点");
            } else {
                ConfigurationSection root = config.getConfigurationSection("back");
                if (root == null) {
                    player.sendMessage(ChatColor.RED + "暂无返回点");
                } else {
                    ConfigurationSection back = root.getConfigurationSection("back");
                    if (back != null) {
                        Location location = BukkitTool.createLocation(sender.getServer(), back);
                        if (location != null) {
                            player.teleport(location);
                        } else {
                            player.sendMessage(ChatColor.RED + "返回点获取失败");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "暂无返回点");
                    }
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "该命令只能由玩家使用");
        }
        return true;
    }

}
