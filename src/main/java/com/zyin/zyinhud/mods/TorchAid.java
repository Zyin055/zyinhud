package com.zyin.zyinhud.mods;


import java.awt.AWTException;
import java.awt.Robot;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.MovingObjectPosition;

import com.zyin.zyinhud.util.InventoryUtil;
import com.zyin.zyinhud.util.ModCompatibility;

/**
 * TorchAid Aid allows the player to easily use a torch without having it selected. It does this by
 * selecting a torch before the Use Block key is pressed, then unselecting the torch after the Use Block
 * key is released.
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
    
    
    private Robot r = null;
    
    /**
     * Use this instance for all method calls.
     */
    public static TorchAid instance = new TorchAid();

    private TorchAid()
    {
        try
        {
            r = new Robot();
        }
        catch (AWTException e)
        {
            e.printStackTrace();
        }
    }
    
    /** After the <code>EquipTorchIfToolIsEquipped()</code> function fires, this is set to the index of where the torch was in the inventory,
     * or the index of the hotbar slot that was selected. The <code>UnequipTorch()</code> function uses this value to determine
     * what to do next. -1 means there are no torches in inventory.*/
    private static int previousTorchIndex = -1;
    

    public void Pressed()
    {
    	if(TorchAid.Enabled)
    		EquipTorchIfToolIsEquipped();
    }
    
    public void Released()
    {
    	if(TorchAid.Enabled)
    		UnequipTorch();
    }
    
    /**
     * Makes the player place a Torch if they are currently using an axe, pickaxe, shovel, or have nothing in hand.
     */
    public void EquipTorchIfToolIsEquipped()
    {
    	if(mc.currentScreen == null && mc.inGameHasFocus)
    	{
    		ItemStack currentItemStack = mc.thePlayer.getHeldItem();
    		if(currentItemStack == null
    			|| currentItemStack.getItem() instanceof ItemTool
    			|| ModCompatibility.TConstruct.IsTConstructToolWithoutARightClickAction(currentItemStack.getItem()))
    		{
    			UseTorch();
    		}
    	}
    }
    
    /**
     * Makes the player place a Torch if they have any by selecting a Torch in their inventory then right clicking.
     */
    public void UseTorch()
    {
        if (EatingAid.instance.isEating())
        {
            EatingAid.instance.StopEating();    //it's not good if we have a torch selected and hold right click down...
        }
        
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            int torchHotbarIndex = InventoryUtil.GetItemIndexFromHotbar(Blocks.torch);
            
            if(torchHotbarIndex < 0)
            {
                int torchInventoryIndex = InventoryUtil.GetItemIndexFromInventory(Blocks.torch);

    			if(torchInventoryIndex >= 0)
    			{
    				previousTorchIndex = torchInventoryIndex;
    				EquipItemFromInventory(torchInventoryIndex);
    			}
    			else
    			{
    				//player has no torches
    				//don't display a notification because the player may be trying to interact with a useable block
    			}
            }
            else
            {
            	previousTorchIndex = InventoryUtil.TranslateHotbarIndexToInventoryIndex(mc.thePlayer.inventory.currentItem);
            	EquipItemFromHotbar(torchHotbarIndex);
            }
        }
    }
    
    /**
     * Selects the item at the specified inventory index by swapping it with the currently held item.
     * @param inventoryIndex 9-35
     */
    private void EquipItemFromInventory(int inventoryIndex)
    {
    	if(inventoryIndex < 9 || inventoryIndex > 35)
    		return;

        int currentItemInventoryIndex = InventoryUtil.GetCurrentlySelectedItemInventoryIndex();
        
        InventoryUtil.Swap(currentItemInventoryIndex, inventoryIndex);
    }
    
    /**
     * Selects the item at specified hotbar index.
     * @param hotbarIndex 36-44
     */
    private void EquipItemFromHotbar(int hotbarIndex)
    {
    	if(hotbarIndex < 36 || hotbarIndex > 44)
    		return;
    	
    	hotbarIndex = InventoryUtil.TranslateInventoryIndexToHotbarIndex(hotbarIndex);
    	
    	mc.thePlayer.inventory.currentItem = hotbarIndex;
    }
    

    /**
     * Uses the <code>previousTorchIndex</code> variable to determine how to unequip the currently held torch.
     * after placing one.
     */
    private void UnequipTorch()
    {
    	if(previousTorchIndex < 0)
    		return;
    	else
    	{
        	if(previousTorchIndex >= 36 && previousTorchIndex <= 44)	//on the hotbar
        	{
        		mc.thePlayer.inventory.currentItem = InventoryUtil.TranslateInventoryIndexToHotbarIndex(previousTorchIndex);
        	}
        	else
        	{
        		InventoryUtil.Swap(InventoryUtil.TranslateHotbarIndexToInventoryIndex(mc.thePlayer.inventory.currentItem), previousTorchIndex);
        	}
    	}
    	previousTorchIndex = -1;
    }
}
