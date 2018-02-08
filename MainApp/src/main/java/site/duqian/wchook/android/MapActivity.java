package site.duqian.wchook.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;

import site.duqian.wchook.MainActivity;
import site.duqian.wchook.R;
import site.duqian.wchook.common.ApiUtil;
import site.duqian.wchook.model.NonoCallBack;
import site.duqian.wchook.utils.LogUtils;
import site.duqian.wchook.utils.ToastUtil;
import site.duqian.wchook.xposed.SettingsHelper;

import static site.duqian.wchook.model.Constant.MAP_DEFAULT_LATITUDE;
import static site.duqian.wchook.model.Constant.MAP_DEFAULT_LONGITUDE;
import static site.duqian.wchook.model.Constant.SP_ADDRESS;
import static site.duqian.wchook.model.Constant.SP_LATITUDE;
import static site.duqian.wchook.model.Constant.SP_LONGITUDE;

/**
 * Created by Dusan (duqian) on 2017/5/6 - 20:56.
 * E-mail: duqian2010@gmail.com
 * Description:MapActivity 高德地图 google map
 * remarks:
 */
public class MapActivity extends FragmentActivity implements View.OnClickListener,
        AMap.OnCameraChangeListener, OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener, AMapLocationListener,
        CompoundButton.OnCheckedChangeListener, AMap.OnMapClickListener{
    private static final String TAG = MapActivity.class.getSimpleName();
    private ToggleButton mcheckbtn;
    private Button mapbtn;
    private Button btn_confirm;
    private TextView tv_position;
    private LinearLayout mContainerLayout;
    private LinearLayout.LayoutParams mParams;
    private TextureMapView mAmapView;
    private MapView mGoogleMapView;
    private float zoom = 10;
    private double latitude = 23.10485;
    private double longitude = 113.388975;
    private boolean mIsAmapDisplay = true;
    private boolean mIsAuto = true;
    private AMap amap;
    private GoogleMap googlemap;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private AlphaAnimation anappear;
    private AlphaAnimation andisappear;
    private Context context;
    private MapActivity activity;
    private GeocodeSearch geocoderSearch;
    private String formatAddress="";
    private SettingsHelper mSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        context = this;
        activity = this;
        mSettings = new SettingsHelper(context);
        initView();
        initGDMap(savedInstanceState);
        configGDLocation();
        //changeToGoogleMapView();
        getLastPostion();
    }

    //标注上次保存的位置
    private void getLastPostion() {
        double latitude = Double.parseDouble(mSettings.getString(SP_LATITUDE, MAP_DEFAULT_LATITUDE));
        double longitude = Double.parseDouble(mSettings.getString(SP_LONGITUDE, MAP_DEFAULT_LONGITUDE));
        LatLng latLng = new LatLng(latitude,longitude);
        initGeocodeSearch(latLng);
    }

    // 中心点marker
    private Marker centerMarker;
    private BitmapDescriptor ICON_YELLOW = BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
    private BitmapDescriptor ICON_RED = BitmapDescriptorFactory
            .defaultMarker(BitmapDescriptorFactory.HUE_RED);
    private MarkerOptions markerOption = null;

    //高德地图
    @Override
    public void onMapClick(LatLng latLng) {
        longitude = latLng.longitude;
        latitude = latLng.latitude;
        initGeocodeSearch(latLng);
    }

    //高德地图，纬度/经度的反向地理编码
    private void initGeocodeSearch(LatLng latLng) {
        if (geocoderSearch==null) {
            geocoderSearch = new GeocodeSearch(this);
            geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                    RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
                    formatAddress = regeocodeAddress.getFormatAddress();
                    LogUtils.debug(TAG,"regeocodeResult："+ formatAddress);
                    setCurrentPositionInfo();
                    addCenterMarker(latLng);
                }

                @Override
                public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {}
            });
        }
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude,latLng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    private void setCurrentPositionInfo() {
        String position = "当前所选位置：经度：" + longitude + " 纬度：" + latitude+"\n"+formatAddress;
        LogUtils.debug(TAG, position);
        ToastUtil.toast(context, position);
        tv_position.setText(position);
        tv_position.setBackgroundColor(getResources().getColor(R.color.colorWhite));
    }

    private void addCenterMarker(LatLng latlng) {
        if (null == centerMarker) {
            markerOption = new MarkerOptions();
            markerOption.icon(ICON_RED);//ICON_RED  ICON_YELLOW
            centerMarker = amap.addMarker(markerOption);
            centerMarker.setPosition(latlng);
            centerMarker.setTitle("当前坐标："+longitude+","+latitude+"\n"+formatAddress);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //setMapResult();
    }

    private void setMapResult() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(SP_LATITUDE, "" + latitude);
        intent.putExtra(SP_LONGITUDE, "" + longitude);
        intent.putExtra(SP_ADDRESS, "" + formatAddress);
        activity.setResult(100, intent);
        activity.finish();
    }

    private void configGDLocation() {
        //初始化client
        mlocationClient = new AMapLocationClient(this.getApplicationContext());
        // 设置定位监听
        mlocationClient.setLocationListener(this);
        //定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置为高精度定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
        //myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        //myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        amap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        amap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        amap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }

    private void initView() {
        mContainerLayout = (LinearLayout) findViewById(R.id.map_container);
        tv_position = (TextView) findViewById(R.id.tv_position);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        mapbtn = (Button) findViewById(R.id.btn_change_map);
        mcheckbtn = (ToggleButton) findViewById(R.id.btn_auto_change);
        mapbtn.setOnClickListener(this);
        mcheckbtn.setOnClickListener(this);
        mcheckbtn.setOnCheckedChangeListener(this);
        btn_confirm.setOnClickListener(this);
        mContainerLayout = (LinearLayout) findViewById(R.id.map_container);
    }

    private void initGDMap(Bundle savedInstanceState) {
        mAmapView = new TextureMapView(this);
        mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mContainerLayout.addView(mAmapView, mParams);
        mAmapView.onCreate(savedInstanceState);
        if (amap == null) {
            amap = mAmapView.getMap();
            amap.setOnCameraChangeListener(this);
            amap.setOnMapClickListener(this);//new
        }
        anappear = new AlphaAnimation(0, 1);
        andisappear = new AlphaAnimation(1, 0);
        anappear.setDuration(5000);
        andisappear.setDuration(5000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_auto_change:
                mIsAuto = mcheckbtn.isChecked();
                break;
            case R.id.btn_confirm:
                setMapResult();
                break;
            case R.id.btn_change_map:
                mcheckbtn.setChecked(false);
                mIsAuto = false;
                if (mIsAmapDisplay) {
                    changeToGoogleMapView();
                } else {
                    changeToAmapView();
                }
                break;
        }
    }

    private void changeToAmapView() {
        if (googlemap != null) {
            zoom = googlemap.getCameraPosition().zoom;
            latitude = googlemap.getCameraPosition().target.latitude;
            longitude = googlemap.getCameraPosition().target.longitude;
        }
        mapbtn.setText("To 谷歌地图");
        mAmapView = new TextureMapView(this, new AMapOptions()
                .camera(new com.amap.api.maps.model.CameraPosition(new LatLng(latitude, longitude), zoom, 0, 0)));
        mAmapView.onCreate(null);
        mAmapView.onResume();
        mContainerLayout.addView(mAmapView, mParams);

        mGoogleMapView.animate().alpha(0f).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mGoogleMapView.setVisibility(View.GONE);
                mContainerLayout.removeView(mGoogleMapView);
                if (mGoogleMapView != null) {
                    mGoogleMapView.onDestroy();
                }
            }
        });
        //AMap amap = mAmapView.getMap();
        amap = mAmapView.getMap();
        amap.setOnCameraChangeListener(this);
        amap.setOnMapClickListener(this);
        mIsAmapDisplay = true;
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            mAmapView.setVisibility(View.GONE);
            mContainerLayout.removeView(mAmapView);
            if (mAmapView != null) {
                mAmapView.onDestroy();
            }
        }
    };

    private void changeToGoogleMapView() {
        zoom = mAmapView.getMap().getCameraPosition().zoom;
        latitude = mAmapView.getMap().getCameraPosition().target.latitude;
        longitude = mAmapView.getMap().getCameraPosition().target.longitude;

        mapbtn.setText("To 高德地图");
        mIsAmapDisplay = false;
        mGoogleMapView = new com.google.android.gms.maps.MapView(this, new GoogleMapOptions()
                .camera(new com.google.android.gms.maps.model
                        .CameraPosition(new com.google.android.gms.maps.model.LatLng(latitude, longitude), zoom, 0, 0)));
        mGoogleMapView.onCreate(null);
        mGoogleMapView.onResume();
        mContainerLayout.addView(mGoogleMapView, mParams);
        mGoogleMapView.getMapAsync(this);
        handler.sendEmptyMessageDelayed(0, 500);
    }

    @Override
    public void onCameraChange(com.amap.api.maps.model.CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(com.amap.api.maps.model.CameraPosition cameraPosition) {
        longitude = cameraPosition.target.longitude;
        latitude = cameraPosition.target.latitude;
        zoom = cameraPosition.zoom;
        if (!isInArea(latitude, longitude) && mIsAmapDisplay && mIsAuto) {
            changeToGoogleMapView();
        }
    }

    private boolean isInArea(double latitude, double longtitude) {
        if ((latitude > 3.837031) && (latitude < 53.563624)
                && (longtitude < 135.095670) && (longtitude > 73.502355)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAmapView != null) {
            mAmapView.onResume();
        }
        if (mGoogleMapView != null) {
            try {
                mGoogleMapView.onResume();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAmapView != null) {
            mAmapView.onPause();
        }
        if (mGoogleMapView != null) {
            try {
                mGoogleMapView.onPause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAmapView != null) {
            mAmapView.onSaveInstanceState(outState);
        }
        if (mGoogleMapView != null) {
            try {
                mGoogleMapView.onSaveInstanceState(outState);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLocation();
        if (mAmapView != null) {
            mAmapView.onDestroy();
        }
        if (mGoogleMapView != null) {
            try {
                mGoogleMapView.onDestroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googlemap = googleMap;
        if (googlemap != null) {
            googlemap.setOnCameraMoveListener(this);
        }
        addGoogleMarker();
        googlemap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(com.google.android.gms.maps.model.LatLng latLng) {
                longitude = latLng.longitude;
                latitude = latLng.latitude;
                getGooglePosition(latLng);
            }
        });
        UiSettings uiSettings = googlemap.getUiSettings();
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        //uiSettings.setCompassEnabled(true);
    }

    private void getGooglePosition(com.google.android.gms.maps.model.LatLng latLng) {
        ApiUtil.init().getGooglePostion(latLng, new NonoCallBack() {
            public void onSuccess(String result) {
                formatAddress = result;
                LogUtils.debug(TAG,"Google formatAddress ="+formatAddress);
                setCurrentPositionInfo();
                addGoogleMarker();
            }
        });
    }

    private void addGoogleMarker() {
        com.google.android.gms.maps.model.LatLng location = new com.google.android.gms.maps.model.LatLng(latitude, longitude);
        googlemap.addMarker(new com.google.android.gms.maps.model.MarkerOptions().position(location).title("当前坐标："+longitude+","+latitude));
        googlemap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLng(location));
    }


    @Override
    public void onCameraMove() {
        CameraPosition cameraPosition=googlemap.getCameraPosition();
        longitude = cameraPosition.target.longitude;
        latitude = cameraPosition.target.latitude;
        zoom = cameraPosition.zoom;
        if (isInArea(latitude, longitude) && !mIsAmapDisplay && mIsAuto) {
            changeToAmapView();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null
                && aMapLocation.getErrorCode() == 0) {
            //longitude = aMapLocation.getLongitude();
            //latitude = aMapLocation.getLatitude();
            if (!aMapLocation.getCountry().equals("中国")){
                changeToGoogleMapView();
            } else {
                amap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 15));
            }
            //Toast.makeText(context,aMapLocation.getCountry(),Toast.LENGTH_LONG).show();
            mIsAuto = false;
            mcheckbtn.setChecked(false);
            //setCurrentPositionInfo();
        } else {
            String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
            LogUtils.debug("AmapErr", errText);
            Toast.makeText(context, errText, Toast.LENGTH_LONG).show();
        }
    }

    private void stopLocation(){
        // 停止定位
        mlocationClient.stopLocation();
    }

    private void destroyLocation(){
        if (null != mlocationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mlocationClient.onDestroy();
            mlocationClient = null;
            mlocationClient = null;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId()==R.id.btn_auto_change) {
            mIsAuto = isChecked;
        }
    }
}
