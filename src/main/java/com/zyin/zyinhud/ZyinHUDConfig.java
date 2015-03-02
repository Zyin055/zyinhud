package com.zyin.zyinhud;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.mods.AnimalInfo;
import com.zyin.zyinhud.mods.Clock;
import com.zyin.zyinhud.mods.Compass;
import com.zyin.zyinhud.mods.Coordinates;
import com.zyin.zyinhud.mods.DistanceMeasurer;
import com.zyin.zyinhud.mods.DurabilityInfo;
import com.zyin.zyinhud.mods.EatingAid;
import com.zyin.zyinhud.mods.EnderPearlAid;
import com.zyin.zyinhud.mods.Fps;
import com.zyin.zyinhud.mods.HealthMonitor;
import com.zyin.zyinhud.mods.InfoLine;
import com.zyin.zyinhud.mods.ItemSelector;
import com.zyin.zyinhud.mods.Miscellaneous;
import com.zyin.zyinhud.mods.PlayerLocator;
import com.zyin.zyinhud.mods.PotionAid;
import com.zyin.zyinhud.mods.PotionTimers;
import com.zyin.zyinhud.mods.QuickDeposit;
import com.zyin.zyinhud.mods.SafeOverlay;
import com.zyin.zyinhud.mods.TorchAid;
import com.zyin.zyinhud.mods.WeaponSwapper;
import com.zyin.zyinhud.util.Localization;

/**
 * This class is responsible for interacting with the configuration file.
 */
public class ZyinHUDConfig
{
    public static final String CATEGORY_MISCELLANEOUS = "miscellaneous";
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
    public static final String CATEGORY_QUICKDEPOSIT = "quickdeposit";
    public static final String CATEGORY_HEALTHMONITOR = "healthmonitor";
    public static final String CATEGORY_ITEMSELECTOR = "itemselector";
    public static final String CATEGORY_TORCHAID= "torchaid";

    public static Configuration config = null;
    
    /**
     * Loads every value from the configuration file.
     * @param configFile
     */
    public static void LoadConfigSettings(File configFile)
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
        
        config.addCustomCategoryComment(CATEGORY_MISCELLANEOUS, "Other settings not related to any specific functionality.");
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
        config.addCustomCategoryComment(CATEGORY_QUICKDEPOSIT, "Quick Stack allows you to inteligently deposit every item in your inventory quickly into a chest.");
        config.addCustomCategoryComment(CATEGORY_HEALTHMONITOR, "Plays warning beeps when you are low on health.");
        config.addCustomCategoryComment(CATEGORY_ITEMSELECTOR, "Item Selector allows you to conveniently swap your currently selected hotbar item with something in your inventory.");
        config.addCustomCategoryComment(CATEGORY_TORCHAID, "Torch Aid lets you right click while holding an axe, pickaxe, shovel, or when you have nothing in your hand to place a torch.");
        
		// CATEGORY_MISCELLANEOUS
        p = config.get(CATEGORY_MISCELLANEOUS, "UseEnhancedMiddleClick", true);
        p.comment = "Enable/Disable improving the middle click functionality to work with blocks in your inventory.";
        if(loadSettings)
        	Miscellaneous.UseEnhancedMiddleClick = p.getBoolean(true);
        else
        	p.set(Miscellaneous.UseEnhancedMiddleClick);
        
        p = config.get(CATEGORY_MISCELLANEOUS, "UseQuickPlaceSign", false);
        p.comment = "Enable/Disable being able to place a sign with no text by sneaking while placing a sign.";
        if(loadSettings)
        	Miscellaneous.UseQuickPlaceSign = p.getBoolean(false);
        else
        	p.set(Miscellaneous.UseQuickPlaceSign);
        
        p = config.get(CATEGORY_MISCELLANEOUS, "UseUnlimitedSprinting", false);
        p.comment = "Enable/Disable overriding the default sprint behavior and run forever.";
        if(loadSettings)
        	Miscellaneous.UseUnlimitedSprinting = p.getBoolean(false);
        else
        	p.set(Miscellaneous.UseUnlimitedSprinting);
        
