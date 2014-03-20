package com.zyin.zyinhud.keyhandlers;

import com.zyin.zyinhud.mods.EatingAid;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class EatingAidKeyHandler implements ZyinHUDKeyHandlerBase
{
    public static final String HotkeyDescription = "key.zyinhud.eatingaid";
    
	public static void Pressed(KeyInputEvent event) 
	{
		if (mc.currentScreen != null)
        {
            return;    //don't activate if the user is looking at a GUI
        }

		if (EatingAid.Enabled)
            EatingAid.instance.Eat();
	}
}