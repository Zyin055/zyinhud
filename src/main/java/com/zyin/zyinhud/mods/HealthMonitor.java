package com.zyin.zyinhud.mods;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import com.zyin.zyinhud.ZyinHUDSound;
import com.zyin.zyinhud.mods.EatingAid.Modes;
import com.zyin.zyinhud.util.Localization;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Plays a warning sound when the player is low on health.
 */
public class HealthMonitor extends ZyinHUDModBase
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
        OOT(Localization.get("healthmonitor.mode.0"), "lowhealth_OoT"),
        LTTP(Localization.get("healthmonitor.mode.1"), "lowhealth_LttP"),
        ORACLE(Localization.get("healthmonitor.mode.2"), "lowhealth_Oracle"),
        LA(Localization.get("healthmonitor.mode.3"), "lowhealth_LA"),
        LOZ(Localization.get("healthmonitor.mode.4"), "lowhealth_LoZ"),
        AOL(Localization.get("healthmonitor.mode.5"), "lowhealth_AoL");
        
        private String friendlyName;
        public String soundName;
        
        private Modes(String friendlyName, String soundName)
        {
        	this.friendlyName = friendlyName;
        	this.soundName = soundName;
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
        	catch (IllegalArgumentException e) {return OOT;}
        }
        
        public String GetFriendlyName()
        {
        	return friendlyName;
        }
    }
    
	private static Timer timer = new Timer();
	
	private static int LowHealthSoundThreshold;
	public static boolean PlayFasterNearDeath;
	
	private static boolean isPlayingLowHealthSound = false;
	private static int repeatDelay = 1000;
	
	public static HealthMonitor instance = new HealthMonitor();
	
	@SubscribeEvent
	public void LivingHurtEvent(LivingHurtEvent event)
	{
		if(HealthMonitor.Enabled &&
				event.entity.equals(mc.thePlayer) &&
				!isPlayingLowHealthSound)
		{
			PlayLowHealthSoundIfHurt();
		}
	}
	
	/**
	 * Checks to see if the player has less health than the set threshold, and will play a
	 * warning sound on a 1 second loop until they heal up.
	 */
	public static void PlayLowHealthSoundIfHurt()
	{
		int playerHealth = (int)mc.thePlayer.getHealth();
		if(playerHealth < LowHealthSoundThreshold)
		{
			if(!mc.playerController.isInCreativeMode() && !mc.isGamePaused())
				PlayLowHealthSound();
			
			isPlayingLowHealthSound = true;
			
			int soundDelay = repeatDelay;
			
			if(PlayFasterNearDeath)
				soundDelay = (int)((float)repeatDelay * ((float)playerHealth / (float)LowHealthSoundThreshold));
			
			TimerTask t = new PlayLowHealthSoundTimerTask();
			timer.schedule(t, soundDelay);
		}
		else
		{
			isPlayingLowHealthSound = false;
		}
	}
	
	
	/**
	 * Gets the name of the sound resource associated with the current mode.
	 * Sound resouce names are declared in assets/zyinhud/sounds.json.
	 * @return
	 */
	private static String GetSoundNameFromMode()
	{
		return Mode.soundName;
	}
	
	/**
	 * Plays the low health warning sound right now.
	 */
	public static void PlayLowHealthSound()
	{
		System.out.println("PlayLowHealthSound()");
		ZyinHUDSound.PlaySound(GetSoundNameFromMode());
	}
	
	private static class PlayLowHealthSoundTimerTask extends TimerTask
    {
        PlayLowHealthSoundTimerTask()
        {
        	
        }

        @Override
        public void run()
        {
        	PlayLowHealthSoundIfHurt();
        }
    }
	
	public static void SetLowHealthSoundThreshold(int lowHealthSoundThreshold)
	{
		if(lowHealthSoundThreshold > 20)
			LowHealthSoundThreshold = 20;
		else if(lowHealthSoundThreshold < 1)
			LowHealthSoundThreshold = 1;
		else
			LowHealthSoundThreshold = lowHealthSoundThreshold;
	}
	public static int GetLowHealthSoundThreshold()
	{
		return LowHealthSoundThreshold;
	}
	

    /**
     * Toggles making the sound play quicker when close to dieing
     * @return 
     */
    public static boolean TogglePlayFasterNearDeath()
    {
    	PlayFasterNearDeath = !PlayFasterNearDeath;
    	return PlayFasterNearDeath;
    }
}
