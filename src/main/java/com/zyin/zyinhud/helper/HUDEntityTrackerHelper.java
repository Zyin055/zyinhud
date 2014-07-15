package com.zyin.zyinhud.helper;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityWolf;

import com.zyin.zyinhud.mods.PlayerLocator;

/**
 * The EntityTrackerHUDHelper calculates the (x,y) position on the HUD for entities in the game world.
 */
public class HUDEntityTrackerHelper
{
    private static Minecraft mc = Minecraft.getMinecraft();
    //private static EntityClientPlayerMP me;

    private static FloatBuffer modelMatrix = BufferUtils.createFloatBuffer(16);
    private static FloatBuffer projMatrix = BufferUtils.createFloatBuffer(16);
    
    private static final double pi = Math.PI;
    private static final double twoPi = 2 * Math.PI;
    
    
    /**
     * Stores world render transform matrices for later use when rendering HUD.
     */
    public static void StoreMatrices()
    {
        modelMatrix.rewind();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelMatrix);
        projMatrix.rewind();
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projMatrix);
        
    }
   
    /**
     * Send information about the positions of entities to mods that need this information.
     * <p>
     * Place new rendering methods for mods in this function.
     * @param entity
     * @param x location on the HUD
     * @param y location on the HUD
     */
    private static void RenderEntityInfoOnHUD(Entity entity, int x, int y)
    {
        PlayerLocator.RenderEntityInfoOnHUD(entity, x, y);
    }
    

    /**
     * Calculates the on-screen (x,y) positions of entities and renders various overlays over them.
     * @param partialTickTime 
     */
    public static void RenderEntityInfo(float partialTickTime)
    {
    	PlayerLocator.numOverlaysRendered = 0;
    	
        if (PlayerLocator.Enabled && PlayerLocator.Mode == PlayerLocator.Modes.ON
                && mc.inGameHasFocus)
        {
        	float playerX = (float) (mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTickTime);
            float playerY = (float) (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTickTime);
            float playerZ = (float) (mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTickTime);
            
            ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            int width = res.getScaledWidth();		//~427
            int height = res.getScaledHeight();	//~240
            
            IntBuffer viewport = BufferUtils.createIntBuffer(16);
            GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        	
            //iterate over all the loaded Entity objects and find just the entities we are tracking
            for (Object object : mc.theWorld.loadedEntityList)
            {
                //only track entities that we are tracking (i.e. other players/wolves)
                if(!(object instanceof EntityOtherPlayerMP || object instanceof EntityWolf))
                	continue;
                
                Entity entity = (Entity)object;
                
                float entityX = (float) (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTickTime);
                float entityY = (float) (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTickTime);
                float entityZ = (float) (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTickTime);
                
                //start calculating the angles needed to render the overlay message onto the screen in (x,y) coordinates
                /*double pitch = ((-me.rotationPitch) * pi) / 180; //-pi/2 to pi/2; direction inverted
                double yaw  = (((me.rotationYaw % 360 + 360 + 90 + 180) % 360 - 180) * pi) / 180; //-pi to pi
                	// +360 to result of first modulo to make sure it's positive
                    // +90 offset
                    // +180 shift before second modulo, then -180 shift after to get -180 to 180 range

                //Vector 3D: b - vector from the player's position to the entity's position
                Vec3 b = Vec3.createVectorHelper(entity.posX - me.posX, entity.posY - me.posY, entity.posZ - me.posZ);
                
                //compute the horizontal angle the between the player's crosshair and the location of the entity
                double horizontalAngleB = Math.atan2(b.zCoord, b.xCoord);		//-pi to pi
                double horizontalAngle = ((yaw - horizontalAngleB) % twoPi + twoPi + pi) % twoPi - pi;	//normalize to -pi to pi; similar operations to yaw above
                horizontalAngle = Math.min(Math.max(horizontalAngle, -pi/2), pi/2); 					//constrain to -pi/2 to pi/2
                
                //compute the vertical angle the between the player's crosshair and the location of the entity
                double verticalAngleB = Math.atan2(b.yCoord, Math.hypot(b.xCoord, b.zCoord));
                double verticalAngle = pitch - verticalAngleB;					//-pi to pi
                verticalAngle = Math.min(Math.max(verticalAngle, -pi/2), pi/2);	//constrain to -pi/2 to pi/2
                
                //the player's FOV can range from 30 to 110 degrees (30*pi/180 to 110*pi/180)
                //this seems to be vertical fov
                double fov = mc.gameSettings.fovSetting;	//float:30 to 110 degrees (default=70)
                fov *= me.getFOVMultiplier();	//FOV multiplier when sprinting(1.15)/flying(1.1)
                fov *= (pi / 180);	//convert degrees to radians
                
                // calculate horizontal fov
                double hfov = 2 * Math.atan(Math.tan(fov/2) * width / height);*/
                
                //int x = -(int)Math.round(Math.tan(horizontalAngle) / Math.tan(hfov/2) * width / 2) + width / 2;
                //int y = (int)Math.round(Math.tan(verticalAngle) / Math.tan(fov/2) * height / 2) + height / 2;
                
                //if we are facing away from target, we need to snap the message to the edge of the screen,
                //otherwise it gets displayed in the middle of the screen when looking away from it

                /*if (horizontalAngle >= pi / 2)
                {
                    x = 0;
                }
                else if (horizontalAngle <= -pi / 2)
                {
                    x = width;
                }
                
                if (verticalAngle >= pi / 2)
                {
                    y = height;
                }
                else if (verticalAngle <= -pi / 2)
                {
                    y = 0;
                }*/
                

                
                float x = (float)(playerX - entityX);
                float y = (float)(playerY - entityY);
                float z = (float)(playerZ - entityZ);
                FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
                
                modelMatrix.rewind();
                projMatrix.rewind();
                GLU.gluProject(x, y, z, modelMatrix, projMatrix, viewport, screenCoords);
                
                int hudX = Math.round(screenCoords.get(0)) / res.getScaleFactor();
                int hudY = height - Math.round(screenCoords.get(1)) / res.getScaleFactor();
                
                mc.fontRenderer.drawStringWithShadow(hudX + ", " + hudY, 1, 60, 0xffddff);
                
                int newHudX = hudX, newHudY = hudY;
                
                if (hudX < 0)
                	newHudY = (int)((hudY - height / 2) / (1 - (2 * (float)hudX / width)) + height / 2);
                else if (hudX > width)
                	newHudY = (int)((hudY - height / 2) / ((2 * (float)hudX / width) - 1) + height / 2);
                
                if (hudY < 0)
                	newHudX = (int)((hudX - width / 2) / (1 - (2 * (float)hudY / height)) + width / 2);
                else if (hudY > height)
                	newHudX = (int)((hudX - width / 2) / ((2 * (float)hudY / height) - 1) + width / 2);
                
                hudX = newHudX;
                hudY = newHudY;
                
                if (screenCoords.get(2) < 1) // screen Z coord; > 1 if entity in front, < 1 if behind
                {

                }
  
                mc.fontRenderer.drawStringWithShadow(Float.toString(screenCoords.get(2)), 1, 40, 0xffffff);
                mc.fontRenderer.drawStringWithShadow(hudX + ", " + hudY, 1, 80, 0xffffff);
                
                RenderEntityInfoOnHUD(entity, hudX, hudY);
            }
        }
    }
}
