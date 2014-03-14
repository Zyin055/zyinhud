package com.zyin.zyinhud.keyhandlers;

import net.minecraft.client.Minecraft;

import com.zyin.zyinhud.mods.EatingAid;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class EatingAidKeyHandler
{
    public static final String HotkeyDescription = "key.zyinhud.eatingaid";
    
    private static Minecraft mc = Minecraft.getMinecraft();
    
	public static void Pressed(KeyInputEvent event) 
	{
		if (mc.currentScreen != null)
        {
            return;    //don't activate if the user is looking at a GUI
        }

		if (EatingAid.Enabled)
            EatingAid.instance.Eat();
	}
}