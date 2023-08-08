package org.banger.firstmcclans;

import org.banger.commands.ClanChatCommand;
import org.banger.commands.ClanCommand;
import org.banger.listener.InventoryListeners;
import org.banger.listener.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class FirstMCClansPlugin extends JavaPlugin {

    private static FirstMCClansPlugin instance;
    private File file1;
    private YamlConfiguration cfgUtility;
    private File file2;
    private YamlConfiguration clanInvites;
    private File file3;
    private YamlConfiguration ClanUtility;

    private static String prefix = "§e§lClans §8» ";

    PluginManager pm = Bukkit.getPluginManager();

    @Override
    public void onEnable() {
        instance = this;

        createFiles();

        registerListener();
        registerCommands();

        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§aDas Clan-System ist erfolgreich hochgefahren!");

    }

    @Override
    public void onDisable() {

    }

    private void registerListener() {
        pm.registerEvents(new InventoryListeners(), this);
        pm.registerEvents(new JoinListener(), this);
    }

    private void registerCommands() {
        this.getCommand("clan").setExecutor(new ClanCommand());
        this.getCommand("clanchat").setExecutor(new ClanChatCommand());

    }

    public void createFiles() {
        if (file1 == null) {
            this.getDataFolder().getAbsoluteFile().mkdirs();
            file1 = new File("plugins//FirstMCClansPlugin//ClanInfo.yml");
            try {
                file1.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            cfgUtility = YamlConfiguration.loadConfiguration(file1);
        }
        if (file2 == null) {
            file2 = new File("plugins//FirstMCClansPlugin//ClanInvites.yml");
            try {
                file2.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            clanInvites = YamlConfiguration.loadConfiguration(file2);
        }
        if (file3 == null) {
            file3 = new File("plugins//FirstMCClansPlugin//ClanMembers.yml");
            try {
                file3.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ClanUtility = YamlConfiguration.loadConfiguration(file3);
        }
        try {
            cfgUtility.load(file1);
            clanInvites.load(file2);
            ClanUtility.load(file3);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static FirstMCClansPlugin getInstance() {
        return instance;
    }

    public YamlConfiguration getCfgUtility() {
        return cfgUtility;
    }

    public YamlConfiguration getCfgUtility2() {
        return clanInvites;
    }

    public YamlConfiguration getClanUtility() {
        return ClanUtility;
    }

    public File getFile1() {
        return file1;
    }

    public File getFile2() {
        return file2;
    }

    public File getFile3() {
        return file3;
    }

    public String getPrefix() {
        return prefix;
    }
}
