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
import me.efekos.simpler.translation.TranslateManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class EntityEvents implements Listener {
    // Prevent killing hologram armor stands
    @EventHandler
    public void onEntityKilled(EntityDeathEvent e){
        if(!e.getEntityType().equals(EntityType.ARMOR_STAND))return;
        UUID id = e.getEntity().getUniqueId();

        World world = e.getEntity().getWorld();

        Main.CRATES.getAll().forEach(data -> {
            if(data.getHolograms().contains(id)){
                OfflinePlayer player= Bukkit.getOfflinePlayer(data.getOwner());

                for (UUID uuid : data.getHolograms()) {
                    Entity armor_stand = world.getEntities().stream().filter(entity -> entity.getUniqueId().equals(uuid)).findFirst().get();
                    armor_stand.remove();
                }

                data.setHolograms(new ArrayList<>());

                data.addHologram(Utilities.makeHologram(world,data.getLocation().add(0.5,1.7,0.5), TranslateManager.translateColors(Main.LANG_CONFIG.getString("hologram.1","&e%player%'s Crate").replace("%player%", Objects.requireNonNull(player.getName())))).getUniqueId());
                data.addHologram(Utilities.makeHologram(world,data.getLocation().add(0.5,1.4,0.5),TranslateManager.translateColors(Main.LANG_CONFIG.getString("hologram.2","&ePrice: &a%price%").replace("%price%", String.valueOf(data.getPrice())))).getUniqueId());
                data.addHologram(Utilities.makeHologram(world,data.getLocation().add(0.5,1.1,0.5),TranslateManager.translateColors(Main.LANG_CONFIG.getString("hologram.3","&7Right-Click to Open"))).getUniqueId());
                data.addHologram(Utilities.makeHologram(world,data.getLocation().add(0.5,0.8,0.5),TranslateManager.translateColors(Main.LANG_CONFIG.getString("hologram.4", "&7Left-Click to See Items"))).getUniqueId());
            }
        });
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e){
        ItemStack stack = e.getItem();
        ItemMeta meta = stack.getItemMeta();
        if(meta instanceof PotionMeta){
            PotionMeta potionMeta = (PotionMeta) meta;
            Player p = e.getPlayer();

            PotionType type = potionMeta.getBasePotionData().getType();
            PotionData data = potionMeta.getBasePotionData();

            p.sendMessage("type is "+type.name());
            if(data.isExtended())p.sendMessage("extended");
            if(data.isUpgraded())p.sendMessage("upgraded");
        }
    }
}