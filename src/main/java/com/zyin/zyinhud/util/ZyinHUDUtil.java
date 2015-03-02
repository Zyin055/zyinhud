package com.zyin.zyinhud.util;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockCake;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.BlockPos;
//import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;

/**
 * General utility class for ZyinHUD.
 */
public class ZyinHUDUtil
{
    protected static Minecraft mc = Minecraft.getMinecraft();
    protected static final RenderItem itemRenderer = mc.getRenderItem();
    protected static final TextureManager textureManager = mc.getTextureManager();
	
    /***
     * Determines if something will happen if you right click on the block the 
     * player is currently looking at
     * @return
     */
	public static boolean IsMouseoveredBlockRightClickable()
	{
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
        	Block block = GetMouseOveredBlock();

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
        //couldn't find a way to see if a block is 'right click-able' without running the onBlockActivated() method
        //for that block, which we don't want to do
        return block instanceof BlockContainer	//BlockContainer = beacons, brewing stand, chest, command block, daylight detector, dispenser, enchantment table, ender chest, end portal, flower pot, furnace, hopper, jukebox, mob spawner, note block, piston moving, sign, skull
                || block instanceof BlockButton
                || block instanceof BlockLever
                || block instanceof BlockRedstoneDiode	//BlockRedstoneDiode = repeaters + comparators
                || block instanceof BlockDoor
                || block instanceof BlockAnvil
                || block instanceof BlockBed
                || block instanceof BlockCake
                || block instanceof BlockFenceGate
                || block instanceof BlockTrapDoor
                || block instanceof BlockWorkbench;
	}
	
    /**
     * Gets a protected/private field from a class using reflection.
     * @param <T> The return type of the field you are getting
     * @param <E> The class the field is in
     * @param classToAccess The ".class" of the class the field is in
     * @param instance The instance of the class
     * @param fieldNames comma seperated names the field may have (i.e. obfuscated, non obfuscated).
     * Obfustated field names can be found in %USERPROFILE%\.gradle\caches\minecraft\de\oceanlabs\mcp\...\fields.csv
     * @return
     */
    public static <T, E> T GetFieldByReflection(Class<? super E> classToAccess, E instance, String... fieldNames)
    {
		Field field = null;
		for(String fieldName : fieldNames)
		{
			try
			{
			     field = classToAccess.getDeclaredField(fieldName);
			}
			catch(NoSuchFieldException e){}
			
			if(field != null)
				break;
	    }
		
		if(field != null)
		{
			field.setAccessible(true);
		    T fieldT = null;
		    try
			{
		    	fieldT = (T) field.get(instance);
			}
		    catch (IllegalArgumentException e){}
		    catch (IllegalAccessException e){}
		
		    return fieldT;
		}
		
		return null;
    }

    public static Block GetMouseOveredBlock()
    {
    	int x = mc.objectMouseOver.func_178782_a().getX();	//func_178782_a() friendly name is probably getBlockPos()
    	int y = mc.objectMouseOver.func_178782_a().getY();
    	int z = mc.objectMouseOver.func_178782_a().getZ();
    	return GetBlock(x, y, z);
    }
    
    public static BlockPos GetMouseOveredBlockPos()
    {
    	int x = mc.objectMouseOver.func_178782_a().getX();	//func_178782_a() friendly name is probably getBlockPos()
    	int y = mc.objectMouseOver.func_178782_a().getY();
    	int z = mc.objectMouseOver.func_178782_a().getZ();
    	return new BlockPos(x, y, z);
    }

    public static Block GetBlock(int x, int y, int z)
    {
    	BlockPos pos = new BlockPos(x, y, z);
    	return GetBlock(pos);
    }
    public static Block GetBlock(BlockPos pos)
    {
    	IBlockState blockState = GetBlockState(pos);
    	if(blockState == null)
    		return null;
    	else
    		return blockState.getBlock();
    }

    public static IBlockState GetBlockState(int x, int y, int z)
    {
    	BlockPos pos = new BlockPos(x, y, z);
    	return GetBlockState(pos);
    }
    public static IBlockState GetBlockState(BlockPos pos)
    {
    	if(mc.theWorld != null)
    		return mc.theWorld.getBlockState(pos);
    	else
    		return null;
    }
	
}
