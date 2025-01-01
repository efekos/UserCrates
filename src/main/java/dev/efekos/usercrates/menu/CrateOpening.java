/*
 * MIT License
 *
 * Copyright (c) 2025 efekos
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.efekos.usercrates.menu;

import dev.efekos.usercrates.Main;
import dev.efekos.usercrates.Utilities;
import me.efekos.simpler.menu.Menu;
import me.efekos.simpler.menu.MenuData;
import me.efekos.simpler.menu.MenuManager;
import me.efekos.simpler.translation.TranslateManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class CrateOpening extends Menu {
    public CrateOpening(MenuData data) {
        super(data);
    }

    @Override
    public boolean cancelAllClicks() {
        return true;
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public String getTitle() {
        return TranslateManager.translateColors(Main.LANG_CONFIG.getString("crate_opening.title", "Opening %player%'s Crate!").replace("%player%", (String) data.get("crateOwner")));
    }

    @Override
    public void onClick(InventoryClickEvent inventoryClickEvent) {

    }

    private boolean openDone = false;
    private boolean openedOnce = false;

    @Override
    public void onClose(InventoryCloseEvent e) {
        if (!openDone) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    owner.openInventory(inventory);
                }
            }.runTaskLater(Main.getInstance(), 2);
        } else {
            data.set("inventory", null);
            data.set("winningItem", null);
            data.set("crateOwner", null);
            data.set("crateOwnerUUID", null);
            MenuManager.updateMenuData(data.getOwner(), data);
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent inventoryOpenEvent) {
        if (openedOnce) return;
        openedOnce = true;
        openDone = false;

        Inventory chestInventory = (Inventory) data.get("inventory");
        ItemStack stackToGive = (ItemStack) data.get("winningItem");

        List<ItemStack> contents = Arrays.stream(chestInventory.getContents()).filter(Objects::nonNull).toList();

        BukkitRunnable makeGreen = new BukkitRunnable() {
            @Override
            public void run() {
                owner.playSound(owner, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 100);

                ItemStack black_glass_pane = createItem(Material.LIME_STAINED_GLASS_PANE, " ");
                inventory.setItem(0, black_glass_pane);
                inventory.setItem(1, black_glass_pane);
                inventory.setItem(2, black_glass_pane);
                inventory.setItem(3, black_glass_pane);
                inventory.setItem(4, black_glass_pane);
                inventory.setItem(5, black_glass_pane);
                inventory.setItem(6, black_glass_pane);
                inventory.setItem(7, black_glass_pane);
                inventory.setItem(8, black_glass_pane);
                inventory.setItem(18, black_glass_pane);
                inventory.setItem(19, black_glass_pane);
                inventory.setItem(20, black_glass_pane);
                inventory.setItem(21, black_glass_pane);
                inventory.setItem(22, black_glass_pane);
                inventory.setItem(23, black_glass_pane);
                inventory.setItem(24, black_glass_pane);
                inventory.setItem(25, black_glass_pane);
                inventory.setItem(26, black_glass_pane);
            }
        };

        AtomicInteger timesLeft = new AtomicInteger((int) Math.round(Math.random() * 25) + 25);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (timesLeft.get() == 0) {
                    cancel();
                    getRunnable(stackToGive, makeGreen).run();
                    return;
                }

                ItemStack stack = contents.get((int) Math.round(Math.random() * (contents.size() - 1)));

                inventory.setItem(13, stack);
                owner.playSound(owner, Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 100F, 100F);
                timesLeft.set(timesLeft.get() - 1);
            }
        }.runTaskTimer(Main.getInstance(), 0, 2);
    }

    @NotNull
    private BukkitRunnable getRunnable(ItemStack stackToGive, BukkitRunnable makeGreen) {
        BukkitRunnable makeBlack = new BukkitRunnable() {
            @Override
            public void run() {
                owner.playSound(owner, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 100);

                ItemStack black_glass_pane = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
                ItemStack green_glass_pane = createItem(Material.LIME_STAINED_GLASS_PANE, " ");
                inventory.setItem(0, black_glass_pane);
                inventory.setItem(1, black_glass_pane);
                inventory.setItem(2, black_glass_pane);
                inventory.setItem(3, black_glass_pane);
                inventory.setItem(4, green_glass_pane);
                inventory.setItem(5, black_glass_pane);
                inventory.setItem(6, black_glass_pane);
                inventory.setItem(7, black_glass_pane);
                inventory.setItem(8, black_glass_pane);

                inventory.setItem(18, black_glass_pane);
                inventory.setItem(19, black_glass_pane);
                inventory.setItem(20, black_glass_pane);
                inventory.setItem(21, black_glass_pane);
                inventory.setItem(22, green_glass_pane);
                inventory.setItem(23, black_glass_pane);
                inventory.setItem(24, black_glass_pane);
                inventory.setItem(25, black_glass_pane);
                inventory.setItem(26, black_glass_pane);
            }
        };

        return new BukkitRunnable() {
            @Override
            public void run() {
                inventory.setItem(13, stackToGive);
                owner.getInventory().addItem(stackToGive.clone());

                ItemMeta stackMeta = stackToGive.getItemMeta();

                assert stackMeta != null;
                BaseComponent component = stackMeta.hasDisplayName() ? new TextComponent(stackMeta.getDisplayName()) : TranslateManager.translateMaterial(stackToGive.getType());
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(stackToGive.getType().getKey().toString(),stackToGive.getAmount(), ItemTag.ofNbt(stackMeta.getAsString()))));

                owner.spigot().sendMessage(Utilities.makeComponentsForValue(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.done", "&aYou got &f[&f%item%&f] x%count% &afrom the crate!").replace("%count%", stackToGive.getAmount() + "")), "%item%", component));

                makeGreen.run();
                makeBlack.runTaskLater(Main.getInstance(), 10);
                makeGreen.runTaskLater(Main.getInstance(), 20);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        openDone = true;
                        if (Objects.equals(owner.getOpenInventory().getTopInventory().getHolder(), inventory.getHolder()))
                            owner.closeInventory();
                    }
                }.runTaskLater(Main.getInstance(), 50);
            }
        };
    }

    @Override
    public void fill() {
        ItemStack black_glass_pane = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        ItemStack green_glass_pane = createItem(Material.LIME_STAINED_GLASS_PANE, " ");
        inventory.setItem(0, black_glass_pane);
        inventory.setItem(1, black_glass_pane);
        inventory.setItem(2, black_glass_pane);
        inventory.setItem(3, black_glass_pane);
        inventory.setItem(4, green_glass_pane);
        inventory.setItem(5, black_glass_pane);
        inventory.setItem(6, black_glass_pane);
        inventory.setItem(7, black_glass_pane);
        inventory.setItem(8, black_glass_pane);

        inventory.setItem(18, black_glass_pane);
        inventory.setItem(19, black_glass_pane);
        inventory.setItem(20, black_glass_pane);
        inventory.setItem(21, black_glass_pane);
        inventory.setItem(22, green_glass_pane);
        inventory.setItem(23, black_glass_pane);
        inventory.setItem(24, black_glass_pane);
        inventory.setItem(25, black_glass_pane);
        inventory.setItem(26, black_glass_pane);
    }
}