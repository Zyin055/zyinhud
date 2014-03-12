package com.zyin.zyinhud.keyhandlers;

import com.zyin.zyinhud.mods.ItemSelector;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MouseEvent;
import org.lwjgl.input.Keyboard;

public class ItemSelectorKeyHandler
{
    public static final String HotkeyDescription = "key.zyinhud.itemselector";

    public static String DefaultHotkeyString = "LMENU";
    public static int    DefaultHotkey       = Keyboard.KEY_LMENU;
    public static int    Hotkey              = Keyboard.KEY_NONE;	//this is updated when the config file is loaded

    static Minecraft mc = Minecraft.getMinecraft();

    public static void MouseWheel(MouseEvent event, boolean modifierPressed)
    {
        if (mc.currentScreen != null || !ItemSelector.Enabled)
            return;

        if (!modifierPressed)
            return;

        ItemSelector.Scroll(event.dwheel > 0 ? ItemSelector.WHEEL_UP : ItemSelector.WHEEL_DOWN);
        event.setCanceled(true);
    }
}