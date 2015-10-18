package com.zyin.zyinhud.util;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import com.zyin.zyinhud.mods.QuickDeposit;

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
    private Timer timer = new Timer();
    
    /**
     * Minimum suggested delay between swapping items around.
     * We should use a higher value for laggier servers.
     */
    private static int suggestedItemSwapDelay;

    /**
     * Use this instance in order to use the SwapWithDelay() method call.
     */
    public static InventoryUtil instance = new InventoryUtil();

    
    
    private InventoryUtil()
    {
    	suggestedItemSwapDelay = GetSuggestedItemSwapDelay();
    }

    

    /**
     * Determines an appropriate duration in milliseconds that should be used as the delay for swapping items
     * around in the inventory.
     * @return
     */
	public static int GetSuggestedItemSwapDelay()
	{
    	//on single player there is very little lag, so we can set the delay betwen swapping items around
    	//to be very small, but the ping on servers requires us to have a larger value in order to work more reliably.
    	if(mc.isSingleplayer())
    		return suggestedItemSwapDelay = 170;
    	else
    		return suggestedItemSwapDelay = 450;
	}


	/**
	 * Uses an item locaed in your inventory or hotbar.
	 * <p>
	 * If it is in your hotbar, it will change the selected hotbar index in order to use it.
	 * <br>If it is in your inventory, it will swap the item into your hotbar in order to use it.
	 * @param object The type of item being used. E.x.: Blocks.torch, Items.ender_pearl
	 * @return true if the item was used.
	 */
	public static boolean UseItem(Object object)
	{
		int hotbarIndex = GetItemIndexFromHotbar(object);
		if(hotbarIndex < 0)
		{
			int inventoryIndex = GetItemIndexFromInventory(object);
			if(inventoryIndex < 0)
				return false;
			else
				return UseItemInInventory(object);
		}
		else
			return UseItemInHotbar(object);
	}

	
	/**
	 * Uses an item in the players hotbar by changing the selected index, using it, then changing it back.
	 * @param object The type of item being used. E.x.: Blocks.torch, Items.ender_pearl
	 * @return true if the item was used.
	 */
	public static boolean UseItemInHotbar(Object object)
	{
		int itemHotbarIndex = GetItemIndexFromHotbar(object);

		return UseItemInHotbar(object, itemHotbarIndex);
	}


	/**
	 * Uses an item in the players hotbar by changing the selected index, using it, then changing it back.
	 * @param object The type of item being used. E.x.: Blocks.torch, Items.ender_pearl
	 * @param itemSlotIndex 36-44
	 * @return true if the item was used.
	 */
	public static boolean UseItemInHotbar(Object object, int itemSlotIndex)
	{
		if(itemSlotIndex < 36 || itemSlotIndex > 44)
			return false;
		
		int itemToUseHotbarIndex = TranslateInventoryIndexToHotbarIndex(itemSlotIndex);
		
		int previouslySelectedHotbarSlotIndex = mc.thePlayer.inventory.currentItem;
    	mc.thePlayer.inventory.currentItem = itemToUseHotbarIndex;

		boolean wasUsedSuccessfully = false;
		
    	if(object instanceof Item)
    	{
    		wasUsedSuccessfully = SendUseItem();
    	}
    	else if(object instanceof Block)
    	{
    		wasUsedSuccessfully = SendUseBlock();
    	}
    	
    	mc.thePlayer.inventory.currentItem = previouslySelectedHotbarSlotIndex;
        
    	return wasUsedSuccessfully;
	}

	
	/**
	 * Uses an item in the players inventory by quickly Swap()ing it into the hotbar, using it, then Swap()ing it back.
	 * @param object The type of item being used. E.x.: Blocks.torch, Items.ender_pearl
	 * @return true if the item was used.
	 */
	public static boolean UseItemInInventory(Object object)
	{
		int itemInventoryIndex = GetItemIndexFromInventory(object);

		return UseItemInInventory(object, itemInventoryIndex);
	}

	
	/**
	 * Uses an item in the players inventory by quickly Swap()ing it into the hotbar, using it, then Swap()ing it back.
	 * @param object The type of item being used. E.x.: Blocks.torch, Items.ender_pearl
	 * @param itemSlotIndex 0-35
	 * @return true if the item was used.
	 */
	public static boolean UseItemInInventory(Object object, int itemSlotIndex)
	{
		if(itemSlotIndex < 0 || itemSlotIndex > 35)
			return false;
		
		int previouslySelectedHotbarSlotIndex = mc.thePlayer.inventory.currentItem;
		mc.thePlayer.inventory.currentItem = 0;	//use the first hotbar slot so that mods that extend the vanilla hotbar will be compatible

		int currentItemInventoryIndex = TranslateHotbarIndexToInventoryIndex(mc.thePlayer.inventory.currentItem);

		Swap(itemSlotIndex, currentItemInventoryIndex);
		
		boolean wasUsedSuccessfully = false;
		
    	if(object instanceof Item)
    	{
    		wasUsedSuccessfully = SendUseItem();
    	}
    	else if(object instanceof Block)
    	{
    		wasUsedSuccessfully = SendUseBlock();
    	}
		
        instance.SwapWithDelay(itemSlotIndex, currentItemInventoryIndex, GetSuggestedItemSwapDelay());
        
        mc.thePlayer.inventory.currentItem = previouslySelectedHotbarSlotIndex;

    	return wasUsedSuccessfully;
	}
	
	/**
	 * Makes the player use the Item in their currently selected hotbar slot.
	 * To use Blocks, use SendUseBlock()
	 * @return 
	 */
	public static boolean SendUseItem()
	{
		//Items need to use the sendUseItem() function to work properly (only works for instant-use items, NOT something like food!)
		boolean sendUseItem = mc.playerController.sendUseItem((EntityPlayer)mc.thePlayer, (World)mc.theWorld, mc.thePlayer.getHeldItem());
		return sendUseItem;
	}
	
	/**
	 * Makes the player use the Block in their currently selected hotbar slot.
	 * To use Items, use SendUseItem()
	 */
	public static boolean SendUseBlock()
	{
		//Blocks need to use the onPlayerRightClick() function to work properly
		//return mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ, mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec);
		
		boolean sendUseBlock = mc.playerController.func_178890_a(mc.thePlayer, 
				mc.theWorld, 
				mc.thePlayer.getHeldItem(), 
				new BlockPos(mc.objectMouseOver.hitVec.xCoord, mc.objectMouseOver.hitVec.yCoord, mc.objectMouseOver.hitVec.zCoord), 
				mc.objectMouseOver.field_178784_b, //EnumFacing
				mc.objectMouseOver.hitVec);
		BlockPos pos = new BlockPos(mc.objectMouseOver.hitVec.xCoord, mc.objectMouseOver.hitVec.yCoord, mc.objectMouseOver.hitVec.zCoord);
		return sendUseBlock;
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
		try
		{
			timer.schedule(swapTimerTask, delay);
		}
		catch (IllegalStateException e)
		{
			//IllegalStateException: Timer already cancelled.
		}
		return swapTimerTask;
	}

	
	/**
	 * Swaps 2 items in your inventory GUI.
	 * @param srcIndex
	 * @param destIndex
	 * @return true if the items were successfully swapped
	 */
	public static boolean Swap(int srcIndex, int destIndex)
	{
		if(srcIndex == destIndex
			|| srcIndex < 0
			|| destIndex < 0)
			return false;

		List inventorySlots = mc.thePlayer.inventoryContainer.inventorySlots;

	    ItemStack srcStack = ((Slot)inventorySlots.get(srcIndex)).getStack();
	    ItemStack destStack = ((Slot)inventorySlots.get(destIndex)).getStack();
	    
	    
	    ItemStack handStack = mc.thePlayer.inventory.getItemStack();
	    if(handStack != null)
	    {
	    	int emptyIndex = GetFirstEmptyIndexInInventory();
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
	 * Deposits all items in the players inventory, including any item being held on the cursor, into the chest
	 * as long as there is a matching item already in the chest.
	 * <br>
	 * <br>Only works with single chest, double chest, donkey/mules, hopper, dropper, and dispenser. For other containers,
	 * use their specific methods: DepositAllMatchingItemsInMerchant(), DepositAllMatchingItemsInFurance(), and
	 * DepositAllMatchingItemsInBrewingStand().
	 * @param onlyDepositMatchingItems only deposit an item if another one exists in the chest already
	 * @param ignoreItemsInHotbar if true, won't deposit items that are in the player's hotbar
	 * @return true if operation completed successfully, false if some items were left behind (aka there was a full chest)
	 */
	public static boolean DepositAllMatchingItemsInContainer(boolean onlyDepositMatchingItems, boolean ignoreItemsInHotbar)
	{
	    //check to see if the player is holding an item
	    ItemStack handStack = mc.thePlayer.inventory.getItemStack();
	    if(handStack != null)
	    {
	    	int emptyIndex;
	    	//if we can't deposit this item being held in the cursor, put it down in our inventory
		    if(!QuickDeposit.IsAllowedToBeDepositedInContainer(handStack))
		    {
		    	emptyIndex = GetFirstEmptyIndexInContainerInventory();
	    		if(emptyIndex < 0)
	    			return false;
	    		else
	    			LeftClickContainerSlot(emptyIndex);
		    }
		    //if we can deposit this item being held in the cursor, put it in the chest
		    else
		    {
		    	emptyIndex = GetFirstItemIndexInContainer(handStack);
		    	if(emptyIndex < 0)
		    	{
		    		emptyIndex = GetFirstEmptyIndexInContainerInventory();
		    		if(emptyIndex < 0)
		    			return false;
		    		else
		    			LeftClickContainerSlot(emptyIndex);
		    	}
		    	else
		    	{
		    		LeftClickContainerSlot(emptyIndex);
			    	
			    	//keep putting into next available slot until we deposit all the items in this stack
				    handStack = mc.thePlayer.inventory.getItemStack();
				    while(handStack != null)
				    {
				    	emptyIndex = GetFirstEmptyIndexInContainer(handStack);
				    	if(emptyIndex < 0)
				    		return false;
				    	
				    	LeftClickContainerSlot(emptyIndex);
					    handStack = mc.thePlayer.inventory.getItemStack();
				    }
		    	}
		    }
	    }
	    
	    
		List chestSlots = mc.thePlayer.openContainer.inventorySlots;
	    
    	int numDisplayedSlots = mc.thePlayer.openContainer.inventorySlots.size();
	    
	    int numInventorySlots = 36;
	    int numChestSlots = numDisplayedSlots - numInventorySlots;
	    
	    //some 3rd party mods have containers with no slots (such as Pixelmon)
	    if(numChestSlots <= 0)
	    	return false;
	    
	    int iStart = numChestSlots;
	    int iEnd = numDisplayedSlots;
	    
	    if(ignoreItemsInHotbar)
	    	iEnd -= 9;
	    
	    //iterate over the player's inventory and deposit items as needed
	    for(int i = iStart; i < iEnd; i++)
	    {
	    	Slot slot = (Slot)chestSlots.get(i);
			ItemStack itemStack = slot.getStack();
			if(itemStack != null)
			{
			    
				if(onlyDepositMatchingItems)
				{
				    int itemIndex = GetFirstItemIndexInContainer(itemStack);
				    
				    //if the item exists in the chest
				    if(itemIndex >= 0) {
				    	DepositItemInContainer(i, itemIndex);
				    }
				}
				else
				{
				    int emptyIndex = GetFirstEmptyIndexInContainer(itemStack);
				    
				    //if an empty spot exists in the chest
				    if(emptyIndex >= 0)
				    	DepositItemInContainer(i, emptyIndex);
				    else
				    	return true;
				}
			}
	    }
	    return true;
	}
	
	/**
	 * Moves an item from the players inventory to a chest or horse inventory. It assumes that no ItemStack is being held on the cursor.
	 * @param srcIndex player inventory slot: single chest = 28-63, double chest = 55-90
	 * @param destIndex chest slot: single chest = 0-27, double chest = 0-54
	 * @return true if an item was successfully moved
	 */
	public static boolean DepositItemInContainer(int srcIndex, int destIndex)
	{
		//horse chest + player invetory = 53 big
	    //single chest + player invetory = 63 big
	    //double chest + player invetory = 90 big
	    int numDisplayedSlots = mc.thePlayer.openContainer.inventorySlots.size();

	    //the last 4 rows (9*4=36) are the player's inventory
	    int numInventorySlots = 36;

		//horse chest = 17 big
	    //single chest = 27 big
	    //double chest = 54 big
	    int numContainerSlots = numDisplayedSlots - numInventorySlots;

		if(numContainerSlots == 53-numInventorySlots && (srcIndex < 18 || srcIndex > 53))
			return false;
		if(numContainerSlots == 63-numInventorySlots && (srcIndex < 28 || srcIndex > 63))
			return false;
		if(numContainerSlots == 90-numInventorySlots && (srcIndex < 55 || srcIndex > 90))
			return false;
		
		if(destIndex < 0)
			return false;
		if(numContainerSlots == 53-numInventorySlots && (destIndex < 0 || destIndex > 17))
			return false;
		if(numContainerSlots == 63-numInventorySlots && (destIndex < 0 || destIndex > 27))
			return false;
		if(numContainerSlots == 90-numInventorySlots && (destIndex < 0 || destIndex > 54))
			return false;
	    
	    ItemStack srcStack = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(srcIndex)).getStack();
	    ItemStack destStack = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(destIndex)).getStack();
	    

	    if(!QuickDeposit.IsAllowedToBeDepositedInContainer(srcStack))
	    	return true;
	    
	    
	    //there are 4 cases we need to handle:
	    //1: src = null, dest = null
	    if(srcStack == null && destStack == null)
	    {
	    	return false;
	    }
	    //2: src = null, dest = item
	    else if(srcStack == null && destStack != null)
	    {
			return false;
	    }
	    //3: src = item, dest = null
	    else if(srcStack != null && destStack == null)
	    {
	    	LeftClickContainerSlot(srcIndex);
		    LeftClickContainerSlot(destIndex);
			return true;
	    }
	    //4: src = item, dest = item
	    else// if(srcStack != null && destStack != null)
	    {
	    	//if the 2 items are of different item types
	    	if(srcStack.getItem() != destStack.getItem())
	    	{
		    	return false;
	    	}
	    	//if the 2 items are the same, stack as much as we can into the spot then place the leftovers in a new slot
	    	else// if(srcStack.itemID == destStack.itemID)
	    	{
	    		//there are 3 cases we need to handle:
	    		//1: dest is a full stack
	    		if(destStack.stackSize == destStack.getMaxStackSize())
	    		{
	    			//put this in the next available slot
	    			int emptyIndex = GetFirstEmptyIndexInContainer(destStack);
	    			if(emptyIndex < 0)
			    	{
			    		return false;
			    	}
	    			
			    	LeftClickContainerSlot(srcIndex);
				    LeftClickContainerSlot(emptyIndex);
				    
				    //keep putting into next available slot until we deposit all the items in this stack
				    ItemStack handStack = mc.thePlayer.inventory.getItemStack();
				    while(handStack != null)
				    {
				    	emptyIndex = GetFirstEmptyIndexInContainer(destStack);
				    	if(emptyIndex < 0)
				    	{
				    		LeftClickContainerSlot(srcIndex);
				    		return false;
				    	}
				    	
				    	LeftClickContainerSlot(emptyIndex);
					    handStack = mc.thePlayer.inventory.getItemStack();
				    }
				    
				    return true;
	    		}
	    		//2: if the combined stacks overflow past the stack limit
	    		else if(srcStack.stackSize + destStack.stackSize > destStack.getMaxStackSize())
	    		{
	    			int emptyIndex = GetFirstEmptyIndexInContainer(destStack);
	    			if(emptyIndex < 0)
			    	{
			    		LeftClickContainerSlot(destIndex);
			    		LeftClickContainerSlot(srcIndex);
			    		return false;
			    	}
	    			
			    	LeftClickContainerSlot(srcIndex);
				    LeftClickContainerSlot(destIndex);

				    //keep putting into next available slot until we deposit all the items in this stack
				    ItemStack handStack = mc.thePlayer.inventory.getItemStack();
				    while(handStack != null)
				    {
				    	emptyIndex = GetFirstEmptyIndexInContainer(destStack);
				    	if(emptyIndex < 0)
				    	{
				    		LeftClickContainerSlot(srcIndex);
				    		return false;
				    	}

				    	LeftClickContainerSlot(emptyIndex);
					    handStack = mc.thePlayer.inventory.getItemStack();
				    }

				    return true;
	    		}
	    		//3: if the combined stacks fit into one slot
	    		else
	    		{
			    	LeftClickContainerSlot(srcIndex);
				    LeftClickContainerSlot(destIndex);
				    
				    return true;
	    		}
	    	}
	    }
	}
	
	/**
	 * Deposits all items in the players inventory, including any item being held on the cursor, into the chest
	 * as long as there is a matching item already in the chest.
	 * @return
	 */
	public static boolean DepositAllMatchingItemsInMerchant()
	{
	    if(!(mc.currentScreen instanceof GuiMerchant))
	    	return false;
	    
	    //villager container = 39 big
	    //slot 0 = left buy slot
	    //slot 1 = right buy slot
	    //slot 2 = sell slot
	    //the last 4 rows (9*4=36) are the player's inventory
	    int numDisplayedSlots = mc.thePlayer.openContainer.inventorySlots.size();
	    
	    int numInventorySlots = 36;
	    int numMerchantSlots = numDisplayedSlots - numInventorySlots;
	    
	    GuiMerchant guiMerchant = ((GuiMerchant)mc.currentScreen);
	    MerchantRecipeList merchantRecipeList = guiMerchant.getMerchant().getRecipes(mc.thePlayer);
	    
        if (merchantRecipeList == null || merchantRecipeList.isEmpty())
        	return false;
        
        //field_70473_e used to work in 1.6.4
        //field_147041_z works in 1.7.2
    	int currentRecipeIndex = ZyinHUDUtil.GetFieldByReflection(GuiMerchant.class, guiMerchant, "currentRecipeIndex","field_70473_e","field_147041_z");
        MerchantRecipe merchantRecipe = (MerchantRecipe)merchantRecipeList.get(currentRecipeIndex);
        
        ItemStack buyingItemStack1 = merchantRecipe.getItemToBuy();
        ItemStack buyingItemStack2 = merchantRecipe.getSecondItemToBuy();
        
        //check if we have an item in our cursor
        ItemStack handStack = mc.thePlayer.inventory.getItemStack();
        if(handStack != null)
        {
        	if(buyingItemStack1 != null && handStack.isItemEqual(buyingItemStack1))
		    {
        		LeftClickContainerSlot(0);
		    }
			else if(buyingItemStack2 != null && handStack.isItemEqual(buyingItemStack2))
		    {
				LeftClickContainerSlot(1);
		    }
        }
        
        List merchantSlots = mc.thePlayer.openContainer.inventorySlots;
        
        int iStart = numMerchantSlots;	//villagers have 3 container slots
        int iEnd = numDisplayedSlots;
        
        //find items in our inventory that match the items the villager is selling
	    for(int i = iStart; i < iEnd; i++)
	    {
	    	Slot slot = (Slot)merchantSlots.get(i);
			ItemStack itemStack = slot.getStack();
			if(itemStack != null)
			{
				if(buyingItemStack1 != null && itemStack.isItemEqual(buyingItemStack1))
			    {
					DepositItemInMerchant(i, 0);
			    }
				else if(buyingItemStack2 != null && itemStack.isItemEqual(buyingItemStack2))
			    {
					DepositItemInMerchant(i, 1);
			    }
			}
	    }
		
		return true;
	}
	
	private static boolean DepositItemInMerchant(int srcIndex, int destIndex)
	{
		if(destIndex < 0 || destIndex > 1)
			return false;
		if(srcIndex < 3 || srcIndex > 39)
			return false;
		
	    ItemStack srcStack = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(srcIndex)).getStack();
	    ItemStack destStack = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(destIndex)).getStack();
		
		//there are 4 cases we need to handle:
	    //1: src = null, dest = null
	    if(srcStack == null && destStack == null)
	    {
	    	return false;
	    }
	    //2: src = null, dest = item
	    else if(srcStack == null && destStack != null)
	    {
			return false;
	    }
	    //3: src = item, dest = null
	    else if(srcStack != null && destStack == null)
	    {
	    	LeftClickContainerSlot(srcIndex);
		    LeftClickContainerSlot(destIndex);
			return true;
	    }
	    //4: src = item, dest = item
	    else// if(srcStack != null && destStack != null)
	    {
	    	if(destStack.isItemEqual(srcStack))
	    	{
		    	LeftClickContainerSlot(srcIndex);
			    LeftClickContainerSlot(destIndex);
			    
			    ItemStack handStack = mc.thePlayer.inventory.getItemStack();
			    if(handStack != null)
			    {
			    	LeftClickContainerSlot(srcIndex);
			    }
			    return true;
	    	}
	    	return false;
	    }
	}
	
	
	public static boolean DepositAllMatchingItemsInFurance()
	{
	    if(!(mc.currentScreen instanceof GuiFurnace))
	    	return false;
	    
	    //furance container = 39 big
	    //slot 0 = input
	    //slot 1 = fuel
	    //slot 2 = output
	    //the last 4 rows (9*4=36) are the player's inventory
	    int numDisplayedSlots = mc.thePlayer.openContainer.inventorySlots.size();
	    
	    int numInventorySlots = 36;
	    int numFurnaceSlots = numDisplayedSlots - numInventorySlots;
	    
	    List furanceSlots = mc.thePlayer.openContainer.inventorySlots;

	    ItemStack inputStack = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(0)).getStack();
	    ItemStack fuelStack = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(1)).getStack();
	    ItemStack outputStack = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(2)).getStack();
	    
	    //check to see if we have an item in our cursor
	    ItemStack handStack = mc.thePlayer.inventory.getItemStack();
        if(handStack != null)
        {
        	if(inputStack != null && handStack.isItemEqual(inputStack))
		    {
				LeftClickContainerSlot(0);
		    }
			else if(fuelStack != null && handStack.isItemEqual(fuelStack))
		    {
				LeftClickContainerSlot(1);
		    }
        }
	    
        int iStart = numFurnaceSlots;	//furances have 3 container slots
        int iEnd = numDisplayedSlots;
        
        //find items in our inventory that match the items in the furance fuel/input slot
	    for(int i = iStart; i < iEnd; i++)
	    {
	    	Slot slot = (Slot)furanceSlots.get(i);
			ItemStack itemStack = slot.getStack();
			if(itemStack != null)
			{
				if(inputStack != null && itemStack.isItemEqual(inputStack))
			    {
					DepositItemInFurance(i, 0);
			    }
				else if(fuelStack != null && itemStack.isItemEqual(fuelStack))
			    {
					DepositItemInFurance(i, 1);
			    }
			}
	    }
	    
	    //take the item from the output slot and put it in our inventory
	    if(outputStack != null)
	    {
	    	int openSlot = GetFirstEmptyIndexInContainerInventory(outputStack);
	    	if(openSlot > 0)
	    		DepositItemInFurance(2, openSlot);	//'deposit' it from the output slot into an empty slot in our inventory
	    }

	    return true;
	}
	
	private static boolean DepositItemInFurance(int srcIndex, int destIndex)
	{
		/*
		if(destIndex < 0 || destIndex > 1)
			return false;
		if(srcIndex < 3 || srcIndex > 39)
			return false;
		*/
		
	    ItemStack srcStack = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(srcIndex)).getStack();
	    ItemStack destStack = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(destIndex)).getStack();
		
		//there are 4 cases we need to handle:
	    //1: src = null, dest = null
	    if(srcStack == null && destStack == null)
	    {
	    	return false;
	    }
	    //2: src = null, dest = item
	    else if(srcStack == null && destStack != null)
	    {
			return false;
	    }
	    //3: src = item, dest = null
	    else if(srcStack != null && destStack == null)
	    {
	    	LeftClickContainerSlot(srcIndex);
		    LeftClickContainerSlot(destIndex);
			return true;
	    }
	    //4: src = item, dest = item
	    else// if(srcStack != null && destStack != null)
	    {
	    	if(destStack.isItemEqual(srcStack))
	    	{
		    	LeftClickContainerSlot(srcIndex);
			    LeftClickContainerSlot(destIndex);
			    
			    ItemStack handStack = mc.thePlayer.inventory.getItemStack();
			    if(handStack != null)
			    {
			    	LeftClickContainerSlot(srcIndex);
			    	
			    	do
			    	{
			        	int openSlot = GetFirstEmptyIndexInContainerInventory(srcStack);
			        	if(openSlot < 0)
			        		break;
			        	
					    LeftClickContainerSlot(openSlot);
					    handStack = mc.thePlayer.inventory.getItemStack();
			    	} while(handStack != null);
			    }
			    return true;
	    	}
	    	return false;
	    }
	}

	
	public static boolean DepositAllMatchingItemsInBrewingStand()
	{
	    if(!(mc.currentScreen instanceof GuiBrewingStand))
	    	return false;
	    
	    //brewing stand container = 40 big
	    //slot 0 = input
	    //slot 1 = output 1
	    //slot 2 = output 2
	    //slot 3 = output 3
	    //the last 4 rows (9*4=36) are the player's inventory
	    int numDisplayedSlots = mc.thePlayer.openContainer.inventorySlots.size();
	    
	    int numInventorySlots = 36;
	    int numFurnaceSlots = numDisplayedSlots - numInventorySlots;
	    
	    List brewingStandSlots = mc.thePlayer.openContainer.inventorySlots;

	    ItemStack inputStack = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(3)).getStack();
	    ItemStack outputStack1 = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(0)).getStack();
	    ItemStack outputStack2 = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(1)).getStack();
	    ItemStack outputStack3 = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(2)).getStack();
	    
	    //check to see if we have an item in our cursor
	    ItemStack handStack = mc.thePlayer.inventory.getItemStack();
        if(handStack != null)
        {
        	if(inputStack != null && handStack.isItemEqual(inputStack))
		    {
				LeftClickContainerSlot(3);
		    }
        	else if(handStack.getItemDamage() == 0 && Items.potionitem == handStack.getItem())
			{
				//if handStack is a "Water Bottle"
				//then deposit the water bottle in an empty output slot
				if(outputStack1 == null)
				{
					LeftClickContainerSlot(0);
					outputStack1 = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(0)).getStack();
				}
				else if(outputStack2 == null)
				{
					LeftClickContainerSlot(1);
					outputStack2 = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(1)).getStack();
				}
				else if(outputStack3 == null)
				{
					LeftClickContainerSlot(2);
					outputStack3 = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(2)).getStack();
				}
		    }
        }
	    
        int iStart = numFurnaceSlots;	//furances have 3 container slots
        int iEnd = numDisplayedSlots;
        
        //find items in our inventory that match the items in the furance fuel/input slot
	    for(int i = iStart; i < iEnd; i++)
	    {
	    	Slot slot = (Slot)brewingStandSlots.get(i);
			ItemStack itemStack = slot.getStack();
			if(itemStack != null)
			{
				if(inputStack != null && itemStack.isItemEqual(inputStack))
			    {
					DepositItemInBrewingStand(i, 3);
			    }
				else if(itemStack.getItemDamage() == 0 && Items.potionitem == itemStack.getItem())
				{
					//if itemStack is a "Water Bottle"
					//then deposit the water bottle in an empty output slot
					if(outputStack1 == null)
					{
						DepositItemInBrewingStand(i, 0);
						outputStack1 = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(0)).getStack();
						continue;
					}
					else if(outputStack2 == null)
					{
						DepositItemInBrewingStand(i, 1);
						outputStack2 = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(1)).getStack();
						continue;
					}
					else if(outputStack3 == null)
					{
						DepositItemInBrewingStand(i, 2);
						outputStack3 = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(2)).getStack();
						continue;
					}
			    }
			}
	    }
	    
	    return true;
	}
	

	private static boolean DepositItemInBrewingStand(int srcIndex, int destIndex)
	{
		if(destIndex < 0 || destIndex > 3)
			return false;
		if(srcIndex < 5 || srcIndex > 39)
			return false;
		
	    ItemStack srcStack = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(srcIndex)).getStack();
	    ItemStack destStack = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(destIndex)).getStack();
		
		//there are 4 cases we need to handle:
	    //1: src = null, dest = null
	    if(srcStack == null && destStack == null)
	    {
	    	return false;
	    }
	    //2: src = null, dest = item
	    else if(srcStack == null && destStack != null)
	    {
			return false;
	    }
	    //3: src = item, dest = null
	    else if(srcStack != null && destStack == null)
	    {
	    	LeftClickContainerSlot(srcIndex);
		    LeftClickContainerSlot(destIndex);
			return true;
	    }
	    //4: src = item, dest = item
	    else// if(srcStack != null && destStack != null)
	    {
	    	if(destStack.isItemEqual(srcStack))
	    	{
		    	LeftClickContainerSlot(srcIndex);
			    LeftClickContainerSlot(destIndex);
			    
			    ItemStack handStack = mc.thePlayer.inventory.getItemStack();
			    if(handStack != null)
			    {
			    	LeftClickContainerSlot(srcIndex);
			    }
			    return true;
	    	}
	    	return false;
	    }
	}
	

	/**
	 * Gets the index of an item class.
	 * @param object The type of item being used. E.x.: Blocks.torch, Items.ender_pearl, or the BlockPos of a block
	 * @param iStart index in the inventory to start looking
	 * @param iEnd index in the inventory to stop looking
	 * @return 9-44, -1 if not found
	 */
	private static int GetItemIndex(Object object, int iStart, int iEnd)
    {
		List inventorySlots = mc.thePlayer.inventoryContainer.inventorySlots;

		//iterate over the main inventory (9~44)
    	for (int i = iStart; i <= iEnd; i++)
        {
    		Slot slot = (Slot)inventorySlots.get(i);
			ItemStack itemStack = slot.getStack();
			if(itemStack != null)
			{
            	if(object instanceof BlockPos)
            	{
            		Block blockToFind = ZyinHUDUtil.GetBlock((BlockPos)object);
            		
            		if(Block.getBlockFromItem(itemStack.getItem()) == blockToFind)
            		{
                		int blockToFindDamage = blockToFind.getDamageValue(mc.theWorld, (BlockPos)object);
                		int inventoryBlockDamage = itemStack.getItemDamage();
                		
                    	//check to see if their damage value matches (applicable to blocks such as wood planks)
                    	if(blockToFindDamage == inventoryBlockDamage)
                    	{
                    		return i;
                    	}
            		}
            	}
            	else if((object instanceof Block
                			&& Block.getBlockFromItem(itemStack.getItem()) == object))
                {
                	return i;
                }
            	else if(object instanceof Item
                			&& itemStack.getItem() == object)
                {
                	return i;
                }
			}
        }

        return -1;
    }
	
	/**
	 * Gets the index of an item class in your inventory.
	 * @param object The type of item being used. E.x.: Blocks.torch, Items.ender_pearl
	 * @return 9-44, -1 if not found
	 */
	public static int GetItemIndexFromInventory(Object object)
    {
		return GetItemIndex(object, 9, 35);
    }
	
	
	/**
	 * Gets the index of an item class in your hotbar.
	 * @param object The type of item being used. E.x.: Blocks.torch, Items.ender_pearl
	 * @return 36-44, -1 if not found
	 */
	public static int GetItemIndexFromHotbar(Object object)
    {
		return GetItemIndex(object, 36, 44);
    }
	
	

	/**
	 * Gets the index in your inventory of the first empty slot.
	 * @return 9-44, -1 if no empty spot
	 */
	private static int GetFirstEmptyIndexInInventory()
	{
		List inventorySlots = mc.thePlayer.inventoryContainer.inventorySlots;

		//iterate over the main inventory (9-35) then the hotbar (36-44)
    	for (int i = 9; i <= 44; i++)
        {
    		Slot slot = (Slot)inventorySlots.get(i);
			ItemStack itemStack = slot.getStack();
			if(itemStack == null)
			{
                return i;
			}
        }

        return -1;
	}

	/**
	 * Gets the index in the chest's player's inventory (bottom section of gui) of the first empty slot.
	 * @return 27,54-63,90, -1 if no empty spot
	 */
	private static int GetFirstEmptyIndexInContainerInventory()
	{
		return GetFirstEmptyIndexInContainerInventory(null);
	}

	/**
	 * Gets the index in the chest's player's inventory (bottom section of gui) of the first empty slot.
	 * It prioritizes slots with partially filled stacks of items with 'itemStackToMatch'.
	 * @param itemStackToMatch an ItemStack to count as an empty spot
	 * @return 0,1-15,27,54. -1 if no empty spot
	 */
	private static int GetFirstEmptyIndexInContainerInventory(ItemStack itemStackToMatch)
	{
		List containerSlots = mc.thePlayer.openContainer.inventorySlots;
		
	    int numDisplayedSlots = containerSlots.size();
	    
	    int numInventorySlots = 36;
	    int numContainerSlots = numDisplayedSlots - numInventorySlots;
	    
	    int iStart = numContainerSlots;
	    int iEnd = numDisplayedSlots;

	    int firstEmptyIndex = -1;
	    int firstEmptyMatchingItemStackIndex = -1;

		//iterate over the chest's inventory (0,1-15,27,54)
    	for (int i = iStart; i <= iEnd-1; i++)
        {
    		Slot slot = (Slot)containerSlots.get(i);
			ItemStack itemStack = slot.getStack();
			if(itemStack == null && firstEmptyIndex == -1)
			{
				firstEmptyIndex = i;
			}
			else if(itemStack != null && itemStackToMatch != null 
					&& itemStack.isItemEqual(itemStackToMatch) 
					&& itemStack.stackSize < itemStack.getMaxStackSize() 
					&& firstEmptyMatchingItemStackIndex == -1)
			{
				firstEmptyMatchingItemStackIndex = i;
				break;
			}
        }
    	
    	if(firstEmptyMatchingItemStackIndex != -1)
    		return firstEmptyMatchingItemStackIndex;
    	else
    		return firstEmptyIndex;
	}
	
	/**
	 * Gets the index in the chest's inventory (top section of gui) of the first empty slot.
	 * @return 0,1-15,27,54. -1 if no empty spot
	 */
	private static int GetFirstEmptyIndexInContainer()
	{
		return GetFirstEmptyIndexInContainer(null);
	}

	/**
	 * Gets the index in the chest's inventory (top section of gui) of the first empty slot.
	 * It prioritizes slots with partially filled stacks of items with 'itemStackToMatch'.
	 * @param itemStackToMatch an ItemStack to count as an empty spot
	 * @return 0,1-15,27,54. -1 if no empty spot
	 */
	private static int GetFirstEmptyIndexInContainer(ItemStack itemStackToMatch)
	{
		List containerSlots = mc.thePlayer.openContainer.inventorySlots;
		
	    int numDisplayedSlots = containerSlots.size();
	    
	    int numInventorySlots = 36;
	    int numContainerSlots = numDisplayedSlots - numInventorySlots;
	    
	    int iStart = 0;
	    int iEnd = numContainerSlots;
	    
	    if(mc.currentScreen instanceof GuiScreenHorseInventory)
	    	iStart = 2;	//the first index is the saddle slot, second index is the armor slot

	    int firstEmptyIndex = -1;
	    int firstEmptyMatchingItemStackIndex = -1;

		//iterate over the chest's inventory (0,1-15,27,54)
    	for (int i = iStart; i <= iEnd-1; i++)
        {
    		Slot slot = (Slot)containerSlots.get(i);
			ItemStack itemStack = slot.getStack();
			if(itemStack == null && firstEmptyIndex == -1)
			{
				firstEmptyIndex = i;
			}
			else if(itemStack != null && itemStackToMatch != null 
					&& itemStack.isItemEqual(itemStackToMatch) 
					&& itemStack.stackSize < itemStack.getMaxStackSize() 
					&& firstEmptyMatchingItemStackIndex == -1)
			{
				firstEmptyMatchingItemStackIndex = i;
				break;
			}
        }
    	
    	if(firstEmptyMatchingItemStackIndex != -1)
    		return firstEmptyMatchingItemStackIndex;
    	else
    		return firstEmptyIndex;
	}
	
	/**
	 * Determines if an item exists in a container's (chest, horse, etc) inventory (top section of gui) and returns its location
	 * @param itemID the item to search for
	 * @return 0-27,54, -1 if no item found
	 */
	private static int GetFirstItemIndexInContainer(ItemStack itemStackToMatch)
	{
		List chestSlots = mc.thePlayer.openContainer.inventorySlots;
		
	    int numDisplayedSlots = chestSlots.size();

	    int numInventorySlots = 36;
	    int numContainerSlots = numDisplayedSlots - numInventorySlots;
	    
	    int iStart = 0;
	    int iEnd = numContainerSlots;
	    
	    if(mc.currentScreen instanceof GuiScreenHorseInventory)
	    	iStart = 2;	//the first index is the saddle slot, second index is the armor slot - skip these

	    //iterate over the chest's inventory (0,1-16,27,54)
    	for (int i = iStart; i <= iEnd-1; i++)
        {
    		Slot slot = (Slot)chestSlots.get(i);
			ItemStack itemStack = slot.getStack();
			if(itemStack != null && itemStack.isItemEqual(itemStackToMatch))
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
	 * Moves an armor the player is wearing into their inventory
	 * @param armorSlotIndex index 0-3, 0 = boots, 1 = pants, 2 = armor, 3 = helm (0-3 index will be changed to 5-8 in this method)
	 * @return
	 */
	public static boolean MoveArmorIntoPlayerInventory(int armorSlotIndex)
	{
		armorSlotIndex = (3-armorSlotIndex) + 5;	//parameter comes in as 0-3, we shift it to 5-8
		int emptySlotIndex = GetFirstEmptyIndexInInventory();
		
		if(emptySlotIndex != -1)
		{
			return Swap(armorSlotIndex, emptySlotIndex);
		}
		
		return false;
	}
	
	
	/**
	 * Moves an item the player has selected (selected in the hotbar) to their inventory
	 * @return
	 */
	public static boolean MoveHeldItemIntoPlayerInventory()
	{
		int heldItemSlotIndex = GetCurrentlySelectedItemInventoryIndex();
		int emptySlotIndex = GetFirstEmptyIndexInInventory();
		
		if(emptySlotIndex != -1)
		{
			return Swap(heldItemSlotIndex, emptySlotIndex);
		}
		
		return false;
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
     * Simulates a left click as if your inventory GUI screen was open at the specified item slot index.
     * @param itemIndex the item Slot index
     */
	private static void LeftClickInventorySlot(int itemIndex)
    {
        SendInventoryClick(itemIndex, false, false);
    }
    
	/**
	 * Simulates a left click as if a chest GUI screen was open at the specified item slot index.
     * @param itemIndex the item Slot index
	 */
    private static void LeftClickContainerSlot(int itemIndex)
    {
    	SendContainerClick(itemIndex, false, false);
    }
    
    /**
     * Simulates a left click if your inventory GUI screen was open at the specified item slot index.
     * @param itemIndex the item Slot index
     * @param rightClick is right click held?
     * @param shiftHold is shift held?
     */
    private static void SendInventoryClick(int itemIndex, boolean rightClick, boolean shiftHold)
    {
        if (itemIndex < 0 || itemIndex > 44)	//0-44 is the size of the players inventory
        	return;
        
        try
        {
	        mc.playerController.windowClick(
	        		mc.thePlayer.inventoryContainer.windowId,
	        		itemIndex,
	        		(rightClick) ? 1 : 0,
					(shiftHold) ? 1 : 0,
					mc.thePlayer);
        }
        catch (IndexOutOfBoundsException e)
        {
        	//fix for a bug with an unknown cause:
        	//https://github.com/Zyin055/zyinhud/issues/39#issuecomment-77441332
        	//https://github.com/Zyin055/zyinhud/issues/40#issuecomment-96220702
        	return;
        }
    }
    
    /**
     * Simulates a left click as if a chest GUI screen was open at the specified item slot index.
     * @param itemIndex the item Slot index
     * @param rightClick is right click held?
     * @param shiftHold is shift held?
     */
    private static void SendContainerClick(int itemIndex, boolean rightClick, boolean shiftHold)
    {
        if (itemIndex < 0)	//don't check for an upper bounds in case a mod increases the size of a container past a double chest
        	return;
        
        try
        {
        	mc.playerController.windowClick(
        		mc.thePlayer.openContainer.windowId,
        		itemIndex,
        		(rightClick) ? 1 : 0,
				(shiftHold) ? 1 : 0,
				mc.thePlayer);
        }
        catch(Exception e)
        {
        	//Sometimes netManager in NetClientHandler.addToSendQueue() will throw a null pointer exception for an unknown reason.
        	//catching this seemingly random exception will prevent the game from crashing.
        }
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
