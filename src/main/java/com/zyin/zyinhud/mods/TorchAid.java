package com.zyin.zyinhud.mods;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.zyin.zyinhud.util.InventoryUtil;

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
    public static void OnRightClick()
    {
    	if(TorchAid.Enabled)
    		UseTorchIfToolIsEquipped();
    }
    
    
    /**
     * Makes the player place a Torch if they are currently using an axe, pickaxe, shovel, or have nothing in hand.
     */
    public static void UseTorchIfToolIsEquipped()
    {
    	if(mc.currentScreen == null && mc.inGameHasFocus)
    	{
    		ItemStack currentItemStack = mc.thePlayer.getHeldItem();
    		if(currentItemStack == null	|| currentItemStack.getItem() instanceof ItemTool)	//hand or tool (axe, pickaxe, shovel) selected
    		{
    			UseTorch();
    		}
    	}
    }
    
    /**
     * Makes the player place a Torch if they have any.
     */
    public static void UseTorch()
    {
        if (EatingAid.instance.isEating())
        {
            EatingAid.instance.StopEating();    //it's not good if we have a torch selected and hold right click down...
        }
        
        
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            if(CanPlaceTorchAt(mc.theWorld, mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ, mc.objectMouseOver.sideHit))
            {
                boolean usedTorchSuccessfully = InventoryUtil.UseItem(Blocks.torch);
                
                if (!usedTorchSuccessfully)
                {
                	//either no torches were found in the players inventory, or a torch cannot be placed there
                    //InfoLine.DisplayNotification(Localization.get("torchaid.notorches"));
                }
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
                CanPlaceTorchOnTopOf(world, x, y - 0, z);	//for some reason, the default Minecraft code used y - 1.
															//Using y - 1 caused issues such as right clicking beds and levers caused them to be used twice.
															//Using y - 0 caused issues such as not being able to place torches next to Ice blocks
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
