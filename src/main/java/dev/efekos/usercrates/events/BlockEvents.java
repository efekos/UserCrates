/*
 * MIT License
 *
 * Copyright (c) 2024 efekos
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

package dev.efekos.usercrates.events;

import dev.efekos.usercrates.Main;
import dev.efekos.usercrates.Utilities;
import dev.efekos.usercrates.data.Crate;
import dev.efekos.usercrates.menu.CrateDisplay;
import me.efekos.simpler.menu.MenuData;
import me.efekos.simpler.menu.MenuManager;
import me.efekos.simpler.translation.TranslateManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class BlockEvents implements Listener {
    // Prevent breaking chest blocks
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.getBlock().getType().equals(Material.CHEST)) return; // return if the block is not a chest

        Chest state = (Chest) e.getBlock().getState();
        PersistentDataContainer container = state.getPersistentDataContainer();

        if (container.has(Main.CRATE_UUID, PersistentDataType.STRING)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("crate-break", "&cYou can't break a crate block. If you tried to remove it, consider &b/crate delete&c.")));
        }
    }

    //Prevent exploding chest blocks
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        BlockState state = e.getBlock().getState();

        if (state instanceof Chest) {
            Chest chest = (Chest) state;

            if (chest.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING))
                e.setCancelled(true);
        }

    }

    // Looking to a chest
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction().toString().endsWith("_AIR")) return; // If player didn't interact with a block then return
        if (e.getClickedBlock() == null) return;
        if (!e.getClickedBlock().getType().equals(Material.CHEST))
            return; // If the player didn't interact with a chest then return

        // get chest's state and container
        Chest chest = (Chest) e.getClickedBlock().getState();
        PersistentDataContainer container = chest.getPersistentDataContainer();

        if (!container.has(Main.CRATE_UUID, PersistentDataType.STRING)) return;
        Crate crate = Main.CRATES.get(UUID.fromString(Objects.requireNonNull(container.get(Main.CRATE_UUID, PersistentDataType.STRING))));

        switch (e.getAction()) {
            case LEFT_CLICK_BLOCK:
                MenuData data = MenuManager.getMenuData(e.getPlayer());
                data.set("chestInventory", chest.getBlockInventory());
                MenuManager.updateMenuData(e.getPlayer(), data);
                MenuManager.Open(e.getPlayer(), CrateDisplay.class);
                break;
            case RIGHT_CLICK_BLOCK:
                Player p = e.getPlayer();

                assert crate != null;
                if ((crate.getAccessors().contains(p.getUniqueId()) || crate.getOwner().equals(p.getUniqueId())) && p.isSneaking())
                    return; // stop if an accessor shift-clicked
                if (Arrays.stream(p.getInventory().getContents()).filter(Objects::nonNull).count() == 36) { // stop if player has full inventory
                    p.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.no-space", "&cYou have no space in your inventory to open this crate.")));
                    return;
                }

                int price = crate.getPrice();
                double money = Main.getEconomy().getBalance(p);

                e.setCancelled(true);
                MenuData menuData = MenuManager.getMenuData(p);
                if (menuData.get("crateOwner") != null) return;
                menuData = null;
                switch (crate.getConsumeType()) { // checks based on type
                    case ONLY_ACCESSORS:
                        //------------------------------------------------------------
                        if (!crate.getAccessors().contains(p.getUniqueId())) {
                            p.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.not-accessor", "&cThis crate is accessor only, you can't open it.")));
                            return;
                        }
                        p.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.opening", "&eOpening the crate...")));
                        Utilities.openCrate(p, crate, chest, false);
                        break;
                    //-------------------------------------------------------------------
                    case PRICE:
                        if (!Main.economyAvaliable()) {
                            p.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.no-econ", "&cYou can't open this crate right now, because this server probably removed their economy system while this crate was still in &bOnly Price &ctype. Ask the crate's owner to change the type.")));
                            return;
                        }

                        if (money < price) {  // means player does not have enough money
                            p.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.neb", "&cYou need &a%price% &cto open this crate.").replace("%price%", Main.getEconomy().format(price + 0.0))));
                            return;
                        }

                        Utilities.openCrate(p, crate, chest, true);
                        break;
                    //-------------------------------------------------------------
                    case KEY:
                        ItemStack stack = e.getItem();
                        if (stack == null) {
                            p.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.no-key", "&cYou need to hold a key for this crate.")));
                            return;
                        }
                        ItemMeta meta = stack.getItemMeta();
                        assert meta != null;
                        PersistentDataContainer stackContainer = meta.getPersistentDataContainer();
                        if (!stackContainer.has(Main.CRATE_UUID, PersistentDataType.STRING)) {
                            p.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.no-key", "&cYou need to hold a key in your hand for this crate.")));
                            return;
                        }
                        UUID keyId = UUID.fromString(Objects.requireNonNull(stackContainer.get(Main.CRATE_UUID, PersistentDataType.STRING)));
                        if (!crate.getUniqueId().equals(keyId)) {

                            p.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.invalid-key", "&cYou are not holding the correct key for this crate.")));
                            return;
                        }

                        e.getItem().setAmount(stack.getAmount() - 1); // remove the key

                        p.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.opening", "&eOpening the crate...")));
                        Utilities.openCrate(p, crate, chest, false);
                        break;
                    //----------------------------------------------------------
                    case BOTH_PRICE_KEY:
                        if (e.getItem() != null && e.getItem().hasItemMeta() && Objects.requireNonNull(e.getItem().getItemMeta()).getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)) { // means he has a key
                            stack = e.getItem();
                            if (stack == null) {
                                p.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.no-key", "&cYou need to hold a key for this crate.")));
                                return;
                            }
                            meta = stack.getItemMeta();
                            stackContainer = meta.getPersistentDataContainer();

                            keyId = UUID.fromString(Objects.requireNonNull(stackContainer.get(Main.CRATE_UUID, PersistentDataType.STRING)));
                            if (!crate.getUniqueId().equals(keyId)) {
                                p.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.invalid-key", "&cYou are not holding the correct key for this crate.")));
                                return;
                            }

                            e.getItem().setAmount(stack.getAmount() - 1); // remove the key

                            p.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.opening", "&eOpening the crate...")));
                            Utilities.openCrate(p, crate, chest, false);
                        } else { // means he probably wants to buy it
                            if (!Main.economyAvaliable()) {
                                p.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.no-econ", "&cYou can't open this crate right now, because this server probably removed their economy system while this crate was still in &bOnly Price &ctype. Ask the crate's owner to change the type.")));
                                return;
                            }

                            if (money < price) {  // means player does not have enough money
                                p.sendMessage(TranslateManager.translateColors(Main.LANG_CONFIG.getString("open.neb", "&cYou need &a%price% &cto open this crate.").replace("%price%", Main.getEconomy().format(price + 0.0))));
                                return;
                            }

                            Utilities.openCrate(p, crate, chest, true);
                        }
                        break;
                }
                break;
            default:
                break;
        }
    }

    // prevent placing chests right next to crates
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!Arrays.asList(Material.CHEST, Material.HOPPER).contains(e.getBlock().getType())) return;
        Location blockLoc = e.getBlock().getLocation();


        for (Location location : Arrays.asList(
                blockLoc.clone().add(1, 0, 0),
                blockLoc.clone().add(0, 1, 0),
                blockLoc.clone().add(0, 0, 1),
                blockLoc.clone().add(-1, 0, 0),
                blockLoc.clone().add(0, -1, 0),
                blockLoc.clone().add(0, 0, -1)
        )) {
            Block otherChest = location.getBlock();
            if (!otherChest.getType().equals(Material.CHEST)) return;
            Chest otherC = (Chest) otherChest.getState();
            if (otherC.getPersistentDataContainer().has(Main.CRATE_UUID, PersistentDataType.STRING)) {
                e.setCancelled(true);
            }
        }

    }
}
