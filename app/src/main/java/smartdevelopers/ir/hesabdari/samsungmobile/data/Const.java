package smartdevelopers.ir.hesabdari.samsungmobile.data;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Samsung Mobile on 07/07/2018.
 */

public class Const {
    public final static String EXTERA_INTENT_NAME ="EXTERA_INTENT_NAME";
    public final static String EXTERA_INTENT_USERNAME ="extera_intent_username";
    public final static String EXTERA_INTENT_MODEL ="extera_intent_model";
    public static final String[] MOSTAFA_JOBS={"نصب نرم افزار","فلش","خدمات","کامپیوتری"};
    public static final String[] HOSSEIN_JOBS={"تعمیرات","باتری"};
    public static final String[] MEHDI_JOBS={"جانبی","گوشی","خرج کرد"};
    public final static String MOSTAFA ="mostafa";
    public final static String HOSSEIN ="hossein";
    public final static String MEHDI ="mehdi";
    public final static String dbName="samsungmobiledb";
    public final static String UPDATE="update";
    public final static String UPDATE_Test="update-test";
    public static final String[] USERS=new String[]{"mostafa","hossein","mehdi"};
    public final static String APP_VERSION="app_version";
    public final static String APP_URL="app_url";
    public final static String APP_NEW_VERSION_TIPS="app_new_version_tips";
    public final static String PASSWORD="passwords";
    public static final String API_KEY="74bca798aa39ffe2b2858149edb9acd6b99486f882c459ed2c642e351699a384";
    public static final String SECRET_KEY="a5db5f02d216a393db0c2cc290a46ff8e43cff0bcb0628630da2cf6db08b6ad7";


    public static int UPDATE_REQUEST_CODE =5462;
    public static void savePrefs(Activity activity,String prefsName,String value){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString(prefsName,value);
        editor.apply();
    }
    public static String getPrefs(Activity activity,String prefsName,String defualt){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(activity);
        return preferences.getString(prefsName,defualt);

    }
    public static boolean getPrefs(Activity activity,String prefsName,boolean defualt){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(activity);
        return preferences.getBoolean(prefsName,defualt);

    }
}
