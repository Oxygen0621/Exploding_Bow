package com.xiaoyu1163.exploding_bow;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventException;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
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
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.*;
import java.util.*;
import java.util.Timer;

public class Main extends JavaPlugin implements Listener {
    private Plugin plugin;
    public static int StoredArrow = 0;
    public int taskID = 0;
    public int taskTimer = 0;
    public boolean TaskIsOnline = false;

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
            if (!(event.getAction() == Action.RIGHT_CLICK_AIR ||
                    event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                return;
            }
            Player p = event.getPlayer();
            Inventory inv = p.getInventory();
            ItemStack itemInMainHand = p.getInventory().getItemInMainHand();
            if (itemInMainHand.getType() != Material.BOW ){
                return;
            }
            getLogger().info("Right in");

            int inv_Amount = 0;
            for (ItemStack i : inv){
                if (i != null &&i.getType() == Material.ARROW){
                    inv_Amount += i.getAmount();
                }
            }
            if (inv_Amount >= 6){
                StoredArrow = 6;
            } else {
                StoredArrow = inv_Amount;
            }
        }

        // 監聽玩家射箭
        @EventHandler
        public void onShoot(EntityShootBowEvent e){

            Player p = (Player) e.getEntity();
            /*
            ItemStack itemInHand = p.getInventory().getItemInMainHand();
            if (itemInHand.getItemMeta().getDisplayName().equals("§c爆炸弓") || itemInHand.getItemMeta().getLore().equals(new ArrayList<>(
                    Arrays.asList("§f§l效果：","§f於落下位置產生爆炸", "§f還在改進")))) {
                return;
            }
             */
            // 檢查射擊者是否為玩家
            if (!(isHuman(e.getEntity()))){
                return;
            }
            if (!(e.getProjectile() instanceof Arrow)){
                return;
            }

            Inventory inv = p.getInventory();
            Arrow arrow = (Arrow) e.getProjectile();
            arrow.remove();


            /*
            for (int i=0;i<StoredArrow;i++){
                if (i==0){
                    LetPlayerShootArrow(p);
                    inv.removeItem(new ItemStack(Material.ARROW,1));
                    i++;
                } // 第一次執行時先執行一次run()，先執行後延遲
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    LetPlayerShootArrow(p);
                    inv.removeItem(new ItemStack(Material.ARROW,1));
                    }
                },20L);
            }
            */ //無法正常使用的代碼
            if (!TaskIsOnline){
                LetPlayerShootArrow(p);
            }
        }

    }
    private class ArrowListener implements Listener {
        @EventHandler
        public void onEntityShootBow(EntityShootBowEvent e){
            //getLogger().info("Exploding Bow");
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
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent e){
            if (!isHuman(e.getPlayer())){
                return;
            }

            Player p = e.getPlayer();
            e.setJoinMessage("§f§l+ &r&f| "+p.getName());
        }// 加入訊息替換
        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent e){
            if (!(isHuman(e.getPlayer()))){
                return;
            }

            Player p = e.getPlayer();
            e.setQuitMessage("§c§l- &r&f| "+p.getName());
        }// 離開訊息替換


    }


    public void LetPlayerShootArrow(Player p) {
        //getLogger().info("LetShoot");
        TaskIsOnline = true;
        Inventory inv = p.getInventory();
        ItemStack itemInHand = p.getInventory().getItemInMainHand();


        taskID = new BukkitRunnable(){
            @Override
            public void run(){
                getLogger().info("taskTimer、StoredArrow:"+taskTimer+","+StoredArrow);
                if (taskTimer>=StoredArrow || p.getInventory().getItemInMainHand().getType() != Material.BOW){
                    TaskIsOnline = false;
                    Bukkit.getScheduler().cancelTask(taskID);
                    taskTimer=0;
                }
                Arrow arrow = p.launchProjectile(Arrow.class);
                if (itemInHand.getItemMeta().getDisplayName().equals("§c爆炸弓") || itemInHand.getItemMeta().getLore() == (new ArrayList<>(
                        Arrays.asList("§f§l效果：","§f於落下位置產生爆炸", "§f還在改進")))) {
                    arrow.setMetadata("ExplodingBow", new FixedMetadataValue(plugin,true));
                }
                arrow.setShooter(p);
                inv.removeItem(new ItemStack(Material.ARROW,1));
                taskTimer++;
            }
        }.runTaskTimer(plugin,0,2).getTaskId();



        /*
        final int[] taskTimes = {0};
        BukkitRunnable ShootTask = new BukkitRunnable() {
            @Override
            public void run() {
                p.launchProjectile(Arrow.class);
                inv.removeItem(new ItemStack(Material.ARROW, 1));
                taskTimes[0]++;
            }
        };

        // 設定延遲並重複執行
            int taskID = ShootTask.runTaskTimer((Plugin) plugin,0,2).getTaskId();
            if (taskID == StoredArrow){
                Bukkit.getScheduler().cancelTask(taskID);
            }

        */
    }//讓玩家射箭
    public static boolean isHuman(Entity e){
        if (e instanceof Player){
            return true;
        }
        return false;
    }// 回傳是否為玩家
}