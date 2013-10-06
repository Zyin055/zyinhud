package zyin.zyinhud.mods;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import zyin.zyinhud.util.InventoryUtil;
import zyin.zyinhud.util.Localization;
import zyin.zyinhud.util.ZyinHUDUtil;

/**
 * Eating Helper allows the player to eat food in their inventory by calling its Eat() method.
 */
public class EatingAid
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
    
    public static String Hotkey;
    public static final String HotkeyDescription = "Zyin's HUD: Eating Aid";
    
	/**
	 * 0=basic<br>
	 * 1=intelligent<br>
	 */
    public static int Mode = 0;
    
    /** The maximum number of modes that is supported */
    public static int NumberOfModes = 2;
    
    /** Such as golden carrots, golden apples */
    public static boolean EatGoldenFood;
    public static boolean PrioritizeFoodInHotbar;
    
    
    private Minecraft mc = Minecraft.getMinecraft();
    private Timer timer = new Timer();
    private TimerTask swapTimerTask;
    private TimerTask eatTimerTask;

    private Robot r = null;
    private boolean isCurrentlyEating;
    private boolean previousEatFromHotbar;

    private int foodItemIndex;
    private int currentItemInventoryIndex;
    private int currentItemHotbarIndex;
    
    /**
     * Use this instance for all method calls.
     */
    public static EatingAid instance = new EatingAid();

    private EatingAid()
    {
        try
        {
            r = new Robot();
        }
        catch (AWTException e)
        {
            e.printStackTrace();
        }

        isCurrentlyEating = false;
        previousEatFromHotbar = false;
    }

    /**
     * Makes the player eat a food item on their hotbar or in their inventory.
     */
    public void Eat()
    {
        //currentItemStack.onFoodEaten(mc.theWorld, mc.thePlayer);	//INSTANT EATING (single player only?)
    	
        //make sure we're not about to click on a right-clickable thing
        if(ZyinHUDUtil.IsMouseoveredBlockRightClickable())
        	return;

        if (isCurrentlyEating)
        {
            //if we're eating and we try to eat again, then cancel whatever we're eating
            //by releasing right click, and swapping the food back to its correct position
            StopEating();
            return;
        }
        else
        {
            //we need to eat something by first finding the best food to eat, then eat it
            if (!mc.thePlayer.getFoodStats().needFood())
            {
                //if we're not hungry then don't do anything
                return;
            }
            
            foodItemIndex = GetFoodItemIndexFromInventory();
        	if (foodItemIndex < 0)
            {
                InfoLine.DisplayNotification(Localization.get("eatingaid.nofood"));
                return;
            }
            
            if(foodItemIndex  > 35 && foodItemIndex < 45)	//on the hotbar
            {
            	StartEatingFromHotbar(foodItemIndex);
            }
            else //if(foodItemIndex  > 8 && foodItemIndex < 36)	//in the inventory
            {
            	StartEatingFromInventory(foodItemIndex);
            }
            
        }
    }
    
    /**
     * Changes the selected index in your hotbar to where the food is, then eats it.
     * @param foodHotbarIndex 36-44
     */
    private void StartEatingFromHotbar(int foodHotbarIndex)
    {
    	if(foodHotbarIndex < 36 | foodHotbarIndex > 44)
    		return;
    	
    	currentItemHotbarIndex = mc.thePlayer.inventory.currentItem;
    	foodHotbarIndex = InventoryUtil.TranslateInventoryIndexToHotbarIndex(foodHotbarIndex);
    	
    	int previouslySelectedHotbarSlotIndex = mc.thePlayer.inventory.currentItem;
    	mc.thePlayer.inventory.currentItem = foodHotbarIndex;

        r.mousePress(InputEvent.BUTTON3_MASK); //perform a right click
        isCurrentlyEating = true;
        previousEatFromHotbar = true;
        
    	ItemStack currentItemStack = mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem];
    	ItemFood currentFood = (ItemFood)currentItemStack.getItem();
        int eatingDurationInMilliseconds = 1000*currentFood.itemUseDuration / 20;
        
        //after this timer runs out we'll release right click to stop eating and select the previously selected item
        eatTimerTask = new StopEatingTimerTask(r, previouslySelectedHotbarSlotIndex);
        timer.schedule(eatTimerTask, eatingDurationInMilliseconds + InventoryUtil.suggestedItemSwapDelay);
    }
    
    /**
     * Swaps an item from your inventory into your hotbar, then eats it.
     * @param foodInventoryIndex 9-35
     */
    private void StartEatingFromInventory(int foodInventoryIndex)
    {
    	if(foodInventoryIndex < 9 | foodInventoryIndex > 35)
    		return;
    	
        currentItemInventoryIndex = InventoryUtil.GetCurrentlySelectedItemInventoryIndex();
        InventoryUtil.Swap(currentItemInventoryIndex, foodInventoryIndex);
        
        r.mousePress(InputEvent.BUTTON3_MASK); //perform a right click
        isCurrentlyEating = true;
        previousEatFromHotbar = false;
        
        ItemStack currentItemStack = mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem];
        ItemFood currentFood = (ItemFood)currentItemStack.getItem();
        int eatingDurationInMilliseconds = 1000 * currentFood.itemUseDuration / 20;
        
        //after this timer runs out we'll release right click to stop eating
        eatTimerTask = new StopEatingTimerTask(r);
        timer.schedule(eatTimerTask, eatingDurationInMilliseconds);
        swapTimerTask = InventoryUtil.instance.SwapWithDelay(currentItemInventoryIndex, foodInventoryIndex,
        		eatingDurationInMilliseconds + InventoryUtil.suggestedItemSwapDelay);
    }
    
    
    
    

    /**
     * Stops eating by releasing right click and moving the food back to its original position.
     */
    public void StopEating()
    {
    	if(previousEatFromHotbar)
    		StopEatingFromHotbar();
    	else
    		StopEatingFromInventory();
    }

    private void StopEatingFromInventory()
    {
        r.mouseRelease(InputEvent.BUTTON3_MASK); //release right click
        eatTimerTask.cancel();
    	swapTimerTask.cancel();
        InventoryUtil.Swap(currentItemInventoryIndex, foodItemIndex);
        isCurrentlyEating = false;
    }
    private void StopEatingFromHotbar()
    {
        r.mouseRelease(InputEvent.BUTTON3_MASK); //release right click
        eatTimerTask.cancel();
        mc.thePlayer.inventory.currentItem = currentItemHotbarIndex;
        isCurrentlyEating = false;
    }

    /**
     * Are we currently eating food?
     * @return
     */
    public boolean isEating()
    {
        return isCurrentlyEating;
    }
    
    
    
    public int GetFoodItemIndexFromInventory()
    {
    	if(Mode == 0)
    	{
    		//basic mode
    		return GetStrongestFoodItemIndexFromInventory();
    	}
    	else if(Mode == 1)
    	{
    		//intelligent mode
    		return GetBestFoodItemIndexFromInventory();
    	}
    	else
    	{
    		return -2;
    	}
    }
    
    /**
     * Finds the food with the highest saturation value and returns its index in your inventory.
     * @return
     */
    public int GetStrongestFoodItemIndexFromInventory()
    {
    	List inventorySlots = mc.thePlayer.inventoryContainer.inventorySlots;
        int bestFoodMatchIndex = -1;
        float bestFoodMatchSaturation = 0;
        int foodLevel = mc.thePlayer.getFoodStats().getFoodLevel();	//max 20
        
        //iterate over the hotbar (36-44), then main inventory (9-35)
        for (int i = inventorySlots.size() - 1; i > 8; i--)
        {
        	if(PrioritizeFoodInHotbar
        		&& i == 35 && bestFoodMatchIndex > -1)
        		return bestFoodMatchIndex;
        	
        	
            Slot slot = (Slot)inventorySlots.get(i);
            ItemStack itemStack = slot.getStack();

            if (itemStack == null)
            {
                continue;
            }

            Item item = itemStack.getItem();

            if (item instanceof ItemFood)
            {
                ItemFood food = (ItemFood)item;
                float saturation = food.getSaturationModifier();
                
                if (item.equals(item.goldenCarrot)
                        || item.equals(item.appleGold))
                {
                    if (!EatGoldenFood)
                    {
                        continue;
                    }

                    saturation = 0.0001f;	//setting the saturation value low will make it unappealing to the food selection algorithm
                }
                else if (item.equals(item.rottenFlesh)
                         || item.equals(item.poisonousPotato)
                         || item.equals(item.spiderEye))
                {
                	saturation = 0.0002f;	//setting the saturation value low will make it unappealing to the food selection algorithm
                }
                
                if(saturation > bestFoodMatchSaturation)
                {
                	bestFoodMatchIndex = i;
                	bestFoodMatchSaturation = saturation;
                	continue;
                }
            }
        }

        if (bestFoodMatchIndex > -1)
            return bestFoodMatchIndex;
        else
            return -1;
    }
    
    
    /**
     * Determines the best food that you can eat and returns its index in your inventory.
     * The best food is defined by not over eating (not wasting food), but still healing the most hunger.
     * @return the index in your inventory that has the best food to eat (9-34), or -1 if no food found.
     */
    public int GetBestFoodItemIndexFromInventory()
    {
        List inventorySlots = mc.thePlayer.inventoryContainer.inventorySlots;
        int bestFoodMatchIndex = -1;
        int bestFoodMatchOvereat = 999;
        int bestFoodMatchEat = -999;
        int foodLevel = mc.thePlayer.getFoodStats().getFoodLevel();	//max 20
        
        //iterate over the hotbar (36-44), then main inventory (9-35)
        for (int i = inventorySlots.size() - 1; i > 8; i--)
        {
        	if(PrioritizeFoodInHotbar
        		&& i == 35 && bestFoodMatchIndex > -1)
        		return bestFoodMatchIndex;
        	
        	
            Slot slot = (Slot)inventorySlots.get(i);
            ItemStack itemStack = slot.getStack();

            if (itemStack == null)
            {
                continue;
            }

            Item item = itemStack.getItem();

            if (item instanceof ItemFood)
            {
                ItemFood food = (ItemFood)item;
                int foodNeeded = 20 - foodLevel;	//amount of hunger needed to be full
                int eat = food.getHealAmount();		//amount of hunger restored by eating this food
                int overeat = foodNeeded - eat;
                overeat = (overeat > 0) ? 0 : Math.abs(overeat);	//positive number, amount we would overeat by eating this food

                if (item.equals(item.goldenCarrot)
                        || item.equals(item.appleGold))
                {
                    if (!EatGoldenFood)
                    {
                        continue;
                    }

                    overeat = 999;	//setting the overeat value high will make it unappealing to the food selection algorithm
                }
                else if (item.equals(item.rottenFlesh)
                         || item.equals(item.poisonousPotato)
                         || item.equals(item.spiderEye))
                {
                    overeat = 998;	//setting the overeat value high will make it unappealing to the food selection algorithm
                }
                
                //this food is better if we overeat less, or the overeat is the same but it heals more hunger
                if (bestFoodMatchOvereat > overeat ||
                        ((overeat == bestFoodMatchOvereat) && (eat > bestFoodMatchEat)))
                {
                    bestFoodMatchIndex = i;
                    bestFoodMatchOvereat = overeat;
                    bestFoodMatchEat = eat;
                    continue;
                }
            }
        }

        if (bestFoodMatchIndex > -1)
            return bestFoodMatchIndex;
        else
            return -1;
    }


    /**
     * Toggles the whether you eat golden food or not
     * @return The state it was changed to
     */
    public static boolean ToggleEatingGoldenFood()
    {
    	EatGoldenFood = !EatGoldenFood;
    	return EatGoldenFood;
    }
    
    /**
     * Toggles the prioritizing food in hotbar
     * @return The state it was changed to
     */
    public static boolean TogglePrioritizeFoodInHotbar()
    {
    	PrioritizeFoodInHotbar = !PrioritizeFoodInHotbar;
    	return PrioritizeFoodInHotbar;
    }
    
    /**
     * Increments the Eating Aid mode
     * @return The new Eating Aid mode
     */
    public static int ToggleMode()
    {
    	Mode++;
    	if(Mode >= NumberOfModes)
    		Mode = 0;
    	return Mode;
    }
    
    
    private class StopEatingTimerTask extends TimerTask
    {
        private Robot r;
        private int hotbarIndexToBeSelected = -1;

        /**
         * Helper class whose purpose is to release right click and set our status to not eating.
         */
        StopEatingTimerTask(Robot r)
        {
            this.r = r;
        }
        /**
         * Helper class whose purpose is to release right click, set our status to not eating, and select a hotbar index.
         */
        StopEatingTimerTask(Robot r, int hotbarIndexToBeSelected)
        {
            this.r = r;
            this.hotbarIndexToBeSelected = hotbarIndexToBeSelected;
        }

        @Override
        public void run()
        {
            r.mouseRelease(InputEvent.BUTTON3_MASK); //release right click
            isCurrentlyEating = false;
            
            if(hotbarIndexToBeSelected > -1)
            {
            	mc.thePlayer.inventory.currentItem = hotbarIndexToBeSelected;
            }
        }
    }
}
