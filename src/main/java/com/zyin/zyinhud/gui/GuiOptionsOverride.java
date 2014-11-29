package com.zyin.zyinhud.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;

import com.zyin.zyinhud.util.Localization;

/**
 * This GUI extends the default GuiOptions screen (when you click on "Options..." in the pause menu)
 * by adding additional buttons.
 * <p>
 * This is able to replace the default one by utilizing an event in the ZyinHUDRenderer class.
 */
public class GuiOptionsOverride extends GuiOptions
{

    public GuiOptionsOverride(GuiScreen par1GuiScreen, GameSettings par2GameSettings)
    {
		super(par1GuiScreen, par2GameSettings);
	}

	/**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
    	super.initGui();
    	this.buttonList.add(new GuiButton(1337, this.width / 2 + 5, this.height / 6 + 24 - 6, 150, 20, Localization.get("gui.override.options.buttons.options")));
    }
    
    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     * @throws IOException 
     */
    protected void actionPerformed(GuiButton par1GuiButton) throws IOException
    {
    	super.actionPerformed(par1GuiButton);
    	
        if (par1GuiButton.id == 1337)
        {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiZyinHUDOptions(this));
        }
    }
}
