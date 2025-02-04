package com.dt.lBT.uis;

import com.cryptomorin.xseries.XMaterial;
import com.dt.lBT.Main;
import com.dt.lBT.utils.ConfirmationMenuNoLore;
import com.dt.lBT.utils.ItemBuilder;
import com.dt.lBT.utils.TextHandler;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

public class ResetInterval implements InventoryProvider, Listener {
    @Getter private final SmartInventory smartInventory;
    private static final List<Player> nn = new ArrayList<>();

    public ResetInterval() {
        this.smartInventory = SmartInventory.builder()
                .provider(this)
                .manager(Main.getInstance().getInventoryManager())
                .title(TextHandler.format("&8Edit Reset-Interval"))
                .size(3,9)
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        contents.set(2,0,ClickableItem.of(new ItemBuilder(XMaterial.ARROW,"&cBack To Menu").build(),e->{
            new LBTGUI().getSmartInventory().open(player);
        }));
        contents.fillBorders(ClickableItem.empty(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE,"&7")
                .durability((short) 7).build()));
        contents.set(1,3, ClickableItem.of(new ItemBuilder(XMaterial.CLOCK, "&eSet the time of the reset-interval").build(),e->{
            nn.add(player);
            player.sendMessage(TextHandler.colorize("&6Please enter a number in the chat"));
            this.getSmartInventory().close(player);
        }));
        contents.set(1,5,ClickableItem.of(new ItemBuilder(XMaterial.CLOCK,"&eSet the time unit of the reset-interval").build(),e->{
            new DynamicGUI()
                    .title("&8Set Time Unit Of Reset-Interval")
                    .size(5,9)
                    .init((p,c)->{
                        c.set(4,0,ClickableItem.of(new ItemBuilder(XMaterial.ARROW,"&cBack To Menu").build(),ev->{
                            this.getSmartInventory().open(player);
                        }));
                        c.fillBorders(ClickableItem.empty(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE, "&7").durability((short) 7).build()));
                        c.set(1,4,ClickableItem.empty(new ItemBuilder(XMaterial.GRAY_DYE, "&7Current Time-Unit&8: &6"+Main.getInstance().getSettingsConfig().getConfig().getString("reset-interval.time-unit"))
                                .durability((short) 8)
                                .build()));
                        c.set(3,1,ClickableItem.of(new ItemBuilder(XMaterial.CLOCK,"&cSeconds").build(),es->{
                            Main.getInstance().getSettingsConfig().getConfig().set("reset-interval.time-unit","seconds");
                            Main.getInstance().getSettingsConfig().save();
                            player.sendMessage(TextHandler.colorize("&aTime-Unit has been set to &bSeconds"));
                        }));
                        c.set(3,3,ClickableItem.of(new ItemBuilder(XMaterial.CLOCK,"&cMinutes").build(),es->{
                            Main.getInstance().getSettingsConfig().getConfig().set("reset-interval.time-unit","minutes");
                            Main.getInstance().getSettingsConfig().save();
                            player.sendMessage(TextHandler.colorize("&aTime-Unit has been set to &bMinutes"));
                        }));
                        c.set(3,5,ClickableItem.of(new ItemBuilder(XMaterial.CLOCK,"&cHours").build(),es->{
                            Main.getInstance().getSettingsConfig().getConfig().set("reset-interval.time-unit","hours");
                            Main.getInstance().getSettingsConfig().save();
                            player.sendMessage(TextHandler.colorize("&aTime-Unit has been set to &bHours"));
                        }));
                        c.set(3,7,ClickableItem.of(new ItemBuilder(XMaterial.CLOCK,"&cDays").build(),es->{
                            Main.getInstance().getSettingsConfig().getConfig().set("reset-interval.time-unit","days");
                            Main.getInstance().getSettingsConfig().save();
                            player.sendMessage(TextHandler.colorize("&aTime-Unit has been set to &bDays"));
                        }));
                    }).update((p,c)->{
                        c.set(1,4,ClickableItem.empty(new ItemBuilder(XMaterial.GRAY_DYE, "&7Current Time-Unit&8: &6"+Main.getInstance().getSettingsConfig().getConfig().getString("reset-interval.time-unit"))
                                .durability((short) 8)
                                .build()));
                    }).open(player);
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
    @EventHandler
    public void onchat(AsyncPlayerChatEvent e){
        if (nn.contains(e.getPlayer())){
            e.setCancelled(true);
            try {
                Bukkit.getScheduler().runTask(Main.getInstance(),()->{
                    int number =Integer.parseInt(e.getMessage());
                    ConfirmationMenuNoLore.getConfirmationInventory(()->{
                        Main.getInstance().getSettingsConfig().getConfig().set("reset-interval.time-value", number);
                        Main.getInstance().getSettingsConfig().save();
                        nn.remove(e.getPlayer());
                        e.getPlayer().sendMessage(TextHandler.colorize("&aReset-interval time has been set to &b"+number));
                    },()->{
                        nn.remove(e.getPlayer());
                        this.getSmartInventory().open(e.getPlayer());
                    }).open(e.getPlayer());
                });
            }catch (NumberFormatException exception){
                e.getPlayer().sendMessage(TextHandler.colorize("&cPlease enter a valid number."));
            }
        }
    }
}
