package com.zyin.zyinhud.keyhandlers;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.settings.KeyBinding;

import com.zyin.zyinhud.mods.Coordinates;
import com.zyin.zyinhud.mods.WeaponSwapper;
import com.zyin.zyinhud.util.ZyinHUDUtil;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class CoordinatesKeyHandler// extends KeyHandler
{
    private static Minecraft mc = Minecraft.getMinecraft();
    
    public static String DefaultHotkeyString = "F1";	//if chat gui is open
    public static int DefaultHotkey = Keyboard.getKeyIndex(DefaultHotkeyString);
    public static int Hotkey = Keyboard.KEY_NONE;	//this is updated when the config file is loaded
    public static final String HotkeyDescription = "key.zyinhud.coordinates";
    
	public static void Pressed(KeyInputEvent event) 
	{
		if(mc.currentScreen != null && mc.currentScreen instanceof GuiChat)
    	{
        	String coordinateString = Coordinates.ChatStringFormat;
        	coordinateString = coordinateString.replace("{x}", Integer.toString(Coordinates.GetXCoordinate()));
        	coordinateString = coordinateString.replace("{y}", Integer.toString(Coordinates.GetYCoordinate()));
        	coordinateString = coordinateString.replace("{z}", Integer.toString(Coordinates.GetZCoordinate()));
        	
        	GuiTextField inputField = ZyinHUDUtil.GetFieldByReflection(GuiChat.class, (GuiChat)mc.currentScreen, "inputField","field_146415_a");
        	
        	if(inputField != null)
        	{
        		inputField.writeText(coordinateString);
        	}
    	}
	}
}