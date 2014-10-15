package com.zyin.zyinhud.keyhandlers;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.ZyinHUDSound;
import com.zyin.zyinhud.mods.InfoLine;
import com.zyin.zyinhud.mods.SafeOverlay;
import com.zyin.zyinhud.mods.SafeOverlay.Modes;
import com.zyin.zyinhud.util.Localization;
import com.zyin.zyinhud.util.ZyinHUDUtil;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class SafeOverlayKeyHandler implements ZyinHUDKeyHandlerBase
{
    public static final String HotkeyDescription = "key.zyinhud.safeoverlay";
    
	public static void Pressed(KeyInputEvent event) 
	{
		if (mc.currentScreen != null)
        {
            return;    //don't activate if the user is looking at a GUI
        }
        
        if(!SafeOverlay.Enabled)
        	return;

        //if "+" is pressed, increase the draw distance
        if (Keyboard.isKeyDown(Keyboard.KEY_EQUALS) || 	//keyboard "+" ("=")
                Keyboard.isKeyDown(Keyboard.KEY_ADD))	//numpad "+"
        {
            int drawDistance = SafeOverlay.instance.IncreaseDrawDistance();

            if (drawDistance == SafeOverlay.maxDrawDistance)
            {
                ZyinHUDUtil.DisplayNotification(Localization.get("safeoverlay.distance") + " " + drawDistance + " ("+Localization.get("safeoverlay.distance.max")+")");
            }
            else
            {
                ZyinHUDUtil.DisplayNotification(Localization.get("safeoverlay.distance") + " " + drawDistance);
            }

            SafeOverlay.instance.RecalculateUnsafePositions();
            return;
        }

        //if "-" is pressed, decrease the draw distance
        if (Keyboard.isKeyDown(Keyboard.KEY_MINUS))
        {
            int drawDistance = SafeOverlay.instance.DecreaseDrawDistance();
            ZyinHUDUtil.DisplayNotification(Localization.get("safeoverlay.distance") + " " + drawDistance);
            
            SafeOverlay.instance.RecalculateUnsafePositions();
            return;
        }

        //if "0" is pressed, set to the default draw distance
        if (Keyboard.isKeyDown(Keyboard.KEY_0))
        {
            int drawDistance = SafeOverlay.instance.SetDrawDistance(SafeOverlay.defaultDrawDistance);
            SafeOverlay.instance.SetSeeUnsafePositionsThroughWalls(false);
            ZyinHUDUtil.DisplayNotification(Localization.get("safeoverlay.distance") + " " + Localization.get("safeoverlay.distance.default") + " (" + drawDistance + ")");
            
            SafeOverlay.instance.RecalculateUnsafePositions();
        	ZyinHUDSound.PlayButtonPress();
            return;
        }
        
        //if Control is pressed, enable see through mode
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
                || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
        {
            boolean seeThroughWalls = SafeOverlay.instance.ToggleSeeUnsafePositionsThroughWalls();

            if (seeThroughWalls)
            {
                ZyinHUDUtil.DisplayNotification(Localization.get("safeoverlay.seethroughwallsenabled"));
            }
            else
            {
                ZyinHUDUtil.DisplayNotification(Localization.get("safeoverlay.seethroughwallsdisabled"));
            }

            SafeOverlay.instance.RecalculateUnsafePositions();
        	ZyinHUDSound.PlayButtonPress();
            return;
        }
        
        //if nothing is pressed, do the default behavior
        
        SafeOverlay.Modes.ToggleMode();
    	ZyinHUDSound.PlayButtonPress();

        if (SafeOverlay.Mode == Modes.ON)
        {
            //if we enable the mod, recalculate unsafe areas immediately
            SafeOverlay.instance.RecalculateUnsafePositions();
        }
	}
	
}