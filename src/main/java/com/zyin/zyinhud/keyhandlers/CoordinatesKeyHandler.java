package com.zyin.zyinhud.keyhandlers;

import net.minecraft.client.gui.GuiChat;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.ZyinHUDKeyHandlers;
import com.zyin.zyinhud.mods.Coordinates;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

public class CoordinatesKeyHandler implements ZyinHUDKeyHandlerBase
{
    public static final String HotkeyDescription = "key.zyinhud.coordinates";
    
	public static void Pressed(KeyInputEvent event) 
	{
		Coordinates.PasteCoordinatesIntoChat();
	}
	

    
    private static boolean keyDown = false;
    
	public static void ClientTickEvent(ClientTickEvent event)
    {
		if(mc.currentScreen != null && mc.currentScreen instanceof GuiChat)
    	{
			if(Keyboard.getEventKey() == ZyinHUDKeyHandlers.KEY_BINDINGS[1].getKeyCode())
    		{
    			if(Keyboard.getEventKeyState())
    			{
    				if(keyDown == false)
    					OnKeyDown();
    	            keyDown = true;
    	        }
    	        else
    	        {
    				//if(keyDown == true)
    					//OnKeyUp();
    	            keyDown = false;
    	        }
    		}
    		
    	}
    }

	private static void OnKeyDown()
	{
        Pressed(null);
	}
}