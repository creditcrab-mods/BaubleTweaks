package com.creditcrab.baubletweaks.mixins.late.thaumcraft;

import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import baubles.common.container.InventoryBaubles;
import com.creditcrab.baubletweaks.BaubleTweaks;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.api.IArchitect;
import thaumcraft.api.IGoggles;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.nodes.IRevealer;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.client.lib.REHWandHandler;
import thaumcraft.client.lib.RenderEventHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketNote;
import thaumcraft.common.tiles.TileSensor;
import thaumcraft.common.tiles.TileWandPedestal;

import static thaumcraft.client.lib.RenderEventHandler.blockTags;
import static thaumcraft.client.lib.RenderEventHandler.tagscale;

@Mixin(value = RenderEventHandler.class,remap = false)
public abstract class MixinRenderEventHandler {

    @Shadow
    public abstract void drawTagsOnContainer(double x, double y, double z, AspectList tags, int bright, ForgeDirection dir, float partialTicks);

    @Shadow
    public abstract void drawTextInAir(double x, double y, double z, float partialTicks, String text);

    @Shadow
    @SideOnly(Side.CLIENT)
    public REHWandHandler wandHandler;

    /**
     * @author creditcrab
     * @Reason displayes aspect popup when goggles are equipped in head bauble slot
     */

    @Overwrite
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void blockHighlight(DrawBlockHighlightEvent event) {
        int ticks = event.player.ticksExisted;
        MovingObjectPosition target = event.target;
        if (blockTags.size() > 0) {
            int x = (Integer)blockTags.get(0);
            int y = (Integer)blockTags.get(1);
            int z = (Integer)blockTags.get(2);
            AspectList ot = (AspectList)blockTags.get(3);
            ForgeDirection dir = ForgeDirection.getOrientation((Integer)blockTags.get(4));
            if (x == target.blockX && y == target.blockY && z == target.blockZ) {
                if (tagscale < 0.5F) {
                    tagscale += 0.031F - tagscale / 10.0F;
                }

                this.drawTagsOnContainer((double)((float)target.blockX + (float)dir.offsetX / 2.0F), (double)((float)target.blockY + (float)dir.offsetY / 2.0F), (double)((float)target.blockZ + (float)dir.offsetZ / 2.0F), ot, 220, dir, event.partialTicks);
            }
        }
        InventoryBaubles baubles = (InventoryBaubles) BaublesApi.getBaubles((event.player) );
        ItemStack baubleStack = baubles.func_70301_a(BaubleTweaks.HEAD);
        Item baubleItem = baubleStack == null ? null : baubleStack.getItem();
        IGoggles bauble = baubleItem instanceof IGoggles ? (IGoggles) baubleItem : null;

        if ((event.player.inventory.armorItemInSlot(3) != null && event.player.inventory.armorItemInSlot(3).getItem() instanceof IGoggles && ((IGoggles)event.player.inventory.armorItemInSlot(3).getItem()).showIngamePopups(event.player.inventory.armorItemInSlot(3), event.player)
            || (bauble != null && bauble.showIngamePopups(baubleStack,event.player))) ){
            boolean spaceAbove = event.player.worldObj.isAirBlock(target.blockX, target.blockY + 1, target.blockZ);
            TileEntity te = event.player.worldObj.getTileEntity(target.blockX, target.blockY, target.blockZ);
            if (te != null) {
                int note = -1;
                if (te instanceof TileEntityNote) {
                    note = ((TileEntityNote)te).note;
                } else if (te instanceof TileSensor) {
                    note = ((TileSensor)te).note;
                } else if (te instanceof IAspectContainer && ((IAspectContainer)te).getAspects() != null && ((IAspectContainer)te).getAspects().size() > 0) {
                    float shift = 0.0F;
                    if (te instanceof TileWandPedestal) {
                        shift = 0.6F;
                    }

                    if (tagscale < 0.3F) {
                        tagscale += 0.031F - tagscale / 10.0F;
                    }

                    this.drawTagsOnContainer((double)target.blockX, (double)((float)target.blockY + (spaceAbove ? 0.4F : 0.0F) + shift), (double)target.blockZ, ((IAspectContainer)te).getAspects(), 220, spaceAbove ? ForgeDirection.UP : ForgeDirection.getOrientation(event.target.sideHit), event.partialTicks);
                }

                if (note >= 0) {
                    if (ticks % 5 == 0) {
                        PacketHandler.INSTANCE.sendToServer(new PacketNote(target.blockX, target.blockY, target.blockZ, event.player.worldObj.provider.dimensionId));
                    }

                    this.drawTextInAir((double)target.blockX, (double)(target.blockY + 1), (double)target.blockZ, event.partialTicks, "Note: " + note);
                }
            }
        }

        if (this.wandHandler == null) {
            this.wandHandler = new REHWandHandler();
        }

        if (target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && event.player.getHeldItem() != null && event.player.getHeldItem().getItem() instanceof IArchitect && !(event.player.getHeldItem().getItem() instanceof ItemFocusBasic) && this.wandHandler.handleArchitectOverlay(event.player.getHeldItem(), event, ticks, target)) {
            event.setCanceled(true);
        }

    }

}
