package me.unibike.config;

import java.util.UUID;

public abstract class UniLockConfig {

    public static final UUID READ_DATA_UUID;

    public static final UUID WRITE_DATA_UUID;

    public static final UUID WRITE_DATA_UUID_OTA;

    public static final int COMMAND_BYTE_LENGTH;

    static {
        WRITE_DATA_UUID_OTA=UUID.fromString("0000fd00-0000-1000-8000-00805f9b34fb");

        READ_DATA_UUID = UUID.fromString("000036f5-0000-1000-8000-00805f9b34fb");

        WRITE_DATA_UUID = UUID.fromString("000036f6-0000-1000-8000-00805f9b34fb");

        COMMAND_BYTE_LENGTH = 16;
    }
}
