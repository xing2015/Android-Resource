import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CountdownUtils {
    private static DecimalFormat df00 = new DecimalFormat("00");
    private static UITimer uiTimer;

    /**
     * 格式化观看进度
     *
     * @param lastPlayMillisecond  上次播放到多少毫秒
     * @param durationMillisencond 总毫秒数
     * @return
     */
    public static String formatWatchProgress(int lastPlayMillisecond, int durationMillisencond) {
        String where = "已看完";
        if (durationMillisencond != lastPlayMillisecond) {
            double wh = lastPlayMillisecond * 1.0 / durationMillisencond;
            int pInt = (int) (wh * 100);
            pInt = pInt > 0 ? pInt : 1;
            String per = String.valueOf(pInt);
            where = "观看至" + per + "%";
        }
        return where;
    }

    /**
     * 格式化毫秒为日期
     *
     * @param milliseconds
     * @return
     */
    public static String formatMillisecondsToDate(long milliseconds) {
        SimpleDateFormat format1 = new SimpleDateFormat("MM/dd");
        return format1.format(new Date(milliseconds));
    }

    /**
     * 倒计时监听器
     */
    public static abstract class OnCountDownListener {
        /**
         * 倒计时回调
         *
         * @param countDownHours   倒计小时
         * @param countDownMinutes 倒计分钟
         * @param countDownSeconds 倒计秒
         */
        void onCountDown(int countDownHours, int countDownMinutes, int countDownSeconds) {
            onCountDown(String.format("%s:%s:%s", df00.format(countDownHours), df00.format(countDownMinutes), df00.format(countDownSeconds))
            ,countDownHours,countDownMinutes,countDownSeconds);
        }

        /**
         * 倒计时回调
         *
         * @param countDownTime 倒计时时间字符串
         */
        public abstract void onCountDown(String countDownTime,int hour,int minutes,int second);

    }

    public static abstract class OnLiveCountDownListener extends OnCountDownListener {
        public abstract void onLiveStart();
    }

    /**
     * 指定日期到现在的倒计时
     *
     * @param startUnixTimestamp 开始的Unix时间戳
     * @param l
     */
    public static void countDown(final long curUnixTimestamp, final long startUnixTimestamp, final OnCountDownListener l) {
        if (l == null) {
            return;
        }
        if (uiTimer != null) {
            uiTimer.cancel();
            uiTimer = null;
        }
        uiTimer = new UITimer();
        uiTimer.schedule(new CountDownRuanable(startUnixTimestamp, curUnixTimestamp, l), 1000);
    }

    public static class CountDownRuanable implements Runnable {
        long startUnixTimestamp;
        long curUnixTimestamp;
        OnCountDownListener l;

        public CountDownRuanable(long startUnixTimestamp, long curUnixTimestamp, OnCountDownListener l) {
            this.startUnixTimestamp = startUnixTimestamp;
            this.curUnixTimestamp = curUnixTimestamp;
            this.l = l;
        }

        @Override
        public void run() {
            curUnixTimestamp++;
            long intervalSeconds = startUnixTimestamp - curUnixTimestamp;
            int countDownHours = (int) (intervalSeconds / (60 * 60));
            int countDownMinutes = (int) (intervalSeconds / 60 - countDownHours * 60);
            int countSeconds = (int) (intervalSeconds - countDownMinutes * 60 - countDownHours * 60 * 60);
            l.onCountDown(countDownHours, countDownMinutes, countSeconds);
        }
    }

    /**
     * 开始直播倒计时
     *
     * @param curUnixTimestamp   当前的Unix时间戳
     * @param startUnixTimestamp 开始的Unix时间戳
     * @param endUnixTimestamp   结束的Unix时间戳
     * @param l
     */
    public static void startLiveCountDown(final long curUnixTimestamp, final long startUnixTimestamp, final long endUnixTimestamp, final OnLiveCountDownListener l) {
        try {
            if (l == null) {
                return;
            }
            if (startUnixTimestamp <= curUnixTimestamp || endUnixTimestamp <= curUnixTimestamp) {
                l.onLiveStart();
                return;
            }
            countDown(curUnixTimestamp, startUnixTimestamp, new OnCountDownListener() {
                @Override
                public void onCountDown(String countDownTime,int hour,int minutes,int second) {
                }

                @Override
                void onCountDown(int countDownHours, int countDownMinutes, int countDownSeconds) {
                    l.onCountDown(countDownHours, countDownMinutes, countDownSeconds);
                    if (countDownHours == 0 && countDownMinutes == 0 && countDownSeconds == 0) {
                        stopLiveCountDown();
                        l.onLiveStart();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止直播倒计时
     */
    public static void stopLiveCountDown() {
        if (uiTimer != null) {
            uiTimer.cancel();
            uiTimer = null;
        }
    }
}
