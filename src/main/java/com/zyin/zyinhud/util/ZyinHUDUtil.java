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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

/**
 * General utility class for ZyinHUD.
 */
public class ZyinHUDUtil
{
    protected static Minecraft mc = Minecraft.getMinecraft();
    protected static final RenderItem itemRenderer = new RenderItem();
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
            Block block = mc.theWorld.getBlock(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ);

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
	 * Renders an Item icon in the 3D world at the specified coordinates
	 * @param item
	 * @param x
	 * @param y
	 * @param z
	 * @param partialTickTime
	 */
	public static void RenderFloatingIcon(Item item, float x, float y, float z, float partialTickTime)
    {
    	RenderManager renderManager = RenderManager.instance;
        FontRenderer fontRenderer = mc.fontRenderer;
        
        float playerX = (float) (mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTickTime);
        float playerY = (float) (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTickTime);
        float playerZ = (float) (mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTickTime);

        float dx = x-playerX;
        float dy = y-playerY;
        float dz = z-playerZ;
        float scale = 0.025f;
        
        GL11.glColor4f(1f, 1f, 1f, 0.75f);
        GL11.glPushMatrix();
        GL11.glTranslatef(dx, dy, dz);
        GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-scale, -scale, scale);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        ResourceLocation resource = textureManager.getResourceLocation(new ItemStack(item).getItemSpriteNumber());
        IIcon icon = new ItemStack(item).getIconIndex();
        
        textureManager.bindTexture(resource);
        itemRenderer.renderIcon(-8, -8, icon, 16, 16);

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }
	
	
	
	/**
	 * Renders floating text in the 3D world at a specific position.
	 * @param text The text to render
	 * @param x X coordinate in the game world
	 * @param y Y coordinate in the game world
	 * @param z Z coordinate in the game world
	 * @param offset vertical offset of the text being rendered
	 * @param color 0xRRGGBB
	 * @param renderBlackBox render a pretty black border behind the text?
	 * @param partialTickTime Usually taken from RenderWorldLastEvent.partialTicks variable
	 */
    public static void RenderFloatingText(String text, float x, float y, float z, int color, boolean renderBlackBox, float partialTickTime)
    {
    	String textArray[] = {text};
    	RenderFloatingText(textArray, x, y, z, color, renderBlackBox, partialTickTime);
    }
    
    /**
	 * Renders floating lines of text in the 3D world at a specific position.
	 * @param text The string array of text to render
	 * @param x X coordinate in the game world
	 * @param y Y coordinate in the game world
	 * @param z Z coordinate in the game world
	 * @param offset vertical offset of the text being rendered
	 * @param color 0xRRGGBB
	 * @param renderBlackBox render a pretty black border behind the text?
	 * @param partialTickTime Usually taken from RenderWorldLastEvent.partialTicks variable
	 */
    public static void RenderFloatingText(String[] text, float x, float y, float z, int color, boolean renderBlackBox, float partialTickTime)
    {
    	//Thanks to Electric-Expansion mod for the majority of this code
    	//https://github.com/Alex-hawks/Electric-Expansion/blob/master/src/electricexpansion/client/render/RenderFloatingText.java
    	
    	RenderManager renderManager = RenderManager.instance;
        FontRenderer fontRenderer = mc.fontRenderer;
        
        float playerX = (float) (mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTickTime);
        float playerY = (float) (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTickTime);
        float playerZ = (float) (mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTickTime);

        float dx = x-playerX;
        float dy = y-playerY;
        float dz = z-playerZ;
        float distance = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        float multiplier = distance / 120f;	//mobs only render ~120 blocks away
        float scale = 0.45f * multiplier;
        
        GL11.glColor4f(1f, 1f, 1f, 0.5f);
        GL11.glPushMatrix();
        GL11.glTranslatef(dx, dy, dz);
        GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-scale, -scale, scale);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        int textWidth = 0;
        for (String thisMessage : text)
        {
            int thisMessageWidth = mc.fontRenderer.getStringWidth(thisMessage);

            if (thisMessageWidth > textWidth)
            	textWidth = thisMessageWidth;
        }
        
        int lineHeight = 10;
        
        if(renderBlackBox)
        {
        	Tessellator tessellator = Tessellator.instance;
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            tessellator.startDrawingQuads();
            int stringMiddle = textWidth / 2;
            tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.5F);
            tessellator.addVertex(-stringMiddle - 1, -1 + 0, 0.0D);
            tessellator.addVertex(-stringMiddle - 1, 8 + lineHeight*text.length-lineHeight, 0.0D);
            tessellator.addVertex(stringMiddle + 1, 8 + lineHeight*text.length-lineHeight, 0.0D);
            tessellator.addVertex(stringMiddle + 1, -1 + 0, 0.0D);
            tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
        
        int i = 0;
        for(String message : text)
        {
            fontRenderer.drawString(message, -textWidth / 2, i*lineHeight, color);
        	i++;
        }
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }
	
    

    /**
     * Draws a texture at the specified 2D coordinates
     * @param x X coordinate
     * @param y Y coordinate
     * @param u X coordinate of the texture inside of the .png
     * @param v Y coordinate of the texture inside of the .png
     * @param width width of the texture
     * @param height height of the texture
     * @param resourceLocation A reference to the texture's ResourceLocation. If null, it'll use the last used resource.
     * @param scaler How much to scale the texture by when rendering it
     */
    public static void DrawTexture(int x, int y, int u, int v, int width, int height, ResourceLocation resourceLocation, float scaler)
    {
        x /= scaler;
        y /= scaler;
        
        GL11.glPushMatrix();
        //GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glScalef(scaler, scaler, scaler);
        
        if(resourceLocation != null)
        	mc.getTextureManager().bindTexture(resourceLocation);
        
        mc.ingameGUI.drawTexturedModalRect(x, y, u, v, width, height);
        
        GL11.glPopMatrix();
    }
    

    /**
     * Displays a short notification to the user. Uses the Minecraft code to display messages.
     * @param message the message to be displayed
     */
    public static void DisplayNotification(String message)
    {
        mc.ingameGUI.func_110326_a(message, false);
    }
    
    /**
     * Gets a protected/private field from a class using reflection.
     * @param <T> The return type of the field you are getting
     * @param <E> The class the field is in
     * @param classToAccess The ".class" of the class the field is in
     * @param instance The instance of the class
     * @param fieldNames comma seperated names the field may have (i.e. obfuscated, non obfuscated).
     * Obfustated field names can be found in fml/conf/fields.csv
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
    
	
}
