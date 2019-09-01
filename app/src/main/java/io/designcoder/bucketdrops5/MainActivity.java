package io.designcoder.bucketdrops5;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import io.designcoder.bucketdrops5.adapters.AdapterDrops;
import io.designcoder.bucketdrops5.adapters.AddListener;
import io.designcoder.bucketdrops5.adapters.CompleteListener;
import io.designcoder.bucketdrops5.adapters.Divider;
import io.designcoder.bucketdrops5.adapters.Filter;
import io.designcoder.bucketdrops5.adapters.MarkListener;
import io.designcoder.bucketdrops5.adapters.ResetListener;
import io.designcoder.bucketdrops5.adapters.SimpleTouchCallback;
import io.designcoder.bucketdrops5.beans.Drop;
import io.designcoder.bucketdrops5.extras.Util;
import io.designcoder.bucketdrops5.services.NotificationsService;
import io.designcoder.bucketdrops5.widgets.BucketRecyclerView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Christ";
    BucketRecyclerView mRecycler;
    Toolbar mToolbar;
    Button mBtnAdd;
    RealmResults<Drop> results;
    View mEmptyView;
    AdapterDrops mAdapter;

    private AddListener mAddListener = new AddListener() {
        @Override
        public void add() {
            showDialogAdd();
        }
    };

    private RealmChangeListener mChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object o) {
            Log.d(TAG, "onChange: was called");
            mAdapter.update(results);

        }
    };

  private ResetListener mResetListener = new ResetListener() {
      @Override
      public void onReset() {
AppBucketDrops.save(MainActivity.this,Filter.NONE);
 loadResults(Filter.NONE);
      }
  };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get the configuration
        Realm.init(getApplicationContext());
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();

        //set the real configuration
        Realm.setDefaultConfiguration(configuration);
        //get an instance of realm
        Realm realm = Realm.getDefaultInstance();

        int filterOption = AppBucketDrops.load(this);
        loadResults(filterOption);
        mRecycler = findViewById(R.id.rv_drops);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(manager);
        mAdapter = new AdapterDrops(this, realm, results, mAddListener, mMarkListener,mResetListener);
        mAdapter.setAddListener(mAddListener);
        mAdapter.setHasStableIds(true);
        mRecycler.setAdapter(mAdapter);
        mToolbar = findViewById(R.id.toolbar);
        mBtnAdd = findViewById(R.id.btn_add);
        mEmptyView = findViewById(R.id.empty_drops);
        mRecycler.addItemDecoration(new Divider(this, LinearLayoutManager.VERTICAL));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.hideIfEmpty(mToolbar);
        mRecycler.showIfEmpty(mEmptyView);
        SimpleTouchCallback callback = new SimpleTouchCallback(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecycler);

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAdd();
            }

        });
        AdapterDrops.setRalewayRegular(this,mBtnAdd);
        setSupportActionBar(mToolbar);
        initBackgroundImage();
        Util.scheduleAlarm(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = true;
        int filterOption = Filter.NONE;

        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                showDialogAdd();
                break;
            case R.id.action_none:
                filterOption = Filter.NONE;
                break;
            case R.id.action_sort_ascending_date:
                filterOption = Filter.LEAST_TIME_LEFT;
                break;
            case R.id.action_sort_descending_date:
                filterOption=Filter.MOST_TIME_LEFT;
                break;

            case R.id.action_show_complete:
                filterOption=Filter.COMPLETE;
                break;

            case R.id.action_show_incomplete:
                filterOption = Filter.INCOMPLETE;
                break;

            default:
                handled = false;
                break;

        }
        AppBucketDrops.save(this,filterOption);
        loadResults(filterOption);
        return handled;
    }

    private void loadResults(int filterOption) {
        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();
        switch (filterOption) {
            case Filter.NONE:
                results = realm.where(Drop.class).findAllAsync();
                break;
            case Filter.LEAST_TIME_LEFT:
                results = realm.where(Drop.class).sort("when").findAllAsync();
                break;
            case Filter.MOST_TIME_LEFT:
                results = realm.where(Drop.class).sort("when", Sort.DESCENDING).findAllAsync();
                break;
            case Filter.COMPLETE:
                results = realm.where(Drop.class).equalTo("completed", true).findAllAsync();
                break;
            case Filter.INCOMPLETE:
                results = realm.where(Drop.class).equalTo("completed", false).findAllAsync();
                break;
        }
        results.addChangeListener(mChangeListener);
    }



    private MarkListener mMarkListener = new MarkListener() {
        @Override
        public void onMark(int position) {
            showDialogMark(position);
        }
    };

    private CompleteListener mCompleteListener = new CompleteListener() {
        @Override
        public void onComplete(int position) {
//                 Toast.makeText(MainActivity.this,"position in activity "+position,Toast.LENGTH_LONG).show();
            mAdapter.markComplete(position);

        }
    };

    private void showDialogAdd() {
        DialogAdd dialog = new DialogAdd();
        dialog.show(getSupportFragmentManager(), "Add");
    }

    private void showDialogMark(int position) {
        DialogMark dialog = new DialogMark();
        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", position);
        dialog.setCompleteListener(mCompleteListener);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "Mark");


    }

    private void initBackgroundImage() {
        ImageView background = findViewById(R.id.iv_background);
        Glide.with(this)
                .load(R.drawable.background)
                .centerCrop()
                .into(background);
    }

    @Override
    protected void onStart() {
        super.onStart();
        results.addChangeListener(mChangeListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        results.removeChangeListener(mChangeListener);
    }

//    public void getRealmData(View view) {
//        Log.i( " log ", " inside getRealmData " );
//
//        //get the configuration
//        Realm.init(getApplicationContext());
//        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
//
//        //set the real configuration
//        Realm.setDefaultConfiguration(configuration);
//
//        //get an instance of realm
//        Realm realm = Realm.getDefaultInstance();
//
//        RealmResults<Drop> allDrops = realm.where(Drop.class).findAll();
//
//        for(Drop drop: allDrops){
//
//            Log.i( " log ", " what is it: "  + drop.getWhat());
//            Log.i("log","when : " + drop.getWhen());
//            Log.i( " log ", " added : "  + drop.getAdded());
//            Log.i( " log ", " Completed : "  + drop.isCompleted());
//
//        }
    //  }
}
