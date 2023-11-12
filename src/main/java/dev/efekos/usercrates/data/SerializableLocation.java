package dev.efekos.usercrates.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;

public class SerializableLocation implements Serializable {
    private String world;

    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    public void setWorld(World world) {
        this.world = world.getName();
    }

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public int getBlockX() {
        return (int) Math.floor(x);
    }


    public int getBlockY() {
        return (int) Math.floor(y);
    }


    public int getBlockZ() {
        return (int) Math.floor(z);
    }

    public Location toLocation(){
        return new Location(getWorld(),getX(),getY(),getZ(),getYaw(),getPitch());
    }

    public static SerializableLocation from(Location location){
        return new SerializableLocation(location.getWorld().getName(),location.getX(),location.getY(),location.getZ(),location.getYaw(),location.getPitch());
    }

    public SerializableLocation(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
