package com.zyin.zyinhud.mods;


import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.zyin.zyinhud.util.InventoryUtil;
import com.zyin.zyinhud.util.ModCompatibility;

import cpw.mods.fml.common.eventhandler.Event;

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
    public static void Pressed(Event event)
    {
    	if(TorchAid.Enabled)
    		UseTorchIfToolIsEquipped(event);
    }
    
    
    /**
     * Makes the player place a Torch if they are currently using an axe, pickaxe, shovel, or have nothing in hand.
     */
    public static void UseTorchIfToolIsEquipped(Event event)
    {
    	if(mc.currentScreen == null && mc.inGameHasFocus)
    	{
    		ItemStack currentItemStack = mc.thePlayer.getHeldItem();
    		//if(currentItemStack == null	|| currentItemStack.getItem() instanceof ItemTool)	//hand or tool (axe, pickaxe, shovel) selected
    		if(currentItemStack == null
    			|| currentItemStack.getItem() instanceof ItemTool
    			|| ModCompatibility.TConstruct.IsTConstructToolWithoutARightClickAction(currentItemStack.getItem()))
    		{
    			UseTorch(event);
    		}
    	}
    }
    
    /**
     * Makes the player place a Torch if they have any.
     */
    public static void UseTorch(Event event)
    {
        if (EatingAid.instance.isEating())
        {
            EatingAid.instance.StopEating();    //it's not good if we have a torch selected and hold right click down...
        }
        
        
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
        	int x = mc.objectMouseOver.blockX;
        	int y = mc.objectMouseOver.blockY;
        	int z = mc.objectMouseOver.blockZ;
        	int sideHit = mc.objectMouseOver.sideHit;
        	
            if (sideHit == 0)
                y--;
            else if (sideHit == 1)
                y++;
            else if (sideHit == 2)
                z--;
            else if (sideHit == 3)
                z++;
            else if (sideHit == 4)
                x--;
            else if (sideHit == 5)
                x++;
            
            if(CanPlaceTorchAt(mc.theWorld, x, y, z, sideHit))
            {
            	boolean attemptedToUseTorch = InventoryUtil.UseItem(Blocks.torch);
            	
            	if(event.isCancelable())	//MouseEvents are cancelable, KeyInputEvents are not
            		event.setCanceled(true);	//cancel the original right click since we sent our own with InventoryUtil.UseItem()
            }
        }
    }
    

    /**
     * Checks to see if its valid to put this torch at the specified coordinates.
     * Copy/pasted from BlockTorch.CanPlaceBlockAt()
     */
    protected static boolean CanPlaceTorchAt(World world, int x, int y, int z, int side)
    {
        return world.isSideSolid(x - 1, y, z, ForgeDirection.EAST,  true) ||
                world.isSideSolid(x + 1, y, z, ForgeDirection.WEST,  true) ||
                world.isSideSolid(x, y, z - 1, ForgeDirection.SOUTH, true) ||
                world.isSideSolid(x, y, z + 1, ForgeDirection.NORTH, true) ||
                CanPlaceTorchOnTopOf(world, x, y - 1, z);
    }
    
    /**
     * Checks to see if its valid to put this torch on top of the block at the specified coordinates.
     * Copy/pasted from BlockTorch.func_150107_m()
     */
    private static boolean CanPlaceTorchOnTopOf(World world, int x, int y, int z)
    {
        if (World.doesBlockHaveSolidTopSurface(world, x, y, z))
        {
            return true;
        }
        else
        {
            Block block = world.getBlock(x, y, z);
            return block.canPlaceTorchOnTop(world, x, y, z);
        }
    }
}