        p = config.get(CATEGORY_MISCELLANEOUS, "ShowAnvilRepairs", true);
        p.comment = "Enable/Disable showing the repair count on items while using the anvil.";
        if(loadSettings)
        	Miscellaneous.ShowAnvilRepairs = p.getBoolean(true);
        else
        	p.set(Miscellaneous.ShowAnvilRepairs);
        

        //CATEGORY_INFOLINE
        p = config.get(CATEGORY_INFOLINE, "EnableInfoLine", true);
        p.comment = "Enable/Disable the entire info line in the top left part of the screen. This includes the clock, coordinates, compass, mod status, etc.";
        if(loadSettings)
        	InfoLine.Enabled = p.getBoolean(true);
        else
        	p.set(InfoLine.Enabled);
        
        p = config.get(CATEGORY_INFOLINE, "ShowBiome", false);
        p.comment = "Enable/Disable showing what biome you are in on the info line.";
        if(loadSettings)
        	InfoLine.ShowBiome = p.getBoolean(false);
        else
        	p.set(InfoLine.ShowBiome);
        
        p = config.get(CATEGORY_INFOLINE, "ShowCanSnow", false);
        p.comment = "Enable/Disable showing if it can snow at the player's feet on the info line.";
        if(loadSettings)
        	InfoLine.ShowCanSnow = p.getBoolean(false);
        else
        	p.set(InfoLine.ShowCanSnow);

        p = config.get(CATEGORY_INFOLINE, "InfoLineLocationVertical", 1);
        p.comment = "The vertical position of the info line. 1 is top, 200 is very bottom.";
        if(loadSettings)
        	InfoLine.SetVerticalLocation(p.getInt());
        else
        	p.set(InfoLine.GetVerticalLocation());

        p = config.get(CATEGORY_INFOLINE, "InfoLineLocationHorizontal", 1);
        p.comment = "The horizontal position of the info line. 1 is left, 400 is far right.";
        if(loadSettings)
        	InfoLine.SetHorizontalLocation(p.getInt());
        else
        	p.set(InfoLine.GetHorizontalLocation());
        
        
        
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
        
        p = config.get(CATEGORY_COORDINATES, "CoordinatesChatStringFormat", Coordinates.DefaultChatStringFormat);
        p.comment = "The format used when sending your coordiantes in a chat message by pressing '" + Keyboard.getKeyName(ZyinHUDKeyHandlers.KEY_BINDINGS[1].getKeyCode()) + "'. {x}{y}{z} are replaced with actual coordiantes.";
        if(loadSettings)
        	Coordinates.ChatStringFormat = p.getString();
        else
        	p.set(Coordinates.ChatStringFormat);

        p = config.get(CATEGORY_COORDINATES, "CoordinatesMode", "XZY");
        p.comment = "Sets the Coordinates mode.";
        
        String mode = p.getString();
        
        String test = Localization.get("compass.name");
        
        if(loadSettings)
        	Coordinates.Mode = Coordinates.Modes.GetMode(mode);
        	//Coordinates.Mode = Coordinates.Modes.XZY;
        	//Coordinates.Mode = null;
        else
        	p.set(Coordinates.Mode.name());
        
        
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
        
        p = config.get(CATEGORY_DISTANCEMEASURER, "DistanceMeasurerMode", "OFF");
        p.comment = "Sets the Distance Measurer mode.";
        if(loadSettings)
        	DistanceMeasurer.Mode = DistanceMeasurer.Modes.GetMode(p.getString());
        else
        	p.set(DistanceMeasurer.Mode.name());
        
        
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

        p = config.get(CATEGORY_DURABILITYINFO, "ShowIndividualArmorIcons", true);
        p.comment = "Enable/Disable showing armor peices instead of the big broken armor icon.";
        if(loadSettings)
        	DurabilityInfo.ShowIndividualArmorIcons = p.getBoolean(true);
        else
        	p.set(DurabilityInfo.ShowIndividualArmorIcons);
        
        p = config.get(CATEGORY_DURABILITYINFO, "DurabilityDisplayThresholdForArmor", 0.1);
        p.comment = "Display when armor gets damaged less than this fraction of its durability.";
        if(loadSettings)
        	DurabilityInfo.SetDurabilityDisplayThresholdForArmor((float)p.getDouble(0.1));
        else
        	p.set(DurabilityInfo.GetDurabilityDisplayThresholdForArmor());
        
