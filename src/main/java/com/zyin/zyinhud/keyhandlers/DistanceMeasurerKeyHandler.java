package com.zyin.zyinhud.keyhandlers;

import net.minecraft.client.Minecraft;

import com.zyin.zyinhud.mods.DistanceMeasurer;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class DistanceMeasurerKeyHandler
{
    public static final String HotkeyDescription = "key.zyinhud.distancemeasurer";
    
    private static Minecraft mc = Minecraft.getMinecraft();
    
	public static void Pressed(KeyInputEvent event) 
	{
		if (mc.currentScreen != null)
        {
            return;    //don't activate if the user is looking at a GUI
        }
        
        if(DistanceMeasurer.Enabled)
        	DistanceMeasurer.ToggleMode();
	}
}