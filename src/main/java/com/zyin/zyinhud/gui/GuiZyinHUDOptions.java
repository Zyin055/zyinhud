package com.zyin.zyinhud.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.zyin.zyinhud.ZyinHUD;
import com.zyin.zyinhud.ZyinHUDConfig;
import com.zyin.zyinhud.ZyinHUDKeyHandlers;
import com.zyin.zyinhud.gui.buttons.GuiHotkeyButton;
import com.zyin.zyinhud.gui.buttons.GuiLabeledButton;
import com.zyin.zyinhud.gui.buttons.GuiNumberSlider;
import com.zyin.zyinhud.keyhandlers.AnimalInfoKeyHandler;
import com.zyin.zyinhud.keyhandlers.CoordinatesKeyHandler;
import com.zyin.zyinhud.keyhandlers.DistanceMeasurerKeyHandler;
import com.zyin.zyinhud.keyhandlers.EatingAidKeyHandler;
import com.zyin.zyinhud.keyhandlers.EnderPearlAidKeyHandler;
import com.zyin.zyinhud.keyhandlers.ItemSelectorKeyHandler;
import com.zyin.zyinhud.keyhandlers.PlayerLocatorKeyHandler;
import com.zyin.zyinhud.keyhandlers.PotionAidKeyHandler;
import com.zyin.zyinhud.keyhandlers.QuickDepositKeyHandler;
import com.zyin.zyinhud.keyhandlers.SafeOverlayKeyHandler;
import com.zyin.zyinhud.keyhandlers.WeaponSwapperKeyHandler;
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
 * This is the options GUI which is used to change any configurable setting while in game.
 * <p>
 * The tabs on the left side represent the various mods and more can be added using the
 * tabbedButtonNames and tabbedButtonIDs variables, then adding functionality to the actionPerformed()
 * method to draw additional buttons specific to the mod.
 * <p>
 * There are 5 types of buttons we use in this GUI:<br>
 * <ol>
 * <li>"Enabled" button (GuiButton)
 * <li>"Mode" button (GuiButton)
 * <li>"Boolean" button (GuiButton)
 * <li>"Slider" button (GuiNumberSlider) *custom*
 * <li>"Hotkey" button (GuiHotkeyButton) *custom*
 * </ol>
 * See existing examples on how to use these.
 * <p>
 * We are able to access this screen by using a hotkey (Ctrl + Alt + Z), or navigating through the
 * default options window. We put an additional button into the Options window by replacing the normal
 * GuiOptions class with our custom OverrideGuiOptions class.
 * <p>
 * In order to get the GuiNumberSlider to work when we click and drag it, we override and modify 3 methods:
 * mouseClicked(), mouseMovedOrUp(), and actionPerformed_MouseUp().
 * <p>
 * GuiHotkeyButton is a class used to assign hotkeys. It relies on GuiZyinHUDOptions to function properly.
 */
public class GuiZyinHUDOptions extends GuiTooltipScreen
{
    public static final String HotkeyDescription = "key.zyinhud.zyinhudoptions";
	
	protected GuiScreen parentGuiScreen;
    
    /** The title string that is displayed in the top-center of the screen. */
    protected String screenTitle;

    /** The button that was just pressed. */
    protected GuiButton selectedButton;
    
    protected Object[][] tabbedButtons = {
    		{2000, Localization.get("miscellaneous.name"), null},
    		{100, Localization.get("infoline.name"), null},
    		{200, Localization.get("clock.name"), null},
    		{300, Localization.get("coordinates.name"), GetKeyBindingAsString(1)},
    		{400, Localization.get("compass.name"), null},
    		{500, Localization.get("fps.name"), null},
    		{600, Localization.get("distancemeasurer.name"), GetKeyBindingAsString(2)},
    		{700, Localization.get("safeoverlay.name"), GetKeyBindingAsString(8)},
    		{800, Localization.get("playerlocator.name"), GetKeyBindingAsString(5)},
    		{900, Localization.get("animalinfo.name"), GetKeyBindingAsString(0)},
    		{1100, Localization.get("durabilityinfo.name"), null},
    		{1000, Localization.get("potiontimers.name"), null},
    		{1200, Localization.get("enderpearlaid.name"), GetKeyBindingAsString(4)},
    		{1300, Localization.get("eatingaid.name"), GetKeyBindingAsString(3)},
    		{1400, Localization.get("potionaid.name"), GetKeyBindingAsString(6)},
    		{1900, Localization.get("torchaid.name"), null},
    		{1500, Localization.get("weaponswapper.name"), GetKeyBindingAsString(9)},
    		{1600, Localization.get("quickdeposit.name"), GetKeyBindingAsString(7)},
    		{1700, Localization.get("itemselector.name"), GetKeyBindingAsString(11)},
    		{1800, Localization.get("healthmonitor.name"), null}
    };
    /**
     * @param keyBindingIndex the index in <code>ZyinHUDKeyHandlers.KEY_BINDINGS[]</code>
     * @return hotkey as a string
     */
    private String GetKeyBindingAsString(int keyBindingIndex)
    {
    	try {
    		return Keyboard.getKeyName(ZyinHUDKeyHandlers.KEY_BINDINGS[keyBindingIndex].getKeyCode());
    	}
    	catch (ArrayIndexOutOfBoundsException e) {
    		return "[?]";	//A user reported having getKeyCode() returning -89 and causing this exception
    	}
    }
    
    private GuiHotkeyButton currentlySelectedHotkeyButton;
    private static GuiButton currentlySelectedTabButton = null;
    private final String currentlySelectedTabButtonColor = EnumChatFormatting.YELLOW.toString();
    
    
    //variables influencing the placement/sizing of the tab buttons on the left
    protected int tabbedButtonX;
    protected int tabbedButtonY;
    protected int tabbedButtonWidth;
    protected int tabbedButtonHeight;
    protected int tabbedButtonSpacing;
	
	protected int pagingButtonWidth;
	protected int pagingButtonHeight;
	
    /** The current tab page. It is 0 indexed. */
    protected static int tabbedPage = 0;
    
    /** The amount of tabs shown on each page. */
    protected static int tabbedPageSize = 12;
    protected static int tabbedMaxPages;
    
    
    //variables influencing the placement/sizing of the buttons inside each tab
	protected int buttonY;
	protected int buttonWidth;
	protected int buttonWidth_double;
	protected int buttonHeight;
	protected int buttonSpacing;
	
    
    public GuiZyinHUDOptions(GuiScreen parentGuiScreen)
    {
        this.parentGuiScreen = parentGuiScreen;
        tabbedMaxPages = (int) Math.ceil((double)(tabbedButtons.length)/tabbedPageSize);
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        //button variables
    	buttonSpacing = 2;
    	buttonWidth = 130;
    	buttonWidth_double = buttonWidth*2 + buttonSpacing*2;
    	
        //tabbed button variables
        tabbedButtonSpacing = 0;
        tabbedButtonWidth = 130;
        tabbedButtonHeight = 14;
        tabbedButtonX = width/2 - (tabbedButtonWidth + buttonWidth_double)/2;
        tabbedButtonY = (int)(height*0.13);	//0.16

        //button variables
    	buttonHeight = 20;
    	buttonY = (int)(height*0.13);	//0.17
    	
    	//paging buttons
    	pagingButtonWidth = 15;
    	pagingButtonHeight = 14;
    	
        screenTitle = Localization.get("gui.options.title");
        
        DrawAllButtons();
        
        //simulate a click on the last tabbed button that was clicked to re-open it
        actionPerformed(currentlySelectedTabButton);
    }

