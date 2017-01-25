package com.badoo.meetingroom.presentation.presenter.impl;

import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.GetPersons;
import com.badoo.meetingroom.presentation.model.intf.PersonModel;
import com.badoo.meetingroom.presentation.presenter.intf.EventCreatorDialogPresenter;
import com.badoo.meetingroom.presentation.view.view.EventCreatorDialogView;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by zhangyaozhong on 25/01/2017.
 */

public class EventCreatorDialogPresenterImpl implements EventCreatorDialogPresenter {

    private EventCreatorDialogView mView;
    private String mEmailAddress;

    private final GetPersons mGetPersonsUseCase;

    @Inject
    EventCreatorDialogPresenterImpl(GetPersons getPersonsUseCase) {
        mGetPersonsUseCase = getPersonsUseCase;
    }

    @Override
    public void setView(EventCreatorDialogView view) {
        mView = view;
    }


    @Override
    public void getPerson(String emailAddress) {
        mEmailAddress = emailAddress;
        mGetPersonsUseCase.execute(new GetPersonsSubscriber());
    }


    private final class GetPersonsSubscriber extends DefaultSubscriber<List<PersonModel>> {

        @Override
        public void onStart() {
            super.onStart();
            mView.showLoadingData("");
        }

        @Override
        public void onNext(List<PersonModel> personModelList) {
            for (PersonModel personModel : personModelList) {
                if (mEmailAddress.equals(personModel.getEmailAddress())) {
                    mView.loadAvatar(personModel);
                    break;
                }
            }
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            mView.dismissLoadingData();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            try {
                throw e;
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }


    @Override
    public void Resume() {

    }

    @Override
    public void Pause() {

    }

    @Override
    public void destroy() {
        mGetPersonsUseCase.unSubscribe();
    }
}
