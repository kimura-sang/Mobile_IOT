package com.chug.north_outlet.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.chug.north_outlet.utils.DensityUtils;


public class DragGridView extends GridView {


    public static enum DragState {
        /**
         * LONG_CLICK_DRAG 长按拖动状态
         * TOUCH_DRAG      触摸拖动状态
         */
        NONE, LONG_CLICK_DRAG, TOUCH_DRAG
    }

    private DragState mState = DragState.TOUCH_DRAG;

    /**
     * 点击时候的X位置
     */
    public int downX;
    /**
     * 点击时候的Y位置
     */
    public int downY;

    public int lastY;

    /**
     * 点击时候对应整个界面的X位置
     */
    public  int windowX;
    /**
     * 点击时候对应整个界面的Y位置
     */
    public  int windowY;
    /**
     * 屏幕上的X
     */
    private int win_view_x;
    /**
     * 屏幕上的Y
     */
    private int win_view_y;
    /**
     * 拖动的里x的距离
     */
    int dragOffsetX;
    /**
     * 拖动的里Y的距离
     */
    int dragOffsetY;
    /**
     * 长按时候对应postion
     */
    public int dragPosition;
    /**
     * 拖动的时候对应ITEM的VIEW
     */
    private View dragImageView = null;
    /**
     * 拖动的时候对应ITEM的视图
     */
    private Bitmap dragBitmap;
    /**
     * 长按的时候ITEM的VIEW
     */
    private ViewGroup                  dragItemView  = null;
    /**
     * WindowManager管理器
     */
    private WindowManager              windowManager = null;
    /** */
    private WindowManager.LayoutParams windowParams  = null;
    /**
     * 是否在移动
     */
    private boolean                    isMoving      = false;
    /**
     * 拖动的时候放大的倍数
     */
    private double                     dragScale     = 1.0D;
    /**
     * 震动器
     */
    private Vibrator mVibrator;
    /**
     * 每个ITEM之间的水平间距
     */
    private int mHorizontalSpacing = 15;

