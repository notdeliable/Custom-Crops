/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.api.customplugin;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.Function;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.pot.PotManager;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class Handler extends Function implements Listener {

    protected PlatformManager platformManager;

    public Handler(PlatformManager platformManager) {
        this.platformManager = platformManager;
    }

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(this, CustomCrops.getInstance());
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        platformManager.onInteractBlock(event);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        platformManager.onBreakVanilla(event);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        platformManager.onPlaceVanilla(event);
    }

    @EventHandler
    public void onMoistureChange(MoistureChangeEvent event) {
        if (event.isCancelled()) return;
        if (ConfigManager.disableMoistureMechanic) event.setCancelled(true);
    }

    @EventHandler
    public void onTrampling(EntityChangeBlockEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlock();
        if (block.getType() == Material.FARMLAND && event.getTo() == Material.DIRT) {
            if (ConfigManager.preventTrampling) {
                event.setCancelled(true);
            } else if (PotManager.enableFarmLand) {
                platformManager.onBreakPot(event.getEntity(), "FARMLAND", block.getLocation(), event);
            }
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (!PotManager.enableFarmLand || event.isCancelled()) return;
        Block block = event.getBlock();
        if (block.getType() == Material.FARMLAND) {
            if (CustomCrops.getInstance().getPlatformInterface().detectAnyThing(event.getBlock().getLocation().clone().add(0,1,0))) {
                event.setCancelled(true);
            } else {
                platformManager.onBreakPot(null, "FARMLAND", block.getLocation(), event);
            }
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (!PotManager.enableVanillaBlock || event.isCancelled()) return;
        PotManager potManager = CustomCrops.getInstance().getPotManager();
        for (Block block : event.getBlocks()) {
            String id = block.getType().name();
            if (potManager.containsPotBlock(id)) {
                platformManager.onBreakPot(null, id, block.getLocation(), event);
            }
        }
    }
}