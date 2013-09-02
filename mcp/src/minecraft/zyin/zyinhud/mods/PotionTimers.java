package zyin.zyinhud.mods;

import java.util.Collection;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Potion Timers displays the remaining time left on any potion effects the user has.
 */
public class PotionTimers
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
    private static Minecraft mc = Minecraft.getMinecraft();

    private static final int[] blinkingThresholds = {3 * 20, 6 * 20, 16 * 20};	//the time at which blinking starts
    private static final int[] blinkingSpeed = {5, 10, 20};					//how often the blinking occurs
    private static final int[] blinkingDuration = {2, 3, 3};					//how long the blink lasts

    /**
     * Renders the duration any potion effects that the player currently has on the left side of the screen.
     */
    public static void RenderOntoHUD()
    {
        //if the player is in the world
        //and not in a menu
        //and F3 not shown
        if (PotionTimers.Enabled &&
                (mc.inGameHasFocus || mc.currentScreen == null || (mc.currentScreen instanceof GuiChat))
                && !mc.gameSettings.showDebugInfo)
        {
            Collection potionEffects = mc.thePlayer.getActivePotionEffects();	//key:potionId, value:potionEffect
            mc.fontRenderer.setUnicodeFlag(true);
            Iterator it = potionEffects.iterator();
            int i = 0;

            while (it.hasNext())
            {
                PotionEffect potionEffect = (PotionEffect)it.next();
                Boolean isFromBeacon = potionEffect.getIsAmbient();	//Minecraft bug: this is always false

                if (!isFromBeacon)	//ignore effects from Beacons (Minecraft bug: isFromBeacon is always false)
                {
                    String durationString = Potion.getDurationString(potionEffect);
                    Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
                    int colorInt = potion.getLiquidColor();
                    int potionDuration = potionEffect.getDuration();	//goes down by 20 ticks per second

                    //render the potion duration text onto the screen
                    if (potionDuration >= blinkingThresholds[blinkingThresholds.length - 1])	//if the text is not blinking then render it normally
                    {
                        mc.fontRenderer.drawStringWithShadow(durationString, 1, 16 + 10 * i, colorInt);
                    }
                    else //else if the text is blinking, have a chance to not render it based on the blinking variables
                    {
                        for (int j = 0; j < blinkingThresholds.length; j++)
                        {
                            if (potionDuration < blinkingThresholds[j])
                            {
                                if (potionDuration % blinkingSpeed[j] > blinkingDuration[j])
                                {
                                    mc.fontRenderer.drawStringWithShadow(durationString, 1, 16 + 10 * i, colorInt);
                                }

                                break;
                            }
                        }
                    }

                    i++;
                }
            }

            mc.fontRenderer.setUnicodeFlag(false);
        }
    }
}
