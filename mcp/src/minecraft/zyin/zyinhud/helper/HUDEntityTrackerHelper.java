package zyin.zyinhud.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.Vec3;
import zyin.zyinhud.mods.PlayerLocator;

/**
 * The EntityTrackerHUDHelper calculates the (x,y) position on the HUD for entities in the game world.
 */
public class HUDEntityTrackerHelper
{
    private static Minecraft mc = Minecraft.getMinecraft();
    private static EntityClientPlayerMP me;

    private static final double pi = Math.PI;

    public static int maxOverlayMessagesRendered = 200;	//Renders only the first nearest X entities

    /**
     * The list of entities that the EntitryTrackerHelper will track.
     * The class must exactly match the entity's class, don't use a parent class like EntityPlayer.
     * <p>
     * Entities must be added to this list in order for new mods to track it.
     */
    private static Class[] trackedEntities =
    {
        //EntityClientPlayerMP.class,	//this is the player
        EntityOtherPlayerMP.class
        //EntityCow.class	//for single player testing/debugging!
    };

    /**
     * Send information about the positions of entities to mods that need this information.
     * <p>
     * Place new rendering methods in this function, in addition to adding the mod's mode
     * check at the start of RenderEntityInfo().
     * @param entity
     * @param x location on the HUD
     * @param y location on the HUD
     * @param isEntityBehindUs
     */
    private static void RenderEntityInfoOnHUD(Entity entity, int x, int y, boolean isEntityBehindUs)
    {
        PlayerLocator.RenderEntityInfoOnHUD(entity, x, y, isEntityBehindUs);
        //AnimalInfo.RenderEntityInfoOnHUD(entity, x, y, isEntityBehindUs);
    }
    

    /**
     * Calculates the on-screen (x,y) positions of entities and renders various overlays on them.
     */
    public static void RenderEntityInfo()
    {
        if (PlayerLocator.Mode == 1
                && mc.inGameHasFocus)
        {
            me = mc.thePlayer;
            int i = 0;

            //iterate over all the loaded Entity objects and find just the players
            for (Object object : mc.theWorld.loadedEntityList)
            {
                if (i > maxOverlayMessagesRendered)
                {
                    break;
                }

                //only track entities that we are tracking (i.e. players, horses)
                boolean trackingThisEntity = false;

                for (Class trackedEntity : trackedEntities)
                {
                    if (trackedEntity.getName().equals(object.getClass().getName()))
                    {
                        trackingThisEntity = true;
                        break;
                    }
                }

                if (!trackingThisEntity)
                {
                    continue;
                }

                Entity entity = (Entity)object;
                
                //start calculating the angles needed to render the overlay message onto the screen in (x,y) coordinates
                double pitch = ((me.rotationPitch + 90) * Math.PI) / 180;
                double yaw  = ((me.rotationYaw + 90)  * Math.PI) / 180;
                
                //calculate the vector located at the player with respect to the camera's orientation
                double ax = Math.sin(pitch) * Math.cos(yaw);
                double ay = Math.cos(pitch);
                double az = Math.sin(pitch) * Math.sin(yaw);
                
                //Vector 3D: a - normalized vector created by the direction the player's camera is facing
                //Vector 3D: b - vector from the player's position to the entity's position
                Vec3 a = Vec3.createVectorHelper(ax, ay, az);
                Vec3 b = Vec3.createVectorHelper(entity.posX - me.posX, entity.posY - me.posY, entity.posZ - me.posZ);
                
                ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
                int width = res.getScaledWidth();		//~427
                int height = res.getScaledHeight();	//~240
                
                //Vec3 aNorm = a.normalize();	//a is already normalized
                Vec3 bNorm = b.normalize();
                
                //compute the horizontal angle the between the player's corsshair and the location of the entity
                double horizontalAngleA = Math.atan2(a.zCoord, a.xCoord);		//-pi to pi
                double horizontalAngleB = Math.atan2(b.zCoord, b.xCoord);		//-pi to pi
                double horizontalAngle = horizontalAngleA - horizontalAngleB;	//-2pi to 2pi
                
                //mc.fontRenderer.drawStringWithShadow("horizontalAngleA: " + horizontalAngleA, 1, 50, 0xffffff);
                //mc.fontRenderer.drawStringWithShadow("horizontalAngleB: " + horizontalAngleB, 1, 60, 0xffffff);
                //mc.fontRenderer.drawStringWithShadow("horizontalAngle: " + horizontalAngle, 1, 70, 0xffffff);
                //mc.fontRenderer.drawStringWithShadow("sin(horizontalAngle): " + Math.sin(horizontalAngle), 1, 80, 0xffffff);

                //normalize the horizontal angle so that 0 -> pointing at target, pi -> pointint away, 2pi -> pointing at target
                if (horizontalAngle < 0)
                {
                    horizontalAngle += 2 * pi;
                }

                //compute the vertical angle the between the player's corsshair and the location of the entity
                double verticalAngleA = Math.asin(a.yCoord);
                double verticalAngleB = Math.asin(bNorm.yCoord);
                double verticalAngle = verticalAngleA - verticalAngleB;	//-pi/2 to pi/2
                
                //mc.fontRenderer.drawStringWithShadow("verticalAngleA:"+verticalAngleA, 1, 100, 0xffffff);
                //mc.fontRenderer.drawStringWithShadow("verticalAngleB:"+verticalAngleB, 1, 110, 0xffffff);
                //mc.fontRenderer.drawStringWithShadow("verticalAngle: " + verticalAngle, 1, 120, 0xffffff);
                //mc.fontRenderer.drawStringWithShadow("sin(verticalAngle): " + Math.sin(verticalAngle), 1, 130, 0xffffff);
                
                //the player's FOV can range from 70 to 110 degrees (70*pi/180 to 110*pi/180)
                double fov = mc.gameSettings.fovSetting;	//float:0 to 1, (representing 70 to 110 degrees)
                fov = (70 + fov * 40) * pi / 180;
                fov *= me.getFOVMultiplier();	//FOV multiplier when sprinting/flying
                
                //compute the x and y coordinates where the overlayMessage should be rendered on screen
                int x = (int)(Math.sin(horizontalAngle) * -2 * width / pi / fov) + width / 2;	//not sure exactly why this works, but it does (almost)
                int y = (int)(Math.sin(verticalAngle) * height / fov) + height / 2;
                
                //if we are facing away from target, we need to snap the message to the right or left side of the screen,
                //otherwise it gets displayed in the middle of the screen when looking away from it
                boolean entityIsBehindUs = false;

                if (horizontalAngle > pi / 2 && horizontalAngle <= pi)
                {
                    entityIsBehindUs = true;
                    x = 0;
                }
                else if (horizontalAngle < 3 * pi / 2 && horizontalAngle > pi)
                {
                    entityIsBehindUs = true;
                    x = width;
                }

                RenderEntityInfoOnHUD(entity, x, y, entityIsBehindUs);
                i++;
            }
        }
    }
}
