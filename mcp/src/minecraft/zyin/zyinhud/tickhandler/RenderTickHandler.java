package zyin.zyinhud.tickhandler;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;
import zyin.zyinhud.mods.SafeOverlay;

public class RenderTickHandler
{
    public static RenderTickHandler instance = new RenderTickHandler();

    private RenderTickHandler()
    {
    }

    /**
     * Event fired when the world gets rendered.
     * We render any things that need to be rendered into the game world in this method.
     * @param event
     */
    @ForgeSubscribe
    public void renderWorldLastEvent(RenderWorldLastEvent event)
    {
        //render unsafe positions (cache calculations are done from this render method)
        SafeOverlay.instance.RenderAllUnsafePositionsMultithreaded(event.partialTicks);
    }
}
