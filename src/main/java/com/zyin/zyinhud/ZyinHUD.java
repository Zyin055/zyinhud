package com.zyin.zyinhud;

import net.minecraftforge.common.MinecraftForge;

import com.zyin.zyinhud.command.CommandFps;
import com.zyin.zyinhud.command.CommandZyinHUDOptions;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
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
    public static final String MODVERSION = "1.1.4.1";
    public static final String MODID = "zyinhud";
    public static final String MODNAME = "Zyin's HUD";
    
    @SidedProxy(clientSide = "com.zyin.zyinhud.ClientProxy", serverSide = "com.zyin.zyinhud.CommonProxy")
    public static CommonProxy proxy;
    
    
    public ZyinHUD()
    {
    	
    }
    
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	//load all our Key Handlers
    	FMLCommonHandler.instance().bus().register(ZyinHUDKeyHandlers.instance);
    	
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
    	MinecraftForge.EVENT_BUS.register(ZyinHUDRenderer.instance);
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
    
    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
    	//THIS EVENT IS NOT FIRED ON SMP SERVERS
    	ZyinHUDConfig.SaveConfigSettings();
    }
    
}

