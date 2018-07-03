package ar.location.utility;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ar.location.app.ArLocationApplication;
import ng.dat.ar.R;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class Utility {

    private static Dialog popupWindow;
    public static final int SNACK_TIME = 2000;

    public static final String DOWNLOAD_APP_LINK = "https://play.google.com/store/apps/details?id=com.sahm";

    public static final String LEGAL_USER_URL = "http://sahm.sqtdev.us/legal-mobile/";
    public static final String ABOUT_US_USER_URL = "http://sahm.sqtdev.us/about-us-mobile/";
    public static final String JOIN_OUR_TEAM_USER_URL = "http://sahm.sqtdev.us/join-our-team-mobile/";
    public static final String OUR_PARTNER_USER_URL = "http://sahm.sqtdev.us/our-partners-mobile";
    public static final String TERMS_AND_CONDITION = "http://sahm.sqtdev.us/terms-and-condition-mobile/";
    public static final String MOBILE_FAQ = "http://sahm.sqtdev.us/faq-mobile/";
    public static final java.text.SimpleDateFormat simpleDateFormatddMMMyyhhmmaa = new SimpleDateFormat("dd MMM, hh:mm aa", Locale.getDefault());

    public static final String LEGAL_DRIVER_URL = "http://sahm.sqtdev.us/terms-of-services";
    public static final String TERMS_AND_CONDITION_USER_URL = "http://sahm.sqtdev.us/terms-of-services";
    public static final String TERMS_AND_CONDITION_DRIVER_URL = "http://sahm.sqtdev.us/terms-of-services";
    public static final String FAQ_HELP_USER_URL = "http://sahm.sqtdev.us/terms-of-services";
    public static final String FAQ_HELP_DRIVER_URL = "http://sahm.sqtdev.us/terms-of-services";
    public static final String JOIN_OUR_TEAM_DRIVER_URL = "http://sahm.sqtdev.us/terms-of-services";
    public static final String ABOUT_US_DRIVER_URL = "http://sahm.sqtdev.us/terms-of-services";

    public static final String DRIVER_NOTIFICATION_BROADCAST = "com.sahm.gotnotification";
    public static final String CLIENT_NOTIFICATION_BROADCAST = "com.sahm.gotnotificationUser";

    public static final String POPUP_DIALOG_CLICK_TYPE_POSITIVE = "CLICK_TYPE_POSITIVE";
    public static final String POPUP_DIALOG_CLICK_TYPE_NEGATIVE = "CLICK_TYPE_NEGATIVE";

    private static final String TAG = "Sahm";
    public static final java.text.DateFormat dateFormatMMMMdd = new SimpleDateFormat("MMMM dd", Locale.getDefault());
    public static final java.text.DateFormat dateFormatddMMMyyhhmmaa = new SimpleDateFormat("dd MMM''yy | hh:mm aa", Locale.getDefault());
    public static final java.text.DateFormat DATEFORMATddMMMyyy = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    public static final java.text.DateFormat DATEFORMATddMMyyyy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    public static final java.text.DateFormat dateFormatddMMyyyy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    public static final java.text.DateFormat dateFormathhmma = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    public static final DecimalFormat DIGIT_FORMAT_TWO_DECIMAL = new DecimalFormat(".##");
    public static final String TWO_DECIMAL_FORMAT = "%.2f";
    public static final String FOUR_DECIMAL_FORMAT = "%.4f";

    public static final String DATE_FORMAT_NORMAL = "dd-MM-yyyy";
    public static final String DATE_FORMAT_YY_NORMAL = "yyyy-MM-dd";

    public static final java.text.SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    public static final java.text.SimpleDateFormat DATE_FORMAT_YY = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static final java.text.SimpleDateFormat DATE_FORMAT_YYYY = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
    public static final java.text.SimpleDateFormat DATE_FORMAT_MMM = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
    public static final java.text.SimpleDateFormat DATE_FORMAT_YY_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static void showToast(String message, Context context) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, pxToDp(context, 70));
        toast.show();
    }

    public static int dpToPx(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int pxToDp(Context context, float px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getFileName(String s) {
        int start = s.lastIndexOf("/");
        return s.substring(start + 1, s.length());
    }

    public static String getFileExtension(String s) {
        int start = s.lastIndexOf(".");
        return s.substring(start + 1, s.length());
    }

    public static String dateFormate(String oldDateString, String NEW_FORMAT, String OLD_FORMAT) {

        String newDateString = "";

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d = null;
        try {
            d = sdf.parse(oldDateString);

            sdf.applyPattern(NEW_FORMAT);
            newDateString = sdf.format(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return newDateString;

    }

    public static String getAppStoragePath(Context context, String dir) {
        String path;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            path = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name);
        } else {
            path = context.getFilesDir() + "/" + context.getString(R.string.app_name);
        }

        String directory = +dir.trim().length() > 0 ? (dir) : "";
        File file = new File(path + directory);
        if (!file.exists()) file.mkdirs();

        return file.getAbsolutePath();
    }

    public static File getAppStorageFile(Context context, String dir) {
        String path;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            path = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name);
        } else {
            path = context.getFilesDir() + "/" + context.getString(R.string.app_name);
        }
        String directory = +dir.trim().length() > 0 ? (dir) : "";
        File file = new File(path + directory);
        if (!file.exists()) file.mkdirs();
        return file;
    }

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String convertTimeStampToDate(Long timeStamp, String format) {
        String date = "";
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeStamp * 1000);
        date = DateFormat.format(format, cal).toString();
        return date;
    }


    public static TimeZone getTimeZone() {
        TimeZone tz = TimeZone.getTimeZone("GMT");
        return tz;
    }

    public static RequestBody imageToBody(String text) {
        RequestBody requestBody;
        if (text != null && text.length() > 0) {
            MediaType MEDIA_TYPE = MediaType.parse("image/*");
            File file = new File(text);
            requestBody = RequestBody.create(MEDIA_TYPE, file);
        } else {
            requestBody = null;
        }
        return requestBody;
    }

    public static void showProgress(final Context context) {
        try {
            if (!((Activity) context).isFinishing()) {

                View layout = LayoutInflater.from(context).inflate(R.layout.layout_popup_loading, null);
                popupWindow = new Dialog(context, R.style.ProgressDialog);
                popupWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popupWindow.setContentView(layout);
                popupWindow.setCancelable(false);

                if (!((Activity) context).isFinishing()) {
                    popupWindow.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideProgress() {
        try {
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public static int getScreenResolutionWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        return screenWidth;
    }

    public static int getScreenResolutionHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        return screenHeight;
    }

    public static String getDate(long milliSeconds, SimpleDateFormat dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = dateFormat;
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds * 1000L);
        return formatter.format(calendar.getTime());
    }

    public static String getDate(long startTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime * 1000L);
        return DateFormat.format("dd/MM/yyyy", cal).toString();
    }

    public static String getOpenDate(long startTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime * 1000L);
        return DateFormat.format("dd/MM/yyyy", cal).toString();
    }

    public static String generateFileName(String s) {
        String filename = "";
        Calendar c = Calendar.getInstance();
        filename = s + "_" + (c.getTimeInMillis() + "");
        return filename;
    }

    public static void hideSoftKeyboard(Activity mActivity) {
        InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
    }

    public static ProgressDialog initProgress(Object mActivity) {
        ProgressDialog mpDialog = null;
        if (mActivity instanceof Activity) {
            mpDialog = new ProgressDialog((Activity) mActivity);
        } else if (mActivity instanceof Context) {
            mpDialog = new ProgressDialog((Context) mActivity);
        }
        mpDialog.setTitle("Please wait...");
        mpDialog.setMessage("Loading...");
        mpDialog.setCanceledOnTouchOutside(false);
        // Set the progress dialog background color
//        if (checkIsLollipopOrHigher()) {
//            mpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
//        } else {
//            mpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
//        }
        return mpDialog;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) ArLocationApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void dialogShow(Activity mActivity, final ProgressDialog mpDialog) {
        if (mpDialog != null && !mpDialog.isShowing()) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mpDialog.show();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void dialogDismiss(Activity mActivity, final ProgressDialog mpDialog) {
        if (mpDialog != null && mpDialog.isShowing()) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mpDialog.dismiss();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void log(String message) {
        if (message != null) {
            Log.e(TAG, message);
        } else {
            Log.e("Message", "null");
        }
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null)
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void hideKeyBoardFromView(Activity mActivity) {
        InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = ((Activity) mActivity).getCurrentFocus();
        if (view == null) {
            view = new View(mActivity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showToast(final String message) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(ArLocationApplication.getInstance(), message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, pxToDp(ArLocationApplication.getInstance(), 70));
                toast.show();
            }
        });
    }

    public static void showError(final String message) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(ArLocationApplication.getInstance(), message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, pxToDp(ArLocationApplication.getInstance(), 70));
                toast.show();
            }
        });
    }

    public static Date getCurrentDate() {
        Date currentDate = null;
        try {
            currentDate = Utility.DATEFORMATddMMMyyy.parse(Utility.dateFormatMMMMdd.format(Calendar.getInstance().getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Log.e("UTILS currentDate", "" + currentDate);
        return currentDate;
    }

    public static int ColorDeprecated(Context mContext, int color) {
        if (Build.VERSION.SDK_INT >= 23) {
            return mContext.getColor(color);
        } else {
            return ((Activity) mContext).getResources().getColor(color);
        }
    }

    public static Drawable DrawableDeprecated(Context mContext, int color) {
        if (Build.VERSION.SDK_INT >= 23) {
            return mContext.getDrawable(color);
        } else {
            return ((Activity) mContext).getResources().getDrawable(color);
        }
    }

    private static String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "<sup>th</sup>";
        }
        switch (day % 10) {
            case 1:
                return "<sup>st</sup>";
            case 2:
                return "<sup>nd</sup>";
            case 3:
                return "<sup>rd</sup>";
            default:
                return "<sup>th</sup>";
        }
    }

    public static RecyclerView.LayoutManager getLayoutManager(Activity mActivity) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(true);
        return layoutManager;
    }

    public static RecyclerView.LayoutManager getLayoutManagerHorizontal(Activity mActivity) {
        return new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
    }

    public static void clearAllNotification() {
        NotificationManager notificationManager = (NotificationManager) ArLocationApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static final String SHARE_APP = "https://play.google.com/store/apps/details?id=" + ArLocationApplication.getInstance().getPackageName();

    public static void shareApp(Activity mActivity, String shareText) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "" + shareText);
        mActivity.startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    public static void generateHashkey(Activity mActivity) {
        try {
            PackageInfo info = mActivity.getPackageManager().getPackageInfo(
                    mActivity.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                log("Key Hash: " + Base64.encodeToString(md.digest(),
                        Base64.NO_WRAP));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("Name not found", e.getMessage(), e);

        } catch (NoSuchAlgorithmException e) {
            Log.d("Error", e.getMessage(), e);
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        Bitmap newBitmap;
        if (realImage.getWidth() > maxImageSize || realImage.getHeight() > maxImageSize) {
            float ratio = Math.min(maxImageSize / realImage.getWidth(), maxImageSize / realImage.getHeight());
            int width = Math.round(ratio * realImage.getWidth());
            int height = Math.round(ratio * realImage.getHeight());
            newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
        } else {
            newBitmap = Bitmap.createScaledBitmap(realImage, realImage.getWidth(), realImage.getHeight(), filter);
        }
        return newBitmap;
    }

    public static void shareContent(Activity mActivity, int requestCode) {
        Intent intentShare = new Intent();
        intentShare.setAction(Intent.ACTION_SEND);
        intentShare.setType("text/plain");
        String shareContact = "Hi, i have found this article on Ambit App , for visit this article follow this below link www.ambit.com" /*+ Utility.DOWNLOAD_APP_LINK*/;
        intentShare.putExtra(Intent.EXTRA_TEXT, shareContact);
        mActivity.startActivityForResult(Intent.createChooser(intentShare, "Select"), requestCode);
    }

    public static void shareContent(Activity mActivity) {
        Intent intentShare = new Intent();
        intentShare.setAction(Intent.ACTION_SEND);
        intentShare.setType("text/plain");
        String shareContact = "Hi, i have found this article on Ambit App , for visit this article follow this below link www.ambit.com" /*+ Utility.DOWNLOAD_APP_LINK*/;
        intentShare.putExtra(Intent.EXTRA_TEXT, shareContact);
        mActivity.startActivity(Intent.createChooser(intentShare, "Select"));
    }

    public static void openAppInPlayStore(Activity mActivity) {
        final String appPackageName = mActivity.getPackageName();
        try {
            mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static boolean isNull(EditText edtText) {
        if (edtText != null && !edtText.getText().toString().equalsIgnoreCase("")) {
            return false;
        }
        return true;
    }

    public static boolean isNullTXT(TextView edtText) {
        if (edtText != null && !edtText.getText().toString().equalsIgnoreCase("")) {
            return false;
        }
        return true;
    }

    public static String getRealPathFromURI(String contentURI, Context mContext) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = mContext.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public static boolean isValidNumber(String number) {
        if (number != null && number.length() >= 6 && number.length() <= 16) {
            return true;
        }
        return false;
    }

    public static boolean checkNullString(String text) {
        if (text != null && !text.trim().equalsIgnoreCase("") && !text.trim().equalsIgnoreCase("0")) {
            return false;
        } else {
            return true;

        }
    }

    public static String checkNullStringValue(String text) {
        if (text != null && !text.trim().equalsIgnoreCase("") && !text.trim().equalsIgnoreCase("0")) {
            return text;
        } else {
            return "";

        }
    }

    private static String MAPS_APIKEY;

    public static String getLogTAG(Class klass) {
        return klass.getName();
    }


    /**
     * return true if string is null or empty.
     *
     * @param string
     * @return
     */
    public static boolean isStringEmpty(String string) {
        return string == null || string.trim().length() == 0;
    }


    /**
     * return true if list is null or empty.
     *
     * @param list
     * @return
     */
    public static boolean isListEmpty(List list) {
        return list == null || list.size() == 0;
    }


    /**
     * check location is enable or not
     *
     * @param context
     * @return
     */
    public static boolean isLocationServiceEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Log.d(TAG, "unable to check GPS enabled " + ex.getMessage());
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            Log.d(TAG, "unable to check networkEnabled enabled " + ex.getMessage());
        }

        return gpsEnabled && networkEnabled;
    }


    /**
     * Displays a simple alert dialog.
     */
    public static void showAlert(Context context, String head, String msg,
                                 String postiveBtnName,
                                 DialogInterface.OnClickListener positiveBtnListner,
                                 String negativeBtnName,
                                 DialogInterface.OnClickListener negativeBtnListner,
                                 boolean... cancelable) {
        AlertDialog d;
        boolean canBeClosed = (cancelable == null || cancelable.length == 0 || cancelable[0]);

        if (negativeBtnListner == null) {
            d = new AlertDialog.Builder(context).setMessage(msg)
                    .setTitle(head)
                    .setPositiveButton(postiveBtnName, positiveBtnListner)
                    .setCancelable(canBeClosed)
                    .create();
        } else {
            d = new AlertDialog.Builder(context).setMessage(msg)
                    .setTitle(head)
                    .setPositiveButton(postiveBtnName, positiveBtnListner)
                    .setNegativeButton(negativeBtnName, negativeBtnListner)
                    .setCancelable(canBeClosed)
                    .create();
        }
        d.show();
    }

    public static long getLocalToUtcDelta() {
        Calendar local = Calendar.getInstance();
        local.clear();
        local.set(1970, Calendar.JANUARY, 1, 0, 0, 0);
        return local.getTimeInMillis() / 1000;
    }

    public static long converLocalTimeToUtcTime(long timeSinceLocalEpoch) {
        return timeSinceLocalEpoch + getLocalToUtcDelta();
    }

    public static long getDateCurrentTimeZone(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currenTimeZone = (Date) calendar.getTime();
            long timestampreturn = currenTimeZone.getTime();
            return timestampreturn;
        } catch (Exception e) {
        }
        return 0;
    }

}
