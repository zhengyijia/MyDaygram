package com.oneplus.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Oneplus on 2016/9/16.
 */
public class BufferStore<T extends Serializable & Comparable<T>> {
    private final String mBuffPath;

    /**
     * @param buffPath 存放缓存的路径
     */
    public BufferStore(String buffPath) {
        mBuffPath = buffPath;
    }

    /**
     * @param list     向本地写入的缓存数据
     * @param maxCount 本地缓存的最大数据量
     */
    public synchronized void write(List<T> list, int maxCount) {
        if (list == null || maxCount <= 0) {
            return;
        }
        // 获得缓存数据
        List<T> oldList = get();
        // 将新数据加入
        for (T t : list) {
            // 不存在才加入
            if (!oldList.contains(t)) {
                oldList.add(t);
            }
        }

        // 将数据排序
        Collections.sort(oldList);

        // 删除多余数据
        for (int i = oldList.size() - 1; i >= maxCount; i--) {
            oldList.remove(i);
        }

        // 写入本地
        put(oldList);
    }

    /**
     * 读取缓存数据
     *
     * @return 缓存数据，数据为空时返回长度为0的list
     */
    public synchronized List<T> read() {
        return get();
    }

    /**
     * 向本地写入数据
     */
    private void put(List<T> list) {

        try {
            // 打开文件
            FileOutputStream fos = new FileOutputStream(mBuffPath);

            // 将数据写入文件
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(list);

            // 释放资源
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从本地读取数据
     */
    @SuppressWarnings("unchecked")
    private List<T> get() {
        List<T> list = new ArrayList<T>();
        try {
            File file = new File(mBuffPath);
            if (!file.exists()) {
                return list;
            }

            // 打开文件
            FileInputStream fis = new FileInputStream(mBuffPath);

            // 读取文件
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (List<T>) ois.readObject();

            // 释放资源
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

}
