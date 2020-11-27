package com.urdriver.urdriver.Common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.urdriver.urdriver.OTPActivity;
import com.urdriver.urdriver.R;
import com.urdriver.urdriver.model.Driver;
import com.urdriver.urdriver.model.DriverRequestModel;
import com.urdriver.urdriver.model.RequestData;
import com.urdriver.urdriver.model.RequestDataOneWay;
import com.urdriver.urdriver.model.RequestDataRoundWay;
import com.urdriver.urdriver.model.Trip;
import com.urdriver.urdriver.model.UserForAdminModel;
import com.urdriver.urdriver.model.cabDetails;
import com.urdriver.urdriver.retrofit.FCMClient;
import com.urdriver.urdriver.retrofit.IFCMService;
import com.urdriver.urdriver.retrofit.IURDriver;
import com.urdriver.urdriver.retrofit.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Common {

    public static String fromActivity, AuthPhone;
    public static String requestUserPhone;
    public static String way;
    public static String image;
    public static String act;
    public static Location mLastLocation = null;
    public static String BASE_URL = "http://myinvented.com/urdriver/";
    public static Driver currentDriver;
    public static Driver register;
    public static String name, phone, email;
    public static double Price;
    public static Trip trip;
    public static HorizontalInfiniteCycleViewPager CycleViewPager;
    public static String check;
    public static List<RequestData> requestList;
    public static ViewPager slideViewPage;
    public static TextView mDotLayout, TotalAmount;
    public static List<String> imageCab;
    public static List<String> imgCab;
    public static List<Trip> tripList;
    public static boolean isDec = false;
    public static LinearLayout terms;
    public static RequestDataOneWay requestDataOneWay;
    public static RequestDataRoundWay requestDataRoundWay;
    public static String baseActivity;
    public static DriverRequestModel driverRequestModel;
    public static UserForAdminModel userForAdminModel;
    public static cabDetails cabDetail;

    public static final int PICK_IMAGE_REQUEST = 71;

    private static final String FCM_API = "https://fcm.googleapis.com/";
    public static String id;

    public static IFCMService getGetFCMService() {
        return FCMClient.getClient(FCM_API).create(IFCMService.class);
    }

    public static void setBack(Activity context) {
        //make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            context.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void setTop(Activity context) {
        //make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            context.getWindow().setStatusBarColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static IURDriver getAPI() {
        return RetrofitClient.getClient(BASE_URL).create(IURDriver.class);
    }

    public static String verificationCode;
    public static PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    public static void sendOTP(Activity context, String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phone,   // Phone number to verify
                60,                           // Timeout duration
                TimeUnit.SECONDS,                // Unit of timeout
                context,              // Activity (for callback binding)
                mCallback);
    }

    public static void StartFirebaseLogin(final Context context) {
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//                Toast.makeText(context, "verification completed", Toast.LENGTH_SHORT).show();
//                SigninWithPhone(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                e.printStackTrace();
                Toast.makeText(context, "verification failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(context, "OTP sent ", Toast.LENGTH_SHORT).show();
                if (fromActivity.equals("reg"))
                    start_Activity(context);
                else if (fromActivity.equals("fog"))
                    start_OTPActivity(context);
                else if (fromActivity.equals("pro"))
                    start_Activity(context);
            }
        };
    }

    public static void start_Activity(Context context) {
        Intent intent = new Intent(context, OTPActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void start_OTPActivity(Context context) {
        Intent intent = new Intent(context, OTPActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


//    public static final String baseURL = "https://maps.googleapis.com";
//
//    public static IGoogleAPI getGoogleAPI() {
//        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
//    }


    //sendnig sms

//    public static final String ACCOUNT_SID = "AC77604927a74092a058b4a0cc73cfc3bd";
//    public static final String AUTH_TOKEN = "88d03b831fa871cdf752eecdb26cd247";
//
//    public static void sendMessage(String Message, String Phone) {
//        String body = Message;
//        String from = "+19143420044";
//        String to = "+19142788368";
//
//        String base64EncodedCredentials = "Basic " + Base64.encodeToString(
//                (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP
//        );
//
//        Map<String, String> smsData = new HashMap<>();
//        smsData.put("From", from);
//        smsData.put("To", to);
//        smsData.put("Body", body);
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://api.twilio.com/2010-04-01/")
//                .build();
//        TwilioApi api = retrofit.create(TwilioApi.class);
//
//        api.sendMessage(ACCOUNT_SID, base64EncodedCredentials, smsData).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) Log.d("TAG", response.message());
//                else Log.d("TAG", response.message());
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.d("TAG", "onFailure");
//            }
//        });
//    }

    public static int getCountOfDays(String createdDateString, String expireDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        java.util.Date createdConvertedDate = null, expireCovertedDate = null, todayWithZeroTime = null;
        try {
            createdConvertedDate = dateFormat.parse(createdDateString);
            expireCovertedDate = dateFormat.parse(expireDateString);

            Date today = new Date();

            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int cYear = 0, cMonth = 0, cDay = 0;

        if (createdConvertedDate.after(todayWithZeroTime)) {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(createdConvertedDate);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);

        } else {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(todayWithZeroTime);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);
        }

        Calendar eCal = Calendar.getInstance();
        eCal.setTime(expireCovertedDate);

        int eYear = eCal.get(Calendar.YEAR);
        int eMonth = eCal.get(Calendar.MONTH);
        int eDay = eCal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(cYear, cMonth, cDay);
        date2.clear();
        date2.set(eYear, eMonth, eDay);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);

        return ((int) dayCount + 1);
    }

}
