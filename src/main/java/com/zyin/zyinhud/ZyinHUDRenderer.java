package com.zyin.zyinhud;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.zyin.zyinhud.helper.HUDEntityTrackerHelper;
import com.zyin.zyinhud.helper.RenderEntityTrackerHelper;
import com.zyin.zyinhud.mods.AnimalInfo;
import com.zyin.zyinhud.mods.DistanceMeasurer;
import com.zyin.zyinhud.mods.DurabilityInfo;
import com.zyin.zyinhud.mods.InfoLine;
import com.zyin.zyinhud.mods.ItemSelector;
import com.zyin.zyinhud.mods.PotionTimers;
import com.zyin.zyinhud.mods.SafeOverlay;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is in charge of rendering things onto the HUD and into the game world.
 */
public class ZyinHUDRenderer
{
	public static ZyinHUDRenderer instance = new ZyinHUDRenderer();
	private static Minecraft mc = Minecraft.getMinecraft();
	
	/**
	 * Event fired at various points during the GUI rendering process.
	 * We render anything that need to be rendered onto the HUD in this method.
	 * @param event
	 */
    @SubscribeEvent
    public void RenderGameOverlayEvent(RenderGameOverlayEvent event)
    {
    	//render everything onto the screen
    	if(event.type == RenderGameOverlayEvent.ElementType.TEXT)
    	{
    		InfoLine.RenderOntoHUD();
    		DistanceMeasurer.RenderOntoHUD();
            DurabilityInfo.RenderOntoHUD();
            PotionTimers.RenderOntoHUD();
            HUDEntityTrackerHelper.RenderEntityInfo(event.partialTicks);	//calls other mods that need to render things on the HUD near entities
            ItemSelector.RenderOntoHUD(event.partialTicks);
    	}
    	else if(event.type == RenderGameOverlayEvent.ElementType.DEBUG)
    	{
            AnimalInfo.RenderOntoDebugMenu();
    	}
    	
    	
    	//change how the inventories are rendered (this has to be done on every game tick)
    	if (mc.currentScreen instanceof InventoryEffectRenderer)
    	{
    		PotionTimers.DisableInventoryPotionEffects((InventoryEffectRenderer)mc.currentScreen);
    	}
    }
    

    /**
     * Event fired when the world gets rendered.
     * We render anything that need to be rendered into the game world in this method.
     * @param event
     */
    @SubscribeEvent
    public void RenderWorldLastEvent(RenderWorldLastEvent event)
    {
        //render unsafe positions (cache calculations are done from this render method)
        SafeOverlay.instance.RenderAllUnsafePositionsMultithreaded(event.partialTicks);
    	
        //calls other mods that need to render things in the game world nearby other entities
        RenderEntityTrackerHelper.RenderEntityInfo(event.partialTicks);
        
        //store world render transform matrices for later use when rendering HUD
        HUDEntityTrackerHelper.StoreMatrices();
    }
    
    
    

	/**
	 * Renders an Item icon in the 3D world at the specified coordinates
	 * @param item
	 * @param x
	 * @param y
	 * @param z
	 * @param partialTickTime
	 */
	public static void RenderFloatingItemIcon(float x, float y, float z, Item item, float partialTickTime)
    {
    	RenderManager renderManager = mc.getRenderManager();
        
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
        
        RenderItemTexture(-8, -8, item, 16, 16);

        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }
	
	/**
	 * Renders a texture at the specified location
	 * Copy/pasted static version of Gui.func_175175_a()
	 * @param x
	 * @param y
	 * @param item
	 * @param width
	 * @param height
	 */
    public static void RenderItemTexture(int x, int y, Item item, int width, int height)
    {
        IBakedModel iBakedModel = mc.getRenderItem().getItemModelMesher().getItemModel(new ItemStack(item));
        TextureAtlasSprite textureAtlasSprite = mc.getTextureMapBlocks().getAtlasSprite(iBakedModel.getTexture().getIconName());
        mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        
        RenderTexture(x, y, textureAtlasSprite, width, height, 0);
    }
    
