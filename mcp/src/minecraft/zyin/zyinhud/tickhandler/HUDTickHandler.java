package zyin.zyinhud.tickhandler;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import zyin.zyinhud.helper.HUDEntityTrackerHelper;
import zyin.zyinhud.mods.AnimalInfo;
import zyin.zyinhud.mods.DurabilityInfo;
import zyin.zyinhud.mods.InfoLine;
import zyin.zyinhud.mods.PotionTimers;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class HUDTickHandler implements ITickHandler
{
    public static int renderTickCount = 0;
    private static Minecraft mc = Minecraft.getMinecraft();

    public HUDTickHandler()
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
        return "HUD Tick Handler";
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        if (type.equals(EnumSet.of(TickType.RENDER)))
        {
            onRenderTick();
        }
    }
    
    /**
     * Render any things that need to be rendered onto the user's HUD (on the screen, NOT in the game
     * world - that is done in the RenderWorldLastEvent of RenderTickHandler.java)
     */
    protected void onRenderTick()
    {
        InfoLine.RenderOntoHUD();
        DurabilityInfo.RenderOntoHUD();
        PotionTimers.RenderOntoHUD();
        AnimalInfo.RenderOntoDebugMenu();
        HUDEntityTrackerHelper.RenderEntityInfo();	//calls other mods that need to render things on the HUD near entities
        renderTickCount++;
    }
}