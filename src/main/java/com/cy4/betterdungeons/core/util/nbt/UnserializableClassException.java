package com.cy4.betterdungeons.core.util.nbt;

@SuppressWarnings("serial")
public class UnserializableClassException extends Exception {

    private final Class<?> clazz;

    public UnserializableClassException(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getOffendingClass() {
        return this.clazz;
    }
}