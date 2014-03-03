package com.zyin.zyinhud.keyhandlers;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.settings.KeyBinding;

import com.zyin.zyinhud.mods.Coordinates;
import com.zyin.zyinhud.mods.WeaponSwapper;
import com.zyin.zyinhud.util.ZyinHUDUtil;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

public class CoordinatesKeyHandler
{
    private static Minecraft mc = Minecraft.getMinecraft();
    
    public static String DefaultHotkeyString = "F1";	//if chat gui is open
    public static int DefaultHotkey = Keyboard.getKeyIndex(DefaultHotkeyString);
    public static int Hotkey = Keyboard.KEY_NONE;	//this is updated when the config file is loaded
    public static final String HotkeyDescription = "key.zyinhud.coordinates";
    
	public static void Pressed(KeyInputEvent event) 
	{
		Coordinates.PasteCoordinatesIntoChat();
	}
	

    
    private static boolean keyDown = false;
    
	public static void CoordinatesTickEvent(ClientTickEvent event)
    {
		if(mc.currentScreen != null && mc.currentScreen instanceof GuiChat)
    	{
    		if(Keyboard.getEventKey() == CoordinatesKeyHandler.Hotkey)
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