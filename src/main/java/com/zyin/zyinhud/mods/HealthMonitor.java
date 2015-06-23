package com.zyin.zyinhud.mods;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import com.zyin.zyinhud.ZyinHUDSound;
import com.zyin.zyinhud.mods.EatingAid.Modes;
import com.zyin.zyinhud.util.Localization;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

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
        OOT(Localization.get("healthmonitor.mode.oot"), "lowhealth_OoT"),
        LTTP(Localization.get("healthmonitor.mode.lttp"), "lowhealth_LttP"),
        ORACLE(Localization.get("healthmonitor.mode.oracle"), "lowhealth_Oracle"),
        LA(Localization.get("healthmonitor.mode.la"), "lowhealth_LA"),
        LOZ(Localization.get("healthmonitor.mode.loz"), "lowhealth_LoZ"),
        AOL(Localization.get("healthmonitor.mode.aol"), "lowhealth_AoL");
        
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
	
	public HealthMonitor()
	{
		
	}
	
	/**
	 * We use a ClientTickEvent instead of a LivingHurtEvent because a LivingHurtEvent will only
	 * fire in single player, whereas a ClientTickEvent fires in both single and multi player.
	 * PlayerTickEvent ticks for every player rendered.
	 * WorldTickEvent doesn't work on servers.
	 * @param event
	 */
    @SubscribeEvent
	public void ClientTickEvent(ClientTickEvent event)
	{
		//only play the sound if it's not playing already
		if(HealthMonitor.Enabled && !isPlayingLowHealthSound)
		{
			PlayLowHealthSoundIfHurt();
		}
	}
	
	
	/**
	 * Checks to see if the player has less health than the set threshold, and will play a
	 * warning sound on a 1 second loop until they heal up.
	 */
	protected static void PlayLowHealthSoundIfHurt()
	{
		if(mc.thePlayer != null)
		{
			int playerHealth = (int)mc.thePlayer.getHealth();
			if(playerHealth < LowHealthSoundThreshold && playerHealth > 0)
			{
				//don't play the sound if the user is looking at a screen or in creative
				if(!mc.playerController.isInCreativeMode() && !mc.isGamePaused())// && mc.inGameHasFocus)
					PlayLowHealthSound();
				
				isPlayingLowHealthSound = true;
				
				int soundDelay = repeatDelay;
				
				if(PlayFasterNearDeath)
					soundDelay = repeatDelay/2 + (int)((float)repeatDelay/2 * ((float)playerHealth / (float)LowHealthSoundThreshold));
				
				TimerTask t = new PlayLowHealthSoundTimerTask();
				timer.schedule(t, soundDelay);
				
				return;
			}
		}
		
		isPlayingLowHealthSound = false;
	}
	
	
	/**
	 * Gets the name of the sound resource associated with the current mode.
	 * Sound resouce names are declared in assets/zyinhud/sounds.json.
	 * @return
	 */
	protected static String GetSoundNameFromMode()
	{
		return Mode.soundName;
	}
	
	/**
	 * Plays the low health warning sound right now.
	 */
	public static void PlayLowHealthSound()
	{
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
		LowHealthSoundThreshold = MathHelper.clamp_int(lowHealthSoundThreshold, 1, 20);
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
    	return PlayFasterNearDeath = !PlayFasterNearDeath;
    }
}
