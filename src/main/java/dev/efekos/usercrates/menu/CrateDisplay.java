package dev.efekos.usercrates.menu;

import dev.efekos.usercrates.Main;
import me.efekos.simpler.translation.TranslateManager;
import me.efekos.simpler.menu.Menu;
import me.efekos.simpler.menu.MenuData;
import me.efekos.simpler.menu.MenuManager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CrateDisplay extends Menu {
    public CrateDisplay(MenuData data) {
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
        return Main.LANG_CONFIG.getString("crate_display.title","Crate Display");
    }

    @Override
    public void onClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public void onClose(InventoryCloseEvent inventoryCloseEvent) {

    }

    @Override
    public void onOpen(InventoryOpenEvent inventoryOpenEvent) {

    }

    @Override
    public void fill() {
        Inventory chestInventory = (Inventory) data.get("chestInventory");
        if(chestInventory == null){
            owner.closeInventory();
        }

        for (int i = 0;i<27;i++){
            assert chestInventory != null;
            inventory.setItem(i,chestInventory.getItem(i));
        }
        data.set("chestInventory",null);
        MenuManager.updateMenuData(owner,data);


        int itemCount = (int) Arrays.stream(inventory.getContents()).filter(Objects::nonNull).count();

        HashMap<ItemStack,Integer> chances = new HashMap<>();

        for (ItemStack stack : inventory.getContents()){
            if(stack==null) continue;
            int similarCount = (int) Arrays.stream(inventory.getContents()).filter(Objects::nonNull).filter(itemStack -> itemStack.equals(stack)).count();
            chances.put(stack.clone(),similarCount);
        }

        for (ItemStack stack : inventory.getContents()) {
            if(stack==null) continue;

            int similarCount = chances.getOrDefault(stack,0);
            int chance = Math.round(( (float) similarCount /itemCount)*100);

            ItemMeta meta = stack.getItemMeta();
            assert meta != null;
            List<String> lore = meta.getLore();
            if(lore==null) lore = new ArrayList<>();
            lore.add("\n");
            lore.add(TranslateManager.translateColors(Main.LANG_CONFIG.getString("crate_display.chance","&eChance&6: &3%&b%chance% &3(&b%count% in %all%&3)").replace("%chance%", String.valueOf(chance)).replace("%all%", String.valueOf(itemCount)).replace("%count%", String.valueOf(similarCount))));

            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
    }
}
