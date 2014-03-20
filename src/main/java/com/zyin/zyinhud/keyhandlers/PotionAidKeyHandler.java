package com.zyin.zyinhud.keyhandlers;

import com.zyin.zyinhud.mods.PotionAid;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class PotionAidKeyHandler implements ZyinHUDKeyHandlerBase
{
    public static final String HotkeyDescription = "key.zyinhud.potionaid";
    
	public static void Pressed(KeyInputEvent event) 
	{
		if (mc.currentScreen != null)
        {
            return;    //don't activate if the user is looking at a GUI
        }

		if (PotionAid.Enabled)
            PotionAid.instance.Drink();
	}
}