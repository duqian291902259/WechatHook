package site.duqian.wchook.android;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import site.duqian.wchook.R;
import site.duqian.wchook.base.BaseActivity;
import site.duqian.wchook.utils.CommandResult;
import site.duqian.wchook.utils.ShellUtils;
import site.duqian.wchook.utils.ThreadManager;
import site.duqian.wchook.utils.ToastUtil;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;

import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

public class WxJumpActivity extends BaseActivity {

    public static final String TAG = "dq";
    private boolean isShow; //悬浮窗口是否显示
    private boolean isOpen;
    private Context context;

    @BindView(R.id.showFloat)
    public Button mShowFloat;
    @BindView(R.id.et_time)
    public EditText etTime;

    @Override
    public int getContentViewId() {
        return R.layout.activity_wx_jump;
    }

    @Override
    public void initViewAndData() {
        context = this;
    }

    private boolean isTest = true;

    @OnClick(R.id.tv_test_root)
    public void testRoot() {
        isTest = true;
        jump(100);
    }

    @OnClick(R.id.showFloat)
    public void showFloatWindow() {
        if (isOpen) {
            mShowFloat.setText("打开悬浮窗");
            FloatWindow.destroy("logo");
        } else {
            mShowFloat.setText("关闭悬浮窗");
            showFloat();
        }
        isOpen = !isOpen;
    }

    /**
     * 显示悬浮窗，跳一跳的logo
     */
    private void showFloat() {
        final View view = View.inflate(this, R.layout.float_view, null);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.logo);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShow) {
                    Log.d(TAG, "onClick: 创建了");
                    FloatWindow
                            .with(getApplicationContext())
                            .setView(view)
                            .setWidth(Screen.width, 1f)
                            .setHeight(Screen.height, 0.5f)
                            .setX(100)
                            .setTag("window")
                            .setDesktopShow(true)
                            .setMoveType(MoveType.inactive)
                            .setY(Screen.height, 0.3f)
                            .build();
                    onResume();
                } else {
                    FloatWindow.get("window").hide();
                    FloatWindow.destroy("window");
                }
                isShow = !isShow;
            }
        });

        String trim = etTime.getText().toString().trim();
        double base = 2.0;
        if (!TextUtils.isEmpty(trim)){
            base = Double.parseDouble(trim);
        }
        double finalBase = base;

        showLogoFloat(imageView);
        view.setOnTouchListener(new View.OnTouchListener() {

            private float mStartY;
            private float mStartX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "start: " + event.getRawX() + " " + event.getRawY());
                        mStartX = event.getRawX();
                        mStartY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "end: " + event.getRawX() + " " + event.getRawY());
                        float endX = event.getRawX();
                        float endY = event.getRawY();
                        float length1 = Math.abs(endX - mStartX);
                        float length2 = Math.abs(endY - mStartY);
                        //通过勾股定理计算间距
                        int distance = (int) Math.sqrt(Math.pow(length1, 2) + Math.pow(length2, 2));
                        Log.d(TAG, "distance: " + distance);
                        int time = (int) (distance * finalBase);
                        //机型差异，自己尝试找到最佳时间 720*1280   2
                        isTest = false;
                        jump(time);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

    /**
     * 跳一跳按压屏幕
     *
     * @param time 时长
     */
    private void jump(final int time) {
        ThreadManager.getBackgroundPool().execute(new Runnable() {
            @Override
            public void run() {
                int a = getRandomNum(50);
                int b = getRandomNum(60);
                String move = "input touchscreen swipe 200 200 " + a + " " + b + " " + time;
                CommandResult commandResult = ShellUtils.execCommand(move, true);
                int result = commandResult.getResult();
                if (result != 0) {
                    ToastUtil.toast(context, "没有获取root权限！功能受限！");
                } else {
                    if (isTest) {
                        ToastUtil.toast(context, "已经拥有root权限，swiped " + time);
                        isTest = false;
                    }
                }
            }
        });
    }

    private Random random;

    private int getRandomNum(int range) {
        if (random == null) {
            random = new Random();
        }
        return random.nextInt(range) + 200;
    }

    /**
     * 显示logo悬浮图标
     *
     * @param view
     */
    private void showLogoFloat(View view) {
        FloatWindow
                .with(getApplicationContext())
                .setView(view)
                .setY(Screen.height, 0.1f)
                .setDesktopShow(true)
                .setTag("logo")
                .build();
        onResume();
    }
}
