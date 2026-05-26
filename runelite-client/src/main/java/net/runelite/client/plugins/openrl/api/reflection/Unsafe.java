package net.runelite.client.plugins.openrl.api.reflection;

import java.lang.reflect.Field;

public class Unsafe
{
	private static final sun.misc.Unsafe unsafe;

	static
	{
		try
		{
			final Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			unsafeField.setAccessible(true);
			unsafe = (sun.misc.Unsafe) unsafeField.get(null);
		}
		catch (Exception ex)
		{
			throw new RuntimeException("Failed to create unsafe instance", ex);
		}
	}

	public static sun.misc.Unsafe getInstance()
	{
		return unsafe;
	}

	public static long getFieldOffset(Class<?> clazz, String fieldName) throws NoSuchFieldException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		return unsafe.objectFieldOffset(field);
	}

	public static Object readObjectField(Object obj, String fieldName) throws NoSuchFieldException
	{
		return unsafe.getObject(obj, getFieldOffset(obj.getClass(), fieldName));
	}

	public static void writeObjectField(Object obj, String fieldName, Object value) throws NoSuchFieldException
	{
		unsafe.putObject(obj, getFieldOffset(obj.getClass(), fieldName), value);
	}

	public static int readIntField(Object obj, String fieldName) throws NoSuchFieldException
	{
		return unsafe.getInt(obj, getFieldOffset(obj.getClass(), fieldName));
	}

	public static void writeIntField(Object obj, String fieldName, int value) throws NoSuchFieldException
	{
		unsafe.putInt(obj, getFieldOffset(obj.getClass(), fieldName), value);
	}

	public static long readLongField(Object obj, String fieldName) throws NoSuchFieldException
	{
		return unsafe.getLong(obj, getFieldOffset(obj.getClass(), fieldName));
	}

	public static void writeLongField(Object obj, String fieldName, long value) throws NoSuchFieldException
	{
		unsafe.putLong(obj, getFieldOffset(obj.getClass(), fieldName), value);
	}

	public static boolean readBooleanField(Object obj, String fieldName) throws NoSuchFieldException
	{
		return unsafe.getBoolean(obj, getFieldOffset(obj.getClass(), fieldName));
	}

	public static void writeBooleanField(Object obj, String fieldName, boolean value) throws NoSuchFieldException
	{
		unsafe.putBoolean(obj, getFieldOffset(obj.getClass(), fieldName), value);
	}

	public static byte readByteField(Object obj, String fieldName) throws NoSuchFieldException
	{
		return unsafe.getByte(obj, getFieldOffset(obj.getClass(), fieldName));
	}

	public static void writeByteField(Object obj, String fieldName, byte value) throws NoSuchFieldException
	{
		unsafe.putByte(obj, getFieldOffset(obj.getClass(), fieldName), value);
	}

	public static short readShortField(Object obj, String fieldName) throws NoSuchFieldException
	{
		return unsafe.getShort(obj, getFieldOffset(obj.getClass(), fieldName));
	}

	public static void writeShortField(Object obj, String fieldName, short value) throws NoSuchFieldException
	{
		unsafe.putShort(obj, getFieldOffset(obj.getClass(), fieldName), value);
	}

	public static float readFloatField(Object obj, String fieldName) throws NoSuchFieldException
	{
		return unsafe.getFloat(obj, getFieldOffset(obj.getClass(), fieldName));
	}

	public static void writeFloatField(Object obj, String fieldName, float value) throws NoSuchFieldException
	{
		unsafe.putFloat(obj, getFieldOffset(obj.getClass(), fieldName), value);
	}

	public static double readDoubleField(Object obj, String fieldName) throws NoSuchFieldException
	{
		return unsafe.getDouble(obj, getFieldOffset(obj.getClass(), fieldName));
	}

	public static void writeDoubleField(Object obj, String fieldName, double value) throws NoSuchFieldException
	{
		unsafe.putDouble(obj, getFieldOffset(obj.getClass(), fieldName), value);
	}

	// Static field read/write

	public static Object readStaticObjectField(Class<?> clazz, String fieldName) throws NoSuchFieldException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		return unsafe.getObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
	}

	public static void writeStaticObjectField(Class<?> clazz, String fieldName, Object value) throws NoSuchFieldException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		unsafe.putObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
	}

	public static int readStaticIntField(Class<?> clazz, String fieldName) throws NoSuchFieldException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		return unsafe.getInt(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
	}

	public static void writeStaticIntField(Class<?> clazz, String fieldName, int value) throws NoSuchFieldException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		unsafe.putInt(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
	}

	public static long readStaticLongField(Class<?> clazz, String fieldName) throws NoSuchFieldException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		return unsafe.getLong(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
	}

	public static void writeStaticLongField(Class<?> clazz, String fieldName, long value) throws NoSuchFieldException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		unsafe.putLong(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
	}

	public static boolean readStaticBooleanField(Class<?> clazz, String fieldName) throws NoSuchFieldException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		return unsafe.getBoolean(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
	}

	public static void writeStaticBooleanField(Class<?> clazz, String fieldName, boolean value) throws NoSuchFieldException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		unsafe.putBoolean(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
	}

	public static byte readStaticByteField(Class<?> clazz, String fieldName) throws NoSuchFieldException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		return unsafe.getByte(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
	}

	public static void writeStaticByteField(Class<?> clazz, String fieldName, byte value) throws NoSuchFieldException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		unsafe.putByte(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
	}

	public static short readStaticShortField(Class<?> clazz, String fieldName) throws NoSuchFieldException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		return unsafe.getShort(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
	}

	public static void writeStaticShortField(Class<?> clazz, String fieldName, short value) throws NoSuchFieldException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		unsafe.putShort(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
	}

	public static float readStaticFloatField(Class<?> clazz, String fieldName) throws NoSuchFieldException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		return unsafe.getFloat(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
	}

	public static void writeStaticFloatField(Class<?> clazz, String fieldName, float value) throws NoSuchFieldException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		unsafe.putFloat(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
	}

	public static double readStaticDoubleField(Class<?> clazz, String fieldName) throws NoSuchFieldException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		return unsafe.getDouble(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
	}

	public static void writeStaticDoubleField(Class<?> clazz, String fieldName, double value) throws NoSuchFieldException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		unsafe.putDouble(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
	}
}