package demo.chat.yingshe.com.customrectangleprogressview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 菜鹰帅帅 on 2016/11/30.
 * 矩形倒计时进度框
 */

public class RectangleProgressView extends View {

    // 矩形厚度
    private float thickness = 30;
    // 矩形颜色
    private int color = Color.parseColor("#50B5EB");    // 随时设置随时变化
    // 矩形进度
    private float currentValue = 0;    // 随时设置随时变化
    // 起始角度
    private int startAngle = -90; // 默认是正北方向 -90度
    // 更新UI周期（默认40ms） 可以自行设置 越小 更新频率越快！越大 可能就会造成卡顿 因为更新频率低
    int updateTime = 30;
    // 倒计时总时长 单位 s 秒
    private int duration = 60;
    private Paint paint;    // 画笔
    private PorterDuffXfermode xfermode;    // 画笔模式
    private RectF rectF;    // 方框的位置
    private RectF rectF2;   // 覆盖方框 擦除方框的位置
    private progressListener listener;  // 进度变化监听器
    private boolean running = false;    // 是否正在运行的标记
    private boolean stop;   // 是否停止
    private int currentSecond;  // 当前秒

    public interface progressListener {
        /**
         * 倒计时结束
         */
        void over();

        /**
         * 开始
         */
        void start();

        /**
         * 进度回调
         *
         * @param total    总共秒数
         * @param progress 当前秒数
         */
        void progress(int total, float progress);
    }

    public RectangleProgressView(Context context) {
        super(context);
        initData();
    }

    public RectangleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public RectangleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void initData() {
        paint = new Paint();
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        rectF = new RectF();
        rectF2 = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getMeasuredWidth() - getPaddingRight();
        int bottom = getMeasuredHeight() - getPaddingBottom();
        rectF.set(left, top, right, bottom);
        rectF2.set(-200, -200, getMeasuredWidth() + 200, getMeasuredHeight() + 200);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制第二层
        int sc = canvas.saveLayer(rectF.left, rectF.top, rectF.right, rectF.bottom, null,
                Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
                        | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                        | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                        | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
        // 绘制矩形框
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setXfermode(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(thickness);
        canvas.drawRect(rectF, paint);
        // 设置遮挡属性
        paint.setXfermode(xfermode);
        paint.setStyle(Paint.Style.FILL);
        // 绘制扇形
        canvas.drawArc(rectF2, startAngle, currentValue, true, paint);
        // 将第二层反馈给画布
        canvas.restoreToCount(sc);
    }

    /**
     * 设置颜色
     *
     * @param color 颜色色值
     */
    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public float getThickness() {
        return thickness;
    }

    /**
     * 设置矩形框厚度
     *
     * @param thickness 厚度（px）
     */
    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    public int getStartAngle() {
        return startAngle;
    }

    /**
     * 设置开始角度 默认为-90 正北方向(顺时针)
     *
     * @param startAngle 开始角度
     */
    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }


    /**
     * 获取当前秒数
     *
     * @return
     */
    public int getCurrentSecond() {
        return currentSecond;
    }

    /**
     * 获取总共秒数
     *
     * @return
     */
    public int getTotalSecond() {
        return duration;
    }


    /**
     * 进度变化监听
     */
    public void setProgressListener(progressListener listener) {
        this.listener = listener;
    }

    /**
     * 设置秒数，开始倒计时(不可重复设置)
     *
     * @param time 倒计时秒数
     */
    public void start(final int time) {

        duration = time;
        if (duration <= 0) {
            return;
        }

//        ThreadPoolUtil.execute(runnable); // 线程池方式运行
        new Thread(runnable).start();   // 每次都是新建线程！考虑这个线程可能持续相当长的时间 就不占用线程池的线程了！

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            SystemClock.sleep(updateTime);  // 睡眠更新频率的时间，避免出现多个线程共同运行的情况！

            if (running) {
                // 如果正在运行，则不可再次运行
                return;
            }
            stop = false;
            running = true;

            // 如果40ms更新一次 计算一共需要更新多少次
            int totalCount = (duration * 1000) / updateTime;
            if (totalCount <= 0) {
                return;
            }
            if (listener != null) {
                listener.start();
            }

            // 恢复状态
            currentValue = 0;


            // 每次更新多少度
            float everyAngle = (360 / (float) totalCount);
            for (int i = 0; i <= totalCount; i++) {
                if (stop) {
                    running = false;
                    return;
                }
                currentValue = ((float) i * everyAngle);
                postInvalidate();
                SystemClock.sleep(updateTime);
                if (stop) {
                    running = false;
                    return;
                }
                // 当前秒
                int i1 = i * updateTime / 1000;
                if (listener != null && currentSecond != i1) {
                    currentSecond = i1;
                    listener.progress(duration, i1);
                }
            }
            running = false;
            if (listener != null) {
                listener.over();
            }
        }
    };


    /**
     * 停止更新（不会回调over）
     */
    public void stop() {
        stop = true;
        running = false;
        currentValue = 0;
        postInvalidate();
    }

    /**
     * 暂停更新（不会回调over）
     */
    public void pause() {
        stop = true;
    }
}
