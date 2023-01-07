package org.yingye.scs.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.yingye.scs.core.Core;
import org.yingye.scs.immutable.Color;
import org.yingye.scs.util.Auxiliary;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WorldCommand implements CommandExecutor {

    private static final HashMap<String, World.Environment> WORLD_TYPE = new HashMap<>(Map.of("normal", World.Environment.NORMAL, "nether", World.Environment.NETHER, "end", World.Environment.THE_END));
    private static final HashMap<String, String> WORLD_TYPE_NAME = new HashMap<>(Map.of("normal", "正常", "nether", "下界", "end", "末地"));

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        label = label.toLowerCase();
        if (label.equalsIgnoreCase("world") || label.equalsIgnoreCase("simplecommandset:world")) {
            if (args.length > 0) {
                world(sender, args);
            } else {
                sender.sendMessage(ChatColor.RED + "参数不足");
            }
        }
        return true;
    }

    private void world(CommandSender sender, String[] args) {
        String label = args[0].toLowerCase();
        switch (label) {
            case "create" -> createWorld(sender, args);
            case "delete" -> deleteWorld(sender, args);
            case "tp" -> {
                if (sender instanceof Player player) {
                    tpWorld(player, args);
                } else {
                    sender.sendMessage("该命令只能由玩家使用");
                }
            }
        }
    }

    private void createWorld(CommandSender sender, String[] args) {
        if (args.length == 0) {
            Core.sendErr(sender, "请输入控制指令");
        } else if (args.length == 1) {
            Core.sendErr(sender, "请输入要新建的世界名");
        } else if (args.length == 2) {
            createWorld(sender, args[1]);
        } else {
            createWorld(sender, args[1], args[2]);
        }
    }

    private void createWorld(CommandSender sender, String worldName) {
        String type = "normal";
        createWorld(sender, worldName, type);
    }

    /**
     * 创建一个世界
     *
     * @param sender    操作者
     * @param worldName 世界名
     * @param type      世界类型
     */
    private void createWorld(CommandSender sender, String worldName, String type) {
        WorldCreator creator = new WorldCreator(worldName);
        World.Environment environment = WORLD_TYPE.get(type);
        creator.environment(Objects.requireNonNullElse(environment, World.Environment.NORMAL));
        sender.getServer().createWorld(creator);
        TextComponent msg = Component.empty()
                .append(Component.text("成功创建世界: ", Color.LIGHT_GREEN))
                .append(Component.text(worldName, Color.AQUA))
                .append(Component.text(",世界类型: ", Color.LIGHT_GREEN))
                .append(Component.text(type, Color.AQUA));
        Core.sendTextComponent(sender, msg);
        if (sender instanceof Player) {
            Core.printWarn(Auxiliary.getFormatDate() + " --- 管理员: " + sender.getName() + ",创建了世界: " + worldName + ",世界类型为: " + WORLD_TYPE_NAME.get(type));
        }
    }

    private void deleteWorld(CommandSender sender, String[] args) {
        String des = "true";
        if (args.length == 0) {
            Core.sendErr(sender, "请输入控制指令");
        } else if (args.length == 1) {
            Core.sendErr(sender, "请输入目标世界名");
        } else {
            if (args.length > 2) {
                des = args[2];
            }
            String why = des;
            new BukkitRunnable() {
                @Override
                public void run() {
                    deleteWorld(sender, args[1], why);
                }
            }.runTaskLater(Core.getPlugin(), 20);
        }
    }

    /**
     * @param sender    命令触发者
     * @param worldName 要删除的世界名
     * @param destroy   是否真的删除，如果为false，则会在下次创建同名世界的时候直接挂载上
     */
    private void deleteWorld(CommandSender sender, String worldName, String destroy) {
        World world = sender.getServer().getWorld(worldName);
        if (world == null) {
            sender.sendMessage(ChatColor.RED + "没有找到该世界");
            return;
        }
        sender.getServer().unloadWorld(world, true);
        if (destroy.equals("true")) {
            File file = world.getWorldFolder();
            removeDir(sender, file);
        }
        sender.sendMessage(ChatColor.GREEN + "删除成功");
        if (sender instanceof Player) {
            Core.printWarn(Auxiliary.getFormatDate() + " --- 管理员: " + sender.getName() + ",删除了世界: " + worldName + ",世界类型为: " + new WorldCreator(worldName).environment().name().toLowerCase());
        }
    }

    /**
     * 删除世界对应的文件夹
     *
     * @param file 世界对应的资源文件夹
     */
    private void removeDir(CommandSender sender, File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                Core.sendErr(sender, "无法获取 " + file.getAbsolutePath() + " 文件夹下的文件");
            } else {
                if (files.length == 0) {
                    if (!file.delete()) {
                        Core.sendErr(sender, "文件夹 " + file.getAbsolutePath() + " 删除失败");
                    }
                } else {
                    for (File f : files) {
                        removeDir(sender, f);
                    }
                }
            }
        } else {
            if (!file.delete()) {
                Core.sendErr(sender, "文件 " + file.getAbsolutePath() + " 删除失败");
            }
        }
    }

    private void tpWorld(Player player, String[] args) {
        if (args.length == 1) {
            Core.sendErr(player, "请输入要前往的世界名");
        } else if (args.length == 2) {
            tpWorld(player, args[1]);
        } else if (args.length >= 5) {
            tpWorldWithAnyLocation(player, Arrays.copyOfRange(args, 1, 5));
        } else {
            Core.sendErr(player, "请输入完整的xyz坐标");
        }
    }

    /**
     * 世界传送
     *
     * @param player    触发命令的玩家
     * @param worldName 要传送到哪个世界
     */
    private void tpWorld(Player player, String worldName) {
        World world = player.getServer().getWorld(worldName);
        if (world == null) {
            player.sendMessage(ChatColor.RED + "没有找到这个世界");
        } else {
            player.teleport(world.getSpawnLocation());
        }
    }

    private void tpWorldWithAnyLocation(Player player, String[] args) {
        World world = Core.SERVER.getWorld(args[0]);
        if (world == null) {
            player.sendMessage(ChatColor.RED + "没有找到这个世界");
        } else {
            try {
                double x = Double.parseDouble(args[1]), y = Double.parseDouble(args[2]), z = Double.parseDouble(args[3]);
                Location location = player.getLocation();
                location.setWorld(world);
                location.set(x, y, z);
                player.teleport(location);
            } catch (Exception e) {
                String message = e.getMessage();
                Core.sendErr(player, "发现非合规数字: " + message.substring(message.indexOf("\"") + 1, message.length() - 1) + ", 坐标数值必须是数字");
            }
        }
    }

}