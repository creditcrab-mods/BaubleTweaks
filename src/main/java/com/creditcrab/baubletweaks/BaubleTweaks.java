package com.creditcrab.baubletweaks;

import baubles.api.expanded.BaubleExpandedSlots;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import thaumcraft.api.ThaumcraftApi;

import java.io.File;

@Mod(modid = BaubleTweaks.MODID, version = Tags.VERSION, name = "BaubleTweaks", acceptedMinecraftVersions = "[1.7.10]",
dependencies = "required-after:Baubles|Expanded;after:Thaumcraft;after:ThaumicExploration;after:ThaumicTinkerer;after:Botania;after:ThermalExpansion")
public class BaubleTweaks {

    public static final String MODID = "baubletweaks";
    public static final Logger LOG = LogManager.getLogger(MODID);

    public static int HEAD;
    public static int BODY;
    public static int CHARM;

    public static  Configuration CONFIGURATION;





    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        BaubleExpandedSlots.tryAssignSlotsUpToMinimum(BaubleExpandedSlots.headType,1);
        BaubleExpandedSlots.tryAssignSlotsUpToMinimum(BaubleExpandedSlots.bodyType,1);
        BaubleExpandedSlots.tryAssignSlotsUpToMinimum(BaubleExpandedSlots.charmType,1);

    }

    @Mod.EventHandler
    public void postInit(FMLInitializationEvent event)throws Exception{
        try {
            HEAD = BaubleExpandedSlots.getIndexesOfAssignedSlotsOfType(BaubleExpandedSlots.headType)[0];
            BODY = BaubleExpandedSlots.getIndexesOfAssignedSlotsOfType(BaubleExpandedSlots.headType)[0];
            CHARM = BaubleExpandedSlots.getIndexesOfAssignedSlotsOfType(BaubleExpandedSlots.headType)[0];
        }
        catch (IndexOutOfBoundsException e){
            //LOG.error("The required bauble slots have been overridden");
            throw new Exception("The required bauble slots have been overridden, remove the overrides in the Baubles Expanded config") ;
        }
    }
}
