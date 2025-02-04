package com.dt.lBT.uis;

import com.dt.lBT.Main;
import com.dt.lBT.utils.TextHandler;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class DynamicGUI {

    private String title;
    private int rows;
    private int columns;
    private BiConsumer<Player, InventoryContents> initConsumer;
    private BiConsumer<Player, InventoryContents> updateConsumer;

    @Getter
    private SmartInventory smartInventory;

    public DynamicGUI() {
        this.initConsumer = (player, contents) -> {};
        this.updateConsumer = (player, contents) -> {};
    }

    public DynamicGUI title(String title) {
        this.title = TextHandler.colorize(title);
        return this;
    }

    public DynamicGUI size(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        return this;
    }

    public DynamicGUI init(BiConsumer<Player, InventoryContents> initFunction) {
        this.initConsumer = initFunction;
        return this;
    }

    public DynamicGUI update(BiConsumer<Player, InventoryContents> updateFunction) {
        this.updateConsumer = updateFunction;
        return this;
    }

    public void open(Player player) {
        if (this.title == null || this.rows == 0 || this.columns == 0) {
            throw new IllegalStateException("Title, size (rows and columns) must be set before opening the GUI.");
        }

        this.smartInventory = SmartInventory.builder()
                .title(TextHandler.colorize(this.title))
                .size(this.rows, this.columns)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        initConsumer.accept(player, contents);
                    }

                    @Override
                    public void update(Player player, InventoryContents contents) {
                        updateConsumer.accept(player, contents);
                    }
                })
                .manager(Main.getInstance().getInventoryManager())
                .build();

        this.smartInventory.open(player);
    }
}