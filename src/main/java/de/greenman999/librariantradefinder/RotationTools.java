package de.greenman999.librariantradefinder;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class RotationTools {

    public static boolean isRotated = false;
    private static float destPitch = 0;
    private static float destYaw = 0;
    private static float startPitch = 0;
    private static float startYaw = 0;
    private static float speed = 0;
    private static float elapsedTime = 0;
    private static long prevTimeMillis = 0;

    public static void smoothLookAt(Vec3 target, float speed) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null) return;
        Vec3 vec3d = EntityAnchorArgument.Anchor.EYES.apply(mc.player);
        double d = target.x - vec3d.x;
        double e = target.y - vec3d.y;
        double f = target.z - vec3d.z;
        double g = Math.sqrt(d * d + f * f);
        destPitch = Mth.wrapDegrees((float)(-(Mth.atan2(e, g) * 57.2957763671875)));
        destYaw = Mth.wrapDegrees((float)(Mth.atan2(f, d) * 57.2957763671875) - 90.0F);
        startPitch = mc.player.getXRot();
        startYaw = mc.player.getYRot();
        if(destPitch == startPitch && destYaw == startYaw) return;
        RotationTools.speed = speed / 10f;
        elapsedTime = 0;
        prevTimeMillis = System.currentTimeMillis();
        isRotated = false;
    }

    public static void render() {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null || isRotated || speed == 0) return;
        float delta = (System.currentTimeMillis() - (prevTimeMillis == 0 ? System.currentTimeMillis() : prevTimeMillis )) / 1000f;
        prevTimeMillis = System.currentTimeMillis();
        elapsedTime += delta;
        float percentageComplete = elapsedTime / speed;
        if(percentageComplete >= 1) {
            isRotated = true;
            speed = 0;
            return;
        }

        float pitch = Mth.lerp(percentageComplete, startPitch, destPitch);
        float yaw = Mth.lerp(percentageComplete, startYaw, destYaw);

        mc.player.setXRot(pitch);
        mc.player.setYRot(yaw);
    }

}
