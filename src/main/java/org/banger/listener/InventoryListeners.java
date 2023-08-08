package org.banger.listener;

import net.luckperms.api.LuckPerms;
import org.banger.firstmcclans.FirstMCClansPlugin;
import org.banger.manage.ClanAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryListeners implements Listener {

    private static FirstMCClansPlugin firstMCClansPlugin = FirstMCClansPlugin.getInstance();

    private static YamlConfiguration InfoCFG = firstMCClansPlugin.getCfgUtility();
    private static YamlConfiguration InvitesCFG = firstMCClansPlugin.getCfgUtility2();
    private static YamlConfiguration MembersCFG = firstMCClansPlugin.getClanUtility();

    private static RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
    private static LuckPerms api = provider.getProvider();


    @EventHandler
    public void onInventoryPick(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();
       if(ClanAPI.isInClan((Player)e.getWhoClicked())) {
           if (e.getView().getTitle().equalsIgnoreCase(firstMCClansPlugin.getPrefix() + "§7Info §a" + ClanAPI.checkClan((Player) e.getWhoClicked()) + " §7Clan") || e.getView().getTitle().equalsIgnoreCase("§e" + ClanAPI.checkClan(player) + " §8| §7Memberliste")) {
               e.setCancelled(true);
               if(e.getCurrentItem().getType() == Material.PAPER && e.getCurrentItem() != null){
                   e.setCancelled(true);
                   e.getWhoClicked().closeInventory();
                   player.getInventory().setItemInOffHand(null);
                   ConfigurationSection section = MembersCFG.getConfigurationSection("");
                   player.sendMessage(firstMCClansPlugin.getPrefix() + "§4Leader §8| §7" + ClanAPI.getLeader(ClanAPI.checkClan(player)));
                   for(String uuidStr : section.getKeys(true)){
                       UUID uuid = UUID.fromString(uuidStr);
                       if(Bukkit.getPlayer(uuid) != null){
                           if(ClanAPI.checkClan(Bukkit.getPlayer(uuid)).equals(ClanAPI.checkClan(player))) {
                               if(!ClanAPI.getLeader(ClanAPI.checkClan(player)).equals(Bukkit.getPlayer(uuid).getName())) {
                                   if (Bukkit.getPlayer(uuid).hasPermission("clan.mod")) {
                                       player.sendMessage(firstMCClansPlugin.getPrefix() + "§cClan-Mod §8| §7" + Bukkit.getPlayer(uuid).getName());
                                   }
                               }
                           }
                       }else{
                           if(ClanAPI.checkClanOffline(Bukkit.getOfflinePlayer(uuid)).equals(ClanAPI.checkClan(player))) {
                               if(!ClanAPI.getLeader(ClanAPI.checkClan(player)).equals(Bukkit.getOfflinePlayer(uuid).getName())) {
                                   player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Member §8| §7" + Bukkit.getOfflinePlayer(uuid).getName());
                               }
                           }
                       }
                   }
                   for(String uuidStr : section.getKeys(true)){
                       UUID uuid = UUID.fromString(uuidStr);
                       if(Bukkit.getPlayer(uuid) != null){
                           if(ClanAPI.checkClan(Bukkit.getPlayer(uuid)).equals(ClanAPI.checkClan(player))) {
                               if (!ClanAPI.getLeader(ClanAPI.checkClan(player)).equals(Bukkit.getPlayer(uuid).getName())) {
                                   if(!Bukkit.getPlayer(uuid).hasPermission("clan.mod")) {
                                       player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Member §8| §7" + Bukkit.getPlayer(uuid).getName());
                                   }
                               }
                           }
                       }
                   }



                   }
               }
           }
       }


    @EventHandler
    public void onInventoryInteract(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if(ClanAPI.isInClan((Player) e.getPlayer())){
            if(e.getView().getTitle().equalsIgnoreCase(firstMCClansPlugin.getPrefix() + "§7Inventory - Level 1") ||
                    e.getView().getTitle().equalsIgnoreCase(firstMCClansPlugin.getPrefix() + "§7Inventory - Level 2") ||
                    e.getView().getTitle().equalsIgnoreCase(firstMCClansPlugin.getPrefix() + "§7Inventory - Level 3")){
                Inventory inv = e.getInventory();


                for(int i = 0; i < inv.getSize(); i++){
                    if(inv.getItem(i) != null){
                        InfoCFG.set(ClanAPI.checkClan(p) + "." + i, inv.getItem(i));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryOpenEvent e){
        if(e.getView().getTitle().equalsIgnoreCase(firstMCClansPlugin.getPrefix() + "§7Inventory - Level 1") ||
                e.getView().getTitle().equalsIgnoreCase(firstMCClansPlugin.getPrefix() + "§7Inventory - Level 2") ||
                e.getView().getTitle().equalsIgnoreCase(firstMCClansPlugin.getPrefix() + "§7Inventory - Level 3")){
            InfoCFG.set(ClanAPI.checkClan((Player) e.getPlayer()) + ".isOpenInv", true);
            try {
                InfoCFG.save(firstMCClansPlugin.getFile1());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if(e.getView().getTitle().equalsIgnoreCase(firstMCClansPlugin.getPrefix() + "§7Inventory - Level 1") ||
                e.getView().getTitle().equalsIgnoreCase(firstMCClansPlugin.getPrefix() + "§7Inventory - Level 2") ||
                e.getView().getTitle().equalsIgnoreCase(firstMCClansPlugin.getPrefix() + "§7Inventory - Level 3")){
            InfoCFG.set(ClanAPI.checkClan((Player) e.getPlayer()) + ".isOpenInv", false);
            try {
                InfoCFG.save(firstMCClansPlugin.getFile1());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
