package zyin.zyinhud.tickhandler;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import zyin.zyinhud.gui.GuiOptionsOverride;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class GUITickHandler implements ITickHandler
{
    private static Minecraft mc = Minecraft.getMinecraft();

    public GUITickHandler()
    {
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.RENDER, TickType.CLIENT);
    }

    @Override
    public String getLabel()
    {
        return "GUI Tick Handler";
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        if (type.equals(EnumSet.of(TickType.CLIENT)))
        {
            GuiScreen guiScreen = mc.currentScreen;

            if (guiScreen != null)
            {
                onTickInGUI(guiScreen);
            }
        }
    }
    
    
    /**
     * We override any default GUIs with our custom GUI classes here
     * @param guiScreen
     */
    protected void onTickInGUI(GuiScreen guiScreen)
    {
    	if (guiScreen instanceof GuiOptionsOverride)
        {
    		//don't do anything if we're looking at the new override screen
        }
    	else if (guiScreen instanceof GuiOptions)
        {
    		mc.displayGuiScreen(new GuiOptionsOverride(new GuiIngameMenu(), mc.gameSettings));
        }
    }
}