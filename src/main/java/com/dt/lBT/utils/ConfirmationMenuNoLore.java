package com.dt.guildGate.manager;

import com.dt.guildGate.Main;
import com.dt.guildGate.utils.ItemBuilder;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

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
        contents.fill(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE, "§7").build()));
        ItemStack confirmItem = new ItemBuilder(Material.GREEN_TERRACOTTA, "§aYes").build();
        contents.set(1, 3, ClickableItem.of(confirmItem, e -> {
            player.closeInventory();
            onConfirm.run();
        }));

        ItemStack cancelItem = new ItemBuilder(Material.RED_TERRACOTTA, "§cNo").build();
        contents.set(1, 5, ClickableItem.of(cancelItem, e -> {
            player.closeInventory();
            onCancel.run();
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {


    }
}