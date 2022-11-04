package org.yingye.scs.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.yingye.scs.core.Config;
import org.yingye.scs.core.Core;
import org.yingye.scs.util.SimpleUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@SuppressWarnings("all")
public class DebugCommand implements CommandExecutor {

    private static final List<Player> HERCLUES = new ArrayList<>();
    private static final List<Player> CURRENT_LOCATION = new ArrayList<>();

    private static final Set<String> OP_COMMAND = Set.of("cllist", "flylist", "godlist", "ohklist");

    public static List<Player> getHerclues() {
        return List.copyOf(HERCLUES);
    }

    public static List<Player> getCurrentLocation() {
        return List.copyOf(CURRENT_LOCATION);
    }

    public static Set<String> getOpCommand() {
        return Set.copyOf(OP_COMMAND);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (s.equals("ohk") || s.equals("simplecommandset:ohk")) {
            oneHitKill(sender, args);
        } else if (s.equals("hb") || s.equals("simplecommandset:hb")) {
            healthBoost(sender, args);
        } else if (s.equals("cl") || s.equals("simplecommandset:cl")) {
            currentLocation(sender, args);
        } else if (s.equals("showdata") || s.equals("simplecommandset:showdata")) {
            showData(sender, args);
        }
        return true;
    }

    private void oneHitKill(CommandSender sender, String[] args) {
        if (args.length <= 0) {
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
        if (HERCLUES.contains(player)) {
            HERCLUES.remove(player);
            Core.sendWarn(player, "已为你关闭一击必杀模式");
        } else {
            HERCLUES.add(player);
            Core.sendSuccess(player, "已为你开启一击必杀模式");
        }
    }

    private void healthBoost(CommandSender sender, String[] args) {
        if (args.length <= 0) {
            Core.sendErr(sender, "请输入玩家名和血量数值");
        } else if (args.length == 1) {
            if (!(sender instanceof Player)) {
                Core.printErr("单参数模式只能由玩家使用");
                return;
            }

            if (SimpleUtil.notPositive(args[0])) {
                sender.sendMessage(ChatColor.RED + "输入的血量有误");
                return;
            }
            Double health = Double.valueOf(args[0]);
            healthBoost(sender, (Player) sender, health);
        } else if (args.length >= 2) {
            Player player = sender.getServer().getPlayerExact(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "未找到名为(" + ChatColor.AQUA + args[0] + ChatColor.RED + ")的玩家");
                return;
            }

            if (SimpleUtil.notPositive(args[1])) {
                sender.sendMessage(ChatColor.RED + "输入的血量有误");
                return;
            }
            Double health = Double.valueOf(args[1]);
            healthBoost(sender, player, health);
        }
    }

