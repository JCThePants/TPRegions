/* This file is part of TPRegions for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.bukkit.tpregions;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

/**
 * An {@code ITPDestination} implementation of a {@code Location}.
 */
public class DestinationLocation extends Location implements ITPDestination {

    /**
     * Convert a {@code Location} object to a new {@code DestinationLocation}.
     *
     * @param location  The location to convert.
     */
    public static DestinationLocation from(Location location) {
        return new DestinationLocation(location.getWorld(),
                location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
    }

    /**
     * Constructor.
     *
     * @param world  The location world.
     * @param x      The location X coordinates.
     * @param y      The location Y coordinates.
     * @param z      The location Z coordinates.
     */
    public DestinationLocation(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    /**
     * Constructor.
     *
     * @param world  The location world.
     * @param x      The location X coordinates.
     * @param y      The location Y coordinates.
     * @param z      The location Z coordinates.
     * @param yaw    The yaw angle.
     * @param pitch  The pitch angle.
     */
    public DestinationLocation(World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void teleport(@Nullable ITPDestination sender, Entity entity, float yaw) {
        PreCon.notNull(entity);

        Location destination = new Location(getWorld(),
                getX(), getY(), getZ(),
                getYaw(), getPitch());

        if (!destination.getChunk().isLoaded())
            destination.getChunk().load();

        Teleporter.teleport(entity, destination);
    }

    @Override
    public String toString() {
        return TextUtils.formatLocation(this, true);
    }
}
