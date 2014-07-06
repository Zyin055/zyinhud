package com.zyin.zyinhud.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.Vec3;

import com.zyin.zyinhud.mods.PlayerLocator;

/**
 * The EntityTrackerHUDHelper calculates the (x,y) position on the HUD for entities in the game world.
 */
public class HUDEntityTrackerHelper
{
    private static Minecraft mc = Minecraft.getMinecraft();
    private static EntityClientPlayerMP me;

    private static final double pi = Math.PI;


    /**
     * Send information about the positions of entities to mods that need this information.
     * <p>
     * Place new rendering methods for mods in this function.
     * @param entity
     * @param x location on the HUD
     * @param y location on the HUD
     * @param isEntityBehindUs
     */
    private static void RenderEntityInfoOnHUD(Entity entity, int x, int y, boolean isEntityBehindUs)
    {
        PlayerLocator.RenderEntityInfoOnHUD(entity, x, y, isEntityBehindUs);
    }
    

    /**
     * Calculates the on-screen (x,y) positions of entities and renders various overlays over them.
     */
    public static void RenderEntityInfo()
    {
    	PlayerLocator.numOverlaysRendered = 0;
    	
        if (PlayerLocator.Enabled && PlayerLocator.Mode == PlayerLocator.Modes.ON
                && mc.inGameHasFocus)
        {
            me = mc.thePlayer;
            
            ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            int width = res.getScaledWidth();		//~427
            int height = res.getScaledHeight();	//~240
        	
            //iterate over all the loaded Entity objects and find just the entities we are tracking
            for (Object object : mc.theWorld.loadedEntityList)
            {
                //only track entities that we are tracking (i.e. other players/wolves)
                if(!(object instanceof EntityOtherPlayerMP || object instanceof EntityWolf))
                	continue;
                
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
                
                
                //Vec3 aNorm = a.normalize();	//a is already normalized
                Vec3 bNorm = b.normalize();
                
                //compute the horizontal angle the between the player's corsshair and the location of the entity
                double horizontalAngleA = Math.atan2(a.zCoord, a.xCoord);		//-pi to pi
                double horizontalAngleB = Math.atan2(b.zCoord, b.xCoord);		//-pi to pi
                double horizontalAngle = horizontalAngleA - horizontalAngleB;	//-2pi to 2pi

                //compute the vertical angle the between the player's corsshair and the location of the entity
                double verticalAngleA = Math.asin(a.yCoord);
                double verticalAngleB = Math.asin(bNorm.yCoord);
                double verticalAngle = verticalAngleA - verticalAngleB;	//-pi/2 to pi/2
                
                //the player's FOV can range from 30 to 110 degrees (30*pi/180 to 110*pi/180)
                double fov = mc.gameSettings.fovSetting;	//float:30 to 110 degrees (default=70)
                fov *= me.getFOVMultiplier();	//FOV multiplier when sprinting(1.15)/flying(1.1)
                fov *= (pi / 180);	//convert degrees to radians
                
                //skew the horizontalAngle to match our FOV
                horizontalAngle = horizontalAngle * fov/2;
                
                //compute the x and y coordinates where the overlayMessage should be rendered on screen
                int x = (int)(Math.sin(horizontalAngle) * -2 * width / pi) + width / 2;
                int y = (int)(Math.sin(verticalAngle) * 2 * height / pi) + height / 2;
                
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
            }
        }
    }
}
