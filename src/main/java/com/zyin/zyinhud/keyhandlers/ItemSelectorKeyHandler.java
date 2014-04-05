package com.zyin.zyinhud.keyhandlers;

import com.zyin.zyinhud.ZyinHUDKeyHandlers;
import com.zyin.zyinhud.mods.ItemSelector;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.client.event.MouseEvent;
import org.lwjgl.input.Keyboard;

public class ItemSelectorKeyHandler implements ZyinHUDKeyHandlerBase
{
    public static final String HotkeyDescription = "key.zyinhud.itemselector";
    
    //public static void Pressed(KeyInputEvent event)
    //{
    //	
    //}

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

        int direction = event.button == 3
            ? ItemSelector.WHEEL_UP
            : ItemSelector.WHEEL_DOWN;

        ItemSelector.SideButton(direction);
        event.setCanceled(true);
    }
    
    private static boolean keyDown = false;
    
	public static void ClientTickEvent(ClientTickEvent event)
    {
		if(mc.currentScreen == null && mc.inGameHasFocus)
    	{
			if(Keyboard.getEventKey() == ZyinHUDKeyHandlers.KEY_BINDINGS[11].getKeyCode())
    		{
    			if(Keyboard.getEventKeyState())
    			{
    				//if(keyDown == false)
    					//OnKeyDown();
    	            keyDown = true;
    	        }
    	        else
    	        {
    				if(keyDown == true)
    					OnKeyUp();
    	            keyDown = false;
    	        }
    		}
    	}
    }

	//private static void OnKeyDown()
	//{
    //    Pressed(null);
	//}
	
	private static void OnKeyUp()
	{
        ItemSelector.OnHotkeyReleased();
	}
}