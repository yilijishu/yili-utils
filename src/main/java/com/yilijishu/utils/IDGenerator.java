package com.yilijishu.utils;

/**
 * 雪花算法
 */
public class IDGenerator {

    private final long originTimestamp = 1288834974657L;
    private final long machineBits = 8L;
    private final long bussinessBits = 5L;
    /**
     * 机器ID最大数
     */
    private final long maxMachineId = -1L ^ (-1L << machineBits);

    /**
     * 机器ID最大数
     */
    private final long maxBussinessId = -1L ^ (-1L << bussinessBits);

    /**
     * 序号位数
     */
    private final long sequenceBits = 9L;
    /**
     * 序号最大
     */
    private final long maxSequenceId = -1L ^ (-1L << sequenceBits);

    private long machineId;

    private long bussinessId;

    private long sequence = 0L;

    private long lastTimestamp = -1L;

    /**
     * 初始化机器码和业务码。
     *
     * @param machineId 机器码
     * @param bussinessId 业务码
     */
    public IDGenerator(long machineId, long bussinessId) {
        if (machineId > maxMachineId || machineId < 0) {
            throw new IllegalArgumentException(String.format("machine Id can't be greater than %d or less than 0", maxMachineId));
        }
        if (bussinessId > maxBussinessId || bussinessId < 0) {
            throw new IllegalArgumentException(String.format("bussiness Id can't be greater than %d or less than 0", maxBussinessId));
        }
        this.machineId = machineId;
        this.bussinessId = bussinessId;
    }

    /**
     * 生成ID，【同步】
     * @return 返回ID
     */
    public synchronized long getId() {
        long timestamp = getCurrentTimeStamp();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp != timestamp) {
            sequence = 0L;
        } else {
            sequence = (sequence + 1) & maxSequenceId;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        }
        lastTimestamp = timestamp;
        return ((timestamp - originTimestamp) << (sequenceBits + bussinessBits + machineBits))
                | (machineId << (sequenceBits + bussinessBits)) | (bussinessId << sequenceBits) | sequence;
    }

    /**
     * 根据当前时间获取ID【同步】
     * @param currentTimeStamp 当前时间戳
     * @return ID
     */
    public synchronized long getId(long currentTimeStamp) {
        long timestamp = currentTimeStamp;
//        if (timestamp < lastTimestamp) {
//            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
//        }
        if (lastTimestamp != timestamp) {
            sequence = 0L;
        } else {
            sequence = (sequence + 1) & maxSequenceId;
            if (sequence == 0) {
                timestamp = getNextMillis(lastTimestamp, timestamp);
            }
        }
        lastTimestamp = timestamp;
        return ((timestamp - originTimestamp) << (sequenceBits + bussinessBits + machineBits))
                | (machineId << (sequenceBits + bussinessBits)) | (bussinessId << sequenceBits) | sequence;
    }

    /**
     * 获取当前时间戳
     * @return 时间戳
     */
    protected long getCurrentTimeStamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取下一个时间戳
     * @param lastTimestamp 上一个时间戳
     * @param currentTimeStamp 当前时间戳
     * @return 下一个时间戳
     */
    protected long getNextMillis(long lastTimestamp, long currentTimeStamp) {
        long timestamp = currentTimeStamp;
        while (timestamp <= lastTimestamp) {
            timestamp = timestamp + 1L;
        }
        return timestamp;
    }

    /**
     * 等待下一个时间戳
     * @param lastTimestamp 上一个时间戳
     * @return 时间戳
     */
    protected long waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimeStamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimeStamp();
        }
        return timestamp;
    }

    /**
     * 判断是否为生成ID
     * @param id ID
     * @return 真假
     */
    public static boolean isGenerator(long id) {
        if(id >= 1000000000000000000L) {
            String str = Long.toBinaryString(id);
            Long a = Long.parseLong(str.substring(0, 41),2);
            return System.currentTimeMillis() >= a;
        }
        return false;

    }

}