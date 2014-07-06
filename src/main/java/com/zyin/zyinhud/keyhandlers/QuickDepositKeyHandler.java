package com.zyin.zyinhud.keyhandlers;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.ZyinHUDKeyHandlers;
import com.zyin.zyinhud.mods.QuickDeposit;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

public class QuickDepositKeyHandler implements ZyinHUDKeyHandlerBase
{
    public static final String HotkeyDescription = "key.zyinhud.quickdeposit";
    
	public static void Pressed(KeyInputEvent event) 
	{
		if (!(mc.currentScreen instanceof GuiContainer))
        {
            return;    //don't activate if the user isn't looking at a container gui
        }
        
        if (QuickDeposit.Enabled)
        {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            	QuickDeposit.QuickDepositItemsInChest(false);
            else
            	QuickDeposit.QuickDepositItemsInChest(true);
        }
	}
    
    
    
    private static boolean keyDown = false;
    
	public static void ClientTickEvent(ClientTickEvent event)
    {
    	if(mc.currentScreen instanceof GuiContainer)
    	{
    		if(Keyboard.getEventKey() == ZyinHUDKeyHandlers.KEY_BINDINGS[7].getKeyCode())
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