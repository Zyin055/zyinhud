package com.zyin.zyinhud.mods;

import com.zyin.zyinhud.util.FontCodes;
import com.zyin.zyinhud.util.Localization;

public class Fps extends ZyinHUDModBase
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
	
    public static String currentFps = "0";

    public static String CalculateMessageForInfoLine()
    {
        if (Fps.Enabled)
        {
            currentFps = mc.debug.substring(0, mc.debug.indexOf(' '));
        	return FontCodes.WHITE + currentFps + " " + Localization.get("fps.infoline");
        }
        else
        {
            return "";
        }
    }
}
