package demo.chat.yingshe.com.customrectangleprogressview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
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
//    private int color = Color.WHITE;    // 随时设置随时变化
//    private int bgColor = Color.parseColor("#00d478");    // 随时设置随时变化
    private int color = Color.parseColor("#00d478");    // 随时设置随时变化
    private int bgColor = Color.WHITE;    // 随时设置随时变化
    // 矩形进度
    private float currentValue = 0;    // 随时设置随时变化
    // 起始角度
    private float startAngle = -90; // 默认是正北方向 -90度(如果需要在左上角或者右上角，需要根据边长计算角度，然后设置)
    // 更新UI周期（默认40ms）
    int updateTime = 30;

    int currentLocation = 0;    // 当前位置
    // 倒计时总时长 单位 s 秒
    private int duration = 60;
    private Paint paint;    // 画笔
    private PorterDuffXfermode xfermode;    // 画笔模式
    private RectF rectF;    // 方框的位置
    private RectF rectF2;   // 覆盖方框 擦除方框的位置
    private progressListener listener;  // 进度变化监听器
    private boolean running = false;    // 是否正在运行的标记
    private boolean stop = true;   // 是否停止
    private int currentSecond;  // 当前秒
    private boolean isDestroy;  // 是否销毁
    private int totalCount;
    private float everyAngle;
    private Handler handler;

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
        handler = new Handler();
        paint = new Paint();
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
//        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
//        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
//        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
//        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
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
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
//        startAngle = -90;
        float a = ((float) (measuredWidth + 200) / 2) / ((float) (measuredHeight + 200) / 2);
        float atan = (float) Math.atan(a);
        float degrees = (float) Math.toDegrees(atan);
        startAngle = -90 - degrees;
//        if (LogUtil.isLog()) LogUtil.s("  获取到的角度：" + atan + "  measuredWidth：" + measuredWidth + "   measuredHeight:" + measuredHeight + "  a：" + a + "   degrees:" + degrees);
//        if (LogUtil.isLog()) LogUtil.s("  最终获取到的角度：" + startAngle);
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
        paint.setColor(bgColor);
        paint.setXfermode(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(thickness);
        canvas.drawRect(rectF, paint);
        // 设置遮挡属性
        paint.setXfermode(xfermode);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        if (stop && currentValue != 0) {
            // 不是清空状态的时候，在停止状态下不允许更新UI
            return;
        }
        if (currentLocation >= totalCount) {
            // 最后一点点了。绘制扇形
            canvas.drawArc(rectF2, startAngle, 0, true, paint);
        } else {
            // 绘制扇形
            canvas.drawArc(rectF2, startAngle, currentValue, true, paint);
        }
        // 将第二层反馈给画布
        canvas.restoreToCount(sc);

//        ==========================================================
        if (currentLocation >= totalCount) {
            currentValue = 0;
        } else {
            currentValue = 360 - ((float) currentLocation * everyAngle);
        }

        if (isDestroy) {
            return;
        }

        if (listener == null) {
//            runnable = null;
            return;
        }
        if (stop) {
            running = false;
            return;
        }

        if (currentLocation >= totalCount) {
            if (listener != null) {
                listener.over();
            }
            return;
        } else {
            // 当前秒
            int i1 = currentLocation * updateTime / 1000;
            if (currentSecond != i1) {
                currentSecond = i1;
                listener.progress(duration, i1);
            }
        }
        currentLocation++;
        if (handler != null) {
            handler.postDelayed(invalideteRunnable, updateTime);
        }
//        =======================================================
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

    public float getStartAngle() {
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
//        if (LogUtil.isLog()) LogUtil.s("  收到了开始倒计时的调用：" + time);
        duration = time;
        if (duration <= 0) {
            return;
        }
        // 如果40ms更新一次 计算一共需要更新多少次
        totalCount = (duration * 1000) / updateTime;
//        if (LogUtil.isLog())
//            LogUtil.s(" 准备开始倒计时~  duration：" + duration + "  updateTime:" + updateTime + "  total:" + totalCount);
        if (totalCount <= 0) {
            return;
        }
        if (listener != null) {
            listener.start();
        }
        // 每次更新多少度
        everyAngle = (360 / (float) totalCount);
        stop = false;
        postInvalidate();
    }

    private Runnable invalideteRunnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    /**
     * 停止更新（不会回调over）
     */
    public void stop() {
        currentValue = 0;
        stop = true;
        running = false;
        currentLocation = 0;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        postInvalidate();
    }

    public void refreshState() {
        currentLocation = 0;
        currentValue = 0;
    }

    /**
     * 暂停更新（不会回调over）
     */
    public void pause() {
        stop = true;
        running = false;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public void destroy() {
        isDestroy = true;
        if (listener != null) {
            listener = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
}
