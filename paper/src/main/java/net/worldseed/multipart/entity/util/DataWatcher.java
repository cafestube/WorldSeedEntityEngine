package net.worldseed.multipart.entity.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataWatcher {

    private final Map<Integer, Item<?>> data = new Int2ObjectOpenHashMap<>();

    public <T> void set(EntityDataAccessor<T> accessor, T value) {
        @SuppressWarnings("unchecked") Item<T> item = (Item<T>) data.get(accessor.id());
        if (item == null) {
            item = new Item<>(accessor, value);
            data.put(accessor.id(), item);
        } else {
            item.value = value;
            item.dirty = true;
        }
    }

    public <T> @Nullable T get(EntityDataAccessor<T> accessor) {
        @SuppressWarnings("unchecked") Item<T> item = (Item<T>) data.get(accessor.id());
        if(item == null) return null;

        return item.value;
    }

    public List<SynchedEntityData.DataValue<?>> packAll() {
        List<SynchedEntityData.DataValue<?>> list = new ArrayList<>(this.data.size());

        for (Item<?> value : this.data.values()) {
            list.add(value.value());
        }

        return list;
    }

    private static class Item<T> {

        private EntityDataAccessor<T> accessor;
        private T value;
        private boolean dirty = false;

        public Item(EntityDataAccessor<T> accessor, T value) {
            this.accessor = accessor;
            this.value = value;
        }

        public SynchedEntityData.DataValue<T> value() {
            return SynchedEntityData.DataValue.create(this.accessor, this.value);
        }

    }

}
