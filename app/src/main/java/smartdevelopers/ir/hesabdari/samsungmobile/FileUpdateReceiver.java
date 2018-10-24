package smartdevelopers.ir.hesabdari.samsungmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import java.io.File;

/**
 * Created by mostafa on 20/07/2018.
 */

public class FileUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            String action=intent.getAction();
            String appName=context.getPackageName();
            Uri data=intent.getData();
            String updatedAppName=null;
            if (data!=null) {
                 updatedAppName = intent.getData().getEncodedSchemeSpecificPart();
            }
            if (action!=null && (action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_REPLACED))){
                if (updatedAppName!=null && updatedAppName.equals(appName)){
                    File external=context.getExternalFilesDir(null);
                    File temp=new File(external,"temp");
                    File app=new File(temp,"temp.apk");
//                    Log.v("SSS","app path ="+app.getPath());
                    if (app.exists()){
//                        Log.v("SSS","app exist");
                        app.delete();
                    }
                }

            }
//            Log.v("SSS","pakage instal action ="+action);
//
//            Log.v("SSS","package name ="+updatedAppName);



        }catch (Exception e){
                Log.v("SSS","EErrror");
        }
    }
}
