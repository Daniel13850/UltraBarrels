package me.daniel1385.ultrabarrels.guis;

import me.daniel1385.ultrabarrels.UltraBarrels;
import me.daniel1385.ultrabarrels.apis.InventoryGUI;
import me.daniel1385.ultrabarrels.events.LagerUpdateEvent;
import me.daniel1385.ultrabarrels.objects.LagerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class UnendlichesLagerGUI extends InventoryGUI {
    private Location loc;
    private UltraBarrels plugin;

    public UnendlichesLagerGUI(UltraBarrels plugin, Location loc, LagerData data) {
        super(plugin, "Unendliches Lager", 4);
        this.loc = loc;
        this.plugin = plugin;
        updateGUI(data);
    }

    private void updateGUI(LagerData data) {
        inv.clear();
        ItemStack item = data.getItem();
        long amount = data.getAmount();
        if(item == null) {
            setItem(4, new ItemStack(Material.BARRIER), "§aKlicke auf ein Item, um es dem Lager hinzuzufügen!");
        } else {
            setItem(4, item);
        }
        long wert = amount;
        if(wert > 999999999) {
            wert = 999999999;
        }
        long a = wert / 100000000;
        wert -= a*100000000;
        long b = wert / 10000000;
        wert -= b*10000000;
        long c = wert / 1000000;
        wert -= c*1000000;
        long d = wert / 100000;
        wert -= d*100000;
        long e = wert / 10000;
        wert -= e*10000;
        long f = wert / 1000;
        wert -= f*1000;
        long g = wert / 100;
        wert -= g*100;
        long h = wert / 10;
        wert -= h*10;
        long i = wert;
        setItem(9, Integer.max((int) a, 1), a == 0 ? Material.WHITE_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, "§7Anzahl: §6" + amount);
        setItem(10, Integer.max((int) b, 1), b == 0 ? Material.WHITE_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, "§7Anzahl: §6" + amount);
        setItem(11, Integer.max((int) c, 1), c == 0 ? Material.WHITE_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, "§7Anzahl: §6" + amount);
        setItem(12, Integer.max((int) d, 1), d == 0 ? Material.WHITE_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, "§7Anzahl: §6" + amount);
        setItem(13, Integer.max((int) e, 1), e == 0 ? Material.WHITE_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, "§7Anzahl: §6" + amount);
        setItem(14, Integer.max((int) f, 1), f == 0 ? Material.WHITE_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, "§7Anzahl: §6" + amount);
        setItem(15, Integer.max((int) g, 1), g == 0 ? Material.WHITE_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, "§7Anzahl: §6" + amount);
        setItem(16, Integer.max((int) h, 1), h == 0 ? Material.WHITE_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, "§7Anzahl: §6" + amount);
        setItem(17, Integer.max((int) i, 1), i == 0 ? Material.WHITE_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS_PANE, "§7Anzahl: §6" + amount);
        setItem(19, Material.RED_STAINED_GLASS_PANE, 1, "§c1 abholen", "§7Anzahl: §6" + amount);
        setItem(20, Material.RED_STAINED_GLASS_PANE, 2, "§c2 abholen", "§7Anzahl: §6" + amount);
        setItem(21, Material.RED_STAINED_GLASS_PANE, 4, "§c4 abholen", "§7Anzahl: §6" + amount);
        setItem(22, Material.RED_STAINED_GLASS_PANE, 8, "§c8 abholen", "§7Anzahl: §6" + amount);
        setItem(23, Material.RED_STAINED_GLASS_PANE, 16, "§c16 abholen", "§7Anzahl: §6" + amount);
        setItem(24, Material.RED_STAINED_GLASS_PANE, 32, "§c32 abholen", "§7Anzahl: §6" + amount);
        setItem(25, Material.RED_STAINED_GLASS_PANE, 64, "§c64 abholen", "§7Anzahl: §6" + amount);
        setItem(31, Material.LIME_STAINED_GLASS_PANE, "§aItems hinzufügen", "§7Anzahl: §6" + amount, "§7Linksklick: §fStack hinzufügen", "§7Rechtsklick: §f1 Item hinzufügen");
    }

    @EventHandler
    public void onUpdate(LagerUpdateEvent event) {
        if(!event.getBarrel().getLocation().equals(loc)) {
            return;
        }
        updateGUI(event.getData());
    }

    @Override
    public void click(Player p, int id, boolean paramBoolean) {
        Block block = loc.getBlock();
        if (!block.getType().equals(Material.BARREL)) {
            p.closeInventory();
            return;
        }
        Barrel barrel = (Barrel) block.getState();
        if(plugin.getLager(barrel) == null) {
            p.closeInventory();
            return;
        }
        int anzahl = 0;
        if(id >= 19 && id <= 25) {
            if(id == 19) {
                anzahl = 1;
            }
            if(id == 20) {
                anzahl = 2;
            }
            if(id == 21) {
                anzahl = 4;
            }
            if(id == 22) {
                anzahl = 8;
            }
            if(id == 23) {
                anzahl = 16;
            }
            if(id == 24) {
                anzahl = 32;
            }
            if(id == 25) {
                anzahl = 64;
            }
            List<ItemStack> stack = plugin.removeLager(barrel, anzahl);
            if(stack == null) {
                p.sendMessage(plugin.getPrefix() + "§cEs sind nicht genügend Items vorhanden!");
            } else {
                if(checkInvSpace(stack.get(0), p.getInventory().getStorageContents()) < anzahl) {
                    p.sendMessage(plugin.getPrefix() + "§cDein Inventar ist voll!");
                    for(ItemStack item : stack) {
                        plugin.addLager(barrel, item);
                    }
                    return;
                } else {
                    for(ItemStack item : stack) {
                        p.getInventory().addItem(item);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClickInv(InventoryClickEvent event) {
        if (event.getInventory().equals(super.inv)) {
            event.setCancelled(true);
            Inventory click = event.getClickedInventory();
            if (click == null) {
                return;
            }
            if (click.equals(event.getWhoClicked().getInventory())) {
                Block block = loc.getBlock();
                if (!block.getType().equals(Material.BARREL)) {
                    event.getWhoClicked().closeInventory();
                    return;
                }
                Barrel barrel = (Barrel) block.getState();
                if(plugin.getLager(barrel) == null) {
                    event.getWhoClicked().closeInventory();
                    return;
                }
                if(event.getCurrentItem() == null) {
                    return;
                }
                if(event.isLeftClick()) {
                    if (plugin.addLager(barrel, new ItemStack(event.getCurrentItem()))) {
                        event.getCurrentItem().setAmount(0);
                    } else {
                        event.getWhoClicked().sendMessage(plugin.getPrefix() + "§cDu kannst nur das selbe Item hinzufügen!");
                    }
                } else if(event.isRightClick()) {
                    ItemStack neu = new ItemStack(event.getCurrentItem());
                    neu.setAmount(1);
                    if (plugin.addLager(barrel, neu)) {
                        event.getCurrentItem().setAmount(event.getCurrentItem().getAmount()-1);
                    } else {
                        event.getWhoClicked().sendMessage(plugin.getPrefix() + "§cDu kannst nur das selbe Item hinzufügen!");
                    }
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
                continue;
            }
        }
        return space;
    }
}
