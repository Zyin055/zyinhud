package zyin.zyinhud.util;

import java.awt.event.InputEvent;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockCake;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockRedstoneLogic;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;

public class ZyinHUDUtil
{
    private static Minecraft mc = Minecraft.getMinecraft();
	
    /***
     * Determines if something will happen if you right click on the block the 
     * player is currently looking at
     * @return
     */
	public static boolean IsMouseoveredBlockRightClickable()
	{
        MovingObjectPosition objectMouseOver = mc.thePlayer.rayTrace(5, 1);

        if (objectMouseOver != null && objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
        {
            int blockId = mc.theWorld.getBlockId(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
            Block block = Block.blocksList[blockId];

            if(ZyinHUDUtil.IsBlockRightClickable(block))
            	return true;
        }
        return false;
	}
	
	/**
	 * Determines if something will happen if you right click a block
	 * @param block
	 * @return 
	 */
	public static boolean IsBlockRightClickable(Block block)
	{
        //couldn't find a way to see if a block is 'right click-able' without running the onBlockActivation() method
        //which we don't want to do
        return block instanceof BlockContainer	//chests, hoppers, dispenser, jukebox, beacon, etc.
                || block instanceof BlockButton
                || block instanceof BlockLever
                || block instanceof BlockRedstoneLogic
                || block instanceof BlockDoor
                || block instanceof BlockAnvil
                || block instanceof BlockBed
                || block instanceof BlockCake
                || block instanceof BlockFenceGate
                || block instanceof BlockTrapDoor
                || block instanceof BlockWorkbench;
	}
	
	
	
	
	
}
