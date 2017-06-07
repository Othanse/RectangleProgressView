package demo.chat.yingshe.com.customrectangleprogressview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by 菜鹰帅帅 on 2016/11/30.
 * 矩形倒计时进度框
 */

public class RectangleProgressView2 extends View {

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

    public RectangleProgressView2(Context context) {
        super(context);
        initData();
    }

    public RectangleProgressView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public RectangleProgressView2(Context context, AttributeSet attrs, int defStyleAttr) {
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
//        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
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
        System.out.println("  想看下 会不会一直在画呢？");
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
//        paint.setXfermode(xfermode);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
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
        // 当前秒
        int i1 = currentLocation * updateTime / 1000;
        if (currentSecond != i1) {
            currentSecond = i1;
            listener.progress(duration, i1);
        }
        if (currentLocation >= totalCount) {
            System.out.println("  这是在结束的时候的哈 currentLocation：" + currentLocation + "  totalCount：" + totalCount);
            currentValue = 0;
            stop = true;
            running = false;
            currentLocation = 0;
            if (listener != null) {
                listener.over();
            }
            return;
        }
        currentLocation++;
        getHandler().postDelayed(invalideteRunnable, updateTime);
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
        System.out.println(" 开始倒计时 " + time);
//        if (LogUtil.isLog()) LogUtil.s("  收到了开始倒计时的调用：" + time);
        duration = time;
        if (duration <= 0) {
            return;
        }
//        if (runnable == null) {
//            return;
//        }
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

        // 恢复状态
//        currentValue = 0;


        // 每次更新多少度
        everyAngle = (360 / (float) totalCount);
        stop = false;
//        currentLocation = 0;
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
        System.out.println("  停止倒计时");
        getHandler().removeCallbacksAndMessages(null);
        currentLocation = totalCount;
        postInvalidate();
//        getHandler().removeCallbacksAndMessages(null);
    }

    /**
     * 暂停更新（不会回调over）
     */
    public void pause() {
        System.out.println(" 暂停倒计时");
        stop = true;
        running = false;
        getHandler().removeCallbacksAndMessages(null);
//        postInvalidate();
    }

    public void destroy() {
        System.out.println(" 销毁倒计时");

        isDestroy = true;
        if (listener != null) {
            listener = null;
        }
        getHandler().removeCallbacksAndMessages(null);
//        if (runnable != null) {
//            runnable = null;
//        }
    }
}