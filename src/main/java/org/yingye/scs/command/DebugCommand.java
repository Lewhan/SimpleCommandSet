package org.yingye.scs.command;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yingye.scs.core.Config;
import org.yingye.scs.core.Core;
import org.yingye.scs.util.Auxiliary;
import org.yingye.scs.util.CommonTool;
import org.yingye.scs.util.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class DebugCommand implements CommandExecutor {

    private static final List<Player> HERACLES = new ArrayList<>();
    private static final List<Player> CURRENT_LOCATION = new ArrayList<>();

    private static final Set<String> OP_COMMAND = Set.of("cllist", "flylist", "godlist", "ohklist");

    public static List<Player> getHeracles() {
        return List.copyOf(HERACLES);
    }

    public static List<Player> getCurrentLocation() {
        return List.copyOf(CURRENT_LOCATION);
    }

    public static Set<String> getOpCommand() {
        return Set.copyOf(OP_COMMAND);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        switch (s) {
            case "ohk", "simplecommandset:ohk" -> oneHitKill(sender, args);
            case "hb", "simplecommandset:hb" -> healthBoost(sender, args);
            case "cl", "simplecommandset:cl" -> currentLocation(sender, args);
            case "showdata", "simplecommandset:showdata" -> showData(sender, args);
        }
        return true;
    }

    private void oneHitKill(CommandSender sender, String[] args) {
        if (args.length == 0) {
            oneHitKill(sender);
        } else {
            oneHitKill(sender, args[0]);
        }
    }

    private void oneHitKill(CommandSender sender) {
        if (sender instanceof Player player) {
            oneHitKill(player);
        } else {
            Core.printErr("无参数模式只能由玩家使用");
        }
    }

    private void oneHitKill(CommandSender sender, String name) {
        Player player = sender.getServer().getPlayerExact(name);
        if (player != null) {
            oneHitKill(player);
        } else {
            Core.printErr("未找到该玩家");
        }
    }

    private void oneHitKill(Player player) {
        if (HERACLES.contains(player)) {
            HERACLES.remove(player);
            Core.sendWarn(player, "已为你关闭一击必杀模式");
        } else {
            HERACLES.add(player);
            Core.sendSuccess(player, "已为你开启一击必杀模式");
        }
    }

    private void healthBoost(CommandSender sender, String[] args) {
        if (args.length == 0) {
            Core.sendErr(sender, "请输入玩家名和血量数值");
        } else if (args.length == 1) {
            if (!(sender instanceof Player)) {
                Core.printErr("单参数模式只能由玩家使用");
                return;
            }

            if (Auxiliary.notPositive(args[0])) {
                sender.sendMessage(ChatColor.RED + "输入的血量有误");
                return;
            }
            double health = Double.parseDouble(args[0]);
            healthBoost(sender, (Player) sender, health);
        } else {
            Player player = sender.getServer().getPlayerExact(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "未找到名为(" + ChatColor.AQUA + args[0] + ChatColor.RED + ")的玩家");
                return;
            }

            if (Auxiliary.notPositive(args[1])) {
                sender.sendMessage(ChatColor.RED + "输入的血量有误");
                return;
            }
            double health = Double.parseDouble(args[1]);
            healthBoost(sender, player, health);
        }
    }

    private void healthBoost(CommandSender sender, Player player, double health) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if(attribute == null) {
            Core.sendErr(sender, "生命值属性获取失败");
        } else {
            attribute.setBaseValue(health);
            player.setHealth(health);
            sender.sendMessage(ChatColor.GREEN + "成功将玩家(" + ChatColor.AQUA + player.getName() + ChatColor.GREEN + ")的血量上限设置为: " + health);
            Core.printWarn(Auxiliary.getFormatDate() + ChatColor.GREEN + " --- 管理员: " + sender.getName() + ",将玩家: " + player.getName() + "的血量上限更改为: " + health);
        }
    }

    private void currentLocation(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                currentLocation(player);
            } else {
                Core.printErr("无参数模式只能由玩家使用");
            }
        } else {
            Player player = sender.getServer().getPlayerExact(args[0]);
            if (player == null) {
                Core.sendErr(sender, "未找到该玩家");
            } else {
                currentLocation(player);
            }
        }
    }

    private void currentLocation(Player player) {
        if (CURRENT_LOCATION.contains(player)) {
            CURRENT_LOCATION.remove(player);
            Core.sendWarn(player, "已为你关闭坐标提示");
        } else {
            CURRENT_LOCATION.add(player);
            Core.sendSuccess(player, "已为你开启坐标提示");
        }
    }

    private void showData(CommandSender sender, String[] args) {
        if (args.length == 0) {
            Core.sendErr(sender, "请输入控制指令");
            return;
        }
        String mode = args[0];
        if (mode.equalsIgnoreCase("all") || mode.equalsIgnoreCase("limit") || mode.equalsIgnoreCase("homekeys")) {
            showPlayerData(sender, args);
        } else if (OP_COMMAND.contains(mode) && sender.isOp()) {
            if (mode.equalsIgnoreCase("cllist")) {
                Core.sendInfo(sender, Arrays.toString(DebugCommand.CURRENT_LOCATION.stream().map(Player::getName).toArray()));
            } else if (mode.equalsIgnoreCase("flylist")) {
                Core.sendInfo(sender, Arrays.toString(FlyCommand.getFlyPlayers().stream().map(Player::getName).toArray()));
            } else if (mode.equalsIgnoreCase("godlist")) {
                Core.sendInfo(sender, Arrays.toString(GodCommand.getGodPlayers().stream().map(Player::getName).toArray()));
            } else if (mode.equalsIgnoreCase("ohklist")) {
                Core.sendInfo(sender, Arrays.toString(DebugCommand.HERACLES.stream().map(Player::getName).toArray()));
            }
        } else {
            Core.sendErr(sender, "未知指令");
        }
    }

    private void showPlayerData(CommandSender sender, String[] args) {
        try {
            if (sender instanceof Player player) {
                showAnyPlayerData(player, args);
            } else {
                showAnyPlayerDataWithConsole(sender, args);
            }
        } catch (Exception e) {
            Core.sendErr(sender, "执行出现错误");
            e.printStackTrace();
        }
    }

    private void showAnyPlayerDataWithConsole(CommandSender sender, String[] args) {
        if (args.length == 1) {
            Core.printErr("请指定一个玩家");
        } else {
            try {
                String mode = args[0];
                if (mode.equalsIgnoreCase("all")) {
                    showAnyPlayerAllDataWithConsole(args);
                } else if (mode.equalsIgnoreCase("homekeys")) {
                    showAnyPlayerHomeKeysWithConsole(sender, args);
                } else if (mode.equalsIgnoreCase("limit")) {
                    showAnyPlayerLimitDataWithConsole(args);
                }
            } catch (FileNotFoundException e) {
                Core.printErr("未找到数据文件，该玩家可能尚未使用过tp、back、home命令");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    private void showAnyPlayerAllDataWithConsole(String[] args) throws FileNotFoundException {
        File file = Config.getPlayerConfigFile(args[1]);
        Core.printInfo(CommonTool.mapToJsonString(Parser.parseYamlToMap(file), null, 1));
    }

    private void showAnyPlayerLimitDataWithConsole(String[] args) throws FileNotFoundException {
        if (args.length < 3) {
            Core.printErr("请输入要查询的项");
        } else {
            Map<?, ?> map = deepFindPath(args[1], args[2]);
            if (map == null) {
                Core.printInfo("未找到指定项");
            } else {
                Core.printInfo(args[1] + ": " + args[2] + "\n" + CommonTool.mapToJsonString(map, null, 1));
            }
        }
    }

    private void showAnyPlayerHomeKeysWithConsole(CommandSender sender, String[] args) throws FileNotFoundException {
        File file = Config.getPlayerConfigFile(args[1]);
        HashMap<?, ?> map = Parser.parseYamlToMap(file);
        Map<?, ?> home = (Map<?, ?>) map.get("home");
        Core.sendInfo(sender, args[1] + ": homekeys\n" + home.keySet());
    }

    private void showSelfData(Player player) throws IOException {
        File file = Config.getPlayerConfigFile(player.getName());
        if (!file.exists()) {
            Core.sendErr(player, "未找到数据文件，你可能尚未使用过tp、back、home命令");
            return;
        }
        Core.sendInfo(player, CommonTool.mapToJsonString(Parser.parseYamlToMap(file), null, 1));
    }

    private void showAnyPlayerData(Player player, String[] args) throws IOException {
        String mode = args[0];
        if (args.length == 1) {
            if (mode.equalsIgnoreCase("all")) {
                showSelfData(player);
            } else if (mode.equalsIgnoreCase("homekeys")) {
                showSelfHomeKeys(player);
            } else {
                Core.sendErr(player, "请输入要查询的项");
            }
            return;
        }

        try {
            if (mode.equalsIgnoreCase("all")) {
                showAnyPlayerAllData(player, player.isOp() ? args[1] : player.getName());
            } else if (mode.equalsIgnoreCase("homekeys")) {
                showAnyPlayerHomeKeys(player, player.isOp() ? args[1] : player.getName());
            } else {
                if (args.length == 2) {
                    showAnyPlayerLimitData(player, args[1]);
                } else {
                    showAnyPlayerLimitData(player, args[1], args[2]);
                }
            }
        } catch (FileNotFoundException e) {
            if (args.length >= 3 && player.isOp()) {
                Core.sendErr(player, "未找到数据文件，该玩家可能尚未使用过tp、back、home命令");
            } else {
                Core.sendErr(player, "未找到数据文件，你可能尚未使用过tp、back、home命令");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void showAnyPlayerAllData(Player player, String name) throws FileNotFoundException {
        File file = Config.getPlayerConfigFile(name);
        Core.sendInfo(player, name + ": \n" + CommonTool.mapToJsonString(Parser.parseYamlToMap(file), null, 1));
    }

    private void showAnyPlayerLimitData(Player player, String path) throws IOException {
        Map<?, ?> map = deepFindPath(player.getName(), path);
        if (map == null) {
            Core.sendInfo(player, "没有找到指定项");
        } else {
            Core.sendInfo(player, path + "\n" + CommonTool.mapToJsonString(map, null, 1));
        }
    }

    private void showAnyPlayerLimitData(Player player, String path, String name) throws IOException {
        if (!player.isOp()) {
            showAnyPlayerLimitData(player, path);
        } else {
            Map<?, ?> map = deepFindPath(name, path);
            if (map == null) {
                Core.sendInfo(player, "没有找到指定项");
            } else {
                Core.sendInfo(player, name + ": " + path + "\n" + CommonTool.mapToJsonString(map, null, 1));
            }
        }
    }

    private void showSelfHomeKeys(Player player) throws FileNotFoundException {
        File file = Config.getPlayerConfigFile(player.getName());
        if (!file.exists()) {
            Core.sendErr(player, "未找到数据文件，你可能尚未使用过tp、back、home命令");
            return;
        }
        HashMap<?, ?> map = Parser.parseYamlToMap(file);
        Map<?, ?> home = (Map<?, ?>) map.get("home");
        if (home == null) {
            Core.sendErr(player, "没有home节点，你可能尚未使用过sethome命令");
        } else {
            Core.sendInfo(player, home.keySet().toString());
        }
    }

    private void showAnyPlayerHomeKeys(Player player, String name) throws FileNotFoundException {
        File file = Config.getPlayerConfigFile(name);
        HashMap<?, ?> map = Parser.parseYamlToMap(file);
        Map<?, ?> home = (Map<?, ?>) map.get("home");
        if (home == null) {
            Core.sendErr(player, "没有home节点，该玩家可能尚未使用过sethome命令");
        } else {
            Core.sendInfo(player, name + ": homekeys\n" + home.keySet());
        }
    }

    private Map<?, ?> deepFindPath(String name, String path) throws FileNotFoundException {
        HashMap<?, ?> root = Parser.parseYamlToMap(Config.getPlayerConfigFile(name));
        if (path.contains(".")) {
            String[] strs = path.split("\\.");
            Map<?, ?> t = null;
            for (String str : strs) {
                if (t == null) {
                    t = (Map<?, ?>) root.get(str);
                } else {
                    t = (Map<?, ?>) t.get(str);
                }
            }
            return t;
        } else {
            return (Map<?, ?>) root.get(path);
        }
    }

}
