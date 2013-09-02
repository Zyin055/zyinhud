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
            int coordX = (int) Math.floor(mc.thePlayer.posX);
            int coordY = (int) Math.floor(mc.thePlayer.posY);
            int coordZ = (int) Math.floor(mc.thePlayer.posZ);
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
