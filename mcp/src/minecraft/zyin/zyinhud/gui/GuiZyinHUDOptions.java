package zyin.zyinhud.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import zyin.zyinhud.ZyinHUD;
import zyin.zyinhud.gui.buttons.GuiAnimalInfoHotkeyButton;
import zyin.zyinhud.gui.buttons.GuiCoordinatesHotkeyButton;
import zyin.zyinhud.gui.buttons.GuiDistanceMeasurerHotkeyButton;
import zyin.zyinhud.gui.buttons.GuiEatingAidHotkeyButton;
import zyin.zyinhud.gui.buttons.GuiEnderPearlAidHotkeyButton;
import zyin.zyinhud.gui.buttons.GuiHotkeyButton;
import zyin.zyinhud.gui.buttons.GuiNumberSlider;
import zyin.zyinhud.gui.buttons.GuiPlayerLocatorHotkeyButton;
import zyin.zyinhud.gui.buttons.GuiPotionAidHotkeyButton;
import zyin.zyinhud.gui.buttons.GuiQuickDepositHotkeyButton;
import zyin.zyinhud.gui.buttons.GuiSafeOverlayHotkeyButton;
import zyin.zyinhud.gui.buttons.GuiWeaponSwapperHotkeyButton;
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
import zyin.zyinhud.mods.QuickDeposit;
import zyin.zyinhud.mods.SafeOverlay;
import zyin.zyinhud.mods.WeaponSwapper;
import zyin.zyinhud.util.FontCodes;
import zyin.zyinhud.util.Localization;

/**
 * This is the options GUI which is used to change any configurable setting while in game.
 * <p>
 * The tabs on the left side represent the various mods and more can be added using the
 * tabbedButtonNames and tabbedButtonIDs variables, then adding functionality to the actionPerformed()
 * method to draw additional buttons specific to the mod.
 * <p>
 * A helper class, GuiHotkeyButton, is to help us assign hotkeys. An additional class is needed but is
 * very lightweight (see existing examples)
 * <p>
 * There are 4 types of buttons we use in this GUI:<br>
 * 1) "Enabled" button<br>
 * 2) "Mode" button<br>
 * 3) "Boolean" button<br>
 * 4) "Slider" button<br>
 * See existing examples on how to use these.
 * <p>
 * We are able to access this screen by using a hotkey (Ctrl + Alt + Z), or navigating through the
 * default options window. We put an additional button into the Options window by using the GUITickHandler
 * class and replacing the normal GuiOptions class with our custom OverrideGuiOptions class.
 * <p>
 * In order to get the GuiNumberSlider to work when we click and drag it, we override and modify 3 methods:
 * mouseClicked(), mouseMovedOrUp(), and actionPerformed_MouseUp().
 */
public class GuiZyinHUDOptions extends GuiScreen
{
	public static String Hotkey;
    public static final String HotkeyDescription = "Zyin's HUD: Options";
	
	protected GuiScreen parentGuiScreen;
    
    /** The title string that is displayed in the top-center of the screen. */
    protected String screenTitle = "Zyin's HUD Settings";

    /** The button that was just pressed. */
    protected GuiButton selectedButton;

    protected String[] tabbedButtonNames = {
    		Localization.get("infoline.name"),
    		Localization.get("clock.name"),
    		Localization.get("coordinates.name"),
    		Localization.get("compass.name"),
    		Localization.get("fps.name"),
    		Localization.get("distancemeasurer.name"),
    		Localization.get("safeoverlay.name"),
    		Localization.get("playerlocator.name"),
    		Localization.get("animalinfo.name"),
    		Localization.get("potiontimers.name"),
    		Localization.get("durabilityinfo.name"),
    		Localization.get("enderpearlaid.name"),
    		Localization.get("eatingaid.name"),
    		Localization.get("potionaid.name"),
    		Localization.get("weaponswapper.name"),
    		Localization.get("quickdeposit.name")};
    
    protected int[] tabbedButtonIDs = {
    		100,
    		200,
    		300,
    		400,
    		500,
    		600,
    		700,
    		800,
    		900,
    		1000,
    		1100,
    		1200,
    		1300,
    		1400,
    		1500,
    		1600};
    
    /** The current tab page. It is 0 indexed. */
    protected static int tabbedPage = 0;
    
    /** The amount of items shown on each page. */
    protected static int tabbedPageSize = 11;
    protected static int tabbedMaxPages;

    protected int tabbedButtonX;
    protected int tabbedButtonY;
    protected int tabbedButtonWidth;
    protected int tabbedButtonHeight;
    protected int tabbedButtonSpacing;
    
    protected int buttonX_column1;
	protected int buttonX_column2;
	protected int buttonY;
	protected int buttonWidth_half;
	protected int buttonWidth_full;
	protected int buttonHeight;
	protected int buttonSpacing;

	protected int pagingButtonWidth;
	protected int pagingButtonHeight;
    
    private GuiHotkeyButton currentlySelectedHotkeyButton;
    private GuiButton currentlySelectedTabButton = null;
    private String currentlySelectedTabButtonColor = FontCodes.YELLOW;
    

    public GuiZyinHUDOptions(GuiScreen parentGuiScreen)
    {
        this.parentGuiScreen = parentGuiScreen;
        tabbedMaxPages = (int) Math.ceil((double)(tabbedButtonNames.length)/tabbedPageSize);
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        //button variables
    	buttonSpacing = 2;
    	buttonWidth_half = 130;
    	buttonWidth_full = buttonWidth_half*2 + buttonSpacing*2;
    	
        //tabbed button variables
        tabbedButtonSpacing = 0;
        tabbedButtonWidth = 130;
        tabbedButtonHeight = 14;
        tabbedButtonX = width/2 - (tabbedButtonWidth + buttonWidth_full)/2;
        tabbedButtonY = (int)(height*0.16);

        //button variables
    	buttonHeight = 20;
        buttonX_column1 = tabbedButtonWidth + tabbedButtonX + buttonSpacing*2;
    	buttonX_column2 = buttonX_column1 + buttonWidth_half + buttonSpacing*2;
    	buttonY = (int)(height*0.17);
    	
    	//paging buttons
    	pagingButtonWidth = 15;
    	pagingButtonHeight = 14;
    	
        screenTitle = Localization.get("gui.options.title");
        
        DrawAllButtons();
    }

    protected void DrawAllButtons()
    {
        buttonList.clear();
        currentlySelectedTabButton = null;
        DrawMiscButtons();
        DrawTabbedButtons();
    }
    
