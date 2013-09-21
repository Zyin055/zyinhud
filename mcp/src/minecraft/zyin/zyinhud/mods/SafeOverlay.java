package zyin.zyinhud.mods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCake;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockHalfSlab;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockWall;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import org.lwjgl.opengl.GL11;

import zyin.zyinhud.util.FontCodes;
import zyin.zyinhud.util.Localization;

/**
 * The Safe Overlay renders an overlay onto the game world showing which areas
 * mobs can spawn on.
 */
public class SafeOverlay
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
    public static final String HotkeyDescription = "ZyinHUD: Safe Overlay";
    
	/**
	 * 0=off<br>
	 * 1=on<br>
	 */
    public static int Mode = 0;
    
    /** The maximum number of modes that is supported */
    public static int NumberOfModes = 2;
    
    /**
     * Time in MS between re-calculations. This value changes based on the drawDistance
     * from updateFrequencyMin to updateFrequencyMax.
     * <p>
     * Examples:
     * <br>drawDistance = 2, updateFrequency = 100ms
     * <br>drawDistance = 20, updateFrequency = ~1000ms
     * <br>drawDistance = 175, updateFrequency = 8000ms
     */
    protected int updateFrequency;
    /**
     * The fastest the update frequency should be set to, in milliseconds.
     * It will be set to this value when the drawDistance = minDrawDistance.
     */
    protected static final  int updateFrequencyMin = 100;
    /**
     * The slowest the update frequency should be set to, in milliseconds.
     * It will be set to this value when the drawDistance = maxDrawDistance.
     */
    protected static final int updateFrequencyMax = 8000;

    /**
     * USE THE Getter/Setter METHODS FOR THIS!!
     * <p>
     * Calculate locations in a cube with this radius around the player.
     * <br>
     * Actual area calculated: (drawDistance*2)^3
     * <p>
     * drawDistance = 2 = 64 blocks (min)
     * <br>
     * drawDistance = 20 = 64,000 blocks (default)
     * <br>
     * drawDistance = 80 = 4,096,000 blocks
     * <br>
     * drawDistance = 175 = 42,875,000 blocks (max)
     */
    protected int drawDistance = 20;
    public static final int defaultDrawDistance = 20;
    public static final int minDrawDistance = 2;	//can't go lower than 2. setting this to 1 dispays nothing
    public static final int maxDrawDistance = 175;	//175 is the edge of the visible map on far

    /**
     * The transprancy of the "X" marks when rendered, between (0.1 and 1]
     */
    private float unsafeOverlayTransparency;
    private float unsafeOverlayMinTransparency = 0.11f;
    private float unsafeOverlayMaxTransparency = 1f;

    /**
     * The last time the overlay cache was generated
     */
    private long lastGenerate;

    public boolean displayInNether = false;
    private boolean renderUnsafePositionsThroughWalls = false;

    private Position playerPosition;
    private Position cachePosition = new Position();
    private static List<Position> unsafePositionCache;

    private Minecraft mc;
    private EntityPlayer player;

    /**
     * When this flag is set to true Safe Overlay will recalculate the unsafe position cache.
     */
    private static boolean recalculateUnsafePositionsFlag = false;
    private List<Thread> safeCalculatorThreads = Collections.synchronizedList(new ArrayList<Thread>(drawDistance * 2 + 1));

    /**
     * Use this instance of the Safe Overlay for method calls.
     */
    public static SafeOverlay instance = new SafeOverlay();

    protected SafeOverlay()
    {
        mc = Minecraft.getMinecraft();
        player = mc.thePlayer;
        playerPosition = new Position();
        
        //Don't let multiple threads access this list at the same time by making it a Synchronized List
        unsafePositionCache = Collections.synchronizedList(new ArrayList<Position>());
    }

    /**
     * Event fired when the player interacts with another block.
     * <p>
     * This event only fires on single player worlds!
     * @param event
     */
    @ForgeSubscribe
    public void onPlayerInteractEvent(PlayerInteractEvent event)
    {
    	//THIS EVENT IS NOT FIRED ON SMP SERVERS
    	
        if (event.action != Action.RIGHT_CLICK_BLOCK)
        {
            return;    //can only place blocks by right clicking
        }

        int x = event.x;
        int y = event.y;
        int z = event.z;
        int blockClickedId = mc.theWorld.getBlockId(x, y, z);
        int blockFace = event.face;	// Bottom = 0, Top = 1, Sides = 2-5

        if (blockFace == 0)
        {
            y--;
        }
        else if (blockFace == 1)
        {
            y++;
        }
        else if (blockFace == 2)
        {
            z--;
        }
        else if (blockFace == 3)
        {
            z++;
        }
        else if (blockFace == 4)
        {
            x--;
        }
        else if (blockFace == 5)
        {
            x++;
        }

        int blockPlacedId = mc.theWorld.getBlockId(x, y, z);

        if (blockPlacedId != 0)	//if it's not an Air block
        {
            onBlockPlaced(blockPlacedId, x, y , z);
        }
    }

    /**
     * ONLY WORKS IN SINGLE PLAYER<p>
     * Psuedo event handler for blocks being placed.
     * Will fire when the player ATTEMPTS to placed a block
     * (it will fire even if the block isn't succesfully placed).
     * @param blockId
     * @param x
     * @param y
     * @param z
     */
    public void onBlockPlaced(int blockId, int x, int y, int z)
    {
        //System.out.println("block placed at ("+x+","+y+","+z+")");
        if (Block.lightValue[blockId] > 0)
        {
            onLightEmittingBlockPlaced(blockId, x, y, z);
        }
    }

    /**
     * ONLY WORKS IN SINGLE PLAYER<p>
     * Psuedo event handler for a light emitting block being placed.
     * Will fire when the player ATTEMPTS to placed a block
     * (it will fire even if the block isn't succesfully placed).
     * @param blockId
     * @param x
     * @param y
     * @param z
     */
    public void onLightEmittingBlockPlaced(int blockId, int x, int y, int z)
    {
        //System.out.println("light emitting block placed at ("+x+","+y+","+z+")");
        RecalculateUnsafePositions();
    }

    /**
     * This thead will calculate unsafe positions around the player given a Y coordinate.
     * <p>
     * <b>Single threaded</b> performance (with drawDistance=80):
     * <br>Average CPU usage: 24%
     * <br>Time to calculate all unsafe areas: <b>305 ms</b>
     * <p>
     * <b>Multi threaded</b> performance (with drawDistance=80):
     * <br>Average CPU usage: 25-35%
     * <br>Time to calculate all unsafe areas: <b>100 ms</b>
     * <p>
     * Machine specs when this test took place: Core i7 2.3GHz, 8GB DDR3, GTX 260
     * <br>With vanilla textures, far render distance, superflat map.
     */
    class SafeCalculatorThread extends Thread
    {
    	//this is the y-coordinate this thread is responsible for calculating
        private int y;

        SafeCalculatorThread(int y)
        {
            super("Safe Overlay Calculator Thread at y=" + y);
            this.y = y;

            //Start the thread
            start();
        }

        //This is the entry point for the thread after start() is called.
        public void run()
        {
            Position pos = new Position();

            for (int x = -drawDistance; x < drawDistance; x++)
            for (int z = -drawDistance; z < drawDistance; z++)
            {
                pos.x = playerPosition.x + x;
                pos.y = playerPosition.y + y;
                pos.z = playerPosition.z + z;
                
                if(CanMobsSpawnAtPosition(pos))
                {
                    unsafePositionCache.add(new Position(pos));
                }
            }
        }
    }
    
    /**
     * Determines if any mob can spawn at a position. Works very well at detecting
     * if bipeds or spiders can spawn there.
     * @param pos Position of the block whos surface gets checked
     * @return
     */
    public static boolean CanMobsSpawnAtPosition(Position pos)
    {
        //if a mob can spawn here, add it to the unsafe positions cache so it can be rendered as unsafe
        //4 things must be true for a mob to be able to spawn here:
        //1) mobs need to be able to spawn on top of this block (block with a solid top surface)
        //2) mobs need to be able to spawn inside of the block above (air, button, lever, etc)
        //3) needs < 8 light level
        if (pos.CanMobsSpawnOnBlock(0, 0, 0) && pos.CanMobsSpawnInBlock(0, 1, 0) && pos.GetLightLevelWithoutSky() < 8)
        {
            //4) 2 blocks above needs to be air for bipeds
        	if(pos.IsAirBlock(0, 2, 0))
        		return true;

            //4.5) 2 blocks above needs to be transparent (air, glass, stairs, etc) for spiders
        	if(!pos.IsOpaqueBlock(0, 2, 0))	//block is see through like air, stairs, glass, etc.
        	{
        		//check to see if a spider can spawn here by checking the 8 neighboring blocks
        		if(pos.CanMobsSpawnInBlock(-1, 1, 1) &&
						pos.CanMobsSpawnInBlock(-1, 1, 0) &&
						pos.CanMobsSpawnInBlock(-1, 1, -1) &&
						pos.CanMobsSpawnInBlock(0, 1, -1) &&
						pos.CanMobsSpawnInBlock(0, 1, 1) &&
						pos.CanMobsSpawnInBlock(1, 1, 1) &&
						pos.CanMobsSpawnInBlock(1, 1, 0) &&
						pos.CanMobsSpawnInBlock(1, 1, -1))
        			return true;
        	}
        }
    	
    	return false;
    }


    /**
     * Renders all unsafe areas around the player.
     * It will only recalculate the unsafe areas once every [updateFrequency] milliseconds
     * @param partialTickTime
     */
    public void RenderAllUnsafePositionsMultithreaded(float partialTickTime)
    {
        if (!SafeOverlay.Enabled || Mode == 0)	//0 = off, 1 = on
        {
            return;
        }

        player = mc.thePlayer;

        if (!displayInNether && player.dimension == -1)	//turn off in the nether, mobs can spawn no matter what
        {
            return;
        }

        double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTickTime;
        double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTickTime;
        double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTickTime;
        
        playerPosition = new Position((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));

        if (recalculateUnsafePositionsFlag || System.currentTimeMillis() - lastGenerate > updateFrequency)
        {
            CalculateUnsafePositionsMultithreaded();
        }

        GL11.glPushMatrix();
        GL11.glTranslated(-x, -y, -z);		//go from cartesian x,y,z coordinates to in-world x,y,z coordinates
        GL11.glDisable(GL11.GL_TEXTURE_2D);	//fixes color rendering bug (we aren't rendering textures)
        
        //BLEND and ALPHA allow for color transparency
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (renderUnsafePositionsThroughWalls)
        {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);    //allows this unsafe position to be rendered through other blocks
        }
        else
        {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        GL11.glBegin(GL11.GL_LINES);	//begin drawing lines defined by 2 vertices

        //wait for all the threads to finish calculation before rendering the unsafe positions
        for (Thread t : safeCalculatorThreads)
        {
            try
            {
                t.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        

        //render unsafe areas
        for (Position position : unsafePositionCache)
        {
            RenderUnsafeMarker(position);
        }
        

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);	//puts blending back to normal, fixes bad HD texture rendering
        GL11.glPopMatrix();
    }

    /**
     * Renders an unsafe marker ("X" icon) at the position with colors depending on the Positions light levels.
     * It also takes into account the block above this position and relocates the mark vertically if needed.
     * @param position A position defined by (x,y,z) coordinates
     */
    protected void RenderUnsafeMarker(Position position)
    {
        int blockId = position.GetBlockId(0, 0, 0);
        int blockAboveId = position.GetBlockId(0, 1, 0);
        Block block = Block.blocksList[blockId];
        Block blockAbove = Block.blocksList[blockAboveId];
        
        //block is null when attempting to render on an Air block
        //we don't like null references so treat Air like an ordinary Stone block
        block = (block == null) ? Block.stone : block;
        
        //get bounding box data for this block
        //don't bother for horizontal (X and Z) bounds because every hostile mob spawns on a 1.0 wide block
        //some blocks, like farmland, have a different vertical (Y) bound
        double boundingBoxMinX = 0.0;
        double boundingBoxMaxX = 1.0;
        double boundingBoxMaxY = block.getBlockBoundsMaxY();	//almost always 1, but farmland is 0.9375
        double boundingBoxMinZ = 0.0;
        double boundingBoxMaxZ = 1.0;
        float r, g, b, alpha;
        int lightLevelWithSky = position.GetLightLevelWithSky();
        int lightLevelWithoutSky = position.GetLightLevelWithoutSky();

        if (lightLevelWithSky > lightLevelWithoutSky && lightLevelWithSky > 7)
        {
            //yellow
            //decrease the brightness of the yellow "X" marks if the surrounding area is dark
            int blockLightLevel = Math.max(lightLevelWithSky, lightLevelWithoutSky);
            float colorBrightnessModifier = blockLightLevel / 15f;
            r = 1f * colorBrightnessModifier;
            g = 1f * colorBrightnessModifier;
            b = 0f;
            alpha = unsafeOverlayTransparency;
        }
        else
        {
            //red
            r = 0.5f;
            g = 0f;
            b = 0f;
            alpha = unsafeOverlayTransparency;
        }

        //Minecraft bug: the Y-bounds for half slabs change if the user is aimed at them, so set them manually
        if (block instanceof BlockHalfSlab)
        {
            boundingBoxMaxY = 1.0;
        }

        if (blockAbove != null)	//if block above is not an Air block
        {
        	
            if (blockAbove instanceof BlockRailBase
                    || blockAbove instanceof BlockBasePressurePlate
                    || blockAbove instanceof BlockCarpet)
            {
                //is there a spawnable block on top of this one?
                //if so, then render the mark higher up to match its height
                boundingBoxMaxY = 1 + blockAbove.getBlockBoundsMaxY();
            }
            else if (blockAbove instanceof BlockSnow)
            {
            	//mobs only spawn on snow blocks that are stacked 1 high (when metadata = 0)
            	
            	//Minecraft bug: the Y-bounds for stacked snow blocks is bugged and changes based on the last one you looked at
                int snowMetadata = mc.theWorld.getBlockMetadata(position.x, position.y+1, position.z);
                if(snowMetadata == 0)
                	boundingBoxMaxY = 1 + 0.125;
            }
        }
        

        double minX = position.x + boundingBoxMinX + 0.02;
        double maxX = position.x + boundingBoxMaxX - 0.02;
        double maxY = position.y + boundingBoxMaxY + 0.02;
        double minZ = position.z + boundingBoxMinZ + 0.02;
        double maxZ = position.z + boundingBoxMaxZ - 0.02;
        
        //render the "X" mark
        //since we are using doubles it causes the marks to 'flicker' when very far from spawn (~5000 blocks)
        //if we use GL11.glVertex3i(int, int, int) it fixes the issue but then we can't render the marks
        //precisely where we want to
        GL11.glColor4f(r, g, b, alpha);	//alpha must be > 0.1
        GL11.glVertex3d(maxX, maxY, maxZ);
        GL11.glVertex3d(minX, maxY, minZ);
        GL11.glVertex3d(maxX, maxY, minZ);
        GL11.glVertex3d(minX, maxY, maxZ);
    }

    /**
     * Calculates which areas around the player are unsafe and adds these Positions
     * to the unsafePositionCache. The cache is used when the unsafe positions are
     * rendered (a.k.a. every frame). The cache is used to save CPU cycles from not
     * having to recalculate the unsafe locations every frame.
     * <p>
     * This is a multithreaded method that makes a new thread to calculate unsafe
     * areas for each elevation (Y coordinate) around the player. This means that
     * if the drawDistance=20, then a 40*40*40 cube is analyzed, which means we make
     * 40 new threads to help calculate unsafe areas.
     */
    protected void CalculateUnsafePositionsMultithreaded()
    {
        unsafePositionCache.clear();

        for (int y = -drawDistance; y < drawDistance; y++)
        {
            safeCalculatorThreads.add(new SafeCalculatorThread(y));
        }
        
        recalculateUnsafePositionsFlag = false;
        cachePosition = playerPosition;
        lastGenerate = System.currentTimeMillis();
    }

    /**
     * Sets a flag for the Safe Overlay to recalculate unsafe positions on the next screen render.
     */
    public void RecalculateUnsafePositions()
    {
        recalculateUnsafePositionsFlag = true;
    }

    /**
     * Gets the status of the Safe Overlay
     * @return the string "safe" if the Safe Overlay is enabled, otherwise "".
     */
    public static String CalculateMessageForInfoLine()
    {
        if (Mode == 0)	//off
        {
            return FontCodes.WHITE + "";
        }
        else if (Mode == 1)	//on
        {
            return FontCodes.WHITE + Localization.get("safeoverlay.infoline") + InfoLine.SPACER;
        }
        else
        {
            return FontCodes.WHITE + "???" + InfoLine.SPACER;
        }
    }

    /**
     * Gets the current draw distance.
     * @return the draw distance radius
     */
    public int getDrawDistance()
    {
        return drawDistance;
    }

    /**
     * Sets the current draw distance.
     * @param newDrawDistance the new draw distance
     * @return the updated draw distance
     */
    public int setDrawDistance(int newDrawDistance)
    {
        if (newDrawDistance > maxDrawDistance)
        {
            newDrawDistance = maxDrawDistance;
        }
        else if (newDrawDistance < minDrawDistance)
        {
            newDrawDistance = minDrawDistance;
        }

        drawDistance = newDrawDistance;
        double percent = (double)newDrawDistance / maxDrawDistance;
        updateFrequency = (int)((double)(updateFrequencyMax - updateFrequencyMin) * percent  + updateFrequencyMin);
        RecalculateUnsafePositions();
        return drawDistance;
    }

    /**
     * Increases the current draw distance by 3 blocks.
     * @return the updated draw distance
     */
    public int increaseDrawDistance()
    {
        return setDrawDistance(drawDistance + 3);
    }
    /**
     * Decreases the current draw distance by 3 blocks.
     * @return the updated draw distance
     */
    public int decreaseDrawDistance()
    {
        return setDrawDistance(drawDistance - 3);
    }
    /**
     * Increases the current draw distance.
     * @param amount how much to increase the draw distance by
     * @return the updated draw distance
     */
    public int increaseDrawDistance(int amount)
    {
        return setDrawDistance(drawDistance + amount);
    }
    /**
     * Decreases the current draw distance.
     * @param amount how much to increase the draw distance by
     * @return the updated draw distance
     */
    public int decreaseDrawDistance(int amount)
    {
        return setDrawDistance(drawDistance - amount);
    }

    /**
     * Checks if see through walls mode is enabled.
     * @return
     */
    public boolean getSeeUnsafePositionsThroughWalls()
    {
        return renderUnsafePositionsThroughWalls;
    }
    /**
     * Sets seeing unsafe areas in the Nether
     * @param displayInUnsafeAreasInNether true or false
     * @return the updated see Nether viewing mode
     */
    public boolean setDisplayInNether(Boolean displayInUnsafeAreasInNether)
    {
    	displayInNether = displayInUnsafeAreasInNether;
        return displayInNether;
    }
    /**
     * Gets if you can see unsafe areas in the Nether
     * @return the Nether viewing mode
     */
    public boolean getDisplayInNether()
    {
        return displayInNether;
    }
    /**
     * Toggles the current display in Nether mode
     * @return the updated see display in Nether mode
     */
    public boolean toggleDisplayInNether()
    {
        return setDisplayInNether(!displayInNether);
    }
    /**
     * Sets the see through wall mode
     * @param safeOverlaySeeThroughWalls true or false
     * @return the updated see through wall mode
     */
    public boolean setSeeUnsafePositionsThroughWalls(Boolean safeOverlaySeeThroughWalls)
    {
        renderUnsafePositionsThroughWalls = safeOverlaySeeThroughWalls;
        return renderUnsafePositionsThroughWalls;
    }
    /**
     * Toggles the current see through wall mode
     * @return the udpated see through wall mode
     */
    public boolean toggleSeeUnsafePositionsThroughWalls()
    {
        return setSeeUnsafePositionsThroughWalls(!renderUnsafePositionsThroughWalls);
    }
    /**
     * Sets the alpha value of the unsafe marks
     * @param alpha the alpha value of the unsafe marks
     * @return the updated alpha value
     */
    public float setUnsafeOverlayTransparency(float alpha)
    {
    	//must be between (0.101, 1]
        unsafeOverlayTransparency = (alpha <= unsafeOverlayMinTransparency) ? unsafeOverlayMinTransparency : alpha;	//check lower bounds
        unsafeOverlayTransparency = (alpha >= unsafeOverlayMaxTransparency) ? unsafeOverlayMaxTransparency : alpha;	//check upper bounds
        return unsafeOverlayTransparency;
    }
    /**
     * gets the alpha value of the unsafe marks
     * @return the alpha value
     */
    public float getUnsafeOverlayTransparency()
    {
        return unsafeOverlayTransparency;
    }
    /**
     * gets the smallest allowed alpha value of the unsafe marks
     * @return the alpha value
     */
    public float getUnsafeOverlayMinTransparency()
    {
        return unsafeOverlayMinTransparency;
    }
    /**
     * gets the largest allowed alpha value of the unsafe marks
     * @return the alpha value
     */
    public float getUnsafeOverlayMaxTransparency()
    {
        return unsafeOverlayMaxTransparency;
    }
    
    /**
     * Increments the Clock mode
     * @return The new Clock mode
     */
    public static int ToggleMode()
    {
    	Mode++;
    	if(Mode >= NumberOfModes)
    		Mode = 0;
    	return Mode;
    }
    
    
    
    /**
     * Helper class to storing information about a location in the world.
     * <p>
     * It uses (x,y,z) coordinates to determine things like mob spawning, and helper methods
     * to find blocks nearby.
     */
    class Position
    {
        public int x;
        public int y;
        public int z;

        public Position() {}

        public Position(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Position(Position o)
        {
            this(o.x, o.y, o.z);
        }

        public Position(Position o, int dx, int dy, int dz)
        {
            this(o.x + dx, o.y + dy, o.z + dz);
        }

        /**
         * Gets the ID of a block relative to this block.
         * @param dx x location relative to this block
         * @param dy y location relative to this block
         * @param dz z location relative to this block
         * @return
         */
        public int GetBlockId(int dx, int dy, int dz)
        {
            return mc.theWorld.getBlockId(x + dx, y + dy, z + dz);
        }

        /**
         * Checks if mobs can spawn ON the block at a location.
         * @param dx x location relative to this block
         * @param dy y location relative to this block
         * @param dz z location relative to this block
         * @return true if mobs can spawn ON this block
         */
        public boolean CanMobsSpawnOnBlock(int dx, int dy, int dz)
        {
            int blockId = GetBlockId(dx, dy, dz);
            Block block = Block.blocksList[blockId];

            if (block == null)	//air block
            {
                return false;
            }

            if (blockId > 0 && block.isOpaqueCube())
            {
                return true;
            }

            if (mc.theWorld.doesBlockHaveSolidTopSurface(x + dx, y + dy, z + dz))
            {
                return true;
            }

            // exception to the isOpaqueCube and doesBlockHaveSolidTopSurface rules
            if (block instanceof BlockFarmland)
            {
                return true;
            }

            return false;
        }

        /**
         * Checks if mobs can spawn IN the block at a location.
         * @param dx x location relative to this block
         * @param dy y location relative to this block
         * @param dz z location relative to this block
         * @return true if mobs can spawn ON this block
         */
        public boolean CanMobsSpawnInBlock(int dx, int dy, int dz)
        {
            int blockId = GetBlockId(dx, dy, dz);
            Block block = Block.blocksList[blockId];

            if (block == null)	//air block
            {
                return true;
            }

            if (block.isOpaqueCube())	//majority of blocks: dirt, stone, etc.
            {
                return false;
            }

            //list of transparent blocks mobs can NOT spawn inside of.
            //for example, they cannot spawn inside of leaves even though they are transparent.
            //  (I wonder if the list shorter for blocks that mobs CAN spawn in?
            //   lever, button, redstone  torches, reeds, rail, plants, crops, etc.)
            return !(block instanceof BlockHalfSlab
                     || block instanceof BlockStairs
                     || block instanceof BlockFluid
                     || block instanceof BlockChest
                     || block instanceof BlockGlass
                     || block instanceof BlockIce
                     || block instanceof BlockFence
                     || block instanceof BlockFenceGate
                     || block instanceof BlockLeaves
                     || block instanceof BlockWall
                     || block instanceof BlockPane
                     || block instanceof BlockWeb
                     || block instanceof BlockCactus
                     || block instanceof BlockAnvil
                     || block instanceof BlockBed
                     || block instanceof BlockFarmland
                     || block instanceof BlockHopper
                     || block instanceof BlockPistonBase
                     || block instanceof BlockPistonExtension
                     || block instanceof BlockPistonMoving
                     || block instanceof BlockCake);
        }
        
        /**
         * Checks if a block is an opqaue cube.
         * @param dx x location relative to this block
         * @param dy y location relative to this block
         * @param dz z location relative to this block
         * @return true if the block is opaque (like dirt, stone, etc.)
         */
        public boolean IsOpaqueBlock(int dx, int dy, int dz)
        {
        	int blockId = GetBlockId(dx, dy, dz);
            Block block = Block.blocksList[blockId];
            
            if (block == null)	//air block
            {
                return false;
            }

            return block.isOpaqueCube();
        }

        /**
         * Checks if a block is air.
         * @param dx x location relative to this block
         * @param dy y location relative to this block
         * @param dz z location relative to this block
         * @return true if the block is opaque (like dirt, stone, etc.)
         */
        public boolean IsAirBlock(int dx, int dy, int dz)
        {
        	int blockId = GetBlockId(dx, dy, dz);
            Block block = Block.blocksList[blockId];
            
            if (block == null)	//air block
            {
                return true;
            }

            return false;
        }

        /**
         * Gets the light level of the spot above this block. Does not take into account sunlight.
         * @return 0-15
         */
        public int GetLightLevelWithoutSky()
        {
            return mc.theWorld.getSavedLightValue(EnumSkyBlock.Block, x, y + 1, z);
        }

        /**
         * Gets the light level of the spot above this block. Take into account sunlight.
         * @return 0-15
         */
        public int GetLightLevelWithSky()
        {
            return mc.theWorld.getSavedLightValue(EnumSkyBlock.Sky, x, y + 1, z);
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }

            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            Position that = (Position) o;

            if (x != that.x)
            {
                return false;
            }

            if (y != that.y)
            {
                return false;
            }

            if (z != that.z)
            {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = (x ^ (x >>> 16));
            result = 31 * result + (y ^ (y >>> 16));
            result = 31 * result + (z ^ (z >>> 16));
            return result;
        }
    }
}