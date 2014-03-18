package com.zyin.zyinhud.keyhandlers;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.ZyinHUDSound;
import com.zyin.zyinhud.mods.InfoLine;
import com.zyin.zyinhud.mods.SafeOverlay;
import com.zyin.zyinhud.util.Localization;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class SafeOverlayKeyHandler
{
    public static final String HotkeyDescription = "key.zyinhud.safeoverlay";

    private static Minecraft mc = Minecraft.getMinecraft();

    /**
     * Since we enable this key handler to repeat when the user holds the key down, we
     * want to be able to execute some code only on the initial key press.
     */
    //private static boolean isFirstKeypress = true;	//TODO 1.7 need an OnKeyUp event for this...
    
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
            int drawDistance = SafeOverlay.instance.increaseDrawDistance();

            if (drawDistance == SafeOverlay.maxDrawDistance)
            {
                InfoLine.DisplayNotification(Localization.get("safeoverlay.distance") + " " + drawDistance + " ("+Localization.get("safeoverlay.distance.max")+")");
            }
            else
            {
                InfoLine.DisplayNotification(Localization.get("safeoverlay.distance") + " " + drawDistance);
            }

            SafeOverlay.instance.RecalculateUnsafePositions();
            return;
        }

        //if "-" is pressed, decrease the draw distance
        if (Keyboard.isKeyDown(Keyboard.KEY_MINUS))
        {
            int drawDistance = SafeOverlay.instance.decreaseDrawDistance();
            InfoLine.DisplayNotification(Localization.get("safeoverlay.distance") + " " + drawDistance);
            
            SafeOverlay.instance.RecalculateUnsafePositions();
            return;
        }

        //if "0" is pressed, set to the default draw distance
        if (Keyboard.isKeyDown(Keyboard.KEY_0))
        {
            int drawDistance = SafeOverlay.instance.setDrawDistance(SafeOverlay.defaultDrawDistance);
            SafeOverlay.instance.setSeeUnsafePositionsThroughWalls(false);
            InfoLine.DisplayNotification(Localization.get("safeoverlay.distance") + " " + Localization.get("safeoverlay.distance.default") + " (" + drawDistance + ")");
            
            SafeOverlay.instance.RecalculateUnsafePositions();
        	ZyinHUDSound.PlayButtonPress();
            return;
        }
        
        //if Control is pressed, enable see through mode
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
                || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
        {
            boolean seeThroughWalls = SafeOverlay.instance.toggleSeeUnsafePositionsThroughWalls();

            if (seeThroughWalls)
            {
                InfoLine.DisplayNotification(Localization.get("safeoverlay.seethroughwallsenabled"));
            }
            else
            {
                InfoLine.DisplayNotification(Localization.get("safeoverlay.seethroughwallsdisabled"));
            }

            SafeOverlay.instance.RecalculateUnsafePositions();
        	ZyinHUDSound.PlayButtonPress();
            return;
        }
        
        //if nothing is pressed, do the default behavior
        
        SafeOverlay.ToggleMode();
    	ZyinHUDSound.PlayButtonPress();

        if (SafeOverlay.Mode == 1)
        {
            //if we enable the mod, recalculate unsafe areas immediately
            SafeOverlay.instance.RecalculateUnsafePositions();
        }
	}
	
}