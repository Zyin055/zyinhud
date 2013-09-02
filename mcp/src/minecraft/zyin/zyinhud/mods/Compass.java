package zyin.zyinhud.mods;

import net.minecraft.client.Minecraft;
import zyin.zyinhud.util.FontCodes;
import zyin.zyinhud.util.Localization;

/**
 * The Compass determines what direction the player is facing.
 */
public class Compass
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

    /**
     * Calculates the direction the player is facing
     * @return "[Direction]" compass formatted string if the Compass is enabled, otherwise "".
     */
    public static String CalculateMessageForInfoLine()
    {
        if (Compass.Enabled)
        {
            int yaw = (int)mc.thePlayer.rotationYaw;
            yaw += 22;	//+22 centers the compass (45degrees/2)
            yaw %= 360;

            if (yaw < 0)
            {
                yaw += 360;
            }

            int facing = yaw / 45; //  360degrees divided by 45 == 8 zones
            String compassDirection = "";

            if (facing == 0)
            {
                compassDirection = Localization.get("compass.south");
            }
            else if (facing == 1)
            {
                compassDirection = Localization.get("compass.southwest");
            }
            else if (facing == 2)
            {
                compassDirection = Localization.get("compass.west");
            }
            else if (facing == 3)
            {
                compassDirection = Localization.get("compass.northwest");
            }
            else if (facing == 4)
            {
                compassDirection = Localization.get("compass.north");
            }
            else if (facing == 5)
            {
                compassDirection = Localization.get("compass.northeast");
            }
            else if (facing == 6)
            {
                compassDirection = Localization.get("compass.east");
            }
            else// if(facing == 7)
            {
                compassDirection = Localization.get("compass.southeast");
            }

            String compassString = FontCodes.GRAY + "[" + FontCodes.RED + compassDirection + FontCodes.GRAY + "]";
            return compassString + InfoLine.SPACER;
        }

        return "";
    }
    
    
}
