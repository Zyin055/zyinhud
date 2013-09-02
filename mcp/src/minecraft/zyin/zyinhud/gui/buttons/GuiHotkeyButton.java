package zyin.zyinhud.gui.buttons;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.KeyBinding;
import zyin.zyinhud.ZyinHUD;
import zyin.zyinhud.util.FontCodes;
import zyin.zyinhud.util.Localization;

/**
 * Abstract class that is used for other mods to help implement their hotkey buttons.
 */
public abstract class GuiHotkeyButton extends GuiButton
{
	protected static Minecraft mc = Minecraft.getMinecraft();
    private boolean waitingForHotkeyInput;
	private String hotkey;
	
	public GuiHotkeyButton(int id, int x, int y, int width, int height, String hotkey)
	{
		super(id, x, y, width, height, Localization.get("gui.options.hotkey") + hotkey);
		waitingForHotkeyInput = false;
		this.hotkey = hotkey;
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
    		displayString = Localization.get("gui.options.hotkey") + FontCodes.WHITE + "> " + FontCodes.YELLOW + "??? " + FontCodes.WHITE + "<";
    	else
    		displayString = Localization.get("gui.options.hotkey") + hotkey;
		
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
        	if(keyBindings[i].keyDescription.equals(hotkeyDescription))
        	{
        		return keyBindings[i];
        	}
        }
        return null;
	}
	
	/**
	 * Sets the mod's hotkey to the given parameter, and also updates Minecraft's keybinding.
	 * @param newHotkey e.x. 37 (K), 1 (Esc), 55 (*)
	 */
	public void ApplyHotkey(int newHotkey)
	{
		waitingForHotkeyInput = false;
		hotkey = Keyboard.getKeyName(newHotkey);
		
		SetHotkey(hotkey);
		UpdateDisplayString();
		
		//update key binding in Minecraft
        KeyBinding keyBinding = FindKeyBinding(GetHotkeyDescription());
        if(keyBinding != null)
        {
        	keyBinding.keyCode = newHotkey;
        	KeyBinding.resetKeyBindingArrayAndHash();
        }
	}
	
	/**
	 * Sets the mods hotkey
	 * @param hotkey the new hotkey to use
	 */
	protected abstract void SetHotkey(String hotkey);
	
	/**
	 * Gets the description for the mods hotkey
	 * @return
	 */
	protected abstract String GetHotkeyDescription();
	
}