    /**
     * Other misc text that is rendered on various screens
     */
    private void DrawMiscText()
    {
    	if(currentlySelectedTabButton == null)
    	{
        	int x = (int) (width - width*0.05);
        	int y = (int) (height / 6 + 158);
        	int lineHeight = 10;
        	
        	String[] text = {
        			ZyinHUD.GetName(),
        			"v." + ZyinHUD.GetVersion(),
        			"",
        			"To reset values to their default",
        			"setting, delete it in the configuration",
        			"file at /.minecraft/config/ZyinHUD.cfg",
        			"",
        			"Found a bug? Want an enhancement? Submit",
        			"it to my GitHub at github.com/Zyin055/zyinhud"
        			};
        	
        	for(int i = 0; i < text.length; i++)
        	{
            	int strWidth = fontRenderer.getStringWidth(text[i]);
        		int xOffset = -strWidth;
        		int yOffset = -(lineHeight * (text.length - i));

                GL11.glEnable(GL11.GL_BLEND);	//for transparent text
            	fontRenderer.drawStringWithShadow(text[i], x + xOffset, y + yOffset, 0x22ffffff);
                GL11.glDisable(GL11.GL_BLEND);
        	}
    	}
    	else if(currentlySelectedTabButton.id == 1600)	//Quick Deposit
    	{
    		String text = FontCodes.UNDERLINE + Localization.get("quickdeposit.options.blacklist");
    		
        	int x = buttonX_column2 + buttonWidth_half/2 - mc.fontRenderer.getStringWidth(text)/2;
        	int y = buttonY - buttonHeight/2 - mc.fontRenderer.FONT_HEIGHT/2 + 3;
        	
        	fontRenderer.drawStringWithShadow(text, x, y, 0xffffff);
    	}
    }
    private void DrawMiscButtons()
    {
    	//Save button
    	buttonList.add(new GuiButton(1, width / 2 - 100, height / 6 + 168, Localization.get("gui.options.saveandexit")));
    	
    }
    private void DrawTabbedButtons()
    {
		//make the paging controls
		GuiButton pagingPrev = new GuiButton(10, tabbedButtonX, tabbedButtonY-pagingButtonHeight, pagingButtonWidth, pagingButtonHeight, "<");
		GuiButton pagingNext = new GuiButton(11, tabbedButtonX+tabbedButtonWidth-pagingButtonWidth+1, tabbedButtonY-pagingButtonHeight, pagingButtonWidth, pagingButtonHeight, ">");
    	
		if(tabbedPage == 0)
    		pagingPrev.enabled = false;
    	else if(tabbedPage == tabbedMaxPages-1)
    		pagingNext.enabled = false;
    	
		//add the paging controls
		buttonList.add(pagingPrev);
    	buttonList.add(pagingNext);

    	int Y = tabbedButtonY;
    	
    	//make the tabbed buttons
    	for(int i = 0; i < tabbedPageSize; i++)
    	{
    		int index = (tabbedPage * tabbedPageSize + i);
    		if(index >= tabbedButtonIDs.length)
    			break;
    		int id = tabbedButtonIDs[index];
    		String buttonName = tabbedButtonNames[tabbedPage * tabbedPageSize + i];
    		
    		buttonList.add(new GuiButton(id, tabbedButtonX, Y, tabbedButtonWidth, tabbedButtonHeight, buttonName));
    		
    		Y += tabbedButtonHeight;
    	}
    }
    