        p = config.get(CATEGORY_DURABILITYINFO, "DurabilityDisplayThresholdForItem", 0.1);
        p.comment = "Display when an item gets damaged less than this fraction of its durability.";
        if(loadSettings)
        	DurabilityInfo.SetDurabilityDisplayThresholdForItem((float)p.getDouble(0.1));
        else
        	p.set(DurabilityInfo.GetDurabilityDisplayThresholdForItem());
        
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
        
        p = config.get(CATEGORY_DURABILITYINFO, "AutoUnequipArmor", true);
        p.comment = "Enable/Disable automatically unequipping armor before it breaks.";
        if(loadSettings)
        	DurabilityInfo.AutoUnequipArmor = p.getBoolean(true);
        else
        	p.set(DurabilityInfo.AutoUnequipArmor);
        
        p = config.get(CATEGORY_DURABILITYINFO, "AutoUnequipTools", true);
        p.comment = "Enable/Disable automatically unequipping tools before they breaks.";
        if(loadSettings)
        	DurabilityInfo.AutoUnequipTools = p.getBoolean(true);
        else
        	p.set(DurabilityInfo.AutoUnequipTools);
        
        p = config.get(CATEGORY_DURABILITYINFO, "ShowDamageAsPercentage", false);
        p.comment = "Toggle showing damaged items as a percentage or their remaining durability.";
        if(loadSettings)
        	DurabilityInfo.ShowDamageAsPercentage = p.getBoolean(false);
        else
        	p.set(DurabilityInfo.ShowDamageAsPercentage);
        
        p = config.get(CATEGORY_DURABILITYINFO, "UseColoredNumbers", false);
        p.comment = "Toggle using colored numbering.";
        if(loadSettings)
        	DurabilityInfo.UseColoredNumbers = p.getBoolean(true);
        else
        	p.set(DurabilityInfo.UseColoredNumbers);

        p = config.get(CATEGORY_DURABILITYINFO, "DurabilityScale", 1.0);
        p.comment = "How large the durability icons are rendered, 1.0 being the normal size.";
        if(loadSettings)
        	DurabilityInfo.DurabilityScale = (float)p.getDouble(1.0);
        else
        	p.set(DurabilityInfo.DurabilityScale);
        
        
        //CATEGORY_SAFEOVERLAY
        p = config.get(CATEGORY_SAFEOVERLAY, "EnableSafeOverlay", true);
        p.comment = "Enable/Disable the Safe Overlay.";
        if(loadSettings)
        	SafeOverlay.Enabled = p.getBoolean(true);
        else
        	p.set(SafeOverlay.Enabled);
        
        p = config.get(CATEGORY_SAFEOVERLAY, "SafeOverlayMode", "OFF");
        p.comment = "Sets the Safe Overlay mode.";
        if(loadSettings)
        	SafeOverlay.Mode = SafeOverlay.Modes.GetMode(p.getString());
        else
        	p.set(SafeOverlay.Mode.name());
        
        p = config.get(CATEGORY_SAFEOVERLAY, "SafeOverlayDrawDistance", 20);
        p.comment = "How far away unsafe spots should be rendered around the player measured in blocks. This can be changed in game with - + "
        			+ Keyboard.getKeyName(ZyinHUDKeyHandlers.KEY_BINDINGS[8].getKeyCode())+" and + + "
        			+ Keyboard.getKeyName(ZyinHUDKeyHandlers.KEY_BINDINGS[8].getKeyCode())+".";
        if(loadSettings)
        	SafeOverlay.instance.SetDrawDistance(p.getInt(20));
        else
        	p.set(SafeOverlay.instance.GetDrawDistance());
        
        p = config.get(CATEGORY_SAFEOVERLAY, "SafeOverlayTransparency", 0.3);
        p.comment = "The transparency of the unsafe marks. Must be between greater than 0.1 and less than or equal to 1.";
        if(loadSettings)
        	SafeOverlay.instance.SetUnsafeOverlayTransparency((float)p.getDouble(0.3));
        else
        	p.set(SafeOverlay.instance.GetUnsafeOverlayTransparency());
        
        p = config.get(CATEGORY_SAFEOVERLAY, "SafeOverlayDisplayInNether", false);
        p.comment = "Enable/Disable showing unsafe areas in the Nether.";
        if(loadSettings)
        	SafeOverlay.instance.SetDisplayInNether(p.getBoolean(false));
        else
        	p.set(SafeOverlay.instance.GetDisplayInNether());
        
