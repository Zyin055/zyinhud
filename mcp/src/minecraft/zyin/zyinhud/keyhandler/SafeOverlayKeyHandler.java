package zyin.zyinhud.keyhandler;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import zyin.zyinhud.ZyinHUD;
import zyin.zyinhud.mods.InfoLine;
import zyin.zyinhud.mods.SafeOverlay;
import zyin.zyinhud.util.Localization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.Property;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class SafeOverlayKeyHandler extends KeyHandler
{
    private Minecraft mc = Minecraft.getMinecraft();
    private EnumSet tickTypes = EnumSet.of(TickType.CLIENT);
    
    /**
     * Since we enable this key handler to repeat when the user holds the key down, we
     * want to be able to execute some code only on the initial key press.
     */
    private static boolean isFirstKeypress = true;

    public SafeOverlayKeyHandler(KeyBinding[] keyBindings, boolean[] repeatings)
    {
        super(keyBindings, repeatings);
    }

    @Override
    public String getLabel()
    {
        return "Safe Overlay Key Handler";
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
    {
        if (!tickEnd)
        {
            return;    //this fixes an issue with the method being called twice
        }

        if (mc.currentScreen != null)
        {
            return;    //don't activate if the user is looking at a GUI
        }
        
        if(!SafeOverlay.Enabled)
        	return;

        //if "+" is pressed, increase the draw distance
        if (Keyboard.isKeyDown(Keyboard.KEY_EQUALS) || 	//keyboard "+" ("=")
                Keyboard.isKeyDown(Keyboard.KEY_ADD))	//numpad "+"
        {
            int drawDistance = SafeOverlay.instance.increaseDrawDistance();

            if (drawDistance == SafeOverlay.maxDrawDistance)
            {
                InfoLine.DisplayNotification(Localization.get("safeoverlay.distance") + " " + drawDistance + " ("+Localization.get("safeoverlay.distance.max")+")");
            }
            else
            {
                InfoLine.DisplayNotification(Localization.get("safeoverlay.distance") + " " + drawDistance);
            }

            SafeOverlay.instance.RecalculateUnsafePositions();

            isFirstKeypress = false;
            return;
        }

        //if "-" is pressed, decrease the draw distance
        if (Keyboard.isKeyDown(Keyboard.KEY_MINUS))
        {
            int drawDistance = SafeOverlay.instance.decreaseDrawDistance();
            InfoLine.DisplayNotification(Localization.get("safeoverlay.distance") + " " + drawDistance);
            SafeOverlay.instance.RecalculateUnsafePositions();

            isFirstKeypress = false;
            return;
        }

        //if "0" is pressed, set to the default draw distance
        if (Keyboard.isKeyDown(Keyboard.KEY_0))
        {
            int drawDistance = SafeOverlay.instance.setDrawDistance(SafeOverlay.defaultDrawDistance);
            SafeOverlay.instance.setSeeUnsafePositionsThroughWalls(false);
            InfoLine.DisplayNotification(Localization.get("safeoverlay.distance") + " " + Localization.get("safeoverlay.distance.default") + " (" + drawDistance + ")");
            SafeOverlay.instance.RecalculateUnsafePositions();

            isFirstKeypress = false;
            return;
        }
        
        if(!isFirstKeypress)
        	return;
        isFirstKeypress = false;
        


        //if Control is pressed, enable see through mode
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
                || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
        {
            boolean seeThroughWalls = SafeOverlay.instance.toggleSeeUnsafePositionsThroughWalls();

            if (seeThroughWalls)
            {
                InfoLine.DisplayNotification(Localization.get("safeoverlay.seethroughwallsenabled"));
            }
            else
            {
                InfoLine.DisplayNotification(Localization.get("safeoverlay.seethroughwallsdisabled"));
            }

            SafeOverlay.instance.RecalculateUnsafePositions();
            
            return;
        }
        
        

        SafeOverlay.ToggleMode();

        if (SafeOverlay.Mode == 1)
        {
            //if we enable the mod, recalculate unsafe areas immediately
            SafeOverlay.instance.RecalculateUnsafePositions();
        }
    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd)
    {
        if (!tickEnd)
        {
            return;    //this fixes an issue with the method being called twice
        }
        
        isFirstKeypress = true;
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return tickTypes;
    }
}