package zyin.zyinhud.mods;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import zyin.zyinhud.util.InventoryUtil;

/**
 * Quick Deposit allows you to inteligently deposit every item in your inventory quickly into a chest.
 */
public class QuickDeposit
{
	/** Enables/Disables this Mod */
	public static boolean Enabled;

    /**
     * Toggles this Mod on or off
     * @return The state the Mod was changed to
     */
    public static boolean ToggleEnabled()
    {
    	Enabled = !Enabled;
    	return Enabled;
    }
    
    public static String Hotkey;
    public static final String HotkeyDescription = "Zyin's HUD: Quick Deposit";
    
    private static Minecraft mc = Minecraft.getMinecraft();

    public static boolean IgnoreItemsInHotbar;
    public static boolean CloseChestAfterDepositing;

    public static boolean BlacklistTorch;
    public static boolean BlacklistFood;
    public static boolean BlacklistEnderPearl;
    public static boolean BlacklistArrow;
    public static boolean BlacklistWaterBucket;
    public static boolean BlacklistClockCompass;
    
    /**
     * Deposits all items in your inventory into a chest, if the item exists in the chest
     */
    public static void QuickDepositItemsInChest()
    {
    	if(!(mc.currentScreen instanceof GuiContainer))
    	{
    		return;
    	}
    	if(!QuickDeposit.Enabled)
    	{
    		return;
    	}
    	
    	
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
        	InventoryUtil.DepositAllMatchingItemsInContainer(IgnoreItemsInHotbar);
        	
        	if(CloseChestAfterDepositing)
        		mc.thePlayer.closeScreen();
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
		
		if((BlacklistTorch && itemStack.itemID == Block.torchWood.blockID)
				|| (BlacklistArrow && itemStack.itemID == Item.arrow.itemID)
				|| (BlacklistEnderPearl && itemStack.itemID == Item.enderPearl.itemID)
				|| (BlacklistWaterBucket && itemStack.itemID == Item.bucketWater.itemID)
				|| (BlacklistFood && (itemStack.getItem() instanceof ItemFood || itemStack.itemID == Item.cake.itemID))
				|| (BlacklistClockCompass && (itemStack.itemID == Item.compass.itemID || itemStack.itemID == Item.pocketSundial.itemID)))
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
    	IgnoreItemsInHotbar = !IgnoreItemsInHotbar;
    	return IgnoreItemsInHotbar;
    }
    /**
     * Toggles depositing items in your hotbar
     * @return 
     */
    public static boolean ToggleCloseChestAfterDepositing()
    {
    	CloseChestAfterDepositing = !CloseChestAfterDepositing;
    	return CloseChestAfterDepositing;
    }
    /**
     * Toggles blacklisting this item
     * @return 
     */
    public static boolean ToggleBlacklistTorch()
    {
    	BlacklistTorch = !BlacklistTorch;
    	return BlacklistTorch;
    }
    /**
     * Toggles blacklisting this item
     * @return 
     */
    public static boolean ToggleBlacklistArrow()
    {
    	BlacklistArrow = !BlacklistArrow;
    	return BlacklistArrow;
    }
    /**
     * Toggles blacklisting this item
     * @return 
     */
    public static boolean ToggleBlacklistEnderPearl()
    {
    	BlacklistEnderPearl = !BlacklistEnderPearl;
    	return BlacklistEnderPearl;
    }
    /**
     * Toggles blacklisting this item
     * @return 
     */
    public static boolean ToggleBlacklistFood()
    {
    	BlacklistFood = !BlacklistFood;
    	return BlacklistFood;
    }
    /**
     * Toggles blacklisting this item
     * @return 
     */
    public static boolean ToggleBlacklistWaterBucket()
    {
    	BlacklistWaterBucket = !BlacklistWaterBucket;
    	return BlacklistWaterBucket;
    }
    /**
     * Toggles blacklisting this item
     * @return 
     */
    public static boolean ToggleBlacklistClockCompass()
    {
    	BlacklistClockCompass = !BlacklistClockCompass;
    	return BlacklistClockCompass;
    }

    
}
