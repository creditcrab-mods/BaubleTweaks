package com.creditcrab.baubletweaks.mixins.late.botania;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import baubles.api.expanded.BaubleItemHelper;
import baubles.api.expanded.IBaubleExpanded;
import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.item.IBaubleRender;
import vazkii.botania.common.item.equipment.bauble.ItemBauble;
import vazkii.botania.common.item.equipment.bauble.ItemHolyCloak;

import java.util.List;

import static vazkii.botania.common.item.equipment.bauble.ItemHolyCloak.*;

@Mixin(value = ItemHolyCloak.class, remap = false)
public abstract class MixinItemHolyCloak extends ItemBauble implements IBaubleRender, IBaubleExpanded {

    private static String[] baubleTypes;


    public MixinItemHolyCloak(String name) {
        super(name);
    }

    @Inject(method = "<init>*",at=@At("RETURN"))
    public void init(CallbackInfo ci){
        if (BaubleExpandedSlots.totalCurrentlyAssignedSlotsOfType(BaubleExpandedSlots.capeType) > 0){
            baubleTypes = new String[]{BaubleExpandedSlots.capeType};
        }
        else{
            baubleTypes = new String[]{ BaubleExpandedSlots.bodyType};
        }
    }

    @Override
    public String[] getBaubleTypes(ItemStack itemStack) {
        return baubleTypes;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        BaubleItemHelper.addSlotInformation(par3List,this.getBaubleTypes(par1ItemStack));
    }


    /**
     * @Author Nick
     * @Reason Changes the method to check whichever bauble slot it is using
     * @param event
     */
    @Overwrite
    @SubscribeEvent
    public void onPlayerDamage(LivingHurtEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.entityLiving;
            IInventory baubles = BaublesApi.getBaubles(player);
            ItemStack baub = baubles.getStackInSlot(BaubleExpandedSlots.getIndexesOfAssignedSlotsOfType(baubleTypes[0])[0]);
            if (baub != null && baub.getItem() instanceof ItemHolyCloak && !isInEffect(baub)) {
                ItemHolyCloak cloak = (ItemHolyCloak)baub.getItem();
                int cooldown = getCooldown(baub);
                setInEffect(baub, true);
                if (cooldown == 0 && cloak.effectOnDamage(event, player, baub)) {
                    setCooldown(baub, cloak.getCooldownTime(baub));
                }

                setInEffect(baub, false);
            }
        }

    }
}
