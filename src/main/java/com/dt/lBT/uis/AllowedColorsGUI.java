package com.dt.lBT.uis;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.dt.lBT.Main;
import com.dt.lBT.utils.ItemBuilder;
import com.dt.lBT.utils.TextHandler;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


@Getter
public class AllowedColorsGUI implements InventoryProvider, Listener {
    private final SmartInventory smartInventory;
    public static Set<Player> playersAddingColor = new HashSet<>();
    private static final Map<Player, BukkitRunnable> runningTasks = new HashMap<>();

    public AllowedColorsGUI() {
        this.smartInventory = SmartInventory.builder()
                .manager(Main.getInstance().getInventoryManager())
                .provider(this)
                .size(6, 9)
                .title(TextHandler.colorize("&8Colors"))
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {


        ConfigurationSection section = Main.getInstance().getSettingsConfig().getConfig().getConfigurationSection("allowed-colors");

        contents.fillBorders(ClickableItem.empty(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE, "&7")
                .durability((short) 7)
                .build()));

        contents.set(5, 4, ClickableItem.of(new ItemBuilder(XMaterial.GRAY_DYE, "&aAdd LuckyBlock Color")
                .durability((short) 7)
                .build(), e -> {
            playersAddingColor.add(player);
            smartInventory.close(player);
            player.sendMessage(TextHandler.colorize("&ePlease enter a valid name for a lucky block"));
        }));

        contents.set(5, 0, ClickableItem.of(new ItemBuilder(XMaterial.ARROW, "&cBack To Menu").build(), e -> {
            new LBTGUI().getSmartInventory().open(player);
        }));

        if (section != null && !section.getKeys(false).isEmpty()) {
            Set<String> allowedColors = section.getKeys(false);


            BukkitRunnable task = new BukkitRunnable() {
                final Iterator<String> iterator = allowedColors.iterator();
                int currentRow = 1;
                int currentColumn = 1;

                @Override
                public void run() {
                    if (!iterator.hasNext() || currentRow > 4) {
                        this.cancel();
                        runningTasks.remove(player);
                        return;
                    }
                    String color = iterator.next();
                    ItemStack colorItem = new ItemBuilder(XMaterial.ARMOR_STAND)
                            .displayname("&7Color&8: &6" + color)
                            .build();

                    ClickableItem clickableItem = ClickableItem.of(colorItem, e -> {
                        if (e.isLeftClick()) {
                            Main.getInstance().getSettingsConfig().getConfig().set("allowed-colors." + color, null);
                            Main.getInstance().getSettingsConfig().save();
                            player.sendMessage(TextHandler.colorize("&aColor &b" + color + " &ahas been removed."));
                            smartInventory.open(player);
                        }
                    });

                    contents.set(currentRow, currentColumn, clickableItem);
                    player.playSound(player.getLocation(), XSound.ENTITY_SLIME_SQUISH_SMALL.parseSound(), 10, 0.5f);

                    currentColumn++;
                    if (currentColumn >= 8) {
                        currentColumn = 1;
                        currentRow++;
                    }
                }
            };

            task.runTaskTimer(Main.getInstance(), 0L, 6L);
            runningTasks.put(player, task);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String invTitle = event.getView().getTitle();

        if (invTitle.equals(TextHandler.colorize("&8Colors"))) {
            if (runningTasks.containsKey(player)) {
                runningTasks.get(player).cancel();
                runningTasks.remove(player);
            }
        }
    }
    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