        p = config.get(CATEGORY_SAFEOVERLAY, "SafeOverlaySeeThroughWalls", false);
        p.comment = "Enable/Disable showing unsafe areas through walls. Toggle in game with Ctrl + "+Keyboard.getKeyName(ZyinHUDKeyHandlers.KEY_BINDINGS[8].getKeyCode())+".";
        if(loadSettings)
        	SafeOverlay.instance.SetSeeUnsafePositionsThroughWalls(p.getBoolean(false));
        else
        	p.set(SafeOverlay.instance.GetSeeUnsafePositionsThroughWalls());
        
        
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

        p = config.get(CATEGORY_POTIONTIMERS, "UsePotionColors", true);
        p.comment = "Enable/Disable using the potion type to determine the text color.";
        if(loadSettings)
        	PotionTimers.UsePotionColors = p.getBoolean(true);
        else
        	p.set(PotionTimers.UsePotionColors);

        p = config.get(CATEGORY_POTIONTIMERS, "PotionScale", 1.0);
        p.comment = "How large the potion timers are rendered, 1.0 being the normal size.";
        if(loadSettings)
        	PotionTimers.PotionScale = (float)p.getDouble(1.0);
        else
        	p.set(PotionTimers.PotionScale);

        p = config.get(CATEGORY_POTIONTIMERS, "HidePotionEffectsInInventory", false);
        p.comment = "Enable/Disable hiding the default potion effects when you open your inventory.";
        if(loadSettings)
        	PotionTimers.HidePotionEffectsInInventory = p.getBoolean(false);
        else
        	p.set(PotionTimers.HidePotionEffectsInInventory);
        
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
        
        p = config.get(CATEGORY_PLAYERLOCATOR, "PlayerLocatorMode", "OFF");
        p.comment = "Sets the Player Locator mode.";
        if(loadSettings)
        	PlayerLocator.Mode = PlayerLocator.Modes.GetMode(p.getString());
        else
        	p.set(PlayerLocator.Mode.name());
        
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
        
        p = config.get(CATEGORY_PLAYERLOCATOR, "PlayerLocatorMinViewDistance", 0);
        p.comment = "Stop showing player names when they are this close (distance measured in blocks).";
        if(loadSettings)
        	PlayerLocator.viewDistanceCutoff = p.getInt(0);
        else
        	p.set(PlayerLocator.viewDistanceCutoff);
        
        p = config.get(CATEGORY_PLAYERLOCATOR, "ShowWolves", true);
        p.comment = "Show your tamed wolves in addition to other players.";
        if(loadSettings)
        	PlayerLocator.ShowWolves = p.getBoolean(true);
        else
        	p.set(PlayerLocator.ShowWolves);
        
        p = config.get(CATEGORY_PLAYERLOCATOR, "UseWolfColors", true);
        p.comment = "Use the color of your wolf's collar to colorize their name.";
        if(loadSettings)
        	PlayerLocator.UseWolfColors = p.getBoolean(true);
        else
        	p.set(PlayerLocator.UseWolfColors);
        
        p = config.get(CATEGORY_PLAYERLOCATOR, "ShowWitherSkeletons", false);
        p.comment = "Show wither skeletons in addition to other players.";
        if(loadSettings)
        	PlayerLocator.ShowWitherSkeletons = p.getBoolean(false);
        else
        	p.set(PlayerLocator.ShowWitherSkeletons);
        
        
        //CATEGORY_EATINGAID
        p = config.get(CATEGORY_EATINGAID, "EnableEatingAid", true);
        p.comment = "Enables pressing " + Keyboard.getKeyName(ZyinHUDKeyHandlers.KEY_BINDINGS[3].getKeyCode())
        			+ " to eat food even if it is  in your inventory and not your hotbar.";
        if(loadSettings)
        	EatingAid.Enabled = p.getBoolean(true);
        else
        	p.set(EatingAid.Enabled);
        
        p = config.get(CATEGORY_EATINGAID, "EatGoldenFood", false);
        p.comment = "Enable/Disable using golden apples and golden carrots as food.";
        if(loadSettings)
        	EatingAid.EatGoldenFood = p.getBoolean(false);
        else
        	p.set(EatingAid.EatGoldenFood);
        
