package com.xiaoyu1163.exploding_bow;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ICE_Walker implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                ItemStack ice_walker = new ItemStack(Material.ENDER_PEARL, 1);
                ItemMeta Exploding_Bow = ice_walker.getItemMeta();
                Exploding_Bow.setDisplayName("§b冰霜行者");
                Exploding_Bow.setLore(new ArrayList<>(
                        Arrays.asList("§f§l效果：","§f於踩踏位置生成冰塊", "§f還在改進")));
                ice_walker.setItemMeta(Exploding_Bow);

                player.getInventory().addItem(ice_walker);
                return true;
            }
        }
        return false;
    }
}