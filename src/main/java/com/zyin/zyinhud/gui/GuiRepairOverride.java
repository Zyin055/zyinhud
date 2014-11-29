package com.zyin.zyinhud.gui;

import net.minecraft.client.gui.GuiRepair;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.zyin.zyinhud.util.ZyinHUDUtil;

/**
 * This GUI extends the default GuiRepair screen (when you right click on an Anvil)
 * by adding additional text.
 * <p>
 * This is able to replace the default one by utilizing an event in the Miscellaneous class.
 */
public class GuiRepairOverride extends GuiRepair
{
	private static final int maxRepairTimes = 6;
	
    public GuiRepairOverride(InventoryPlayer inventoryPlayer, World world)
    {
        super(inventoryPlayer, world);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
    	super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        //GlStateManager.disableLighting();
        //GlStateManager.disableBlend();
    	
		ContainerRepair anvil = ZyinHUDUtil.GetFieldByReflection(GuiRepair.class, (GuiRepair)this, "anvil", "field_147092_v");
    	IInventory inputSlots = ZyinHUDUtil.GetFieldByReflection(ContainerRepair.class, anvil, "inputSlots", "field_82853_g");
		
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
        	
            fontRendererObj.drawString(leftItemRepairCost, 26, 37, 0xffffff);
        }
        if(rightItemStack != null)
        {
        	int timesRepaired = GetTimesRepaired(rightItemStack);
        	String rightItemRepairCost;
        	
        	if(timesRepaired >= maxRepairTimes)
        		rightItemRepairCost = EnumChatFormatting.RED.toString() + timesRepaired + EnumChatFormatting.DARK_GRAY + "/" + maxRepairTimes;
        	else
        		rightItemRepairCost = EnumChatFormatting.DARK_GRAY.toString() + timesRepaired + "/" + maxRepairTimes;
        	
            fontRendererObj.drawString(rightItemRepairCost, 76, 37, 0xffffff);
        }
        if(leftItemStack != null && rightItemStack != null)
        {
        	int timesRepaired = GetTimesRepaired(leftItemStack) + GetTimesRepaired(rightItemStack) + 1;
        	String finalItemRepairCost = EnumChatFormatting.DARK_GRAY.toString() + timesRepaired+"/" + maxRepairTimes;
        	
        	if(timesRepaired <= maxRepairTimes)
        	{
                fontRendererObj.drawString(finalItemRepairCost, 133, 37, 0xffffff);
        	}
        }
        
        //GlStateManager.enableLighting();
    }
    
    protected static int GetTimesRepaired(ItemStack itemStack)
    {
    	/*
    	equations: 2^n - 1, log2(n + 1)
    	times repair, repair cost, xp
    	0: 0, 2
    	1: 1, 3
    	2: 3, 5
    	3: 7, 9
    	4: 15, 17
    	5: 31, 33
    	6: 63, 65 (too expensive)
    	*/
    	return log(itemStack.getRepairCost() + 1, 2);
    }

    private static int log(int x, int base)
    {
        return (int)(Math.log(x) / Math.log(base));
    }

}
