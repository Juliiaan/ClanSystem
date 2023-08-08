package org.banger.commands;

import org.banger.firstmcclans.FirstMCClansPlugin;
import org.banger.manage.ClanAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanChatCommand implements CommandExecutor {

    private static FirstMCClansPlugin firstMCClansPlugin = FirstMCClansPlugin.getInstance();

    @Override
    public boolean onCommand(CommandSender cs, Command command, String s, String[] args) {
        if(cs instanceof Player){
            Player player = (Player)cs;
            if(ClanAPI.isInClan(player)){
                String clanP1 = ClanAPI.checkClan(player);

                String msg = "";
                for(int i = 0; i < args.length; i++){
                    msg = msg + args[i] + " ";
                }
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (ClanAPI.isInClan(all)) {
                        if (ClanAPI.checkClan(all).equals(clanP1)) {
                            all.sendMessage("§8[§e§lClanChat§8] §a" + player.getName() + " §8» §7" + msg);
                        }
                    }
                }


            }else{
                player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du bist in keinem Clan!");
            }

        }else{
            cs.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist kein Spieler!");
        }
        return false;
    }
}
