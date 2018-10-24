package smartdevelopers.ir.hesabdari.samsungmobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by mostafa on 20/07/2018.
 */

public class UpdateApp extends AsyncTask<String,String,Void> {
    ProgressDialog progressDialog;
    int status=0;
    private Activity activity;

    public UpdateApp( Activity activity,ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            int count = 0;
            URL url=new URL(strings[0]);
            //Log.v("SSS",url.toString());
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //connection.setDoOutput(true);
            connection.connect();
            int lengthOfFile=connection.getContentLength();
            File location = activity.getExternalFilesDir(null);
            File temp=new File(location,"temp");
            if (!temp.exists()){
                temp.mkdir();
            }
            File outputFile=new File(temp,"temp.apk");
            if (outputFile.exists()){
                outputFile.delete();
            }
            FileOutputStream fos=new FileOutputStream(outputFile);
            InputStream is=connection.getInputStream();
            byte[] buffer=new byte[1024];
            int len=0;
            while ((len=is.read(buffer))!=-1){
                count+=len;
                publishProgress(""+(int)((count*100)/lengthOfFile));
                fos.write(buffer,0,len);
            }
            fos.flush();
            fos.close();
            is.close();
            APKLauncher launcher=new APKLauncher(activity,outputFile);
            launcher.lunchAPK();

        } catch (IOException e) {
            e.printStackTrace();
            status=1;
            Log.v("SSS","error in file outpout");
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        progressDialog.setProgress(Integer.valueOf(values[0]));

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressDialog.cancel();
        if (status==1){
//            Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show();
            activity.finish();
        }
    }
}
