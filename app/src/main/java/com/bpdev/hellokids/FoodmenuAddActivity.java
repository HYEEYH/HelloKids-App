package com.bpdev.hellokids;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bpdev.hellokids.api.FoodMenuApi;
import com.bpdev.hellokids.api.NetworkClient;
import com.bpdev.hellokids.api.SettingApi;
import com.bpdev.hellokids.config.Config;
import com.bpdev.hellokids.model.ClassList;
import com.bpdev.hellokids.model.NurseryClass;
import com.bpdev.hellokids.model.Result;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FoodmenuAddActivity extends AppCompatActivity {

    // 스피너, 반 이름
    ArrayList<NurseryClass> classArrayList = new ArrayList<>(); // api
    String[] classNameList = {};
    Spinner spinnerSelectClass;
    ArrayList<String> classNameArrayList = new ArrayList<>(); // 스피너에 넣어줄 반 목록
    ArrayAdapter<String> arrayAdapter;
    HashMap<String, Integer> map = new HashMap<>(); // 스피너 반이름 클릭 시 데이터의 id 반환
    int classId1;


    // 최상단 헤더 버튼
    TextView btnRegister;
    TextView btnLogin;
    ImageButton btnTranslate;


    // 하단 바로가기 메뉴 버튼
    Button btnBottomHome;
    Button btnBottomNotice;
    Button btnBottomDailyNote;
    Button btnBottomSchoolbus;
    Button btnBottomSetting;

    // 메인 파트 버튼
    Button btnAdd;
    Button btnSelectDate;
    Button btnSelectPhoto;
    EditText textContents;
    EditText textCategory;


    ImageView mealPhoto;
    String foodPhotoUrl;
    String foodDate;
    String foodContent;
    String foodType;
    Uri imgUri;
    File photoFile;
    DatePickerDialog datePickerDialog;
    String date1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodmenu_add);


        // 최상단 헤더 버튼 화면 연결
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnTranslate = findViewById(R.id.btnTranslate);

        // 하단 바로가기 메뉴 화면 연결
        btnBottomHome = findViewById(R.id.btnBottomHome);
        btnBottomNotice = findViewById(R.id.btnBottomNotice);
        btnBottomDailyNote = findViewById(R.id.btnBottomDailynote);
        btnBottomSchoolbus = findViewById(R.id.btnBottomSchoolbus);
        btnBottomSetting = findViewById(R.id.btnBottomSetting);

        // 메인 파트 화면 연결
        btnAdd = findViewById(R.id.btnMealAdd);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        textContents = findViewById(R.id.textInputTitle);
        textCategory = findViewById(R.id.textInputCategory);
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        mealPhoto = findViewById(R.id.mealPhotoSelect);




        // 달력
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                int year1 = calendar.get(Calendar.YEAR);
                int month1 = calendar.get(Calendar.MONTH);
                int day1 = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(FoodmenuAddActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year1, int month1, int day1) {

                                // 1월부터 시작하는데 시작이 0이므로 +1 해준다
                                month1 = month1 +1;

                                // 10 이하의 날짜가 03 이런식으로 나오게 표시 방법 바꾸기
                                String month;
                                if ( month1 < 10 ){
                                    month = "0" + month1;
                                }else{
                                    month = "" + month1; // 문자열로 만들기
                                }

                                String day;
                                if ( day1 < 10 ){
                                    day = "0" + day1;
                                }else{
                                    day = "" + day1; // 문자열로 만들기
                                }

                                date1 = ""+ year1 + "-" + month + "-" + day;

                                btnSelectDate.setText(date1);
                            }
                        },
                        year1, month1, day1);
                datePickerDialog.show();

            }
        });



        // 최상단 헤더 버튼
        // 회원가입 버튼
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodmenuAddActivity.this, RegisterSelectActivity.class);
                startActivity(intent);
            }
        });


        // 로그인 버튼
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodmenuAddActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // 번역 버튼


        // 하단 바로가기 메뉴 버튼
        // 홈 바로가기
        btnBottomHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodmenuAddActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        // 공지사항 바로가기
        btnBottomNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodmenuAddActivity.this, NoticeListActivity.class);
                startActivity(intent);
            }
        });


        // 알림장 바로가기
        btnBottomDailyNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodmenuAddActivity.this, DailynoteListActivity.class);
                startActivity(intent);
            }
        });


        // 안심등하원 바로가기
        btnBottomSchoolbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 선생님화면
                Intent intent = new Intent(FoodmenuAddActivity.this, SchoolbusListActivity.class);
                startActivity(intent);

                // 학부모화면
                // Intent intent = new Intent(MainActivity.this, SchoolbusParentListActivity.class);
                // startActivity(intent);
            }
        });


        // 설정 바로가기
        btnBottomSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodmenuAddActivity.this, SettingListActivity.class);
                startActivity(intent);
            }
        });




        // 메인 파트 동작
        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });



        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodContent = textContents.getText().toString().trim();
                foodType = textCategory.getText().toString().trim();
                foodDate = date1;

                // API호출
                Retrofit retrofit = NetworkClient.getRetrofitClient(FoodmenuAddActivity.this);
                FoodMenuApi foodMenuApi = retrofit.create(FoodMenuApi.class);
                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String token = sp.getString(Config.ACCESS_TOKEN,"");

                // 보낼 파일
                RequestBody fileBody = RequestBody.create(photoFile, MediaType.parse("image/jpg"));
                // 용량이 큰 파일은 잘게 쪼개서 보내는 작업
                MultipartBody.Part mealPhotoUrl = MultipartBody.Part.createFormData("mealPhotoUrl", photoFile.getName(), fileBody);
                RequestBody mealDate = RequestBody.create(foodDate, MediaType.parse("text/plain"));
                RequestBody mealContent = RequestBody.create(foodContent, MediaType.parse("text/plain"));
                RequestBody mealType = RequestBody.create(foodType, MediaType.parse("text/plain"));
                Call<Result> call = foodMenuApi.foodMenuAdd("Bearer "+token, mealDate, mealPhotoUrl, mealContent, mealType);


                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        if (response.isSuccessful()) {
                            Intent intent = new Intent(FoodmenuAddActivity.this, FoodmenuListActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (response.code() == 400) {

                        } else if (response.code() == 401) {

                        } else if (response.code() == 404) {

                        } else if (response.code() == 500) {

                        } else {
                            // 200OK 아닌경우
                        }
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        return;
                    }
                });
            }
        });
    }



    // 알러트 다이얼로그
    // alert_title 부분은 리소스폴더 - 벨류 - 스트링스 xml에서 복사해온다
    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(FoodmenuAddActivity.this);
        builder.setTitle(R.string.alert_title);
        builder.setItems(R.array.alert_photo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i==0){
                    camera();

                }else if(i==1){
                    album();
                }
            }
        });
        builder.show();
    }


    // 카메라 함수
    private void camera(){
        int permissionCheck = ContextCompat.checkSelfPermission(
                FoodmenuAddActivity.this, android.Manifest.permission.CAMERA);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FoodmenuAddActivity.this,
                    new String[]{android.Manifest.permission.CAMERA} ,
                    1000);
            Toast.makeText(FoodmenuAddActivity.this, "카메라 권한 필요합니다.",
                    Toast.LENGTH_SHORT).show();
            return;
        } else { // 권한 허가했다면
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(i.resolveActivity(FoodmenuAddActivity.this.getPackageManager())  != null  ){

                // 사진의 파일명을 만들기
                String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                photoFile = getPhotoFile(fileName);

                Uri fileProvider = FileProvider.getUriForFile(FoodmenuAddActivity.this,
                        // 메니페스트 파일에서 안드로이드:어쏘리티즈(authorities) = '' 의 내용과 아래 "" 부분이 같아야 함.
                        "com.bpdev.hellokids.fileprovider", photoFile);
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                startActivityForResult(i, 100);

            } else{ //카메라가 없다면
                Toast.makeText(FoodmenuAddActivity.this, "카메라 앱이 없습니다.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    // 사진 가져오는 함수
    private File getPhotoFile(String fileName) {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try{
            return File.createTempFile(fileName, ".jpg", storageDirectory);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    // 앨범 선택했을 때 실행
    // 체크 퍼미션 함수 가져 오기
    private void album(){
        if(checkPermission()){
            displayFileChoose();
        }else{
            displayFileChoose();
//            requestPermission();
        }
    }

    // 체크퍼미션 함수
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(FoodmenuAddActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_DENIED){
            return false;
        }else{
            return true;
        }
    }

    // 리퀘스트 퍼미션
    private void requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(FoodmenuAddActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Log.i("DEBUGGING5", "true");
            Toast.makeText(FoodmenuAddActivity.this, "권한 수락이 필요합니다.",
                    Toast.LENGTH_SHORT).show();
        }else{
            Log.i("DEBUGGING6", "false");
            ActivityCompat.requestPermissions(FoodmenuAddActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 500);
        }
    }

    private void displayFileChoose() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "SELECT IMAGE"), 300);
    }

    // 권한 설정 알림창
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(FoodmenuAddActivity.this, "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FoodmenuAddActivity.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();

                }
                break;
            }
            case 500: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(FoodmenuAddActivity.this, "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FoodmenuAddActivity.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();

                }

            }

        }
    }

    // 선택한 카메라 또는 앨범 보여주기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100 && resultCode == RESULT_OK){

            Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            ExifInterface exif = null;
            try {
                exif = new ExifInterface(photoFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            photo = rotateBitmap(photo, orientation);

            // 압축시킨다. 해상도 낮춰서
            OutputStream os;
            try {
                os = new FileOutputStream(photoFile);
                photo.compress(Bitmap.CompressFormat.JPEG, 50, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }

            photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            mealPhoto.setImageBitmap(photo);
            mealPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // 네트워크로 데이터 보낸다. ( -> 필요하면 이 자리에 쓴다)



            // 앨범 선택하면 다음 else 코드 실행
        }else if(requestCode == 300 && resultCode == RESULT_OK && data != null &&
                data.getData() != null){

            Uri albumUri = data.getData( );
            String fileName = getFileName( albumUri );

            try {

                ParcelFileDescriptor parcelFileDescriptor = getContentResolver( ).openFileDescriptor( albumUri, "r" );
                if ( parcelFileDescriptor == null ) return;

                FileInputStream inputStream = new FileInputStream( parcelFileDescriptor.getFileDescriptor( ) );

                photoFile = new File( this.getCacheDir( ), fileName );

                FileOutputStream outputStream = new FileOutputStream( photoFile );
                IOUtils.copyStream( inputStream, outputStream );

//                // 임시파일 생성
//                File file = createImgCacheFile( );
//                String cacheFilePath = file.getAbsolutePath( );

                // 압축시킨다. 해상도 낮춰서
                Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                OutputStream os;
                try {
                    os = new FileOutputStream(photoFile);
                    photo.compress(Bitmap.CompressFormat.JPEG, 60, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }

                mealPhoto.setImageBitmap(photo);
                mealPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                // imageView.setImageBitmap( getBitmapAlbum( imageView, albumUri ) );

            } catch ( Exception e ) {
                e.printStackTrace( );
            }
            // 네트워크로 보낸다.(-->네트워크로 보내는게 필요하다면 여기에 적으면 된다)
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 로테이트비트맵
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }


    //앨범에서 선택한 사진이름 가져오기
    public String getFileName( Uri uri ) {
        Cursor cursor = getContentResolver( ).query( uri, null, null, null, null );
        try {
            if ( cursor == null ) return null;
            cursor.moveToFirst( );
            @SuppressLint("Range") String fileName = cursor.getString( cursor.getColumnIndex( OpenableColumns.DISPLAY_NAME ) );
            cursor.close( );
            return fileName;

        } catch ( Exception e ) {
            e.printStackTrace( );
            cursor.close( );
            return null;
        }
    }


    // 다이얼로그
    Dialog dialog;
    void showProgress(){
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(this));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    void dismissProgress(){
        dialog.dismiss();
    }


}


