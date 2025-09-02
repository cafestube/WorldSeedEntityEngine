package net.worldseed.multipart.entity.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import org.jetbrains.annotations.Nullable;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class DataWatcher {

    private final Map<Integer, Item<?>> data = new Int2ObjectOpenHashMap<>();

    private final @Nullable Consumer<List<SynchedEntityData.DataValue<?>>> updateHandler;
    private boolean notifyAboutChanges = true;

    public DataWatcher(@Nullable Consumer<List<SynchedEntityData.DataValue<?>>> updateHandler) {
        this.updateHandler = updateHandler;
    }

    public <T> void set(EntityDataAccessor<T> accessor, T value) {
        @SuppressWarnings("unchecked") Item<T> item = (Item<T>) data.get(accessor.id());
        if (item == null) {
            item = new Item<>(accessor, value);
            data.put(accessor.id(), item);
        } else {
            if(Objects.equals(item.value, value)) return;
            item.value = value;
            item.dirty = true;
        }

        if(notifyAboutChanges && updateHandler != null) {
            updateHandler.accept(packDirty());
        }
    }


    public void setSharedFlag(int flag, boolean set) {
        Byte b = get(EntityData.ENTITY_DATA_SHARED_FLAGS_ID);
        if(b == null) {
            b = 0;
        }

        if (set) {
            set(EntityData.ENTITY_DATA_SHARED_FLAGS_ID, (byte)(b | 1 << flag));
        } else {
            set(EntityData.ENTITY_DATA_SHARED_FLAGS_ID, (byte)(b & ~(1 << flag)));
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

    public List<SynchedEntityData.DataValue<?>> packDirty() {
        List<SynchedEntityData.DataValue<?>> list = new ArrayList<>(this.data.size());
        for (Item<?> value : this.data.values()) {
            if (value.dirty) {
                list.add(value.value());
                value.dirty = false;
            }
        }
        return list;
    }

    public boolean setNotifyAboutChanges(boolean b) {
        boolean prev = this.notifyAboutChanges;
        this.notifyAboutChanges = b;

        if(notifyAboutChanges && updateHandler != null) {
            List<SynchedEntityData.DataValue<?>> dataValues = packDirty();
            if(!dataValues.isEmpty()) {
                updateHandler.accept(dataValues);
            }
        }

        return prev;
    }

    private static class Item<T> {

        private final EntityDataAccessor<T> accessor;
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
