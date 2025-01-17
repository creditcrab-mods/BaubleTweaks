package com.creditcrab.baubletweaks.mixins.late.botania;

import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import baubles.api.expanded.BaubleItemHelper;
import baubles.api.expanded.IBaubleExpanded;
import baubles.common.BaublesExpanded;
import baubles.common.lib.PlayerHandler;
import com.creditcrab.baubletweaks.BaubleTweaks;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.botania.api.item.IBaubleRender;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.Botania;
import vazkii.botania.common.achievement.ICraftAchievement;
import vazkii.botania.common.core.handler.ConfigHandler;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.equipment.bauble.ItemBauble;
import vazkii.botania.common.item.equipment.bauble.ItemFlightTiara;

import java.util.List;

import static vazkii.botania.common.item.equipment.bauble.ItemFlightTiara.playerStr;
import static vazkii.botania.common.item.equipment.bauble.ItemFlightTiara.playersWithFlight;

@Mixin(value = ItemFlightTiara.class, remap = false)
public abstract class MixinItemFlightTiara extends ItemBauble implements IBaubleExpanded, IManaUsingItem, IBaubleRender, ICraftAchievement {
    public MixinItemFlightTiara(String name) {
        super(name);
    }

    @Override
    public String[] getBaubleTypes(ItemStack itemStack) {
        return new String[]{BaubleExpandedSlots.headType};
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        par3List.add(StatCollector.translateToLocal("botania.wings" + par1ItemStack.getItemDamage()));
        BaubleItemHelper.addSlotInformation(par3List,this.getBaubleTypes(par1ItemStack));
    }

    /**
     *
     * @author Nick
     * @Reason update to extended bauble api
     * @return
     */
    @Overwrite
    public boolean shouldPlayerHaveFlight(EntityPlayer player){
        ItemStack tiara = BaublesApi.getBaubles(player).getStackInSlot(BaubleTweaks.HEAD);
        if (tiara != null && tiara.getItem() == this) {
            int left = ItemNBTHelper.getInt(tiara, "timeLeft", 1200);
            boolean flying = ItemNBTHelper.getBoolean(tiara, "flying", false);
            return (left > (flying ? 0 : 120) || player.inventory.hasItem(ModItems.flugelEye)) && ManaItemHandler.requestManaExact(tiara, player, this.getCost(tiara, left), false);
        } else {
            return false;
        }
    }

    @Shadow
    public abstract int getCost(ItemStack stack, int timeLeft);

    /**
     * @author Nick
     * @Reason rewrite to extended bauble API
     * @param event
     */
    @Overwrite
    @SubscribeEvent
    public void updatePlayerFlyStatus(LivingEvent.LivingUpdateEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.entityLiving;
            ItemStack tiara = BaublesApi.getBaubles(player).getStackInSlot(BaubleTweaks.HEAD);
            int left = ItemNBTHelper.getInt(tiara, "timeLeft", 1200);
            //System.out.println("timeleft " + left);
            if (playersWithFlight.contains(playerStr(player))) {
                if (this.shouldPlayerHaveFlight(player)) {
                    player.capabilities.allowFlying = true;
                    if (player.capabilities.isFlying) {
                        if (!player.worldObj.isRemote) {
                            ManaItemHandler.requestManaExact(tiara, player, this.getCost(tiara, left), true);
                        } else if (Math.abs(player.motionX) > 0.1 || Math.abs(player.motionZ) > 0.1) {
                            double x = event.entityLiving.posX - 0.5;
                            double y = event.entityLiving.posY - 1.7;
                            double z = event.entityLiving.posZ - 0.5;
                            player.getGameProfile().getName();
                            float r = 1.0F;
                            float g = 1.0F;
                            float b = 1.0F;
                            switch (tiara.getItemDamage()) {
                                case 2:
                                    r = 0.1F;
                                    g = 0.1F;
                                    b = 0.1F;
                                    break;
                                case 3:
                                    r = 0.0F;
                                    g = 0.6F;
                                    break;
                                case 4:
                                    g = 0.3F;
                                    b = 0.3F;
                                    break;
                                case 5:
                                    r = 0.6F;
                                    g = 0.0F;
                                    b = 0.6F;
                                    break;
                                case 6:
                                    r = 0.4F;
                                    g = 0.0F;
                                    b = 0.0F;
                                    break;
                                case 7:
                                    r = 0.2F;
                                    g = 0.6F;
                                    b = 0.2F;
                                    break;
                                case 8:
                                    r = 0.85F;
                                    g = 0.85F;
                                    b = 0.0F;
                                    break;
                                case 9:
                                    r = 0.0F;
                                    b = 0.0F;
                            }

                            for(int i = 0; i < 2; ++i) {
                                Botania.proxy.sparkleFX(event.entityLiving.worldObj, x + Math.random() * (double)event.entityLiving.width, y + Math.random() * 0.4, z + Math.random() * (double)event.entityLiving.width, r, g, b, 2.0F * (float)Math.random(), 20);
                            }
                        }
                    }
                } else {
                    if (!player.capabilities.isCreativeMode) {
                        player.capabilities.allowFlying = false;
                        player.capabilities.isFlying = false;
                        player.capabilities.disableDamage = false;
                    }

                    playersWithFlight.remove(playerStr(player));
                }
            } else if (this.shouldPlayerHaveFlight(player)) {
                playersWithFlight.add(playerStr(player));
                player.capabilities.allowFlying = true;
            }
        }

    }


    @Shadow
    private static ResourceLocation textureHud;

    /**
     *
     * @author Nick
     * @reason update to extended bauble api
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public static void renderHUD(ScaledResolution resolution, EntityPlayer player, ItemStack stack) {
        int u = Math.max(1, stack.getItemDamage()) * 9 - 9;
        int v = 0;
        Minecraft mc = Minecraft.getMinecraft();
        mc.renderEngine.bindTexture(textureHud);
        int xo = resolution.getScaledWidth() / 2 + 10;
        int x = xo;
        int y = resolution.getScaledHeight() - ConfigHandler.flightBarHeight;
        if (player.getAir() < 300) {
            y = resolution.getScaledHeight() - ConfigHandler.flightBarBreathHeight;
        }

        int left = ItemNBTHelper.getInt(stack, "timeLeft", 1200);
        int segTime = 120;
        int segs = left / segTime + 1;
        int last = left % segTime;

        int width;
        for(width = 0; width < segs; ++width) {
            float trans = 1.0F;
            if (width == segs - 1) {
                trans = (float)last / (float)segTime;
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glDisable(3008);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, trans);
            RenderHelper.drawTexturedModalRect(x, y, 0.0F, u, v, 9, 9);
            x += 8;
        }

        if (player.capabilities.isFlying) {
            width = ItemNBTHelper.getInt(stack, "dashCooldown", 0);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (width > 0) {
                Gui.drawRect(xo, y - 2, xo + 80, y - 1, -2013265920);
            }

            Gui.drawRect(xo, y - 2, xo + width, y - 1, -1);
        }

        GL11.glEnable(3008);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(Gui.icons);
    }


}
