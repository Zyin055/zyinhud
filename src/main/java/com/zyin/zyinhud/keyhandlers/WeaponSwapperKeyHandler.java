package com.zyin.zyinhud.keyhandlers;

import net.minecraft.client.Minecraft;

import com.zyin.zyinhud.mods.WeaponSwapper;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class WeaponSwapperKeyHandler
{
    public static final String HotkeyDescription = "key.zyinhud.weaponswapper";
    
    private static Minecraft mc = Minecraft.getMinecraft();
    
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