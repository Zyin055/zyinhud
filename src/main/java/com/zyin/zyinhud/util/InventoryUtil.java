package com.zyin.zyinhud.util;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.lwjgl.opengl.GL11;

import com.zyin.zyinhud.mods.QuickDeposit;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
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
	 * Swaps 2 items in your inventory GUI.
	 * @param srcIndex
	 * @param destIndex
	 * @return true if the items were successfully swapped
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
	 * @param ignoreItemsInHotbar if true, won't deposit items that are in the player's hotbar
	 * @return true if operation completed successfully, false if some items were left behind (aka there was a full chest)
	 */
	public static boolean DepositAllMatchingItemsInContainer(boolean ignoreItemsInHotbar)
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
			    int itemIndex = GetFirstItemIndexInContainer(itemStack);
			    
			    //if the item exists in the chest
			    if(itemIndex >= 0)
			    {
			    	DepositItemInContainer(i, itemIndex);
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
		//horse chest = 53 big
	    //single chest = 63 big
	    //double chest = 90 big
	    //the last 4 rows (9*4=36) are the player's inventory
	    int numDisplayedSlots = mc.thePlayer.openContainer.inventorySlots.size();
	    
	    int numInventorySlots = 36;
	    int numContainerSlots = numDisplayedSlots - numInventorySlots;
	    
	    //check parameters for valid values
		if(numContainerSlots == 53 && (srcIndex < 18 || srcIndex > 53))
			return false;
		if(numContainerSlots == 63 && (srcIndex < 28 || srcIndex > 63))
			return false;
		if(numContainerSlots == 90 && (srcIndex < 55 || srcIndex > 90))
			return false;
		if(numContainerSlots == 53 && (destIndex < 0 || destIndex > 17))
			return false;
		if(numContainerSlots == 63 && (destIndex < 0 || destIndex > 27))
			return false;
		if(numContainerSlots == 90 && (destIndex < 0 || destIndex > 54))
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
	    //MerchantRecipeList merchantRecipeList = guiMerchant.getIMerchant().getRecipes(mc.thePlayer);
	    MerchantRecipeList merchantRecipeList = guiMerchant.func_147035_g().getRecipes(mc.thePlayer);
	    
        if (merchantRecipeList == null || merchantRecipeList.isEmpty())
        	return false;
        
    	int currentRecipeIndex = ZyinHUDUtil.GetFieldByReflection(GuiMerchant.class, guiMerchant, "currentRecipeIndex","field_70473_e");
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
	    ItemStack fueldStack = ((Slot)mc.thePlayer.openContainer.inventorySlots.get(1)).getStack();
	    
	    //check to see if we have an item in our cursor
	    ItemStack handStack = mc.thePlayer.inventory.getItemStack();
        if(handStack != null)
        {
        	if(inputStack != null && handStack.isItemEqual(inputStack))
		    {
				LeftClickContainerSlot(0);
		    }
			else if(fueldStack != null && handStack.isItemEqual(fueldStack))
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
				else if(fueldStack != null && itemStack.isItemEqual(fueldStack))
			    {
					DepositItemInFurance(i, 1);
			    }
			}
	    }
	    
	    
	    return false;
	}
	
	private static boolean DepositItemInFurance(int srcIndex, int destIndex)
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
	private static int GetFirstEmptyIndexInInventory()
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
	 * Gets the index in the chest's player's inventory (bottom section of gui) of the first empty slot.
	 * @return 27,54-63,90, -1 if no empty spot
	 */
	private static int GetFirstEmptyIndexInContainerInventory()
	{
		List containerSlots = mc.thePlayer.openContainer.inventorySlots;
		
	    int numDisplayedSlots = containerSlots.size();
	    
	    int numInventorySlots = 36;
	    int numContainerSlots = numDisplayedSlots - numInventorySlots;
	    
	    int iStart = numContainerSlots;
	    int iEnd = numDisplayedSlots;

		//iterate over the player's inventory (16,27,54-53,63,90)
    	for (int i = iStart; i <= iEnd-1; i++)
        {
    		Slot slot = (Slot)containerSlots.get(i);
			ItemStack itemStack = slot.getStack();
			if(itemStack == null)
			{
                return i;
			}
        }

        return -1;
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
	 * It prioritizes slots with partially filled stacks of items with 'itemID'.
	 * @param itemID an itemID to count as an empty spot
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
	    int firstEmptyMatchingItemIdStack = -1;

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
					&& firstEmptyMatchingItemIdStack == -1)
			{
				firstEmptyMatchingItemIdStack = i;
				break;
			}
        }
    	
    	if(firstEmptyMatchingItemIdStack != -1)
    		return firstEmptyMatchingItemIdStack;
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
     * Simulates a left click as if a chest GUI screen was open at the specified item slot index.
     * @param itemIndex the item Slot index
     * @param rightClick is right click held?
     * @param shiftHold is shift held?
     */
    private static void SendContainerClick(int itemIndex, boolean rightClick, boolean shiftHold)
    {
        if (itemIndex < 0 || itemIndex > 90)
        	return;
        
        try
        {
        	playerController.windowClick(
        		mc.thePlayer.openContainer.windowId,
        		itemIndex,
        		(rightClick) ? 1 : 0,
				(shiftHold) ? 1 : 0,
				mc.thePlayer);
        }
        catch(Exception e)
        {
        	//Sometimes netManager in NetClientHandler.addToSendQueue() will throw a null pointer exception for an unknown reason
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