    /**
     * 
     * @param x
     * @param y
     * @param block
     * @param width
     * @param height
     */
    public static void RenderBlockTexture(int x, int y, Block block, int width, int height)
    {
        TextureAtlasSprite textureAtlasSprite = mc.getBlockRendererDispatcher().func_175023_a().func_178122_a(block.getDefaultState());
        mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        
        RenderTexture(x, y, textureAtlasSprite, width, height, 0);
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
     * @param scale How much to scale the texture by when rendering it
     */
    public static void RenderCustomTexture(int x, int y, int u, int v, int width, int height, ResourceLocation resourceLocation, float scale)
    {
        x /= scale;
        y /= scale;
        
        GL11.glPushMatrix();
        //GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glScalef(scale, scale, scale);
        
        if(resourceLocation != null)
        	mc.getTextureManager().bindTexture(resourceLocation);
        
        mc.ingameGUI.drawTexturedModalRect(x, y, u, v, width, height);
        
        GL11.glPopMatrix();
    }
	
    /**
     * Renders a previously bound texture (with mc.getTextureManager().bindTexture())
     * @param x
     * @param y
     * @param textureAtlasSprite
     * @param width
     * @param height
     * @param zLevel
     */
	private static void RenderTexture(int x, int y, TextureAtlasSprite textureAtlasSprite, int width, int height, double zLevel)
	{
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.startDrawingQuads();
        worldrenderer.addVertexWithUV((double)(x), 			(double)(y + height), 	(double)zLevel, (double)textureAtlasSprite.getMinU(), (double)textureAtlasSprite.getMaxV());
        worldrenderer.addVertexWithUV((double)(x + width), 	(double)(y + height), 	(double)zLevel, (double)textureAtlasSprite.getMaxU(), (double)textureAtlasSprite.getMaxV());
        worldrenderer.addVertexWithUV((double)(x + width), 	(double)(y), 			(double)zLevel, (double)textureAtlasSprite.getMaxU(), (double)textureAtlasSprite.getMinV());
        worldrenderer.addVertexWithUV((double)(x), 			(double)(y), 			(double)zLevel, (double)textureAtlasSprite.getMinU(), (double)textureAtlasSprite.getMinV());
        tessellator.draw();
	}
	
	
	
	/**
	 * Renders floating text in the 3D world at a specific position.
	 * @param text The text to render
	 * @param x X coordinate in the game world
	 * @param y Y coordinate in the game world
	 * @param z Z coordinate in the game world
	 * @param color 0xRRGGBB text color
	 * @param renderBlackBackground render a pretty black border behind the text?
	 * @param partialTickTime Usually taken from RenderWorldLastEvent.partialTicks variable
	 */
    public static void RenderFloatingText(String text, float x, float y, float z, int color, boolean renderBlackBackground, float partialTickTime)
    {
    	String textArray[] = {text};
    	RenderFloatingText(textArray, x, y, z, color, renderBlackBackground, partialTickTime);
    }
    
    /**
	 * Renders floating lines of text in the 3D world at a specific position.
	 * @param text The string array of text to render
	 * @param x X coordinate in the game world
	 * @param y Y coordinate in the game world
	 * @param z Z coordinate in the game world
	 * @param color 0xRRGGBB text color
	 * @param renderBlackBackground render a pretty black border behind the text?
	 * @param partialTickTime Usually taken from RenderWorldLastEvent.partialTicks variable
	 */
    public static void RenderFloatingText(String[] text, float x, float y, float z, int color, boolean renderBlackBackground, float partialTickTime)
    {
    	//Thanks to Electric-Expansion mod for the majority of this code
    	//https://github.com/Alex-hawks/Electric-Expansion/blob/master/src/electricexpansion/client/render/RenderFloatingText.java
    	
    	RenderManager renderManager = mc.getRenderManager();
        
        float playerX = (float) (mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTickTime);
        float playerY = (float) (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTickTime);
        float playerZ = (float) (mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTickTime);

        float dx = x-playerX;
        float dy = y-playerY;
        float dz = z-playerZ;
        float distance = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        float scale = 0.03f;
        
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
            int thisMessageWidth = mc.fontRendererObj.getStringWidth(thisMessage);

            if (thisMessageWidth > textWidth)
            	textWidth = thisMessageWidth;
        }
        
        int lineHeight = 10;
        
        if(renderBlackBackground)
        {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        	
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            worldrenderer.startDrawingQuads();
            int stringMiddle = textWidth / 2;
            GlStateManager.color(0.0F, 0.0F, 0.0F, 0.5F);
            worldrenderer.addVertex(-stringMiddle - 1, -1 + 0, 0.0D);
            worldrenderer.addVertex(-stringMiddle - 1, 8 + lineHeight*text.length-lineHeight, 0.0D);
            worldrenderer.addVertex(stringMiddle + 1, 8 + lineHeight*text.length-lineHeight, 0.0D);
            worldrenderer.addVertex(stringMiddle + 1, -1 + 0, 0.0D);
            tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
        
        int i = 0;
        for(String message : text)
        {
        	mc.fontRendererObj.drawString(message, -textWidth / 2, i*lineHeight, color);
        	i++;
        }
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }
    

    /**
     * Displays a short notification to the user. Uses the Minecraft code to display messages.
     * @param message the message to be displayed
     */
    public static void DisplayNotification(String message)
    {
        mc.ingameGUI.setRecordPlaying(message, false);
    }
    
    
    
}
