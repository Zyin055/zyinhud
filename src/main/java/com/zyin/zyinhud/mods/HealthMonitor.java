package com.zyin.zyinhud.mods;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import com.zyin.zyinhud.ZyinHUDSound;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Plays a warning sound when the player is low on health.
 */
public class HealthMonitor
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
	 * 0=OoT<br>
	 * 1=LttP<br>
	 * 2=Oracle<br>
	 * 3=LA<br>
	 * 4=LoZ<br>
	 * 5=AoL<br>
	 */
    public static int Mode = 0;
    
    /** The maximum number of modes that is supported */
    public static int NumberOfModes = 6;
    
	private static final Minecraft mc = Minecraft.getMinecraft();
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
	
	public static void PlayLowHealthSound()
	{
		ZyinHUDSound.PlaySound(GetSoundNameFromMode());
	}
	
	/**
	 * Gets the name of the sound resource associated with the current mode.
	 * Sound resouce names are declared in assets/zyinhud/sounds.json.
	 * @return
	 */
	private static String GetSoundNameFromMode()
	{
		switch (Mode)
		{
			case 0: return "lowhealth_OoT";
			case 1: return "lowhealth_LttP";
			case 2: return "lowhealth_Oracle";
			case 3: return "lowhealth_LA";
			case 4: return "lowhealth_LoZ";
			case 5: return "lowhealth_AoL";
			default: return "";
		}
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
