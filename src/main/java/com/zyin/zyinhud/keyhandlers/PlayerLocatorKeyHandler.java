package com.zyin.zyinhud.keyhandlers;

import net.minecraft.client.Minecraft;

import com.zyin.zyinhud.mods.PlayerLocator;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class PlayerLocatorKeyHandler
{
    public static final String HotkeyDescription = "key.zyinhud.playerlocator";
    
    private static Minecraft mc = Minecraft.getMinecraft();
    
	public static void Pressed(KeyInputEvent event) 
	{
		if (mc.currentScreen != null)
        {
            return;    //don't activate if the user is looking at a GUI
        }

        if(PlayerLocator.Enabled)
        	PlayerLocator.ToggleMode();
	}
}