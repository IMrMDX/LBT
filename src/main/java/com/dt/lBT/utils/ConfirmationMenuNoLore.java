package com.dt.lBT.utils;

import com.cryptomorin.xseries.XMaterial;
import com.dt.lBT.Main;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConfirmationMenuNoLore implements InventoryProvider {

    private final Runnable onConfirm;
    private final Runnable onCancel;


    public ConfirmationMenuNoLore(Runnable onConfirm, Runnable onCancel) {
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
    }

    public static SmartInventory getConfirmationInventory(Runnable onConfirm, Runnable onCancel) {
        return SmartInventory.builder()
                .provider(new ConfirmationMenuNoLore(onConfirm, onCancel))
                .size(3, 9)
                .title("§aConfirmation Menu")
                .manager(Main.getInstance().getInventoryManager())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fill(ClickableItem.empty(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE, "§7")
                .durability((short) 7)
                .build()));
        ItemStack confirmItem = new ItemBuilder(XMaterial.GREEN_TERRACOTTA, "§aYes")
                .durability((short) 13)
                .build();
        contents.set(1, 3, ClickableItem.of(confirmItem, e -> {
            player.closeInventory();
            onConfirm.run();
        }));

        ItemStack cancelItem = new ItemBuilder(XMaterial.RED_TERRACOTTA, "§cNo")
                .durability((short) 14)
                .build();
        contents.set(1, 5, ClickableItem.of(cancelItem, e -> {
            player.closeInventory();
            onCancel.run();
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}