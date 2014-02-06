package com.zyin.zyinhud.mods;

import net.minecraft.client.Minecraft;
import com.zyin.zyinhud.util.FontCodes;

/**
 * Calculates time.
 * <p>
 * ClockMode = 0: standard 24 hour in game time<br>
 * ClockMode = 1: real time till Minecraft's day or night cycle ends
 * <p>
 * @See {@link http://www.minecraftwiki.net/wiki/Day-night_cycle} 
 */
public class Clock
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
	 * 0=standard clock<br>
	 * 1=time till night/day<br>
	 */
    public static int Mode = 0;
    
    /** The maximum number of modes that is supported */
    public static int NumberOfModes = 2;
    
    
	private static Minecraft mc = Minecraft.getMinecraft();

	private static long mobSpawningStartTime = 13187;
	
	//mobs stop spawning at: 22813
	//mobs start to burn at: 23600
	private static long mobSpawningStopTime = 23600;
	
    /**
     * Calculates time
     * @return time if the Clock is enabled, otherwise "".
     */
    public static String CalculateMessageForInfoLine()
    {
        if (Clock.Enabled)
        {
        	if(Clock.Mode == 0)
        	{
            	//0 game time is 6am, so add 6000
                long time = (mc.theWorld.getWorldTime() + 6000) % 24000;
                
                long hours = time / 1000;
                long seconds = (long)((time % 1000) * (60.0/1000.0));
                
                String clockString = FontCodes.WHITE + String.format("%02d", hours) + ":" + String.format("%02d", seconds) + InfoLine.SPACER;
                return clockString;
        	}
        	else if(Clock.Mode == 1)
        	{
                long time = (mc.theWorld.getWorldTime()) % 24000;
                
        		if(time >= mobSpawningStartTime && time < mobSpawningStopTime)
        		{
        			//night time
        			long secondsTillDay = (mobSpawningStopTime - time) / 20;
        			
        			long minutes = secondsTillDay / 60;
        			long seconds = secondsTillDay - minutes*60;
        			
                    String nighttimeTimerString = FontCodes.GRAY + String.format("%02d", minutes) + ":" + String.format("%02d", seconds) + InfoLine.SPACER;
                    return nighttimeTimerString;
        		}
        		else
        		{
        			//day time
        			long secondsTillNight;
        			if(time > mobSpawningStopTime)
        				secondsTillNight = (24000 - time + mobSpawningStartTime) / 20;
	    			else
	    				secondsTillNight = (mobSpawningStartTime - time) / 20;
        			
        			long minutes = secondsTillNight / 60;
        			long seconds = secondsTillNight - minutes*60;

                    String daytimeTimerString = FontCodes.YELLOW + String.format("%02d", minutes) + ":" + String.format("%02d", seconds) + InfoLine.SPACER;
                    return daytimeTimerString;
        		}
        	}
        }

        return "";
    }
    
    /**
     * Increments the Clock mode
     * @return The new Clock mode
     */
    public static int ToggleMode()
    {
    	Mode++;
    	if(Mode >= NumberOfModes)
    		Mode = 0;
    	return Mode;
    }
}
