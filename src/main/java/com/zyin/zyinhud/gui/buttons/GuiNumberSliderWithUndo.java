package com.zyin.zyinhud.gui.buttons;

import com.zyin.zyinhud.gui.buttons.GuiNumberSlider.Modes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiNumberSliderWithUndo extends GuiNumberSlider
{
	String undoSymbol = GuiUtils.UNDO_CHAR;
	int undoSymbolX;
	int undoSymbolY;
	int undoSymbolWidth = 5;
	int undoSymbolHeight = 7;
	
	float defaultValue;
	
	public GuiNumberSliderWithUndo(int id, int x, int y, int width, int height, String displayString, float minValue, float maxValue, float currentValue, float defaultValue, Modes mode)
	{
		super(id, x, y, width, height, displayString, minValue, maxValue, currentValue, mode);
		this.defaultValue = defaultValue;
	}
	
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		super.drawButton(mc, mouseX, mouseY);

		int undoSymbolColor = 0xffffff;
    	undoSymbolX = xPosition + width - (undoSymbolWidth+1);
    	undoSymbolY =  yPosition + height - (undoSymbolHeight+1);
		
		//if mouseovered the undo symbol
		if(IsUndoMouseovered(mouseX, mouseY))
		{
			undoSymbolColor = 0x55ffff;	//0x55ffff is the same as EnumChatFormatting.AQUA
		}
		
		mc.fontRendererObj.func_175063_a(undoSymbol, undoSymbolX, undoSymbolY, undoSymbolColor);	//func_175063_a() is drawStringWithShadow()
	}
	
	protected boolean IsUndoMouseovered(int mouseX, int mouseY)
	{
		return mouseX > undoSymbolX && mouseX < undoSymbolX + undoSymbolWidth
			&& mouseY > undoSymbolY && mouseY < undoSymbolY + undoSymbolHeight;
	}
	
	protected void UndoButtonClicked()
	{
		sliderValue = (defaultValue - minValue) / (maxValue - minValue);
		UpdateLabel();
	}
	
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
		//this is the mousePressed method for GuiButton, we want to skip the dragging behavior of GuiNumberSlider
		//boolean mousePressed = this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
		
		//boolean mousePressed = ((GuiButton)this).mousePressed(mc, mouseX, mouseY);
		
        if(this.enabled && this.visible && IsUndoMouseovered(mouseX, mouseY))
        {
        	UndoButtonClicked();
        	return true;
        }
        else
        {
        	return super.mousePressed(mc, mouseX, mouseY);
        }
    }
}
