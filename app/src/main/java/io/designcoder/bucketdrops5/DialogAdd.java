package io.designcoder.bucketdrops5;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import io.designcoder.bucketdrops5.adapters.AdapterDrops;
import io.designcoder.bucketdrops5.widgets.BucketPickerView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.designcoder.bucketdrops5.beans.Drop;

public class DialogAdd extends DialogFragment {

    private ImageButton mBtnClose;
    private EditText mInputWhat;
    private TextView mTitle;
    private BucketPickerView mInputWhen;
    private Button mBtnAdd;


    private  View.OnClickListener mBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    private  View.OnClickListener mBtnClickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
              addAction();
              dismiss();
        }
    };

    private void addAction() {
        //get the value of the 'goal' or 'to-do'
        //get the time when it was added
        String what= mInputWhat.getText().toString();

        long now = System.currentTimeMillis();
        Realm.init(getActivity());
        RealmConfiguration configuration =  new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(configuration);
        Realm realm = Realm.getDefaultInstance();
        final Drop drop = new Drop(what,now,mInputWhen.getTime(),false);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(drop);
            }
        });
        realm.close();
    }

    public DialogAdd() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mInputWhat = view.findViewById(R.id.et_drop);
        mBtnClose = view.findViewById(R.id.btn_close);
        mInputWhen = view.findViewById(R.id.bpv_date);
        mBtnAdd =  view.findViewById(R.id.btn_add_it);
        mTitle = view.findViewById(R.id.tv_title);

        mBtnClose.setOnClickListener(mBtnClickListener);
        mBtnAdd.setOnClickListener(mBtnClickListener2);

        AdapterDrops.setRalewayRegular(view.getContext(),mInputWhat,mBtnAdd,mTitle);
    }
}