    private MotionEvent downEv;
    private  Context mContext;
    public DragGridView(Context context) {
        super(context);
        init(context);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        mContext=context;
        if (isInEditMode()) {
            return;
        }
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // 将布局文件中设置的间距dip转为px
        mHorizontalSpacing = DensityUtils.dip2px(context, mHorizontalSpacing);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downX = (int) ev.getX();
            downY = (int) ev.getY();
            windowX = (int) ev.getX();
            windowY = (int) ev.getY();
            downEv = ev;
            //			if (mState == DragState.LONG_CLICK_DRAG) {
            //				setOnItemClickListener(ev);
            //			} else if (mState == DragState.TOUCH_DRAG) {
            //				setTouchDragStart(ev);
            //			}
        }
        return super.onInterceptTouchEvent(ev);
    }
  /*  GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d("hh", "双击了吗");
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d("hh", "是谁双击了");
            return true;
        }
    });*/
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        // if (dragImageView != null && dragPosition !=
        // AdapterView.INVALID_POSITION) {
        // // 移动时候的对应x,y位置
        // int x = (int) ev.getX();
        // int y = (int) ev.getY();
        // switch (ev.getAction()) {
        // case MotionEvent.ACTION_DOWN:
        // downX = (int) ev.getX();
        // windowX = (int) ev.getX();
        // downY = (int) ev.getY();
        // windowY = (int) ev.getY();
        // requestDisallowInterceptTouchEvent(true);
        // break;
        // case MotionEvent.ACTION_MOVE:
        // int rawX = (int) ev.getRawX();
        // int rawY = (int) ev.getRawY();
        // onDrag(x, y ,rawX , rawY);
        // if(listener != null){
        // boolean isMoveUp = rawY < lastY;
        // int delaY = rawY - lastY;
        // listener.onMoving(windowParams.x, windowParams.y,isMoveUp,delaY);
        // }
        // lastY = rawY;
        // requestDisallowInterceptTouchEvent(true);
        // break;
        // case MotionEvent.ACTION_CANCEL:
        // case MotionEvent.ACTION_UP:
        // isMoving = false;
        // if(listener != null){
        // listener.onDrop(this,windowParams.x + dragImageView.getWidth()/2,
        // windowParams.y +
        // dragImageView.getHeight()/2,getItemAtPosition(dragPosition),dragBitmap);
        // }
        // stopDrag();
        // requestDisallowInterceptTouchEvent(false);
        // break;
        //
        // default:
        // break;
        // }
        // }
        // 移动时候的对应x,y位置
        if (mState == DragState.TOUCH_DRAG) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = (int) ev.getX();
                    windowX = (int) ev.getX();
                    downY = (int) ev.getY();
                    windowY = (int) ev.getY();
                    //				requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (dragImageView == null) {
                        setTouchDragStart(ev);
                    }
                    if (dragPosition != AdapterView.INVALID_POSITION) {
                        int rawX = (int) ev.getRawX();
                        int rawY = (int) ev.getRawY();
                        onDrag(x, y, rawX, rawY);
                        if (listener != null && windowParams != null && dragImageView != null) {
                            boolean isMoveUp = rawY < lastY;
                            int delaY = rawY - lastY;
                            listener.onMoving(windowParams.x, windowParams.y, isMoveUp, delaY);
                        }
                        lastY = rawY;
                        requestDisallowInterceptTouchEvent(true);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    isMoving = false;
                    if (listener != null && windowParams != null && dragImageView != null) {
                        listener.onDrop(this, windowParams.x + dragImageView.getWidth() / 2, windowParams.y + dragImageView.getHeight() / 2, getItemAtPosition(dragPosition), dragBitmap);
                    }
                    stopDrag();
                    requestDisallowInterceptTouchEvent(false);
                    break;

                default:
                    break;
            }
        }
       // gestureDetector.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    /**
     * 在拖动的情况
     */
    private void onDrag(int x, int y, int rawx, int rawy) {
        if (dragImageView != null) {
            windowParams.alpha = 0.6f;
            // windowParams.x = rawx - itemWidth / 2;
            // windowParams.y = rawy - itemHeight / 2;
            windowParams.x = rawx - win_view_x;
            windowParams.y = rawy - win_view_y;
            windowManager.updateViewLayout(dragImageView, windowParams);
        }
    }

    /**
     * 长按点击监听
     *
     * @param ev
     */
    public void setOnItemClickListener(final MotionEvent ev) {
        setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int x = (int) ev.getX();// 长按事件的X位置
                int y = (int) ev.getY();// 长按事件的y位置

                dragPosition = position;

                // 如果特殊的这个不等于拖动的那个,并且不等于-1
                if (dragPosition != AdapterView.INVALID_POSITION) {
                    dragItemView = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
                    // 释放的资源使用的绘图缓存。如果你调用buildDrawingCache()手动没有调用setDrawingCacheEnabled(真正的),你应该清理缓存使用这种方法。
                    win_view_x = windowX - dragItemView.getLeft();// VIEW相对自己的X，半斤
                    win_view_y = windowY - dragItemView.getTop();// VIEW相对自己的y，半斤
                    dragOffsetX = (int) (ev.getRawX() - x);// 手指在屏幕的上X位置-手指在控件中的位置就是距离最左边的距离
                    dragOffsetY = (int) (ev.getRawY() - y);// 手指在屏幕的上y位置-手指在控件中的位置就是距离最上边的距离
                    dragItemView.destroyDrawingCache();
                    dragItemView.setDrawingCacheEnabled(true);
                    dragBitmap = Bitmap.createBitmap(dragItemView.getDrawingCache());
                    mVibrator.vibrate(50);// 设置震动时间
                    startDrag(dragBitmap, (int) ev.getRawX(), (int) ev.getRawY());
                    dragItemView.setVisibility(View.INVISIBLE);
                    isMoving = true;
                    lastY = (int) ev.getRawY();
                    requestDisallowInterceptTouchEvent(true);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * @param ev
     */
    private void setTouchDragStart(MotionEvent ev) {
        int x = (int) ev.getX();// 长安事件的X位置
        int y = (int) ev.getY();// 长安事件的y位置

        dragPosition = pointToPosition(x, y);
        if (dragPosition != AdapterView.INVALID_POSITION) {
            dragItemView = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
            // 如果特殊的这个不等于拖动的那个,并且不等于-1
            // 释放的资源使用的绘图缓存。如果你调用buildDrawingCache()手动没有调用setDrawingCacheEnabled(真正的),你应该清理缓存使用这种方法。
            win_view_x = windowX - dragItemView.getLeft();// VIEW相对自己的X，半斤
            win_view_y = windowY - dragItemView.getTop();// VIEW相对自己的y，半斤
            dragOffsetX = (int) (ev.getRawX() - x);// 手指在屏幕的上X位置-手指在控件中的位置就是距离最左边的距离
            dragOffsetY = (int) (ev.getRawY() - y);// 手指在屏幕的上y位置-手指在控件中的位置就是距离最上边的距离
            dragItemView.destroyDrawingCache();
            dragItemView.setDrawingCacheEnabled(true);
            dragBitmap = Bitmap.createBitmap(dragItemView.getDrawingCache());
            //			mVibrator.vibrate(50);// 设置震动时间
            startDrag(dragBitmap, (int) ev.getRawX(), (int) ev.getRawY());
            dragItemView.setVisibility(View.INVISIBLE);
            isMoving = true;
            lastY = (int) ev.getRawY();
            requestDisallowInterceptTouchEvent(false);
        }
    }

    public void startDrag(Bitmap dragBitmap, int x, int y) {
        stopDrag();
        windowParams = new WindowManager.LayoutParams();// 获取WINDOW界面的
        // Gravity.TOP|Gravity.LEFT;这个必须加
        windowParams.gravity = Gravity.TOP | Gravity.LEFT;
        // windowParams.x = x - (int)((itemWidth / 2) * dragScale);
        // windowParams.y = y - (int) ((itemHeight / 2) * dragScale);
        // 得到preview左上角相对于屏幕的坐标
        windowParams.x = x - win_view_x;
        windowParams.y = y - win_view_y;
        // this.windowParams.x = (x - this.win_view_x + this.viewX);//位置的x值
        // this.windowParams.y = (y - this.win_view_y + this.viewY);//位置的y值
        // 设置拖拽item的宽和高
        windowParams.width = (int) (dragScale * dragBitmap.getWidth());// 放大dragScale倍，可以设置拖动后的倍数
        windowParams.height = (int) (dragScale * dragBitmap.getHeight());// 放大dragScale倍，可以设置拖动后的倍数
        this.windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        this.windowParams.format = PixelFormat.TRANSLUCENT;
        this.windowParams.windowAnimations = 0;
        ImageView iv = new ImageView(getContext());
        iv.setImageBitmap(dragBitmap);
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);// "window_off"
        windowManager.addView(iv, windowParams);
        dragImageView = iv;
    }

    /**
     * 停止拖动 ，释放并初始化
     */
    private void stopDrag() {
        if (dragImageView != null) {
            windowManager.removeView(dragImageView);
            dragImageView = null;
        }
        if (dragItemView != null) {
            dragItemView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 在ScrollView内，所以要进行计算高度
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    public DragState getState() {
        return mState;
    }

    public void setState(DragState mState) {
        this.mState = mState;
    }

    public boolean getMovingState() {
        return isMoving;
    }

    // 回调接口
    private DragGridViewListener listener;

    public void setDragGridViewListener(DragGridViewListener listener) {
        this.listener = listener;
    }

    public interface DragGridViewListener {
        void onDrop(DragGridView grid, int x, int y, Object obj, Bitmap dragBitmap);

        void onMoving(int x, int y, boolean isMoveUp, int delaY);
    }
}