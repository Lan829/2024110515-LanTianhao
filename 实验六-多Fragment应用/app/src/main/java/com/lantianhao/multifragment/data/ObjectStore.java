package com.lantianhao.multifragment.data;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 对象序列化存储工具。
 *
 * <p>写文件使用 Context.openFileOutput + ObjectOutputStream，
 * 读文件使用 Context.openFileInput + ObjectInputStream，
 * 保存的对象必须实现 {@link Serializable}。</p>
 */
public final class ObjectStore {

    public static final String PLAYER_FILE = "player.dat";
    public static final String GAME_TWO_FILE = "game_two.dat";
    public static final String GAME_THREE_FILE = "game_three.dat";

    private ObjectStore() {
    }

    /**
     * 读取对象，文件不存在或读取失败时返回 null。
     */
    @SuppressWarnings("unchecked")
    public static <T> T read(Context context, String fileName) {
        try (FileInputStream fileInput = context.openFileInput(fileName);
             ObjectInputStream objectInput = new ObjectInputStream(fileInput)) {
            return (T) objectInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * 把对象写入应用私有文件。
     */
    public static void write(Context context, String fileName, Serializable data) {
        try (FileOutputStream fileOutput = context.openFileOutput(fileName, Context.MODE_PRIVATE);
             ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput)) {
            objectOutput.writeObject(data);
        } catch (IOException ignored) {
            // 存储失败时保留当前内存数据，下次保存重试。
        }
    }
}
