package com.yy.httpproxy.thirdparty;

import android.content.Context;

import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.yy.httpproxy.service.ConnectionService;
import com.yy.httpproxy.util.Log;
import com.yy.httpproxy.util.ServiceCheckUtil;

/**
 * Created by xuduo on 22/05/2017.
 */

public class UmengProvider implements NotificationProvider {

    public final static String TAG = "UmengProvider";
    private static String token;

    public UmengProvider(Context context) {
        Log.i(TAG, "UmengProvider init");
    }

    public static void register(Context context) {
        try {
            PushAgent mPushAgent = PushAgent.getInstance(context);
            mPushAgent.setDebugMode(false);
            String regId = mPushAgent.getRegistrationId();
            Log.i(TAG, "register " + mPushAgent.getRegistrationId());
            if (regId != null && !regId.isEmpty()) {
                token = regId;
            }
            //注册推送服务，每次调用register方法都会回调该接口
            mPushAgent.register(new IUmengRegisterCallback() {

                @Override
                public void onSuccess(String deviceToken) {
                    Log.i(TAG, "main process onSuccess deviceToken " + deviceToken);
                    ConnectionService.setToken(deviceToken);
                }

                @Override
                public void onFailure(String s, String s1) {
                    Log.e(TAG, "main process onFailure " + s + " " + s1);
                }
            });
            mPushAgent.setPushIntentServiceClass(UmengIntentService.class);
        } catch (Exception e) {
            //友盟sdk可能会崩溃
            Log.e(TAG, "register error", e);
        }

    }

    public static boolean available(Context context) {
        try {
            boolean available = ServiceCheckUtil.isIntentServiceAvailable(context, UmengIntentService.class) &&
                    Class.forName("com.umeng.message.PushAgent") != null
                    && Class.forName("com.umeng.message.UmengIntentService") != null;
            Log.d(TAG, "available " + available);
            return available;
        } catch (Throwable e) {
            Log.e(TAG, "available ", e);
            return false;
        }
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getType() {
        return "umeng";
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }
}
