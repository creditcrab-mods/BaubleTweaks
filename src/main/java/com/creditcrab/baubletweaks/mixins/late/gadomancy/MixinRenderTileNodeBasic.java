package com.creditcrab.baubletweaks.mixins.late.gadomancy;

import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import baubles.api.expanded.BaubleItemHelper;
import baubles.common.BaublesExpanded;
import com.creditcrab.baubletweaks.BaubleTweaks;
import makeo.gadomancy.client.renderers.tile.RenderTileNodeBasic;
import makeo.gadomancy.common.events.EventHandlerRedirect;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.IRevealer;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.items.relics.ItemThaumometer;
import thaumcraft.common.tiles.TileJarNode;
import thaumcraft.common.tiles.TileNode;

import static makeo.gadomancy.client.renderers.tile.RenderTileNodeBasic.renderNode;

@Mixin(value = RenderTileNodeBasic.class, remap = false)
public abstract class MixinRenderTileNodeBasic {


    @Overwrite
    public static void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks, float size) {
        EventHandlerRedirect.preNodeRender(tile);
        if (!(tile instanceof INode)) {
            EventHandlerRedirect.postNodeRender(tile);
        } else {
            INode node = (INode)tile;
            double viewDistance = 64.0;
            EntityLivingBase viewer = Minecraft.getMinecraft().renderViewEntity;
            boolean condition = false;
            boolean depthIgnore = false;
            if (viewer instanceof EntityPlayer player) {
                if (tile != null && tile instanceof TileJarNode) {
                    condition = true;
                    size = (float)((double)size * 7.0 / 10.0);
                } else if (player.inventory.armorItemInSlot(3) != null && ((EntityPlayer)viewer).inventory.armorItemInSlot(3).getItem() instanceof IRevealer && ((IRevealer)((EntityPlayer)viewer).inventory.armorItemInSlot(3).getItem()).showNodes(((EntityPlayer)viewer).inventory.armorItemInSlot(3), viewer)) {
                    condition = true;
                    depthIgnore = true;
                } else if (player.inventory.getCurrentItem() != null && ((EntityPlayer)viewer).inventory.getCurrentItem().getItem() instanceof ItemThaumometer && UtilsFX.isVisibleTo(0.44F, viewer, x, y, z)) {
                    condition = true;
                    depthIgnore = true;
                    viewDistance = 48.0;
                }
                else if(BaublesApi.getBaubles(player).getStackInSlot(BaubleTweaks.HEAD) != null && BaublesApi.getBaubles(player).getStackInSlot(BaubleTweaks.HEAD).getItem() instanceof IRevealer){
                    condition = true;
                    depthIgnore = true;
                }
            }

            renderNode(viewer, viewDistance, condition, depthIgnore, size, x, y, z, partialTicks, ((INode)tile).getAspects(), ((INode)tile).getNodeType(), ((INode)tile).getNodeModifier());
            if (tile instanceof TileNode && ((TileNode)tile).drainEntity != null && ((TileNode)tile).drainCollision != null) {
                Entity drainEntity = ((TileNode)tile).drainEntity;
                if (drainEntity instanceof EntityPlayer && !((EntityPlayer)drainEntity).isUsingItem()) {
                    ((TileNode)tile).drainEntity = null;
                    ((TileNode)tile).drainCollision = null;
                    EventHandlerRedirect.postNodeRender(tile);
                    return;
                }

                MovingObjectPosition drainCollision = ((TileNode)tile).drainCollision;
                GL11.glPushMatrix();
                float f10 = 0.0F;
                int iiud = ((EntityPlayer)drainEntity).getItemInUseDuration();
                if (drainEntity instanceof EntityPlayer) {
                    f10 = MathHelper.sin((float)iiud / 10.0F) * 10.0F;
                }

                Vec3 vec3 = Vec3.createVectorHelper(-0.1, -0.1, 0.5);
                vec3.rotateAroundX(-(drainEntity.prevRotationPitch + (drainEntity.rotationPitch - drainEntity.prevRotationPitch) * partialTicks) * 3.141593F / 180.0F);
                vec3.rotateAroundY(-(drainEntity.prevRotationYaw + (drainEntity.rotationYaw - drainEntity.prevRotationYaw) * partialTicks) * 3.141593F / 180.0F);
                vec3.rotateAroundY(-f10 * 0.01F);
                vec3.rotateAroundX(-f10 * 0.015F);
                double d3 = drainEntity.prevPosX + (drainEntity.posX - drainEntity.prevPosX) * (double)partialTicks + vec3.xCoord;
                double d4 = drainEntity.prevPosY + (drainEntity.posY - drainEntity.prevPosY) * (double)partialTicks + vec3.yCoord;
                double d5 = drainEntity.prevPosZ + (drainEntity.posZ - drainEntity.prevPosZ) * (double)partialTicks + vec3.zCoord;
                double d6 = drainEntity == Minecraft.getMinecraft().thePlayer ? 0.0 : (double)drainEntity.getEyeHeight();
                UtilsFX.drawFloatyLine(d3, d4 + d6, d5, (double)drainCollision.blockX + 0.5, (double)drainCollision.blockY + 0.5, (double)drainCollision.blockZ + 0.5, partialTicks, ((TileNode)tile).color.getRGB(), "textures/misc/wispy.png", -0.02F, (float)Math.min(iiud, 10) / 10.0F);
                GL11.glPopMatrix();
            }

            EventHandlerRedirect.postNodeRender(tile);
        }
    }
}