    protected void DrawAllButtons()
    {
    	this.zLevel = 0f;
    	
        buttonList.clear();
        //currentlySelectedTabButton = null;
        DrawOtherButtons();
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
        			ZyinHUD.MODNAME,
        			"v." + ZyinHUD.VERSION,
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
            	int strWidth = fontRendererObj.getStringWidth(text[i]);
        		int xOffset = -strWidth;
        		int yOffset = -(lineHeight * (text.length - i));

                GL11.glEnable(GL11.GL_BLEND);	//for transparent text
            	fontRendererObj.drawString(text[i], x + xOffset, y + yOffset, 0x22ffffff);
                GL11.glDisable(GL11.GL_BLEND);
        	}
    	}
    	else if(currentlySelectedTabButton.id == 1600)	//Quick Deposit
    	{
    		String text = EnumChatFormatting.UNDERLINE + Localization.get("quickdeposit.options.blacklist");
    		
        	int x = tabbedButtonWidth + tabbedButtonX + buttonSpacing*2 + buttonWidth + buttonSpacing*2 + buttonWidth/2 - fontRendererObj.getStringWidth(text)/2;
        	int y = buttonY - buttonHeight/2 - fontRendererObj.FONT_HEIGHT/2 + 3;
        	
        	fontRendererObj.drawString(text, x, y, 0xffffff);
    	}
    }
    private void DrawOtherButtons()
    {
    	//Save button
    	buttonList.add(new GuiButton(1, width / 2 - 100, height / 6 + 168, Localization.get("gui.options.saveandexit")));
    	
    }
    private void DrawTabbedButtons()
    {
		//make the paging controls
		GuiButton prevPageButton = new GuiButton(10, tabbedButtonX, tabbedButtonY-pagingButtonHeight, pagingButtonWidth, pagingButtonHeight, "<");
		GuiButton nextPageButton = new GuiButton(11, tabbedButtonX+tabbedButtonWidth-pagingButtonWidth+1, tabbedButtonY-pagingButtonHeight, pagingButtonWidth, pagingButtonHeight, ">");
    	
		if(tabbedPage == 0)
    		prevPageButton.enabled = false;
    	else if(tabbedPage == tabbedMaxPages-1)
    		nextPageButton.enabled = false;
    	
		//add the paging controls
		buttonList.add(prevPageButton);
    	buttonList.add(nextPageButton);

    	int Y = tabbedButtonY;
    	
    	//make the tabbed buttons
    	for(int i = 0; i < tabbedPageSize; i++)
    	{
    		int index = (tabbedPage * tabbedPageSize + i);
    		if(index >= tabbedButtons.length)
    			break;
    		int id = (Integer) tabbedButtons[index][0];
    		String buttonName = (String) tabbedButtons[index][1];
    		String buttonLabel = (String) tabbedButtons[index][2];
    		
    		buttonList.add(new GuiLabeledButton(id, tabbedButtonX, Y, tabbedButtonWidth, tabbedButtonHeight, buttonName, buttonLabel));
    		
    		Y += tabbedButtonHeight;
    	}
    }
    
    /**
     * Helper method for adding buttons at specific positions when a tab is clicked. This will correctly set
     * the button's xPosition and yPosition based on the specified row and column arguments.
     * There are 2 columns and 8 rows visible on screen.
     * @param column values: [0, 1]
     * @param row values: [0, 1, 2, 3, 4, 5, 6, 7]
     * @param button
     */
    private void AddButtonAt(int column, int row, GuiButton button)
    {
    	button.xPosition = tabbedButtonWidth + tabbedButtonX + buttonSpacing*2 + (buttonWidth + buttonSpacing*2)*column;
    	button.yPosition = buttonY + (buttonHeight + buttonSpacing)*row;
    	
    	buttonList.add(button);
    }
    
    private void DrawMiscellaneousButtons()
    {
    	AddButtonAt(0, 0, new GuiButton(2001, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("miscellaneous.options.useenhancedmiddleclick", Miscellaneous.UseEnhancedMiddleClick)));
    	AddButtonAt(0, 1, new GuiButton(2002, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("miscellaneous.options.usequickplacesign", Miscellaneous.UseQuickPlaceSign)));
    	AddButtonAt(0, 2, new GuiButton(2003, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("miscellaneous.options.useunlimitedsprinting", Miscellaneous.UseUnlimitedSprinting)));
    	AddButtonAt(0, 3, new GuiButton(2004, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("miscellaneous.options.showanvilrepairs", Miscellaneous.ShowAnvilRepairs)));
    	
    }
    private void DrawInfoLineButtons()
    {
    	AddButtonAt(0, 0, new GuiButton(101, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(InfoLine.Enabled)));
    	AddButtonAt(0, 1, new GuiButton(102, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("infoline.options.showbiome", InfoLine.ShowBiome)));
    	AddButtonAt(0, 2, new GuiButton(105, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("infoline.options.showcansnow", InfoLine.ShowCanSnow)));
    	AddButtonAt(0, 6, new GuiNumberSlider(103, 0, 0, buttonWidth_double, buttonHeight, Localization.get("infoline.options.offsetx"), 1, width - 25, InfoLine.GetHorizontalLocation(), GuiNumberSlider.Modes.INTEGER));
    	AddButtonAt(0, 7, new GuiNumberSlider(104, 0, 0, buttonWidth_double, buttonHeight, Localization.get("infoline.options.offsety"), 1, height - 8, InfoLine.GetVerticalLocation(), GuiNumberSlider.Modes.INTEGER));
    	
    }
    private void DrawClockButtons()
    {
    	AddButtonAt(0, 0, new GuiButton(201, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(Clock.Enabled)));
    	AddButtonAt(0, 1, new GuiButton(202, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Mode(Clock.Mode.GetFriendlyName())));
    	
    }
    private void DrawCoordinatesButtons()
    {
    	AddButtonAt(0, 0, new GuiButton(301, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(Coordinates.Enabled)));
    	AddButtonAt(0, 1, new GuiHotkeyButton(303, 0, 0, buttonWidth, buttonHeight, CoordinatesKeyHandler.HotkeyDescription));
    	AddButtonAt(0, 2, new GuiButton(304, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Mode(Coordinates.Mode.GetFriendlyName())));
    	AddButtonAt(0, 3, new GuiButton(302, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("coordinates.options.useycoordinatecolors", Coordinates.UseYCoordinateColors)));
    	AddButtonAt(0, 4, new GuiButton(305, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("coordinates.options.showchunkcoordinates", Coordinates.ShowChunkCoordinates)));
    	
    }
    private void DrawCompassButtons()
    {
    	AddButtonAt(0, 0, new GuiButton(401, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(Compass.Enabled)));
    	
    }
    private void DrawFPSButtons()
    {
    	AddButtonAt(0, 0, new GuiButton(501, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(Fps.Enabled)));
    	
    }
    private void DrawDistanceMeasurerButtons()
    {
    	AddButtonAt(0, 0, new GuiButton(601, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(DistanceMeasurer.Enabled)));
    	AddButtonAt(0, 1, new GuiHotkeyButton(602, 0, 0, buttonWidth, buttonHeight, DistanceMeasurerKeyHandler.HotkeyDescription));
    	
    }
    private void DrawSafeOverlayButtons()
    {
    	AddButtonAt(0, 0, new GuiButton(701, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(SafeOverlay.Enabled)));
    	AddButtonAt(0, 1, new GuiHotkeyButton(702, 0, 0, buttonWidth, buttonHeight, SafeOverlayKeyHandler.HotkeyDescription));
    	AddButtonAt(0, 2, new GuiNumberSlider(703, 0, 0, buttonWidth, buttonHeight, Localization.get("safeoverlay.options.drawdistance"), SafeOverlay.minDrawDistance, SafeOverlay.maxDrawDistance, SafeOverlay.instance.GetDrawDistance(), GuiNumberSlider.Modes.INTEGER));
    	AddButtonAt(0, 3, new GuiNumberSlider(704, 0, 0, buttonWidth, buttonHeight, Localization.get("safeoverlay.options.transparency"), SafeOverlay.instance.GetUnsafeOverlayMinTransparency(), SafeOverlay.instance.GetUnsafeOverlayMaxTransparency(), SafeOverlay.instance.GetUnsafeOverlayTransparency(), GuiNumberSlider.Modes.PERCENT));
    	AddButtonAt(0, 4, new GuiButton(705, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("safeoverlay.options.displayinnether", SafeOverlay.instance.GetDisplayInNether())));
    	AddButtonAt(0, 5, new GuiButton(706, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("safeoverlay.options.seethroughwalls", SafeOverlay.instance.GetSeeUnsafePositionsThroughWalls())));
    	
    }
    private void DrawPlayerLocatorButtons()
    {
    	AddButtonAt(0, 0, new GuiButton(801, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(PlayerLocator.Enabled)));
    	AddButtonAt(0, 1, new GuiHotkeyButton(802, 0, 0, buttonWidth, buttonHeight, PlayerLocatorKeyHandler.HotkeyDescription));
    	AddButtonAt(0, 2, new GuiNumberSlider(803, 0, 0, buttonWidth, buttonHeight, Localization.get("playerlocator.options.minviewdistance"), PlayerLocator.minViewDistanceCutoff, PlayerLocator.maxViewDistanceCutoff, PlayerLocator.viewDistanceCutoff, GuiNumberSlider.Modes.INTEGER));
    	AddButtonAt(0, 3, new GuiButton(804, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("playerlocator.options.showdistancetoplayers", PlayerLocator.ShowDistanceToPlayers)));
    	AddButtonAt(0, 4, new GuiButton(805, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("playerlocator.options.showplayerhealth", PlayerLocator.ShowPlayerHealth)));
    	
    	AddButtonAt(1, 0, new GuiButton(808, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("playerlocator.options.showwitherskeletons", PlayerLocator.ShowWitherSkeletons)));
    	AddButtonAt(1, 1, new GuiButton(806, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("playerlocator.options.showwolves", PlayerLocator.ShowWolves)));
    	AddButtonAt(1, 2, new GuiButton(807, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("playerlocator.options.usewolfcolors", PlayerLocator.UseWolfColors)));
    	
    }
    private void DrawAnimalInfoButtons()
    {
    	
    	AddButtonAt(0, 0, new GuiButton(901, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(AnimalInfo.Enabled)));
    	AddButtonAt(0, 1, new GuiHotkeyButton(902, 0, 0, buttonWidth, buttonHeight, AnimalInfoKeyHandler.HotkeyDescription));
    	AddButtonAt(0, 2, new GuiNumberSlider(903, 0, 0, buttonWidth, buttonHeight, Localization.get("animalinfo.options.maxviewdistance"), AnimalInfo.minViewDistanceCutoff, AnimalInfo.maxViewDistanceCutoff, AnimalInfo.viewDistanceCutoff, GuiNumberSlider.Modes.INTEGER));
    	AddButtonAt(0, 3, new GuiButton(907, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("animalinfo.options.showtextbackground", AnimalInfo.ShowTextBackgrounds)));
    	AddButtonAt(0, 4, new GuiNumberSlider(904, 0, 0, buttonWidth, buttonHeight, Localization.get("animalinfo.options.numdecimalsdisplayed"), AnimalInfo.minNumberOfDecimalsDisplayed, AnimalInfo.maxNumberOfDecimalsDisplayed, AnimalInfo.GetNumberOfDecimalsDisplayed(), GuiNumberSlider.Modes.INTEGER));
    	AddButtonAt(0, 5, new GuiButton(905, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("animalinfo.options.showhorsestatsonf3menu", AnimalInfo.ShowHorseStatsOnF3Menu)));
    	AddButtonAt(0, 6, new GuiButton(906, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("animalinfo.options.showhorsestatsoverlay", AnimalInfo.ShowHorseStatsOverlay)));
    	
    	AddButtonAt(1, 0, new GuiButton(916, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("animalinfo.options.showbreedingicons", AnimalInfo.ShowBreedingIcons)));
    	//AddButtonAt(1, 1, new GuiButton(917, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("animalinfo.options.showbreedingtimers", AnimalInfo.ShowBreedingTimers)));
    	
    }
    private void DrawPotionTimerButtons()
    {
    	AddButtonAt(0, 0, new GuiButton(1001, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(PotionTimers.Enabled)));
    	AddButtonAt(0, 1, new GuiButton(1002, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("potiontimers.options.showpotionicons", PotionTimers.ShowPotionIcons)));
    	AddButtonAt(0, 2, new GuiButton(1005, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("potiontimers.options.usepotioncolors", PotionTimers.UsePotionColors)));
    	AddButtonAt(0, 3, new GuiButton(1007, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("potiontimers.options.hidepotioneffectsininventory", PotionTimers.HidePotionEffectsInInventory)));
    	AddButtonAt(0, 5, new GuiNumberSlider(1006, 0, 0, buttonWidth, buttonHeight, Localization.get("potiontimers.options.potionscale"), 1.0f, 4.0f, PotionTimers.PotionScale, GuiNumberSlider.Modes.PERCENT));
    	AddButtonAt(0, 6, new GuiNumberSlider(1003, 0, 0, buttonWidth_double, buttonHeight, Localization.get("potiontimers.options.offsetx"), 1, width - 25, PotionTimers.GetHorizontalLocation(), GuiNumberSlider.Modes.INTEGER));
    	AddButtonAt(0, 7, new GuiNumberSlider(1004, 0, 0, buttonWidth_double, buttonHeight, Localization.get("potiontimers.options.offsety"), 0, height - 10, PotionTimers.GetVerticalLocation(), GuiNumberSlider.Modes.INTEGER));
    	
    }
    private void DrawDurabilityInfoButtons()
    {
    	AddButtonAt(0, 0, new GuiButton(1101, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(DurabilityInfo.Enabled)));
    	AddButtonAt(0, 1, new GuiButton(1102, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("durabilityinfo.options.showarmordurability", DurabilityInfo.ShowArmorDurability)));
    	AddButtonAt(0, 2, new GuiNumberSlider(1103, 0, 0, buttonWidth, buttonHeight, Localization.get("durabilityinfo.options.armordurabilitythreshold"), 0f, 1f, DurabilityInfo.GetDurabilityDisplayThresholdForArmor(), GuiNumberSlider.Modes.PERCENT));
    	AddButtonAt(0, 3, new GuiButton(1111, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("durabilityinfo.options.autounequiparmor", DurabilityInfo.AutoUnequipArmor)));
    	AddButtonAt(0, 4, new GuiButton(1104, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("durabilityinfo.options.showindividualarmoricons", DurabilityInfo.ShowIndividualArmorIcons)));
    	AddButtonAt(0, 5, new GuiNumberSlider(1114, 0, 0, buttonWidth, buttonHeight, Localization.get("durabilityinfo.options.durabilityscale"), 1.0f, 4.0f, DurabilityInfo.DurabilityScale, GuiNumberSlider.Modes.PERCENT));
    	AddButtonAt(0, 6, new GuiNumberSlider(1108, 0, 0, buttonWidth_double, buttonHeight, Localization.get("durabilityinfo.options.offsetx"), 0, width - DurabilityInfo.toolX, DurabilityInfo.durabalityLocX, GuiNumberSlider.Modes.INTEGER));
    	AddButtonAt(0, 7, new GuiNumberSlider(1109, 0, 0, buttonWidth_double, buttonHeight, Localization.get("durabilityinfo.options.offsety"), 0, height - DurabilityInfo.toolY, DurabilityInfo.durabalityLocY, GuiNumberSlider.Modes.INTEGER));
    	
    	AddButtonAt(1, 0, new GuiButton(1113, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("durabilityinfo.options.usecolorednumbers", DurabilityInfo.UseColoredNumbers)));
    	AddButtonAt(1, 1, new GuiButton(1105, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("durabilityinfo.options.showitemdurability", DurabilityInfo.ShowItemDurability)));
    	AddButtonAt(1, 2, new GuiNumberSlider(1106, 0, 0, buttonWidth, buttonHeight, Localization.get("durabilityinfo.options.itemdurabilitythreshold"), 0f, 1f, DurabilityInfo.GetDurabilityDisplayThresholdForItem(), GuiNumberSlider.Modes.PERCENT));
    	AddButtonAt(1, 3, new GuiButton(1112, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("durabilityinfo.options.autounequiptools", DurabilityInfo.AutoUnequipTools)));
    	AddButtonAt(1, 4, new GuiButton(1110, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("durabilityinfo.options.showdamageaspercent", DurabilityInfo.ShowDamageAsPercentage)));

    }
    private void DrawEnderPearlAidButtons()
    {
    	AddButtonAt(0, 0, new GuiButton(1201, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(EnderPearlAid.Enabled)));
    	AddButtonAt(0, 1, new GuiHotkeyButton(1202, 0, 0, buttonWidth, buttonHeight, EnderPearlAidKeyHandler.HotkeyDescription));
    	
    }
    private void DrawEatingAidButtons()
    {
    	AddButtonAt(0, 0, new GuiButton(1301, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(EatingAid.Enabled)));
    	AddButtonAt(0, 1, new GuiHotkeyButton(1302, 0, 0, buttonWidth, buttonHeight, EatingAidKeyHandler.HotkeyDescription));
    	AddButtonAt(0, 2, new GuiButton(1303, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Mode(EatingAid.Mode.GetFriendlyName())));
    	AddButtonAt(0, 3, new GuiButton(1304, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("eatingaid.options.eatgoldenfood", EatingAid.EatGoldenFood)));
    	AddButtonAt(0, 4, new GuiButton(1306, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("eatingaid.options.eatrawfood", EatingAid.EatRawFood)));
    	AddButtonAt(0, 5, new GuiButton(1305, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("eatingaid.options.prioritizefoodinhotbar", EatingAid.PrioritizeFoodInHotbar)));
    	
    	AddButtonAt(1, 0, new GuiButton(1307, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("eatingaid.options.usepvpsoup", EatingAid.UsePvPSoup)));
    	
    }
    private void DrawPotionAidButtons()
    {
    	AddButtonAt(0, 0, new GuiButton(1401, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(PotionAid.Enabled)));
    	AddButtonAt(0, 1, new GuiHotkeyButton(1402, 0, 0, buttonWidth, buttonHeight, PotionAidKeyHandler.HotkeyDescription));
    	
    }
    private void DrawWeaponSwapperButtons()
    {
    	AddButtonAt(0, 0, new GuiButton(1501, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(WeaponSwapper.Enabled)));
    	AddButtonAt(0, 1, new GuiHotkeyButton(1502, 0, 0, buttonWidth, buttonHeight, WeaponSwapperKeyHandler.HotkeyDescription));
    	
    }
    private void DrawQuickDepositButtons()
    {
    	AddButtonAt(0, 0, new GuiButton(1601, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(QuickDeposit.Enabled)));
    	AddButtonAt(0, 1, new GuiHotkeyButton(1602, 0, 0, buttonWidth, buttonHeight, QuickDepositKeyHandler.HotkeyDescription));
    	AddButtonAt(0, 2, new GuiButton(1603, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("quickdeposit.options.ignoreitemsinhotbar", QuickDeposit.IgnoreItemsInHotbar)));
    	AddButtonAt(0, 3, new GuiButton(1604, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("quickdeposit.options.closechestafterdepositing", QuickDeposit.CloseChestAfterDepositing)));
    	
    	AddButtonAt(1, 0, new GuiButton(1605, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("quickdeposit.options.blacklisttorch", QuickDeposit.BlacklistTorch)));
    	AddButtonAt(1, 1, new GuiButton(1611, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("quickdeposit.options.blacklistweapons", QuickDeposit.BlacklistWeapons)));
    	AddButtonAt(1, 2, new GuiButton(1606, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("quickdeposit.options.blacklistarrow", QuickDeposit.BlacklistArrow)));
    	AddButtonAt(1, 3, new GuiButton(1607, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("quickdeposit.options.blacklistfood", QuickDeposit.BlacklistFood)));
    	AddButtonAt(1, 4, new GuiButton(1608, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("quickdeposit.options.blacklistenderpearl", QuickDeposit.BlacklistEnderPearl)));
    	AddButtonAt(1, 5, new GuiButton(1609, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("quickdeposit.options.blacklistwaterbucket", QuickDeposit.BlacklistWaterBucket)));
    	AddButtonAt(1, 6, new GuiButton(1610, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("quickdeposit.options.blacklistclockcompass", QuickDeposit.BlacklistClockCompass)));
    	
    }
    private void DrawItemSelectorButtons()
    {
        AddButtonAt(0, 0, new GuiButton(1701, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(ItemSelector.Enabled)));
        AddButtonAt(0, 1, new GuiHotkeyButton(1702, 0, 0, buttonWidth, buttonHeight, ItemSelectorKeyHandler.HotkeyDescription));
        AddButtonAt(0, 2, new GuiButton(1704, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Mode(ItemSelector.Mode.GetFriendlyName())));
        AddButtonAt(0, 3, new GuiNumberSlider(1703, 0, 0, buttonWidth, buttonHeight, Localization.get("itemselector.options.ticks"), ItemSelector.minTimeout, ItemSelector.maxTimeout, ItemSelector.GetTimeout(), GuiNumberSlider.Modes.INTEGER));
        AddButtonAt(0, 4, new GuiButton(1705, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("itemselector.options.sideButtons", ItemSelector.UseMouseSideButtons)));

    }
    
    private void DrawHealthMonitorButtoins()
    {
    	AddButtonAt(0, 0, new GuiButton(1801, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(HealthMonitor.Enabled)));
    	AddButtonAt(0, 1, new GuiButton(1802, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Mode(HealthMonitor.Mode.GetFriendlyName())));
    	AddButtonAt(0, 2, new GuiButton(1804, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Boolean("healthmonitor.options.playfasterneardeath", HealthMonitor.PlayFasterNearDeath)));
    	AddButtonAt(0, 3, new GuiNumberSlider(1805, 0, 0, buttonWidth_double, buttonHeight, Localization.get("healthmonitor.options.lowhealthsoundthreshold"), 1, 20, HealthMonitor.GetLowHealthSoundThreshold(), GuiNumberSlider.Modes.INTEGER));

    	AddButtonAt(1, 1, new GuiButton(1803, 0, 0, buttonWidth/2, buttonHeight, Localization.get("healthmonitor.options.mode.play")));
    	
    }
    
    private void DrawTorchAidButtons()
    {
    	AddButtonAt(0, 0, new GuiButton(1901, 0, 0, buttonWidth, buttonHeight, GetButtonLabel_Enabled(TorchAid.Enabled)));
    	
    }
    
    /**
     * Helper method to get the text for a button that toggles between modes for a mod.
     * @param modeName The friendly name of the mode (use Mode.toString())
     * @return a String to be used as the button label
     */
    private static String GetButtonLabel_Mode(String modeName)
    {
    	return Localization.get("gui.options.mode") + modeName;
    }
    
    /**
     * Helper method to get the text for a button that toggles the mod on and off.
     * @param enabled the current enabled/disabled boolean status of the mod
     * @return a color coded String to be used as the button label
     */
    private static String GetButtonLabel_Enabled(boolean enabled)
    {
    	if(enabled)
    		return Localization.get("gui.options.enabled") + EnumChatFormatting.GREEN + Localization.get("options.on") + EnumChatFormatting.WHITE;
    	else
    		return Localization.get("gui.options.enabled") + EnumChatFormatting.RED + Localization.get("options.off") + EnumChatFormatting.WHITE;
    }
    
    /**
     * Helper method to get the text for a button that toggles between true and false.
     * @param localizationString the text from the localization file to be used as the label for the button
     * @param bool boolean value to display
     * @return a String to be used as the button label
     */
    private static String GetButtonLabel_Boolean(String localizationString, boolean bool)
    {
		if(bool)
			return Localization.get(localizationString) + Localization.get("options.on");
		else
			return Localization.get(localizationString) + Localization.get("options.off");
    }
    
    

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
    	//play a sound and fire the actionPerformed() method when a button is left clicked
        if (mouseButton == 0)	//left click
        {
            for (int l = 0; l < this.buttonList.size(); ++l)
            {
                GuiButton guibutton = (GuiButton)this.buttonList.get(l);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY))
                {
                    selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    actionPerformed(guibutton);
                }
            }
        }
    }
    
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.selectedButton != null && state == 0)	//released the mouse click
        {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
    	if(clickedMouseButton == 0)	//left click
    	{
    		if(selectedButton != null && selectedButton instanceof GuiNumberSlider)
    		{
    			//continuously apply updates for any GuiNumberSlider buttons as they are being dragged
    			actionPerformed(selectedButton);
    		}
    	}
    }
    
    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     * In this method we handle every buttons action.
     */
    protected void actionPerformed(GuiButton button)
    {
        if (button != null && button.enabled)
        {
            /////////////////////////////////////////////////////////////////////////
            // Tab buttons
            /////////////////////////////////////////////////////////////////////////
        	
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

            switch (button.id)
            {
	            /////////////////////////////////////////////////////////////////////////
	            // Misc
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 1:	//Save
	            	//the actual saving is done in onGuiClosed()
	                mc.displayGuiScreen(parentGuiScreen);
	            	break;
	
	            /////////////////////////////////////////////////////////////////////////
	            // Paging
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 10:	//Previous Page
	                DecrementTabbedPage();
	            	break;
	            case 11:	//Next Page
	                IncrementTabbedPage();
	            	break;
	            
	            /////////////////////////////////////////////////////////////////////////
	            // Info Line
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 100:
	            	screenTitle = Localization.get("infoline.name");
	            	DrawInfoLineButtons();
	            	break;
	            case 101:	//Enable/Disable
	            	InfoLine.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(InfoLine.Enabled);
	            	break;
	            case 102:	//Show Biome
	            	InfoLine.ToggleShowBiome();
	            	button.displayString = GetButtonLabel_Boolean("infoline.options.showbiome", InfoLine.ShowBiome);
	            	break;
	            case 105:	//Show if it can snow
	            	InfoLine.ToggleShowCanSnow();
	            	button.displayString = GetButtonLabel_Boolean("infoline.options.showcansnow", InfoLine.ShowCanSnow);
	            	break;
	            case 103:	//Horizontal location
	            	InfoLine.SetHorizontalLocation(((GuiNumberSlider)button).GetValueAsInteger());
	            	break;
	            case 104:	//Vertical location
	            	InfoLine.SetVerticalLocation(((GuiNumberSlider)button).GetValueAsInteger());
	            	break;
	            
	            /////////////////////////////////////////////////////////////////////////
	            // Clock
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 200:
	            	screenTitle = Localization.get("clock.name");
	            	DrawClockButtons();
	            	break;
	            case 201:	//Enable/Disable
	            	Clock.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(Clock.Enabled);
	            	break;
	            case 202:	//Mode
	            	Clock.Mode.ToggleMode();
	            	button.displayString = GetButtonLabel_Mode(Clock.Mode.GetFriendlyName());
	            	break;
	
	            /////////////////////////////////////////////////////////////////////////
	            // Coordinates
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 300:
	            	screenTitle = Localization.get("coordinates.name");
	            	DrawCoordinatesButtons();
	            	break;
	            case 301:	//Enable/Disable
	            	Coordinates.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(Coordinates.Enabled);
	            	break;
	            case 302:	//Y Colors
	            	Coordinates.ToggleUseYCoordinateColors();
	            	button.displayString = GetButtonLabel_Boolean("coordinates.options.useycoordinatecolors", Coordinates.UseYCoordinateColors);
	            	break;
	            case 303:	//Hotkey
	            	HotkeyButtonClicked((GuiHotkeyButton)button);
	            	break;
	            case 304:	//Mode
	            	Coordinates.Modes.ToggleMode();
	            	button.displayString = GetButtonLabel_Mode(Coordinates.Mode.GetFriendlyName());
	            	break;
	            case 305:	//Chunk coords
	            	Coordinates.ToggleShowChunkCoordinates();
	            	button.displayString = GetButtonLabel_Boolean("coordinates.options.showchunkcoordinates", Coordinates.ShowChunkCoordinates);
	            	break;
	
	            /////////////////////////////////////////////////////////////////////////
	            // Compass
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 400:
	            	screenTitle = Localization.get("compass.name");
	            	DrawCompassButtons();
	            	break;
	            case 401:	//Enable/Disable
	            	Compass.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(Compass.Enabled);
	            	
	            	break;
	
	            /////////////////////////////////////////////////////////////////////////
	            // FPS
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 500:
	            	screenTitle = Localization.get("fps.name");
	            	DrawFPSButtons();
	            	break;
	            case 501:	//Enable/Disable
	            	Fps.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(Fps.Enabled);
	            	break;
	
	            /////////////////////////////////////////////////////////////////////////
	            // Distance Measurer
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 600:
	            	screenTitle = Localization.get("distancemeasurer.name");
	            	DrawDistanceMeasurerButtons();
	            	break;
	            case 601:	//Enable/Disable
	            	DistanceMeasurer.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(DistanceMeasurer.Enabled);
	            	break;
	            case 602:	//Hotkey
	            	HotkeyButtonClicked((GuiHotkeyButton)button);
	            	break;
	            
	            /////////////////////////////////////////////////////////////////////////
	            // Safe Overlay
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 700:
	            	screenTitle = Localization.get("safeoverlay.name");
	            	DrawSafeOverlayButtons();
	            	break;
	            case 701:	//Enable/Disable
	            	SafeOverlay.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(SafeOverlay.Enabled);
	            	break;
	            case 702:	//Hotkey
	            	HotkeyButtonClicked((GuiHotkeyButton)button);
	            	break;
	            case 703:	//Draw distance slider
	            	int value = ((GuiNumberSlider)button).GetValueAsInteger();
	            	SafeOverlay.instance.SetDrawDistance(value);
	            	break;
	            case 704:	//Draw distance slider
	            	SafeOverlay.instance.SetUnsafeOverlayTransparency(((GuiNumberSlider)button).GetValueAsFloat());
	            	break;
	            case 705:	//Show in Nether
	            	SafeOverlay.instance.ToggleDisplayInNether();
	            	button.displayString = GetButtonLabel_Boolean("safeoverlay.options.displayinnether", SafeOverlay.instance.GetDisplayInNether());
	            	break;
	            case 706:	//X-ray
	            	SafeOverlay.instance.ToggleSeeUnsafePositionsThroughWalls();
	            	button.displayString = GetButtonLabel_Boolean("safeoverlay.options.seethroughwalls", SafeOverlay.instance.GetSeeUnsafePositionsThroughWalls());
	            	break;
	            
	            /////////////////////////////////////////////////////////////////////////
	            // Player Locator
	            /////////////////////////////////////////////////////////////////////////
	
	            case 800:
	            	screenTitle = Localization.get("playerlocator.name");
	            	DrawPlayerLocatorButtons();
	            	break;
	            case 801:	//Enable/Disable
	            	PlayerLocator.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(PlayerLocator.Enabled);
	            	break;
	            case 802:	//Hotkey
	            	HotkeyButtonClicked((GuiHotkeyButton)button);
	            	break;
	            case 803:	//Min view distance slider
	            	PlayerLocator.viewDistanceCutoff = ((GuiNumberSlider)button).GetValueAsInteger();
	            	break;
	            case 804:	//Show distance to players
	            	PlayerLocator.ToggleShowDistanceToPlayers();
	            	button.displayString = GetButtonLabel_Boolean("playerlocator.options.showdistancetoplayers", PlayerLocator.ShowDistanceToPlayers);
	            	break;
	            case 805:	//Show players health
	            	PlayerLocator.ToggleShowPlayerHealth();
	            	button.displayString = GetButtonLabel_Boolean("playerlocator.options.showplayerhealth", PlayerLocator.ShowPlayerHealth);
	            	break;
	            case 806:	//Show tamed wolves
	            	PlayerLocator.ToggleShowWolves();
	            	button.displayString = GetButtonLabel_Boolean("playerlocator.options.showwolves", PlayerLocator.ShowWolves);
	            	break;
	            case 807:	//Use wolf colors
	            	PlayerLocator.ToggleUseWolfColors();
	            	button.displayString = GetButtonLabel_Boolean("playerlocator.options.usewolfcolors", PlayerLocator.UseWolfColors);
	            	break;
	            case 808:	//Show wither skeletons
	            	PlayerLocator.ToggleShowWitherSkeletons();
	            	button.displayString = GetButtonLabel_Boolean("playerlocator.options.showwitherskeletons", PlayerLocator.ShowWitherSkeletons);
	            	break;
	            
	            /////////////////////////////////////////////////////////////////////////
	            // Horse Info
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 900:
	            	screenTitle = Localization.get("animalinfo.name");
	            	DrawAnimalInfoButtons();
	            	break;
	            case 901:	//Enable/Disable
	            	AnimalInfo.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(AnimalInfo.Enabled);
	            	break;
	            case 902:	//Hotkey
	            	HotkeyButtonClicked((GuiHotkeyButton)button);
	            	break;
	            case 903:	//Min view distance slider
	            	AnimalInfo.viewDistanceCutoff = ((GuiNumberSlider)button).GetValueAsInteger();
	            	break;
	            case 904:	//Decimal slider
	            	AnimalInfo.SetNumberOfDecimalsDisplayed(((GuiNumberSlider)button).GetValueAsInteger());
	            	break;
	            case 905:	//Show on F3 menu
	            	AnimalInfo.ToggleShowHorseStatsOnF3Menu();
	            	button.displayString = GetButtonLabel_Boolean("animalinfo.options.showhorsestatsonf3menu", AnimalInfo.ShowHorseStatsOnF3Menu);
	            	break;
	            case 906:	//Show on F3 menu
	            	AnimalInfo.ToggleShowHorseStatsOverlay();
	            	button.displayString = GetButtonLabel_Boolean("animalinfo.options.showhorsestatsoverlay", AnimalInfo.ShowHorseStatsOverlay);
	            	break;
	            case 907:	//Show text backgrounds
	            	AnimalInfo.ToggleShowTextBackgrounds();
	            	button.displayString = GetButtonLabel_Boolean("animalinfo.options.showtextbackground", AnimalInfo.ShowTextBackgrounds);
	            	break;
	            case 916:	//Toggle showing breeding icons
	            	AnimalInfo.ToggleShowBreedingIcons();
	            	button.displayString = GetButtonLabel_Boolean("animalinfo.options.showbreedingicons", AnimalInfo.ShowBreedingIcons);
	            	break;
	            /*
	            case 917:	//Toggle showing breeding timers
	            	AnimalInfo.ToggleShowBreedingTimers();
	            	button.displayString = GetButtonLabel_Boolean("animalinfo.options.showbreedingtimers", AnimalInfo.ShowBreedingTimers);
	            	break;
	            */
	            
	            /////////////////////////////////////////////////////////////////////////
	            // Potion Timers
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 1000:
	            	screenTitle = Localization.get("potiontimers.name");
	            	DrawPotionTimerButtons();
	            	break;
	            case 1001:	//Enable/Disable
	            	PotionTimers.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(PotionTimers.Enabled);
	            	break;
	            case 1002:	//Show potion icons
	            	PotionTimers.ToggleShowPotionIcons();
	            	button.displayString = GetButtonLabel_Boolean("potiontimers.options.showpotionicons", PotionTimers.ShowPotionIcons);
	            	break;
	            case 1005:	//Show potion colors
	            	PotionTimers.ToggleUsePotionColors();
	            	button.displayString = GetButtonLabel_Boolean("potiontimers.options.usepotioncolors", PotionTimers.UsePotionColors);
	            	break;
	            case 1007:	//Hide default potion effects in inveotyr
	            	PotionTimers.ToggleHidePotionEffectsInInventory();
	            	button.displayString = GetButtonLabel_Boolean("potiontimers.options.hidepotioneffectsininventory", PotionTimers.HidePotionEffectsInInventory);
	            	break;
	            case 1006:	//Potion scale slider
	            	PotionTimers.PotionScale = ((GuiNumberSlider)button).GetValueAsFloat();
	            	break;
	            case 1003:	//Horizontal location
	            	PotionTimers.SetHorizontalLocation(((GuiNumberSlider)button).GetValueAsInteger());
	            	break;
	            case 1004:	//Vertical location
	            	PotionTimers.SetVerticalLocation(((GuiNumberSlider)button).GetValueAsInteger());
	            	break;
	            
	            /////////////////////////////////////////////////////////////////////////
	            // Durability Info
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 1100:
	            	screenTitle = Localization.get("durabilityinfo.name");
	            	DrawDurabilityInfoButtons();
	            	break;
	            case 1101:	//Enable/Disable
	            	DurabilityInfo.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(DurabilityInfo.Enabled);
	            	break;
	            case 1102:	//Enable Armor
	            	DurabilityInfo.ToggleShowArmorDurability();
	            	button.displayString = GetButtonLabel_Boolean("durabilityinfo.options.showarmordurability", DurabilityInfo.ShowArmorDurability);
	            	break;
	            case 1103:	//Armor durability threshold slider
	            	DurabilityInfo.SetDurabilityDisplayThresholdForArmor(((GuiNumberSlider)button).GetValueAsFloat());
	            	break;
	            case 1104:	//Show armor icons
	            	DurabilityInfo.ToggleShowIndividualArmorIcons();
	            	button.displayString = GetButtonLabel_Boolean("durabilityinfo.options.showindividualarmoricons", DurabilityInfo.ShowIndividualArmorIcons);
	            	break;
	            case 1105:	//Enable Items
	            	DurabilityInfo.ToggleShowItemDurability();
	            	button.displayString = GetButtonLabel_Boolean("durabilityinfo.options.showitemdurability", DurabilityInfo.ShowItemDurability);
	            	break;
	            case 1106:	//Item  durability threshold slider
	            	DurabilityInfo.SetDurabilityDisplayThresholdForItem(((GuiNumberSlider)button).GetValueAsFloat());
	            	break;
	            case 1108:	//Horizontal location
	            	DurabilityInfo.SetHorizontalLocation(((GuiNumberSlider)button).GetValueAsInteger());
	            	break;
	            case 1109:	//Vertical location
	            	DurabilityInfo.SetVerticalLocation(((GuiNumberSlider)button).GetValueAsInteger());
	            	break;
	            case 1110:	//Show as Percent
	            	DurabilityInfo.ToggleShowDamageAsPercent();
	            	button.displayString = GetButtonLabel_Boolean("durabilityinfo.options.showdamageaspercent", DurabilityInfo.ShowDamageAsPercentage);
	            	break;
	            case 1111:	//Auto unequip Armor
	            	DurabilityInfo.ToggleAutoUnequipArmor();
	            	button.displayString = GetButtonLabel_Boolean("durabilityinfo.options.autounequiparmor", DurabilityInfo.AutoUnequipArmor);
	            	break;
	            case 1112:	//Auto unequip Tools
	            	DurabilityInfo.ToggleAutoUnequipTools();
	            	button.displayString = GetButtonLabel_Boolean("durabilityinfo.options.autounequiptools", DurabilityInfo.AutoUnequipTools);
	            	break;
	            case 1113:	//Use colored numbers
	            	DurabilityInfo.ToggleUseColoredNumbers();
	            	button.displayString = GetButtonLabel_Boolean("durabilityinfo.options.usecolorednumbers", DurabilityInfo.UseColoredNumbers);
	            	break;
	            case 1114:	//Durability scale slider
	            	DurabilityInfo.DurabilityScale = ((GuiNumberSlider)button).GetValueAsFloat();
	            	break;
	            
	            
	        	
	            
	            /////////////////////////////////////////////////////////////////////////
	            // Ender Pearl Aid
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 1200:
	            	screenTitle = Localization.get("enderpearlaid.name");
	            	DrawEnderPearlAidButtons();
	            	break;
	            case 1201:	//Enabled/Disabled
	            	EnderPearlAid.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(EnderPearlAid.Enabled);
	            	break;
	            case 1202:	//Hotkey
	            	HotkeyButtonClicked((GuiHotkeyButton)button);
	            	break;
	            
	            /////////////////////////////////////////////////////////////////////////
	            // Eating Aid
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 1300:
	            	screenTitle = Localization.get("eatingaid.name");
	            	DrawEatingAidButtons();
	            	break;
	            case 1301:	//Enabled/Disabled
	            	EatingAid.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(EatingAid.Enabled);
	            	break;
	            case 1302:	//Hotkey
	            	HotkeyButtonClicked((GuiHotkeyButton)button);
	            	break;
	            case 1303:	//Eating Mode
	            	EatingAid.Modes.ToggleMode();
	            	button.displayString = GetButtonLabel_Mode(EatingAid.Mode.GetFriendlyName());
	            	break;
	            case 1304:	//Eat golden food
	            	EatingAid.ToggleEatingGoldenFood();
	            	button.displayString = GetButtonLabel_Boolean("eatingaid.options.eatgoldenfood", EatingAid.EatGoldenFood);
	            	break;
	            case 1306:	//Eat raw food
	            	EatingAid.ToggleEatingRawFood();
	            	button.displayString = GetButtonLabel_Boolean("eatingaid.options.eatrawfood", EatingAid.EatRawFood);
	            	break;
	            case 1305:	//Prioritize food in hotbar
	            	EatingAid.TogglePrioritizeFoodInHotbar();
	            	button.displayString = GetButtonLabel_Boolean("eatingaid.options.prioritizefoodinhotbar", EatingAid.PrioritizeFoodInHotbar);
	            	break;
	            case 1307:	//Use PvP Soup
	            	EatingAid.ToggleUsePvPSoup();
	            	button.displayString = GetButtonLabel_Boolean("eatingaid.options.usepvpsoup", EatingAid.UsePvPSoup);
	            	break;
	            
	            /////////////////////////////////////////////////////////////////////////
	            // Potion Aid
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 1400:
	            	screenTitle = Localization.get("potionaid.name");
	            	DrawPotionAidButtons();
	            	break;
	            case 1401:	//Enabled/Disabled
	            	PotionAid.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(PotionAid.Enabled);
	            	break;
	            case 1402:	//Hotkey
	            	HotkeyButtonClicked((GuiHotkeyButton)button);
	            	break;
	            
	            
	            /////////////////////////////////////////////////////////////////////////
	            // Weapon Swapper
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 1500:
	            	screenTitle = Localization.get("weaponswapper.name");
	            	DrawWeaponSwapperButtons();
	            	break;
	            case 1501:	//Enabled/Disabled
	            	WeaponSwapper.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(WeaponSwapper.Enabled);
	            	break;
	            case 1502:	//Hotkey
	            	HotkeyButtonClicked((GuiHotkeyButton)button);
	            	break;
	            
	            
	            /////////////////////////////////////////////////////////////////////////
	            // Quick Deposit
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 1600:
	            	screenTitle = Localization.get("quickdeposit.name");
	            	DrawQuickDepositButtons();
	            	break;
	            case 1601:	//Enabled/Disabled
	            	QuickDeposit.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(QuickDeposit.Enabled);
	            	break;
	            case 1602:	//Hotkey
	            	HotkeyButtonClicked((GuiHotkeyButton)button);
	            	break;
	            case 1603:	//Ignore hotbar
	            	QuickDeposit.ToggleIgnoreItemsInHotbar();
	            	button.displayString = GetButtonLabel_Boolean("quickdeposit.options.ignoreitemsinhotbar", QuickDeposit.IgnoreItemsInHotbar);
	            	break;
	            case 1604:	//Closes chest
	            	QuickDeposit.ToggleCloseChestAfterDepositing();
	            	button.displayString = GetButtonLabel_Boolean("quickdeposit.options.closechestafterdepositing", QuickDeposit.CloseChestAfterDepositing);
	            	break;
	            case 1605:	//Blacklist torches
	            	QuickDeposit.ToggleBlacklistTorch();
	            	button.displayString = GetButtonLabel_Boolean("quickdeposit.options.blacklisttorch", QuickDeposit.BlacklistTorch);
	            	break;
	            case 1606:	//Blacklist arrows
	            	QuickDeposit.ToggleBlacklistArrow();
	            	button.displayString = GetButtonLabel_Boolean("quickdeposit.options.blacklistarrow", QuickDeposit.BlacklistArrow);
	            	break;
	            case 1607:	//Blacklist food
	            	QuickDeposit.ToggleBlacklistFood();
	            	button.displayString = GetButtonLabel_Boolean("quickdeposit.options.blacklistfood", QuickDeposit.BlacklistFood);
	            	break;
	            case 1608:	//Blacklist ender pearls
	            	QuickDeposit.ToggleBlacklistEnderPearl();
	            	button.displayString = GetButtonLabel_Boolean("quickdeposit.options.blacklistenderpearl", QuickDeposit.BlacklistEnderPearl);
	            	break;
	            case 1609:	//Blacklist water buckets
	            	QuickDeposit.ToggleBlacklistWaterBucket();
	            	button.displayString = GetButtonLabel_Boolean("quickdeposit.options.blacklistwaterbucket", QuickDeposit.BlacklistWaterBucket);
	            	break;
	            case 1610:	//Blacklist clock/compass
	            	QuickDeposit.ToggleBlacklistClockCompass();
	            	button.displayString = GetButtonLabel_Boolean("quickdeposit.options.blacklistclockcompass", QuickDeposit.BlacklistClockCompass);
	            	break;
	            case 1611:	//Blacklist weapons
	            	QuickDeposit.ToggleBlacklistWeapons();
	            	button.displayString = GetButtonLabel_Boolean("quickdeposit.options.blacklistweapons", QuickDeposit.BlacklistWeapons);
	            	break;
	            	

                /////////////////////////////////////////////////////////////////////////
                // Item Selector
                /////////////////////////////////////////////////////////////////////////
                case 1700:
                    screenTitle = Localization.get("itemselector.name");
                    DrawItemSelectorButtons();
                    break;
                case 1701:  //Enabled/Disabled
                    ItemSelector.ToggleEnabled();
                    button.displayString = GetButtonLabel_Enabled(ItemSelector.Enabled);
                    break;
                case 1702:  //Hotkey
                    HotkeyButtonClicked((GuiHotkeyButton)button);
                    break;
                case 1703:  //Ticks slider
                    int itemSelectorTicks = ((GuiNumberSlider)button).GetValueAsInteger();
                    ItemSelector.SetTimeout(itemSelectorTicks);
                    break;
                case 1704:  //Mode
                    ItemSelector.Modes.ToggleMode();
                    button.displayString = GetButtonLabel_Mode(ItemSelector.Mode.GetFriendlyName());
                    break;
                case 1705:  //Side buttons
                    ItemSelector.ToggleUseMouseSideButtons();
                    button.displayString = GetButtonLabel_Boolean("itemselector.options.sideButtons", ItemSelector.UseMouseSideButtons);
                    break;
                
                
	            /////////////////////////////////////////////////////////////////////////
	            // Health Monitor
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 1800:
	            	screenTitle = Localization.get("healthmonitor.name");
	            	DrawHealthMonitorButtoins();
	            	break;
	            case 1801:	//Enable/Disable
	            	HealthMonitor.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(HealthMonitor.Enabled);
	            	break;
	            case 1802:	//Mode
	            	HealthMonitor.Modes.ToggleMode();
	            	HealthMonitor.PlayLowHealthSound();
	            	button.displayString = GetButtonLabel_Mode(HealthMonitor.Mode.GetFriendlyName());
	            	break;
	            case 1803:	//Play sound
	            	HealthMonitor.PlayLowHealthSound();
	            	break;
	            case 1804:	//Play faster near death
	            	HealthMonitor.TogglePlayFasterNearDeath();
	            	button.displayString = GetButtonLabel_Boolean("healthmonitor.options.playfasterneardeath", HealthMonitor.PlayFasterNearDeath);
	            	break;
	            case 1805:	//Low Health Sound Threshold
	            	HealthMonitor.SetLowHealthSoundThreshold(((GuiNumberSlider)button).GetValueAsInteger());
	            	break;
	                
	                
	            /////////////////////////////////////////////////////////////////////////
	            // Torch Aid
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 1900:
	            	screenTitle = Localization.get("torchaid.name");
	            	DrawTorchAidButtons();
	            	break;
	            case 1901:	//Enable/Disable
	            	TorchAid.ToggleEnabled();
	            	button.displayString = GetButtonLabel_Enabled(TorchAid.Enabled);
	            	break;
	                
	                
	            /////////////////////////////////////////////////////////////////////////
	            // Miscellaneous
	            /////////////////////////////////////////////////////////////////////////
	            
	            case 2000:
	            	screenTitle = Localization.get("miscellaneous.name");
	            	DrawMiscellaneousButtons();
	            	break;
	            case 2001:	//Use enhanced middle click
	            	Miscellaneous.ToggleUseEnchancedMiddleClick();
	            	button.displayString = GetButtonLabel_Boolean("miscellaneous.options.useenhancedmiddleclick", Miscellaneous.UseEnhancedMiddleClick);
	            	break;
	            case 2002:	//Use quick place sign
	            	Miscellaneous.ToggleUseQuickPlaceSign();
	            	button.displayString = GetButtonLabel_Boolean("miscellaneous.options.usequickplacesign", Miscellaneous.UseQuickPlaceSign);
	            	break;
	            case 2003:	//Use unlimited sprinting
	            	Miscellaneous.ToggleUseUnlimitedSprinting();
	            	button.displayString = GetButtonLabel_Boolean("miscellaneous.options.useunlimitedsprinting", Miscellaneous.UseUnlimitedSprinting);
	            	break;
	            case 2004:	//Show anvil repairs
	            	Miscellaneous.ToggleShowAnvilRepairs();
	            	button.displayString = GetButtonLabel_Boolean("miscellaneous.options.showanvilrepairs", Miscellaneous.ShowAnvilRepairs);
	            	break;
	            	
	            
            }
        }
    }
    

	protected String GetButtonTooltip(int buttonId)
	{
		//this is where we set all of our button tooltips
		switch (buttonId)
		{
			case 100: return Localization.get("infoline.options.tooltip");
			case 105: return Localization.get("infoline.options.showcansnow.tooltip");
			case 202: return Localization.get("clock.options.mode.tooltip");
			case 300: return Localization.get("coordinates.options.tooltip");
			case 302: return Localization.get("coordinates.options.useycoordinatecolors.tooltip");
			case 303: return Localization.get("coordinates.options.hotkey.tooltip");
			case 305: return Localization.get("coordinates.options.showchunkcoordinates.tooltip");
			case 700: return Localization.get("safeoverlay.options.tooltip");
			case 702: return Localization.get("safeoverlay.options.hotkey.tooltip");
			case 703: return Localization.get("safeoverlay.options.drawdistance.tooltip");
			case 705: return Localization.get("safeoverlay.options.displayinnether.tooltip");
			case 600: return Localization.get("distancemeasurer.options.tooltip");
			case 800: return Localization.get("playerlocator.options.tooltip");
			case 803: return Localization.get("playerlocator.options.minviewdistance.tooltip");
			case 806: return Localization.get("playerlocator.options.showwolves.tooltip");
			case 807: return Localization.get("playerlocator.options.usewolfcolors.tooltip");
			case 808: return Localization.get("playerlocator.options.showwitherskeletons.tooltip");
			case 900: return Localization.get("animalinfo.options.tooltip");
			case 907: return Localization.get("animalinfo.options.showtextbackground.tooltip");
			case 905: return Localization.get("animalinfo.options.showhorsestatsonf3menu.tooltip");
			case 906: return Localization.get("animalinfo.options.showhorsestatsoverlay.tooltip");
			case 916: return Localization.get("animalinfo.options.showbreedingicons.tooltip");
			//case 917: return Localization.get("animalinfo.options.showbreedingtimers.tooltip");
			case 1000: return Localization.get("potiontimers.options.tooltip");
			case 1007: return Localization.get("potiontimers.options.hidepotioneffectsininventory.tooltip");
			case 1100: return Localization.get("durabilityinfo.options.tooltip");
			case 1103: return Localization.get("durabilityinfo.options.armordurabilitythreshold.tooltip");
			case 1104: return Localization.get("durabilityinfo.options.showindividualarmoricons.tooltip");
			case 1106: return Localization.get("durabilityinfo.options.itemdurabilitythreshold.tooltip");
			case 1110: return Localization.get("durabilityinfo.options.showdamageaspercent.tooltip");
			case 1111: return Localization.get("durabilityinfo.options.autounequiparmor.tooltip");
			case 1112: return Localization.get("durabilityinfo.options.autounequiptools.tooltip");
			case 1200: return Localization.get("enderpearlaid.options.tooltip");
			case 1300: return Localization.get("eatingaid.options.tooltip");
			case 1303: return Localization.get("eatingaid.options.mode.tooltip");
			case 1307: return Localization.get("eatingaid.options.usepvpsoup.tooltip");
			case 1400: return Localization.get("potionaid.options.tooltip");
			case 1500: return Localization.get("weaponswapper.options.tooltip");
			case 1503: return Localization.get("weaponswapper.options.scanhotbarforweaponsfromlefttoright.tooltip");
			case 1600: return Localization.get("quickdeposit.options.tooltip");
			case 1602: return Localization.get("quickdeposit.options.hotkey.tooltip");
			case 1603: return Localization.get("quickdeposit.options.ignoreitemsinhotbar.tooltip");
			case 1604: return Localization.get("quickdeposit.options.closechestafterdepositing.tooltip");
			case 1700: return Localization.get("itemselector.options.tooltip");
			case 1702: return Localization.get("itemselector.options.hotkey.tooltip");
			case 1703: return Localization.get("itemselector.options.ticks.tooltip");
            case 1704: return Localization.get("itemselector.options.mode.tooltip");
            case 1705: return Localization.get("itemselector.options.sideButtons.tooltip");
			case 1800: return Localization.get("healthmonitor.options.tooltip");
			case 1802: return Localization.get("healthmonitor.options.mode.tooltip");
			case 1803: return Localization.get("healthmonitor.options.mode.play.tooltip");
			case 1804: return Localization.get("healthmonitor.options.playfasterneardeath.tooltip");
			case 1900: return Localization.get("torchaid.options.tooltip");
			case 2001: return Localization.get("miscellaneous.options.useenhancedmiddleclick.tooltip");
			case 2002: return Localization.get("miscellaneous.options.usequickplacesign.tooltip");
			case 2003: return Localization.get("miscellaneous.options.useunlimitedsprinting.tooltip");
			case 2004: return Localization.get("miscellaneous.options.showanvilrepairs.tooltip");
			default: return null;
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
            	currentlySelectedHotkeyButton.ApplyHotkey(keycode);
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
    		currentlySelectedTabButton = null;
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
    		currentlySelectedTabButton = null;
    		DrawAllButtons();
    	}
    }
    

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, screenTitle, width / 2, 15, 0xFFFFFF);
        
        DrawMiscText();
        
        super.drawScreen(par1, par2, par3);
    }
    
    /**
     * Called when the screen is unloaded.
     */
    public void onGuiClosed()
    {
    	ZyinHUDConfig.SaveConfigSettings();
    	
    	super.onGuiClosed();
    }
}
