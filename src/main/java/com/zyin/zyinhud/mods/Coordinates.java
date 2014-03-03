package com.zyin.zyinhud.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;

import com.zyin.zyinhud.util.FontCodes;
import com.zyin.zyinhud.util.ZyinHUDUtil;

/**
 * The Coordinates calculates the player's position.
 */
public class Coordinates
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
    
	/**
	 * 0=[x, z, y]<br>
	 * 1=[x, y, z]<br>
	 */
    public static int Mode = 0;

    /** The maximum number of modes that is supported */
    public static int NumberOfModes = 2;
    
    /** The default chat format String which replaces "{x}", "{y}", and "{z}" with coordinates */
    public static String DefaultChatStringFormat = "[{x}, {y}, {z}]";
    /** A String which replaces "{x}", "{y}", and "{z}" with coordinates */
    public static String ChatStringFormat;
    
    /** Use colors to show what ores spawn at the elevation level */
    public static boolean UseYCoordinateColors;
    
    private static Minecraft mc = Minecraft.getMinecraft();
    
    
    private static final int oreBoundaries[] =
    {
        5,	//nothing below 5
        12,	//diamonds stop
        23,	//lapis lazuli stops
        29	//gold stops
        //128	//coal stops
    };
    private static final String oreBoundaryColors[] =
    {
        FontCodes.WHITE,	//nothing below 5
        FontCodes.AQUA,		//diamonds stop
        FontCodes.BLUE,		//lapis lazuli stops
        FontCodes.YELLOW	//gold stops
        //FontCodes.GRAY		//coal stops
    };

    /**
     * Calculates the players coordinates
     * @return coordinates string if the Coordinates are enabled, otherwise "".
     */
    public static String CalculateMessageForInfoLine()
    {
        if(Coordinates.Enabled)
        {
            int coordX = GetXCoordinate();
            int coordY = GetYCoordinate();
            int coordZ = GetZCoordinate();
            String yColor = "";

            if (UseYCoordinateColors)
            {
                for (int y = 0; y < oreBoundaries.length; y++)
                {
                    if (coordY < oreBoundaries[y])
                    {
                        yColor = oreBoundaryColors[y];
                        break;
                    }
                }
            }

            String coordinatesString;
            if(Mode == 0)
            	coordinatesString = FontCodes.WHITE + "[" + coordX + ", " + coordZ + ", " + yColor + coordY + FontCodes.WHITE + "]";
            else if(Mode == 1)
            	coordinatesString = FontCodes.WHITE + "[" + coordX + ", " + yColor + coordY + FontCodes.WHITE + ", " + coordZ + "]";
            else
            	coordinatesString = FontCodes.WHITE + "[??, ??, ??]";
            
            return coordinatesString + InfoLine.SPACER;
        }

        return "";
    }
    
    public static void PasteCoordinatesIntoChat()
    {
    	if(mc.currentScreen != null && mc.currentScreen instanceof GuiChat)
    	{
        	String coordinateString = Coordinates.ChatStringFormat;
        	coordinateString = coordinateString.replace("{x}", Integer.toString(Coordinates.GetXCoordinate()));
        	coordinateString = coordinateString.replace("{y}", Integer.toString(Coordinates.GetYCoordinate()));
        	coordinateString = coordinateString.replace("{z}", Integer.toString(Coordinates.GetZCoordinate()));
        	
        	GuiTextField inputField = ZyinHUDUtil.GetFieldByReflection(GuiChat.class, (GuiChat)mc.currentScreen, "inputField","field_146415_a");
        	
        	if(inputField != null)
        	{
        		inputField.writeText(coordinateString);
        	}
    	}
    }

    public static int GetXCoordinate()
    {
    	return (int) Math.floor(mc.thePlayer.posX);
    }
    public static int GetYCoordinate()
    {
    	return (int) Math.floor(mc.thePlayer.posY);
    }
    public static int GetZCoordinate()
    {
    	return (int) Math.floor(mc.thePlayer.posZ);
    }

    /**
     * Toggles using color coded y coordinates
     * @return The state it was changed to
     */
    public static boolean ToggleUseYCoordinateColors()
    {
    	UseYCoordinateColors = !UseYCoordinateColors;
    	return UseYCoordinateColors;
    }

    /**
     * Increments the Distance Measurer mode
     * @return The new Distance Measurer mode
     */
    public static int ToggleMode()
    {
    	Mode++;
    	if(Mode >= NumberOfModes)
    		Mode = 0;
    	return Mode;
    }
}
