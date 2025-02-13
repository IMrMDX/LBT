package com.dt.lBT.uis;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.dt.lBT.Main;
import com.dt.lBT.utils.ConfirmationMenuNoLore;
import com.dt.lBT.utils.ItemBuilder;
import com.dt.lBT.utils.TextHandler;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import me.DenBeKKer.ntdLuckyBlock.LBMain;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

@Getter
public class LBTGUI implements InventoryProvider , Listener {
    private final SmartInventory smartInventory;
    private static final List<Player> nn = new ArrayList<>();
    private static final Map<Player, String> waitingForPermissionChange = new HashMap<>();
    private static final Map<Player, String> waitingForLimitChange = new HashMap<>();

    public LBTGUI() {
        this.smartInventory = SmartInventory.builder()
                .provider(this)
                .manager(Main.getInstance().getInventoryManager())
                .title(TextHandler.format("&8Edit LBT Config"))
                .size(5,9)
                .build();
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE,"&7")
                .durability((short) 7)
                .build()));
        contents.set(2, 2, ClickableItem.of(new ItemBuilder(XMaterial.CLOCK, "&eGive Timer").build(), e -> {
            nn.add(player);
            player.sendMessage(TextHandler.colorize("&6Please enter a number in the chat"));
            this.getSmartInventory().close(player);
        }));
        contents.set(2, 3, ClickableItem.of(new ItemBuilder(XMaterial.BARRIER, "&eAllowed Colors").build(), e -> new AllowedColorsGUI().getSmartInventory().open(player)));
        boolean nb = Main.getInstance().getSettingsConfig().getConfig().getBoolean("limits-enabled");
        XMaterial glassMaterial = nb ? XMaterial.LIME_STAINED_GLASS : XMaterial.RED_STAINED_GLASS;
        String glassName = nb ? "&aLimits Enabled" : "&cLimits Disabled";
        short durability = nb ? (short) 5 : (short) 14;

        contents.set(2, 4, ClickableItem.of(new ItemBuilder(glassMaterial, glassName)
                .durability(durability)
                .build(), e -> {
            boolean newValue = !nb;
            Main.getInstance().getSettingsConfig().getConfig().set("limits-enabled", newValue);
            Main.getInstance().getSettingsConfig().save();

            String message = newValue ? "&aLimits have been enabled!" : "&cLimits have been disabled!";
            player.sendMessage(TextHandler.colorize(message));

            this.getSmartInventory().open(player);
        }));

        contents.set(2,5,ClickableItem.of(new ItemBuilder(XMaterial.ANVIL, "&eReset Interval").build(),e-> new ResetInterval().getSmartInventory().open(player)));
        contents.set(2,6,ClickableItem.of(new ItemBuilder(XMaterial.NAME_TAG,"&eLimits").build(),e->{
            FileConfiguration config = Main.getInstance().getSettingsConfig().getConfig();
            new DynamicGUI()
                    .title("&8Limits Configuration")
                    .size(4, 9)
                    .init((p, c) -> {
                        Map<String, Object> limits = Objects.requireNonNull(config.getConfigurationSection("limits")).getValues(false);
                        int row = 1;
                        int col = 1;

                        for (String group : limits.keySet()) {

                            c.fillBorders(ClickableItem.empty(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE,"&7")
                                    .durability((short) 7).build()));
                            c.set(row, col, ClickableItem.of(new ItemBuilder(XMaterial.LIME_DYE, "&e" + group.toUpperCase())
                                            .durability((short) 10)
                                            .lore("&7","&7LeftClick to set the daily limit"
                                            ,"&7RightClick to set the permission of the group")
                                    .build(), es -> {
                                if (es.isLeftClick()){
                                    waitingForLimitChange.put(player, group);
                                    player.closeInventory();
                                    player.sendMessage(TextHandler.colorize("&6Please enter a valid number in the chat"));
                                }

                                if (es.isRightClick()){
                                    waitingForPermissionChange.put(player, group);
                                    player.sendMessage(TextHandler.colorize("&6Please enter the permission name in the chat"));
                                    this.getSmartInventory().close(player);
                                }
                            }));
                            c.set(3,0,ClickableItem.of(new ItemBuilder(XMaterial.ARROW,"&cBack To Menu").build(),ev->{
                                new LBTGUI().getSmartInventory().open(player);
                            }));
                            col++;
                            if (col >= 9) {
                                col = 0;
                                row++;
                            }
                        }
                    })
                    .update((p, c) -> {
                    })
                    .open(player);
        }));



    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }


    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (nn.contains(player)) {
            e.setCancelled(true);
            try {
                int number = Integer.parseInt(e.getMessage());

                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    ConfirmationMenuNoLore.getConfirmationInventory(() -> {
                        Main.getInstance().getSettingsConfig().getConfig().set("givetimer", number);
                        Main.getInstance().getSettingsConfig().save();
                        player.sendMessage(TextHandler.colorize("&aGiveTimer time has been set to &b" + number));
                        assert XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound() != null;
                        player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1, 1);
                        this.getSmartInventory().open(player);
                        nn.remove(player);
                    }, () -> {
                        this.getSmartInventory().open(player);
                        nn.remove(player);
                    }).open(player);
                });
            } catch (NumberFormatException ex) {
                player.sendMessage(TextHandler.colorize("&cPlease enter a valid number."));
            }
        }
        if (waitingForPermissionChange.containsKey(player)) {
            e.setCancelled(true);

            String group = waitingForPermissionChange.get(player);
            String newPermission = e.getMessage();

            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                FileConfiguration config = Main.getInstance().getSettingsConfig().getConfig();
                if (Objects.requireNonNull(config.getConfigurationSection("limits")).contains(group)) {
                    Objects.requireNonNull(Objects.requireNonNull(config.getConfigurationSection("limits")).getConfigurationSection(group)).set("permission", newPermission);
                    Main.getInstance().getSettingsConfig().save();
                    player.sendMessage(TextHandler.colorize("&aPermission for group " + group + " has been set to: " + newPermission));
                } else {
                    player.sendMessage(TextHandler.colorize("&cGroup " + group + " does not exist."));
                }
                waitingForPermissionChange.remove(player);
            });
        }
        if (waitingForLimitChange.containsKey(player)) {
            e.setCancelled(true);

            String group = waitingForLimitChange.get(player);
            String input = e.getMessage();

            try {
                int newLimit = Integer.parseInt(input);

                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    FileConfiguration config = Main.getInstance().getSettingsConfig().getConfig();
                    if (Objects.requireNonNull(config.getConfigurationSection("limits")).contains(group)) {
                        Objects.requireNonNull(Objects.requireNonNull(config.getConfigurationSection("limits")).getConfigurationSection(group)).set("dailyLimit", newLimit);
                        Main.getInstance().getSettingsConfig().save();
                        player.sendMessage(TextHandler.colorize("&aDaily limit for group " + group + " has been set to: " + newLimit));
                    } else {
                        player.sendMessage(TextHandler.colorize("&cGroup " + group + " does not exist."));
                    }
                });
            } catch (NumberFormatException ex) {
                player.sendMessage(TextHandler.colorize("&cPlease enter a valid number for the daily limit."));
            }

            waitingForLimitChange.remove(player);
        }


        if (AllowedColorsGUI.playersAddingColor.contains(player)) {
            e.setCancelled(true);
            String newColor = e.getMessage();

            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                ConfirmationMenuNoLore.getConfirmationInventory(() -> {
                    if (!newColor.trim().isEmpty()) {
                        ConfigurationSection section = Main.getInstance().getSettingsConfig().getConfig().getConfigurationSection("allowed-colors");

                        if (section == null) {
                            Main.getInstance().getSettingsConfig().getConfig().createSection("allowed-colors");
                            section = Main.getInstance().getSettingsConfig().getConfig().getConfigurationSection("allowed-colors");
                        }

                        if (section.getKeys(false).contains(newColor)) {
                            player.sendMessage(TextHandler.colorize("&cColor &b" + newColor + " &cis already in the allowed colors list."));
                        } else {

                            section.set(newColor, 10);
                            Main.getInstance().getSettingsConfig().save();
                            player.sendMessage(TextHandler.colorize("&aColor &b" + newColor + " &ahas been added."));
                        }
                        AllowedColorsGUI.playersAddingColor.remove(player);
                        new AllowedColorsGUI().getSmartInventory().open(player);
                    } else {
                        player.sendMessage(TextHandler.colorize("&cPlease enter a valid color name."));
                    }
                }, () -> new AllowedColorsGUI().getSmartInventory().open(player)).open(player);
            });
        }
    }
}