    private void DrawInfoLineButtons()
    {
    	int Y = buttonY;
    	buttonList.add(new GuiButton(101, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Enabled(InfoLine.Enabled)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(102, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("infoline.options.showbiome", InfoLine.ShowBiome)));
    	
    }
    private void DrawClockButtons()
    {
    	int Y = buttonY;
    	buttonList.add(new GuiButton(201, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Enabled(Clock.Enabled)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(202, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Mode(ZyinHUD.CATEGORY_CLOCK, Clock.Mode, Clock.NumberOfModes)));
    	
    }
    private void DrawCoordinatesButtons()
    {
    	int Y = buttonY;
    	buttonList.add(new GuiButton(301, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Enabled(Coordinates.Enabled)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiCoordinatesHotkeyButton(303, buttonX_column1, Y, buttonWidth_half, buttonHeight, Coordinates.Hotkey));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(304, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Mode(ZyinHUD.CATEGORY_COORDINATES, Coordinates.Mode, Coordinates.NumberOfModes)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(302, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("coordinates.options.useycoordinatecolors", Coordinates.UseYCoordinateColors)));
    	
    }
    private void DrawCompassButtons()
    {
    	int Y = buttonY;
    	buttonList.add(new GuiButton(401, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Enabled(Compass.Enabled)));
    	
    }
    private void DrawFPSButtons()
    {
    	int Y = buttonY;
    	buttonList.add(new GuiButton(501, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Enabled(Fps.Enabled)));
    	
    }
    private void DrawDistanceMeasurerButtons()
    {
    	int Y = buttonY;
    	buttonList.add(new GuiButton(601, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Enabled(DistanceMeasurer.Enabled)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiDistanceMeasurerHotkeyButton(602, buttonX_column1, Y, buttonWidth_half, buttonHeight, DistanceMeasurer.Hotkey));
    	
    }
    private void DrawSafeOverlayButtons()
    {
    	int Y = buttonY;
    	buttonList.add(new GuiButton(701, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Enabled(SafeOverlay.Enabled)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiSafeOverlayHotkeyButton(702, buttonX_column1, Y, buttonWidth_half, buttonHeight, SafeOverlay.Hotkey));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiNumberSlider(703, buttonX_column1, Y, buttonWidth_half, buttonHeight, Localization.get("safeoverlay.options.drawdistance"), SafeOverlay.minDrawDistance, SafeOverlay.maxDrawDistance, SafeOverlay.instance.getDrawDistance(), true));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiNumberSlider(704, buttonX_column1, Y, buttonWidth_half, buttonHeight, Localization.get("safeoverlay.options.transparency"), SafeOverlay.instance.getUnsafeOverlayMinTransparency(), SafeOverlay.instance.getUnsafeOverlayMaxTransparency(), SafeOverlay.instance.getUnsafeOverlayTransparency(), false));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(705, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("safeoverlay.options.displayinnether", SafeOverlay.instance.getDisplayInNether())));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(706, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("safeoverlay.options.seethroughwalls", SafeOverlay.instance.getSeeUnsafePositionsThroughWalls())));
    	
    }
    private void DrawPlayerLocatorButtons()
    {
    	int Y = buttonY;
    	buttonList.add(new GuiButton(801, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Enabled(PlayerLocator.Enabled)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiPlayerLocatorHotkeyButton(802, buttonX_column1, Y, buttonWidth_half, buttonHeight, PlayerLocator.Hotkey));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiNumberSlider(803, buttonX_column1, Y, buttonWidth_half, buttonHeight, Localization.get("playerlocator.options.minviewdistance"), PlayerLocator.minViewDistanceCutoff, PlayerLocator.maxViewDistanceCutoff, PlayerLocator.viewDistanceCutoff, true));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(804, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("playerlocator.options.showdistancetoplayers", PlayerLocator.ShowDistanceToPlayers)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(805, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("playerlocator.options.showplayerhealth", PlayerLocator.ShowPlayerHealth)));
    	
    }
    private void DrawAnimalInfoButtons()
    {
    	int Y = buttonY;
    	buttonList.add(new GuiButton(901, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Enabled(AnimalInfo.Enabled)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiAnimalInfoHotkeyButton(902, buttonX_column1, Y, buttonWidth_half, buttonHeight, AnimalInfo.Hotkey));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiNumberSlider(903, buttonX_column1, Y, buttonWidth_half, buttonHeight, Localization.get("animalinfo.options.maxviewdistance"), AnimalInfo.minViewDistanceCutoff, AnimalInfo.maxViewDistanceCutoff, AnimalInfo.viewDistanceCutoff, true));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(907, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("animalinfo.options.showtextbackground", AnimalInfo.ShowTextBackgrounds)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiNumberSlider(904, buttonX_column1, Y, buttonWidth_half, buttonHeight, Localization.get("animalinfo.options.numdecimalsdisplayed"), AnimalInfo.minNumberOfDecimalsDisplayed, AnimalInfo.maxNumberOfDecimalsDisplayed, AnimalInfo.GetNumberOfDecimalsDisplayed(), true));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(905, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("animalinfo.options.showhorsestatsonf3menu", AnimalInfo.ShowHorseStatsOnF3Menu)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(906, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("animalinfo.options.showhorsestatsoverlay", AnimalInfo.ShowHorseStatsOverlay)));
    	
    	Y = buttonY;
    	buttonList.add(new GuiButton(916, buttonX_column2, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("animalinfo.options.showbreedingicons", AnimalInfo.ShowBreedingIcons)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(917, buttonX_column2, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("animalinfo.options.showbreedingtimers", AnimalInfo.ShowBreedingTimers)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(910, buttonX_column2, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("animalinfo.options.showbreedinghorse", AnimalInfo.ShowBreedingTimerForHorses)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(911, buttonX_column2, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("animalinfo.options.showbreedingvillagers", AnimalInfo.ShowBreedingTimerForVillagers)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(912, buttonX_column2, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("animalinfo.options.showbreedingcow", AnimalInfo.ShowBreedingTimerForCows)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(913, buttonX_column2, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("animalinfo.options.showbreedingsheep", AnimalInfo.ShowBreedingTimerForSheep)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(914, buttonX_column2, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("animalinfo.options.showbreedingpig", AnimalInfo.ShowBreedingTimerForPigs)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(915, buttonX_column2, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("animalinfo.options.showbreedingchicken", AnimalInfo.ShowBreedingTimerForChickens)));
    	
    }
    private void DrawPotionTimerButtons()
    {
    	int Y = buttonY;
    	buttonList.add(new GuiButton(1001, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Enabled(PotionTimers.Enabled)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(1002, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("potiontimers.options.showpotionicons", PotionTimers.ShowPotionIcons)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(1005, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("potiontimers.options.usepotioncolors", PotionTimers.UsePotionColors)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(1007, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("potiontimers.options.hidepotioneffectsininventory", PotionTimers.HidePotionEffectsInInventory)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiNumberSlider(1006, buttonX_column1, Y, buttonWidth_half, buttonHeight, Localization.get("potiontimers.options.potionscale"), 1.0f, 4.0f, PotionTimers.PotionScale, false));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiNumberSlider(1003, buttonX_column1, Y, buttonWidth_full, buttonHeight, Localization.get("potiontimers.options.offsetx"), 1, width - 25, PotionTimers.GetHorizontalLocation(), true));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiNumberSlider(1004, buttonX_column1, Y, buttonWidth_full, buttonHeight, Localization.get("potiontimers.options.offsety"), 0, height - 10, PotionTimers.GetVerticalLocation(), true));
    	
    }
    private void DrawDurabilityInfoButtons()
    {
    	int Y = buttonY;
    	buttonList.add(new GuiButton(1101, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Enabled(DurabilityInfo.Enabled)));
    	Y += buttonHeight + buttonSpacing;
    	
    	buttonList.add(new GuiButton(1102, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("durabilityinfo.options.showarmordurability", DurabilityInfo.ShowArmorDurability)));
    	buttonList.add(new GuiNumberSlider(1103, buttonX_column2, Y, buttonWidth_half, buttonHeight, Localization.get("durabilityinfo.options.armordurabilitythreshold"), 0f, 1f, DurabilityInfo.GetDurabilityDisplayThresholdForArmor(), false));
    	Y += buttonHeight + buttonSpacing;
    	
    	buttonList.add(new GuiButton(1105, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("durabilityinfo.options.showitemdurability", DurabilityInfo.ShowItemDurability)));
    	buttonList.add(new GuiNumberSlider(1106, buttonX_column2, Y, buttonWidth_half, buttonHeight, Localization.get("durabilityinfo.options.itemdurabilitythreshold"), 0f, 1f, DurabilityInfo.GetDurabilityDisplayThresholdForItem(), false));
    	Y += buttonHeight + buttonSpacing;
    	
    	
    	buttonList.add(new GuiButton(1104, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("durabilityinfo.options.showindividualarmoricons", DurabilityInfo.ShowIndividualArmorIcons)));
    	buttonList.add(new GuiButton(1110, buttonX_column2, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("durabilityinfo.options.showdamageaspercent", DurabilityInfo.ShowDamageAsPercentage)));
    	Y += buttonHeight + buttonSpacing;
    	
    	
    	buttonList.add(new GuiNumberSlider(1107, buttonX_column1, Y, buttonWidth_half, buttonHeight, Localization.get("durabilityinfo.options.updatefrequency"), 100, 4000, DurabilityInfo.DurabilityUpdateFrequency, true));
    	Y += buttonHeight + buttonSpacing;
    	
    	buttonList.add(new GuiNumberSlider(1108, buttonX_column1, Y, buttonWidth_full, buttonHeight, Localization.get("durabilityinfo.options.offsetx"), 0, width - DurabilityInfo.toolX, DurabilityInfo.durabalityLocX, true));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiNumberSlider(1109, buttonX_column1, Y, buttonWidth_full, buttonHeight, Localization.get("durabilityinfo.options.offsety"), 0, height - DurabilityInfo.toolY, DurabilityInfo.durabalityLocY, true));
    	
    }
    private void DrawEnderPearlAidButtons()
    {
    	int Y = buttonY;
    	buttonList.add(new GuiButton(1201, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Enabled(EnderPearlAid.Enabled)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiEnderPearlAidHotkeyButton(1202, buttonX_column1, Y, buttonWidth_half, buttonHeight, EnderPearlAid.Hotkey));
    	
    }
    private void DrawEatingAidButtons()
    {
    	int Y = buttonY;
    	buttonList.add(new GuiButton(1301, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Enabled(EatingAid.Enabled)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiEatingAidHotkeyButton(1302, buttonX_column1, Y, buttonWidth_half, buttonHeight, EatingAid.Hotkey));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(1303, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Mode(ZyinHUD.CATEGORY_EATINGAID, EatingAid.Mode, EatingAid.NumberOfModes)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(1304, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("eatingaid.options.eatgoldenfood", EatingAid.EatGoldenFood)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(1305, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("eatingaid.options.prioritizefoodinhotbar", EatingAid.PrioritizeFoodInHotbar)));
    }
    private void DrawPotionAidButtons()
    {
    	int Y = buttonY;
    	buttonList.add(new GuiButton(1401, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Enabled(PotionAid.Enabled)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiPotionAidHotkeyButton(1402, buttonX_column1, Y, buttonWidth_half, buttonHeight, PotionAid.Hotkey));
    	
    }
    private void DrawWeaponSwapButtons()
    {
    	int Y = buttonY;
    	buttonList.add(new GuiButton(1501, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Enabled(WeaponSwapper.Enabled)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiWeaponSwapperHotkeyButton(1502, buttonX_column1, Y, buttonWidth_half, buttonHeight, WeaponSwapper.Hotkey));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(1503, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("weaponswapper.options.scanhotbarforweaponsfromlefttoright", WeaponSwapper.ScanHotbarForWeaponsFromLeftToRight)));
    }
    private void DrawQuickDepositButtons()
    {
    	int Y = buttonY;
    	buttonList.add(new GuiButton(1601, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Enabled(QuickDeposit.Enabled)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiQuickDepositHotkeyButton(1602, buttonX_column1, Y, buttonWidth_half, buttonHeight, QuickDeposit.Hotkey));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(1603, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("quickdeposit.options.ignoreitemsinhotbar", QuickDeposit.IgnoreItemsInHotbar)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(1604, buttonX_column1, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("quickdeposit.options.closechestafterdepositing", QuickDeposit.CloseChestAfterDepositing)));
    	
    	Y = buttonY;
    	//Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(1605, buttonX_column2, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("quickdeposit.options.blacklisttorch", QuickDeposit.BlacklistTorch)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(1606, buttonX_column2, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("quickdeposit.options.blacklistarrow", QuickDeposit.BlacklistArrow)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(1607, buttonX_column2, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("quickdeposit.options.blacklistfood", QuickDeposit.BlacklistFood)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(1608, buttonX_column2, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("quickdeposit.options.blacklistenderpearl", QuickDeposit.BlacklistEnderPearl)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(1609, buttonX_column2, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("quickdeposit.options.blacklistwaterbucket", QuickDeposit.BlacklistWaterBucket)));
    	Y += buttonHeight + buttonSpacing;
    	buttonList.add(new GuiButton(1610, buttonX_column2, Y, buttonWidth_half, buttonHeight, GetButtonLabel_Boolean("quickdeposit.options.blacklistclockcompass", QuickDeposit.BlacklistClockCompass)));
    }
    
    /**
     * Helper method to get the text for a button that toggles between modes for a mod.
     * <p>
     * The mod must have names for each mode in the localization file.
     * <p>
     * For example, for Clock there is:<br>
     * clock.mode.0=Standard<br>
     * clock.mode.1=Countdown<br>
     * @param modName e.x. "clock", "safeoverlay", "eatingaid"
     * @param mode the current mode the mod is in
     * @param numModes the maximum amount of possible modes
     * @return a String to be used as the button label
     */
    private static String GetButtonLabel_Mode(String modName, int mode, int numModes)
    {
    	for(int i = 0; i < numModes; i++)
    	{
    		if(mode == i)
    			return Localization.get("gui.options.mode")+Localization.get(modName+".mode."+i);
    	}
    	return Localization.get("gui.options.mode")+"???";
    }
    
    /**
     * Helper method to get the text for a button that toggles the mod on and off.
     * @param enabled the current enabled/disabled boolean status of the mod
     * @return a color coded String to be used as the button label
     */
    private static String GetButtonLabel_Enabled(boolean enabled)
    {
    	String color;
    	if(enabled)
    		color = FontCodes.GREEN;
    	else
    		color = FontCodes.RED;
    	
		return Localization.get("gui.options.enabled")+color+enabled+FontCodes.WHITE;
    }
    
    /**
     * Helper method to get the text for a button that toggles between true and false.
     * @param localizationString the text from the localization file to be used as the label for the button
     * @param bool boolean value to display
     * @return a String to be used as the button label
     */
    private static String GetButtonLabel_Boolean(String localizationString, boolean bool)
    {
		return Localization.get(localizationString) + bool;
    }
    
    

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
    	//play a sound and fire the actionPerformed() method when a button is left clicked
        if (mouseButton == 0)
        {
            for (int l = 0; l < this.buttonList.size(); ++l)
            {
                GuiButton guibutton = (GuiButton)this.buttonList.get(l);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY))
                {
                    selectedButton = guibutton;
                    mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
                    actionPerformed(guibutton);
                }
            }
        }
    }

    /**
     * Called when the mouse is moved or a mouse button is released.  Signature: (mouseX, mouseY, which) which==-1 is
     * mouseMove, which==0 or which==1 is mouseUp
     */
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which)
    {
        if (this.selectedButton != null && which == 0)
        {
            selectedButton.mouseReleased(mouseX, mouseY);
            
            //this line was changed from the original GuiScreen class in order to make our
            //GuiNumberSlider's to update their values properly when sliding the slider.
            actionPerformed_MouseUp(selectedButton);
            
            selectedButton = null;
        }
    }

    /**
     * Fired when a button is hovered over when the mouse is releated.
     */
    protected void actionPerformed_MouseUp(GuiButton button)
    {
    	if(button instanceof GuiNumberSlider)
    	{
    		//in order to have our values updated when the user clicks, drags, then releases the mouse,
    		//we fake a click on mouseup
    		actionPerformed(button);
    	}
    }
    
    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     * In this method we handle every buttons action.
     */
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id % 100 == 0)	//clicked one of the tabs
            {
            	DrawAllButtons();
            	
            	GuiButton clickedButton = GetButtonById(button.id);
            	if(clickedButton != null)
            	{
                    currentlySelectedTabButton = clickedButton;
                    currentlySelectedHotkeyButton = null;
                    
                    //show this button as selected by changing it's color
            		clickedButton.displayString = currentlySelectedTabButtonColor + clickedButton.displayString;
            	}
            }

            /////////////////////////////////////////////////////////////////////////
            // Misc
            /////////////////////////////////////////////////////////////////////////
            
            if (button.id == 1)	//Save and Exit
            {
            	ZyinHUD.SaveConfigSettings();
                mc.displayGuiScreen(parentGuiScreen);
            }

            /////////////////////////////////////////////////////////////////////////
            // Paging
            /////////////////////////////////////////////////////////////////////////
            
            else if (button.id == 10)	//Previous Page
            {
                DecrementTabbedPage();
            }
            
            else if (button.id == 11)	//Next Page
            {
                IncrementTabbedPage();
            }
            
            /////////////////////////////////////////////////////////////////////////
            // Info Line
            /////////////////////////////////////////////////////////////////////////
            
            else if (button.id == 100)
            {
            	screenTitle = Localization.get("infoline.name");
            	DrawInfoLineButtons();
            }
            else if (button.id == 101)	//Enable/Disable
            {
            	InfoLine.ToggleEnabled();
            	button.displayString = GetButtonLabel_Enabled(InfoLine.Enabled);
            }
            else if (button.id == 102)	//Show Biome
            {
            	InfoLine.ToggleShowBiome();
            	button.displayString = GetButtonLabel_Boolean("infoline.options.showbiome", InfoLine.ShowBiome);
            }
            
            /////////////////////////////////////////////////////////////////////////
            // Clock
            /////////////////////////////////////////////////////////////////////////
            
            else if (button.id == 200)
            {
            	screenTitle = Localization.get("clock.name");
            	DrawClockButtons();
            }
            else if (button.id == 201)	//Enable/Disable
            {
            	Clock.ToggleEnabled();
            	button.displayString = GetButtonLabel_Enabled(Clock.Enabled);
            }
            else if (button.id == 202)	//Mode
            {
            	Clock.ToggleMode();
            	button.displayString = GetButtonLabel_Mode(ZyinHUD.CATEGORY_CLOCK, Clock.Mode, Clock.NumberOfModes);
            }

            /////////////////////////////////////////////////////////////////////////
            // Coordinates
            /////////////////////////////////////////////////////////////////////////
            
            else if (button.id == 300)
            {
            	screenTitle = Localization.get("coordinates.name");
            	DrawCoordinatesButtons();
            }
            else if (button.id == 301)	//Enable/Disable
            {
            	Coordinates.ToggleEnabled();
            	button.displayString = GetButtonLabel_Enabled(Coordinates.Enabled);
            }
            else if (button.id == 302)	//Y Colors
            {
            	Coordinates.ToggleUseYCoordinateColors();
            	button.displayString = GetButtonLabel_Boolean("coordinates.options.useycoordinatecolors", Coordinates.UseYCoordinateColors);
            }
            else if (button.id == 303)	//Hotkey
            {
            	HotkeyButtonClicked((GuiHotkeyButton)button);
            }
            else if (button.id == 304)	//Mode
            {
            	Coordinates.ToggleMode();
            	button.displayString = GetButtonLabel_Mode(ZyinHUD.CATEGORY_COORDINATES, Coordinates.Mode, Coordinates.NumberOfModes);
            }

            /////////////////////////////////////////////////////////////////////////
            // Compass
            /////////////////////////////////////////////////////////////////////////
            
            else if (button.id == 400)
            {
            	screenTitle = Localization.get("compass.name");
            	DrawCompassButtons();
            }
            else if (button.id == 401)	//Enable/Disable
            {
            	Compass.ToggleEnabled();
            	button.displayString = GetButtonLabel_Enabled(Compass.Enabled);
            	
            }

            /////////////////////////////////////////////////////////////////////////
            // FPS
            /////////////////////////////////////////////////////////////////////////
            
            else if (button.id == 500)
            {
            	screenTitle = Localization.get("fps.name");
            	DrawFPSButtons();
            }
            else if (button.id == 501)	//Enable/Disable
            {
            	Fps.ToggleEnabled();
            	button.displayString = GetButtonLabel_Enabled(Fps.Enabled);
            }

            /////////////////////////////////////////////////////////////////////////
            // Distance Measurer
            /////////////////////////////////////////////////////////////////////////
            
            else if (button.id == 600)
            {
            	screenTitle = Localization.get("distancemeasurer.name");
            	DrawDistanceMeasurerButtons();
            }
            else if (button.id == 601)	//Enable/Disable
            {
            	DistanceMeasurer.ToggleEnabled();
            	button.displayString = GetButtonLabel_Enabled(DistanceMeasurer.Enabled);
            }
            else if (button.id == 602)	//Hotkey
            {
            	HotkeyButtonClicked((GuiHotkeyButton)button);
            }
            
            /////////////////////////////////////////////////////////////////////////
            // Safe Overlay
            /////////////////////////////////////////////////////////////////////////
            
            else if (button.id == 700)
            {
            	screenTitle = Localization.get("safeoverlay.name");
            	DrawSafeOverlayButtons();
            }
            else if (button.id == 701)	//Enable/Disable
            {
            	SafeOverlay.ToggleEnabled();
            	button.displayString = GetButtonLabel_Enabled(SafeOverlay.Enabled);
            }
            else if (button.id == 702)	//Hotkey
            {
            	HotkeyButtonClicked((GuiHotkeyButton)button);
            }
            else if (button.id == 703)	//Draw distance slider
            {
            	int value = ((GuiNumberSlider)button).GetValueAsInteger();
            	SafeOverlay.instance.setDrawDistance(value);
            }
            else if (button.id == 704)	//Draw distance slider
            {
            	float value = ((GuiNumberSlider)button).GetValueAsFloat();
            	SafeOverlay.instance.setUnsafeOverlayTransparency(value);
            }
            else if (button.id == 705)	//Show in Nether
            {
            	SafeOverlay.instance.toggleDisplayInNether();
            	button.displayString = GetButtonLabel_Boolean("safeoverlay.options.displayinnether", SafeOverlay.instance.getDisplayInNether());
            }
            else if (button.id == 706)	//X-ray
            {
            	SafeOverlay.instance.toggleSeeUnsafePositionsThroughWalls();
            	button.displayString = GetButtonLabel_Boolean("safeoverlay.options.seethroughwalls", SafeOverlay.instance.getSeeUnsafePositionsThroughWalls());
            }
            
            /////////////////////////////////////////////////////////////////////////
            // Player Locator
            /////////////////////////////////////////////////////////////////////////

            else if (button.id == 800)
            {
            	screenTitle = Localization.get("playerlocator.name");
            	DrawPlayerLocatorButtons();
            }
            else if (button.id == 801)	//Enable/Disable
            {
            	PlayerLocator.ToggleEnabled();
            	button.displayString = GetButtonLabel_Enabled(PlayerLocator.Enabled);
            }
            else if (button.id == 802)	//Hotkey
            {
            	HotkeyButtonClicked((GuiHotkeyButton)button);
            }
            else if (button.id == 803)	//Min view distance slider
            {
            	int value = ((GuiNumberSlider)button).GetValueAsInteger();
            	PlayerLocator.viewDistanceCutoff = value;
            }
            else if (button.id == 804)	//Show distance to players
            {
            	PlayerLocator.ToggleShowDistanceToPlayers();
            	button.displayString = GetButtonLabel_Boolean("playerlocator.options.showdistancetoplayers", PlayerLocator.ShowDistanceToPlayers);
            }
            else if (button.id == 805)	//Show players health
            {
            	PlayerLocator.ToggleShowPlayerHealth();
            	button.displayString = GetButtonLabel_Boolean("playerlocator.options.showplayerhealth", PlayerLocator.ShowPlayerHealth);
            }
            
            /////////////////////////////////////////////////////////////////////////
            // Horse Info
            /////////////////////////////////////////////////////////////////////////
            
            else if (button.id == 900)
            {
            	screenTitle = Localization.get("animalinfo.name");
            	DrawAnimalInfoButtons();
            }
            else if (button.id == 901)	//Enable/Disable
            {
            	AnimalInfo.ToggleEnabled();
            	button.displayString = GetButtonLabel_Enabled(AnimalInfo.Enabled);
            }
            else if (button.id == 902)	//Hotkey
            {
            	HotkeyButtonClicked((GuiHotkeyButton)button);
            }
            else if (button.id == 903)	//Min view distance slider
            {
            	int value = ((GuiNumberSlider)button).GetValueAsInteger();
            	AnimalInfo.viewDistanceCutoff = value;
            }
            else if (button.id == 904)	//Decimal slider
            {
            	int value = ((GuiNumberSlider)button).GetValueAsInteger();
            	AnimalInfo.SetNumberOfDecimalsDisplayed(value);
            }
            else if (button.id == 905)	//Show on F3 menu
            {
            	AnimalInfo.ToggleShowHorseStatsOnF3Menu();
            	button.displayString = GetButtonLabel_Boolean("animalinfo.options.showhorsestatsonf3menu", AnimalInfo.ShowHorseStatsOnF3Menu);
            }
            else if (button.id == 906)	//Show on F3 menu
            {
            	AnimalInfo.ToggleShowHorseStatsOverlay();
            	button.displayString = GetButtonLabel_Boolean("animalinfo.options.showhorsestatsoverlay", AnimalInfo.ShowHorseStatsOverlay);
            }
            else if (button.id == 907)	//Show text backgrounds
            {
            	AnimalInfo.ToggleShowTextBackgrounds();
            	button.displayString = GetButtonLabel_Boolean("animalinfo.options.showtextbackground", AnimalInfo.ShowTextBackgrounds);
            }
            
            else if (button.id == 910)	//Toggle showing breeding horses
            {
            	AnimalInfo.ToggleShowBreedingHorses();
            	button.displayString = GetButtonLabel_Boolean("animalinfo.options.showbreedinghorse", AnimalInfo.ShowBreedingTimerForHorses);
            }
            else if (button.id == 911)	//Toggle showing breeding villagers
            {
            	AnimalInfo.ToggleShowBreedingVillagers();
            	button.displayString = GetButtonLabel_Boolean("animalinfo.options.showbreedingvillagers", AnimalInfo.ShowBreedingTimerForVillagers);
            }
            else if (button.id == 912)	//Toggle showing breeding cows
            {
            	AnimalInfo.ToggleShowBreedingCows();
            	button.displayString = GetButtonLabel_Boolean("animalinfo.options.showbreedingcow", AnimalInfo.ShowBreedingTimerForCows);
            }
            else if (button.id == 913)	//Toggle showing breeding sheep
            {
            	AnimalInfo.ToggleShowBreedingSheep();
            	button.displayString = GetButtonLabel_Boolean("animalinfo.options.showbreedingsheep", AnimalInfo.ShowBreedingTimerForSheep);
            }
            else if (button.id == 914)	//Toggle showing breeding pig
            {
            	AnimalInfo.ToggleShowBreedingPigs();
            	button.displayString = GetButtonLabel_Boolean("animalinfo.options.showbreedingpig", AnimalInfo.ShowBreedingTimerForPigs);
            }
            else if (button.id == 915)	//Toggle showing breeding chicken
            {
            	AnimalInfo.ToggleShowBreedingChickens();
            	button.displayString = GetButtonLabel_Boolean("animalinfo.options.showbreedingchicken", AnimalInfo.ShowBreedingTimerForChickens);
            }
            else if (button.id == 916)	//Toggle showing breeding icons
            {
            	AnimalInfo.ToggleShowBreedingIcons();
            	button.displayString = GetButtonLabel_Boolean("animalinfo.options.showbreedingicons", AnimalInfo.ShowBreedingIcons);
            }
            else if (button.id == 917)	//Toggle showing breeding timers
            {
            	AnimalInfo.ToggleShowBreedingTimers();
            	button.displayString = GetButtonLabel_Boolean("animalinfo.options.showbreedingtimers", AnimalInfo.ShowBreedingTimers);
            }
            
            
            /////////////////////////////////////////////////////////////////////////
            // Potion Timers
            /////////////////////////////////////////////////////////////////////////
            
            else if (button.id == 1000)
            {
            	screenTitle = Localization.get("potiontimers.name");
            	DrawPotionTimerButtons();
            }
            else if (button.id == 1001)	//Enable/Disable
            {
            	PotionTimers.ToggleEnabled();
            	button.displayString = GetButtonLabel_Enabled(PotionTimers.Enabled);
            }
            else if (button.id == 1002)	//Show potion icons
            {
            	PotionTimers.ToggleShowPotionIcons();
            	button.displayString = GetButtonLabel_Boolean("potiontimers.options.showpotionicons", PotionTimers.ShowPotionIcons);
            }
            else if (button.id == 1005)	//Show potion colors
            {
            	PotionTimers.ToggleUsePotionColors();
            	button.displayString = GetButtonLabel_Boolean("potiontimers.options.usepotioncolors", PotionTimers.UsePotionColors);
            }
            else if (button.id == 1007)	//Hide default potion effects in inveotyr
            {
            	PotionTimers.ToggleHidePotionEffectsInInventory();
            	button.displayString = GetButtonLabel_Boolean("potiontimers.options.hidepotioneffectsininventory", PotionTimers.HidePotionEffectsInInventory);
            }
            else if (button.id == 1006)	//Potion scale slider
            {
            	float value = ((GuiNumberSlider)button).GetValueAsFloat();
            	PotionTimers.PotionScale = value;
            }
            else if (button.id == 1003)	//Horizontal location
            {
            	int value = ((GuiNumberSlider)button).GetValueAsInteger();
            	PotionTimers.SetHorizontalLocation(value);
            }
            else if (button.id == 1004)	//Vertical location
            {
            	int value = ((GuiNumberSlider)button).GetValueAsInteger();
            	PotionTimers.SetVerticalLocation(value);
            }
            
            /////////////////////////////////////////////////////////////////////////
            // Durability Info
            /////////////////////////////////////////////////////////////////////////
            
            else if (button.id == 1100)
            {
            	screenTitle = Localization.get("durabilityinfo.name");
            	DrawDurabilityInfoButtons();
            }
            else if (button.id == 1101)	//Enable/Disable
            {
            	DurabilityInfo.ToggleEnabled();
            	button.displayString = GetButtonLabel_Enabled(DurabilityInfo.Enabled);
            }
            else if (button.id == 1102)	//Enable Armor
            {
            	DurabilityInfo.ToggleShowArmorDurability();
            	button.displayString = GetButtonLabel_Boolean("durabilityinfo.options.showarmordurability", DurabilityInfo.ShowArmorDurability);
            }
            else if (button.id == 1103)	//Armor durability threshold slider
            {
            	float value = ((GuiNumberSlider)button).GetValueAsFloat();
            	DurabilityInfo.SetDurabilityDisplayThresholdForArmor(value);
            }
            else if (button.id == 1104)	//Show armor icons
            {
            	DurabilityInfo.ToggleShowIndividualArmorIcons();
            	button.displayString = GetButtonLabel_Boolean("durabilityinfo.options.showindividualarmoricons", DurabilityInfo.ShowIndividualArmorIcons);
            }
            else if (button.id == 1105)	//Enable Items
            {
            	DurabilityInfo.ToggleShowItemDurability();
            	button.displayString = GetButtonLabel_Boolean("durabilityinfo.options.showitemdurability", DurabilityInfo.ShowItemDurability);
            }
            else if (button.id == 1106)	//Item  durability threshold slider
            {
            	float value = ((GuiNumberSlider)button).GetValueAsFloat();
            	DurabilityInfo.SetDurabilityDisplayThresholdForItem(value);
            }
            else if (button.id == 1107)	//Update frequency
            {
            	int value = ((GuiNumberSlider)button).GetValueAsInteger();
            	DurabilityInfo.DurabilityUpdateFrequency = value;
            }
            else if (button.id == 1108)	//Horizontal location
            {
            	int value = ((GuiNumberSlider)button).GetValueAsInteger();
            	DurabilityInfo.SetHorizontalLocation(value);
            }
            else if (button.id == 1109)	//Vertical location
            {
            	int value = ((GuiNumberSlider)button).GetValueAsInteger();
            	DurabilityInfo.SetVerticalLocation(value);
            }
            else if (button.id == 1110)	//Show as Percent
            {
            	DurabilityInfo.ToggleShowDamageAsPercent();
            	button.displayString = GetButtonLabel_Boolean("durabilityinfo.options.showdamageaspercent", DurabilityInfo.ShowDamageAsPercentage);
            }
        	
            
            /////////////////////////////////////////////////////////////////////////
            // Ender Pearl Aid
            /////////////////////////////////////////////////////////////////////////
            
            else if (button.id == 1200)
            {
            	screenTitle = Localization.get("enderpearlaid.name");
            	DrawEnderPearlAidButtons();
            }
            else if (button.id == 1201)	//Enabled/Disabled
            {
            	EnderPearlAid.ToggleEnabled();
            	button.displayString = GetButtonLabel_Enabled(EnderPearlAid.Enabled);
            }
            else if (button.id == 1202)	//Hotkey
            {
            	HotkeyButtonClicked((GuiHotkeyButton)button);
            }
            
            /////////////////////////////////////////////////////////////////////////
            // Eating Aid
            /////////////////////////////////////////////////////////////////////////
            
            else if (button.id == 1300)
            {
            	screenTitle = Localization.get("eatingaid.name");
            	DrawEatingAidButtons();
            }
            else if (button.id == 1301)	//Enabled/Disabled
            {
            	EatingAid.ToggleEnabled();
            	button.displayString = GetButtonLabel_Enabled(EatingAid.Enabled);
            }
            else if (button.id == 1302)	//Hotkey
            {
            	HotkeyButtonClicked((GuiHotkeyButton)button);
            }
            else if (button.id == 1303)	//Eating Mode
            {
            	EatingAid.ToggleMode();
            	button.displayString = GetButtonLabel_Mode(ZyinHUD.CATEGORY_EATINGAID, EatingAid.Mode, EatingAid.NumberOfModes);
            }
            else if (button.id == 1304)	//Eat golden food
            {
            	EatingAid.ToggleEatingGoldenFood();
            	button.displayString = GetButtonLabel_Boolean("eatingaid.options.eatgoldenfood", EatingAid.EatGoldenFood);
            }
            else if (button.id == 1305)	//Prioritize food in hotbar
            {
            	EatingAid.TogglePrioritizeFoodInHotbar();
            	button.displayString = GetButtonLabel_Boolean("eatingaid.options.prioritizefoodinhotbar", EatingAid.PrioritizeFoodInHotbar);
            }
            
            /////////////////////////////////////////////////////////////////////////
            // Potion Aid
            /////////////////////////////////////////////////////////////////////////
            
            else if (button.id == 1400)
            {
            	screenTitle = Localization.get("potionaid.name");
            	DrawPotionAidButtons();
            }
            else if (button.id == 1401)	//Enabled/Disabled
            {
            	PotionAid.ToggleEnabled();
            	button.displayString = GetButtonLabel_Enabled(PotionAid.Enabled);
            }
            else if (button.id == 1402)	//Hotkey
            {
            	HotkeyButtonClicked((GuiHotkeyButton)button);
            }
            
            
            /////////////////////////////////////////////////////////////////////////
            // Weapon Swapper
            /////////////////////////////////////////////////////////////////////////
            
            else if (button.id == 1500)
            {
            	screenTitle = Localization.get("weaponswapper.name");
            	DrawWeaponSwapButtons();
            }
            else if (button.id == 1501)	//Enabled/Disabled
            {
            	WeaponSwapper.ToggleEnabled();
            	button.displayString = GetButtonLabel_Enabled(WeaponSwapper.Enabled);
            }
            else if (button.id == 1502)	//Hotkey
            {
            	HotkeyButtonClicked((GuiHotkeyButton)button);
            }
            else if (button.id == 1503)	//Scan hotbar from left to right
            {
            	WeaponSwapper.ToggleScanHotbarFromLeftToRight();
            	button.displayString = GetButtonLabel_Boolean("weaponswapper.options.scanhotbarforweaponsfromlefttoright", WeaponSwapper.ScanHotbarForWeaponsFromLeftToRight);
            }
            
            
            /////////////////////////////////////////////////////////////////////////
            // Quick Deposit
            /////////////////////////////////////////////////////////////////////////
            
            else if (button.id == 1600)
            {
            	screenTitle = Localization.get("quickdeposit.name");
            	DrawQuickDepositButtons();
            }
            else if (button.id == 1601)	//Enabled/Disabled
            {
            	QuickDeposit.ToggleEnabled();
            	button.displayString = GetButtonLabel_Enabled(QuickDeposit.Enabled);
            }
            else if (button.id == 1602)	//Hotkey
            {
            	HotkeyButtonClicked((GuiHotkeyButton)button);
            }
            else if (button.id == 1603)	//Ignore hotbar
            {
            	QuickDeposit.ToggleIgnoreItemsInHotbar();
            	button.displayString = GetButtonLabel_Boolean("quickdeposit.options.ignoreitemsinhotbar", QuickDeposit.IgnoreItemsInHotbar);
            }
            else if (button.id == 1604)	//Closes chest
            {
            	QuickDeposit.ToggleCloseChestAfterDepositing();
            	button.displayString = GetButtonLabel_Boolean("quickdeposit.options.closechestafterdepositing", QuickDeposit.CloseChestAfterDepositing);
            }
            else if (button.id == 1605)	//Blacklist torches
            {
            	QuickDeposit.ToggleBlacklistTorch();
            	button.displayString = GetButtonLabel_Boolean("quickdeposit.options.blacklisttorch", QuickDeposit.BlacklistTorch);
            }
            else if (button.id == 1606)	//Blacklist arrows
            {
            	QuickDeposit.ToggleBlacklistArrow();
            	button.displayString = GetButtonLabel_Boolean("quickdeposit.options.blacklistarrow", QuickDeposit.BlacklistArrow);
            }
            else if (button.id == 1607)	//Blacklist food
            {
            	QuickDeposit.ToggleBlacklistFood();
            	button.displayString = GetButtonLabel_Boolean("quickdeposit.options.blacklistfood", QuickDeposit.BlacklistFood);
            }
            else if (button.id == 1608)	//Blacklist ender pearls
            {
            	QuickDeposit.ToggleBlacklistEnderPearl();
            	button.displayString = GetButtonLabel_Boolean("quickdeposit.options.blacklistenderpearl", QuickDeposit.BlacklistEnderPearl);
            }
            else if (button.id == 1609)	//Blacklist water buckets
            {
            	QuickDeposit.ToggleBlacklistWaterBucket();
            	button.displayString = GetButtonLabel_Boolean("quickdeposit.options.blacklistwaterbucket", QuickDeposit.BlacklistWaterBucket);
            }
            else if (button.id == 1610)	//Blacklist clock/compass
            {
            	QuickDeposit.ToggleBlacklistClockCompass();
            	button.displayString = GetButtonLabel_Boolean("quickdeposit.options.blacklistclockcompass", QuickDeposit.BlacklistClockCompass);
            }
            
            
            
        }
    }
    
    /**
     * Helper method to keep track of any GuiHotkeyButtons we've clicked.
     * @param hotkeyButton
     */
    private void HotkeyButtonClicked(GuiHotkeyButton hotkeyButton)
    {
    	hotkeyButton.Clicked();
    	if(hotkeyButton.IsWaitingForHotkeyInput())
    		currentlySelectedHotkeyButton = hotkeyButton;
    	else
    		currentlySelectedHotkeyButton = null;
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char key, int keycode)
    {
    	//if a hotkey button is waiting for input, use this key pressed and assign it to the hotkey
        if(currentlySelectedHotkeyButton != null)
        {
            if (keycode == Keyboard.KEY_ESCAPE)
            {
            	currentlySelectedHotkeyButton.Cancel();
            	currentlySelectedHotkeyButton = null;
            	return;
            }
            else
            	currentlySelectedHotkeyButton.ApplyHotkey(keycode);//(String.valueOf(key));
        }
        
        //if escape is pressed, then close the screen
        if (keycode == Keyboard.KEY_ESCAPE)
        {
            mc.displayGuiScreen((GuiScreen)null);
            mc.setIngameFocus();
        }
    }
    
    /**
     * Gets a reference to a GuiButton being rendered based on its ID.
     * @param id
     * @return
     */
    public GuiButton GetButtonById(int id)
    {
    	for(int i = 0; i < buttonList.size(); i++)
    	{
    		GuiButton button = (GuiButton)buttonList.get(i);
    		if(button.id == id)
    			return button;
    	}
    	return null;
    }
    
    /**
     * Determines if a button tab (buttons on the left part of the screen) is selected.
     * @param buttonTabLabel Localized name of this button tab as displayed on the button itself
     * @return
     */
    public boolean IsButtonTabSelected(String buttonTabLabel)
    {
    	if(currentlySelectedTabButton != null)
    		return currentlySelectedTabButton.displayString.replace(currentlySelectedTabButtonColor,"").equals(buttonTabLabel);
    	else
    		return false;
    }
    
    /**
     * Goes to the next page of tabbed buttons.
     */
    public void IncrementTabbedPage()
    {
    	tabbedPage++;
    	if(tabbedPage >= tabbedMaxPages)
    		tabbedPage = tabbedMaxPages;
    	else
    	{
    		screenTitle = Localization.get("gui.options.title");
    		DrawAllButtons();
    	}
    }

    /**
     * Goes to the previous page of tabbed buttons.
     */
    public void DecrementTabbedPage()
    {
    	tabbedPage--;
    	if(tabbedPage < 0)
    		tabbedPage = 0;
    	else
    	{
    		screenTitle = Localization.get("gui.options.title");
    		DrawAllButtons();
    	}
    }
    

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, screenTitle, width / 2, 15, 0xFFFFFF);
        
        DrawMiscText();
        
        super.drawScreen(par1, par2, par3);
    }
}
