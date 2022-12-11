package com.xiaoyu1163.exploding_bow;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.Location;
import org.bukkit.World;

import javax.swing.*;
import java.util.*;
import java.util.Timer;

public class Main extends JavaPlugin implements Listener {
    private Plugin plugin;


    // 儲存玩家是否按住了右鍵
    private boolean isRightClickPressed = false;

    @Override
    public void onLoad(){
        plugin = this;
    }
    @Override
    public void onEnable() {
        getLogger().info("ExplodingBow is Start");

        Bukkit.getPluginManager().registerEvents(new ArrowListener(), this);
        // 註冊箭矢監聽器
        getCommand("summonExplosiveBow").setExecutor(new Exploding_Bow());
        getCommand("summonICE_Walker").setExecutor(new ICE_Walker());



        getServer().getPluginManager().registerEvents(new ShootListener(),this);
        getServer().getPluginManager().registerEvents(new PlayerListener(),this);
        // 註冊事件監聽器
    }


    private class ShootListener implements Listener {
        // 監聽玩家按鍵事件
        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent event) {
            // 檢查玩家按下的按鍵是否為右鍵
            if (event.getAction() == Action.RIGHT_CLICK_AIR ||
                    event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                // 儲存玩家按下了右鍵
                isRightClickPressed = true;

                // 儲存箭矢
                getLogger().info("Right in");
            }


        }

        // 監聽玩家射箭
        @EventHandler
        public void onEntityShootBOW(EntityShootBowEvent e){
            // 檢查射擊者是否為玩家
            if (!(isHuman(e.getEntity()))){
                return;
            }
            Player p = (Player) e.getEntity();
            Arrow arrow = (Arrow) e.getProjectile();
            arrow.remove();
            LetPlayerShootArrow(p);
        }

    }
    private class ArrowListener implements Listener {
        @EventHandler
        public void onEntityShootBow(EntityShootBowEvent e){
            if (!(e.getProjectile() instanceof Arrow)){
                return;
            }
            Arrow arrow = (Arrow) e.getProjectile();
            if (!(isHuman(e.getEntity()))){
                return;
            }
            Player p = (Player) e.getEntity();
            //getLogger().info("Shoot Exploding Arrow");
            ItemStack itemInHand = p.getInventory().getItemInMainHand();
            if (itemInHand == null || itemInHand.getType() != Material.BOW || !itemInHand.getItemMeta().getDisplayName().equals("§c爆炸弓") || !itemInHand.getItemMeta().getLore().equals(new ArrayList<>(
                    Arrays.asList("§f§l效果：","§f於落下位置產生爆炸", "§f還在改進")))) {
                return;
            }
            // 設定箭矢的MetaData

            arrow.setMetadata("ExplodingBow", new FixedMetadataValue(plugin,true));
        }
        // 射出時立刻判斷並賦予箭矢一個Metadata

        @EventHandler
        public void onProjectileHit(ProjectileHitEvent e) {
            if (!(e.getEntity() instanceof Arrow)) {
                return;
            }
            Arrow arrow = (Arrow) e.getEntity();
            // 檢查箭矢的MetaData
            if (!arrow.hasMetadata("ExplodingBow")){
                return;
            }

            Location lc = arrow.getLocation();
            World wrd = lc.getWorld();
            wrd.createExplosion(lc, 4.0f);
            arrow.remove();
        }
        // 射中實體時判斷是否為射出時的Metadata
    }
    // 箭矢監聽器
    private class PlayerListener implements Listener {
        @EventHandler
        public void onPlayerWalk(PlayerMoveEvent e){
            Player p = e.getPlayer();
            ItemStack itemInHand = p.getInventory().getItemInMainHand();
            if (itemInHand == null || itemInHand.getType() != Material.ENDER_PEARL || !itemInHand.getItemMeta().getDisplayName().equals("§b冰霜行者") || !itemInHand.getItemMeta().getLore().equals(new ArrayList<>(
                    Arrays.asList("§f§l效果：","§f於踩踏位置生成冰塊", "§f還在改進")))) {
                return;
            }
            Location lc = new Location(p.getWorld(),p.getLocation().getX(),p.getLocation().getY()-1,p.getLocation().getZ());
            Material bc = lc.getBlock().getRelative(BlockFace.DOWN).getType();
            if (!(bc == Material.AIR)){
                return;
            }
            lc.getBlock().setType(Material.ICE);
        }// 行走事件

        @EventHandler
        public void onEntityDamage(EntityDamageEvent e){
            Entity et = e.getEntity();
            //getLogger().info("Invulnerable");
            et.setInvulnerable(false);
        } // 取消無敵
        @EventHandler
        public void onPlayerAttack(EntityDamageByEntityEvent e){
            if (!(isHuman(e.getDamager()))){
                return;
            }
            //getLogger().info("Damage");

            Player player =(Player) e.getDamager();
            ItemStack item = player.getInventory().getItemInMainHand();

            player.setCooldown(item.getType(),0);

        }// 未成功
    }


    public void LetPlayerShootArrow(Player p){
        Arrow shot = p.launchProjectile(Arrow.class);
        shot.setVelocity(shot.getVelocity().multiply(1.0));
    }//讓玩家射箭
    public static boolean isHuman(Entity e){
        if (e instanceof Player){
            return true;
        }
        return false;
    }// 回傳是否為玩家
}