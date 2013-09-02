package zyin.zyinhud.mods;

import java.util.Timer;
import java.util.TimerTask;

import zyin.zyinhud.tickhandler.HUDTickHandler;
import zyin.zyinhud.util.FontCodes;
import zyin.zyinhud.util.Localization;

public class Fps
{
	/** Enables/Disables this Mod */
	public static boolean Enabled;

    /**
     * Toggles this Mod on or off
     * @return The state the Mod was changed to
     */
    public static boolean ToggleEnabled()
    {
    	Enabled = !Enabled;
    	return Enabled;
    }
    private static int currentFps = 0;

    private static final Timer timer = new Timer();

    public static final Fps instance = new Fps();

    private Fps()
    {
        timer.scheduleAtFixedRate(new FpsTimerTask(), 0, 1000);    //recalculate once every 1000 ms
    }

    public static String CalculateMessageForInfoLine()
    {
        if (Fps.Enabled)
        {
            return FontCodes.WHITE + currentFps + " " + Localization.get("fps.infoline") + InfoLine.SPACER;
        }
        else
        {
            return "";
        }
    }

    /**
     * Helper TimeTask class which runs once every second to determine the amount of render ticks elapsed.
     */
    class FpsTimerTask extends TimerTask
    {
        private int currentRenderTickCount = 0;
        private int lastRenderTickCount = 0;

        @Override
        public void run()
        {
            currentRenderTickCount = HUDTickHandler.renderTickCount;
            currentFps = currentRenderTickCount - lastRenderTickCount;
            lastRenderTickCount = HUDTickHandler.renderTickCount;
        }
    }
}
