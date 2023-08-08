package org.banger.commands;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import net.ess3.api.MaxMoneyException;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeBuilder;
import net.milkbowl.vault.economy.Economy;
import org.banger.firstmcclans.FirstMCClansPlugin;
import org.banger.manage.ClanAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.checkerframework.checker.units.qual.A;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

public class ClanCommand implements CommandExecutor {

    private static FirstMCClansPlugin firstMCClansPlugin = FirstMCClansPlugin.getInstance();
    private static ClanAPI clan;
    private static Economy economy;

    private static RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
    private static LuckPerms api = provider.getProvider();

    private static YamlConfiguration InfoCFG = firstMCClansPlugin.getCfgUtility();
    private static YamlConfiguration InvitesCFG = firstMCClansPlugin.getCfgUtility2();
    private static YamlConfiguration MembersCFG = firstMCClansPlugin.getClanUtility();

    @Override
    public boolean onCommand(CommandSender cs, Command command, String s, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage("§cDu bist kein Spieler!");
            return false;
        }
        Player player = (Player) cs;

        if (args.length < 1) {
            player.performCommand("clan help");
        } else {

            if (args[0].equalsIgnoreCase("create")) {
                if (args.length == 2) {
                    if (ClanAPI.isInClan(player) == false) {
                        if (!(args[1].length() >= 11)) {
                            if (!ClanAPI.clanExists(args[1])) {
                                ClanAPI clan = new ClanAPI(args[1], player.getUniqueId().toString(), null, (double) 0, "Level 1", null, null, null, true);
                                ClanAPI.writePlayerIntoClanMembers(player, args[1]);
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast erfolgreich den Clan: §7[§a" + args[1] + "§7] §7erstellt!");

                                for (Player all : Bukkit.getOnlinePlayers()) {
                                    if (ClanAPI.isInClan(all)) {
                                        if (api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix() != null) {
                                            String Namebefore = api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix();
                                            all.setPlayerListName(Namebefore.replaceAll("&", "§") + all.getName() + " §8[§a" + ClanAPI.checkClan(all) + "§8]");
                                        }
                                    } else {
                                        String Namebefore = api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix();
                                        all.setPlayerListName(Namebefore.replaceAll("&", "§") + all.getName());
                                    }
                                }

                                try {
                                    InfoCFG.load(firstMCClansPlugin.getFile1());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                } catch (InvalidConfigurationException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§cEin Clan mit diesem Namen existiert bereits. Wähle einen anderen Namen!");
                            }
                        } else {
                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDer Name ist zu lang. Verwende maximal 8 Zeichen!");
                        }

                    } else {
                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist bereits in einem Clan. Verlassen den Clan mit: /clan leave");
                    }
                } else {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze: /clan create <Clanname>!");
                }


            } else if (args[0].equalsIgnoreCase("delete")) {
                if (ClanAPI.isInClan(player)) {
                    if (args.length == 1) {
                        String clanP1 = ClanAPI.checkClan(player);
                        if (ClanAPI.getLeader(clanP1).equals(player.getName())) {
                            for (Player all : Bukkit.getOnlinePlayers()) {
                                if (ClanAPI.isInClan(all)) {
                                    if (ClanAPI.checkClan(all).equals(clanP1)) {
                                        MembersCFG.set(all.getUniqueId().toString(), null);
                                        MembersCFG.set(player.getUniqueId().toString(), null);


                                        try {
                                            MembersCFG.save(firstMCClansPlugin.getFile3());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }

                                        all.sendMessage(firstMCClansPlugin.getPrefix() + "§7Euer Clan wurde §c§ngelöscht§7!");
                                        ClanAPI.deleteClan(clanP1);
                                    } else {
                                        if (api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix() != null) {
                                            String Namebefore = api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix();
                                            all.setPlayerListName(Namebefore.replaceAll("&", "§") + all.getName());
                                        }
                                    }
                                } else {
                                    String Namebefore = api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix();
                                    all.setPlayerListName(Namebefore.replaceAll("&", "§") + all.getName());
                                }

                            }

                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast den Clan erfolgreich gelöscht!");

                        } else {
                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist nicht berechtigt den Clan zu löschen!");
                        }
                    } else {
                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze /clan delete!");
                    }


                } else {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist in keinem Clan!");
                }

            } else if (args[0].equalsIgnoreCase("invite")) {
                if (args.length == 2) {
                    if (Bukkit.getPlayer(args[1]) != null) {
                        Player offPlayer = Bukkit.getPlayer(args[1]);
                        if (ClanAPI.isInClan(player)) {
                            if (args.length == 2) {
                                if (ClanAPI.isInClan(offPlayer) == false) {
                                    if (ClanAPI.getLeader(ClanAPI.checkClan(player)).equals(player.getName()) || player.hasPermission("clan.mod")) {
                                        if (offPlayer.isOnline()) {
                                            InvitesCFG.set(offPlayer.getUniqueId().toString(), ClanAPI.checkClan(player));
                                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast erfolgreich den Spieler §a" + offPlayer.getName() + " §7in den Clan eingeladen!");
                                            offPlayer.sendMessage(firstMCClansPlugin.getPrefix() + "§7Der Spieler §a" + player.getDisplayName() + " §7hat dich in seinen Clan: §2§l" + ClanAPI.checkClan(player) + " §7eingeladen!"
                                                    + " §7Falls du dem Clan beitreten willst, joine ihm mit: §a/clan join§7!");


                                            try {
                                                InvitesCFG.save(firstMCClansPlugin.getFile2());
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        } else {
                                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDer Spieler §4§l" + offPlayer.getName() + " §cist im Moment nicht Online. Versuche es später erneut.");
                                        }
                                    } else {
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist nicht berechtigt dazu, andere Spieler in deinen Clan einzuladen!");
                                    }

                                } else {
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDer Spieler ist bereits in einem Clan.");
                                }

                            } else {
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze: /clan invite <Spieler>!");
                            }
                        } else {
                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist in keinem Clan!");
                        }
                    } else {
                        OfflinePlayer player2 = Bukkit.getOfflinePlayer(args[1]);
                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDer Spieler §4§l" + player2.getName() + " §cist im Moment nicht Online. Versuche es später erneut.");
                    }
                } else {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze: /clan invite <Spieler>!");
                }
            } else if (args[0].equalsIgnoreCase("join")) {
                if (args.length != 1) {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze: /clan join");
                    return false;
                }
                if (!ClanAPI.isInClan(player)) {
                    if (ClanAPI.checkIfInvited(player) == true) {
                        ClanAPI.addClanMember(player, ClanAPI.checkClanInvite(player));
                        InvitesCFG.set(player.getUniqueId().toString(), null);
                        try {
                            InvitesCFG.save(firstMCClansPlugin.getFile2());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du bist erfolgreich dem §7[§a" + ClanAPI.checkClan(player) + "§7] §7Clan beigetreten!");
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (ClanAPI.checkIfTwoAreInSameClan(all, player)) {
                                all.sendMessage(firstMCClansPlugin.getPrefix() + "§7Der Spieler §a" + player.getName() + " §7ist dem Clan beigetreten!");
                            }
                        }
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (ClanAPI.isInClan(all)) {
                                if (api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix() != null) {
                                    String Namebefore = api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix();
                                    all.setPlayerListName(Namebefore.replaceAll("&", "§") + all.getName() + " §8[§a" + ClanAPI.checkClan(all) + "§8]");
                                }
                            } else {
                                String Namebefore = api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix();
                                all.setPlayerListName(Namebefore.replaceAll("&", "§") + all.getName());
                            }
                        }

                    } else {
                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu wurdest in keinen Clan eingeladen. Schreibe /clan help, für alle Clan-Befehle!");
                    }
                } else {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist bereits in einem Clan!");
                }

            } else if (args[0].equalsIgnoreCase("leave")) {
                if (args.length != 1) {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cBenutze: /clan leave!");
                } else {
                    if (ClanAPI.isInClan(player)) {
                        if (ClanAPI.getLeader(ClanAPI.checkClan(player)).equals(player.getName())) {
                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist der Leader des Clans. Um den Clan zu löschen, verwende: /clan delete!");
                        } else {
                            for (Player all : Bukkit.getOnlinePlayers()) {
                                if (ClanAPI.checkClan(all).equals(ClanAPI.checkClan(player))) {
                                    if(player.hasPermission("clan.mod")){
                                        api.getUserManager().getUser(player.getUniqueId()).data().remove(Node.builder("clan.mod").build());
                                    }
                                    all.sendMessage(firstMCClansPlugin.getPrefix() + "§7Der Spieler §c" + player.getName() + " §7hat den Clan verlassen!");
                                }
                            }

                            for (Player all : Bukkit.getOnlinePlayers()) {
                                if (ClanAPI.isInClan(all)) {
                                    if (api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix() != null) {
                                        String Namebefore = api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix();
                                        all.setPlayerListName(Namebefore.replaceAll("&", "§") + all.getName() + " §8[§a" + ClanAPI.checkClan(all) + "§8]");
                                    }
                                } else {
                                    String Namebefore = api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix();
                                    all.setPlayerListName(Namebefore.replaceAll("&", "§") + all.getName());
                                }
                            }

                            InfoCFG.set(ClanAPI.checkClan(player) + ".members" + "." + player.getUniqueId().toString(), null);
                            MembersCFG.set(player.getUniqueId().toString(), null);
                            try {
                                InfoCFG.save(firstMCClansPlugin.getFile1());
                                MembersCFG.save(firstMCClansPlugin.getFile3());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist in keinem Clan!");
                    }

                }

            } else if (args[0].equalsIgnoreCase("info")) {
                if (ClanAPI.isInClan(player)) {
                    Inventory inventory = Bukkit.createInventory(null, 54, firstMCClansPlugin.getPrefix() + "§7Info §a" + ClanAPI.checkClan(player) + " §7Clan");
                    ItemStack fill = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                    ItemMeta meta = fill.getItemMeta();
                    meta.setDisplayName(" ");
                    fill.setItemMeta(meta);

                    for (int i = 0; i < 54; i++) {
                        inventory.setItem(i, fill);
                    }

                    ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
                    SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
                    if (Bukkit.getPlayer(ClanAPI.getLeader(ClanAPI.checkClan(player))) != null) {
                        Player player1 = Bukkit.getPlayer(ClanAPI.getLeader(ClanAPI.checkClan(player)));
                        skullMeta.setOwningPlayer(player1);
                        skullMeta.setDisplayName("§eAnführer §8» §7" + player1.getDisplayName());
                        head.setItemMeta(skullMeta);
                    } else {
                        OfflinePlayer player1 = Bukkit.getOfflinePlayer(ClanAPI.getLeader(ClanAPI.checkClan(player)));
                        skullMeta.setOwningPlayer(player1);
                        skullMeta.setDisplayName("§eAnführer §8» §7" + player1.getName());
                        head.setItemMeta(skullMeta);
                    }

                    ItemStack goldStack = new ItemStack(Material.GOLD_INGOT, 1);
                    ItemMeta goldMeta = goldStack.getItemMeta();
                    goldMeta.setDisplayName("§eClan-Kasse §8» §7$" + ClanAPI.getMoney(ClanAPI.checkClan(player)));
                    goldStack.setItemMeta(goldMeta);

                    ItemStack enchantStack = new ItemStack(Material.EXPERIENCE_BOTTLE, 1);
                    ItemMeta enchantMeta = enchantStack.getItemMeta();
                    enchantMeta.setDisplayName("§eLevel §8» §7" + ClanAPI.getLevel(ClanAPI.checkClan(player)));
                    enchantStack.setItemMeta(enchantMeta);

                    ItemStack tagStack = new ItemStack(Material.PAPER, 1);
                    ItemMeta tagMeta = tagStack.getItemMeta();
                    tagMeta.setDisplayName("§eListe der Member ");
                    tagStack.setItemMeta(tagMeta);

                    inventory.setItem(20, enchantStack);
                    inventory.setItem(22, head);
                    inventory.setItem(24, goldStack);
                    inventory.setItem(40, tagStack);

                    player.openInventory(inventory);


                } else {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist in keinem Clan!");
                }


            } else if (args[0].equalsIgnoreCase("einzahlen")) {
                if (args.length == 2) {
                    if (ClanAPI.isInClan(player)) {
                        Double amount = null;
                        try {
                            amount = Double.parseDouble(args[1]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDas ist keine Zahl.");
                            return false;
                        }
                        try {
                            if (com.earth2me.essentials.api.Economy.hasEnough(player.getUniqueId(), BigDecimal.valueOf(amount))) {
                                try {
                                    ClanAPI.addMoney(amount, ClanAPI.checkClan(player));
                                    BigDecimal newMoney = com.earth2me.essentials.api.Economy.getMoneyExact(player.getUniqueId()).subtract(BigDecimal.valueOf(amount));
                                    com.earth2me.essentials.api.Economy.setMoney(player.getUniqueId(), newMoney);
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast §a" + amount + " §7in die Clan-Kasse eingezahlt!");
                                    for (Player all : Bukkit.getOnlinePlayers()) {
                                        if (ClanAPI.checkClan(all).equals(ClanAPI.checkClan(player))) {
                                            all.sendMessage(firstMCClansPlugin.getPrefix() + "§7Der Spieler §a" + player.getName() + " §7hat §2$" + amount + " §7in die Clan-Kasse eingezahlt!");
                                        }
                                    }


                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }

                            } else {
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu hast nicht genug Geld dafür!");
                            }
                        } catch (UserDoesNotExistException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist in keinem Clan!");
                    }


                } else {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze: /clan einzahlen <Anzahl>");
                }

            } else if (args[0].equalsIgnoreCase("abheben")) {
                if (ClanAPI.isInClan(player)) {
                    if (ClanAPI.getLeader(ClanAPI.checkClan(player)).equals(player.getName())) {
                        Double amount = null;
                        try {
                            amount = Double.parseDouble(args[1]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDas ist keine Zahl.");
                            return false;
                        }
                        if (ClanAPI.getMoney(ClanAPI.checkClan(player)) >= amount) {

                            try {
                                ClanAPI.removeMoney(amount, ClanAPI.checkClan(player));
                                com.earth2me.essentials.api.Economy.add(player.getUniqueId(), BigDecimal.valueOf(amount));
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast §c" + amount + " §7von der Clan-Kasse abgehoben!");
                                for (Player all : Bukkit.getOnlinePlayers()) {
                                    if (ClanAPI.checkClan(all).equals(ClanAPI.checkClan(player))) {
                                        all.sendMessage(firstMCClansPlugin.getPrefix() + "§7Der Spieler §c" + player.getName() + " §7hat §c$" + amount + " §7von der Clan-Kasse abgehoben!");
                                    }
                                }


                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (MaxMoneyException e) {
                                throw new RuntimeException(e);
                            } catch (UserDoesNotExistException e) {
                                throw new RuntimeException(e);
                            } catch (NoLoanPermittedException e) {
                                throw new RuntimeException(e);
                            }


                        } else {
                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§cEs ist nicht so viel Geld in der Clan-Kasse!");
                        }
                    } else {
                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist nicht dazu berechtigt, Geld vom Clan-Konto abzuheben!");
                    }

                } else {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist in keinem Clan!");
                }

            } else if (args[0].equalsIgnoreCase("kick")) {
                if (ClanAPI.isInClan(player)) {
                    if (args.length == 2) {
                        if (Bukkit.getPlayer(args[1]) != null) {
                            if (ClanAPI.isInClan(Bukkit.getPlayer(args[1]))) {
                                Player player2 = Bukkit.getPlayer(args[1]);
                                String clanP1 = ClanAPI.checkClan(player);
                                String clanP2 = ClanAPI.checkClan(player2);
                                if (clanP1.equals(clanP2)) {
                                    if (ClanAPI.getLeader(clanP1).equals(player.getName()) || player.hasPermission("clan.mod")) {
                                        player2.closeInventory();
                                        MembersCFG.set(player2.getUniqueId().toString(), null);
                                        try {
                                            MembersCFG.save(firstMCClansPlugin.getFile3());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        player2.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu wurdest aus dem Clan gekickt!");
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast den Spieler §c" + player2.getName() + " §7aus dem Clan gekickt!");
                                        if(player2.hasPermission("clan.mod")){
                                            api.getUserManager().getUser(player2.getUniqueId()).data().remove(Node.builder("clan.mod").build());
                                        }

                                        for (Player all : Bukkit.getOnlinePlayers()) {
                                            if (ClanAPI.isInClan(all)) {
                                                if (ClanAPI.checkClan(all).equals(clanP1)) {
                                                    all.sendMessage(firstMCClansPlugin.getPrefix() + "§7Der Spieler §c" + player2.getName() + " §7wurde aus dem Clan gekickt!");
                                                }
                                            }
                                        }
                                        for (Player all : Bukkit.getOnlinePlayers()) {
                                            if (ClanAPI.isInClan(all)) {
                                                if (api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix() != null) {
                                                    String Namebefore = api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix();
                                                    all.setPlayerListName(Namebefore.replaceAll("&", "§") + all.getName() + " §8[§a" + ClanAPI.checkClan(all) + "§8]");
                                                }
                                            } else {
                                                String Namebefore = api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix();
                                                all.setPlayerListName(Namebefore.replaceAll("&", "§") + all.getName());
                                            }
                                        }

                                    } else {
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist nicht berechtigt den Spieler zu kicken!");
                                    }
                                } else {
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDer Spieler ist nicht in deinem Clan!");
                                }
                            } else {
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDer Spieler ist in keinem Clan!");
                            }
                        } else {
                            if (ClanAPI.isInClanOffline(Bukkit.getOfflinePlayer(args[1]))) {
                                OfflinePlayer player2 = Bukkit.getPlayer(args[1]);
                                String clanP1 = ClanAPI.checkClan(player);
                                String clanP2 = ClanAPI.checkClanOffline(player2);
                                if (clanP1.equals(clanP2)) {
                                    if (ClanAPI.getLeader(clanP1).equals(player.getName()) || player.hasPermission("clan.mod")) {
                                        MembersCFG.set(player2.getUniqueId().toString(), null);
                                        try {
                                            MembersCFG.save(firstMCClansPlugin.getFile3());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast den Spieler §c" + player2.getName() + " §7aus dem Clan gekickt!");
                                        if(api.getUserManager().getUser(player2.getUniqueId()).getCachedData().getPermissionData().checkPermission("clan.mod").asBoolean()){
                                            api.getUserManager().getUser(player2.getUniqueId()).data().remove(Node.builder("clan.mod").build());
                                        }

                                        for (Player all : Bukkit.getOnlinePlayers()) {
                                            if (ClanAPI.isInClan(all)) {
                                                if (ClanAPI.checkClan(all).equals(clanP1)) {
                                                    all.sendMessage(firstMCClansPlugin.getPrefix() + "§7Der Spieler §c" + player2.getName() + " §7wurde aus dem Clan gekickt!");
                                                }
                                            }
                                        }
                                        for (Player all : Bukkit.getOnlinePlayers()) {
                                            if (ClanAPI.isInClan(all)) {
                                                if (api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix() != null) {
                                                    String Namebefore = api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix();
                                                    all.setPlayerListName(Namebefore.replaceAll("&", "§") + all.getName() + " §8[§a" + ClanAPI.checkClan(all) + "§8]");
                                                }
                                            } else {
                                                String Namebefore = api.getUserManager().getUser(all.getUniqueId()).getCachedData().getMetaData().getPrefix();
                                                all.setPlayerListName(Namebefore.replaceAll("&", "§") + all.getName());
                                            }
                                        }

                                    } else {
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist nicht berechtigt den Spieler zu kicken!");
                                    }
                                } else {
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDer Spieler ist nicht in deinem Clan!");
                                }
                            } else {
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDer Spieler ist in keinem Clan!");
                            }


                        }

                    } else {
                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze /clan kick <Spielername>!");
                    }


                } else {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist in keinem Clan!");
                }

            } else if (args[0].equalsIgnoreCase("inv") || (args[0].equalsIgnoreCase("inventory")) || (args[0].equalsIgnoreCase("inventar"))) {
                if (args.length == 1) {
                    if (ClanAPI.isInClan(player)) {
                        if (ClanAPI.getLevel(ClanAPI.checkClan(player)).equalsIgnoreCase("Level 1")) {
                            if (InfoCFG.getBoolean(ClanAPI.checkClan(player) + ".isOpenInv") == false || InfoCFG.get(ClanAPI.checkClan(player) + ".isOpenInv") == null) {
                                Inventory inv = Bukkit.createInventory(null, 18, firstMCClansPlugin.getPrefix() + "§7Inventory - Level 1");
                                for (int i = 0; i < 18; i++) {
                                    if (InfoCFG.contains(ClanAPI.checkClan(player) + "." + i)) {
                                        ItemStack stack = InfoCFG.getItemStack(ClanAPI.checkClan(player) + "." + i);
                                        inv.setItem(i, stack);
                                        player.openInventory(inv);
                                        InfoCFG.set(ClanAPI.checkClan(player) + ".isOpenInv", true);
                                        try {
                                            InfoCFG.save(firstMCClansPlugin.getFile1());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else {
                                        InfoCFG.set(ClanAPI.checkClan(player) + ".isOpenInv", true);
                                        try {
                                            InfoCFG.save(firstMCClansPlugin.getFile1());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        player.openInventory(inv);
                                    }
                                }

                            } else {
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Jemand greift im Moment auf das Inventar zu!");
                            }
                        } else if (ClanAPI.getLevel(ClanAPI.checkClan(player)).equalsIgnoreCase("Level 2")) {
                            if (InfoCFG.getBoolean(ClanAPI.checkClan(player) + ".isOpenInv") == false) {
                                Inventory inv = Bukkit.createInventory(null, 36, firstMCClansPlugin.getPrefix() + "§7Inventory - Level 2");
                                for (int i = 0; i < 36; i++) {
                                    if (InfoCFG.contains(ClanAPI.checkClan(player) + "." + i)) {
                                        ItemStack stack = InfoCFG.getItemStack(ClanAPI.checkClan(player) + "." + i);
                                        inv.setItem(i, stack);
                                        InfoCFG.set(ClanAPI.checkClan(player) + ".isOpenInv", true);
                                        try {
                                            InfoCFG.save(firstMCClansPlugin.getFile1());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        player.openInventory(inv);

                                    } else {
                                        InfoCFG.set(ClanAPI.checkClan(player) + ".isOpenInv", true);
                                        try {
                                            InfoCFG.save(firstMCClansPlugin.getFile1());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        player.openInventory(inv);
                                    }
                                }
                            } else {
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Jemand greift im Moment auf das Inventar zu!");
                            }
                        } else if (ClanAPI.getLevel(ClanAPI.checkClan(player)).equalsIgnoreCase("Level 3")) {
                            if (InfoCFG.getBoolean(ClanAPI.checkClan(player) + ".isOpenInv") == false) {
                                Inventory inv = Bukkit.createInventory(null, 54, firstMCClansPlugin.getPrefix() + "§7Inventory - Level 3");
                                for (int i = 0; i < 54; i++) {
                                    if (InfoCFG.contains(ClanAPI.checkClan(player) + "." + i)) {
                                        ItemStack stack = InfoCFG.getItemStack(ClanAPI.checkClan(player) + "." + i);
                                        inv.setItem(i, stack);
                                        InfoCFG.set(ClanAPI.checkClan(player) + ".isOpenInv", true);
                                        try {
                                            InfoCFG.save(firstMCClansPlugin.getFile1());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        player.openInventory(inv);

                                    } else {
                                        InfoCFG.set(ClanAPI.checkClan(player) + ".isOpenInv", true);
                                        try {
                                            InfoCFG.save(firstMCClansPlugin.getFile1());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        player.openInventory(inv);
                                    }
                                }


                            } else {
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Jemand greift im Moment auf das Inventar zu!");
                            }
                        } else {
                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Jemand greift im Moment auf das Inventar zu!");
                        }
                    } else {
                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du bist in keinem Clan!");
                    }

                } else {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze: /clan <inv/inventory>");
                }


            } else if (args[0].equalsIgnoreCase("sethome")) {
                if (ClanAPI.isInClan(player)) {
                    if (args.length == 2) {
                        String clanP = ClanAPI.checkClan(player);
                        String leaderClanP = ClanAPI.getLeader(clanP);
                        if (player.hasPermission("clan.mod") || leaderClanP.equalsIgnoreCase(player.getName())) {
                            if (ClanAPI.getLevel(clanP).equals("Level 1")) {
                                if (args[1].equalsIgnoreCase("1")) {
                                    if (!InfoCFG.contains(clanP + ".home1")) {
                                        InfoCFG.set(clanP + ".home1", player.getLocation());
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast erfolgreich euer erstes Clan Home gesetzt!");
                                        try {
                                            InfoCFG.save(firstMCClansPlugin.getFile1());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }

                                    } else {
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Euer Clan ist erst Level 1, und euer erstes Home wurde bereits gesetzt! Lösche das erste Home mit /clan delhome 1!");
                                    }
                                } else {
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Da euer Clan erst Level 1 ist, könnt ihr nur das erste Home setzen! Verwende: /clan sethome 1!");
                                }
                            } else if (ClanAPI.getLevel(clanP).equals("Level 2")) {
                                if (args[1].equalsIgnoreCase("1")) {
                                    if (!InfoCFG.contains(clanP + ".home1")) {
                                        InfoCFG.set(clanP + ".home1", player.getLocation());
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast erfolgreich euer erstes Clan Home gesetzt!");
                                    } else {
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Euer erstes Home wurde bereits gesetzt! Lösche das erste Home mit /clan delhome 1!");
                                    }

                                } else if (args[1].equalsIgnoreCase("2")) {
                                    if (!InfoCFG.contains(clanP + ".home2")) {
                                        InfoCFG.set(clanP + ".home2", player.getLocation());
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast erfolgreich euer zweites Clan Home gesetzt!");
                                    } else {
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Euer zweites Home wurde bereits gesetzt! Lösche das erste Home mit /clan delhome 1!");
                                    }

                                } else {
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Da euer Clan erst Level 2 ist, könnt ihr nur das erste und das zweite Home setzen! Verwende: /clan sethome <1/2>!");
                                }
                            } else if (ClanAPI.getLevel(clanP).equals("Level 3")) {
                                if (args[1].equalsIgnoreCase("1")) {
                                    if (!InfoCFG.contains(clanP + ".home1")) {
                                        InfoCFG.set(clanP + ".home1", player.getLocation());
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast erfolgreich euer erstes Clan Home gesetzt!");
                                    } else {
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Euer erstes Home wurde bereits gesetzt! Lösche das erste Home mit /clan delhome 1!");
                                    }

                                } else if (args[1].equalsIgnoreCase("2")) {
                                    if (!InfoCFG.contains(clanP + ".home2")) {
                                        InfoCFG.set(clanP + ".home2", player.getLocation());
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast erfolgreich euer zweites Clan Home gesetzt!");
                                    } else {
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Euer zweites Home wurde bereits gesetzt! Lösche das zweite Home mit /clan delhome 2!");
                                    }

                                } else if (args[1].equalsIgnoreCase("3")) {
                                    if (!InfoCFG.contains(clanP + ".home3")) {
                                        InfoCFG.set(clanP + ".home3", player.getLocation());
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast erfolgreich euer drittes Clan Home gesetzt!");
                                    } else {
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Euer drittes Home wurde bereits gesetzt! Lösche das erste Home mit /clan delhome 3!");
                                    }
                                } else {
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze: /clan sethome <1/2/3>");
                                }
                            }
                        } else {
                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDu bist nicht berechtigt ein Clan-Home zu setzen!");
                        }
                    } else {
                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze: /clan sethome <1/2/3>");
                    }
                }

            } else if (args[0].equalsIgnoreCase("delhome")) {
                if (ClanAPI.isInClan(player)) {
                    if (args.length == 2) {
                        String clanP = ClanAPI.checkClan(player);
                        String leaderClanP = ClanAPI.getLeader(clanP);
                        if (player.hasPermission("clan.mod") || leaderClanP.equalsIgnoreCase(player.getName())) {
                            if (args[1].equalsIgnoreCase("1")) {
                                if (InfoCFG.contains(clanP + ".home1")) {
                                    InfoCFG.set(clanP + ".home1", null);
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast §2erfolgreich §7euer erstes Home gelöscht!");
                                    try {
                                        InfoCFG.save(firstMCClansPlugin.getFile1());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }

                                } else {
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Ihr habt kein erstes Home!");
                                }

                            } else if (args[1].equalsIgnoreCase("2")) {
                                if (InfoCFG.contains(clanP + ".home2")) {
                                    InfoCFG.set(clanP + ".home2", null);
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast §2erfolgreich §7euer zweites Home gelöscht!");
                                    try {
                                        InfoCFG.save(firstMCClansPlugin.getFile1());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }

                                } else {
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Ihr habt kein zweites Home!");
                                }


                            } else if (args[1].equalsIgnoreCase("3")) {
                                if (InfoCFG.contains(clanP + ".home3")) {
                                    InfoCFG.set(clanP + ".home3", null);
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast §2erfolgreich §7euer drittes Home gelöscht!");
                                    try {
                                        InfoCFG.save(firstMCClansPlugin.getFile1());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }

                                } else {
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Ihr habt kein drittes Home!");
                                }


                            } else {
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze: /clan delhome <1/2/3>");
                            }

                        } else {
                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Dafür bist du nicht berechtigt! Du musst Clan Mod oder höher sein!");
                        }
                    } else {
                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze: /clan delhome <1/2/3>");
                    }
                } else {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du bist in keinem Clan!");
                }

            } else if (args[0].equalsIgnoreCase("home")) {
                if (ClanAPI.isInClan(player)) {
                    if (args.length == 2) {
                        if (args[1].equalsIgnoreCase("1")) {
                            if (InfoCFG.contains(ClanAPI.checkClan(player) + ".home1")) {
                                Location loc = InfoCFG.getLocation(ClanAPI.checkClan(player) + ".home1");
                                player.teleport(loc);
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du wurdest §a§nerfolgreich§7 zum ersten Clan-Home teleportiert!");
                            } else {
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Euer Clan hat §c§nkein§7 erstes Home!");
                            }
                        } else if (args[1].equalsIgnoreCase("2")) {
                            if (InfoCFG.contains(ClanAPI.checkClan(player) + ".home2")) {
                                Location loc = InfoCFG.getLocation(ClanAPI.checkClan(player) + ".home2");
                                player.teleport(loc);
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du wurdest §a§nerfolgreich§7 zum zweiten Clan-Home teleportiert!");
                            } else {
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Euer Clan hat §c§nkein§7 zweites Home!");
                            }
                        } else if (args[1].equalsIgnoreCase("3")) {
                            if (InfoCFG.contains(ClanAPI.checkClan(player) + ".home3")) {
                                Location loc = InfoCFG.getLocation(ClanAPI.checkClan(player) + ".home3");
                                player.teleport(loc);
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du wurdest §a§nerfolgreich§7 zum dritten Clan-Home teleportiert!");
                            } else {
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Euer Clan hat §c§nkein§7 drittes Home!");
                            }
                        } else {
                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze: /clan home <1/2/3>!");
                        }

                    } else {
                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze: /clan home <1/2/3>!");
                    }
                } else {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du bist in keinem Clan!");
                }


            } else if (args[0].equalsIgnoreCase("help")) {
                if (args.length == 1) {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7§l§nEine Liste aller Clan-bezogenen Befehle:");
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§a/clan help §8| §7Eine Liste aller Clan-bezogenen Befehle");
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§a/clan info §8| §7Info-GUI mit Information über deinen Clan!");
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§a/clan create <Name> §8| §7Erstelle einen Clan");
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§a/clan delete §8| §7Lösche deinen Clan");
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§a/clan leave §8| §7Verlasse deinen Clan");
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§a/clan join §8| §7Betrete einen Clan zu dem du eingeladen worden bist");
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§a/clan kick <Spieler> §8| §7Entferne einen Spieler aus deinem Clan");
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§a/clan <einzahlen/abheben> <Summe> §8| §7Zahle eine bestimmte Summe auf das Clan Konto ein");
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§a/clan <sethome/delhome> <1/2/3> §8| §7Setze bzw. lösche Clan-Homes");
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§a/clan <promote/demote> <Spieler> §8| §7Befördere oder Degradiere ein Mitglied zum Moderator/zum Member");
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§a/clan home <1/2/3> §8| §7Teleportiere dich zu euren Clan Homes");
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§a/clan upgrade §8| §7Wenn du der Anführer eines Clans bist, kannst du euren Clan auf maximal Level 3 auf Kosten der Clan Kasse upgraden.");
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§a/clan list <Clanname> §8| §7Zeigt dir die Member des angegeben Clans!");
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§eLevel 1§8: §718 Felder im Clan Inventar | 1 Clan-Home");
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§eLevel 2§8: §736 Felder im Clan Inventar | 2 Clan-Homes");
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§eLevel 3§8: §754 Felder im Clan Inventar | 3 Clan-Homes");
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7§l§nEine Liste aller Clan-bezogenen Befehle:");

                } else {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze: /clan help!");
                }
            } else if (args[0].equalsIgnoreCase("promote")) {
                if (args.length == 2) {
                    if (Bukkit.getPlayer(args[1]) != null) {
                        Player player1 = Bukkit.getPlayer(args[1]);
                        if (ClanAPI.isInClan(player)) {
                            String clanP = ClanAPI.checkClan(player);
                            String leaderClanP = ClanAPI.getLeader(clanP);
                            String clanP2 = ClanAPI.checkClan(player1);
                            if (leaderClanP.equalsIgnoreCase(player.getName())) {
                                if (!player1.hasPermission("clan.mod")) {
                                    if (clanP.equalsIgnoreCase(clanP2)) {
                                        api.getUserManager().getUser(player1.getUniqueId()).data().add(Node.builder("clan.mod").build());
                                        api.getUserManager().saveUser(api.getUserManager().getUser(player1.getUniqueId()));
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast den Spieler erfolgreich zum Clan-Moderator hochgestuft!");
                                        player1.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du wurdest vom Leader deines Clans zum §nClan-Moderator §7hochgestuft!");

                                    } else {
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Der Spieler ist nicht in deinem Clan!");
                                    }
                                } else {
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDer Spieler ist bereits Clan-Moderator!");
                                }
                            } else {
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Dazu bist du §c§nnicht §7berechtigt!");
                            }
                        } else {
                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du bist in keinem Clan!");
                        }

                    } else {
                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Dieser Spieler ist im Moment nicht Online!");
                    }
                } else {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze: /clan promote <Spieler>!");
                }
            } else if (args[0].equalsIgnoreCase("demote")) {
                Player player1 = Bukkit.getPlayer(args[1]);
                if (args.length == 2) {
                    if (player1 != null) {
                        if (ClanAPI.isInClan(player)) {
                            String clanP = ClanAPI.checkClan(player);
                            String leaderClanP = ClanAPI.getLeader(clanP);
                            String clanP2 = ClanAPI.checkClan(player1);
                            if (leaderClanP.equalsIgnoreCase(player.getName())) {
                                if (player1.hasPermission("clan.mod")) {
                                    if (clanP.equalsIgnoreCase(clanP2)) {
                                        api.getUserManager().getUser(player1.getUniqueId()).data().remove(Node.builder("clan.mod").build());
                                        api.getUserManager().saveUser(api.getUserManager().getUser(player1.getUniqueId()));
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast den Spieler erfolgreich zum Clan-Member runtergestuft!");
                                        player1.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du wurdest vom Leader deines Clans zum §nClan-Member §7runtergestuft!");

                                    } else {
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Der Spieler ist nicht in deinem Clan!");
                                    }
                                } else {
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDer Spieler ist garkein Clan-Moderator!");
                                }
                            } else {
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Dazu bist du §c§nnicht §7berechtigt!");
                            }
                        } else {
                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du bist in keinem Clan!");
                        }

                    } else {
                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Dieser Spieler ist im Moment nicht Online!");
                    }
                } else {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze: /clan demote <Spieler>!");
                }
            } else if (args[0].equalsIgnoreCase("upgrade")) {
                if (ClanAPI.isInClan(player)) {
                    if (args.length == 1) {
                        String leaderClanP = ClanAPI.getLeader(ClanAPI.checkClan(player));
                        if (leaderClanP.equals(player.getName())) {
                            if (ClanAPI.getLevel(ClanAPI.checkClan(player)).equals("Level 1")) {
                                if (ClanAPI.getMoney(ClanAPI.checkClan(player)) >= 500000.0) {
                                    try {
                                        ClanAPI.removeMoney(500000.0, ClanAPI.checkClan(player));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast erfolgreich das Clan Level erhöht. Ihr seid nun auf Level 2!");
                                    InfoCFG.set(ClanAPI.checkClan(player) + ".level", "Level 2");
                                    try {
                                        InfoCFG.save(firstMCClansPlugin.getFile1());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    for (Player all : Bukkit.getOnlinePlayers()) {
                                        if (ClanAPI.isInClan(all)) {
                                            if (ClanAPI.checkClan(all).equals(ClanAPI.checkClan(player))) {
                                                all.sendMessage(firstMCClansPlugin.getPrefix() + "§7Euer Clan-Leader hat das Clan Level auf Level 2 erhöht!");
                                            }
                                        }
                                    }
                                } else {
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Euer Clan hat nicht genug Geld in der Clan Kasse!");
                                }
                            } else if (ClanAPI.getLevel(ClanAPI.checkClan(player)).equals("Level 2")) {
                                if (ClanAPI.getMoney(ClanAPI.checkClan(player)) >= 1000000.0) {
                                    try {
                                        ClanAPI.removeMoney(1000000.0, ClanAPI.checkClan(player));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du hast erfolgreich das Clan Level erhöht. Ihr seid nun auf Level 3!");
                                    InfoCFG.set(ClanAPI.checkClan(player) + ".level", "Level 3");
                                    try {
                                        InfoCFG.save(firstMCClansPlugin.getFile1());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    for (Player all : Bukkit.getOnlinePlayers()) {
                                        if (ClanAPI.isInClan(all)) {
                                            if (ClanAPI.checkClan(all).equals(ClanAPI.checkClan(player))) {
                                                all.sendMessage(firstMCClansPlugin.getPrefix() + "§7Euer Clan-Leader hat das Clan Level auf Level 3 erhöht!");
                                            }
                                        }
                                    }

                                } else {
                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Euer Clan hat §c§n§lnicht§7 genug Geld in der Clan Kasse!");
                                }
                            } else {
                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Euer Clan ist bereits auf Level 3!");
                            }
                        } else {
                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDafür bist du nicht berechtigt!");
                        }

                    } else {
                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§cFalscher Syntax. Benutze: /clan upgrade!");
                    }
                } else {
                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Du bist in keinem Clan!");
                }
            } else if (args[0].equalsIgnoreCase("list")) {

                    if(args.length == 1){

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

                    }else if(args.length == 2) {
                        ConfigurationSection section = MembersCFG.getConfigurationSection("");
                        if (ClanAPI.clanExists(args[1])) {
                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§4Leader §8| §7" + ClanAPI.getLeader(args[1]));
                            for (String uuidStr : section.getKeys(true)) {
                                UUID uuid = UUID.fromString(uuidStr);
                                if (Bukkit.getPlayer(uuid) != null) {
                                    if (ClanAPI.clanExists(args[1])) {
                                        if (ClanAPI.checkClan(Bukkit.getPlayer(uuid)).equals(args[1])) {
                                            if (!ClanAPI.getLeader(args[1]).equals(Bukkit.getPlayer(uuid).getName())) {
                                                if (Bukkit.getPlayer(uuid).hasPermission("clan.mod")) {
                                                    player.sendMessage(firstMCClansPlugin.getPrefix() + "§cClan-Mod §8| §7" + Bukkit.getPlayer(uuid).getName());
                                                }
                                            }
                                        }
                                    } else {
                                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDieser Clan existiert nicht.");
                                    }
                                } else {
                                    if (ClanAPI.checkClanOffline(Bukkit.getOfflinePlayer(uuid)).equals(args[1])) {
                                        if (!ClanAPI.getLeader(args[1]).equals(Bukkit.getOfflinePlayer(uuid).getName())) {
                                            player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Member §8| §7" + Bukkit.getOfflinePlayer(uuid).getName());
                                        }
                                    }
                                }
                            }
                            for (String uuidStr : section.getKeys(true)) {
                                UUID uuid = UUID.fromString(uuidStr);
                                if (Bukkit.getPlayer(uuid) != null) {
                                    if (ClanAPI.checkClan(Bukkit.getPlayer(uuid)).equals(args[1])) {
                                        if (!ClanAPI.getLeader(args[1]).equals(Bukkit.getPlayer(uuid).getName())) {
                                            if (!Bukkit.getPlayer(uuid).hasPermission("clan.mod")) {
                                                player.sendMessage(firstMCClansPlugin.getPrefix() + "§7Member §8| §7" + Bukkit.getPlayer(uuid).getName());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        player.sendMessage(firstMCClansPlugin.getPrefix() + "§cDieser Clan existiert nicht.");
                    }


            } else {
                player.sendMessage(firstMCClansPlugin.getPrefix() + "§cBenutze: /clan help!");
            }


            return false;
        }
        return false;
    }

}



