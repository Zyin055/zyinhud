package com.zyin.zyinhud.keyhandlers;

import org.lwjgl.input.Keyboard;

import net.minecraftforge.client.event.MouseEvent;

import com.zyin.zyinhud.ZyinHUDSound;
import com.zyin.zyinhud.gui.GuiZyinHUDOptions;
import com.zyin.zyinhud.mods.ItemSelector;

import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class ItemSelectorKeyHandler implements ZyinHUDKeyHandlerBase
{
    public static final String HotkeyDescription = "key.zyinhud.itemselector";
    
    public static void OnMouseWheelScroll(MouseEvent event)
    {
        if (!mc.inGameHasFocus || !ItemSelector.Enabled)
            return;
        
        ItemSelector.Scroll(event.dwheel > 0 ? ItemSelector.WHEEL_UP : ItemSelector.WHEEL_DOWN);
        event.setCanceled(true);
    }

    public static void OnMouseSideButton(MouseEvent event)
    {
        if (!mc.inGameHasFocus || !ItemSelector.Enabled || !ItemSelector.UseMouseSideButtons)
            return;

        int direction = event.button == 3 ? ItemSelector.WHEEL_UP : ItemSelector.WHEEL_DOWN;

        ItemSelector.SideButton(direction);
        event.setCanceled(true);
    }
    
	public static void Pressed(KeyInputEvent event) 
	{
		if (mc.currentScreen != null)
        {
            return;    //don't activate if the user is looking at a GUI or has a modifier pressed
        }
        
		ItemSelector.OnHotkeyPressed();
	}
	
	public static void Released(KeyInputEvent event)
	{
        ItemSelector.OnHotkeyReleased();
	}
}