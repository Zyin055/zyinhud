package com.zyin.zyinhud.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.util.FontCodes;
import com.zyin.zyinhud.util.Localization;

/**
 * A button used to change Minecraft's key bindings
 */
public class GuiHotkeyButton extends GuiButton
{
	protected static Minecraft mc = Minecraft.getMinecraft();
	
    protected boolean waitingForHotkeyInput = false;
    protected String hotkey;	//E.x.: "P"
    protected String hotkeyDescription;	//E.x.: "key.zyinhud.somemod"
	
	/**
	 * 
	 * @param id
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param hotkeyDescription This should be the same string used in the localization file, E.x.: "key.zyinhud.somemod"
	 */
	public GuiHotkeyButton(int id, int x, int y, int width, int height, String hotkeyDescription)
	{
		super(id, x, y, width, height, "");
		this.hotkeyDescription = hotkeyDescription;
		this.hotkey = GetHotkey();
		UpdateDisplayString();
	}
	
	/**
	 * This method should be called whenever this button is clicked.
	 */
	public void Clicked()
	{
		waitingForHotkeyInput = !waitingForHotkeyInput;
		UpdateDisplayString();
	}
	
	/**
	 * Make this button stop accepting hotkey input.
	 */
	public void Cancel()
	{
		waitingForHotkeyInput = false;
		UpdateDisplayString();
	}
	
	protected void UpdateDisplayString()
	{
    	if(waitingForHotkeyInput)
    		displayString = Localization.get("gui.options.hotkey") + FontCodes.WHITE + "> " + FontCodes.YELLOW + GetHotkey() + FontCodes.WHITE + " <";
    	else
    		displayString = Localization.get("gui.options.hotkey") + GetHotkey();
		
	}
	
	public boolean IsWaitingForHotkeyInput()
	{
		return waitingForHotkeyInput;
	}
	
	/**
	 * Finds the KeyBinding object that Minecraft uses based on the hotkey description (it sounds like
	 * bad practice to use the description, but that's how Minecraft does it).
	 * @param hotkeyDescription
	 * @return
	 */
	private KeyBinding FindKeyBinding(String hotkeyDescription)
	{

        KeyBinding keyBinding = null;
        KeyBinding[] keyBindings = mc.gameSettings.keyBindings;
        for(int i = 0; i < keyBindings.length; i++)
        {
        	if(keyBindings[i].getKeyDescription().equals(hotkeyDescription))
        	{
        		return keyBindings[i];
        	}
        }
        return null;
	}
	
	/**
	 * Called when a key is pressed on the GuiZyinHUDOptions screen.
	 * Updates Minecraft's keybinding.
	 * @param newHotkey e.x. 37 (K), 1 (Esc), 55 (*)
	 */
	public void ApplyHotkey(int newHotkey)
	{
		waitingForHotkeyInput = false;
		hotkey = Keyboard.getKeyName(newHotkey);
		
		//SetHotkey(hotkey);
		UpdateDisplayString();
		
		//update key binding in Minecraft
        KeyBinding keyBinding = FindKeyBinding(GetHotkeyDescription());
        if(keyBinding != null)
        {
        	keyBinding.setKeyCode(newHotkey);
        	KeyBinding.resetKeyBindingArrayAndHash();
        }
	}
	
	
	
	/**
	 * Searches Minecraft's key bindings to get the hotkey based on the hotkey description, then caches the result for future use.
	 * @return String representation of the hotkey, e.x. "K", "LMENU"
	 */
	public String GetHotkey()
	{
		if(hotkey == null)
		{
			//get key binding in Minecraft
	        KeyBinding keyBinding = FindKeyBinding(GetHotkeyDescription());
	        if(keyBinding != null)
	        {
	        	SetHotkey(hotkey);
	        	return Keyboard.getKeyName(keyBinding.getKeyCode());
	        }
	        else
	        {
	        	return "?";
	        }
		}
		else
			return hotkey;
	}
	
	
	/**
	 * Sets the mods hotkey
	 * @param hotkey the new hotkey to use
	 */
	protected void SetHotkey(String hotkey)
	{
		this.hotkey = hotkey;
	}
	
	/**
	 * Gets the description for the mods hotkey
	 * @return
	 */
	protected String GetHotkeyDescription()
	{
		return hotkeyDescription;
	}
}
