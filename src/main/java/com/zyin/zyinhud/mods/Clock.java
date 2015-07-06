package com.zyin.zyinhud.mods;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import com.zyin.zyinhud.util.Localization;

/**
 * Calculates time.
 * @See {@link http://www.minecraftwiki.net/wiki/Day-night_cycle} 
 */
public class Clock extends ZyinHUDModBase
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
        STANDARD(Localization.get("clock.mode.standard")),
        COUNTDOWN(Localization.get("clock.mode.countdown")),
        GRAPHIC(Localization.get("clock.mode.graphic"));
        
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
        	catch (IllegalArgumentException e) {return values()[1];}
        }
        
        public String GetFriendlyName()
        {
        	return friendlyName;
        }
    }
	
	private static long mobSpawningStartTime = 13187;
	
	//mobs stop spawning at: 22813
	//mobs start to burn at: 23600
	private static long mobSpawningStopTime = 23600;
	
    /**
     * Calculates time
     * @return time if the Clock is enabled, otherwise "".
     */
    public static String CalculateMessageForInfoLine(String infoLineMessageUpToThisPoint)
    {
        if (Clock.Enabled)
        {
        	if(Clock.Mode == Modes.STANDARD)
        	{
            	//0 game time is 6am, so add 6000
                long time = (mc.theWorld.getWorldTime() + 6000) % 24000;
                
                long hours = time / 1000;
                long seconds = (long)((time % 1000) * (60.0/1000.0));

                if(IsNight())
        		{
                    String nighttimeClockString = EnumChatFormatting.GRAY + String.format("%02d", hours) + ":" + String.format("%02d", seconds);
                    return nighttimeClockString;
        		}
                else
        		{
                    String daytimeClockString = EnumChatFormatting.YELLOW + String.format("%02d", hours) + ":" + String.format("%02d", seconds);
                    return daytimeClockString;
        		}
        	}
        	else if(Clock.Mode == Modes.COUNTDOWN)
        	{
                long time = (mc.theWorld.getWorldTime()) % 24000;
                
        		if(IsNight())
        		{
        			//night time
        			long secondsTillDay = (mobSpawningStopTime - time) / 20;
        			
        			long minutes = secondsTillDay / 60;
        			long seconds = secondsTillDay - minutes*60;
        			
                    //String nighttimeCountdownString = FontCodes.GRAY + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        			String nighttimeCountdownString = EnumChatFormatting.GRAY + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
                    return nighttimeCountdownString;
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

                    String daytimeCountdownString = EnumChatFormatting.YELLOW + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
                    return daytimeCountdownString;
        		}
        	}
        	else if(Clock.Mode == Modes.GRAPHIC)
        	{
        		int infoLineWidth = mc.fontRendererObj.getStringWidth(infoLineMessageUpToThisPoint);
        		//itemRenderer.renderItemIntoGUI(mc.fontRendererObj, mc.renderEngine, new ItemStack(Items.clock), infoLineWidth + InfoLine.infoLineLocX, InfoLine.infoLineLocY);
        		itemRenderer.func_180450_b(new ItemStack(Items.clock), infoLineWidth + InfoLine.infoLineLocX, InfoLine.infoLineLocY);	//func_180450_b() is renderItemAndEffectIntoGUI()
        		
        		GL11.glDisable(GL11.GL_LIGHTING);	//this is needed because the RenderItem.renderItem() methods enable lighting
        		
        		return "     ";	//about the length of the clock graphic
        	}
        }

        return "";
    }
    
    public static boolean IsNight()
    {
    	long time = (mc.theWorld.getWorldTime()) % 24000;
    	return time >= mobSpawningStartTime && time < mobSpawningStopTime;
    }
}
