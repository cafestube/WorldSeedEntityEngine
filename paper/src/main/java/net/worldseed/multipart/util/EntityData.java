package net.worldseed.multipart.util;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Field;

public class EntityData {

    public static final EntityDataAccessor<ItemStack> ITEM_DISPLAY_DATA_ITEM_STACK_ID = getEntityDataAccessor(Display.ItemDisplay.class, "DATA_ITEM_STACK_ID");
    public static final EntityDataAccessor<Vector3f> DISPLAY_DATA_TRANSLATION_ID = getEntityDataAccessor(Display.class, "DATA_TRANSLATION_ID");
    public static final EntityDataAccessor<Vector3f> DISPLAY_DATA_SCALE_ID = getEntityDataAccessor(Display.class, "DATA_SCALE_ID");
    public static final EntityDataAccessor<Quaternionf> DISPLAY_DATA_RIGHT_ROTATION_ID = getEntityDataAccessor(Display.class, "DATA_RIGHT_ROTATION_ID");
    public static final EntityDataAccessor<Byte> ITEM_DISPLAY_DATA_ITEM_DISPLAY_ID = getEntityDataAccessor(Display.ItemDisplay.class, "DATA_ITEM_DISPLAY_ID");

    @SuppressWarnings("unchecked")
    private static <T> EntityDataAccessor<T> getEntityDataAccessor(Class<?> clazz, String id) {
        Field field;

        try {
            field = clazz.getDeclaredField(id);
        } catch (NoSuchFieldException e) {
            try {
                field = clazz.getField(id);
            } catch (NoSuchFieldException ex) {
                throw new RuntimeException("Failed to find entity data accessor: " + id, ex);
            }
        }
        if(!field.canAccess(null)) field.setAccessible(true);
        try {
            return (EntityDataAccessor<T>) field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access entity data accessor: " + id, e);
        }
    }

}
