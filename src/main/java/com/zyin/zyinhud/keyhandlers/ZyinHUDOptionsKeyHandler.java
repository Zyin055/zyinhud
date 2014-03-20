package com.zyin.zyinhud.keyhandlers;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.ZyinHUDSound;
import com.zyin.zyinhud.gui.GuiZyinHUDOptions;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class ZyinHUDOptionsKeyHandler implements ZyinHUDKeyHandlerBase
{
    public static final String HotkeyDescription = "key.zyinhud.zyinhudoptions";
    
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
        	ZyinHUDSound.PlayButtonPress();
        }
	}
}