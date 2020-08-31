package com.mattdag.hoeHarvester;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getWorld;

public class HoeListener implements Listener {
    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        try {
            if (event != null) {
                ItemStack itemHolding = event.getItem();
                Block clickedBlock = event.getClickedBlock();
                Material materialClickedBlock = clickedBlock.getType();
                Player player = event.getPlayer();

                if (itemHolding.getAmount() == 1 && getHoeLevel(itemHolding.getType()) != 0) {
                    if (validCrop(materialClickedBlock) && isFullyGrown(clickedBlock)) {
                        ArrayList<Block> crops = getStoneHoeCrops(clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ(), player.getWorld(), itemHolding.getType());

                        for (Block block: crops) {
                            block.breakNaturally();
                        }

                        int durability = itemHolding.getDurability() + Math.floorDiv(crops.size(), 2);
                        itemHolding.setDurability((short) durability);
                    }
                }
            }
        }
        catch (NullPointerException e) { }
    }

    public boolean isFullyGrown(Block block) {
        MaterialData md = block.getState().getData();

        if(md instanceof Crops) {
            return (((Crops) md).getState() == CropState.RIPE);
        }
        else return false;
    }

    public int getHoeLevel(Material hoe) {
        if (hoe.equals(Material.STONE_HOE)) return 1;
        if (hoe.equals(Material.IRON_HOE) || hoe.equals(Material.GOLDEN_HOE)) return 2;
        if (hoe.equals(Material.DIAMOND_HOE)) return 3;
        return 0;
    }

    public boolean validCrop(Material crop) {
        if (crop.equals(Material.WHEAT) || crop.equals(Material.POTATOES) || crop.equals(Material.CARROTS) || crop.equals(Material.BEETROOTS))
            return true;
        return false;
    }

    public ArrayList<Block> getStoneHoeCrops(int x, int y, int z, World world, Material hoe) {
        ArrayList<Block> blocksUnfiltered = new ArrayList<Block>();
        ArrayList<Block> blocksFiltered = new ArrayList<Block>();

        int currentHoeLevel = getHoeLevel(hoe);

        for (int i = x - currentHoeLevel; i < x + currentHoeLevel + 1; i++) {
            for (int j = z - currentHoeLevel; j <= z + currentHoeLevel; j++) {
                blocksUnfiltered.add(world.getBlockAt(i, y, j));
            }
        }

        for (Block block: blocksUnfiltered) {
            if (validCrop(block.getType()) && isFullyGrown(block)) {
                blocksFiltered.add(block);
            }
        }

        return blocksFiltered;
    }
}
