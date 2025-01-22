package com.creditcrab.baubletweaks.mixinplugin;

import baubles.api.expanded.BaubleExpandedSlots;
import com.creditcrab.baubletweaks.BaubleTweaks;
import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.world.ChunkEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@LateMixin
public class BaubleTweaksLateMixins implements ILateMixinLoader {


    @Override
    public String getMixinConfig() {
        return "mixins.baubletweaks.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {

        boolean gogglesOfRevealing = true;
        boolean foodTalisman = true;
        boolean cleansingTalisman = true;
        boolean xpTalisman = true;
        boolean divaCharm = true;
        boolean flightTiara = true;
        boolean holyCloak = true;
        boolean goldenLaurel = true;
        boolean monocle = true;
        boolean itemFinder = true;
        boolean capacitor = true;


        var config = new Configuration(new File(Loader.instance().getConfigDir(),"BaubleTweaks.cfg"));
        config.load();
        if (Loader.isModLoaded("Thaumcraft")){
            gogglesOfRevealing = config.get("Thaumcraft","Goggles of Revealing",gogglesOfRevealing).getBoolean(gogglesOfRevealing);
        }
        else{
            gogglesOfRevealing = false;
        }
        if (Loader.isModLoaded("ThaumicExploration")){
            foodTalisman =  config.get("Thaumic Exploration","Food Talisman",foodTalisman).getBoolean(foodTalisman);
        }
        else{
            foodTalisman = false;
        }
        if (Loader.isModLoaded("ThaumicTinkerer")){
            cleansingTalisman = config.get("Thaumic Tinkerer","Cleansing Talisman",cleansingTalisman).getBoolean(cleansingTalisman);
            xpTalisman = config.get("Thaumic Tinkerer","XP Talisman",xpTalisman).getBoolean(xpTalisman);
        }
        else{
            cleansingTalisman = false;
            xpTalisman = false;
        }
        if (Loader.isModLoaded("Botania")){
            divaCharm = config.get("Botania","Diva Charm",divaCharm).getBoolean(divaCharm);
            flightTiara = config.get("Botania","Flight Tiara",flightTiara).getBoolean(flightTiara);
            holyCloak = config.get("Botania","Holy Cloak",holyCloak).getBoolean(holyCloak);
            goldenLaurel = config.get("Botania","Golden Laurel",goldenLaurel).getBoolean(goldenLaurel);
            monocle = config.get("Botania","Manaseer Monocle",monocle).getBoolean(monocle);
            itemFinder = config.get("Botania","The Spectator",itemFinder).getBoolean(itemFinder);
        }
        else{
            divaCharm = false;
            flightTiara = false;
            holyCloak = false;
            goldenLaurel = false;
            monocle = false;
            itemFinder = false;
        }
        if(Loader.isModLoaded("ThermalExpansion")){
            capacitor = config.get("Thermal Expansion","Flux Capacitor",capacitor).getBoolean(capacitor);
        }
        else {
            capacitor = false;
        }
        config.save();


        List<String> mixins = new ArrayList<>();
        if (gogglesOfRevealing){
            if (FMLLaunchHandler.side().isClient()){
                mixins.add("thaumcraft.MixinTileNodeRenderer");
                mixins.add("thaumcraft.MixinRenderEventHandler");
                if(Loader.isModLoaded("Gadomancy")){
                    mixins.add("gadomancy.MixinRenderTileNodeBasic");
                }
            }
            mixins.add("thaumcraft.MixinItemGoggles");
            mixins.add("thaumcraft.MixinItemResource");
        }
        if (foodTalisman){
            mixins.add("thaumicexploration.MixinItemFoodTalisman");
        }
        if (cleansingTalisman){
            mixins.add("thaumictinkerer.MixinItemCleansingTalisman");

        }
        if (xpTalisman)mixins.add("thaumictinkerer.MixinItemXPTalisman");
        if (divaCharm){
            mixins.add("botania.MixinItemDivaCharm");
        }
        if(flightTiara){
            mixins.add("botania.MixinItemFlightTiara");
            mixins.add("botania.MixinHUDHandler");
        }
        if(holyCloak){
            mixins.add("botania.MixinItemHolyCloak");
        }
        if(goldenLaurel)mixins.add("botania.MixinItemGoldenLaurel");
        if(monocle) mixins.add("botania.MixinItemMonocle");
        if(itemFinder)mixins.add("botania.MixinItemItemFinder");
        if(capacitor){
            mixins.add("thermalexpansion.MixinItemCapacitor");
        }
        return mixins;
    }
}
