package com.zyin.zyinhud.keyhandlers;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.mods.PlayerLocator;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class PlayerLocatorKeyHandler
{
    private static Minecraft mc = Minecraft.getMinecraft();
    
    public static String DefaultHotkeyString = "P";
    public static int DefaultHotkey = Keyboard.getKeyIndex(DefaultHotkeyString);
    public static int Hotkey = Keyboard.KEY_NONE;	//this is updated when the config file is loaded
    public static final String HotkeyDescription = "key.zyinhud.playerlocator";
    
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