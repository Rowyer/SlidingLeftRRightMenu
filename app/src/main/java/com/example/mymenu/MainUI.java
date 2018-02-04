package com.example.mymenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * Created by Administrator on 2018/1/31.
 */

public class MainUI extends RelativeLayout {
    private Context context;       //因为有左右菜单和中间部分，起承接上下文的关系
    private FrameLayout leftMenu;  //左菜单用FrameLayout填充，下同
    private FrameLayout middleMenu;
    private FrameLayout rightMenu;
    private FrameLayout middleMask;//蒙版
    private Scroller mScroller;
    public static final int LEFT_ID = 0xaabbcc;
    public static final int MIDDLE_ID = 0xaaccbb;
    public static final int RIGHT_ID = 0xccbbaa;

    public MainUI(Context context) {
        super(context);
        initView(context);
    }

    public MainUI(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        this.context = context;
        mScroller = new Scroller(context,new DecelerateInterpolator());

        leftMenu = new FrameLayout(context);    //初始化左菜单
        middleMenu = new FrameLayout(context);
        rightMenu = new FrameLayout(context);
        middleMask = new FrameLayout(context);

        leftMenu.setBackgroundColor(Color.WHITE);  //给左菜单设置背景背景颜色
        middleMenu.setBackgroundColor(Color.WHITE);
        rightMenu.setBackgroundColor(Color.WHITE);
        middleMask.setBackgroundColor(Color.GRAY);

        leftMenu.setId(LEFT_ID);
        middleMenu.setId(MIDDLE_ID);
        rightMenu.setId(RIGHT_ID);




        //将三个部分添加到RelativeLayout这个大View中
        addView(leftMenu);
        addView(middleMenu);
        addView(rightMenu);
        addView(middleMask);
        middleMask.setAlpha(0);
        onMiddleMask();
    }


    public  float onMiddleMask(){
        System.out.println("Alpha is"+middleMask.getAlpha());
        return middleMask.getAlpha();
    }


    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        onMiddleMask();
        int curX =Math.abs(getScrollX());
        float scale = curX/(float)leftMenu.getMeasuredWidth();
        middleMask.setAlpha(scale);


    }

    @Override
    //测量三个部分的宽高数据
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  //两个参数即为当前屏幕的宽高
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        middleMenu.measure(widthMeasureSpec,heightMeasureSpec);   //设置中间部分的宽度高度
        middleMask.measure(widthMeasureSpec,heightMeasureSpec);
        int realWidth = MeasureSpec.getSize(widthMeasureSpec);   //当前屏幕的整体宽度
        int tempWidthMeasure = MeasureSpec.makeMeasureSpec(      //左右菜单的宽度，两个参数
                (int)(realWidth*0.7f),MeasureSpec.EXACTLY);       //一个是宽度（主屏幕的80%)，一个是以怎样的方式测量
        leftMenu.measure(tempWidthMeasure,heightMeasureSpec);     //设置左右菜单的宽度高度
        rightMenu.measure(tempWidthMeasure,heightMeasureSpec);
    }

    @Override
    //通过onLayout方法将各个部分填充进去
    protected void onLayout(boolean changed, int l, int t, int r, int b) {//四个参数，改变监听、左、上、右、下
        super.onLayout(changed, l, t, r, b);
        middleMenu.layout(l, t, r, b);
        middleMask.layout(l, t, r, b);
        leftMenu.layout(l-leftMenu.getMeasuredWidth(),t,r,b);
        rightMenu.layout(
                l+middleMenu.getMeasuredWidth(),
                t,
               l+middleMenu.getMeasuredWidth()+rightMenu.getMeasuredWidth(),
               // r+rightMenu.getMeasuredWidth(),
                b);
    }

    //基本事件类型：ACTION_DOWN、ACTION_MOVE、ACTION_UP
    private boolean isTestCompete;   //判断是一个是怎样的事件
    private boolean leftrightEvent;


    @Override
    //事件分发
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(!isTestCompete){
            getEventType(ev);
            return true;
        }
        if (leftrightEvent){//左右滑动事件处理
            switch (ev.getActionMasked()){
                case MotionEvent.ACTION_MOVE:
                    int curScrollX = getScrollX();     //滚动距离
                    int dis_X = (int)(ev.getX()-point.x);    //滑动距离
                    int exceptX = -dis_X+curScrollX;
                    int finalX = 0;
                    if(exceptX<0){//向右滑动，出现左菜单
                        finalX = Math.max(exceptX,-leftMenu.getMeasuredWidth());
                    }else{//向左滑动，出现右菜单
                        finalX = Math.min(exceptX,rightMenu.getMeasuredWidth());
                    }
                    scrollTo(finalX,0);
                    point.x = (int)ev.getX();
                    break;


                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    curScrollX = getScrollX();
                    if(Math.abs(curScrollX)>leftMenu.getMeasuredWidth()>>1){//允许滑动
                        if(curScrollX<0) {//向右滑动，出现左菜单
                            mScroller.startScroll(curScrollX,
                                    0,
                                    -leftMenu.getMeasuredWidth()-curScrollX,
                                    0);
                        }else{//向左滑动，出现右菜单
                            mScroller.startScroll(curScrollX,
                                    0,
                                    rightMenu.getMeasuredWidth()-curScrollX,
                                    0);
                        }

                    }else{
                        mScroller.startScroll(curScrollX,0,-curScrollX,0);
                    }
                    invalidate();
                    isTestCompete = false;
                    leftrightEvent = false;
                    break;
            }
        }else {//上下滑动事件处理
            switch (ev.getActionMasked()){
                case MotionEvent.ACTION_UP:
                    isTestCompete = false;
                    leftrightEvent = false;
                    break;
            }
        }


        return super.dispatchTouchEvent(ev);
    }

    @Override
    //回调机制，复写回调方法
    public void computeScroll() {
        super.computeScroll();
        if(!mScroller.computeScrollOffset()){
            return;
        }
        int tempX = mScroller.getCurrX();
        scrollTo(tempX,0);
    }

    private Point point = new Point();  //获取当前屏幕的点，根据点获取屏幕滑动的距离
    private  static final int TEST_DIS = 20;
    //getEventType获取事件类型
    private void getEventType(MotionEvent ev) {

        switch (ev.getActionMasked()){
            case MotionEvent.ACTION_DOWN:     //手指放下屏幕
                point.x = (int)ev.getX();
                point.y = (int)ev.getY();

                super.dispatchTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:    //手指滑动屏幕
                int dX = Math.abs((int)ev.getX()-point.x);
                int dY = Math.abs((int)ev.getY()-point.y);
                if(dX>=TEST_DIS && dX>dY){//左右滑动
                    leftrightEvent = true;
                    isTestCompete = true;
                    point.x = (int)ev.getX();
                    point.y = (int)ev.getY();


                }else if(dY>=TEST_DIS && dY>dX){//上下滑动
                    leftrightEvent = false;
                    isTestCompete = true;
                    point.x = (int)ev.getX();
                    point.y = (int)ev.getY();

                }
                break;
            case MotionEvent.ACTION_UP:      //手指离开屏幕
            case MotionEvent.ACTION_CANCEL://触摸到边缘（代表当前的手势被取消）
                super.dispatchTouchEvent(ev);
                isTestCompete = false;
                leftrightEvent = false;
                break;
        }
    }
}
