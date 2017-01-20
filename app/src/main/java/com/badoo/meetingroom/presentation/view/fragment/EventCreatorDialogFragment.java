package com.badoo.meetingroom.presentation.view.fragment;


import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.domain.entity.intf.BadooPerson;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetAvatar;
import com.badoo.meetingroom.domain.interactor.GetPersons;
import com.badoo.meetingroom.presentation.mapper.BadooPersonModelMapper;
import com.badoo.meetingroom.presentation.model.BadooPersonModel;
import com.badoo.meetingroom.presentation.model.EventModel;
import com.badoo.meetingroom.presentation.view.view.RoomStatusView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by zhangyaozhong on 14/01/2017.
 */

public class EventCreatorDialogFragment extends ImmersiveDialogFragment {

    private RoomStatusView mRoomStatusView;
    private EventModel mEvent;


    @BindView(R.id.img_avatar) ImageView mAvatarImg;
    @BindView(R.id.pb_img_loading) ProgressBar mImgLoadingPb;
    @BindView(R.id.tv_event_period) TextView mEventPeriodTv;
    @BindView(R.id.tv_creator_email) TextView mCreatorEmailTv;
    @BindView(R.id.tv_creator_name) TextView mCreatorNameTv;


    @Inject @Named(GetPersons.NAME) GetPersons getPersonsUseCase;
    @Inject @Named(GetAvatar.NAME) GetAvatar getAvatarUseCase;
    @Inject BadooPersonModelMapper mMapper;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
    }

    public static EventCreatorDialogFragment newInstance() {
        return new EventCreatorDialogFragment();
    }


    public void setView(RoomStatusView roomStatusView) {
        mRoomStatusView = roomStatusView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = View.inflate(getActivity().getApplicationContext(),R.layout.dialog_event_creator, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyEventOrganizerDialog);
        builder.setView(view);
        ButterKnife.bind(this, view);
        Typeface stolzlRegularTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/stolzl_regular.otf");
        mEventPeriodTv.setTypeface(stolzlRegularTypeface);
        mCreatorNameTv.setTypeface(stolzlRegularTypeface);

        if (mEvent != null) {
            mEventPeriodTv.setText(mEvent.getDurationInText());

            if (mEvent.getCreatorEmailAddress() != null && mEvent.getCreatorName() != null) {
                mCreatorNameTv.setText(mEvent.getCreatorName());
                mCreatorEmailTv.setText(mEvent.getCreatorEmailAddress());
            }

            if (mEvent.isFastBooking()) {
                mCreatorNameTv.setText(getActivity().getString(R.string.fast_booking));
                mCreatorEmailTv.setText(getActivity().getString(R.string.no_name_available));
            }

            if (mEvent.getCreatorEmailAddress() != null) {
                getPersonsUseCase.execute(new GetPersonsSubscriber());
            } else {
                mAvatarImg.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_fast_booking));
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

    private final class GetPersonsSubscriber extends DefaultSubscriber<List<BadooPerson>> {

        @Override
        public void onStart() {
            super.onStart();
            mImgLoadingPb.setVisibility(View.VISIBLE);
        }

        @Override
        public void onNext(List<BadooPerson> badooPersonList) {
            List<BadooPersonModel> badooPersonModelList = mMapper.map(badooPersonList);
            for (BadooPersonModel badooPersonModel : badooPersonModelList) {
                if (mEvent.getCreatorEmailAddress().equals(badooPersonModel.getEmailAddress())) {
                    String mAvatarUrl = badooPersonModel.getAvatarUrl();
                    Glide.with(getActivity())
                        .load(mAvatarUrl).asBitmap().centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new BitmapImageViewTarget(mAvatarImg){
                            @Override
                            protected void setResource(Bitmap resource) {
                                super.setResource(resource);
                                RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                mAvatarImg.setImageDrawable(circularBitmapDrawable);
                            }
                        });
                    break;
                }
            }
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            mImgLoadingPb.setVisibility(View.GONE);
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            try {
                throw e;
            }
            catch (UserRecoverableAuthIOException userRecoverableAuthIOException) {
                mRoomStatusView.handleRecoverableAuthException(userRecoverableAuthIOException);
            }
            catch (GoogleJsonResponseException googleJsonResponseException) {
                mRoomStatusView.showError(googleJsonResponseException.getDetails().getMessage());
            }
            catch (Exception exception) {
                mRoomStatusView.showError(exception.getMessage());
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getPersonsUseCase.unSubscribe();
        getAvatarUseCase.unSubscribe();
    }
}
