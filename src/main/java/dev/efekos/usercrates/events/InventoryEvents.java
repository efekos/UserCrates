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
     * @param e event.
     */
    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent e){
        if(e.getSource().getHolder() instanceof Chest && e.getDestination().getHolder() instanceof Hopper) {
            Chest chest = (Chest) e.getSource().getHolder();
            PersistentDataContainer container = chest.getPersistentDataContainer();

            if(container.has(Main.CRATE_UUID, PersistentDataType.STRING)) e.setCancelled(true);
        }

        if(e.getSource().getHolder() instanceof Hopper && e.getDestination().getHolder() instanceof Chest) {
            Chest chest = (Chest) e.getDestination().getHolder();
            PersistentDataContainer container = chest.getPersistentDataContainer();

            if(container.has(Main.CRATE_UUID, PersistentDataType.STRING)) e.setCancelled(true);
        }
    }
}
