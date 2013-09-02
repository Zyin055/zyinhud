package zyin.zyinhud.util;

import java.awt.event.InputEvent;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Utility class to help with inventory management.
 * Useful for getting item indexes from your inventory and swapping item positions.
 */
public class InventoryUtil
{
	/* -------------
	 * -Useful Info-
	 * -------------
	 * "Inventory" in the method names refers to slots 9-44, and 
	 * "Hotbar" in the method names referes to slots 36-44.
	 * 
	 * -------------
	 * mc.thePlayer.inventoryContainer.inventorySlots index values:
     *	0 = crafting output?
     *	1-4 = 2x2 crafting grid
     *	5-8 = armor
     *	9-35 = inventory
     *	36-44 = hotbar
     *
     * -------------
     * mc.thePlayer.inventory.mainInventory index values:
     *  0-8 = hotbar
     *  9-35 = inventory
     *  
     * -------------
     * 
	 */

    private static Minecraft mc = Minecraft.getMinecraft();
    private static PlayerControllerMP playerController = mc.playerController;
    private Timer timer = new Timer();
    
    /**
     * Minimum suggested delay between swapping items around.
     * We should use a higher value for laggier servers.
     */
    public static int suggestedItemSwapDelay;

    /**
     * Use this instance in order to use the SwapWithDelay() method call.
     */
    public static InventoryUtil instance = new InventoryUtil();

    
    
    private InventoryUtil()
    {
    	//on single player there is very little lag, so we can set the delay betwen swapping items around
    	//to be very small, but the ping on servers requires us to have a larger value in order to work more reliably.
    	if(mc.isSingleplayer())
    		suggestedItemSwapDelay = 150;
    	else
    		suggestedItemSwapDelay = 450;
    }

    


	/**
	 * Uses an item locaed in your inventory or hotbar.
	 * <p>
	 * If it is in your hotbar, it will change the selected hotbar index in order to use it.
	 * <br>If it is in your inventory, it will swap the item into your hotbar in order to use it.
	 * @param itemClass example: ItemEnderPearl.class
	 * @return true if the item was used.
	 */
	public static boolean UseItem(Class itemClass)
	{
		int hotbarIndex = GetItemIndexFromHotbar(itemClass);
		if(hotbarIndex < 0)
		{
			int inventoryIndex = GetItemIndexFromInventory(itemClass);
			if(inventoryIndex < 0)
				return false;
			else
				return UseItemInInventory(itemClass);
		}
		else
			return UseItemInHotbar(itemClass);
	}
	
    
	/**
	 * Uses an item locaed in your inventory or hotbar.
	 * <p>
	 * If it is in your hotbar, it will change the selected hotbar index in order to use it.
	 * <br>If it is in your inventory, it will swap the item into your hotbar in order to use it.
	 * @param itemIndex (9-35) for items in your inventory, (36-44) for items in your hotbar
	 * @return true if the item was used.
	 */
	public static boolean UseItem(int itemIndex)
	{
		if(itemIndex > 8 && itemIndex <= 35)		//inventory
			return UseItemInInventory(itemIndex);
		else if(itemIndex > 35 && itemIndex <= 44)	//hotbar
			return UseItemInHotbar(itemIndex);
		else
			return false;
	}

	
	/**
	 * Uses an item in the players hotbar by changing the selected index, using it, then changing it back.
	 * @param itemClass example: ItemEnderPearl.class
	 * @return true if the item was used.
	 */
	private static boolean UseItemInHotbar(Class itemClass)
	{
		int itemHotbarIndex = GetItemIndexFromHotbar(itemClass);

		return UseItemInHotbar(itemHotbarIndex);
	}

	
	/**
	 * Uses an item in the players hotbar by changing the selected index, using it, then changing it back.
	 * @param itemSlotIndex 36-44
	 * @return true if the item was used.
	 */
	private static boolean UseItemInHotbar(int itemSlotIndex)
	{
		if(itemSlotIndex < 36 || itemSlotIndex > 44)
			return false;
		
		int itemToUseHotbarIndex = TranslateInventoryIndexToHotbarIndex(itemSlotIndex);
		
		int previouslySelectedHotbarSlotIndex = mc.thePlayer.inventory.currentItem;
    	mc.thePlayer.inventory.currentItem = itemToUseHotbarIndex;

    	
    	ItemStack currentItemStack = mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem];
    	playerController.sendUseItem((EntityPlayer)mc.thePlayer, (World)mc.theWorld, currentItemStack);

    	mc.thePlayer.inventory.currentItem = previouslySelectedHotbarSlotIndex;
        
