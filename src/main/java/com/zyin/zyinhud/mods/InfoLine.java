package com.zyin.zyinhud.mods;

import com.zyin.zyinhud.gui.GuiZyinHUDOptions;
import com.zyin.zyinhud.util.FontCodes;
import com.zyin.zyinhud.util.Localization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import net.minecraft.world.chunk.Chunk;

/**
 * The Info Line consists of everything that gets displayed in the top-left portion
 * of the screen. It's job is to gather information about other classes and render
 * their message into the Info Line.
 */
public class InfoLine
{
	/** Enables/Disables this Mod */
	public static boolean Enabled;

    /**
     * Toggles this Mod on or off
     * @return The state the Mod was changed to
     */
    public static boolean ToggleEnabled()
    {
    	Enabled = !Enabled;
    	return Enabled;
    }
    private static Minecraft mc = Minecraft.getMinecraft();
    
    public static boolean ShowBiome;
    
    /**
     * The padding string that is inserted between different elements of the Info Line
     */
    public static final String SPACER = " ";
    public static int infoLineLocX = 1;
    public static int infoLineLocY = 1;

    private static final int notificationDuration = 1200;	//measured in milliseconds
    private static long notificationTimer = 0;				//timer that goes from notificationDuration to 0
    private static long notificationStartTime;
    private static String notificationMessage = "";
    

    /**
     * Renders the on screen message consisting of everything that gets put into the top let message area,
     * including coordinates and the state of things that can be activated
     */
    public static void RenderOntoHUD()
    {//System.out.println("mc.inGameHasFocus="+mc.inGameHasFocus);
        //if the player is in the world
        //and not looking at a menu
        //and F3 not pressed
        if (InfoLine.Enabled &&
                (mc.inGameHasFocus || (mc.currentScreen != null && (mc.currentScreen instanceof GuiChat || TabIsSelectedInOptionsGui()))) &&
                !mc.gameSettings.showDebugInfo)
        {
            String clock = Clock.CalculateMessageForInfoLine();
            String coordinates = Coordinates.CalculateMessageForInfoLine();
            String compass = Compass.CalculateMessageForInfoLine();
            String distance = DistanceMeasurer.CalculateMessageForInfoLine();
            String biome = ShowBiome ? CalculateBiomeForInfoLine() : "";
            String fps = Fps.CalculateMessageForInfoLine();
            String safe = SafeOverlay.CalculateMessageForInfoLine();
            String players = PlayerLocator.CalculateMessageForInfoLine();
            String animals = AnimalInfo.CalculateMessageForInfoLine();
            
            String message = clock + coordinates + compass + distance + biome + fps + safe + players + animals;
            mc.fontRenderer.drawStringWithShadow(message, infoLineLocX, infoLineLocY, 0xffffff);
        }

        if (notificationTimer > 0)
        {
            RenderNotification(notificationMessage);
        }
    }
    
    protected static String CalculateBiomeForInfoLine()
    {
    	//System.out.println(mc.debugInfoRenders());
    	
    	int xCoord = MathHelper.floor_double(mc.thePlayer.posX);
        int yCoord = MathHelper.floor_double(mc.thePlayer.posZ);
        
    	Chunk chunk = mc.theWorld.getChunkFromBlockCoords(xCoord, yCoord);
    	String biome = chunk.getBiomeGenForWorldCoords(xCoord & 15, yCoord & 15, mc.theWorld.getWorldChunkManager()).biomeName;
    	
    	return FontCodes.WHITE + biome + " ";
    }

    /**
     * Displays a short notification to the user.
     * @param message the message to be displayed
     */
    public static void DisplayNotification(String message)
    {
        notificationMessage = message;
        notificationTimer = notificationDuration;
        notificationStartTime = System.currentTimeMillis();
    }

    /**
     * Renders a short message on the screen.
     * @param message the message to be displayed
     */
    private static void RenderNotification(String message)
    {
        if ((mc.inGameHasFocus || mc.currentScreen == null))
        {
            ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
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
    }
    

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
    	ShowBiome = !ShowBiome;
    	return ShowBiome;
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
    	if(x < 0)
    		x = 0;
    	else if(x > mc.displayWidth)
    		x = mc.displayWidth;
    	
    	infoLineLocX = x;
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
    	if(y < 0)
    		y = 0;
    	else if(y > mc.displayHeight)
    		y = mc.displayHeight;
    	
    	infoLineLocY = y;
    	return infoLineLocY;
    }
}
