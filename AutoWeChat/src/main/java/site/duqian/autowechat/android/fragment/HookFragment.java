package site.duqian.autowechat.android.fragment;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.easy.wtool.sdk.MessageEvent;
import com.easy.wtool.sdk.OnMessageListener;
import com.easy.wtool.sdk.WToolSDK;
import site.duqian.autowechat.R;
import site.duqian.autowechat.android.SubActivity;
import site.duqian.autowechat.android.base.BaseFragment;
import site.duqian.autowechat.model.Config;
import site.duqian.autowechat.model.Constant;
import site.duqian.autowechat.model.robot.ReplyBean;
import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.utils.SPUtils;
import site.duqian.autowechat.utils.ToastUtils;
import site.duqian.autowechat.wechat.WechatHookUtil;
import site.duqian.autowechat.xposed.hook.HookUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;

import static site.duqian.autowechat.utils.FileUitls.makeVideoThumbFile;
import static site.duqian.autowechat.wechat.WechatHookUtil.parseResult;

/**
 * duqian3201@163.com
 * 本界面测试使用，可以无视。依赖于xposed第三方module：微控工具模块
 */
public class HookFragment extends BaseFragment {

    private SubActivity activity;

    private static final String TAG = HookFragment.class.getSimpleName();
    private static String DEF_TALKER = "接收人(点击选择)";
    private static String DEF_IMAGEFILE = "图片(点击选择)";
    private static String DEF_VOICEFILE = "语音(点击选择)";
    private static String DEF_VIDEOFILE = "视频(点击选择)";
    private static int RESULT_IMAGE = 1;
    private static int RESULT_VOICE = 2;
    private static int RESULT_VIDEO = 3;
    private String toWxId = "";
    public static String toImageFile = "";
    private String toVoiceFile = "";
    private String toVideoFile = "";
    private int selectedWxIdIndex = 0;
    
