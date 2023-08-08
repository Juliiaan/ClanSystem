package org.banger.util;


import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ItemBuilder {

    private ItemStack item;

    private ItemMeta itemMeta;

    private SkullMeta skullMeta;

    private ArrayList<String> lore = new ArrayList<String>();

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.itemMeta = this.item.getItemMeta();
    }

    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
        this.itemMeta = this.item.getItemMeta();
    }

    public ItemBuilder displayname(String name) {
        this.itemMeta.setDisplayName(name);
        return this;
    }

    public ItemBuilder owningPlayer(OfflinePlayer player) {
        this.skullMeta = (SkullMeta)this.itemMeta;
        this.skullMeta.setOwningPlayer(player);
        this.itemMeta = (ItemMeta)this.skullMeta;
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        this.itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder itemFlags(ItemFlag... itemFlag) {
        this.itemMeta.addItemFlags(itemFlag);
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        this.itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemStack build() {
        this.item.setItemMeta(this.itemMeta);
        return this.item;
    }

}