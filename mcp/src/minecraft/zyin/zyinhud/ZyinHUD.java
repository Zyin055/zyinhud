package zyin.zyinhud;

import java.io.File;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;

import org.lwjgl.input.Keyboard;

import zyin.zyinhud.command.CommandFps;
import zyin.zyinhud.command.CommandZyinHUDOptions;
import zyin.zyinhud.gui.GuiZyinHUDOptions;
import zyin.zyinhud.keyhandler.AnimalInfoKeyHandler;
import zyin.zyinhud.keyhandler.DistanceMeasurerKeyHandler;
import zyin.zyinhud.keyhandler.EatingAidKeyHandler;
import zyin.zyinhud.keyhandler.EnderPearlAidKeyHandler;
import zyin.zyinhud.keyhandler.GuiZyinHUDOptionsKeyHandler;
import zyin.zyinhud.keyhandler.PlayerLocatorKeyHandler;
import zyin.zyinhud.keyhandler.PotionAidKeyHandler;
import zyin.zyinhud.keyhandler.SafeOverlayKeyHandler;
import zyin.zyinhud.keyhandler.WeaponSwapperKeyHandler;
import zyin.zyinhud.mods.AnimalInfo;
import zyin.zyinhud.mods.Clock;
import zyin.zyinhud.mods.Compass;
import zyin.zyinhud.mods.Coordinates;
import zyin.zyinhud.mods.DistanceMeasurer;
import zyin.zyinhud.mods.DurabilityInfo;
import zyin.zyinhud.mods.EatingAid;
import zyin.zyinhud.mods.EnderPearlAid;
import zyin.zyinhud.mods.Fps;
import zyin.zyinhud.mods.InfoLine;
import zyin.zyinhud.mods.PlayerLocator;
import zyin.zyinhud.mods.PotionAid;
import zyin.zyinhud.mods.PotionTimers;
import zyin.zyinhud.mods.SafeOverlay;
import zyin.zyinhud.mods.WeaponSwapper;
import zyin.zyinhud.tickhandler.GUITickHandler;
import zyin.zyinhud.tickhandler.HUDTickHandler;
import zyin.zyinhud.tickhandler.RenderTickHandler;
import zyin.zyinhud.util.Localization;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "ZyinHUD", name = "Zyin's HUD", version = "0.11.8")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class ZyinHUD
{
    /**
     * Comma seperated values of languages to load by setting the default value in the config file.
     * Recreate the config file, or just the variable "SupportedLanguages" (located in the config file)
     * to see these values updated.
     */
    private static final String DefaultSupportedLanguages = "en_US"; //"en_US, zh_CN";
    
    //this should match the text used in the en_US.properties file because its used to grab data from the localization file
    public static final String CATEGORY_MISC = "misc";
    public static final String CATEGORY_INFOLINE = "infoline";
    public static final String CATEGORY_COORDINATES = "coordinates";
    public static final String CATEGORY_COMPASS = "compass";
    public static final String CATEGORY_DISTANCEMEASURER = "distancemeasurer";
    public static final String CATEGORY_DURABILITYINFO = "durabilityinfo";
    public static final String CATEGORY_SAFEOVERLAY = "safeoverlay";
    public static final String CATEGORY_POTIONTIMERS = "potiontimers";
    public static final String CATEGORY_PLAYERLOCATOR = "playerlocator";
    public static final String CATEGORY_EATINGAID = "eatingaid";
    public static final String CATEGORY_WEAPONSWAP = "weaponswap";
    public static final String CATEGORY_FPS = "fps";
    public static final String CATEGORY_ANIMALINFO = "horseinfo";
    public static final String CATEGORY_ENDERPEARLAID = "enderpearlaid";
    public static final String CATEGORY_CLOCK = "clock";
    public static final String CATEGORY_POTIONAID = "potionaid";

    public static String SupportedLanguages;
    
    //Key bindings
    protected static KeyBinding[] key_K;
    protected static KeyBinding[] key_L;
    protected static KeyBinding[] key_P;
    protected static KeyBinding[] key_G;
    protected static KeyBinding[] key_F;
    protected static KeyBinding[] key_O;
    protected static KeyBinding[] key_C;
    protected static KeyBinding[] key_V;
    protected static KeyBinding[] key_Z;
    
    
    //default hotkeys
    protected static String DefaultDistanceMeasurerHotkey = "K";
    protected static String DefaultSafeOverlayHotkey = "L";
    protected static String DefaultPlayerLocatorHotkey = "P";
    protected static String DefaultEatingAidHotkey = "G";
    protected static String DefaultWeaponSwapHotkey = "F";
    protected static String DefaultAnimalInfoHotkey = "O";	//also: 0 + L, - + L, + + L
    protected static String DefaultEnderPearlAidHotkey = "C";
    protected static String DefaultPotionAidHotkey = "V";
    protected static String DefaultOptionsHotkey = "Z";	//Ctrl + Alt + Z
    
    public static Configuration config = null;
    
    //@Instance("ZyinHUD")
    //public static ZyinHUD instance;

    @SidedProxy(clientSide = "zyin.zyinhud.ClientProxy", serverSide = "zyin.zyinhud.CommonProxy")
    public static CommonProxy proxy;
    
    
    public ZyinHUD()
    {
    	
    }
    

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LoadConfigSettings(event.getSuggestedConfigurationFile());
        
        Localization.LoadLanguages("/lang/zyinhud/", GetSupportedLanguages());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(RenderTickHandler.instance);	//needed for @ForgeSubscribe method subscriptions

        TickRegistry.registerTickHandler(new HUDTickHandler(), Side.CLIENT);
        TickRegistry.registerTickHandler(new GUITickHandler(), Side.CLIENT);
        
    	LoadKeyHandlers();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {

    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
    	//THIS EVENT IS NOT FIRED ON SMP SERVERS
    	event.registerServerCommand(new CommandFps());
    	event.registerServerCommand(new CommandZyinHUDOptions());
    }
    
    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
    	//THIS EVENT IS NOT FIRED ON SMP SERVERS
    	SaveConfigSettings();
    }

    
    
    /**
     * Gets a string array of supported languages
     * @return e.x. ["en_US", "zh_CN"]
     */
    private String[] GetSupportedLanguages()
    {
    	return SupportedLanguages.replace(" ","").split(",");
    }
    
    /**
     * Loads every value from the configuration file.
     * @param configFile
     */
    private static void LoadConfigSettings(File configFile)
    {
    	ReadConfigSettings(configFile, true);
    }
    
    /**
     * Saves every value back to the config file.
     */
    public static void SaveConfigSettings()
    {
    	ReadConfigSettings(null, false);
    }
    
    /**
     * Creates the config file if it doesn't already exist.
     * It loads/saves config values from/to the config file.
     * @param configFile Standard Forge configuration file
     * @param loadSettings set to true to load the settings from the config file, 
     * or false to save the settings to the config file
     */
    private static void ReadConfigSettings(File configFile, boolean loadSettings)
    {
    	//NOTE: doing config.save() multiple times will bug out and add additional quotes to
    	//categories with more than 1 word
    	if(loadSettings)
    	{
            config = new Configuration(configFile);
            config.load();
    	}
        
        Property p;
        
        config.addCustomCategoryComment(CATEGORY_MISC, "Other settings not related to any specific functionality.");
        config.addCustomCategoryComment(CATEGORY_INFOLINE, "Info Line displays the status of other features in the top left corner of the screen.");
        config.addCustomCategoryComment(CATEGORY_COORDINATES, "Coordinates displays your coordinates. Nuff said.");
        config.addCustomCategoryComment(CATEGORY_COMPASS, "Compass displays a text compass.");
        config.addCustomCategoryComment(CATEGORY_DISTANCEMEASURER, "Distance Measurer can calculate distances between you and blocks that you aim at.");
        config.addCustomCategoryComment(CATEGORY_DURABILITYINFO, "Durability Info will display your breaking armor and equipment.");
        config.addCustomCategoryComment(CATEGORY_SAFEOVERLAY, "Safe Overlay shows you which blocks are dark enough to spawn mobs.");
        config.addCustomCategoryComment(CATEGORY_POTIONTIMERS, "Potion Timers shows the duration remaining on potions that you drink.");
        config.addCustomCategoryComment(CATEGORY_PLAYERLOCATOR, "Player Locator gives you a radar-like ability to easily see where other people are.");
        config.addCustomCategoryComment(CATEGORY_EATINGAID, "Eating Aid makes eating food quick and easy.");
        config.addCustomCategoryComment(CATEGORY_WEAPONSWAP, "Weapon Swap allows you to quickly select your sword and bow.");
        config.addCustomCategoryComment(CATEGORY_FPS, "FPS shows your frames per second without having to go into the F3 menu.");
        config.addCustomCategoryComment(CATEGORY_ANIMALINFO, "Animal Info gives you information about horse stats, such as speed and jump height.");
        config.addCustomCategoryComment(CATEGORY_ENDERPEARLAID, "Ender Pearl Aid makes it easier to quickly throw ender pearls.");
        config.addCustomCategoryComment(CATEGORY_CLOCK, "Clock shows you time relevant to Minecraft time.");
        config.addCustomCategoryComment(CATEGORY_POTIONAID, "Potion Aid helps you quickly drink potions based on your circumstance.");
        
        
        //CATEGORY_MISC
        p = config.get(CATEGORY_MISC, "SupportedLanguages", DefaultSupportedLanguages);
        p.comment = "Languages must be added here in order to get loaded, in addition to adding a .properties file at /lang/zyinhud/. Values are comma seperated.";
        SupportedLanguages = p.getString();
        
        p = config.get(CATEGORY_MISC, "OptionsHotkey", DefaultOptionsHotkey);
        p.comment = "Default: "+DefaultOptionsHotkey;
        if(loadSettings)
        	GuiZyinHUDOptions.Hotkey = p.getString();
        else
        	p.set(Keyboard.getKeyName(key_Z[0].keyCode));
        
        
        //CATEGORY_INFOLINE
        p = config.get(CATEGORY_INFOLINE, "EnableInfoLine", true);
        p.comment = "Enable/Disable the entire info line in the top left part of the screen. This includes the clock, coordinates, compass, mod status, etc.";
        if(loadSettings)
        	InfoLine.Enabled = p.getBoolean(true);
        else
        	p.set(InfoLine.Enabled);
        
        
        //CATEGORY_COORDINATES
        p = config.get(CATEGORY_COORDINATES, "EnableCoordinates", true);
        p.comment = "Enable/Disable showing your coordinates.";
        if(loadSettings)
        	Coordinates.Enabled = p.getBoolean(true);
        else
        	p.set(Coordinates.Enabled);
        
        p = config.get(CATEGORY_COORDINATES, "UseYCoordinateColors", true);
        p.comment = "Color code the Y (height) coordinate based on what ores can spawn at that level.";
        if(loadSettings)
        	Coordinates.UseYCoordinateColors = p.getBoolean(true);
        else
        	p.set(Coordinates.UseYCoordinateColors);
        
        
        //CATEGORY_COMPASS
        p = config.get(CATEGORY_COMPASS, "EnableCompass", true);
        p.comment = "Enable/Disable showing the compass.";
        if(loadSettings)
        	Compass.Enabled = p.getBoolean(true);
        else
        	p.set(Compass.Enabled);
        

        //CATEGORY_DISTANCEMEASURER
        p = config.get(CATEGORY_DISTANCEMEASURER, "EnableDistanceMeasurer", true);
        p.comment = "Enable/Disable the distance measurer.";
        if(loadSettings)
        	DistanceMeasurer.Enabled = p.getBoolean(true);
        else
        	p.set(DistanceMeasurer.Enabled);
        
        p = config.get(CATEGORY_DISTANCEMEASURER, "DistanceMeasurerHotkey", DefaultDistanceMeasurerHotkey);
        p.comment = "Default: " + DefaultDistanceMeasurerHotkey;
        if(loadSettings)
        	DistanceMeasurer.Hotkey = p.getString();
        else
        	p.set(DistanceMeasurer.Hotkey);
        
        
        //CATEGORY_DURABILITYINFO
        p = config.get(CATEGORY_DURABILITYINFO, "EnableDurabilityInfo", true);
        p.comment = "Enable/Disable showing all durability info.";
        if(loadSettings)
        	DurabilityInfo.Enabled = p.getBoolean(true);
        else
        	p.set(DurabilityInfo.Enabled);
        
        p = config.get(CATEGORY_DURABILITYINFO, "ShowArmorDurability", true);
        p.comment = "Enable/Disable showing breaking armor.";
        if(loadSettings)
        	DurabilityInfo.ShowArmorDurability = p.getBoolean(true);
        else
        	p.set(DurabilityInfo.ShowArmorDurability);

        p = config.get(CATEGORY_DURABILITYINFO, "ShowItemDurability", true);
        p.comment = "Enable/Disable showing breaking items.";
        if(loadSettings)
        	DurabilityInfo.ShowItemDurability = p.getBoolean(true);
        else
        	p.set(DurabilityInfo.ShowItemDurability);

        p = config.get(CATEGORY_DURABILITYINFO, "ShowIndividualArmorIcons", false);
        p.comment = "Enable/Disable showing armor peices instead of the big broken armor icon.";
        if(loadSettings)
        	DurabilityInfo.ShowIndividualArmorIcons = p.getBoolean(false);
        else
        	p.set(DurabilityInfo.ShowIndividualArmorIcons);
        
        p = config.get(CATEGORY_DURABILITYINFO, "DurabilityDisplayThresholdForArmor", 0.1);
        p.comment = "Display when armor gets damaged less than this fraction of its durability.";
        if(loadSettings)
        	DurabilityInfo.DurabilityDisplayThresholdForArmor = (float)p.getDouble(0.1);
        else
        	p.set(DurabilityInfo.DurabilityDisplayThresholdForArmor);
        
        p = config.get(CATEGORY_DURABILITYINFO, "DurabilityDisplayThresholdForItem", 0.1);
        p.comment = "Display when an item gets damaged less than this fraction of its durability.";
        if(loadSettings)
        	DurabilityInfo.DurabilityDisplayThresholdForItem = (float)p.getDouble(0.1);
        else
        	p.set(DurabilityInfo.DurabilityDisplayThresholdForItem);
        
        p = config.get(CATEGORY_DURABILITYINFO, "DurabilityUpdateFrequency", 1000);
        p.comment = "Update the the durability info display every XX ms.";
        if(loadSettings)
        	DurabilityInfo.DurabilityUpdateFrequency = p.getInt();
        else
        	p.set(DurabilityInfo.DurabilityUpdateFrequency);
        
        p = config.get(CATEGORY_DURABILITYINFO, "DurabilityLocationHorizontal", 30);
        p.comment = "The horizontal position of the durability icons. 0 is left, 400 is far right.";
        if(loadSettings)
        	DurabilityInfo.SetHorizontalLocation(p.getInt());
        else
        	p.set(DurabilityInfo.GetHorizontalLocation());
        
        p = config.get(CATEGORY_DURABILITYINFO, "DurabilityLocationVertical", 20);
        p.comment = "The vertical position of the durability icons. 0 is top, 200 is very bottom.";
        if(loadSettings)
        	DurabilityInfo.SetVerticalLocation(p.getInt());
        else
        	p.set(DurabilityInfo.GetVerticalLocation());
        
        
        //CATEGORY_SAFEOVERLAY
        p = config.get(CATEGORY_SAFEOVERLAY, "EnableSafeOverlay", true);
        p.comment = "Enable/Disable the Safe Overlay.";
        if(loadSettings)
        	SafeOverlay.Enabled = p.getBoolean(true);
        else
        	p.set(SafeOverlay.Enabled);
        
        p = config.get(CATEGORY_SAFEOVERLAY, "SafeOverlayHotkey", DefaultSafeOverlayHotkey);
        p.comment = "Default: "+DefaultSafeOverlayHotkey;
        if(loadSettings)
        	SafeOverlay.Hotkey = p.getString();
        else
        	p.set(Keyboard.getKeyName(key_L[0].keyCode));
        
        p = config.get(CATEGORY_SAFEOVERLAY, "SafeOverlayDrawDistance", 20);
        p.comment = "How far away unsafe spots should be rendered around the player measured in blocks. This can be changed in game with - + "+DefaultSafeOverlayHotkey+" and + + "+DefaultSafeOverlayHotkey+".";
        if(loadSettings)
        	SafeOverlay.instance.setDrawDistance(p.getInt(20));
        else
        	p.set(SafeOverlay.instance.getDrawDistance());
        
        p = config.get(CATEGORY_SAFEOVERLAY, "SafeOverlayTransparency", 0.3);
        p.comment = "The transparency of the unsafe marks. Must be between greater than 0.1 and less than or equal to 1.";
        if(loadSettings)
        	SafeOverlay.instance.setUnsafeOverlayTransparency((float)p.getDouble(0.3));
        else
        	p.set(SafeOverlay.instance.getUnsafeOverlayTransparency());
        
        p = config.get(CATEGORY_SAFEOVERLAY, "SafeOverlayDisplayInNether", false);
        p.comment = "Enable/Disable showing unsafe areas in the Nether.";
        if(loadSettings)
        	SafeOverlay.instance.setDisplayInNether(p.getBoolean(false));
        else
        	p.set(SafeOverlay.instance.getDisplayInNether());
        
        p = config.get(CATEGORY_SAFEOVERLAY, "SafeOverlaySeeThroughWalls", false);
        p.comment = "Enable/Disable showing unsafe areas through walls. Toggle in game with Ctrl + "+DefaultSafeOverlayHotkey+".";
        if(loadSettings)
        	SafeOverlay.instance.setSeeUnsafePositionsThroughWalls(p.getBoolean(false));
        else
        	p.set(SafeOverlay.instance.getSeeUnsafePositionsThroughWalls());
        
        
        //CATEGORY_POTIONTIMERS
        p = config.get(CATEGORY_POTIONTIMERS, "EnablePotionTimers", true);
        p.comment = "Enable/Disable showing the time remaining on potions.";
        if(loadSettings)
        	PotionTimers.Enabled = p.getBoolean(true);
        else
        	p.set(PotionTimers.Enabled);

        p = config.get(CATEGORY_POTIONTIMERS, "ShowPotionIcons", true);
        p.comment = "Enable/Disable showing the status effect of potions next to the timers.";
        if(loadSettings)
        	PotionTimers.ShowPotionIcons = p.getBoolean(true);
        else
        	p.set(PotionTimers.ShowPotionIcons);
        
        p = config.get(CATEGORY_POTIONTIMERS, "PotionTimersLocationHorizontal", 1);
        p.comment = "The horizontal position of the potion timers. 0 is left, 400 is far right.";
        if(loadSettings)
        	PotionTimers.SetHorizontalLocation(p.getInt());
        else
        	p.set(PotionTimers.GetHorizontalLocation());
        
        p = config.get(CATEGORY_POTIONTIMERS, "PotionTimersLocationVertical", 16);
        p.comment = "The vertical position of the potion timers. 0 is top, 200 is very bottom.";
        if(loadSettings)
        	PotionTimers.SetVerticalLocation(p.getInt());
        else
        	p.set(PotionTimers.GetVerticalLocation());
        
        
        
        //CATEGORY_PLAYERLOCATOR
        p = config.get(CATEGORY_PLAYERLOCATOR, "EnablePlayerLocator", true);
        p.comment = "Enable/Disable the Player Locator.";
        if(loadSettings)
        	PlayerLocator.Enabled = p.getBoolean(true);
        else
        	p.set(PlayerLocator.Enabled);
        
        p = config.get(CATEGORY_PLAYERLOCATOR, "PlayerLocatorHotkey", DefaultPlayerLocatorHotkey);
        p.comment = "Default: "+DefaultPlayerLocatorHotkey;
        if(loadSettings)
        	PlayerLocator.Hotkey = p.getString();
        else
        	p.set(Keyboard.getKeyName(key_P[0].keyCode));
        
        p = config.get(CATEGORY_PLAYERLOCATOR, "ShowDistanceToPlayers", false);
        p.comment = "Show how far away you are from the other players next to their name.";
        if(loadSettings)
        	PlayerLocator.ShowDistanceToPlayers = p.getBoolean(false);
        else
        	p.set(PlayerLocator.ShowDistanceToPlayers);
        
        p = config.get(CATEGORY_PLAYERLOCATOR, "ShowPlayerHealth", false);
        p.comment = "Show how much health players have by their name.";
        if(loadSettings)
        	PlayerLocator.ShowPlayerHealth = p.getBoolean(false);
        else
        	p.set(PlayerLocator.ShowPlayerHealth);
        
        p = config.get(CATEGORY_PLAYERLOCATOR, "PlayerLocatorMinViewDistance", 10);
        p.comment = "Stop showing player names when they are this close (distance measured in blocks).";
        if(loadSettings)
        	PlayerLocator.viewDistanceCutoff = p.getInt(10);
        else
        	p.set(PlayerLocator.viewDistanceCutoff);
        
        
        //CATEGORY_EATINGAID
        p = config.get(CATEGORY_EATINGAID, "EnableEatingAid", true);
        p.comment = "Enables pressing a hotkey (default="+DefaultEatingAidHotkey+") to eat food even if it is  in your inventory and not your hotbar.";
        if(loadSettings)
        	EatingAid.Enabled = p.getBoolean(true);
        else
        	p.set(EatingAid.Enabled);
        
        p = config.get(CATEGORY_EATINGAID, "EatingAidHotkey", DefaultEatingAidHotkey);
        p.comment = "Default: "+DefaultEatingAidHotkey;
        if(loadSettings)
        	EatingAid.Hotkey = p.getString();
        else
        	p.set(Keyboard.getKeyName(key_G[0].keyCode));
        
        p = config.get(CATEGORY_EATINGAID, "EatGoldenFood", false);
        p.comment = "Enable/Disable using golden apples and golden carrots as food.";
        if(loadSettings)
        	EatingAid.EatGoldenFood = p.getBoolean(false);
        else
        	p.set(EatingAid.EatGoldenFood);
        
        p = config.get(CATEGORY_EATINGAID, "PrioritizeFoodInHotbar", false);
        p.comment = "Use food that is in your hotbar before looking for food in your main inventory.";
        if(loadSettings)
        	EatingAid.PrioritizeFoodInHotbar = p.getBoolean(false);
        else
        	p.set(EatingAid.PrioritizeFoodInHotbar);
        
        p = config.get(CATEGORY_EATINGAID, "EatingAidMode", 1);
        p.comment = "Set the eating aid mode:" + config.NEW_LINE +
					"0 = always eat food with the highest saturation value" + config.NEW_LINE +
					"1 = intelligently select food so that you don't overeat and waste anything";
        if(loadSettings)
        	EatingAid.Mode = p.getInt(1);
        else
        	p.set(EatingAid.Mode);
        
        
        //CATEGORY_WEAPONSWAP
        p = config.get(CATEGORY_WEAPONSWAP, "EnableWeaponSwap", true);
        p.comment = "Enables pressing a hotkey (default="+DefaultWeaponSwapHotkey+") to swap between your sword and bow.";
        if(loadSettings)
        	WeaponSwapper.Enabled = p.getBoolean(true);
        else
        	p.set(WeaponSwapper.Enabled);
        
        p = config.get(CATEGORY_WEAPONSWAP, "WeaponSwapHotkey", DefaultWeaponSwapHotkey);
        p.comment = "Default: "+DefaultWeaponSwapHotkey;
        if(loadSettings)
        	WeaponSwapper.Hotkey = p.getString();
        else
        	p.set(Keyboard.getKeyName(key_F[0].keyCode));
        
        p = config.get(CATEGORY_WEAPONSWAP, "ScanHotbarForWeaponsFromLeftToRight", true);
        p.comment = "Set to false to scan the hotbar for swords and bows from right to left. Only matters if you have multiple swords/bows in your hotbar.";
        if(loadSettings)
        	WeaponSwapper.ScanHotbarForWeaponsFromLeftToRight = p.getBoolean(true);
        else
        	p.set(WeaponSwapper.ScanHotbarForWeaponsFromLeftToRight);
        
        
        //CATEGORY_FPS
        p = config.get(CATEGORY_FPS, "EnableFPS", false);
        p.comment = "Enable/Disable showing your FPS at the end of the Info Line.";
        if(loadSettings)
        	Fps.Enabled = p.getBoolean(false);
        else
        	p.set(Fps.Enabled);
        
        
        //CATEGORY_ANIMALINFO
        p = config.get(CATEGORY_ANIMALINFO, "EnableAnimalInfo", true);
        p.comment = "Enable/Disable Animal Info.";
        if(loadSettings)
        	AnimalInfo.Enabled = p.getBoolean(true);
        else
        	p.set(AnimalInfo.Enabled);
        
        p = config.get(CATEGORY_ANIMALINFO, "AnimalInfoHotkey", DefaultAnimalInfoHotkey);
        p.comment = "Default: "+DefaultAnimalInfoHotkey;
        if(loadSettings)
        	AnimalInfo.Hotkey = p.getString();
        else
        	p.set(Keyboard.getKeyName(key_O[0].keyCode));
        
        p = config.get(CATEGORY_ANIMALINFO, "ShowTextBackgrounds", true);
        p.comment = "Enable/Disable showing a black background behind text.";
        if(loadSettings)
        	AnimalInfo.ShowTextBackgrounds = p.getBoolean(true);
        else
        	p.set(AnimalInfo.ShowTextBackgrounds);
        
        p = config.get(CATEGORY_ANIMALINFO, "ShowHorseStatsOnF3Menu", true);
        p.comment = "Enable/Disable showing the stats of the horse you're riding on the F3 screen.";
        if(loadSettings)
        	AnimalInfo.ShowHorseStatsOnF3Menu = p.getBoolean(true);
        else
        	p.set(AnimalInfo.ShowHorseStatsOnF3Menu);
        
        p = config.get(CATEGORY_ANIMALINFO, "ShowHorseStatsOverlay", true);
        p.comment = "Enable/Disable showing the stats of horses on screen.";
        if(loadSettings)
        	AnimalInfo.ShowHorseStatsOverlay = p.getBoolean(true);
        else
        	p.set(AnimalInfo.ShowHorseStatsOverlay);
        
        p = config.get(CATEGORY_ANIMALINFO, "AnimalInfoMaxViewDistance", 8);
        p.comment = "How far away animal info will be rendered on the screen (distance measured in blocks).";
        if(loadSettings)
        	AnimalInfo.viewDistanceCutoff = p.getInt(8);
        else
        	p.set(AnimalInfo.viewDistanceCutoff);
        
        p = config.get(CATEGORY_ANIMALINFO, "HorseInfoNumberOfDecimalsDisplayed", 1);
        p.comment = "How many decimal places will be used when displaying horse stats.";
        if(loadSettings)
        	AnimalInfo.SetNumberOfDecimalsDisplayed(p.getInt(1));
        else
        	p.set(AnimalInfo.GetNumberOfDecimalsDisplayed());

        p = config.get(CATEGORY_ANIMALINFO, "ShowBreedingIcons", true);
        p.comment = "Enable/Disable showing an icon if the animal is ready to breed.";
        if(loadSettings)
        	AnimalInfo.ShowBreedingIcons = p.getBoolean(true);
        else
        	p.set(AnimalInfo.ShowBreedingIcons);

        p = config.get(CATEGORY_ANIMALINFO, "ShowBreedingTimers", true);
        p.comment = "Enable/Disable showing a timer counting down to when the animal is ready to breed again.";
        if(loadSettings)
        	AnimalInfo.ShowBreedingTimers = p.getBoolean(true);
        else
        	p.set(AnimalInfo.ShowBreedingTimers);

        p = config.get(CATEGORY_ANIMALINFO, "ShowBreedingTimerForHorses", true);
        p.comment = "Enable/Disable showing a timer that tells you how long until a horse can breed again.";
        if(loadSettings)
        	AnimalInfo.ShowBreedingTimerForHorses = p.getBoolean(true);
        else
        	p.set(AnimalInfo.ShowBreedingTimerForHorses);
        
        p = config.get(CATEGORY_ANIMALINFO, "ShowBreedingTimerForVillagers", true);
        p.comment = "Enable/Disable showing a timer that tells you how long until a villager can breed again.";
        if(loadSettings)
        	AnimalInfo.ShowBreedingTimerForVillagers = p.getBoolean(true);
        else
        	p.set(AnimalInfo.ShowBreedingTimerForVillagers);
        
        p = config.get(CATEGORY_ANIMALINFO, "ShowBreedingTimerForCows", true);
        p.comment = "Enable/Disable showing a timer that tells you how long until a cow can breed again.";
        if(loadSettings)
        	AnimalInfo.ShowBreedingTimerForCows = p.getBoolean(true);
        else
        	p.set(AnimalInfo.ShowBreedingTimerForCows);
        
        p = config.get(CATEGORY_ANIMALINFO, "ShowBreedingTimerForSheep", true);
        p.comment = "Enable/Disable showing a timer that tells you how long until a sheep can breed again.";
        if(loadSettings)
        	AnimalInfo.ShowBreedingTimerForSheep = p.getBoolean(true);
        else
        	p.set(AnimalInfo.ShowBreedingTimerForSheep);
        
        p = config.get(CATEGORY_ANIMALINFO, "ShowBreedingTimerForPigs", true);
        p.comment = "Enable/Disable showing a timer that tells you how long until a pig can breed again.";
        if(loadSettings)
        	AnimalInfo.ShowBreedingTimerForPigs = p.getBoolean(true);
        else
        	p.set(AnimalInfo.ShowBreedingTimerForPigs);
        
        p = config.get(CATEGORY_ANIMALINFO, "ShowBreedingTimerForChickens", true);
        p.comment = "Enable/Disable showing a timer that tells you how long until a chicken can breed again.";
        if(loadSettings)
        	AnimalInfo.ShowBreedingTimerForChickens = p.getBoolean(true);
        else
        	p.set(AnimalInfo.ShowBreedingTimerForChickens);
        
        
        //CATEGORY_ENDERPEARLAID
        p = config.get(CATEGORY_ENDERPEARLAID, "EnableEnderPearlAid", true);
        p.comment = "Enables pressing a hotkey (default="+DefaultEnderPearlAidHotkey+") to use an enderpearl even if it is  in your inventory and not your hotbar.";
        if(loadSettings)
        	EnderPearlAid.Enabled = p.getBoolean(true);
        else
        	p.set(EnderPearlAid.Enabled);
        
        p = config.get(CATEGORY_ENDERPEARLAID, "EnderPearlAidHotkey", DefaultEnderPearlAidHotkey);
        p.comment = "Default: "+DefaultEnderPearlAidHotkey;
        if(loadSettings)
        	EnderPearlAid.Hotkey = p.getString();
        else
        	p.set(Keyboard.getKeyName(key_C[0].keyCode));
        
        
        //CATEGORY_CLOCK
        p = config.get(CATEGORY_CLOCK, "EnableClock", true);
        p.comment = "Enable/Disable showing the Clock.";
        if(loadSettings)
        	Clock.Enabled = p.getBoolean(true);
        else
        	p.set(Clock.Enabled);
        
        p = config.get(CATEGORY_CLOCK, "ClockMode", 0);
        p.comment = "Set the clock mode:" + config.NEW_LINE +
        			"0 = standard Minecraft time in game" + config.NEW_LINE +
        			"1 = countdown timer till morning/night.";
        if(loadSettings)
        	Clock.Mode = p.getInt(0);
        else
        	p.set(Clock.Mode);

        
        //CATEGORY_POTIONAID
        p = config.get(CATEGORY_POTIONAID, "EnablePotionAid", true);
        p.comment = "Enables pressing a hotkey (default="+DefaultPotionAidHotkey+") to drink a potion even if it is  in your inventory and not your hotbar.";
        if(loadSettings)
        	PotionAid.Enabled = p.getBoolean(true);
        else
        	p.set(PotionAid.Enabled);
        
        p = config.get(CATEGORY_POTIONAID, "PotionAidHotkey", DefaultPotionAidHotkey);
        p.comment = "Default: " + DefaultPotionAidHotkey;
        if(loadSettings)
        	PotionAid.Hotkey = p.getString();
        else
        	p.set(Keyboard.getKeyName(key_V[0].keyCode));
        
        

        config.save();
    }
    
    public static void LoadKeyHandlers()
    {
        //Key Bind Handlers (for hotkeys) are defined here

        KeyBinding.resetKeyBindingArrayAndHash();
        boolean[] repeatFalse = {false};
        boolean[] repeatTrue = {true};
        
        int hotkey;
        
        hotkey = GetKeyboardKeyFromString(DistanceMeasurer.Hotkey);
        hotkey = (hotkey == 0) ? Keyboard.getKeyIndex(DefaultDistanceMeasurerHotkey) : hotkey;
        key_K = new KeyBinding[] {new KeyBinding(DistanceMeasurer.HotkeyDescription, hotkey)};
        KeyBindingRegistry.registerKeyBinding(new DistanceMeasurerKeyHandler(key_K, repeatFalse));

        hotkey = GetKeyboardKeyFromString(SafeOverlay.Hotkey);
        hotkey = (hotkey == 0) ? Keyboard.getKeyIndex(DefaultSafeOverlayHotkey) : hotkey;
        key_L = new KeyBinding[] {new KeyBinding(SafeOverlay.HotkeyDescription, hotkey)};
        KeyBindingRegistry.registerKeyBinding(new SafeOverlayKeyHandler(key_L, repeatTrue));

        hotkey = GetKeyboardKeyFromString(PlayerLocator.Hotkey);
        hotkey = (hotkey == 0) ? Keyboard.getKeyIndex(DefaultPlayerLocatorHotkey) : hotkey;
        key_P = new KeyBinding[] {new KeyBinding(PlayerLocator.HotkeyDescription, hotkey)};
        KeyBindingRegistry.registerKeyBinding(new PlayerLocatorKeyHandler(key_P, repeatFalse));

        hotkey = GetKeyboardKeyFromString(EatingAid.Hotkey);
        hotkey = (hotkey == 0) ? Keyboard.getKeyIndex(DefaultEatingAidHotkey) : hotkey;
        key_G = new KeyBinding[] {new KeyBinding(EatingAid.HotkeyDescription, hotkey)};
        KeyBindingRegistry.registerKeyBinding(new EatingAidKeyHandler(key_G, repeatFalse));

        hotkey = GetKeyboardKeyFromString(WeaponSwapper.Hotkey);
        hotkey = (hotkey == 0) ? Keyboard.getKeyIndex(DefaultWeaponSwapHotkey) : hotkey;
        key_F = new KeyBinding[] {new KeyBinding(WeaponSwapper.HotkeyDescription, 	hotkey)};
        KeyBindingRegistry.registerKeyBinding(new WeaponSwapperKeyHandler(key_F, repeatFalse));

        hotkey = GetKeyboardKeyFromString(AnimalInfo.Hotkey);
        hotkey = (hotkey == 0) ? Keyboard.getKeyIndex(DefaultAnimalInfoHotkey) : hotkey;
        key_O = new KeyBinding[] {new KeyBinding(AnimalInfo.HotkeyDescription, hotkey)};
        KeyBindingRegistry.registerKeyBinding(new AnimalInfoKeyHandler(key_O, repeatFalse));
        
        hotkey = GetKeyboardKeyFromString(EnderPearlAid.Hotkey);
        hotkey = (hotkey == 0) ? Keyboard.getKeyIndex(DefaultEnderPearlAidHotkey) : hotkey;
        key_C = new KeyBinding[] {new KeyBinding(EnderPearlAid.HotkeyDescription, hotkey)};
        KeyBindingRegistry.registerKeyBinding(new EnderPearlAidKeyHandler(key_C, repeatFalse));

        hotkey = GetKeyboardKeyFromString(PotionAid.Hotkey);
        hotkey = (hotkey == 0) ? Keyboard.getKeyIndex(DefaultPotionAidHotkey) : hotkey;
        key_V = new KeyBinding[] {new KeyBinding(PotionAid.HotkeyDescription, hotkey)};
        KeyBindingRegistry.registerKeyBinding(new PotionAidKeyHandler(key_V, repeatFalse));

        hotkey = GetKeyboardKeyFromString(GuiZyinHUDOptions.Hotkey);
        hotkey = (hotkey == 0) ? Keyboard.getKeyIndex(DefaultOptionsHotkey) : hotkey;
        key_Z = new KeyBinding[] {new KeyBinding(GuiZyinHUDOptions.HotkeyDescription, hotkey)};
        KeyBindingRegistry.registerKeyBinding(new GuiZyinHUDOptionsKeyHandler(key_Z, repeatFalse));
        

    }
    
    /**
     * Converts the string representation of a key into an integer.
     * @param key example: "L", "G", "NUMPAD2"
     * @return an integer representation of this key
     */
    public static int GetKeyboardKeyFromString(String key)
    {
    	key = key.trim();
    	int keyIndex = Keyboard.getKeyIndex(key.toUpperCase());
    	if(keyIndex == 0)
    	{
    		System.out.println("=========================================================================");
    		System.out.println("[WARNING] ZyinHUD.cfg: \"" + key + "\" is not a valid hotkey!");
    		System.out.println("=========================================================================");
    	}
    	return keyIndex;
    }
    

	 
    public static String GetVersion()
    {
    	try
    	{
    		Mod header = (Mod)(ZyinHUD.class.getAnnotation(Mod.class));
    		return header.version();
    	}
    	catch (Exception exception)
    	{
    		exception.printStackTrace();
    	}
    	return "";
    }
    public static String GetModId()
    {
    	try
    	{
    		Mod header = (Mod)(ZyinHUD.class.getAnnotation(Mod.class));
    		return header.modid();
    	}
    	catch (Exception exception)
    	{
    		exception.printStackTrace();
    	}
    	return "";
    }
    public static String GetName()
    {
    	try
    	{
    		Mod header = (Mod)(ZyinHUD.class.getAnnotation(Mod.class));
    		return header.name();
    	}
    	catch (Exception exception)
    	{
    		exception.printStackTrace();
    	}
    	return "";
    }
}