        p = config.get(CATEGORY_EATINGAID, "EatRawFood", false);
        p.comment = "Enable/Disable eating raw chicken, beef, and porkchops.";
        if(loadSettings)
        	EatingAid.EatRawFood = p.getBoolean(false);
        else
        	p.set(EatingAid.EatRawFood);
        
        p = config.get(CATEGORY_EATINGAID, "PrioritizeFoodInHotbar", false);
        p.comment = "Use food that is in your hotbar before looking for food in your main inventory.";
        if(loadSettings)
        	EatingAid.PrioritizeFoodInHotbar = p.getBoolean(false);
        else
        	p.set(EatingAid.PrioritizeFoodInHotbar);
        
        p = config.get(CATEGORY_EATINGAID, "EatingAidMode", "INTELLIGENT");
        p.comment = "Sets the Eating Aid mode.";
        if(loadSettings)
        	EatingAid.Mode = EatingAid.Modes.GetMode(p.getString());
        else
        	p.set(EatingAid.Mode.name());
        
        p = config.get(CATEGORY_EATINGAID, "UsePvPSoup", false);
        p.comment = "If you are connected to a Bukkit server that uses PvP Soup or Fast Soup (mushroom stew) with this enabled, Eating Aid will use it instead of other foods.";
        if(loadSettings)
        	EatingAid.UsePvPSoup = p.getBoolean(false);
        else
        	p.set(EatingAid.UsePvPSoup);
        
        
        //CATEGORY_WEAPONSWAP
        p = config.get(CATEGORY_WEAPONSWAP, "EnableWeaponSwap", true);
        p.comment = "Enables pressing " + Keyboard.getKeyName(ZyinHUDKeyHandlers.KEY_BINDINGS[9].getKeyCode())
        			+ " to swap between your sword and bow.";
        if(loadSettings)
        	WeaponSwapper.Enabled = p.getBoolean(true);
        else
        	p.set(WeaponSwapper.Enabled);
        
        /*
        p = config.get(CATEGORY_WEAPONSWAP, "ScanHotbarForWeaponsFromLeftToRight", true);
        p.comment = "Set to false to scan the hotbar for swords and bows from right to left. Only matters if you have multiple swords/bows in your hotbar.";
        if(loadSettings)
        	WeaponSwapper.ScanHotbarForWeaponsFromLeftToRight = p.getBoolean(true);
        else
        	p.set(WeaponSwapper.ScanHotbarForWeaponsFromLeftToRight);
        */
        
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
        
        p = config.get(CATEGORY_ANIMALINFO, "AnimalInfoMode", "OFF");
        p.comment = "Sets the Animal Info mode.";
        if(loadSettings)
        	AnimalInfo.Mode = AnimalInfo.Modes.GetMode(p.getString());
        else
        	p.set(AnimalInfo.Mode.name());
        
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
        
        /* Breeding timers info not available on cliennt in 1.8
        p = config.get(CATEGORY_ANIMALINFO, "ShowBreedingTimers", true);
        p.comment = "Enable/Disable showing a timer counting down to when the animal is ready to breed again.";
        if(loadSettings)
        	AnimalInfo.ShowBreedingTimers = p.getBoolean(true);
        else
        	p.set(AnimalInfo.ShowBreedingTimers);
        */
        
        /*
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
        
        p = config.get(CATEGORY_ANIMALINFO, "ShowBreedingTimerForWolves", true);
        p.comment = "Enable/Disable showing a timer that tells you how long until a wolf can breed again.";
        if(loadSettings)
        	AnimalInfo.ShowBreedingTimerForWolves = p.getBoolean(true);
        else
        	p.set(AnimalInfo.ShowBreedingTimerForWolves);
        
        p = config.get(CATEGORY_ANIMALINFO, "ShowBreedingTimerForOcelots", true);
        p.comment = "Enable/Disable showing a timer that tells you how long until an ocelot can breed again.";
        if(loadSettings)
        	AnimalInfo.ShowBreedingTimerForOcelots = p.getBoolean(true);
        else
        	p.set(AnimalInfo.ShowBreedingTimerForOcelots);
        */
        
        
        //CATEGORY_ENDERPEARLAID
        p = config.get(CATEGORY_ENDERPEARLAID, "EnableEnderPearlAid", true);
        p.comment = "Enables pressing " + Keyboard.getKeyName(ZyinHUDKeyHandlers.KEY_BINDINGS[4].getKeyCode())
        			+ " to use an enderpearl even if it is  in your inventory and not your hotbar.";
        if(loadSettings)
        	EnderPearlAid.Enabled = p.getBoolean(true);
        else
        	p.set(EnderPearlAid.Enabled);
        
        
        //CATEGORY_CLOCK
        p = config.get(CATEGORY_CLOCK, "EnableClock", true);
        p.comment = "Enable/Disable showing the Clock.";
        if(loadSettings)
        	Clock.Enabled = p.getBoolean(true);
        else
        	p.set(Clock.Enabled);
        
