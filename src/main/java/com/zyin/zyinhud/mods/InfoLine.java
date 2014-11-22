package com.zyin.zyinhud.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.chunk.Chunk;

import org.lwjgl.opengl.GL11;

import com.zyin.zyinhud.gui.GuiZyinHUDOptions;
import com.zyin.zyinhud.util.FontCodes;
import com.zyin.zyinhud.util.Localization;

/**
 * The Info Line consists of everything that gets displayed in the top-left portion
 * of the screen. It's job is to gather information about other classes and render
 * their message into the Info Line.
 */
public class InfoLine extends ZyinHUDModBase
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
    
    public static boolean ShowBiome;
    public static boolean ShowCanSnow;
    
    /**  The padding string that is inserted between different elements of the Info Line */
    public static final String SPACER = " ";
    public static int infoLineLocX = 1;
    public static int infoLineLocY = 1;

    /*private static final int notificationDuration = 1200;	//measured in milliseconds
    private static long notificationTimer = 0;				//timer that goes from notificationDuration to 0
    private static long notificationStartTime;*/
    
    /** The notification string currently being rendered */
    public static String notificationMessage = "";
    
    /** The info line string currently being rendered */
    public static String infoLineMessage;
    

    /**
     * Renders the on screen message consisting of everything that gets put into the top let message area,
     * including coordinates and the state of things that can be activated
     */
    public static void RenderOntoHUD()
    {
        //if the player is in the world
        //and not looking at a menu
        //and F3 not pressed
        if (InfoLine.Enabled &&
                (mc.inGameHasFocus || (mc.currentScreen != null && (mc.currentScreen instanceof GuiChat || TabIsSelectedInOptionsGui()))) &&
                !mc.gameSettings.showDebugInfo)
        {
        	infoLineMessage = "";
        	
            String clock = Clock.CalculateMessageForInfoLine(infoLineMessage);
            if (clock.length() > 0)
            	clock += SPACER;
            infoLineMessage += clock;
            
            String coordinates = Coordinates.CalculateMessageForInfoLine();
            if (coordinates.length() > 0)
            	coordinates += SPACER;
            infoLineMessage = infoLineMessage + coordinates;
            
            String compass = Compass.CalculateMessageForInfoLine(infoLineMessage);
            if (compass.length() > 0)
            	compass += SPACER;
            infoLineMessage += compass;
            
            String fps = Fps.CalculateMessageForInfoLine();
            if (fps.length() > 0)
            	fps += SPACER;
            infoLineMessage += fps;
            
            String snow = ShowCanSnow ? CalculateCanSnowForInfoLine(infoLineMessage) : "";
            if (snow.length() > 0)
            	snow += SPACER;
            infoLineMessage += snow;
            
            String biome = ShowBiome ? CalculateBiomeForInfoLine() : "";
            if (biome.length() > 0)
            	biome += SPACER;
            infoLineMessage += biome;
            
            String safe = SafeOverlay.CalculateMessageForInfoLine();
            if (safe.length() > 0)
            	safe += SPACER;
            infoLineMessage += safe;
            
            String players = PlayerLocator.CalculateMessageForInfoLine();
            if (players.length() > 0)
            	players += SPACER;
            infoLineMessage += players;
            
            String animals = AnimalInfo.CalculateMessageForInfoLine();
            if (animals.length() > 0)
            	animals += SPACER;
            infoLineMessage += animals;

            mc.fontRenderer.drawStringWithShadow(infoLineMessage, infoLineLocX, infoLineLocY, 0xffffff);
        }

        /*if (notificationTimer > 0)
        {
            RenderNotification(notificationMessage);
        }*/
    }
    
    private static void renderGlint(int par1, int par2, int par3, int par4, int par5)
    {
        for (int j1 = 0; j1 < 2; ++j1)
        {
            OpenGlHelper.glBlendFunc(772, 1, 0, 0);
            float f = 0.00390625F;
            float f1 = 0.00390625F;
            float f2 = (float)(Minecraft.getSystemTime() % (long)(3000 + j1 * 1873)) / (3000.0F + (float)(j1 * 1873)) * 256.0F;
            float f3 = 0.0F;
            Tessellator tessellator = Tessellator.instance;
            float f4 = 4.0F;

            if (j1 == 1)
            {
                f4 = -1.0F;
            }

            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + par5), (double)itemRenderer.zLevel, (double)((f2 + (float)par5 * f4) * f), (double)((f3 + (float)par5) * f1));
            tessellator.addVertexWithUV((double)(par2 + par4), (double)(par3 + par5), (double)itemRenderer.zLevel, (double)((f2 + (float)par4 + (float)par5 * f4) * f), (double)((f3 + (float)par5) * f1));
            tessellator.addVertexWithUV((double)(par2 + par4), (double)(par3 + 0), (double)itemRenderer.zLevel, (double)((f2 + (float)par4) * f), (double)((f3 + 0.0F) * f1));
            tessellator.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)itemRenderer.zLevel, (double)((f2 + 0.0F) * f), (double)((f3 + 0.0F) * f1));
            tessellator.draw();
        }
    }
    
    protected static String CalculateCanSnowForInfoLine(String infoLineMessageUpToThisPoint)
    {
    	int xCoord = MathHelper.floor_double(mc.thePlayer.posX);
        int yCoord = MathHelper.floor_double(mc.thePlayer.posY) - 1;
        int zCoord = MathHelper.floor_double(mc.thePlayer.posZ);
        
    	boolean canSnowAtPlayersFeet = mc.theWorld.canSnowAtBody(xCoord, yCoord, zCoord, false);
    	
    	if(canSnowAtPlayersFeet)
    	{
    		float scaler = 0.66f;
    		GL11.glScalef(scaler, scaler, scaler);
    		
    		int x = (int)(mc.fontRenderer.getStringWidth(infoLineMessageUpToThisPoint) / scaler);
    		int y = (int)(-1);
    		
    		itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, new ItemStack(Items.snowball), x, y);
    		
    		GL11.glDisable(GL11.GL_LIGHTING);
    		GL11.glScalef(1/scaler, 1/scaler, 1/scaler);
    		
        	return "  ";
    	}
    	return "";
    }
    
    protected static String CalculateBiomeForInfoLine()
    {
    	int xCoord = MathHelper.floor_double(mc.thePlayer.posX);
        int zCoord = MathHelper.floor_double(mc.thePlayer.posZ);
        
    	Chunk chunk = mc.theWorld.getChunkFromBlockCoords(xCoord, zCoord);
    	String biomeName = chunk.getBiomeGenForWorldCoords(xCoord & 15, zCoord & 15, mc.theWorld.getWorldChunkManager()).biomeName;
    	
    	return FontCodes.WHITE + biomeName + " ";
    }

    /**
     * Displays a short notification to the user.
     * @param message the message to be displayed
     */
    /*public static void DisplayNotification(String message)
    {
        notificationMessage = message;
        notificationTimer = notificationDuration;
        notificationStartTime = System.currentTimeMillis();
    }*/

    /**
     * Renders a short message on the screen.
     * @param message the message to be displayed
     */
    /*private static void RenderNotification(String message)
    {
        if ((mc.inGameHasFocus || mc.currentScreen == null))
        {
            ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            int width = res.getScaledWidth();		//~427
            int height = res.getScaledHeight();	//~240
            int overlayMessageWidth = mc.fontRenderer.getStringWidth(notificationMessage);
            int x = width / 2 - overlayMessageWidth / 2;
            int y = height - 65;
            double alphaLevel;	//ranges from [0..1]

            if ((double)notificationTimer * 2 / notificationDuration > 1)
            {
                alphaLevel = 1;    //for the first half of the notifications rendering we want it 100% opaque.
            }
            else
            {
                alphaLevel = (double)notificationTimer * 2 / notificationDuration;    //for the second half, we want it to fade out.
            }

            int alpha = (int)(0x33 + 0xCC * alphaLevel);
            alpha = alpha << 24;	//turns it into the format: 0x##000000
            int rgb = 0xFFFFFF;
            int color = rgb + alpha;	//alpha:r:g:b
            mc.fontRenderer.drawStringWithShadow(notificationMessage, x, y, color);
        }

        notificationTimer = notificationStartTime - System.currentTimeMillis() + notificationDuration;	//counts down from 1000 to 0
    }*/
    

    /**
     * Checks to see if the Info Line, Clock, Coordinates, Compass, or FPS tabs are selected in GuiZyinHUDOptions
     * @return
     */
    private static boolean TabIsSelectedInOptionsGui()
    {
    	return mc.currentScreen instanceof GuiZyinHUDOptions &&
    		(((GuiZyinHUDOptions)mc.currentScreen).IsButtonTabSelected(Localization.get("infoline.name")) ||
			((GuiZyinHUDOptions)mc.currentScreen).IsButtonTabSelected(Localization.get("clock.name")) ||
			((GuiZyinHUDOptions)mc.currentScreen).IsButtonTabSelected(Localization.get("coordinates.name")) ||
			((GuiZyinHUDOptions)mc.currentScreen).IsButtonTabSelected(Localization.get("compass.name")) ||
			((GuiZyinHUDOptions)mc.currentScreen).IsButtonTabSelected(Localization.get("fps.name")));
    }


    /**
     * Toggles showing the biome in the Info Line
     * @return The state it was changed to
     */
    public static boolean ToggleShowBiome()
    {
    	return ShowBiome = !ShowBiome;
    }

    /**
     * Toggles showing if it is possible for snow to fall at the player's feet in the Info Line
     * @return The state it was changed to
     */
    public static boolean ToggleShowCanSnow()
    {
    	return ShowCanSnow = !ShowCanSnow;
    }
    

    
    /**
     * Gets the horizontal location where the potion timers are rendered.
     * @return
     */
    public static int GetHorizontalLocation()
    {
    	return infoLineLocX;
    }
    
    /**
     * Sets the horizontal location where the potion timers are rendered.
     * @param x
     * @return the new x location
     */
    public static int SetHorizontalLocation(int x)
    {
    	infoLineLocX = MathHelper.clamp_int(x, 0, mc.displayWidth);
    	return infoLineLocX;
    }
    
    /**
     * Gets the vertical location where the potion timers are rendered.
     * @return
     */
    public static int GetVerticalLocation()
    {
    	return infoLineLocY;
    }

    /**
     * Sets the vertical location where the potion timers are rendered.
     * @param y
     * @return the new y location
     */
    public static int SetVerticalLocation(int y)
    {
    	infoLineLocY = MathHelper.clamp_int(y, 0, mc.displayHeight);
    	return infoLineLocY;
    }
}
