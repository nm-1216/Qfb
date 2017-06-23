package com.sy.qfb.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.BoringLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.sy.qfb.R;
import com.sy.qfb.ble.activity.DeviceControlActivity;
import com.sy.qfb.ble.activity.DeviceScanActivity;
import com.sy.qfb.ble.service.BluetoothLeService;
import com.sy.qfb.ble.utils.SampleGattAttributes;
import com.sy.qfb.controller.SaveController;
import com.sy.qfb.model.MeasureData;
import com.sy.qfb.model.MeasurePoint;
import com.sy.qfb.model.Page;
import com.sy.qfb.model.Target;
import com.sy.qfb.util.ToastHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jshenf on 2017/6/8.
 */

public class MeasureActivity extends BaseActivity {
    @BindView(R.id.tl_measure)
    TableLayout tlTableMeasure;

    @BindView(R.id.sc_scroll)
    ScrollView scScroll;

    @BindView(R.id.btn_previous_page)
    Button btnPreviousPage;

    @BindView(R.id.btn_next_page)
    Button btnNextPage;

    @BindView(R.id.btn_save)
    Button btnSave;

    @BindView(R.id.tv_page_indicator)
    TextView tvPageIndicator;

    @BindView(R.id.tv_part_pn)
    TextView tvPartPn;

    @BindView(R.id.tv_part_name)
    TextView tvPartName;

    @BindView(R.id.tv_measure_target)
    TextView tvMeasureTarget;

    @BindView(R.id.tv_date)
    TextView tvDate;

    @BindView(R.id.btn_ok)
    Button btnOk;

    @BindView(R.id.btn_ng)
    Button btnNg;

    @BindView(R.id.tv_connection_state)
    TextView tvConnectionState;

    @BindView(R.id.img_1)
    ImageView img1;

    @BindView(R.id.ll_images)
    LinearLayout llImages;

    private int currentPage_Index = 0;
    private List<View> currentPage_Rows = new ArrayList<View>();
    private TextView[][] currentPaten_TextViewArray;
    private int currentRow_TvArray = 0;
    private int currentCol_TvArray = 0;
    private TextView currentPage_ActiveTextView = null;

    private SaveController saveController = new SaveController();
    private boolean currentPageSaved = false;

    private HashMap<Integer, List<MeasureData>> page_datas = new HashMap<Integer, List<MeasureData>>();


    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean mConnected = false;
    private ProgressDialog dialog = null;

