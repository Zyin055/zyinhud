package com.zyin.zyinhud.keyhandlers;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.gui.GuiZyinHUDOptions;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class ZyinHUDOptionsKeyHandler
{
    
    public static String DefaultHotkeyString = "Z";	//Ctrl + Alt + Z
    public static int DefaultHotkey = Keyboard.getKeyIndex(DefaultHotkeyString);
    public static int Hotkey = Keyboard.KEY_NONE;	//this is updated when the config file is loaded
    public static final String HotkeyDescription = "key.zyinhud.zyinhudoptions";

    private static Minecraft mc = Minecraft.getMinecraft();
    
	public static void Pressed(KeyInputEvent event) 
	{
		if (mc.currentScreen != null)
        {
            return;    //don't activate if the user is looking at a GUI
        }
        

        //if "Ctrl" and "Alt" is pressed
        if ((Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) &&
            (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU)) )
        {
            //display the GUI
            mc.displayGuiScreen(new GuiZyinHUDOptions(null));
        }
	}
}