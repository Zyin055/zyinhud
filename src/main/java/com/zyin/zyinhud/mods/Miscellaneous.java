package com.zyin.zyinhud.mods;

import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import com.zyin.zyinhud.gui.GuiRepairOverride;
import com.zyin.zyinhud.util.InventoryUtil;
import com.zyin.zyinhud.util.ZyinHUDUtil;

/**
 * The Miscellaneous mod has other functionality not relating to anything specific.
 */
public class Miscellaneous extends ZyinHUDModBase
{
	public static final Miscellaneous instance = new Miscellaneous();

	public static boolean UseEnhancedMiddleClick;
	public static boolean UseQuickPlaceSign;
	public static boolean UseUnlimitedSprinting;
	public static boolean ShowAnvilRepairs;
	

    @SubscribeEvent
    public void GuiOpenEvent(GuiOpenEvent event)
	{
    	if(UseQuickPlaceSign && event.gui instanceof GuiEditSign && mc.thePlayer.isSneaking())
    	{
    		event.setCanceled(true);
    	}
    	if(ShowAnvilRepairs && event.gui instanceof GuiRepair)
    	{
    		event.gui = new GuiRepairOverride(mc.thePlayer.inventory, mc.theWorld);
    	}
	}
    
	/**
	 * When the player middle clicks
	 */
	public static void OnMiddleClick()
	{
		if(UseEnhancedMiddleClick)
			MoveMouseoveredBlockIntoHotbar();
	}
	
	
	/**
	 * Enhanced select block functionality (middle click). If the block exists in your inventory then
	 * it will put it into the hotbar, instead of it only working if the block is on your hotbar.
	 */
	public static void MoveMouseoveredBlockIntoHotbar()
	{
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            //Block block = ZyinHUDUtil.GetMouseOveredBlock();
        	BlockPos blockPos = ZyinHUDUtil.GetMouseOveredBlockPos();
            
            //Item blockItem = Item.getItemFromBlock(block);
            
    		//first, scan the hotbar to see if the mouseovered block already exists on the hotbar
            System.out.println("checking hotbar...");
            int itemIndexInHotbar = InventoryUtil.GetItemIndexFromHotbar(blockPos);
            System.out.println("returned "+itemIndexInHotbar);
            if(itemIndexInHotbar > 0)
            {
            	//if it does then do nothing since Minecraft takes care of it already
            }
            else
            {
            	//if it is not on the hotbar, check to see if it is in our inventory
                System.out.println("checking inventory...");
            	int itemIndexInInventory = InventoryUtil.GetItemIndexFromInventory(blockPos);
                System.out.println("returned "+itemIndexInInventory);
            	if(itemIndexInInventory > 0)
            	{
            		//if it is in our inventory, swap it out to the hotbar
            		InventoryUtil.Swap(InventoryUtil.GetCurrentlySelectedItemInventoryIndex(), itemIndexInInventory);
            	}
            }
        }
	}

    @SubscribeEvent
	public void ClientTickEvent(ClientTickEvent event)
	{
		//only play the sound if it's not playing already
		if(UseUnlimitedSprinting)
		{
			MakeSprintingUnlimited();
		}
	}
	
    
    /**
     * Lets the player sprint longer than 30 seconds at a time. Needs to be called on every game tick to be effective.
     */
	public static void MakeSprintingUnlimited()
	{
		if(mc.thePlayer == null)
			return;
		
		if(!mc.thePlayer.isSprinting())
			mc.thePlayer.sprintingTicksLeft = 0;
		else
			mc.thePlayer.sprintingTicksLeft = 600;	//sprintingTicksLeft is set to 600 when EntityPlayerSP.setSprinting() is called
	}
	
	

    /**
     * Toggles improving the middle click functionality to work with blocks in your inventory
     * @return 
     */
    public static boolean ToggleUseEnchancedMiddleClick()
    {
    	return UseEnhancedMiddleClick = !UseEnhancedMiddleClick;
    }
    
    /**
     * Toggles improving the middle click functionality to work with blocks in your inventory
     * @return 
     */
    public static boolean ToggleUseQuickPlaceSign()
    {
    	return UseQuickPlaceSign = !UseQuickPlaceSign;
    }
    
    /**
     * Toggles unlimited sprinting
     * @return 
     */
    public static boolean ToggleUseUnlimitedSprinting()
    {
    	return UseUnlimitedSprinting = !UseUnlimitedSprinting;
    }
    
    /**
     * Toggles showing anvil repairs
     * @return 
     */
    public static boolean ToggleShowAnvilRepairs()
    {
    	return ShowAnvilRepairs = !ShowAnvilRepairs;
    }
}
