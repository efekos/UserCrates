/*
 * MIT License
 *
 * Copyright (c) 2025 efekos
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.efekos.usercrates.data;

import me.efekos.simpler.config.Storable;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Crate implements Storable {
    /**
     * Location of the block that represents this crate. When the block at this location gets broke by the {@link Crate#owner} of this crate, this crate will be automatically deleted from the database.
     */
    private SerializableLocation location;
    /**
     * UUID of the {@link org.bukkit.entity.Player} that owns this crate. This {@link org.bukkit.entity.Player} is able to add accessors to this crate.
     */
    private final UUID owner;
    /**
     * A list of the {@link UUID}s that belongs to the accessor {@link org.bukkit.entity.Player}s of this crate. These accessors are able to edit the items at the crate.
     */
    private List<UUID> accessors;
    /**
     * A variable that indicates how players will open this crate.
     */
    private CrateConsumeType consumeType;

    /**
     * A list of the holograms belonging to this crate.
     */
    private List<UUID> holograms;

    /**
     * Price of this crate.
     */
    private int price;

    /**
     * Label of the crate
     */
    @Nullable
    private String label;

    public Crate(Location location, UUID owner, List<UUID> accessors, CrateConsumeType consumeType, @Nullable String label) {
        this.location = SerializableLocation.from(location);
        this.owner = owner;
        this.accessors = accessors;
        this.consumeType = consumeType;
        this.holograms = new ArrayList<>();
        this.price = 30;
        this.label = label;
    }

    /**
     * @return The price.
     */
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Nullable
    public String getLabel() {
        return label;
    }

    public void setLabel(@Nullable String label) {
        this.label = label;
    }

    public List<UUID> getHolograms() {
        return holograms;
    }

    public void setHolograms(List<UUID> holograms) {
        this.holograms = holograms;
    }

    public void addHologram(UUID hologram) {
        this.holograms.add(hologram);
    }

    public Location getLocation() {
        return location.toLocation();
    }

    public void setLocation(Location location) {
        this.location = SerializableLocation.from(location);
    }

    public UUID getOwner() {
        return owner;
    }

    public List<UUID> getAccessors() {
        return accessors;
    }

    public void setAccessors(List<UUID> accessors) {
        this.accessors = accessors;
    }

    public void addAccessor(UUID accessor) {
        this.accessors.add(accessor);
    }

    public CrateConsumeType getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(CrateConsumeType consumeType) {
        this.consumeType = consumeType;
    }

    private final UUID uuid = UUID.randomUUID();

    @Override
    public UUID getUniqueId() {
        return uuid;
    }
}
