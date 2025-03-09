package me.daniel1385.ultrabarrels.apis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public abstract class InventoryGUI implements Listener {
	public Inventory inv;

	public InventoryGUI(JavaPlugin plugin, String name, int rows) {
		this.inv = Bukkit.createInventory(null, rows * 9, "§5" + name);
		Bukkit.getPluginManager().registerEvents(this, (Plugin)plugin);
	}

	public void setItem(int index, Material material, String name, String... lore) {
		ItemStack stack = new ItemStack(material);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		stack.setItemMeta(meta);
		setItem(index, stack);
	}

	public void setItem(int index, int amount, Material material, String name, String... lore) {
		ItemStack stack = new ItemStack(material, amount);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		stack.setItemMeta(meta);
		setItem(index, stack);
	}

	public void setItem(int index, Material material, int d, String name, String... lore) {
		ItemStack stack = new ItemStack(material, 1, (short)d);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		stack.setItemMeta(meta);
		setItem(index, stack);
	}

	public void setItem(int index, int amount, Material material, int d, String name, String... lore) {
		ItemStack stack = new ItemStack(material, 1, (short)d);
		stack.setAmount(amount);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		stack.setItemMeta(meta);
		setItem(index, stack);
	}

	public void addItem(Material material, String name, String... lore) {
		ItemStack stack = new ItemStack(material);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		stack.setItemMeta(meta);
		addItem(stack);
	}

	public void setItem(int index, ItemStack stack, String name, String... lore) {
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		stack.setItemMeta(meta);
		this.inv.setItem(index, stack);
	}

	public void setItem(int index, ItemStack stack) {
		this.inv.setItem(index, stack);
	}

	public void addItem(ItemStack stack) {
		this.inv.addItem(new ItemStack[] { stack });
	}

	public void open(Player p) {
		p.openInventory(this.inv);
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getInventory().equals(this.inv)) {
			event.setCancelled(true);
			Inventory click = event.getClickedInventory();
			if (click == null) {
				return;
			}
			if (click.equals(this.inv)) {
				click((Player)event.getWhoClicked(), event.getSlot(), event.getClick().isRightClick());
			}
		}
	}

	public abstract void click(Player paramPlayer, int paramInt, boolean paramBoolean);
}
