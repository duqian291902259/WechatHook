package site.duqian.wchook.model;

/**
 * Created by duqian on 2017/5/12.
 */

public abstract class NonoCallBack {
    public abstract void onSuccess(String result);
    public void onFailure(Exception e){}
}
