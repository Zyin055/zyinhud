package com.zyin.zyinhud.mods;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;

import com.zyin.zyinhud.util.InventoryUtil;
import com.zyin.zyinhud.util.Localization;
import com.zyin.zyinhud.util.ZyinHUDUtil;

/**
 * TorchAid Aid allows the player to easily use an torch without having it selected.
 */
public class TorchAid extends ZyinHUDModBase
{
	/** Enables/Disables this Mod */
	public static boolean Enabled;

    /**
     * Toggles this Mod on or off
     * @return The state the Mod was changed to
     */
    public static boolean ToggleEnabled()
    {
    	return Enabled = !Enabled;
    }
    
    
    /**
     * When the player right clicks
     */
    public static void OnRightClick()
    {
    	if(TorchAid.Enabled)
    		UseTorchIfToolIsEquipped();
    }
    
    
    /**
     * Makes the player place a Torch if they are currently using an axe, pickaxe, shovel, or have nothing in hand.
     */
    public static void UseTorchIfToolIsEquipped()
    {
    	if(mc.currentScreen == null && mc.inGameHasFocus)
    	{
    		ItemStack currentItemStack = mc.thePlayer.getHeldItem();
    		if(currentItemStack == null	|| currentItemStack.getItem() instanceof ItemTool)	//hand or tool (axe, pickaxe, shovel) selected
    		{
    			UseTorch();
    		}
    	}
    }
    
    /**
     * Makes the player place a Torch if they have any.
     */
    public static void UseTorch()
    {
        if (EatingAid.instance.isEating())
        {
            EatingAid.instance.StopEating();    //it's not good if we have an torch selected and hold right click down...
        }
        
        //don't attempt to place a torch while looking at something like a chest or lever
        //unless we're sneaking because sneak acts like an unstoppable right click
        if(ZyinHUDUtil.IsMouseoveredBlockRightClickable() && !mc.thePlayer.isSneaking())
        	return;
        
        boolean usedTorchSuccessfully = InventoryUtil.UseItem(Blocks.torch);
        
        if (!usedTorchSuccessfully)
        {
            InfoLine.DisplayNotification(Localization.get("torchaid.notorches"));
        }
    }
}
