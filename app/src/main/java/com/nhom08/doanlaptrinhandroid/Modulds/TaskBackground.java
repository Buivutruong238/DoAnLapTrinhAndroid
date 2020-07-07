package com.nhom08.doanlaptrinhandroid.Modulds;
import android.os.AsyncTask;

import com.nhom08.doanlaptrinhandroid.Interface_enum.KindStatusRunning;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;

public abstract class TaskBackground<T_Result> extends AsyncTask<String,Integer,T_Result> {
    private OnMyFinishListener<T_Result> onMyFinishListener;
    private Object bonusOfCoder;

    public void setBonusOfCoder(Object bonusOfCoder){
        this.bonusOfCoder = bonusOfCoder;
    }

    public TaskBackground(OnMyFinishListener<T_Result> onMyFinishListener){
        this.onMyFinishListener = onMyFinishListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected abstract T_Result doInBackground_Something(String... params);

    @Override
    protected T_Result doInBackground(String... strings) {
        try{
            return doInBackground_Something(strings);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(T_Result t_result) {
        super.onPostExecute(t_result);
        if (onMyFinishListener == null)
            return;
        if (t_result == null)
            onMyFinishListener.onError(new RuntimeException("Request Your Task Background Is Not Apply!"), bonusOfCoder != null ? bonusOfCoder : KindStatusRunning.DO_IN_BACKGROUND_NOT_RETURN_VALUE);
        else
            onMyFinishListener.onFinish(t_result);
    }
}