package com.zyin.zyinhud.mods;

import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

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
	
    private static final int maxRepairTimes = 6;
	

    @SubscribeEvent
    public void GuiOpenEvent(GuiOpenEvent event)
	{
    	if(UseQuickPlaceSign && event.gui instanceof GuiEditSign && mc.thePlayer.isSneaking())
    	{
    		event.setCanceled(true);
    	}
	}
    

    @SubscribeEvent
    public void DrawScreenEvent(DrawScreenEvent.Post event)
    {
    	if(ShowAnvilRepairs && event.gui instanceof GuiRepair)
    	{
    		DrawGuiRepairCounts((GuiRepair)event.gui);
    	}
    }
    
    /**
     * Draws text above the anvil's repair slots showing how many more times it can be repaired
     * @param guiRepair
     */
    public void DrawGuiRepairCounts(GuiRepair guiRepair)
    {
    	ContainerRepair anvil = ZyinHUDUtil.GetFieldByReflection(GuiRepair.class, guiRepair, "anvil", "field_147092_v");
    	IInventory inputSlots = ZyinHUDUtil.GetFieldByReflection(ContainerRepair.class, anvil, "inputSlots", "field_82853_g");

    	int xSize = ZyinHUDUtil.GetFieldByReflection(GuiContainer.class, guiRepair, "xSize", "field_146999_f");
    	int ySize = ZyinHUDUtil.GetFieldByReflection(GuiContainer.class, guiRepair, "ySize", "field_147000_g");

    	int guiRepairXOrigin = guiRepair.width/2 - xSize/2;
    	int guiRepairYOrigin = guiRepair.height/2 - ySize/2;
		
        ItemStack leftItemStack = inputSlots.getStackInSlot(0);
        ItemStack rightItemStack = inputSlots.getStackInSlot(1);
        ItemStack finalItemStack = inputSlots.getStackInSlot(2);
        
        if(leftItemStack != null)
        {
        	int timesRepaired = GetTimesRepaired(leftItemStack);
        	String leftItemRepairCost;
        	
        	if(timesRepaired >= maxRepairTimes)
        		leftItemRepairCost = EnumChatFormatting.RED.toString() + timesRepaired + EnumChatFormatting.DARK_GRAY + "/" + maxRepairTimes;
        	else
        		leftItemRepairCost = EnumChatFormatting.DARK_GRAY.toString() + timesRepaired + "/" + maxRepairTimes;
        	
        	mc.fontRendererObj.drawString(leftItemRepairCost, guiRepairXOrigin + 26, guiRepairYOrigin + 37, 0xffffff);
        }
        if(rightItemStack != null)
        {
        	int timesRepaired = GetTimesRepaired(rightItemStack);
        	String rightItemRepairCost;
        	
        	if(timesRepaired >= maxRepairTimes)
        		rightItemRepairCost = EnumChatFormatting.RED.toString() + timesRepaired + EnumChatFormatting.DARK_GRAY + "/" + maxRepairTimes;
        	else
        		rightItemRepairCost = EnumChatFormatting.DARK_GRAY.toString() + timesRepaired + "/" + maxRepairTimes;
        	
        	mc.fontRendererObj.drawString(rightItemRepairCost, guiRepairXOrigin + 76, guiRepairYOrigin + 37, 0xffffff);
        }
        if(leftItemStack != null && rightItemStack != null)
        {
        	int timesRepaired = GetTimesRepaired(leftItemStack) + GetTimesRepaired(rightItemStack) + 1;
        	String finalItemRepairCost = EnumChatFormatting.DARK_GRAY.toString() + timesRepaired+"/" + maxRepairTimes;
        	
        	if(timesRepaired <= maxRepairTimes)
        	{
        		mc.fontRendererObj.drawString(finalItemRepairCost, guiRepairXOrigin + 133, guiRepairYOrigin + 37, 0xffffff);
        	}
        }
    }
    /**
     * Returns how many times an item has been used with an Anvil
     * @param itemStack
     * @return
     */
    protected static int GetTimesRepaired(ItemStack itemStack)
    {
    	/*
    	times repaired: repair cost, xp
    	0: 0, 2
    	1: 1, 3
    	2: 3, 5
    	3: 7, 9
    	4: 15, 17
    	5: 31, 33
    	6: 63, 65 (too expensive)
    	
    	equation is 2^n - 1, log2(n + 1)
    	*/
    	return log(itemStack.getRepairCost() + 1, 2);
    }
    /**
     * Takes the log with a specified base.
     * @param x
     * @param base
     * @return log[base](x)
     */
    private static int log(int x, int base)
    {
        return (int)(Math.log(x) / Math.log(base));
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
