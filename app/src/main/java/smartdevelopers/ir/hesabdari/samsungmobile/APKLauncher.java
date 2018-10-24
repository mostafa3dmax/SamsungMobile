package smartdevelopers.ir.hesabdari.samsungmobile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;

import java.io.File;

import smartdevelopers.ir.hesabdari.samsungmobile.data.Const;

/**
 * Created by mostafa on 21/07/2018.
 */

public class APKLauncher {
    private Activity activity;
    private File tempFile;

    public APKLauncher(Activity activity, File tempFile) {
        this.activity = activity;
        this.tempFile = tempFile;
    }
    public void lunchAPK(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            Uri uri=FileProvider.getUriForFile(activity,BuildConfig.APPLICATION_ID+".provider",tempFile);
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivityForResult(intent, Const.UPDATE_REQUEST_CODE);
        }else {
            Uri uri=Uri.fromFile(tempFile);
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivityForResult(intent, Const.UPDATE_REQUEST_CODE);
        }
    }
}
