package io.designcoder.bucketdrops5.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import io.designcoder.bucketdrops5.AppBucketDrops;
import io.designcoder.bucketdrops5.R;
import io.designcoder.bucketdrops5.beans.Drop;
import io.realm.Realm;
import io.realm.RealmResults;

public class AdapterDrops extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SwipeListener {
    public static final int COUNT_FOOTER = 1;
    public static final int COUNT_NO_ITEMS = 1;
    public static final int NO_ITEM = 1;
    public static final int ITEM = 0;
    public static final int FOOTER = 2;
    private final ResetListener mResetListener;
    private Realm mRealm;
    private MarkListener mMarkListener;
    private int mFilterOption;
    private Context mContext;

    private static final String TAG = "Christ";
    private LayoutInflater mInflater;
    private AddListener mAddListener;
    private RealmResults<Drop> mResults;

    public AdapterDrops(Context context, Realm realm, RealmResults<Drop> results, AddListener listener, MarkListener markListener, ResetListener resetListener) {
        mContext = context;
        mAddListener = listener;
        mInflater = LayoutInflater.from(context);
        mRealm = realm;
        mMarkListener = markListener;
        mResetListener = resetListener;
        update(results);
    }

    public void setAddListener(AddListener listener) {
        mAddListener = listener;
    }

    public void update(RealmResults<Drop> results) {
        mResults = results;
        mFilterOption = AppBucketDrops.load(mContext);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (!mResults.isEmpty()) {
            if (position < mResults.size()) {
                return ITEM;
            } else {
                return FOOTER;
            }
        } else {
            if (mFilterOption == Filter.COMPLETE || mFilterOption == Filter.INCOMPLETE) {
                if (position == 0) {
                    return NO_ITEM;
                } else {
                    return FOOTER;
                }
            } else {
                return ITEM;
            }

        }
    }


//    public static ArrayList<String> generateValues() {
//        ArrayList<String> dummyValues = new ArrayList<>();
//        for (int i = 1; i < 101; i++) {
//            dummyValues.add("This is awesome [" + i+"]");
//        }
//        return dummyValues;
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == FOOTER) {
            View view = mInflater.inflate(R.layout.footer, viewGroup, false);
            return new FooterHolder(view);
        } else if (viewType == NO_ITEM) {
            View view = mInflater.inflate(R.layout.no_item, viewGroup, false);
            return new NoItemsHolder(view);
        } else {
            View view = mInflater.inflate(R.layout.row_drop, viewGroup, false);
            return new DropHolder(view, mMarkListener);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof DropHolder) {
            DropHolder dropHolder = (DropHolder) viewHolder;
            Drop drop = mResults.get(i);
            dropHolder.setWhat(drop.getWhat());
            dropHolder.setBackground(drop.isCompleted());
            dropHolder.setWhen(drop.getWhen());
        }
    }

    @Override
    public long getItemId(int position) {
        if (position < mResults.size()){
              return mResults.get(position).getAdded();
        }
        return RecyclerView.NO_ID;
    }

    @Override
    public int getItemCount() {
        if (!mResults.isEmpty()) {
            return mResults.size() + COUNT_FOOTER;
        } else {
            if (mFilterOption == Filter.LEAST_TIME_LEFT
                    || mFilterOption == Filter.MOST_TIME_LEFT
                    || mFilterOption == Filter.NONE) {
                return 0;
            } else {
                return COUNT_NO_ITEMS + COUNT_FOOTER;
            }
        }
    }

    @Override
    public void onSwipe(int position) {

            if (position < mResults.size()) {
                mRealm.beginTransaction();
                mResults.get(position).deleteFromRealm();
                mRealm.commitTransaction();
                notifyItemRemoved(position);
            }

  resetFilterEmpty();
    }

    private void resetFilterEmpty() {
        if (mResults.isEmpty() && (mFilterOption == Filter.COMPLETE || mFilterOption == Filter.INCOMPLETE)){
            mResetListener.onReset();
        }
    }

    public void markComplete(int position) {
        if (position < mResults.size()) {
            mRealm.beginTransaction();
            mResults.get(position).setCompleted(true);
            mRealm.commitTransaction();
            notifyItemChanged(position);
        }

    }

    public static class DropHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTextWhat;
        TextView mTextWhen;
        MarkListener mMarkListener;
        Context mContext;
        private View mItemView;

        public DropHolder(@NonNull View itemView, MarkListener listener) {
            super(itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
            mTextWhat = itemView.findViewById(R.id.tv_what);
            mTextWhen = itemView.findViewById(R.id.tv_when);
            mMarkListener = listener;
            mItemView = itemView;
            setRalewayRegular(mContext,mTextWhat,mTextWhen);

        }


        public void setWhat(String what) {
            mTextWhat.setText(what);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            mMarkListener.onMark(getAdapterPosition());
        }

        public void setBackground(boolean completed) {
            Drawable drawable;
            if (completed) {
                drawable = ContextCompat.getDrawable(mContext, R.color.bg_drop_complete);
            } else {
                drawable = ContextCompat.getDrawable(mContext, R.drawable.bg_row_drop);
            }

            if (Build.VERSION.SDK_INT > 15) {
                mItemView.setBackground(drawable);
            } else {
                mItemView.setBackgroundDrawable(drawable);
            }

        }

        public void setWhen(long when) {
            mTextWhen.setText(DateUtils.getRelativeTimeSpanString(when, System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL));
        }
    }


    public static class NoItemsHolder extends RecyclerView.ViewHolder {

        public NoItemsHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class FooterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button mBtnAdd;

        public FooterHolder(@NonNull View itemView) {
            super(itemView);
            mBtnAdd = itemView.findViewById(R.id.btn_footer);
            mBtnAdd.setOnClickListener(this);
            AdapterDrops.setRalewayRegular(itemView.getContext(),mBtnAdd);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            mAddListener.add();
        }
    }

    public static void setRalewayRegular(Context context,TextView textView){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/raleway_thin.ttf");
        textView.setTypeface(typeface);
    }


    public static void setRalewayRegular(Context context,TextView ...textViews){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/raleway_thin.ttf");

        for (TextView textView : textViews)
        textView.setTypeface(typeface);
    }
}
