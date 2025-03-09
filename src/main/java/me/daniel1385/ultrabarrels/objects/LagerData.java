package me.daniel1385.ultrabarrels.objects;

import org.bukkit.inventory.ItemStack;

public class LagerData {
    private ItemStack item;
    private long amount;

    public LagerData(ItemStack item, long amount) {
        this.item = item;
        this.amount = amount;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public long getAmount() {
        return amount;
    }
}
