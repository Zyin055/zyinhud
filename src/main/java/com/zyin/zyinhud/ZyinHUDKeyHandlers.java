package com.zyin.zyinhud;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.MouseEvent;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.keyhandlers.AnimalInfoKeyHandler;
import com.zyin.zyinhud.keyhandlers.CoordinatesKeyHandler;
import com.zyin.zyinhud.keyhandlers.DistanceMeasurerKeyHandler;
import com.zyin.zyinhud.keyhandlers.EatingAidKeyHandler;
import com.zyin.zyinhud.keyhandlers.EnderPearlAidKeyHandler;
import com.zyin.zyinhud.keyhandlers.ItemSelectorKeyHandler;
import com.zyin.zyinhud.keyhandlers.PlayerLocatorKeyHandler;
import com.zyin.zyinhud.keyhandlers.PotionAidKeyHandler;
import com.zyin.zyinhud.keyhandlers.QuickDepositKeyHandler;
import com.zyin.zyinhud.keyhandlers.SafeOverlayKeyHandler;
import com.zyin.zyinhud.keyhandlers.WeaponSwapperKeyHandler;
import com.zyin.zyinhud.keyhandlers.ZyinHUDOptionsKeyHandler;
import com.zyin.zyinhud.mods.ItemSelector;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ZyinHUDKeyHandlers
{
    /**
     * An array of all of Zyin's HUD custom key bindings. Don't reorder them since they are referenced by their position in the array.<br><ul>
     * <li>[0] Animal Info
     * <li>[1] Coordinates
     * <li>[2] Distance Measurer
     * <li>[3] Eating Aid
     * <li>[4] Ender Pearl Aid
     * <li>[5] Player Locator
     * <li>[6] Potion Aid
     * <li>[7] Quick Deposit
     * <li>[8] Safe Overlay
     * <li>[9] Weapon Swapper
     * <li>[10] Zyin's HUD Options
     * <li>[11] Item Selector
     */
    public static final KeyBinding[] KEY_BINDINGS = 
	{
		new KeyBinding(AnimalInfoKeyHandler.HotkeyDescription, 		Keyboard.getKeyIndex("O"), 	   ZyinHUD.MODNAME),	//[0]
	    new KeyBinding(CoordinatesKeyHandler.HotkeyDescription, 	Keyboard.getKeyIndex("F1"),    ZyinHUD.MODNAME),	//[1]
	    new KeyBinding(DistanceMeasurerKeyHandler.HotkeyDescription,Keyboard.getKeyIndex("K"), 	   ZyinHUD.MODNAME),	//[2]
	    new KeyBinding(EatingAidKeyHandler.HotkeyDescription, 		Keyboard.getKeyIndex("G"), 	   ZyinHUD.MODNAME),	//[3]
	    new KeyBinding(EnderPearlAidKeyHandler.HotkeyDescription, 	Keyboard.getKeyIndex("C"), 	   ZyinHUD.MODNAME),	//[4]
	    new KeyBinding(PlayerLocatorKeyHandler.HotkeyDescription, 	Keyboard.getKeyIndex("P"), 	   ZyinHUD.MODNAME),	//[5]
	    new KeyBinding(PotionAidKeyHandler.HotkeyDescription, 		Keyboard.getKeyIndex("V"), 	   ZyinHUD.MODNAME),	//[6]
	    new KeyBinding(QuickDepositKeyHandler.HotkeyDescription, 	Keyboard.getKeyIndex("X"), 	   ZyinHUD.MODNAME),	//[7]
	    new KeyBinding(SafeOverlayKeyHandler.HotkeyDescription, 	Keyboard.getKeyIndex("L"), 	   ZyinHUD.MODNAME),	//[8]
	    new KeyBinding(WeaponSwapperKeyHandler.HotkeyDescription, 	Keyboard.getKeyIndex("F"), 	   ZyinHUD.MODNAME),	//[9]
	    new KeyBinding(ZyinHUDOptionsKeyHandler.HotkeyDescription, 	Keyboard.getKeyIndex("Z"), 	   ZyinHUD.MODNAME),	//[10]
	    new KeyBinding(ItemSelectorKeyHandler.HotkeyDescription, 	Keyboard.getKeyIndex("LMENU"), ZyinHUD.MODNAME),	//[11]
	};
    
    public static final ZyinHUDKeyHandlers instance = new ZyinHUDKeyHandlers();
	
	public ZyinHUDKeyHandlers()
	{
		for(KeyBinding keyBinding : KEY_BINDINGS)
			ClientRegistry.registerKeyBinding(keyBinding);
	}

	@SubscribeEvent
	public void KeyInputEvent(KeyInputEvent event) 
	{
		//KeyInputEvent will not fire when looking at a GuiScreen - 1.7.2
		
		//if 2 KeyBindings have the same hotkey, only 1 will be flagged as "pressed" in getIsKeyPressed(),
		//which one ends up getting pressed in that scenario is undetermined

		if(KEY_BINDINGS[0].getIsKeyPressed())
			AnimalInfoKeyHandler.Pressed(event);
		//else if(keyBindings[1].getIsKeyPressed())
			//CoordinatesKeyHandler.Pressed(event);		//THIS WILL NOT FIRE ON A GuiScreen
		else if(KEY_BINDINGS[2].getIsKeyPressed())
			DistanceMeasurerKeyHandler.Pressed(event);
		else if(KEY_BINDINGS[3].getIsKeyPressed())
			EatingAidKeyHandler.Pressed(event);
		else if(KEY_BINDINGS[4].getIsKeyPressed())
			EnderPearlAidKeyHandler.Pressed(event);
		else if(KEY_BINDINGS[5].getIsKeyPressed())
			PlayerLocatorKeyHandler.Pressed(event);
		else if(KEY_BINDINGS[6].getIsKeyPressed())
			PotionAidKeyHandler.Pressed(event);
		//else if(keyBindings[7].getIsKeyPressed())
			//QuickDepositKeyHandler.Pressed(event);	//THIS WILL NOT FIRE ON A GuiScreen
		else if(KEY_BINDINGS[8].getIsKeyPressed())
			SafeOverlayKeyHandler.Pressed(event);
		else if(KEY_BINDINGS[9].getIsKeyPressed())
			WeaponSwapperKeyHandler.Pressed(event);
		else if(KEY_BINDINGS[10].getIsKeyPressed())
			ZyinHUDOptionsKeyHandler.Pressed(event);
		//else if(KEY_BINDINGS[11].getIsKeyPressed())
			//ItemSelectorKeyHandler.Pressed(event);	//needs an OnKeyUp event, getIsKeyPressed() only is for OnKeyDown
	}

    @SubscribeEvent
    public void MouseEvent(MouseEvent event)
    {
        if (KEY_BINDINGS[11].getIsKeyPressed() && event.dwheel != 0)
            ItemSelectorKeyHandler.MouseWheel(event);
    }
	
    @SubscribeEvent
    public void ClientTickEvent(ClientTickEvent event)
    {
    	//This to tick handler is to overcome the GuiScreen + KeyInputEvent limitation
    	//for Coordinates and QuickDeposit.
    	
    	//ItemSelector needs it for an OnKeyUp event
    	
		if (Keyboard.getEventKey() == KEY_BINDINGS[1].getKeyCode())
	    	CoordinatesKeyHandler.ClientTickEvent(event);
		else if(Keyboard.getEventKey() == KEY_BINDINGS[7].getKeyCode())
			QuickDepositKeyHandler.ClientTickEvent(event);
		else if(Keyboard.getEventKey() == KEY_BINDINGS[11].getKeyCode())
			ItemSelectorKeyHandler.ClientTickEvent(event);
    }
}