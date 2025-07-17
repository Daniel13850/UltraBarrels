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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class UnendlichesLagerGUI extends InventoryGUI {
    private Location loc;
    private UltraBarrels plugin;

    public UnendlichesLagerGUI(UltraBarrels plugin, Location loc, LagerData data) {
        super(plugin, "Unendliches Lager", 3);
        this.loc = loc;
        this.plugin = plugin;
        updateGUI(data);
    }

    private void updateGUI(LagerData data) {
        inv.clear();
        ItemStack item = data.getItem();
        long amount = data.getAmount();
        if(item == null) {
            setItem(13, new ItemStack(Material.BARRIER), "§aKlicke auf ein Item, um es dem Lager hinzuzufügen!");
        } else {
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
            ItemStack aa = new ItemStack(item);
            aa.setAmount((int) a);
            setItem(0, aa);
            ItemStack bb = new ItemStack(item);
            bb.setAmount((int) b);
            setItem(1, bb);
            ItemStack cc = new ItemStack(item);
            cc.setAmount((int) c);
            setItem(2, cc);
            ItemStack dd = new ItemStack(item);
            dd.setAmount((int) d);
            setItem(3, dd);
            ItemStack ee = new ItemStack(item);
            ee.setAmount((int) e);
            setItem(4, ee);
            ItemStack ff = new ItemStack(item);
            ff.setAmount((int) f);
            setItem(5, ff);
            ItemStack gg = new ItemStack(item);
            gg.setAmount((int) g);
            setItem(6, gg);
            ItemStack hh = new ItemStack(item);
            hh.setAmount((int) h);
            setItem(7, hh);
            ItemStack ii = new ItemStack(item);
            ii.setAmount((int) i);
            setItem(8, ii);
            ItemStack stack = new ItemStack(item);
            stack.setAmount(1);
            setItem(10, stack, "§c1 abholen", "§7Anzahl: " + amount);
            stack = new ItemStack(item);
            stack.setAmount(2);
            setItem(11, stack, "§c2 abholen", "§7Anzahl: " + amount);
            stack = new ItemStack(item);
            stack.setAmount(4);
            setItem(12, stack, "§c4 abholen", "§7Anzahl: " + amount);
            stack = new ItemStack(item);
            stack.setAmount(8);
            setItem(13, stack, "§c8 abholen", "§7Anzahl: " + amount);
            stack = new ItemStack(item);
            stack.setAmount(16);
            setItem(14, stack, "§c16 abholen", "§7Anzahl: " + amount);
            stack = new ItemStack(item);
            stack.setAmount(32);
            setItem(15, stack, "§c32 abholen", "§7Anzahl: " + amount);
            stack = new ItemStack(item);
            stack.setAmount(64);
            setItem(16, stack, "§c64 abholen", "§7Anzahl: " + amount);
            stack = new ItemStack(item);
            stack.setAmount(1);
            setItem(22, stack, "§aItems hinzufügen", "§7Anzahl: " + amount, "§7Linksklick: Stack hinzufügen", "§7Rechtsklick: 1 Item hinzufügen");
        }
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
        if(id >= 10 && id <= 16) {
            if(id == 10) {
                anzahl = 1;
            }
            if(id == 11) {
                anzahl = 2;
            }
            if(id == 12) {
                anzahl = 4;
            }
            if(id == 13) {
                anzahl = 8;
            }
            if(id == 14) {
                anzahl = 16;
            }
            if(id == 15) {
                anzahl = 32;
            }
            if(id == 16) {
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
        if (event.getInventory().equals(this.inv)) {
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
