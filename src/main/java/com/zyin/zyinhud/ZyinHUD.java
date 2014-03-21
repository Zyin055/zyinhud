/* ========================================================================================================
 * 
 * Zyin's HUD
 * 
 * This code is all open source and you are free, and encouraged, to do whatever you want with it.
 * 
 * Adding your own functionality is (relatively) simple. First make a class in com.zyin.zyinhud.mods
 * which contains all of your 'mods' logic. Then you need a way to interact with your mod. You can
 * do this with a Tick Handler (already setup for you in ZyinHUDRenderer.java), a Hotkey (follow the
 * examples in ZyinHUDKeyHandlers.java), or a single-player only command (see com.zyin.zyinhud.command).
 * 
 * To add configurable options to you mod, you need to add a new tab to GuiZyinHUDOptions.java.
 * You do this by modifing the tabbedButtonNames and tabbedButtonIDs variables. Then add your new button 
 * actions in the actionPerformed() method. To have these configurable options persist after logging out,
 * you need to follow the examples in ZyinHUDConfig.java to write your data to the config file.
 * 
 * That's it! Make sure to check out the other classes as they have useful helper functions. If you don't
 * know how to do something, just look at how another mod does something similar to it.
 * 
 * ========================================================================================================
 */

package com.zyin.zyinhud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;

import com.zyin.zyinhud.command.CommandFps;
import com.zyin.zyinhud.command.CommandZyinHUDOptions;
import com.zyin.zyinhud.gui.GuiOptionsOverride;
import com.zyin.zyinhud.mods.HealthMonitor;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = ZyinHUD.MODID, version = ZyinHUD.MODVERSION)
public class ZyinHUD
{
	/**
	 * Version number must be changed in 3 spots before releasing a build:<br><ol>
	 * <li>MODVERSION
	 * <li>src/main/resources/mcmod.info:"version"
	 * <li>build.gradle:version
	 */
	public static final String MODVERSION = "1.2.0";
    public static final String MODID = "zyinhud";
    public static final String MODNAME = "Zyin's HUD";
    
    @SidedProxy(clientSide = "com.zyin.zyinhud.ClientProxy", serverSide = "com.zyin.zyinhud.CommonProxy")
    public static CommonProxy proxy;
    
    protected static final Minecraft mc = Minecraft.getMinecraft();
    
    
    public ZyinHUD()
    {
    	
    }
    
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	//load all our Key Handlers
    	FMLCommonHandler.instance().bus().register(ZyinHUDKeyHandlers.instance);
        MinecraftForge.EVENT_BUS.register(ZyinHUDKeyHandlers.instance);

        //load configuration settings from the ZyinHUD.cfg file
    	ZyinHUDConfig.LoadConfigSettings(event.getSuggestedConfigurationFile());
        
    	//load language localization files
        ModContainer modContainer = FMLCommonHandler.instance().findContainerFor(this);
        LanguageRegistry.instance().loadLanguagesFor(modContainer, Side.CLIENT);
    }
	
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	//needed for @SubscribeEvent method subscriptions
    	MinecraftForge.EVENT_BUS.register(this);
    	MinecraftForge.EVENT_BUS.register(ZyinHUDRenderer.instance);
    	MinecraftForge.EVENT_BUS.register(HealthMonitor.instance);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	
    }
    

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
    	//THIS EVENT IS NOT FIRED ON SMP SERVERS
    	event.registerServerCommand(new CommandFps());
    	event.registerServerCommand(new CommandZyinHUDOptions());
    }
    
    
    /**
     * Event fired before a GUI is opened.
     * @param event
     */
    @SubscribeEvent
    public void GuiOpenEvent(GuiOpenEvent event)
    {
    	//override the default Options screen with our custom one, which contains our custom "Zyin's HUD..." button
    	if (event.gui instanceof GuiOptions && mc.theWorld != null)
        {
    		event.gui = new GuiOptionsOverride(new GuiIngameMenu(), mc.gameSettings);
        }
    }
    
}

