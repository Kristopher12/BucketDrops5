package io.designcoder.bucketdrops5.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.annotation.Nullable;

import io.designcoder.bucketdrops5.R;
import io.designcoder.bucketdrops5.adapters.AdapterDrops;

public class BucketPickerView extends LinearLayout implements View.OnTouchListener {
    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;
    private Calendar mCalendar;
    private TextView mTextDate;
    private TextView mTextMonth;
    private TextView mTextYear;
    private SimpleDateFormat mFormatter;
    private  boolean mIncrement;
    private boolean mDecrement;
    public static final int DELAY = 250;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mIncrement){
                increment(mActiveId);
            }

            if (mDecrement){
                decrement(mActiveId);
            }
            if (mIncrement || mDecrement){
                mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT,DELAY);
            }
            return true;
        }
    });
    private int MESSAGE_WHAT = 123;
    private int mActiveId;

    public BucketPickerView(Context context) {
        super(context);
        init(context);
    }

    public BucketPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BucketPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.bucket_picker_view, this);
        mCalendar = Calendar.getInstance();
        mFormatter = new SimpleDateFormat("MMM");
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("super",super.onSaveInstanceState());
        bundle.putInt("date",mCalendar.get(Calendar.DATE));
        bundle.putInt("month",mCalendar.get(Calendar.MONTH));
        bundle.putInt("year",mCalendar.get(Calendar.YEAR));
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Parcelable){
            Bundle bundle = (Bundle) state;
            state = bundle.getParcelable("super");
            int date = bundle.getInt("date");
            int month = bundle.getInt("month");
            int year = bundle.getInt("year");
            update(date,month,year,0,0,0);
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTextDate = this.findViewById(R.id.tv_date);
        mTextMonth = this.findViewById(R.id.tv_month);
        mTextYear = this.findViewById(R.id.tv_year);
        mTextDate.setOnTouchListener(this);
        mTextMonth.setOnTouchListener(this);
        mTextYear.setOnTouchListener(this);
        int date = mCalendar.get(Calendar.DATE);
        int month = mCalendar.get(Calendar.MONTH);
        int year = mCalendar.get(Calendar.YEAR);
        update(date, month, year, 0, 0, 0);
    }

    private void update(int date, int month, int year, int hour, int minute, int second) {
        mCalendar.set(Calendar.DATE, date);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.HOUR, hour);
        mCalendar.set(Calendar.MINUTE, minute);
        mCalendar.set(Calendar.SECOND, second);
        mTextYear.setText(year + "");
        mTextDate.setText(date + "");
        mTextMonth.setText(mFormatter.format(mCalendar.getTime()));
        AdapterDrops.setRalewayRegular(getContext(),mTextYear,mTextDate,mTextMonth);
    }

    public long getTime() {
        return mCalendar.getTimeInMillis();
    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.tv_date:
                processEventsFor(mTextDate, event);
                break;
            case R.id.tv_month:
                processEventsFor(mTextMonth, event);
                break;
            case R.id.tv_year:
                processEventsFor(mTextYear, event);
                break;
        }

        return true;
    }

    private void processEventsFor(TextView textView, MotionEvent event) {
        Drawable[] drawables = textView.getCompoundDrawables();
        if (hasDrawableTop(drawables) && hasDrawableBottom(drawables)) {
            Rect topBouns = drawables[TOP].getBounds();
            Rect bottomBouns = drawables[BOTTOM].getBounds();
            float x = event.getX();
            float y = event.getY();
            mActiveId = textView.getId();

            if (topDrawableHit(textView, topBouns.height(), x, y)) {
                  if (isActionDown(event)){
                      mIncrement = true;
                      increment(textView.getId());
                      mHandler.removeMessages(MESSAGE_WHAT);
                      mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT,DELAY);
                      toggleDrawable(textView,true);
                  }
                  if (isActionUpOrCancel(event)){
                           mIncrement = false;
                      toggleDrawable(textView,false);
                  }
            } else if (bottomDrawableHit(textView, bottomBouns.height(),x, y)) {
                if (isActionDown(event)){
                    mDecrement = true;
                    decrement(textView.getId());
                    mHandler.removeMessages(MESSAGE_WHAT);
                    mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT,DELAY);
                    toggleDrawable(textView,true);
                }
                if (isActionUpOrCancel(event)){
                    mDecrement = false;
                    toggleDrawable(textView,false);
                }
            } else {
                  mIncrement = false;
                  mDecrement = false;
                toggleDrawable(textView,false);
            }
        }
    }

    private void  increment(int id){
        switch (id) {
            case R.id.tv_date:
                mCalendar.add(Calendar.DATE,1);
                break;
            case R.id.tv_month:
                mCalendar.add(Calendar.MONTH,1);
                break;
            case R.id.tv_year:
                mCalendar.add(Calendar.YEAR,1);
                break;
        }
        set(mCalendar);
    }

    private void decrement(int id){
        switch (id) {
            case R.id.tv_date:
                mCalendar.add(Calendar.DATE,-1);
                break;
            case R.id.tv_month:
                mCalendar.add(Calendar.MONTH,-1);
                break;
            case R.id.tv_year:
                mCalendar.add(Calendar.YEAR,-1);
                break;
        }
        set(mCalendar);
    }


    private void set(Calendar calendar) {
        int date = calendar.get(Calendar.DATE);
        int year = calendar.get(Calendar.YEAR);
        mTextDate.setText(date + "");
        mTextYear.setText(year + "");
        mTextMonth.setText(mFormatter.format(mCalendar.getTime()));
    }

    private boolean isActionDown(MotionEvent event){
        return event.getAction() == MotionEvent.ACTION_DOWN;
    }


    private boolean isActionUpOrCancel(MotionEvent event){
        return event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL;
    }


    private boolean topDrawableHit(TextView textView, int drawableHeight, float x, float y) {
        int xmin = textView.getPaddingLeft();
        int xmax = textView.getWidth() - textView.getPaddingRight();
        int ymin = textView.getPaddingTop();
        int ymax = textView.getPaddingTop() + drawableHeight;
        return x > xmin && x < xmax && y > ymin && y < ymax;

    }

    private boolean bottomDrawableHit(TextView textView, int drawableHeight, float x, float y) {
        int xmin = textView.getPaddingLeft();
        int xmax = textView.getWidth() - textView.getPaddingRight();
        int ymax = textView.getHeight() - textView.getPaddingBottom();
        int ymin = ymax - drawableHeight;
        return x > xmin && x < xmax && y > ymin && y < ymax;
    }

    private boolean hasDrawableTop(Drawable[] drawables) {
        return drawables[TOP] != null;
    }

    private boolean hasDrawableBottom(Drawable[] drawables) {
        return drawables[BOTTOM] != null;
    }

    private void toggleDrawable(TextView textView, boolean pressed){
        if (pressed){
            if (mIncrement){
                   textView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.up_pressed,0,R.drawable.down_normal);
            }
            if (mDecrement){
                textView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.up_normal,0,R.drawable.down_pressed);
            }
        }
        else {
            textView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.up_normal,0,R.drawable.down_normal);
        }
    }

}
