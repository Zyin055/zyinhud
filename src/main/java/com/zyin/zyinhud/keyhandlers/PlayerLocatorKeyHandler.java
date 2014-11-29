package com.zyin.zyinhud.keyhandlers;

import com.zyin.zyinhud.ZyinHUDSound;
import com.zyin.zyinhud.mods.PlayerLocator;

import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class PlayerLocatorKeyHandler implements ZyinHUDKeyHandlerBase
{
    public static final String HotkeyDescription = "key.zyinhud.playerlocator";
    
	public static void Pressed(KeyInputEvent event) 
	{
		if (mc.currentScreen != null)
        {
            return;    //don't activate if the user is looking at a GUI
        }

        if(PlayerLocator.Enabled)
        {
        	PlayerLocator.Modes.ToggleMode();
        	ZyinHUDSound.PlayButtonPress();
        }
	}
}