package com.zyin.zyinhud.mods;

import net.minecraft.client.Minecraft;
import com.zyin.zyinhud.util.FontCodes;
import com.zyin.zyinhud.util.Localization;

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

    private static String south = Localization.get("compass.south");
    private static String southwest = Localization.get("compass.southwest");
    private static String west = Localization.get("compass.west");
    private static String northwest = Localization.get("compass.northwest");
    private static String north = Localization.get("compass.north");
    private static String northeast = Localization.get("compass.northeast");
    private static String east = Localization.get("compass.east");
    private static String southeast = Localization.get("compass.southeast");

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
                compassDirection = south;
            else if (facing == 1)
                compassDirection = southwest;
            else if (facing == 2)
                compassDirection = west;
            else if (facing == 3)
                compassDirection = northwest;
            else if (facing == 4)
                compassDirection = north;
            else if (facing == 5)
                compassDirection = northeast;
            else if (facing == 6)
                compassDirection = east;
            else// if(facing == 7)
                compassDirection = southeast;

            String compassString = FontCodes.GRAY + "[" + FontCodes.RED + compassDirection + FontCodes.GRAY + "]";
            return compassString + InfoLine.SPACER;
        }

        return "";
    }
    
    
}
