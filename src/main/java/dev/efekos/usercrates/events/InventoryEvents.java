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
import org.bukkit.block.Chest;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class InventoryEvents implements Listener {
    /**
     * Prevents all the item transactions between a hopper and a crate.
     *
     * @param e event.
     */
    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent e) {
        if (e.getSource().getHolder() instanceof Chest && e.getDestination().getHolder() instanceof Hopper) {
            Chest chest = (Chest) e.getSource().getHolder();
            PersistentDataContainer container = chest.getPersistentDataContainer();

            if (container.has(Main.CRATE_UUID, PersistentDataType.STRING)) e.setCancelled(true);
        }

        if (e.getSource().getHolder() instanceof Hopper && e.getDestination().getHolder() instanceof Chest) {
            Chest chest = (Chest) e.getDestination().getHolder();
            PersistentDataContainer container = chest.getPersistentDataContainer();

            if (container.has(Main.CRATE_UUID, PersistentDataType.STRING)) e.setCancelled(true);
        }
    }
}
