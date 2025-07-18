package me.daniel1385.ultrabarrels;

import me.daniel1385.ultrabarrels.events.LagerUpdateEvent;
import me.daniel1385.ultrabarrels.listener.LagerListener;
import me.daniel1385.ultrabarrels.objects.LagerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UltraBarrels extends JavaPlugin {
    private String prefix;
    private NamespacedKey keyRecipe;

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig();
        if(!config.contains("prefix")) {
            config.set("prefix", "&7[&9UltraBarrels&7] ");
        }
        saveConfig();
        prefix = translateAllCodes(config.getString("prefix")) + "§r";
        Bukkit.getPluginManager().registerEvents(new LagerListener(this), this);
        keyRecipe = new NamespacedKey(this, "lager");
        ShapedRecipe recipe = new ShapedRecipe(keyRecipe, getLagerItem());
        recipe.shape("ABA", "CDC", "AEA");
        recipe.setIngredient('A', Material.OAK_LOG);
        recipe.setIngredient('B', Material.HOPPER);
        recipe.setIngredient('C', Material.GOLD_BLOCK);
        recipe.setIngredient('D', Material.BARREL);
        recipe.setIngredient('E', Material.IRON_BLOCK);
        Bukkit.addRecipe(recipe);
    }

    @Override
    public void onDisable() {
        Bukkit.removeRecipe(keyRecipe);
    }

    public String getPrefix() {
        return prefix;
    }

    private String translateHexCodes (String text) {
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(text);

        while(matcher.find()) {
            net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.of(text.substring(matcher.start()+1, matcher.end()));
            text = text.replace(text.substring(matcher.start(), matcher.end()), color.toString());
            matcher = pattern.matcher(text);
        }

        return text;
    }

    private String translateAllCodes (String text) {
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', translateHexCodes(text));
    }

    public void update(Barrel barrel, LagerData data) {
        PersistentDataContainer cont = barrel.getPersistentDataContainer();
        NamespacedKey keyAmount = new NamespacedKey(this, "amount");
        NamespacedKey keyItem = new NamespacedKey(this, "item");
        barrel.getSnapshotInventory().clear();
        if(data.getAmount() > 0 && data.getItem() != null) {
            ItemStack one = new ItemStack(data.getItem());
            one.setAmount(1);
            data.setItem(one);
            String s;
            try {
                s = toString(one);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            barrel.getSnapshotInventory().setItem(0, one); // Platziere ein Exemplar als Dummy-Item im Fass, damit Trichter und Komperatoren funktionieren
            cont.set(keyItem, PersistentDataType.STRING, s);
            cont.set(keyAmount, PersistentDataType.LONG, data.getAmount());
        } else {
            cont.remove(keyItem);
            cont.set(keyAmount, PersistentDataType.LONG, 0L);
            data.setAmount(0);
            data.setItem(null);
        }
        barrel.update();
        Bukkit.getPluginManager().callEvent(new LagerUpdateEvent(barrel, data));
        return;
    }

    public LagerData getLager(Barrel barrel) {
        PersistentDataContainer cont = barrel.getPersistentDataContainer();
        NamespacedKey keyAmount = new NamespacedKey(this, "amount");
        NamespacedKey keyItem = new NamespacedKey(this, "item");
        if(cont.has(keyAmount, PersistentDataType.LONG)) {
            ItemStack item;
            try {
                String s = cont.get(keyItem, PersistentDataType.STRING);
                if(s != null) {
                    item = fromString(s);
                } else {
                    item = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            long value = cont.get(keyAmount, PersistentDataType.LONG);
            int update = 0;
            if(item != null) {
                int amount = checkInvItems(item, barrel.getSnapshotInventory().getStorageContents());
                if(amount == 0) {
                    update = -1;
                } else {
                    update = amount-1;
                }
            } else {
                ItemStack in = barrel.getSnapshotInventory().getItem(0);
                if(in != null) {
                    item = new ItemStack(in);
                    update = checkInvItems(item, barrel.getSnapshotInventory().getStorageContents());
                }
            }
            if(update != 0) {
                LagerData data = new LagerData(item, value+update);
                update(barrel, data);
                return getLager(barrel);
            } else {
                return new LagerData(item, value);
            }
        } else {
            return null;
        }
    }

    public ItemStack getLagerItem() {
        ItemStack lager = new ItemStack(Material.BARREL);
        ItemMeta meta = lager.getItemMeta();
        meta.setDisplayName("§6Unendliches Lager");
        meta.setLore(Arrays.asList("§7Lagere unendliche Mengen eines Items in diesem Lager."));
        PersistentDataContainer cont = meta.getPersistentDataContainer();
        NamespacedKey keylager = new NamespacedKey(this, "lager");
        cont.set(keylager, PersistentDataType.BOOLEAN, true);
        lager.setItemMeta(meta);
        return lager;
    }

    public boolean isLager(ItemStack item) {
        if(!item.getType().equals(Material.BARREL)) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer cont = meta.getPersistentDataContainer();
        NamespacedKey keylager = new NamespacedKey(this, "lager");
        if(cont.has(keylager, PersistentDataType.BOOLEAN)) {
            return cont.get(keylager, PersistentDataType.BOOLEAN);
        } else {
            return false;
        }
    }

    public void initLager(Barrel barrel) {
        update(barrel, new LagerData(null, 0));
    }

    public boolean addLager(Barrel barrel, ItemStack item, int i) {
        LagerData data = getLager(barrel);
        if(data == null) {
            return false;
        }
        if(data.getItem() != null) {
            if(!item.isSimilar(data.getItem())) {
                return false;
            }
            update(barrel, new LagerData(item, data.getAmount()+i));
        } else {
            update(barrel, new LagerData(item, i));
        }
        return true;
    }

    public boolean removeLager(Barrel barrel, ItemStack item, int i) {
        LagerData data = getLager(barrel);
        if(data == null) {
            return false;
        }
        if(data.getItem() == null) {
            return false;
        }
        if(!item.isSimilar(data.getItem())) {
            return false;
        }
        if(data.getAmount() < i) {
            return false;
        }
        update(barrel, new LagerData(item, data.getAmount()-i));
        return true;
    }

    public boolean addLager(Barrel barrel, ItemStack item) {
        return addLager(barrel, item, item.getAmount());
    }

    public List<ItemStack> removeLager(Barrel barrel, int i) {
        LagerData data = getLager(barrel);
        if(data == null) {
            return null;
        }
        if(data.getItem() == null) {
            return null;
        }
        if(data.getAmount() < i) {
            return null;
        }
        ItemStack item = data.getItem();
        update(barrel, new LagerData(item, data.getAmount()-i));
        List<ItemStack> result = new ArrayList<>();
        int rest = i;
        while(rest > 0) {
            ItemStack item2 = new ItemStack(item);
            item2.setAmount(Integer.min(item.getMaxStackSize(), rest));
            result.add(item2);
            rest -= item2.getAmount();
        }
        return result;
    }

    private String toString(ItemStack stack) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BukkitObjectOutputStream bukkitOut = new BukkitObjectOutputStream(out);
        bukkitOut.writeObject(stack);
        bukkitOut.close();
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    private ItemStack fromString(String string) throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(string));
        BukkitObjectInputStream bukkitIn = new BukkitObjectInputStream(in);
        ItemStack result = (ItemStack)bukkitIn.readObject();
        bukkitIn.close();
        return result;
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
