package com.zyin.zyinhud.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

/**
 * A normal GuiButton but with label text to the left of the usual button text.
 */
public class GuiLabeledButton extends GuiButton
{
	public String buttonLabel = null;
	
	public GuiLabeledButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, String buttonLabel)
	{
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.buttonLabel = buttonLabel;
	}
	
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		super.drawButton(mc, mouseX, mouseY);
		
		if(buttonLabel != null)
			mc.fontRendererObj.func_175063_a(buttonLabel, this.xPosition + 3, this.yPosition + (height-mc.fontRendererObj.FONT_HEIGHT)/2 + 1, 0x55ffffff);	//func_175063_a() is drawStringWithShadow()
	}
}