    private boolean changed = false;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                Logger.e("Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                Toast.makeText(MeasureActivity.this, "蓝牙连接成功", Toast.LENGTH_SHORT).show();
//                dialog.show();
                progressDialog.show();
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                    .equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                Toast.makeText(MeasureActivity.this, "蓝牙连接断开", Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
//                clearUI();
                mBluetoothLeService.connect(mDeviceAddress);
//                dialog.hide();
                if (progressDialog.isShowing()) {
                    progressDialog.hide();
                }
//				updateConnectionState(R.string.connected_server);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                //服务加载完毕
                // Show all the supported services and characteristics on the
                // user interface.
                displayGattServices(mBluetoothLeService
                        .getSupportedGattServices());
//                dialog.hide();
                if (progressDialog.isShowing()) {
                    progressDialog.hide();
                }
                updateConnectionState(R.string.connected_server);

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                Logger.d("data available");
                //数据显示
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//                dialog.hide();
                if (progressDialog.isShowing()) {
                    progressDialog.hide();
                }
//				updateConnectionState(R.string.connected_server);
            }

        }
    };

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                mConnectionState.setText(resourceId);
                tvConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(final String data) {
        if (data != null) {
//            mDataField.setText(data);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int c_rows = currentPaten_TextViewArray.length;
                    int c_cols = 4;
                    if (currentRow_TvArray >= 0 && currentRow_TvArray < c_rows &&
                            currentCol_TvArray >= 0 && currentCol_TvArray <4) {
                        TextView tv = currentPaten_TextViewArray[currentRow_TvArray][currentCol_TvArray];
                        if (tv != null) {
                            tv.setText(data);
                        }
                    }
                    goNextCell();

                    changed = true;
                }
            });

        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        ButterKnife.bind(this);

        tvPartPn.setText("" + MainActivity.CURRENT_PRODUCT.product_id);

        tvPartName.setText(MainActivity.CURRENT_PRODUCT.product_name);
        tvMeasureTarget.setText(MainActivity.CURRENT_TARGET.target_name);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        tvDate.setText(sdf.format(new Date()));


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        img1.setMaxWidth(metrics.widthPixels / 4);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = metrics.widthPixels / 5;
        img1.setLayoutParams(params);

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params1.width = metrics.widthPixels / 5;
        params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params1.setMargins(0, metrics.heightPixels / 4, metrics.widthPixels / 25, 10);
        llImages.setLayoutParams(params1);

        currentPage_Index = 0;
        loadTable();
        setPageIndicator();
        currentPageSaved = false;

        btnPreviousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                gotoPreviousPage();
            }
        });

        btnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                gotoNextPage();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c_rows = currentPaten_TextViewArray.length;
                int c_cols = 4;
                if (currentRow_TvArray >= 0 && currentRow_TvArray < c_rows &&
                        currentCol_TvArray >= 0 && currentCol_TvArray <4) {
                    TextView tv = currentPaten_TextViewArray[currentRow_TvArray][currentCol_TvArray];
                    if (tv != null) {
                        tv.setText("OK");
                    }
                }
                goNextCell();
                changed = true;
            }
        });

        btnNg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c_rows = currentPaten_TextViewArray.length;
                int c_cols = 4;
                if (currentRow_TvArray >= 0 && currentRow_TvArray < c_rows &&
                        currentCol_TvArray >= 0 && currentCol_TvArray <4) {
                    TextView tv = currentPaten_TextViewArray[currentRow_TvArray][currentCol_TvArray];
                    if (tv != null) {
                        tv.setText("NG");
                    }
                }
                goNextCell();
                changed = true;
            }
        });



        if (MainActivity.CURRENT_TARGET.value_type.equalsIgnoreCase("data")) {
            btnNg.setVisibility(View.GONE);
            btnOk.setVisibility(View.GONE);
            tvConnectionState.setVisibility(View.VISIBLE);

            final Intent intent = getIntent();
            mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
            mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        } else {
            btnNg.setVisibility(View.VISIBLE);
            btnOk.setVisibility(View.VISIBLE);
            tvConnectionState.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (MainActivity.CURRENT_TARGET.value_type.equalsIgnoreCase("data")) {
            dialog = new ProgressDialog(MeasureActivity.this);
            dialog.setMessage("正在加载服务");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//            if (mBluetoothLeService != null) {
//                final boolean result = mBluetoothLeService.connect(mDeviceAddress);
//                Logger.d("Connect request result=" + result);
//            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("本页数据还没保存，请先保存再翻页，不然会丢失数据");

    }

    private void gotoNextPage() {
        currentPage_Index++;
        if (currentPage_Index > MainActivity.CURRENT_TARGET.pages.length - 1) {
            currentPage_Index = MainActivity.CURRENT_TARGET.pages.length - 1;
            ToastHelper.showShort("已在最后一页！");
            return;
        }
        loadTable();
        setPageIndicator();
        currentPageSaved = false;
    }

    private void gotoPreviousPage() {
        currentPage_Index--;
        if (currentPage_Index < 0) {
            currentPage_Index = 0;
            ToastHelper.showShort("已在第一页！");
            return;
        }
        loadTable();
        setPageIndicator();
        currentPageSaved = false;
    }

    private void goNextCell() {
        int c_rows = currentPaten_TextViewArray.length;
        int c_cols = 4;
        if (currentRow_TvArray < c_rows - 1) {
            currentRow_TvArray++;
        } else {
            if (currentCol_TvArray == 3) {
                currentCol_TvArray = 0;
            } else {
                currentCol_TvArray++;
            }
            currentRow_TvArray = 0;
        }

        if (currentPage_ActiveTextView != null) {
            hilightTextView(currentPage_ActiveTextView, false);
        }
        currentPage_ActiveTextView = currentPaten_TextViewArray[currentRow_TvArray][currentCol_TvArray];
//        if (currentPage_ActiveTextView != null) {
            hilightTextView(currentPage_ActiveTextView, true);
//        }

        View vRow = currentPage_Rows.get(currentRow_TvArray);
        int height = scScroll.getMeasuredHeight();
        double scrollY = scScroll.getScrollY();
        Logger.d("vRow.getY() = " + vRow.getY() + ", scScroll.getScrollY() = " + scScroll.getScrollY()  + ", height = " + height);
        if (vRow.getY() + 70 > scrollY + height) {
            scScroll.scrollTo(0, (int) vRow.getY());
        }
        else if (vRow.getY() < scrollY) {
            int target_y = (int) (scrollY - height);
            if (target_y < 0) target_y = 0;
            scScroll.scrollTo(0, target_y);
        }
    }

    private void hilightTextView(TextView tv, boolean hilight) {
        if (hilight) {
//            TableRow.LayoutParams lp = new TableRow.LayoutParams();
//            lp.setMargins(1, 1, 1, 1);
//            currentPage_ActiveTextView.setLayoutParams(lp);
            tv.setBackgroundColor(Color.BLUE);
            tv.setTextColor(Color.WHITE);
        } else {
//                TableRow.LayoutParams lp = new TableRow.LayoutParams();
//                lp.setMargins(0, 0, 0, 0);
//                currentPage_ActiveTextView.setLayoutParams(lp);
            tv.setBackgroundColor(Color.WHITE);
            tv.setTextColor(Color.BLACK);
        }
    }

    private void setPageIndicator() {
        int totalPage = MainActivity.CURRENT_TARGET.pages.length;
        String indication = String.format("页码 Page No. " + (currentPage_Index + 1) + " of " + totalPage);
        tvPageIndicator.setText(indication);
    }

    private void loadTable() {
        Target target = MainActivity.CURRENT_TARGET;
        Page[] pages = target.pages;

        if (currentPage_Index >= 0 && currentPage_Index < pages.length) {
            // 在 UI 中去掉上一页的行
            for (int i = 0; i < tlTableMeasure.getChildCount(); ++i) {
                View child = tlTableMeasure.getChildAt(i);
                if (child.getId() != R.id.row1 && child.getId() != R.id.row2) {
                    tlTableMeasure.removeView(child);
                    --i;
                }
            }

            currentPage_Rows.clear();

            Page p = pages[currentPage_Index];
            MeasurePoint[] mpoints = p.measure_points;

            currentPaten_TextViewArray = new TextView[mpoints.length][4];

            changed = false;
            boolean hasPreviousData = page_datas.containsKey(currentPage_Index);

            LayoutInflater layoutInflater = getLayoutInflater();
            for (int i = 0; i < mpoints.length; ++i) {
                View view = layoutInflater.inflate(R.layout.item_measure, null);
                TextView tvName = (TextView) view.findViewById(R.id.tv_mp_name);
                tvName.setText(mpoints[i].point);
                TextView tvDirection = (TextView) view.findViewById(R.id.tv_mp_direction);
                tvDirection.setText(mpoints[i].direction);

                TextView tvData1 = (TextView) view.findViewById(R.id.tv_data1);
                TextView tvData2 = (TextView) view.findViewById(R.id.tv_data2);
                TextView tvData3 = (TextView) view.findViewById(R.id.tv_data3);
                TextView tvData4 = (TextView) view.findViewById(R.id.tv_data4);

                if (target.value_type.equalsIgnoreCase("OK,NG")) {
                    tvData1.setOnClickListener(new ClickLisenter_Okng(i, 0));
                    tvData2.setOnClickListener(new ClickLisenter_Okng(i, 1));
                    tvData3.setOnClickListener(new ClickLisenter_Okng(i, 2));
                    tvData4.setOnClickListener(new ClickLisenter_Okng(i, 3));
                } else if (target.value_type.equalsIgnoreCase("data")) {
                    tvData1.setOnClickListener(new ClickLisenter_Data(i, 0));
                    tvData2.setOnClickListener(new ClickLisenter_Data(i, 1));
                    tvData3.setOnClickListener(new ClickLisenter_Data(i, 2));
                    tvData4.setOnClickListener(new ClickLisenter_Data(i, 3));
                }

                if (hasPreviousData) {
                    List<MeasureData> datas = page_datas.get(currentPage_Index);
                    loadPreviousData(datas, mpoints[i].point, tvData1, tvData2, tvData3, tvData4);
                    changed = true;
                }

                currentPaten_TextViewArray[i][0] = tvData1;
                currentPaten_TextViewArray[i][1] = tvData2;
                currentPaten_TextViewArray[i][2] = tvData3;
                currentPaten_TextViewArray[i][3] = tvData4;

                tlTableMeasure.addView(view);

                currentPage_Rows.add(view);
            }

            MainActivity.CURRENT_PAGE = p;

            if (p.pictures != null) {
//                for (String pictureName : p.pictures) {
                if (p.pictures.length > 0) {

                    File fileDir = getFilesDir();
//                    File fileImage = new File(fileDir, pictureName);
                    File fileImage = new File(fileDir, p.pictures[0]);

                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("file");
                    builder.path(fileImage.getAbsolutePath());
                    Uri uri = builder.build();
                    img1.setImageURI(uri);

                    img1.setOnClickListener(new ImageOnClickListener(p.pictures[0]));
                }
            }

            currentRow_TvArray = 0;
            currentCol_TvArray = 0;
            currentPage_ActiveTextView = currentPaten_TextViewArray[currentRow_TvArray][currentCol_TvArray];
            hilightTextView(currentPage_ActiveTextView, true);

        }
    }

    private class ImageOnClickListener implements View.OnClickListener {
        private String picName;

        public ImageOnClickListener(String picName) {
            this.picName = picName;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MeasureActivity.this, ImageViewActivity.class);
            intent.putExtra("PIC_NAME", picName);
            startActivity(intent);
        }
    }

    private void loadPreviousData(List<MeasureData> datas, String mpoint, TextView tvData1, TextView tvData2, TextView tvData3, TextView tvData4) {
        for (MeasureData md : datas) {
            if (mpoint.equals(md.measure_point)) {
                tvData1.setText(md.value1);
                tvData2.setText(md.value2);
                tvData3.setText(md.value3);
                tvData4.setText(md.value4);
                break;
            }
        }
    }

    private class ClickLisenter_Okng implements View.OnClickListener {
        private int index_row = 0;
        private int index_col = 0;

        public ClickLisenter_Okng(int r, int c) {
            this.index_row = r;
            this.index_col = c;
        }

        @Override
        public void onClick(View v) {
            MeasureActivity.this.currentRow_TvArray = index_row;
            MeasureActivity.this.currentCol_TvArray = index_col;

            if(currentPage_ActiveTextView != null) {
                hilightTextView(currentPage_ActiveTextView, false);
            }
            currentPage_ActiveTextView = currentPaten_TextViewArray[currentRow_TvArray][currentCol_TvArray];
            hilightTextView(currentPage_ActiveTextView, true);
        }
    }

    private class ClickLisenter_Data implements View.OnClickListener {
        private int index_row = 0;
        private int index_col = 0;

        public ClickLisenter_Data(int r, int c) {
            this.index_row = r;
            this.index_col = c;
        }

        @Override
        public void onClick(View v) {
            MeasureActivity.this.currentRow_TvArray = index_row;
            MeasureActivity.this.currentCol_TvArray = index_col;

            if(currentPage_ActiveTextView != null) {
                hilightTextView(currentPage_ActiveTextView, false);
            }
            currentPage_ActiveTextView = currentPaten_TextViewArray[currentRow_TvArray][currentCol_TvArray];
            hilightTextView(currentPage_ActiveTextView, true);
        }
    }

    private void saveData() {
        if (!changed) return;

        List<MeasureData> lstMeasureData = new ArrayList<MeasureData>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        for (View view : currentPage_Rows) {
            MeasureData data = new MeasureData();

            TextView tvName = (TextView) view.findViewById(R.id.tv_mp_name);
            TextView tvDirection = (TextView) view.findViewById(R.id.tv_mp_direction);
            TextView tvData1 = (TextView) view.findViewById(R.id.tv_data1);
            TextView tvData2 = (TextView) view.findViewById(R.id.tv_data2);
            TextView tvData3 = (TextView) view.findViewById(R.id.tv_data3);
            TextView tvData4 = (TextView) view.findViewById(R.id.tv_data4);

            data.measure_point = tvName.getText().toString();
            data.value1 = tvData1.getText().toString();
            data.value2 = tvData2.getText().toString();
            data.value3 = tvData3.getText().toString();
            data.value4 = tvData4.getText().toString();

            data.username = LoginActivity.CURRENT_USER.username;
            data.timestamp = calendar.getTimeInMillis();

            data.projectId = MainActivity.CURRENT_PROJECT.project_id;
            data.productId = MainActivity.CURRENT_PRODUCT.product_id;
            data.targetId = MainActivity.CURRENT_TARGET.target_id;
            data.pageId = MainActivity.CURRENT_PAGE.page_id;

            data.projectName = MainActivity.CURRENT_PROJECT.project_name;
            data.productName = MainActivity.CURRENT_PRODUCT.product_name;
            data.targetName = MainActivity.CURRENT_TARGET.target_name;
            data.direction = tvDirection.getText().toString();

            data.targetType = MainActivity.CURRENT_TARGET.value_type;

            lstMeasureData.add(data);
        }

        saveController.saveData(lstMeasureData);
        page_datas.put(currentPage_Index, lstMeasureData);

        currentPageSaved = true;
        ToastHelper.showShort("保存成功！");
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        String uuid = null;
        // String unknownServiceString =
        // getResources().getString(R.string.unknown_service);
        // String unknownCharaString =
        // getResources().getString(R.string.unknown_characteristic);

        //服务数据
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        //特性数据
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();

        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        // 遍历可用的GATT服务
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            // if (uuid.equals("0000fff0-0000-1000-8000-00805f9b34fb")) {
            currentServiceData.put(LIST_NAME,
                    SampleGattAttributes.lookup(uuid, "MeasureData CharaString"));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                // charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();

                charas.add(gattCharacteristic);
                currentCharaData.put(LIST_NAME,
                        SampleGattAttributes.lookup(uuid, "MeasureData CharaString"));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);

                if (uuid.equals("0000fff4-0000-1000-8000-00805f9b34fb")) {
                    final int charaProp = gattCharacteristic.getProperties();

                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                        // If there is an active notification on a characteristic,
                        // clear
                        // it first so it doesn't update the data field on the user
                        // interface.
                        if (mNotifyCharacteristic != null) {
                            mBluetoothLeService.setCharacteristicNotification(
                                    mNotifyCharacteristic, false);
                            mNotifyCharacteristic = null;
                        }
                        mBluetoothLeService.readCharacteristic(gattCharacteristic);
                    }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        mNotifyCharacteristic = gattCharacteristic;
                        mBluetoothLeService.setCharacteristicNotification(
                                gattCharacteristic, true);
                    }
                }

            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
            // }
        }

//        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
//                this, gattServiceData,
//                android.R.layout.simple_expandable_list_item_2, new String[]{
//                LIST_NAME, LIST_UUID}, new int[]{android.R.id.text1,
//                android.R.id.text2}, gattCharacteristicData,
//                android.R.layout.simple_expandable_list_item_2, new String[]{
//                LIST_NAME, LIST_UUID}, new int[]{android.R.id.text1,
//                android.R.id.text2});
//        mGattServicesList.setAdapter(gattServiceAdapter);

    }


}
