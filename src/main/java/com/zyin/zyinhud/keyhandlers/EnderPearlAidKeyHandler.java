package com.zyin.zyinhud.keyhandlers;

import com.zyin.zyinhud.mods.EnderPearlAid;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class EnderPearlAidKeyHandler implements ZyinHUDKeyHandlerBase
{
    public static final String HotkeyDescription = "key.zyinhud.enderpearlaid";
    
	public static void Pressed(KeyInputEvent event) 
	{
		if (mc.currentScreen != null)
        {
            return;    //don't activate if the user is looking at a GUI
        }

		if (EnderPearlAid.Enabled)
            EnderPearlAid.UseEnderPearl();
	   
	}
}