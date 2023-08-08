package org.banger.manage;

import org.banger.firstmcclans.FirstMCClansPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;

public class ClanAPI {

    private static FirstMCClansPlugin firstMCClansPlugin = FirstMCClansPlugin.getInstance();

    private static YamlConfiguration cfgUtility = firstMCClansPlugin.getCfgUtility();
    private static YamlConfiguration cfgUtility2 = firstMCClansPlugin.getCfgUtility2();
    private static YamlConfiguration cfgUtility3 = firstMCClansPlugin.getClanUtility();

    String name;
    String leader;
    String members;
    Double money;
    String Level;
    Location home1;
    Location home2;
    Location home3;
    boolean showTag;

    public ClanAPI(String name, String leader, String members, Double money, String Level, Location home1, Location home2, Location home3, boolean showTag) {
        this.name = name;
        this.leader = leader;
        this.members = members;
        this.money = money;
        this.Level = Level;
        this.home1 = home1;
        this.home2 = home2;
        this.home3 = home3;
        this.showTag = showTag;

        cfgUtility.set(name, "");
        cfgUtility.set(name + ".leader", leader);
        cfgUtility.set(name + ".members", members);
        cfgUtility.set(name + ".money", money);
        cfgUtility.set(name + ".level", Level);
        cfgUtility.set(name + ".home1", home1);
        cfgUtility.set(name + ".home2", home2);
        cfgUtility.set(name + ".home3", home3);
        cfgUtility.set(name + ".showTag", showTag);
        try {
            cfgUtility.save(firstMCClansPlugin.getFile1());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean clanExists(String name) {
        if (cfgUtility.contains(name)) {
            return true;
        }
        return false;
    }

    public static void deleteClan(String name) {
        if (clanExists(name)) {
            cfgUtility.set(name, null);
            cfgUtility.set(name + ".leader", null);
            cfgUtility.set(name + ".members", null);
            cfgUtility.set(name + ".money", null);
            cfgUtility.set(name + ".level", null);
            cfgUtility.set(name + ".home1", null);
            cfgUtility.set(name + ".home2", null);
            cfgUtility.set(name + ".home3", null);
            cfgUtility.set(name + ".showTag", null);
            try {
                cfgUtility.save(firstMCClansPlugin.getFile1());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getLeader(String clanName) {
        if(Bukkit.getPlayer(cfgUtility.get(clanName + ".leader").toString()) != null){
            String name = Bukkit.getPlayer(UUID.fromString(cfgUtility.get(clanName + ".leader").toString())).getName();
            return name;
        }else{
            String name = Bukkit.getOfflinePlayer(UUID.fromString(cfgUtility.get(clanName + ".leader").toString())).getName();
            return name;
        }
    }

    public String[] getMembers(String clanName) {
        return (String[]) cfgUtility.get(clanName + ".members");
    }

    public static String getLevel(String clanName) {
        return cfgUtility.get(clanName + ".level").toString();
    }

    public Location getHome1(String clanName) {
        return (Location) cfgUtility.get(clanName + ".home1");
    }

    public Location getHome2(String clanName) {
        return (Location) cfgUtility.get(clanName + ".home2");
    }

    public Location getHome3(String clanName) {
        return (Location) cfgUtility.get(clanName + ".home3");
    }

    public static boolean getShowTag(String clanName) {
        return (boolean) cfgUtility.get(clanName + ".showTag");
    }

    public static boolean isInClan(Player p) {
        if (cfgUtility3.contains(p.getUniqueId().toString())) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isInClanOffline(OfflinePlayer p) {
        if (cfgUtility3.contains(p.getUniqueId().toString())) {
            return true;
        } else {
            return false;
        }
    }

    public static Double getMoney(String clanName) {
        Double currentMoney = (Double) cfgUtility.get(clanName + ".money");
        return currentMoney;
    }

    public static void setMoney(Double money, String clanName) throws IOException {
        cfgUtility.set(clanName + ".money", 0);
        cfgUtility.set(clanName + ".money", money);
        cfgUtility.save(firstMCClansPlugin.getFile1());
    }

    public static void addMoney(Double amount, String clanName) throws IOException {
        Double currentMoney = (Double) cfgUtility.get(clanName + ".money");
        Double newMoney = currentMoney + amount;
        cfgUtility.set(clanName + ".money", 0);
        cfgUtility.set(clanName + ".money", newMoney);
        cfgUtility.save(firstMCClansPlugin.getFile1());

    }

    public static void removeMoney(Double amount, String clanName) throws IOException {
        Double currentMoney = (Double) cfgUtility.get(clanName + ".money");
        Double newMoney = currentMoney - amount;
        cfgUtility.set(clanName + ".money", 0);
        cfgUtility.set(clanName + ".money", newMoney);
        cfgUtility.save(firstMCClansPlugin.getFile1());

    }

    public static void addClanMember(Player p, String clanName) {
        if (isInClan(p) == false) {
            cfgUtility.set(clanName + ".members", p.getUniqueId().toString());
            cfgUtility3.set(p.getUniqueId().toString(), clanName);
            try {
                cfgUtility.save(firstMCClansPlugin.getFile1());
                cfgUtility3.save(firstMCClansPlugin.getFile3());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            p.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist bereits in einem Clan!");
        }
    }

    public static String checkClan(Player p) {
        if (cfgUtility3.get(p.getUniqueId().toString()) != null) {
            return cfgUtility3.get(p.getUniqueId().toString()).toString();
        }
        return "";
    }
    public static String checkClanOffline(OfflinePlayer p) {
        if (cfgUtility3.get(p.getUniqueId().toString()) != null) {
            return cfgUtility3.get(p.getUniqueId().toString()).toString();
        }
        return "";
    }

    public static String checkClanInvite(Player p) {
        return cfgUtility2.get(p.getUniqueId().toString()).toString();
    }

    public static boolean checkIfInvited(Player p) {
        if (cfgUtility2.contains(p.getUniqueId().toString())) {
            return true;
        } else {
            return false;
        }
    }

    public static void writePlayerIntoClanMembers(Player player, String clanName) {
        cfgUtility3.set(player.getUniqueId().toString(), clanName);
        try {
            cfgUtility3.save(firstMCClansPlugin.getFile3());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkIfTwoAreInSameClan(Player player, Player player1){
        if(ClanAPI.checkClan(player).equals(ClanAPI.checkClan(player1))){
            return true;
        }else{
            return false;
        }

    }

}




