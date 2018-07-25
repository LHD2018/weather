package com.lhd.weather2018.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.lhd.weather2018.R;

public class SlidingButtonView extends HorizontalScrollView {
    private TextView mTextView_delete;
    private int mScrollWidth;
    private SlidingButtonListener slidingButtonListener;
    private boolean isOpen=false;
    private boolean once=false;

    public interface SlidingButtonListener{
        void onMenuIsOpen(View view);
        void onDownOrMove(SlidingButtonView slidingButtonView);
    }

    public SlidingButtonView(Context context){
        this(context,null);
    }
    public SlidingButtonView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }
    public SlidingButtonView(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
        this.setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!once){
            mTextView_delete=findViewById(R.id.city_delete);
            once=true;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed){
            this.scrollTo(0,0);
            mScrollWidth=mTextView_delete.getWidth();

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action=ev.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                    slidingButtonListener.onDownOrMove(this);
                    break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                changeScrolx();
                return true;
                default:
                    break;

        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mTextView_delete.setTranslationX(l-mScrollWidth);
    }

    public  void changeScrolx(){
        if (getScrollX()>=(mScrollWidth/2)){
            this.smoothScrollTo(mScrollWidth,0);
            isOpen=true;
            slidingButtonListener.onMenuIsOpen(this);
        }else{
            this.smoothScrollTo(0,0);
            isOpen=false;
        }
    }
    public void closeMenu(){
        if (!isOpen){
            return;
        }
        this.smoothScrollTo(0,0);
        isOpen=false;
    }
    public void setSlidingButtonListener(SlidingButtonListener listener){
        slidingButtonListener=listener;
    }
}
