package com.zyin.zyinhud.mods;

import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

import com.zyin.zyinhud.ZyinHUDSound;
import com.zyin.zyinhud.util.InventoryUtil;
import com.zyin.zyinhud.util.ModCompatibility;

/**
 * Quick Deposit allows you to inteligently deposit every item in your inventory quickly into a chest.
 */
public class QuickDeposit extends ZyinHUDModBase
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
    
    public static boolean IgnoreItemsInHotbar;
    public static boolean CloseChestAfterDepositing;

    public static boolean BlacklistTorch;
    public static boolean BlacklistTools;
    public static boolean BlacklistWeapons;
    public static boolean BlacklistArrow;
    public static boolean BlacklistFood;
    public static boolean BlacklistEnderPearl;
    public static boolean BlacklistWaterBucket;
    public static boolean BlacklistClockCompass;
    
    
    /**
     * Deposits all items in your inventory into a chest, if the item exists in the chest
	 * @param onlyDepositMatchingItems only deposit an item if another one exists in the chest already
     */
    public static void QuickDepositItemsInChest(boolean onlyDepositMatchingItems)
    {
    	if(!(mc.currentScreen instanceof GuiContainer))
    	{
    		return;
    	}
    	
    	if(!QuickDeposit.Enabled)
    	{
    		return;
    	}
    	
    	try
    	{
	    	if(mc.currentScreen instanceof GuiBeacon
	    			|| mc.currentScreen instanceof GuiCrafting
	    			|| mc.currentScreen instanceof GuiEnchantment
	    			|| mc.currentScreen instanceof GuiRepair)
	    	{
	    		//we don't support these
	    		return;
	    	}
	    	else if(mc.currentScreen instanceof GuiMerchant)
	    	{
	    		InventoryUtil.DepositAllMatchingItemsInMerchant();
	    	}
	    	else if(mc.currentScreen instanceof GuiFurnace)
	    	{
	    		InventoryUtil.DepositAllMatchingItemsInFurance();
	    	}
	    	else if(mc.currentScreen instanceof GuiBrewingStand)
	    	{
	    		InventoryUtil.DepositAllMatchingItemsInBrewingStand();
	    	}
	    	else	//single chest, double chest, donkey/mules, hopper, dropper, dispenser
	    	{
	    		InventoryUtil.DepositAllMatchingItemsInContainer(onlyDepositMatchingItems, IgnoreItemsInHotbar);
	        	
	        	if(CloseChestAfterDepositing)
	        		mc.thePlayer.closeScreen();
	    	}
	
	    	ZyinHUDSound.PlayButtonPress();
    	}
    	catch (Exception e)
    	{
    		//Quick Deposit has a bad history of causing unpredictable crashes, so just catch all exceptions
    		e.printStackTrace();
    	}
    }
    
    /**
     * Determines if the item is allowed to be deposited in a chest based on the current list of blacklisted items
     * @param itemStack
     * @return true if it is allowed to be deposited
     */
	public static boolean IsAllowedToBeDepositedInContainer(ItemStack itemStack)
	{
		if(itemStack == null)
			return false;
		if((BlacklistTorch && itemStack.getItem() == Item.getItemFromBlock(Blocks.torch))
				|| (BlacklistTools && itemStack.getItem() instanceof ItemTool || itemStack.getItem() instanceof ItemHoe || itemStack.getItem() instanceof ItemShears || ModCompatibility.TConstruct.IsTConstructHarvestTool(itemStack.getItem()))
				|| (BlacklistWeapons && itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemBow)
				|| (BlacklistArrow && itemStack.getItem() == Items.arrow)
				|| (BlacklistEnderPearl && itemStack.getItem() == Items.ender_pearl)
				|| (BlacklistWaterBucket && itemStack.getItem() == Items.water_bucket)
				|| (BlacklistFood && (itemStack.getItem() instanceof ItemFood || itemStack.getItem() == Items.cake))
				|| (BlacklistClockCompass && (itemStack.getItem() == Items.compass || itemStack.getItem() == Items.clock)))
		{
			return false;
		}
		
		return true;
	}
	

    /**
     * Toggles depositing items in your hotbar
     * @return 
     */
    public static boolean ToggleIgnoreItemsInHotbar()
    {
    	return IgnoreItemsInHotbar = !IgnoreItemsInHotbar;
    }
    /**
     * Toggles depositing items in your hotbar
     * @return 
     */
    public static boolean ToggleCloseChestAfterDepositing()
    {
    	return CloseChestAfterDepositing = !CloseChestAfterDepositing;
    }
    /**
     * Toggles blacklisting this item
     * @return 
     */
    public static boolean ToggleBlacklistTorch()
    {
    	return BlacklistTorch = !BlacklistTorch;
    }
    /**
     * Toggles blacklisting this item
     * @return 
     */
    public static boolean ToggleBlacklistTools()
    {
    	return BlacklistTools = !BlacklistTools;
    }
    /**
     * Toggles blacklisting this item
     * @return 
     */
    public static boolean ToggleBlacklistWeapons()
    {
    	return BlacklistWeapons = !BlacklistWeapons;
    }
    /**
     * Toggles blacklisting this item
     * @return 
     */
    public static boolean ToggleBlacklistArrow()
    {
    	return BlacklistArrow = !BlacklistArrow;
    }
    /**
     * Toggles blacklisting this item
     * @return 
     */
    public static boolean ToggleBlacklistEnderPearl()
    {
    	return BlacklistEnderPearl = !BlacklistEnderPearl;
    }
    /**
     * Toggles blacklisting this item
     * @return 
     */
    public static boolean ToggleBlacklistFood()
    {
    	return BlacklistFood = !BlacklistFood;
    }
    /**
     * Toggles blacklisting this item
     * @return 
     */
    public static boolean ToggleBlacklistWaterBucket()
    {
    	return BlacklistWaterBucket = !BlacklistWaterBucket;
    }
    /**
     * Toggles blacklisting this item
     * @return 
     */
    public static boolean ToggleBlacklistClockCompass()
    {
    	return BlacklistClockCompass = !BlacklistClockCompass;
    }
}
