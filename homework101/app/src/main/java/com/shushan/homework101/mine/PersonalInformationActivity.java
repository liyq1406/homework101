package com.shushan.homework101.mine;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.shushan.homework101.Constants;
import com.shushan.homework101.HttpHelper.service.entity.homework.UploadPic;
import com.shushan.homework101.HttpHelper.service.entity.mine.GetUserInfo;
import com.shushan.homework101.HttpHelper.service.entity.mine.SetUserInfo;
import com.shushan.homework101.HttpHelper.service.presenter.GetUserInfoPresenter;
import com.shushan.homework101.HttpHelper.service.presenter.SetUserInfoPresenter;
import com.shushan.homework101.HttpHelper.service.presenter.UploadPresenter;
import com.shushan.homework101.HttpHelper.service.view.GetUserInfoView;
import com.shushan.homework101.HttpHelper.service.view.SetUserInfoView;
import com.shushan.homework101.HttpHelper.service.view.UploadView;
import com.shushan.homework101.R;
import com.shushan.homework101.Utils.LogUtils;
import com.shushan.homework101.Utils.Utils;
import com.shushan.homework101.banner.ImageViewRoundRect;
import com.shushan.homework101.base.BaseActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public class PersonalInformationActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.tv_actionbar)
    TextView tv_actionbar;
    @Bind(R.id.iv_actionbar_left)
    ImageView iv_actionbar_left;
    @Bind(R.id.ll_personal_info)
    LinearLayout ll_personal_info;
    @Bind(R.id.rl_mine_personal_class)
    RelativeLayout rl_mine_personal_class;
    @Bind(R.id.tv_mine_personal_class_default)
    TextView tv_mine_personal_class_default;
    @Bind(R.id.rb_mine_personal_man)
    RadioButton rb_mine_personal_man;
    @Bind(R.id.rb_mine_personal_woman)
    RadioButton rb_mine_personal_woman;
    @Bind(R.id.rl_mine_personal_via)
    RelativeLayout rl_mine_personal_via;
    @Bind(R.id.iv_mine_personal_visa)
    ImageViewRoundRect iv_mine_personal_visa;
    @Bind(R.id.rl_mine_personal_region)
    RelativeLayout rl_mine_personal_region;
    @Bind(R.id.tv_mine_personal_region_default)
    TextView tv_mine_personal_region_default;
    @Bind(R.id.rl_mine_personal_name)
    RelativeLayout rl_mine_personal_name;
    @Bind(R.id.tv_mine_personal_name_default)
    TextView tv_mine_personal_name_default;
    private String[] classArrays;
    private int width;
    private int height;
    private String selectGrade;
    private WheelGrade wheelClassMain;
    private SharedPreferences sharedPreferences;
    private int sex;
    private int grade_position;

    // ���ջش���
    public final static int CAMERA_REQUEST_CODE = 0;
    // ���ѡ��ش���
    public final static int GALLERY_REQUEST_CODE = 1;
    public static final String PATH = Environment.getExternalStorageDirectory()
            .toString() + "/AndroidMedia/";
    private Uri photoUri;
    private String album_path;
    private CityPickerView mPicker;
    private CityConfig cityConfig;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String visa;
    private String name;
    private SetUserInfoPresenter setUserInfoPresenter;
    private Map<String, String> mapRequest;
    private String userid;
    private String token;
    private UploadPresenter uploadPresenter;
    private GetUserInfoPresenter getUserInfoPresenter;
    private String grade;

    @Override
    protected void initContentView() {
        changeStatusBarTextImgColor(true);
        setContentView(R.layout.activity_personal_information);
    }

    @Override
    protected void initData() {
        sharedPreferences = PersonalInformationActivity.this.getSharedPreferences("info", Context.MODE_PRIVATE);
        DisplayMetrics displayMetrics = Utils.getScreenWH(this);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        classArrays = this.getResources().getStringArray(R.array.preference_primary_class);
        selectGrade = this.getResources().getString(R.string.select_grade);
        userid =sharedPreferences.getString("userid","");
        token = sharedPreferences.getString("token","");

        if(!TextUtils.isEmpty(userid)&&!TextUtils.isEmpty(token)){
            getUserInfoPresenter.getUserInfoMsg(userid,token);
        }
    }

    @Override
    protected void initViews() {
        mapRequest = new HashMap<>();
        tv_actionbar.setText(getResources().getString(R.string.per_details));
        tv_actionbar.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        TextPaint textPaint=tv_actionbar.getPaint();
        textPaint.setFakeBoldText(true);
        mPicker = new CityPickerView();
        mPicker.init(this);
        setUserInfoPresenter = new SetUserInfoPresenter(this);
        setUserInfoPresenter.onCreate(Constants.BASE_URL);
        setUserInfoPresenter.attachView(setUserInfoView);
        uploadPresenter = new UploadPresenter(this);
        uploadPresenter.onCreate(Constants.BASE_URL);
        uploadPresenter.attachView(uploadView);
        getUserInfoPresenter = new GetUserInfoPresenter(this);
        getUserInfoPresenter.onCreate(Constants.BASE_URL);
        getUserInfoPresenter.attachView(getUserInfoView);

    }

    @Override
    protected void initEvents() {
        iv_actionbar_left.setOnClickListener(this);
        rl_mine_personal_class.setOnClickListener(this);
        rb_mine_personal_man.setOnClickListener(this);
        rb_mine_personal_woman.setOnClickListener(this);
        rl_mine_personal_via.setOnClickListener(this);
        rl_mine_personal_region.setOnClickListener(this);
        rl_mine_personal_name.setOnClickListener(this);

    }
    private GetUserInfoView getUserInfoView=new GetUserInfoView() {
        @Override
        public void onSuccess(GetUserInfo getUserInfo) {
            if(getUserInfo!=null&&getUserInfo.getError()==0){
                LogUtils.d(getUserInfo.toString());

                visa=getUserInfo.getData().getTrait();
                name=getUserInfo.getData().getUsername();
                sex=getUserInfo.getData().getSex();
                grade_position=getUserInfo.getData().getGrade_id();
                grade = getUserInfo.getData().getGrade();
                provinceName=TextUtils.isEmpty(getUserInfo.getData().getProvince())?getResources().getString(R.string.province):getUserInfo.getData().getProvince();
                cityName=TextUtils.isEmpty(getUserInfo.getData().getCity())?getResources().getString(R.string.city):getUserInfo.getData().getCity();
                districtName=TextUtils.isEmpty(getUserInfo.getData().getCountry())?getResources().getString(R.string.district):getUserInfo.getData().getCountry();
                RequestOptions options = new RequestOptions()
                        .error(R.drawable.visa)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(PersonalInformationActivity.this).load(visa).apply(options).into(iv_mine_personal_visa);
                tv_mine_personal_name_default.setText(name);
                if(sex==1){
                    rb_mine_personal_man.setChecked(true);
                }else if(sex==2){
                    rb_mine_personal_woman.setChecked(true);
                }
                tv_mine_personal_class_default.setText(grade);
                tv_mine_personal_region_default.setText(provinceName+" "+cityName+" "+districtName);
                sharedPreferences.edit().putString("grade_position",grade_position+"").commit();
            }
        }

        @Override
        public void onError(String result) {

        }
    };
    private UploadView uploadView=new UploadView() {
        @Override
        public void onSuccess(UploadPic uploadPic) {
            if(uploadPic!=null&&uploadPic.getError()==0){
                LogUtils.d(uploadPic.toString());
                visa = uploadPic.getData()[0];
                RequestOptions options = new RequestOptions()
                        .error(R.drawable.visa)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(PersonalInformationActivity.this).load(visa).apply(options).into(iv_mine_personal_visa);
            }
        }

        @Override
        public void onError(String result) {

        }
    };
    private SetUserInfoView setUserInfoView=new SetUserInfoView() {
        @Override
        public void onSuccess(SetUserInfo setUserInfo) {
            if(setUserInfo!=null&&setUserInfo.getError()==0){
                LogUtils.d(setUserInfo.toString());

            }
            finish();
        }

        @Override
        public void onError(String result) {

        }
    };
    public void showVisaPopupWindow() {

        View menuView = LayoutInflater.from(this).inflate(R.layout.show_visa_popup, null);
        final PopupWindow mPopupWindow = new PopupWindow(menuView, (int) (width),
                ActionBar.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.showAtLocation(ll_personal_info, Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new poponDismissListener());
        darkenBackground(0.5f);
        TextView tv_take_photo = (TextView) menuView.findViewById(R.id.tv_take_photo);
        TextView tv_album = (TextView) menuView.findViewById(R.id.tv_album);
        TextView tv_visa_cancel = (TextView) menuView.findViewById(R.id.tv_visa_cancel);


        tv_visa_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
                darkenBackground(1f);

            }
        });

        tv_take_photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
                darkenBackground(1f);
                if (ContextCompat.checkSelfPermission(PersonalInformationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //Ȩ�޻�û�����裬��Ҫ������д����Ȩ�޵Ĵ���
                    // �ڶ���������һ���ַ������飬����������Ҫ�����Ȩ�� ��������������Ȩ��
                    // ���һ�������Ǳ�־����������Ȩ�ޣ��ó�����onRequestPermissionsResult��ʹ�õ�
                    ActivityCompat.requestPermissions(PersonalInformationActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            CAMERA_REQUEST_CODE);
                }else {
                        takePhoto();
                    }

            }
        });
        tv_album.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
                darkenBackground(1f);
                if (ContextCompat.checkSelfPermission(PersonalInformationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(PersonalInformationActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
                } else {
                    choosePhoto();
                }
            }

        });
    }

    public void showGradePopupWindow(String[] arrays, String title) {

        View menuView = LayoutInflater.from(this).inflate(R.layout.show_popup_window, null);
        final PopupWindow mPopupWindow = new PopupWindow(menuView, (int) (width),
                ActionBar.LayoutParams.WRAP_CONTENT);
        wheelClassMain = new WheelGrade(menuView, true, this);
        wheelClassMain.screenheight = height;
        wheelClassMain.initDateTimePicker(arrays);
        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.showAtLocation(ll_personal_info, Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new poponDismissListener());
        darkenBackground(0.5f);
        TextView tv_cancle = (TextView) menuView.findViewById(R.id.tv_cancel);
        TextView tv_ensure = (TextView) menuView.findViewById(R.id.tv_ensure);
        TextView tv_pop_title = menuView.findViewById(R.id.tv_pop_title);
        tv_pop_title.setText(title);

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
                darkenBackground(1f);

            }
        });

        tv_ensure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
                darkenBackground(1f);
                tv_mine_personal_class_default.setText(wheelClassMain.getName());

                for (int i = 0; i < classArrays.length; i++) {
                    if (wheelClassMain.getName().equals(classArrays[i])) {

                        grade_position = i+1;
                        sharedPreferences.edit().putString("grade_position",grade_position+"").commit();
                    }
                }

            }
        });
    }

    private void choosePhoto(){
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        // ��������ϴ�����������ͼƬ����ʱ����ֱ��д�磺"image/jpeg �� image/png�ȵ�����" ����������д "image/*"
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/png");
        startActivityForResult(intentToPickPic, GALLERY_REQUEST_CODE);
    }


    private void takePhoto(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){

            File dir = new File(PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // ��ת��ϵͳ�����ս���
            Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            long dateTaken = System.currentTimeMillis();
            // image name
            String filename = DateFormat.format("yyyy-MM-dd kk.mm.ss", dateTaken)
                    .toString() + ".jpg";

            photoUri = Uri.parse(new File(dir,filename).getPath());

            intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File(PATH,filename)));

            startActivityForResult(intentToTakePhoto, CAMERA_REQUEST_CODE);
        }else{
            //todo
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:

                    if (photoUri != null) {
                        LogUtils.d(photoUri.toString());
                        new ImageToBase64AsyncTask().execute(photoUri.toString());
                    }
                    break;
                case GALLERY_REQUEST_CODE:

                    if (data != null && data.getData() != null) {
                        Uri uri = data.getData();
                        LogUtils.d(uri + "");
                        if (!TextUtils.isEmpty(uri.getAuthority())) {
                            //ʹ�� getAuthority ���ж�����
                            String[] proj = {MediaStore.Images.Media.DATA};
                            Cursor cursor = managedQuery(uri, proj, null, null, null);
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
                            album_path = cursor.getString(column_index);
                            LogUtils.d("album_path:"+album_path);
                        } else {
                            album_path = uri.getPath(); //С��ѡ����Ƭ���� data="file:///..."uri.getAuthority()=="";

                        }
                        //todo:
                        new ImageToBase64AsyncTask().execute(album_path);

                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_mine_personal_via:
                showVisaPopupWindow();
                break;
            case R.id.iv_actionbar_left:

                    if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(visa)
                            &&!TextUtils.isEmpty(provinceName)&&!TextUtils.isEmpty(cityName)&&!TextUtils.isEmpty(districtName)){

                        mapRequest.put("userid",userid);
                        mapRequest.put("token",token);
                        mapRequest.put("username",name);
                        mapRequest.put("trait",visa);
                        mapRequest.put("sex",sex+"");
                        mapRequest.put("grade",grade_position+"");
                        mapRequest.put("province",provinceName);
                        mapRequest.put("city",cityName);
                        mapRequest.put("country",districtName);

                        setUserInfoPresenter.setUserInfoMsg(mapRequest);
                }

                break;
            case R.id.rl_mine_personal_class:
                showGradePopupWindow(classArrays,selectGrade);
                break;
            case R.id.rb_mine_personal_man:
                sex = 1;
                break;
            case R.id.rb_mine_personal_woman:
                sex=2;
                break;
            case R.id.rl_mine_personal_region:
                //���Ĭ�ϵ����ã�����Ҫ�Լ�����
               /* CityConfig cityConfig = new CityConfig.Builder().build();
                mPicker.setConfig(cityConfig);*/
                CityConfig cityConfig = new CityConfig.Builder()
                        //.title("ѡ�����")//����
                        .titleTextSize(16)//�������ִ�С
                        .titleTextColor("#252525")//����������ɫ
                        .titleBackgroundColor("#E9E9E9")//����������ɫ
                        .confirTextColor("#FF7568")//ȷ�ϰ�ť������ɫ
                        //.confirmText("ok")//ȷ�ϰ�ť����
                        .confirmTextSize(16)//ȷ�ϰ�ť���ִ�С
                        .cancelTextColor("#252525")//ȡ����ť������ɫ
                        //.cancelText("cancel")//ȡ����ť����
                        .cancelTextSize(16)//ȡ����ť���ִ�С
                        .setCityWheelType(CityConfig.WheelType.PRO_CITY_DIS)//��ʾ�ֻ࣬��ʾʡ��һ������ʾʡ������������ʾʡ��������
                        .showBackground(true)//�Ƿ���ʾ��͸������
                        .visibleItemsCount(5)//��ʾitem������
                        .province(provinceName)//Ĭ����ʾ��ʡ��
                        .city(cityName)//Ĭ����ʾʡ������ĳ���
                        .district(districtName)//Ĭ����ʾʡ���������������
                        .provinceCyclic(true)//ʡ�ݹ����Ƿ����ѭ������
                        .cityCyclic(true)//���й����Ƿ����ѭ������
                        .districtCyclic(true)//���ع����Ƿ�ѭ������
                        .setCustomItemLayout(R.layout.item_city)//�Զ���item�Ĳ���
                        .setCustomItemTextViewId(R.id.item_city_name_tv)//�Զ���item���������textViewid
                        .drawShadows(true)//���ֲ���ʾģ��Ч��
                        //.setLineColor("#00000000")//�м���ߵ���ɫ
                        //.setLineHeigh(1)//�м���ߵĸ߶�
                        .setShowGAT(true)//�Ƿ���ʾ�۰�̨���ݣ�Ĭ�ϲ���ʾ
                        .build();

                //�����Զ������������
                mPicker.setConfig(cityConfig);
                //����ѡ�����¼������ؽ��
                mPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
                    @Override
                    public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {

                        //ʡ��
                        if (province != null) {
                            provinceName = province.getName();
                        }

                        //����
                        if (city != null) {
                            cityName = city.getName();
                        }

                        //����
                        if (district != null) {
                            districtName = district.getName();

                        }

                        if(!TextUtils.isEmpty(provinceName)&&!TextUtils.isEmpty(cityName)&&!TextUtils.isEmpty(districtName)){

                            tv_mine_personal_region_default.setText(provinceName+" "+cityName+" "+districtName);
                        }
                    }

                    @Override
                    public void onCancel() {
                        //ToastUtils.showLongToast(PersonalInformationActivity.this, "��ȡ��");
                    }
                });

                //��ʾ
                mPicker.showCityPicker( );
                break;
            case R.id.rl_mine_personal_name:
                new CommomDialog(PersonalInformationActivity.this, R.style.dialog, getResources().getString(R.string.notice_msg), new CommomDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm,String per_name) {
                        if(confirm){
                            if(!TextUtils.isEmpty(per_name)){
                                tv_mine_personal_name_default.setText(per_name);
                                name = per_name;
                            }
                            dialog.dismiss();
                        }

                    }
                }).setTitle(getResources().getString(R.string.per_name)).show();

                break;
        }
    }

    class poponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            darkenBackground(1f);

        }
    }
    private void darkenBackground(Float bgcolor) {
        WindowManager.LayoutParams lp = ((Activity) this).getWindow().getAttributes();
        lp.alpha = bgcolor;
        ((Activity) this).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ((Activity) this).getWindow().setAttributes(lp);

    }

    /**
     * ��ͼƬת����Base64������ַ���
     *
     * @param path
     * @return base64������ַ���
     */
    public String imageToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(path);
            //����һ���ַ�����С�����顣
            data = new byte[is.available()];
            //д������
            is.read(data);
            //��Ĭ�ϵı����ʽ���б���
            result = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    private class ImageToBase64AsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            return imageToBase64(strings[0]);
        }

        @Override
        protected void onPostExecute(String strings) {
            Gson g=new Gson();
            uploadPresenter.getUploadMsg(g.toJson(strings), "1", "1");
        }
    }
}
