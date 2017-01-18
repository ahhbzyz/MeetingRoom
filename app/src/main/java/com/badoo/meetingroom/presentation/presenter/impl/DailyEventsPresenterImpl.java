package com.badoo.meetingroom.presentation.presenter.impl;

import com.badoo.meetingroom.data.remote.googlecalendarapi.CalendarApiParams;
import com.badoo.meetingroom.domain.interactor.DefaultSubscriber;
import com.badoo.meetingroom.domain.interactor.event.GetCalendarEvents;
import com.badoo.meetingroom.presentation.Badoo;
import com.badoo.meetingroom.presentation.model.EventModel;
import com.badoo.meetingroom.presentation.presenter.intf.DailyEventsPresenter;
import com.badoo.meetingroom.presentation.view.view.DailyEventsView;
import com.badoo.meetingroom.presentation.view.timeutils.TimeHelper;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by zhangyaozhong on 28/12/2016.
 */

public class DailyEventsPresenterImpl implements DailyEventsPresenter {


    private DailyEventsView mDailyEventsView;
    private final GetCalendarEvents mGetCalendarEventsUseCase;
    private List<EventModel> mEventList;
    private int mPage = 0;

    @Inject
    DailyEventsPresenterImpl(@Named(GetCalendarEvents.NAME)GetCalendarEvents getCalendarEventsUseCase) {
        mGetCalendarEventsUseCase = getCalendarEventsUseCase;
        mEventList = new ArrayList<>();
    }

    private void showDailyEventsInView(List<EventModel> roomEventModelList) {
        mDailyEventsView.renderDailyEvents(roomEventModelList);

    }

    @Override
    public void onSystemTimeUpdate() {
        mDailyEventsView.updateRecyclerView();
    }

    @Override
    public void setView(DailyEventsView dailyEventsView) {
        mDailyEventsView = dailyEventsView;
        mPage = mDailyEventsView.getCurrentPage();
    }

    @Override
    public void getEvents() {
        Event event = new Event();

        DateTime startDateTime = new DateTime(TimeHelper.getMidNightTimeOfDay(mPage));
        EventDateTime start = new EventDateTime()
            .setDateTime(startDateTime)
            .setTimeZone("Europe/London");
        event.setStart(start);

        DateTime endDateTime = new DateTime(TimeHelper.getMidNightTimeOfDay(mPage + 1));
        EventDateTime end = new EventDateTime()
            .setDateTime(endDateTime)
            .setTimeZone("Europe/London");
        event.setEnd(end);

        CalendarApiParams params = new CalendarApiParams(Badoo.getCurrentRoom().getId());
        params.setEventParams(event);
        mGetCalendarEventsUseCase.init(params, mPage).execute(new GetEventsSubscriber());
    }

    private final class GetEventsSubscriber extends DefaultSubscriber<List<EventModel>> {

        @Override
        public void onStart() {
            super.onStart();
            mDailyEventsView.showLoadingData("");
        }

        @Override
        public void onNext(List<EventModel> roomEvents) {
            mEventList = roomEvents;
            showDailyEventsInView(mEventList);
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            mDailyEventsView.dismissLoadingData();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            mDailyEventsView.dismissLoadingData();
            try {
                throw e;
            } catch (UserRecoverableAuthIOException e1) {
                mDailyEventsView.handlerUserRecoverableAuth(e1);
            } catch (GoogleJsonResponseException googleJsonResponseException) {
                mDailyEventsView.showError(googleJsonResponseException.getDetails().getMessage());
            } catch (Exception exception) {
                mDailyEventsView.showError(exception.getMessage());
            } catch (Throwable throwable) {
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
        mGetCalendarEventsUseCase.unSubscribe();
    }
}
