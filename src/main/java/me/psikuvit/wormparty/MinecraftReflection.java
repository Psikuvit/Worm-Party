package me.psikuvit.wormparty;

import org.bukkit.World;

import java.lang.reflect.Method;

public class MinecraftReflection {


    private static String getMinecraftVersion() {
        return org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static Class<?> getNMSClass(String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + getMinecraftVersion() + "." + className);
    }

    public static Object getHandle(Object object) throws ReflectiveOperationException {
        Method getHandle = object.getClass().getMethod("getHandle");
        return getHandle.invoke(object);
    }

    public static Object getNMSWorld(World world) {
        try {
            // Get the CraftWorld class
            Class<?> craftWorldClass = Class.forName(getCraftBukkitPackage() + ".CraftWorld");

            // Invoke the handle method
            return getHandle(craftWorldClass.cast(world));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getCraftBukkitPackage() {
        return "org.bukkit.craftbukkit." + getMinecraftVersion();
    }
}
