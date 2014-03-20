package com.zyin.zyinhud.keyhandlers;

import com.zyin.zyinhud.ZyinHUDSound;
import com.zyin.zyinhud.mods.AnimalInfo;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class AnimalInfoKeyHandler implements ZyinHUDKeyHandlerBase
{
    public static final String HotkeyDescription = "key.zyinhud.animalinfo";
    
	public static void Pressed(KeyInputEvent event) 
	{
		if (mc.currentScreen != null)
        {
            return;    //don't activate if the user is looking at a GUI
        }
        
        if(AnimalInfo.Enabled)
        {
        	AnimalInfo.Modes.ToggleMode();
        	ZyinHUDSound.PlayButtonPress();
        }
	}
}