    private void healthBoost(CommandSender sender, Player player, double health) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        attribute.setBaseValue(health);
        player.setHealth(health);
        sender.sendMessage(ChatColor.GREEN + "成功将玩家(" + ChatColor.AQUA + player.getName() + ChatColor.GREEN + ")的血量上限设置为: " + health);
        Core.printWarn(SimpleUtil.getFormatDate() + ChatColor.GREEN + " --- 管理员: " + sender.getName() + ",将玩家: " + player.getName() + "的血量上限更改为: " + health);
    }

    private void currentLocation(CommandSender sender, String[] args) {
        if (args.length <= 0) {
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

    private void spawnDummy() {

    }

    private void showData(CommandSender sender, String[] args) {
        if (args.length <= 0) {
            Core.sendErr(sender, "缺少必要参数");
            return;
        }
        String mode = args[0];
        if (mode.equalsIgnoreCase("all") || mode.equalsIgnoreCase("limit")) {
            showPlayerData(sender, args);
        } else if (OP_COMMAND.contains(mode) && sender.isOp()) {
            if (mode.equalsIgnoreCase("cllist")) {
                Core.sendInfo(sender, Arrays.toString(DebugCommand.CURRENT_LOCATION.stream().map(player -> player.getName()).toArray()));
            } else if (mode.equalsIgnoreCase("flylist")) {
                Core.sendInfo(sender, Arrays.toString(FlyCommand.getFlyPlayers().stream().map(player -> player.getName()).toArray()));
            } else if (mode.equalsIgnoreCase("godlist")) {
                Core.sendInfo(sender, Arrays.toString(GodCommand.getPlayers().stream().map(player -> player.getName()).toArray()));
            } else if (mode.equalsIgnoreCase("ohklist")) {
                Core.sendInfo(sender, Arrays.toString(DebugCommand.HERCLUES.stream().map(player -> player.getName()).toArray()));
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

    private void showAnyPlayerDataWithConsole(CommandSender sender, String[] args) throws JsonProcessingException {
        String mode = args[0];
        if (args.length < 1) {
            Core.printErr("请输入控制指令");
        } else if (args.length < 2) {
            Core.printErr("请指定一个玩家");
        } else if (args.length >= 2) {
            try {
                if (mode.equalsIgnoreCase("all")) {
                    showAnyPlayerAllDataWithConsole(sender, args);
                } else if (mode.equalsIgnoreCase("limit")) {
                    showAnyPlayerlimitDataWithConsole(sender, args);
                }
            } catch (FileNotFoundException e) {
                Core.printErr("未找到数据文件，该玩家可能尚未使用过tp、back、home命令");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    private void showAnyPlayerAllDataWithConsole(CommandSender sender, String[] args) throws FileNotFoundException, JsonProcessingException {
        File file = Config.getPlayerConfigFile(args[1]);
        Core.printInfo(SimpleUtil.parseYamlToJsonString(file));
    }

    private void showAnyPlayerlimitDataWithConsole(CommandSender sender, String[] args) throws FileNotFoundException, JsonProcessingException {
        if (args.length < 3) {
            Core.printErr("请输入要查询的项");
        } else {
            JsonNode node = deepFindPath(args[1], args[2]);
            if (node.isEmpty()) {
                Core.printInfo("未找到指定项");
            } else {
                Core.printInfo(args[1] + ": " + args[2] + "\n" + node.toPrettyString());
            }
        }
    }

    private void showSelfData(Player player) throws IOException, InvalidConfigurationException {
        File file = Config.getPlayerConfigFile(player.getName());
        if (!file.exists()) {
            Core.sendErr(player, "未找到数据文件，你可能尚未使用过tp、back、home命令");
            return;
        }
        Core.sendInfo(player, SimpleUtil.parseYamlToJsonString(file));
    }

    private void showAnyPlayerData(Player player, String[] args) throws IOException, InvalidConfigurationException {
        String mode = args[0];
        if(mode.equalsIgnoreCase("all")) {
            showSelfData(player);
        } else {
            if (args.length == 1) {
                Core.sendErr(player, "请输入要查询的项");
            } else {
                try {
                    if (args.length == 2) {
                        showAnyPlayerData(player, args[1]);
                    } else if (args.length >= 3) {
                        showAnyPlayerData(player, args[1], args[2]);
                    }
                } catch (FileNotFoundException e) {
                    if(args.length >= 3 && player.isOp()) {
                        Core.sendErr(player, "未找到数据文件，该玩家可能尚未使用过tp、back、home命令");
                    } else {
                        Core.sendErr(player, "未找到数据文件，你可能尚未使用过tp、back、home命令");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }
    }

    private void showAnyPlayerData(Player player, String path) throws IOException, InvalidConfigurationException {
        JsonNode node = deepFindPath(player.getName(), path);
        if (node.isEmpty()) {
            Core.sendInfo(player, "没有找到指定项");
        } else {
            Core.sendInfo(player, path + "\n" + node.toPrettyString());
        }
    }

    private void showAnyPlayerData(Player player, String path, String name) throws IOException, InvalidConfigurationException {
        if (!player.isOp()) {
            showAnyPlayerData(player, path);
        } else {
            JsonNode node = deepFindPath(name, path);
            if (node.isEmpty()) {
                Core.sendInfo(player, "没有找到指定项");
            } else {
                Core.sendInfo(player, name + ": " + path + "\n" + node.toPrettyString());
            }
        }
    }

    private JsonNode deepFindPath(String name, String path) throws FileNotFoundException, JsonProcessingException {
        JsonNode root = SimpleUtil.parseYamlToJson(Config.getPlayerConfigFile(name));
        if (path.indexOf(".") != -1) {
            String[] strs = path.split("\\.");
            JsonNode t = null;
            for (String str : strs) {
                if (t == null) {
                    t = root.findPath(str);
                } else {
                    t = t.findPath(str);
                }
            }
            return t;
        } else {
            return root.findPath(path);
        }
    }

}