        p = config.get(CATEGORY_CLOCK, "ClockMode", "STANDARD");
        p.comment = "Sets the Clock mode.";
        if(loadSettings)
        	Clock.Mode = Clock.Modes.GetMode(p.getString());
        else
        	p.set(Clock.Mode.name());
        
        
        
        //CATEGORY_POTIONAID
        p = config.get(CATEGORY_POTIONAID, "EnablePotionAid", true);
        p.comment = "Enables pressing " + Keyboard.getKeyName(ZyinHUDKeyHandlers.KEY_BINDINGS[6].getKeyCode())
        			+ " to drink a potion even if it is  in your inventory and not your hotbar.";
        if(loadSettings)
        	PotionAid.Enabled = p.getBoolean(true);
        else
        	p.set(PotionAid.Enabled);
        
        
        //CATEGORY_QUICKDEPOSIT
        p = config.get(CATEGORY_QUICKDEPOSIT, "EnableQuickDeposit", true);
        p.comment = "Enables Quick Deposit.";
        if(loadSettings)
        	QuickDeposit.Enabled = p.getBoolean(true);
        else
        	p.set(QuickDeposit.Enabled);

        p = config.get(CATEGORY_QUICKDEPOSIT, "IgnoreItemsInHotbar", false);
        p.comment = "Determines if items in your hotbar will be deposited into chests when '"
        			+ Keyboard.getKeyName(ZyinHUDKeyHandlers.KEY_BINDINGS[7].getKeyCode()) + "' is pressed.";
        if(loadSettings)
        	QuickDeposit.IgnoreItemsInHotbar = p.getBoolean(false);
        else
        	p.set(QuickDeposit.IgnoreItemsInHotbar);

        p = config.get(CATEGORY_QUICKDEPOSIT, "CloseChestAfterDepositing", false);
        p.comment = "Closes the chest GUI after you deposit your items in it. Allows quick and easy depositing of all your items into multiple chests.";
        if(loadSettings)
        	QuickDeposit.CloseChestAfterDepositing = p.getBoolean(false);
        else
        	p.set(QuickDeposit.CloseChestAfterDepositing);

        p = config.get(CATEGORY_QUICKDEPOSIT, "BlacklistTorch", false);
        p.comment = "Stop Quick Deposit from putting torches in chests?";
        if(loadSettings)
        	QuickDeposit.BlacklistTorch = p.getBoolean(false);
        else
        	p.set(QuickDeposit.BlacklistTorch);

        p = config.get(CATEGORY_QUICKDEPOSIT, "BlacklistWeapons", false);
        p.comment = "Stop Quick Deposit from putting swords and bows in chests?";
        if(loadSettings)
        	QuickDeposit.BlacklistWeapons = p.getBoolean(false);
        else
        	p.set(QuickDeposit.BlacklistWeapons);

        p = config.get(CATEGORY_QUICKDEPOSIT, "BlacklistArrow", false);
        p.comment = "Stop Quick Deposit from putting arrows in chests?";
        if(loadSettings)
        	QuickDeposit.BlacklistArrow = p.getBoolean(false);
        else
        	p.set(QuickDeposit.BlacklistArrow);

        p = config.get(CATEGORY_QUICKDEPOSIT, "BlacklistEnderPearl", false);
        p.comment = "Stop Quick Deposit from putting ender pearls in chests?";
        if(loadSettings)
        	QuickDeposit.BlacklistEnderPearl = p.getBoolean(false);
        else
        	p.set(QuickDeposit.BlacklistEnderPearl);

