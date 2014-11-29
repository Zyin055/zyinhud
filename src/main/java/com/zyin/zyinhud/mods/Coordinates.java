package com.zyin.zyinhud.mods;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;

import com.zyin.zyinhud.util.Localization;
import com.zyin.zyinhud.util.ZyinHUDUtil;

/**
 * The Coordinates calculates the player's position.
 */
public class Coordinates extends ZyinHUDModBase
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
    
	/** The current mode for this mod */
	public static Modes Mode;
	
	/** The enum for the different types of Modes this mod can have */
    public static enum Modes
    {
        XZY(Localization.get("coordinates.mode.xzy")),
        XYZ(Localization.get("coordinates.mode.xyz"));
        
        private String friendlyName;
        
        private Modes(String friendlyName)
        {
        	this.friendlyName = friendlyName;
        }

        /**
         * Sets the next availble mode for this mod
         */
        public static Modes ToggleMode()
        {
        	return Mode = Mode.ordinal() < Modes.values().length - 1 ? Modes.values()[Mode.ordinal() + 1] : Modes.values()[0];
        }
        
        /**
         * Gets the mode based on its internal name as written in the enum declaration
         * @param modeName
         * @return
         */
        public static Modes GetMode(String modeName)
        {
        	try {return Modes.valueOf(modeName);}
        	catch (IllegalArgumentException e) {return XZY;}
        }
        
        public String GetFriendlyName()
        {
        	return friendlyName;
        }
    }
    
    /** The default chat format String which replaces "{x}", "{y}", and "{z}" with coordinates */
    public static String DefaultChatStringFormat = "[{x}, {y}, {z}]";
    /** A String which replaces "{x}", "{y}", and "{z}" with coordinates */
    public static String ChatStringFormat;
    
    /** Use colors to show what ores spawn at the elevation level */
    public static boolean UseYCoordinateColors;
    
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
        EnumChatFormatting.WHITE.toString(),	//nothing below 5
        EnumChatFormatting.AQUA.toString(),		//diamonds stop
        EnumChatFormatting.BLUE.toString(),		//lapis lazuli stops
        EnumChatFormatting.YELLOW.toString()	//gold stops
        //EnumChatFormatting.GRAY		//coal stops
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
            if(Mode == Modes.XZY)
            	coordinatesString = EnumChatFormatting.WHITE + "[" + coordX + ", " + coordZ + ", " + yColor + coordY + EnumChatFormatting.WHITE + "]";
            else if(Mode == Modes.XYZ)
            	coordinatesString = EnumChatFormatting.WHITE + "[" + coordX + ", " + yColor + coordY + EnumChatFormatting.WHITE + ", " + coordZ + "]";
            else
            	coordinatesString = EnumChatFormatting.WHITE + "[??, ??, ??]";
            
            return coordinatesString;
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
    	return UseYCoordinateColors = !UseYCoordinateColors;
    }
}
