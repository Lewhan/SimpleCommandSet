package org.yingye.scs.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yingye.scs.core.Core;

public class OtherCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (label.equals("remake") || label.equals("simplecommandset:remake")) {
            remake(sender);
        }
        return true;
    }

    private void remake(@NotNull CommandSender sender) {
        if (sender instanceof Player player) {
            player.sendMessage(ChatColor.GREEN + "如你所愿");
            player.setHealth(0.0);
        } else {
            Core.printErr("该命令只能由玩家执行");
        }
    }
}