        p = config.get(CATEGORY_QUICKDEPOSIT, "BlacklistFood", false);
        p.comment = "Stop Quick Deposit from putting food in chests?";
        if(loadSettings)
        	QuickDeposit.BlacklistFood = p.getBoolean(false);
        else
        	p.set(QuickDeposit.BlacklistFood);

        p = config.get(CATEGORY_QUICKDEPOSIT, "BlacklistWaterBucket", false);
        p.comment = "Stop Quick Deposit from putting water buckets in chests?";
        if(loadSettings)
        	QuickDeposit.BlacklistWaterBucket = p.getBoolean(false);
        else
        	p.set(QuickDeposit.BlacklistWaterBucket);
        	
        //CATEGORY_ITEMSELECTOR
        p = config.get(CATEGORY_ITEMSELECTOR, "EnableItemSelector", true);
        p.comment = "Enables/Disable using mouse wheel scrolling whilst holding "
                + Keyboard.getKeyName( ZyinHUDKeyHandlers.KEY_BINDINGS[11].getKeyCode() ) + " to swap the selected item with an inventory item.";
        if(loadSettings)
          ItemSelector.Enabled = p.getBoolean(true);
        else
          p.set(ItemSelector.Enabled);

        p = config.get(CATEGORY_ITEMSELECTOR, "ItemSelectorTimeout", ItemSelector.defaultTimeout);
        p.comment = "Specifies how many ticks until the item selector confirms your choice and performs the item swap.";
        if(loadSettings)
          ItemSelector.SetTimeout(p.getInt(ItemSelector.defaultTimeout));
        else
          p.set(ItemSelector.GetTimeout());
        
        p = config.get(CATEGORY_ITEMSELECTOR, "ItemSelectorMode", "ALL");
        p.comment = "Sets the Item Selector mode.";
        if(loadSettings)
        	ItemSelector.Mode = ItemSelector.Modes.GetMode(p.getString());
        else
        	p.set(ItemSelector.Mode.name());

        p = config.get(CATEGORY_ITEMSELECTOR, "ItemSelectorSideButtons", false);
        p.comment = "Enable/disable use of side buttons for item selection.";
        if(loadSettings)
            ItemSelector.UseMouseSideButtons = p.getBoolean(false);
        else
            p.set(ItemSelector.UseMouseSideButtons);

        //CATEGORY_HEALTHMONITOR
        p = config.get(CATEGORY_HEALTHMONITOR, "EnableHealthMonitor", false);
        p.comment = "Enable/Disable using the Health Monitor.";
        if(loadSettings)
        	HealthMonitor.Enabled = p.getBoolean(false);
        else
        	p.set(HealthMonitor.Enabled);
        
        p = config.get(CATEGORY_HEALTHMONITOR, "HealthMonitorMode", "OOT");
        p.comment = "Sets the Health Monitor mode.";
        if(loadSettings)
        	HealthMonitor.Mode = HealthMonitor.Modes.GetMode(p.getString());
        else
        	p.set(HealthMonitor.Mode.name());

        p = config.get(CATEGORY_HEALTHMONITOR, "LowHealthSoundThreshold", 6);
        p.comment = "A sound will start playingwhen you have less than this much health left.";
        if(loadSettings)
        	HealthMonitor.SetLowHealthSoundThreshold(p.getInt(1));
        else
        	p.set(HealthMonitor.GetLowHealthSoundThreshold());

        p = config.get(CATEGORY_HEALTHMONITOR, "PlayFasterNearDeath", false);
        p.comment = "Play the warning sounds quicker the closer you get to dieing.";
        if(loadSettings)
        	HealthMonitor.PlayFasterNearDeath = p.getBoolean(false);
        else
        	p.set(HealthMonitor.PlayFasterNearDeath);
        
        //CATEGORY_TORCHAID
        p = config.get(CATEGORY_TORCHAID, "EnableTorchAid", false);
        p.comment = "Enable/Disable using Torch Aid to help you place torches more easily.";
        if(loadSettings)
        	TorchAid.Enabled = p.getBoolean(false);
        else
        	p.set(TorchAid.Enabled);
        

        config.save();
    }
    
}
