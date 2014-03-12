package com.zyin.zyinhud;

import com.zyin.zyinhud.keyhandlers.*;
import com.zyin.zyinhud.mods.ItemSelector;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.MouseEvent;

public class ZyinHUDKeyHandlers
{
    public static final ZyinHUDKeyHandlers instance = new ZyinHUDKeyHandlers();
	
	private KeyBinding key_animalInfo = new KeyBinding(AnimalInfoKeyHandler.HotkeyDescription, AnimalInfoKeyHandler.Hotkey, ZyinHUD.MODNAME);
    private KeyBinding key_coordinates = new KeyBinding(CoordinatesKeyHandler.HotkeyDescription, CoordinatesKeyHandler.Hotkey, ZyinHUD.MODNAME);
    private KeyBinding key_distanceMeasurer = new KeyBinding(DistanceMeasurerKeyHandler.HotkeyDescription, DistanceMeasurerKeyHandler.Hotkey, ZyinHUD.MODNAME);
    private KeyBinding key_eatingAid = new KeyBinding(EatingAidKeyHandler.HotkeyDescription, EatingAidKeyHandler.Hotkey, ZyinHUD.MODNAME);
    private KeyBinding key_enderPearlAid = new KeyBinding(EnderPearlAidKeyHandler.HotkeyDescription, EnderPearlAidKeyHandler.Hotkey, ZyinHUD.MODNAME);
    private KeyBinding key_playerLocator = new KeyBinding(PlayerLocatorKeyHandler.HotkeyDescription, PlayerLocatorKeyHandler.Hotkey, ZyinHUD.MODNAME);
    private KeyBinding key_potionAid = new KeyBinding(PotionAidKeyHandler.HotkeyDescription, PotionAidKeyHandler.Hotkey, ZyinHUD.MODNAME);
    private KeyBinding key_quickDeposit = new KeyBinding(QuickDepositKeyHandler.HotkeyDescription, QuickDepositKeyHandler.Hotkey, ZyinHUD.MODNAME);
    private KeyBinding key_safeOverlay = new KeyBinding(SafeOverlayKeyHandler.HotkeyDescription, SafeOverlayKeyHandler.Hotkey, ZyinHUD.MODNAME);
    private KeyBinding key_weaponSwapper = new KeyBinding(WeaponSwapperKeyHandler.HotkeyDescription, WeaponSwapperKeyHandler.Hotkey, ZyinHUD.MODNAME);
    private KeyBinding key_itemSelector = new KeyBinding(ItemSelectorKeyHandler.HotkeyDescription, ItemSelectorKeyHandler.Hotkey, ZyinHUD.MODNAME);
    private KeyBinding key_zyinHUDOptions = new KeyBinding(ZyinHUDOptionsKeyHandler.HotkeyDescription, ZyinHUDOptionsKeyHandler.Hotkey, ZyinHUD.MODNAME);
    
	public ZyinHUDKeyHandlers()
	{
		ClientRegistry.registerKeyBinding(key_animalInfo);
		ClientRegistry.registerKeyBinding(key_coordinates);
		ClientRegistry.registerKeyBinding(key_distanceMeasurer);
		ClientRegistry.registerKeyBinding(key_eatingAid);
		ClientRegistry.registerKeyBinding(key_enderPearlAid);
		ClientRegistry.registerKeyBinding(key_playerLocator);
		ClientRegistry.registerKeyBinding(key_potionAid);
		ClientRegistry.registerKeyBinding(key_quickDeposit);
		ClientRegistry.registerKeyBinding(key_safeOverlay);
		ClientRegistry.registerKeyBinding(key_weaponSwapper);
		ClientRegistry.registerKeyBinding(key_itemSelector);
		ClientRegistry.registerKeyBinding(key_zyinHUDOptions);
	}

	@SubscribeEvent
	public void KeyInputEvent(KeyInputEvent event) 
	{
		//KeyInputEvent will not fire when looking at a GuiScreen - 1.7.2
		
		//if 2 KeyBindings have the same hotkey, only 1 will be flagged as "pressed" in getIsKeyPressed(),
		//which one ends up getting pressed in that scenario is undetermined
		
		if(key_animalInfo.getIsKeyPressed())
			AnimalInfoKeyHandler.Pressed(event);
		//else if(key_coordinates.getIsKeyPressed())
			//CoordinatesKeyHandler.Pressed(event);		//THIS WILL NOT FIRE ON A GuiScreen
		else if(key_distanceMeasurer.getIsKeyPressed())
			DistanceMeasurerKeyHandler.Pressed(event);
		else if(key_eatingAid.getIsKeyPressed())
			EatingAidKeyHandler.Pressed(event);
		else if(key_enderPearlAid.getIsKeyPressed())
			EnderPearlAidKeyHandler.Pressed(event);
		else if(key_playerLocator.getIsKeyPressed())
			PlayerLocatorKeyHandler.Pressed(event);
		else if(key_potionAid.getIsKeyPressed())
			PotionAidKeyHandler.Pressed(event);
		//else if(key_quickDeposit.getIsKeyPressed())
			//QuickDepositKeyHandler.Pressed(event);	//THIS WILL NOT FIRE ON A GuiScreen
		else if(key_safeOverlay.getIsKeyPressed())
			SafeOverlayKeyHandler.Pressed(event);
		else if(key_weaponSwapper.getIsKeyPressed())
			WeaponSwapperKeyHandler.Pressed(event);
		else if(key_zyinHUDOptions.getIsKeyPressed())
			ZyinHUDOptionsKeyHandler.Pressed(event);
	}

    @SubscribeEvent
    public void MouseEvent(MouseEvent event)
    {
        if (event.dwheel != 0)
            ItemSelectorKeyHandler.MouseWheel( event, key_itemSelector.getIsKeyPressed() );
    }
	
    @SubscribeEvent
    public void ClientTickEvent(ClientTickEvent event)
    {
    	//This to tick handler is to overcome the GuiScreen + KeyInputEvent limitation
    	
    	QuickDepositKeyHandler.QuickDepositTickEvent(event);
    	CoordinatesKeyHandler.CoordinatesTickEvent(event);
        ItemSelector.CheckModifierPressed( key_itemSelector.getIsKeyPressed() );
    }
}