package net.krinsoft.texturesuite;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author krinsdeath
 */
public class TextureCore extends JavaPlugin {
    private final Map<String, String> packs = new HashMap<String, String>();
    private boolean debug = false;

    public void onEnable() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        try {
            debug = getConfig().getBoolean("debug");
            for (String key : getConfig().getConfigurationSection("packs").getKeys(false)) {
                String url = getConfig().getString("packs." + key);
                if (url.equalsIgnoreCase("insert URL here!")) {
                    throw new RuntimeException("Check config.yml to customize the texture pack list.");
                }
                packs.put(key, url);
            }
            debug(packs.size() + " pack" + (packs.size() > 1 ? "s": "") + " loaded.");
        } catch (NullPointerException e) {
            warn("No packs are defined in the config.yml!");
        } catch (RuntimeException e) {
            getLogger().info(e.getLocalizedMessage());
        }
    }

    public void onDisable() {
        packs.clear();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1 || args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {
            return false;
        }
        List<String> arguments = new ArrayList<String>(Arrays.asList(args));
        String subCmd = arguments.remove(0);
        if (hasPermission(sender, subCmd)) {
            if (subCmd.equalsIgnoreCase("list")) {
                StringBuilder message = new StringBuilder();
                message.append(String.format("To get a pack, use " + ChatColor.GREEN + "/%s get [pack]\n" + ChatColor.RESET, label));
                message.append("Available texture packs: \n");
                Set<String> keys = packs.keySet();
                if (keys.size() == 0) {
                    message.append("None.");
                } else {
                    for (String pack : keys) {
                        message.append(pack).append("\n");
                    }
                }
                sender.sendMessage(message.toString());
            } else if (subCmd.equalsIgnoreCase("get")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "To get a texture pack, you must be a player!");
                    return true;
                }
                if (arguments.size() == 0) {
                    sender.sendMessage(ChatColor.RED + "You must supply a pack name!");
                    return true;
                }
                Player player = (Player) sender;
                try {
                    player.setTexturePack(packs.get(arguments.get(0)));
                    player.sendMessage("You have selected the " + ChatColor.GREEN + arguments.get(0) + ChatColor.RESET + " pack.");
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "The supplied pack name was invalid.");
                    warn(e.getLocalizedMessage());
                }
            } else if (subCmd.equalsIgnoreCase("add")) {
                if (arguments.size() < 2) {
                    sender.sendMessage(ChatColor.RED + "Must supply a pack name and a URL!");
                    return true;
                }
                if (addPack(arguments.get(0), arguments.get(1))) {
                    sender.sendMessage(ChatColor.GREEN + String.format("Texture pack '%s' added successfully.", ChatColor.AQUA + arguments.get(0) + ChatColor.GREEN));
                } else {
                    sender.sendMessage(ChatColor.RED + "Failed to add texture pack.");
                }
            } else if (subCmd.equalsIgnoreCase("remove")) {
                if (arguments.size() < 1) {
                    sender.sendMessage(ChatColor.RED + "Must supply a pack name and a URL!");
                    return true;
                }
                if (removePack(arguments.get(0))) {
                    sender.sendMessage(ChatColor.GREEN + String.format("Texture pack '%s' removed successfully.", ChatColor.AQUA + arguments.get(0) + ChatColor.GREEN));
                } else {
                    sender.sendMessage(ChatColor.RED + "Failed to remove texture pack.");
                }
            } else if (subCmd.equalsIgnoreCase("reload")) {
                reloadConfig();
                sender.sendMessage("TextureSuite's config file has been reloaded.");
            }
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
            return true;
        }
    }

    public boolean hasPermission(CommandSender sender, String subCmd) {
        return sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender || sender.hasPermission("texturesuite." + subCmd);
    }

    public void debug(String message) {
        if (debug) {
            getLogger().info("[Debug] " + message);
        }
    }

    public void warn(String message) {
        getLogger().warning(message);
    }

    /**
     * Add a texture pack to the pack list
     * @param pack The name of the pack
     * @param url The url where the pack is hosted
     * @return true if the pack was added successfully, otherwise false
     */
    public boolean addPack(String pack, String url) {
        String exists = getConfig().getString("packs." + pack);
        if (exists != null) {
            return false;
        }
        getConfig().set("packs." + pack, url);
        packs.put(pack, url);
        saveConfig();
        return true;
    }

    /**
     * Remove a texture pack from the pack list.
     * @param pack The name of the pack
     * @return true if the pack removal succeeded, otherwise false
     */
    public boolean removePack(String pack) {
        String exists = getConfig().getString("packs." + pack);
        if (exists == null) {
            return false;
        }
        getConfig().set("packs." + pack, null);
        packs.remove(pack);
        saveConfig();
        return true;
    }

}