    	return true;
	}

	
	/**
	 * Uses an item in the players inventory by quickly Swap()ing it into the hotbar, using it, then Swap()ing it back.
	 * @param itemClass example: ItemEnderPearl.class
	 * @return true if the item was used.
	 */
	private static boolean UseItemInInventory(Class itemClass)
	{
		int itemInventoryIndex = GetItemIndexFromInventory(itemClass);

		return UseItemInInventory(itemInventoryIndex);
	}

	
	/**
	 * Uses an item in the players inventory by quickly Swap()ing it into the hotbar, using it, then Swap()ing it back.
	 * @param itemSlotIndex 0-35
	 * @return true if the item was used.
	 */
	private static boolean UseItemInInventory(int itemSlotIndex)
	{
		if(itemSlotIndex < 0 || itemSlotIndex > 35)
			return false;

		int currentItemInventoryIndex = TranslateHotbarIndexToInventoryIndex(mc.thePlayer.inventory.currentItem);

		Swap(itemSlotIndex, currentItemInventoryIndex);

		Slot slotToUse = (Slot)mc.thePlayer.inventoryContainer.inventorySlots.get(currentItemInventoryIndex);
		ItemStack itemStackToUse = slotToUse.getStack();
        playerController.sendUseItem((EntityPlayer)mc.thePlayer, (World)mc.theWorld, itemStackToUse);

        instance.SwapWithDelay(itemSlotIndex, currentItemInventoryIndex, suggestedItemSwapDelay);

    	return true;
	}

	
	/**
	 * Swaps 2 items in your inventory after a specified amount of time has passed.
	 * @param srcIndex
	 * @param destIndex
	 * @param delay in milliseconds
	 * @return the TimerTask associated with this delayed action. Use it if you want to cancel it later.
	 */
	public TimerTask SwapWithDelay(int srcIndex, int destIndex, int delay)
	{
		TimerTask swapTimerTask = new SwapTimerTask(srcIndex, destIndex);
		timer.schedule(swapTimerTask, delay);
		return swapTimerTask;
	}

	
	/**
	 * Swaps 2 items in your inventory.
	 * @param srcIndex
	 * @param destIndex
	 * @return
	 */
	public static boolean Swap(int srcIndex, int destIndex)
	{
		if(srcIndex == destIndex)
			return false;

		List inventorySlots = mc.thePlayer.inventoryContainer.inventorySlots;

	    ItemStack srcStack = ((Slot)inventorySlots.get(srcIndex)).getStack();
	    ItemStack destStack = ((Slot)inventorySlots.get(destIndex)).getStack();
	    
	    

	    ItemStack handStack = mc.thePlayer.inventory.getItemStack();
	    if(handStack != null)
	    {
	    	int emptyIndex = GetFirstEmptyIndex();
	    	if(emptyIndex < 0)
	    		emptyIndex = 1;	//use the crafting area
	    	
	    	LeftClickInventorySlot(emptyIndex);
	    }
	    
	    //there are 4 cases we need to handle:
	    //1: src = null, dest = null
	    if(srcStack == null && destStack == null)
	    {
	    	return false;
	    }
	    //2: src = null, dest = item
	    else if(srcStack == null && destStack != null)
	    {
		    LeftClickInventorySlot(destIndex);
		    LeftClickInventorySlot(srcIndex);
			return true;
	    }
	    //3: src = item, dest = null
	    else if(srcStack != null && destStack == null)
	    {
		    LeftClickInventorySlot(srcIndex);
		    LeftClickInventorySlot(destIndex);
			return true;
	    }
	    //4: src = item, dest = item
	    else// if(srcStack != null && destStack != null)
	    {
	    	/*if(srcStack.itemID == destStack.itemID)
	    	{
	    		//if the 2 items are the same, do nothing
	    		return false;
	    	}*/
	    	

		    //LeftClickInventorySlot(srcIndex);
		    //LeftClickInventorySlot(destIndex);
		    //LeftClickInventorySlot(srcIndex);
	    	
	    	//using the 3 commented lines of code above may result in items not moving properly due to server lag,
	    	//so if we use the method that uses the temporary storage, it helps it out a little.
	    	
	    	int emptyIndex = 1;	//use the 2x2 crafting grid as temporary storage

	    	LeftClickInventorySlot(srcIndex);
	    	LeftClickInventorySlot(emptyIndex);
	    	LeftClickInventorySlot(destIndex);
	    	LeftClickInventorySlot(srcIndex);
	    	LeftClickInventorySlot(emptyIndex);
	    	LeftClickInventorySlot(destIndex);
	    	
			return true;
	    }
	}

	
	/**
	 * Gets the index of an item class in your inventory.
	 * @param itemClass example: ItemEnderPearl.class
	 * @return 5-44, -1 if not found
	 */
	private static int GetItemIndexFromInventory(Class itemClass)
    {
		List inventorySlots = mc.thePlayer.inventoryContainer.inventorySlots;

		//iterate over the main inventory (9-35)
    	for (int i = 9; i <= 35; i++)
        {
    		Slot slot = (Slot)inventorySlots.get(i);
			ItemStack itemStack = slot.getStack();
			if(itemStack != null)
			{
                Item item = itemStack.getItem();
                if(item.getClass().getName().equals(itemClass.getName()))
                {
                	return i;
                }
			}
        }

        return -1;
    }
	
	
	/**
	 * Gets the index of an item class in your hotbar.
	 * @param itemClass example: ItemEnderPearl.class
	 * @return 36-44, -1 if not found
	 */
	private static int GetItemIndexFromHotbar(Class itemClass)
    {
		List inventorySlots = mc.thePlayer.inventoryContainer.inventorySlots;

		//iterate over the hotbar (36-44)
    	for (int i = 36; i <= 44; i++)
        {
    		Slot slot = (Slot)inventorySlots.get(i);
			ItemStack itemStack = slot.getStack();
			if(itemStack != null)
			{
                Item item = itemStack.getItem();
                if(item.getClass().getName().equals(itemClass.getName()))
                {
                	return i;
                }
			}
        }
    	
        return -1;
    }
	

	/**
	 * Gets the index in your inventory of the first empty slot.
	 * @return 9-44, -1 if no empty spot
	 */
	private static int GetFirstEmptyIndex()
	{
		List inventorySlots = mc.thePlayer.inventoryContainer.inventorySlots;

		//iterate over the main inventory (9-35) then the hotbar (36-44)
    	for (int i = 9; i <= 44; i++)
        {
    		Slot slot = (Slot)inventorySlots.get(i);
			ItemStack itemStack = slot.getStack();
			if(itemStack != null)
			{
                return i;
			}
        }

        return -1;
	}

	
	/**
	 * Gets the index of whatever the player currently has selected on their hotbar
	 * @return 36-44
	 */
	public static int GetCurrentlySelectedItemInventoryIndex()
	{
		return TranslateHotbarIndexToInventoryIndex(mc.thePlayer.inventory.currentItem);
	}

	
	/**
	 * Converts hotbar indexes (0-8) to inventory indexes (36-44)
	 * @param hotbarIndex
	 * @return 36-44, -1 if not a valid index
	 */
	public static int TranslateHotbarIndexToInventoryIndex(int hotbarIndex)
	{
		if(hotbarIndex < 0 || hotbarIndex > 8)
			return -1;

		return hotbarIndex + 36;
	}
	
	
	/**
	 * Converts inventory indexes (9-35) to hotbar index (0-8)
	 * @param inventoryIndex
	 * @return 0-8, -1 if not a valid index
	 */
	public static int TranslateInventoryIndexToHotbarIndex(int inventoryIndex)
	{
		if(inventoryIndex < 36 || inventoryIndex > 44)
			return -1;

		return inventoryIndex - 36;
	}

	
    /**
     * Simulates a left click as if your inventory screen was open at the specified item slot index.
     * @param itemIndex the item Slot index
     */
    private static void LeftClickInventorySlot(int itemIndex)
    {
        SendInventoryClick(itemIndex, false, false);
    }

    //private void RightClickInventorySlot(int index)
    //{
    //    SendInventoryClick(index, true, false);
    //}

    
    /**
     * Simulates a left click if your inventory screen was open at the specified item slot index.
     * @param itemIndex the item Slot index
     * @param rightClick is right click held?
     * @param shiftHold is shift held?
     */
    private static void SendInventoryClick(int itemIndex, boolean rightClick, boolean shiftHold)
    {
        if (itemIndex < 0 || itemIndex > 44)
        	return;
        
        playerController.windowClick(
        		mc.thePlayer.inventoryContainer.windowId,
        		itemIndex,
        		(rightClick) ? 1 : 0,
				(shiftHold) ? 1 : 0,
				mc.thePlayer);
    }
    
    
    
    /**
     * Helper class whose purpose is to release right click and reselect the player's last selected item.
     */
    class SwapTimerTask extends TimerTask
    {
    	private int srcIndex;
    	private int destIndex;

    	public SwapTimerTask(int srcIndex, int destIndex)
    	{
    		this.srcIndex = srcIndex;
    		this.destIndex = destIndex;
    	}

    	@Override
        public void run()
        {
        	Swap(srcIndex, destIndex);
        }
    }
}
