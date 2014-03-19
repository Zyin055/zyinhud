package com.zyin.zyinhud.keyhandlers;

import com.zyin.zyinhud.mods.ItemSelector;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MouseEvent;

public class ItemSelectorKeyHandler
{
    public static final String HotkeyDescription = "key.zyinhud.itemselector";

    static Minecraft mc = Minecraft.getMinecraft();

    public static void MouseWheel(MouseEvent event)
    {
        if (mc.currentScreen != null || !ItemSelector.Enabled)
            return;

        ItemSelector.Scroll(event.dwheel > 0 ? ItemSelector.WHEEL_UP : ItemSelector.WHEEL_DOWN);
        event.setCanceled(true);
    }
}