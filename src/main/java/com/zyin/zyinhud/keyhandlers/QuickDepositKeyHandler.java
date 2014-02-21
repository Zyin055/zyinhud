package com.zyin.zyinhud.keyhandlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.mods.QuickDeposit;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

public class QuickDepositKeyHandler
{
    private static Minecraft mc = Minecraft.getMinecraft();
    
    public static String DefaultHotkeyString = "X";
    public static int DefaultHotkey = Keyboard.getKeyIndex(DefaultHotkeyString);
    public static int Hotkey = Keyboard.KEY_NONE;	//this is updated when the config file is loaded
    public static final String HotkeyDescription = "key.zyinhud.quickdeposit";
    
	public static void Pressed(KeyInputEvent event) 
	{
		if (!(mc.currentScreen instanceof GuiContainer))
        {
            return;    //don't activate if the user isn't looking at a container gui
        }
        
        if (QuickDeposit.Enabled)
        	QuickDeposit.QuickDepositItemsInChest();
	}
    
    
    
    private static boolean keyDown = false;
    
	public static void QuickDepositTickEvent(ClientTickEvent event)
    {
    	if(mc.currentScreen instanceof GuiChest)
    	{
    		if(Keyboard.getEventKey() == QuickDepositKeyHandler.Hotkey)
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