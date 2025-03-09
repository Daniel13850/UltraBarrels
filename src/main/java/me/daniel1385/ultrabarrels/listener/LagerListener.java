package me.daniel1385.ultrabarrels.listener;

import me.daniel1385.ultrabarrels.UltraBarrels;
import me.daniel1385.ultrabarrels.guis.UnendlichesLagerGUI;
import me.daniel1385.ultrabarrels.objects.LagerData;
import org.bukkit.Bukkit;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            if(plugin.getLager(b.getLocation()) != null) {
                remove.add(b);
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
            if(plugin.getLager(b.getLocation()) != null) {
                remove.add(b);
            }
        }
        for(Block b : remove) {
            event.blockList().remove(b);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if(event.getBlock().getType().equals(Material.BARREL)) {
            if(event.getItemInHand().getType().equals(Material.BARREL) && event.getPlayer().isSneaking()) {
                plugin.initLager(event.getBlock().getLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInvMove(InventoryMoveItemEvent event) {
        LagerData source = null;
        LagerData target = null;
        if(event.getSource().getHolder() instanceof Barrel barrel) {
            source = plugin.getLager(barrel.getLocation());
        }
        if(event.getDestination().getHolder() instanceof Barrel barrel) {
            target = plugin.getLager(barrel.getLocation());
        }

        if(source != null && target != null) {
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
            if(checkInvSpace(event.getItem(), event.getDestination().getStorageContents()) >= event.getItem().getAmount()) {
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        plugin.getLager(event.getSource().getLocation());
                    }
                });
            }
        } else if(target != null) {
            if(target.getItem() != null) {
                if(!target.getItem().isSimilar(event.getItem())) {
                    event.setCancelled(true);
                    return;
                }
            }
            if(checkInvItems(event.getItem(), event.getSource().getStorageContents()) >= event.getItem().getAmount()) {
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        plugin.getLager(event.getDestination().getLocation());
                    }
                });
            }
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
        Block block = event.getClickedBlock();
        LagerData data = plugin.getLager(block.getLocation());
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
        LagerData data = plugin.getLager(event.getBlock().getLocation());
        if(data != null) {
            long amount = data.getAmount();
            if(amount > 0) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(plugin.getPrefix() + "§cDu darfst diesen Block erst abbauen, wenn er leer ist!");
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
