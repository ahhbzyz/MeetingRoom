//package com.badoo.meetingroom.domain.interactor;
//
//import com.badoo.meetingroom.domain.repository.RemoteImageRepo;
//import com.bumptech.glide.request.FutureTarget;
//
//import java.io.File;
//
//import javax.inject.Inject;
//
//import rx.Observable;
//
///**
// * Created by zhangyaozhong on 16/01/2017.
// */
//
//public class GetAvatar extends UseCase<FutureTarget<File>> {
//
//    public static final String NAME = "getAvatar";
//
//    private RemoteImageRepo mRemoteImageRepo;
//
//    private String mUrl;
//    private int mWidth;
//    private int mHeight;
//
//
//    @Inject
//    GetAvatar(RemoteImageRepo remoteImageRepo) {
//        mRemoteImageRepo = remoteImageRepo;
//    }
//
//    public GetAvatar init(String url, int width, int height) {
//        mUrl = url;
//        mWidth = width;
//        mHeight = height;
//        return this;
//    }
//
//    @Override
//    protected Observable<FutureTarget<File>>  buildUseCaseObservable() {
//        if (mUrl == null) {
//            throw new IllegalArgumentException("init(EventsGetParams) not called, or called with null argument");
//        }
//        return mRemoteImageRepo.getImage(mUrl, mWidth, mHeight);
//    }
//
//}
