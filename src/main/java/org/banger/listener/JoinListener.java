package org.banger.listener;

import net.luckperms.api.LuckPerms;
import org.banger.firstmcclans.FirstMCClansPlugin;
import org.banger.manage.ClanAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;

import javax.naming.Name;
import java.util.Timer;

public class JoinListener implements Listener {

    private static RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
    private static LuckPerms api = provider.getProvider();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(FirstMCClansPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (ClanAPI.isInClan(all)) {
                            String Namebefore = api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix();
                            all.setPlayerListName(Namebefore.replaceAll("&", "§") + all.getName() + " §8[§a" + ClanAPI.checkClan(all) + "§8]");
                    }else{
                        String Namebefore = api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix();
                        all.setPlayerListName(Namebefore.replaceAll("&", "§") + all.getName());

                    }
                }
            }
        }, 20);

        if (!player.hasPlayedBefore()) {
            player.sendMessage("");
        }
    }

    @EventHandler
    public void onChat(PlayerChatEvent e){
        if (ClanAPI.isInClan(e.getPlayer())) {
            String Namebefore = api.getUserManager().getUser(e.getPlayer().getUniqueId()).getCachedData().getMetaData().getPrefix();
            e.getPlayer().setPlayerListName(Namebefore.replaceAll("&", "§") + e.getPlayer().getName() + " §8[§a" + ClanAPI.checkClan(e.getPlayer()) + "§8]");
        }else{
            String Namebefore = api.getUserManager().getUser(e.getPlayer().getUniqueId()).getCachedData().getMetaData().getPrefix();
            e.getPlayer().setPlayerListName(Namebefore.replaceAll("&", "§") + e.getPlayer().getName());
        }
    }

}
