package com.badoo.meetingroom.presentation.view.fragment;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.intf.EventModel;
import com.badoo.meetingroom.presentation.model.intf.PersonModel;
import com.badoo.meetingroom.presentation.presenter.intf.EventCreatorDialogPresenter;
import com.badoo.meetingroom.presentation.view.view.EventCreatorDialogView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by zhangyaozhong on 14/01/2017.
 */

public class EventCreatorDialogFragment extends ImmersiveDialogFragment implements EventCreatorDialogView{

    private EventModel mEvent;

    @BindView(R.id.img_avatar) ImageView mAvatarImg;
    @BindView(R.id.pb_img_loading) ProgressBar mImgLoadingPb;
    @BindView(R.id.tv_event_period) TextView mEventPeriodTv;
    @BindView(R.id.tv_creator_email) TextView mCreatorEmailTv;
    @BindView(R.id.tv_creator_name) TextView mCreatorNameTv;

    @Inject EventCreatorDialogPresenter mPresenter;

    @Inject @Named("stolzl_regular") Typeface mStolzlRegularTypeface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);

    }

    public static EventCreatorDialogFragment newInstance() {
        return new EventCreatorDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mPresenter.setView(this);


        View view = View.inflate(getActivity().getApplicationContext(),R.layout.dialog_event_creator, null);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyEventOrganizerDialog);
        builder.setView(view);
        ButterKnife.bind(this, view);
        mEventPeriodTv.setTypeface(mStolzlRegularTypeface);
        mCreatorNameTv.setTypeface(mStolzlRegularTypeface);

        if (mEvent != null) {

            mEventPeriodTv.setText(mEvent.getDurationInText());
            mCreatorNameTv.setText(mEvent.getCreatorName());
            mCreatorEmailTv.setText(mEvent.getCreatorEmailAddress());


            if (mEvent.isFastBooking()) {
                mCreatorNameTv.setText(getActivity().getString(R.string.fast_booking));
                mCreatorEmailTv.setText(getActivity().getString(R.string.no_name_available));
                mAvatarImg.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_fast_booking));

            }

            if (mEvent.getCreatorEmailAddress() != null) {
                mPresenter.getPerson(mEvent.getCreatorEmailAddress());
            }
        }

        Dialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_organizer);
        }


        return dialog;
    }

    public void setEvent(EventModel event) {
        if (event != null) {
            mEvent = event;
        }
    }

    @Override
    public void loadAvatar(PersonModel personModel) {
        Glide.with(getActivity())
            .load(personModel.getAvatarUrl()).asBitmap().centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(mAvatarImg);
        if (personModel.getDisplayName() != null) {
            mCreatorNameTv.setText(personModel.getDisplayName());
        }
    }

    @Override
    public void showLoadingData(String message) {
        mImgLoadingPb.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoadingData() {
        mImgLoadingPb.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {

    }

    @Override
    public Context context() {
        return getActivity().getApplicationContext();
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
