package com.creditcrab.baubletweaks.mixins.late.botania;

import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import baubles.common.lib.PlayerHandler;
import com.creditcrab.baubletweaks.BaubleTweaks;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.equipment.bauble.ItemFlightTiara;

@Mixin(value = HUDHandler.class, remap = false)
public class MixinHUDHandler {

    /**
     * @Author Nick
     * @Reason rewrite flight HUD rendering to use extended baubles API
     * @param event
     */
    @Overwrite
    @SubscribeEvent(
        priority = EventPriority.HIGHEST
    )
    public void onDrawScreenPre(RenderGameOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getMinecraft();
        Profiler profiler = mc.mcProfiler;
        if (event.type == RenderGameOverlayEvent.ElementType.HEALTH) {
            profiler.startSection("botania-hud");
            ItemStack tiara = BaublesApi.getBaubles(mc.thePlayer).getStackInSlot(BaubleTweaks.HEAD);
            if (tiara != null && tiara.getItem() == ModItems.flightTiara) {
                profiler.startSection("flugelTiara");
                ItemFlightTiara.renderHUD(event.resolution, mc.thePlayer, tiara);
                profiler.endSection();
            }

            profiler.endSection();
        }

    }
}
