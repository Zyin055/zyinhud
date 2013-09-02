package zyin.zyinhud.gui.buttons;

import java.text.DecimalFormat;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiNumberSlider extends GuiButton
{
    /** The value of this slider control. Ranges from 0 to 1. */
    public float sliderValue;
    
    /** The smallest integer value of this slider control. */
    public float minValue;
    
    /** The largest integer value of this slider control. */
    public float maxValue;

    /** Is this slider control being dragged? */
    public boolean dragging;
    
    /** The text displayed before the number */
    public String label;
    
    /** The text displayed before the number */
    public boolean renderValuesAsIntegers;
    
    private static DecimalFormat twoDecimals = new DecimalFormat("#.00");
    
	public GuiNumberSlider(int id, int x, int y, int width, int height, String displayString, float minValue, float maxValue, float currentValue, boolean renderValuesAsIntegers)
	{
		super(id, x, y, width, height, renderValuesAsIntegers == true ? displayString+((int)currentValue) : displayString+twoDecimals.format(currentValue));
		this.label = displayString;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.sliderValue = (currentValue-minValue) / (maxValue-minValue);
        this.renderValuesAsIntegers = renderValuesAsIntegers;
	}

	/**
	 * Gets the decimal value of the slider.
	 * @return
	 */
	public float GetValueAsFloat()
	{
		return (maxValue - minValue)*sliderValue + minValue;
	}
	/**
	 * Gets the integer value of the slider.
	 * @return
	 */
	public int GetValueAsInteger()
	{
		return (int)((maxValue - minValue)*sliderValue + minValue);
	}
	
	/**
	 * Gets the text being displayed on this slider.
	 * @return
	 */
	public String GetLabel()
	{
		if(renderValuesAsIntegers)
			return label + GetValueAsInteger();
		else
			return label + twoDecimals.format(GetValueAsFloat());
	}
	
	/**
	 * Set the text displayed on this slider.
	 */
	protected void SetLabel()
	{
		displayString = GetLabel();
	}

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean par1)
    {
        return 0;
    }
    

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft mc, int x, int y)
    {
        if (drawButton)
        {
            if (dragging)
            {
                sliderValue = (float)(x - (xPosition + 4)) / (float)(width - 8);

                if (sliderValue < 0.0F)
                {
                    sliderValue = 0.0F;
                }

                if (sliderValue > 1.0F)
                {
                    sliderValue = 1.0F;
                }

                SetLabel();
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(xPosition + (int)(sliderValue * (float)(width - 8)), yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(xPosition + (int)(sliderValue * (float)(width - 8)) + 4, yPosition, 196, 66, 4, 20);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft mc, int x, int y)
    {
        if (super.mousePressed(mc, x, y))
        {
            sliderValue = (float)(x - (xPosition + 4)) / (float)(width - 8);

            if (sliderValue < 0.0F)
            {
                sliderValue = 0.0F;
            }

            if (sliderValue > 1.0F)
            {
                sliderValue = 1.0F;
            }

            SetLabel();
            dragging = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int par1, int par2)
    {
        dragging = false;
    }
}
