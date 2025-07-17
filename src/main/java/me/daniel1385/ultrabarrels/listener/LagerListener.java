package me.daniel1385.ultrabarrels.listener;

import me.daniel1385.ultrabarrels.UltraBarrels;
import me.daniel1385.ultrabarrels.guis.UnendlichesLagerGUI;
import me.daniel1385.ultrabarrels.objects.LagerData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LagerListener implements Listener {
    private Map<Location, UnendlichesLagerGUI> guis = new HashMap<>();
    private UltraBarrels plugin;

    public LagerListener(UltraBarrels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockExplode(BlockExplodeEvent event) {
        List<Block> remove = new ArrayList<>();
        for(Block b : event.blockList()) {
            if(b.getType().equals(Material.BARREL)) {
                Barrel barrel = (Barrel) b.getState();
                if (plugin.getLager(barrel) != null) {
                    remove.add(b);
                }
            }
        }
        for(Block b : remove) {
            event.blockList().remove(b);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> remove = new ArrayList<>();
        for(Block b : event.blockList()) {
            if(b.getType().equals(Material.BARREL)) {
                Barrel barrel = (Barrel) b.getState();
                if (plugin.getLager(barrel) != null) {
                    remove.add(b);
                }
            }
        }
        for(Block b : remove) {
            event.blockList().remove(b);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if(event.getBlock().getType().equals(Material.BARREL)) {
            if(plugin.isLager(event.getItemInHand())) {
                Barrel barrel = (Barrel) event.getBlock().getState();
                plugin.initLager(barrel);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInvMove(InventoryMoveItemEvent event) {
        LagerData source = null;
        LagerData target = null;
        if(event.getSource().getHolder() instanceof Barrel barrel) {
            source = plugin.getLager(barrel);
        }
        if(event.getDestination().getHolder() instanceof Barrel barrel) {
            target = plugin.getLager(barrel);
        }

        if(source != null && target != null) { // sollte eig nicht passieren
            event.setCancelled(true);
        } else if(source != null) {
            if(source.getItem() == null) {
                event.setCancelled(true);
                return;
            }
            if(!source.getItem().isSimilar(event.getItem())) {
                event.setCancelled(true);
                return;
            }
            if(source.getAmount() < event.getItem().getAmount()) {
                event.setCancelled(true);
                return;
            }
            Location loc = event.getSource().getLocation();
            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if(loc.getBlock().getState() instanceof Barrel barrel) {
                        plugin.getLager(barrel); // Update NBT-Daten
                    }
                }
            });
        } else if(target != null) {
            if(target.getItem() != null) {
                if(!target.getItem().isSimilar(event.getItem())) {
                    event.setCancelled(true);
                    return;
                }
            }
            Location loc = event.getDestination().getLocation();
            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if(loc.getBlock().getState() instanceof Barrel barrel) {
                        plugin.getLager(barrel); // Update NBT-Daten
                    }
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if(!event.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }
        if(event.getItem() != null && event.getPlayer().isSneaking()) {
            return;
        }
        if(!event.getClickedBlock().getType().equals(Material.BARREL)) {
            return;
        }
        Barrel barrel = (Barrel) event.getClickedBlock().getState();
        LagerData data = plugin.getLager(barrel);
        if(data != null) {
            event.setCancelled(true);
            if(!guis.containsKey(event.getClickedBlock().getLocation())) {
                UnendlichesLagerGUI gui = new UnendlichesLagerGUI(plugin, event.getClickedBlock().getLocation(), data);
                guis.put(event.getClickedBlock().getLocation(), gui);
            }
            guis.get(event.getClickedBlock().getLocation()).open(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if(!event.getBlock().getType().equals(Material.BARREL)) {
            return;
        }
        Barrel barrel = (Barrel) event.getBlock().getState();
        LagerData data = plugin.getLager(barrel);
        if(data != null) {
            long amount = data.getAmount();
            if(amount > 0) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(plugin.getPrefix() + "§cDu darfst diesen Block erst abbauen, wenn er leer ist!");
            } else {
                event.setDropItems(false);
                if(event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
                    event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), plugin.getLagerItem());
                }
            }
        }
    }

    private int checkInvSpace(ItemStack stack, ItemStack[] inv) {
        int space = 0;
        for(ItemStack item : inv) {
            if(item == null) {
                space += stack.getMaxStackSize();
                continue;
            }
            if(item.isSimilar(stack)) {
                space += stack.getMaxStackSize()-item.getAmount();
            }
        }
        return space;
    }

    private int checkInvItems(ItemStack stack, ItemStack[] inv) {
        int space = 0;
        for(ItemStack item : inv) {
            if(item == null) {
                continue;
            }
            if(item.isSimilar(stack)) {
                space += item.getAmount();
                continue;
            }
        }
        return space;
    }

}