    TextView labelImageFile, labelVoiceFile, labelVideoFile;
    private WToolSDK wToolSDK;
    private Button buttonInit;
    private Button buttonText;
    private Button buttonImage;
    private Button buttonVoice;
    private Button buttonVideo;
    private Button buttonFriends;
    private Button buttonChatrooms;
    private RadioButton radioButtonFriend;
    private RadioButton radioButtonChatroom;
    private TextView labelWxid;
    private Button buttonStartMessage;
    private EditText editText;
    private TextView editContent;
    @BindView(R.id.ckb_xposed)
    CheckBox ckb_xposed;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_xp_wechat;
    }

    @Override
    protected void initView(View view) {
        buttonInit = (Button) view.findViewById(R.id.buttonInit);
        buttonText = (Button) view.findViewById(R.id.buttonText);
        buttonImage = (Button) view.findViewById(R.id.buttonImage);
        buttonVoice = (Button) view.findViewById(R.id.buttonVoice);
        buttonVideo = (Button) view.findViewById(R.id.buttonVideo);
        buttonFriends = (Button) view.findViewById(R.id.buttonFriends);
        buttonChatrooms = (Button) view.findViewById(R.id.buttonChatrooms);
        radioButtonFriend = (RadioButton) view.findViewById(R.id.radioButtonFriend);
        radioButtonChatroom = (RadioButton) view.findViewById(R.id.radioButtonChatroom);
        labelWxid = (TextView) view.findViewById(R.id.labelWxid);
        labelImageFile = (TextView) view.findViewById(R.id.labelImageFile);
        labelVoiceFile = (TextView) view.findViewById(R.id.labelVoiceFile);
        labelVideoFile = (TextView) view.findViewById(R.id.labelVideoFile);
        buttonStartMessage = (Button) view.findViewById(R.id.buttonStartMessage);
        editText = (EditText) view.findViewById(R.id.editText);
        editContent = (TextView) view.findViewById(R.id.editContent);

        buttonStartMessage.setVisibility(View.VISIBLE);
        labelWxid.setText(DEF_TALKER);
        labelVideoFile.setText(DEF_VIDEOFILE);
        labelImageFile.setText(DEF_IMAGEFILE);
        labelVoiceFile.setText(DEF_VOICEFILE);
        editContent.setText("本界面仅测试用,依赖于xposed第三方module：微控工具模块");
    }

    @Override
    protected void initViewAndData() {
        activity = (SubActivity) getActivity();
        activity.setTitle(R.string.page_title_xposed);
        initCheckBox();
        wToolSDK = WechatHookUtil.getToolSDK();
        LogUtils.debug(TAG,"init="+ WechatHookUtil.isInitWToolSDK);
        initListener();
        registerBroadcast();
    }
    @Override
    public void onDestroy() {
        LogUtils.debug(TAG,"onDestroy");
        try {
            if (receiver!=null)
                context.unregisterReceiver(receiver);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void registerBroadcast() {
        IntentFilter intentFilter=new IntentFilter(Constant.ACTION_MEASSAG_RECEIVER);
        try {
            context.registerReceiver(receiver,intentFilter);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!Constant.ACTION_MEASSAG_RECEIVER.equals(intent.getAction())){
                return;
            }
            boolean isListenerMessage = SPUtils.getBoolean(context,Constant.isListenerMessage,false);
            LogUtils.debug(TAG,"receiver  = "+isListenerMessage);
            if (isListenerMessage){
                ReplyBean replyBean = intent.getParcelableExtra("reply");
                String message = "recevied："+replyBean.getTalker()+"："
                        +replyBean.getContent()+"\n";
                editContent.setText(message);
                LogUtils.debug(TAG,"receiver  = "+replyBean);
            }

        }
    };

    private boolean initCheckBox() {
        final boolean isXposedOpen = SPUtils.getBoolean(context, Constant.SP_XPOSED_OPENED, true);
        ckb_xposed.setChecked(isXposedOpen);
        ckb_xposed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SPUtils.putBoolean(context, Constant.SP_XPOSED_OPENED,b);
                LogUtils.debug(TAG,"isXposedOpen="+b);
                HookUtils.IS_XPOSED_OPENED = b;
            }
        });
        return isXposedOpen;
    }

    private void initListener() {
        editContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        //处理消息 回调的Handler
        final Handler messageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                MessageEvent event = (MessageEvent) msg.obj;
                String content = event.getContent();
                final String talker = event.getTalker();

                LogUtils.debug(TAG,"content: " + content);
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    content = wToolSDK.decodeValue(jsonObject.getString("content"));
                } catch (Exception e) {
                    LogUtils.debug(TAG,"get content error " + e);
                }

                editContent.append("message: " + talker + "," + content + "\n");
                super.handleMessage(msg);
            }
        };

        wToolSDK.setOnMessageListener(new OnMessageListener() {
            @Override
            public void messageEvent(MessageEvent event) {
                //由于该回调是在子线程中，因些如果是有UI更新，需要使用Handler
                messageHandler.obtainMessage(0, event).sendToTarget();
            }
        });

        buttonInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //初始化
                parseResult(wToolSDK.init(Config.STRING_AUTHCODE));
                ToastUtils.showToast(context,"init");
            }
        });
        labelWxid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                //builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle(radioButtonFriend.isChecked() ? "选择接收好友" : "选择接收群");
                String content="";
                if (radioButtonFriend.isChecked()) {
                    content = wToolSDK.getFriends(0, 0);
                    //LogUtils.debug(TAG,"getFriends content="+content);
                } else {
                    content  = wToolSDK.getChatrooms(0, 0, true);
                    //final ChatRoom chatRoom = JsonUtil.json2Bean(content, ChatRoom.class);
                    //LogUtils.debug(TAG,"getChatrooms="+chatRoom);
                }

                String text = "";
                try {
                    if (selectedWxIdIndex < 0) {
                        selectedWxIdIndex = 0;
                    }

                    final JSONObject jsonObject = new JSONObject(content);
                    if (jsonObject.getInt("result") == 0) {
                        final JSONArray jsonArray = jsonObject.getJSONArray("content");
                        if (jsonArray.length() > 0) {
                            final String[] friends = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                final JSONObject jsonObject_Friend = jsonArray.getJSONObject(i);
                                friends[i] = wToolSDK.decodeValue(jsonObject_Friend.getString("nickname"));
                                if (radioButtonChatroom.isChecked() && friends[i].equals("")) {
                                    if (jsonObject_Friend.has("displayname")) {
                                        String displayname = wToolSDK.decodeValue(jsonObject_Friend.getString("displayname"));
                                        if (displayname.length()>=16) {
                                            friends[i] = displayname.substring(0, 16) + "...";
                                        }else{
                                            friends[i] = displayname;
                                        }
                                    }
                                }
                            }
                            if (selectedWxIdIndex >= jsonArray.length()) {
                                selectedWxIdIndex = 0;
                            }
                            //    设置一个单项选择下拉框
                            builder.setSingleChoiceItems(friends, selectedWxIdIndex, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    selectedWxIdIndex = which;
                                }
                            });
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        toWxId = wToolSDK.decodeValue(jsonArray.getJSONObject(selectedWxIdIndex).getString("wxid"));
                                        labelWxid.setText(DEF_TALKER + "：" + friends[selectedWxIdIndex]);
                                    } catch (Exception e) {
                                        toWxId = "";
                                    }

                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();
                        } else {
                            text = radioButtonFriend.isChecked() ? "无好友" : "无群";
                        }
                    } else {
                        text = jsonObject.getString("errmsg");
                    }
                } catch (Exception e) {
                    text = "解析结果失败";
                    LogUtils.debug(TAG, "json err "+e);
                }
                if (text.length() > 0) {
                    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                }
            }

        });
        buttonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toWxId.equals("")) {
                    Toast.makeText(context, "请选择接收人！", Toast.LENGTH_LONG).show();
                    return;
                }
                String message = editText.getText().toString();
                if (message.equals("")) {
                    Toast.makeText(context, "发送内容不能为空！", Toast.LENGTH_LONG).show();
                   return;
                }
                //发送文本
                LogUtils.debug(TAG,"toWxId="+toWxId+",msg="+ message);
                parseResult(wToolSDK.sendText(toWxId, message));

            }
        });
        labelImageFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.setType("image*//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);*/
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_IMAGE);
            }
        });
        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toWxId.equals("")) {
                    Toast.makeText(context, "请选择接收人！", Toast.LENGTH_LONG).show();
                    return;
                }
                if (toImageFile.equals("")) {
                    toImageFile = Environment.getExternalStorageDirectory().getPath() + "/lake.jpg";
                    LogUtils.debug(TAG, "filePath=" + toImageFile);
                    //Toast.makeText(context, "请选择要发送的图片！", Toast.LENGTH_LONG).show();
                    return;
                }
                //发送图片
                parseResult(wToolSDK.sendImage(toWxId, toImageFile));

            }
        });
        labelVoiceFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();//Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, RESULT_VOICE);
            }
        });
        buttonVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toWxId.equals("")) {
                    Toast.makeText(context, "请选择接收人！", Toast.LENGTH_LONG).show();
                    return;
                }
                if (toVoiceFile.equals("")) {
                    Toast.makeText(context, "请选择要发送的语音文件！", Toast.LENGTH_LONG).show();
                    return;
                }
                //发送图片
                parseResult(wToolSDK.sendVoice(toWxId, toVoiceFile, 60));

            }
        });
        labelVideoFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();//Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, RESULT_VIDEO);
            }
        });
        buttonVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toWxId.equals("")) {
                    Toast.makeText(context, "请选择接收人！", Toast.LENGTH_LONG).show();
                    return;
                }
                if (toVideoFile.equals("")) {
                    Toast.makeText(context, "请选择要发送的视频文件！", Toast.LENGTH_LONG).show();
                    return;
                }
                //发送video
                String thumbFile = makeVideoThumbFile(toVideoFile);
                if (thumbFile.equals("")) {
                    Toast.makeText(context, "生成视频缩略图失败！", Toast.LENGTH_LONG).show();
                    return;
                }
                parseResult(wToolSDK.sendVideo(toWxId, toVideoFile, thumbFile, 60));

            }
        });
        buttonFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取好友列表
                String content = wToolSDK.getFriends(0, 0);
                editContent.setText(content);
                parseResult(content);
                LogUtils.debug(TAG,"getFriends content="+content);
            }
        });
        buttonChatrooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取群列表
                String content = wToolSDK.getChatrooms(0, 0, true);
                editContent.setText(content);
                parseResult(content);
                LogUtils.debug(TAG,"getChatrooms content="+content);
            }
        });

        buttonStartMessage.setTag(0);

        buttonStartMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonStartMessage.getTag().equals(0)) {
                    final boolean started = WechatHookUtil.startMessageListener(wToolSDK);
                    if (started){
                        buttonStartMessage.setTag(1);
                        buttonStartMessage.setText("停止监听");
                    }
                    String text = "started =" + started;
                    editContent.setText(text);
                } else {
                    //wToolSDK.stopMessageListener();
                    WechatHookUtil.stopMessageListener(wToolSDK);
                    buttonStartMessage.setTag(0);
                    buttonStartMessage.setText("监听消息");
                    editContent.setText("started = false");
                }
            }
        });
    }


}
