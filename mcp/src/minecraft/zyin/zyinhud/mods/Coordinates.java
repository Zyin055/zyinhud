package zyin.zyinhud.mods;

import net.minecraft.client.Minecraft;
import zyin.zyinhud.util.FontCodes;

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
    
    public static String Hotkey;
    public static final String HotkeyDescription = "Zyin's HUD: Chat Coordinates";

    public static String DefaultChatStringFormat = "[{x}, {y}, {z}]";
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
     * @return "(x, z, y)" coordinates formatted string if the Coordinates are enabled, otherwise "".
     */
    public static String CalculateMessageForInfoLine()
    {
        if(Coordinates.Enabled)
        {
            int coordX = GetXCoordinate();
            int coordY = GetYCoordinate();
            int coordZ = GetZCoordinate();
            String yColor = FontCodes.WHITE;

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

            String coordinatesString = FontCodes.WHITE + "[" + coordX + ", " + coordZ + ", " + yColor + coordY + FontCodes.WHITE + "]";
            return coordinatesString + InfoLine.SPACER;
        }

        return "";
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
    
}
