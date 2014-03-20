package com.zyin.zyinhud.keyhandlers;

import com.zyin.zyinhud.mods.WeaponSwapper;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class WeaponSwapperKeyHandler implements ZyinHUDKeyHandlerBase
{
    public static final String HotkeyDescription = "key.zyinhud.weaponswapper";
    
	public static void Pressed(KeyInputEvent event) 
	{
		if (mc.currentScreen != null)
        {
            return;    //don't activate if the user is looking at a GUI
        }

        if (WeaponSwapper.Enabled)
            WeaponSwapper.SwapWeapons();
	}
}