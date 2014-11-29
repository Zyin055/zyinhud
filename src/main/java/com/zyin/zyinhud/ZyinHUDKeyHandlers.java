package com.zyin.zyinhud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

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
import com.zyin.zyinhud.mods.Miscellaneous;
import com.zyin.zyinhud.mods.TorchAid;

public class ZyinHUDKeyHandlers
{
	private final static Minecraft mc = Minecraft.getMinecraft();
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
		else if(Keyboard.getEventKey() == ZyinHUDKeyHandlers.KEY_BINDINGS[11].getKeyCode() && !Keyboard.getEventKeyState())	//on key released
			ItemSelectorKeyHandler.Released(event);
		
	}

    @SubscribeEvent
    public void MouseEvent(MouseEvent event)
    {
    	//event.buttonstate = true if pressed, false if released
    	//event.button = -1 = mouse moved
    	//event.button =  0 = Left click
    	//event.button =  1 = Right click
    	//event.button =  2 = Middle click
    	//event.dwheel =    0 = mouse moved
    	//event.dwheel =  120 = mouse wheel up
    	//event.dwheel = -120 = mouse wheel down
    	
    	if(event.dx != 0 || event.dy != 0)	//mouse movement event
    		return;
    	
    	//Mouse wheel scroll
        if(event.dwheel != 0)
        {
        	if(KEY_BINDINGS[11].getIsKeyPressed())
        		ItemSelectorKeyHandler.OnMouseWheelScroll(event);
        }

        //Mouse side buttons
        if(event.button == 3 || event.button == 4)
        {
	        if(event.buttonstate)
	        {
	            ItemSelectorKeyHandler.OnMouseSideButton(event);
	        }
	    }

        //Middle click
        if(event.button == 2)
        {
        	if(event.buttonstate)
        	{
            	Miscellaneous.OnMiddleClick();
        	}
        }
    }
    

	
    @SubscribeEvent
    public void ClientTickEvent(ClientTickEvent event)
    {
    	//This tick handler is to overcome the GuiScreen + KeyInputEvent limitation
    	//for Coordinates and QuickDeposit
    	
		if (Keyboard.getEventKey() == KEY_BINDINGS[1].getKeyCode())
	    	CoordinatesKeyHandler.ClientTickEvent(event);
		else if(Keyboard.getEventKey() == KEY_BINDINGS[7].getKeyCode())
			QuickDepositKeyHandler.ClientTickEvent(event);
		
		//since this method is in the ClientTickEvent, it'll overcome the GuiScreen limitation of not handling mouse clicks
		FireUseBlockEvents();
    }


    private static boolean useBlockButtonPreviouslyDown = false;
    
    private static void FireUseBlockEvents()
    {
    	//.keyBindUseItem		isButtonDown()
    	//keyboard key = postive
    	//forward click = -96	4
    	//backward click = -97	3
    	//middle click = -98	2
    	//right click = -99		1
    	//left click = -100		0
    	
    	boolean useBlockButtonDown;
    	
    	if(mc.gameSettings.keyBindUseItem.getKeyCode() < 0)	//the Use Block hotkey is bound to the mouse
    	{
            useBlockButtonDown = Mouse.isButtonDown(100 + mc.gameSettings.keyBindUseItem.getKeyCode());
    	}
    	else	//the Use Block hotkey is bound to the keyboard
    	{
            useBlockButtonDown = Keyboard.isKeyDown(mc.gameSettings.keyBindUseItem.getKeyCode());
    	}
    	
    	if(useBlockButtonDown == true & useBlockButtonPreviouslyDown == false)
    		OnUseBlockPressed();
    	else if(useBlockButtonDown == false & useBlockButtonPreviouslyDown == true)
    		OnUseBlockReleased();
    	
    	useBlockButtonPreviouslyDown = useBlockButtonDown;
    }
    private static void OnUseBlockPressed()
    {
    	TorchAid.instance.Pressed();
    }
    private static void OnUseBlockReleased()
    {
    	TorchAid.instance.Released();
    }
    
}