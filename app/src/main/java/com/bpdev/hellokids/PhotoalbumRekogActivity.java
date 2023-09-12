package com.bpdev.hellokids;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bpdev.hellokids.adapter.ScheduleAdapter;
import com.bpdev.hellokids.api.NetworkClient;
import com.bpdev.hellokids.api.SettingApi;
import com.bpdev.hellokids.config.Config;
import com.bpdev.hellokids.model.ClassList;
import com.bpdev.hellokids.model.NurseryClass;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PhotoalbumRekogActivity extends AppCompatActivity {

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
    Button btnPhotoAdd;
    Button btnSelectPhoto;
    Button btnRekog1;
    TextView textTitleShow;
    TextView textContentShow;
    ImageView imgPhotoAdd1;
    ImageView imgPhotoAdd2;


    // 작성일 선택
    DatePickerDialog datePickerDialog;
    String date1;

    // 앨범에서 사진 선택, 레트로핏 이미지 담기
    Bitmap photo;
    File photoUrl;
    File photoFile;
    int classIdTemp;


    // 스피너, 반 이름
    Spinner spinnerClass;
    ScheduleAdapter adapter;
    ArrayList<String> classNameArrayList = new ArrayList<>(); // 스피너에 넣어줄 반 목록
    ArrayList<NurseryClass> classArrayList = new ArrayList<>(); // api에 쓸 것
    ArrayAdapter<String> arrayAdapter;
    HashMap<String, Integer> map = new HashMap<>(); // 스피너에 들어가있는 반이름을 클릭하면 그 반이름을 가진 반 데이터의 id를 반환할 때 사용
    int classId1;


    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoalbum_rekog);


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
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        btnPhotoAdd = findViewById(R.id.btnPhotoAdd);
        btnRekog1 = findViewById(R.id.btnRekog1);
        textTitleShow = findViewById(R.id.textTitleShow);
        textContentShow = findViewById(R.id.textContentShow);
        imgPhotoAdd1 = findViewById(R.id.imgPhotoAdd1);
        imgPhotoAdd2 = findViewById(R.id.imgPhotoAdd2);

        // 스피너 연결
        spinnerClass = findViewById(R.id.spinnerClass);


        // -- -- -- 메인 파트 동작 -- -- -- //
        // 스피너
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classNameArrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 스피너에 반 이름 가져오기
        Retrofit retrofit = NetworkClient.getRetrofitClient(PhotoalbumRekogActivity.this);
        SettingApi api = retrofit.create(SettingApi.class);

        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString(Config.ACCESS_TOKEN, "");

        Call<ClassList> call = api.classListView("Bearer " + token);
        call.enqueue(new Callback<ClassList>() {
            @Override
            public void onResponse(Call<ClassList> call, Response<ClassList> response) {
                if (response.isSuccessful()) {
                    ClassList classList = response.body();
                    classArrayList.addAll(classList.getItems());

                    for (int i = 0; i < classArrayList.size(); i++) {
                        classNameArrayList.add(classArrayList.get(i).getClassName());
                        map.put(classArrayList.get(i).getClassName(), classArrayList.get(i).getId());
                        arrayAdapter.notifyDataSetChanged();
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<ClassList> call, Throwable t) {
            }
        });

        spinnerClass.setAdapter(arrayAdapter);

        spinnerClass.setSelection(0);



        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinnerValue = adapterView.getItemAtPosition(i).toString();
                spinnerClass.setSelection(i);
                Toast.makeText(getApplicationContext(), spinnerValue+"이 선택되었습니다.", Toast.LENGTH_SHORT).show();

                classId1 = map.get(spinnerValue);

                //String.valueOf(classId1);


                Log.i("classId", classId1 + "");


//                // 반별 일정표 리스트 조회  ------>>> 여기서부터 반 목록 가져오는걸로 바꾸면 된다.
//                Retrofit retrofit1 = NetworkClient.getRetrofitClient(PhotoalbumAddActivity.this);
//
//                ScheduleApi api1 = retrofit1.create(ScheduleApi.class);
//
//                SharedPreferences sp1 = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
//                String token1 = sp1.getString(Config.ACCESS_TOKEN, "");
//
//                Log.i("token1", token1);
//
//                Call<ScheduleList> call1 = api1.scheduleClassList(classId, "Bearer " + token1);
//                call1.enqueue(new Callback<ScheduleList>() {
//                    @Override
//                    public void onResponse(Call<ScheduleList> call, Response<ScheduleList> response) {
//                        if (response.isSuccessful()) {
//                            ScheduleList scheduleList1 = response.body();
//
//                            scheduleArrayList.addAll(scheduleList1.getItems());
//
//                            //Adapter를 이용해서 postInfo에 있는 내용을 가져와서 저장해둔 listView 형식에 맞게 띄움
//                            adapter = new ScheduleAdapter(PhotoalbumAddActivity.this, scheduleArrayList);
//                            recyclerView.setAdapter(adapter);
//                            scheduleArrayList = new ArrayList<>(); // 중복 방지 위한 초기화
//
//
//                        } else {
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ScheduleList> call, Throwable t) {
//
//                    }
//                });

            }



            public void onNothingSelected(AdapterView<?> adapterView) { // 아무것도 선택하지 않았을 때 실행되는건데 자동으로 선택이 되기때문에 이 코드가 실행되지 않는다

//                Retrofit retrofit2 = NetworkClient.getRetrofitClient(PhotoalbumAddActivity.this);
//
//                ScheduleApi api2 = retrofit2.create(ScheduleApi.class);
//
//                SharedPreferences sp2 = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
//                String token2 = sp2.getString(Config.ACCESS_TOKEN, "");
//
//                Log.i("token2", token2);
//
//                Call<ScheduleList> call2 = api2.scheduleList("Bearer " + token2);
//                call2.enqueue(new Callback<ScheduleList>() {
//                    @Override
//                    public void onResponse(Call<ScheduleList> call, Response<ScheduleList> response) {
//                        if (response.isSuccessful()) {
//                            ScheduleList scheduleList2 = response.body();
//
//                            Log.i("aaa2", scheduleList2.getResult());
//
//                            scheduleArrayList.addAll(scheduleList2.getItems());
//
//                            //Adapter를 이용해서 postInfo에 있는 내용을 가져와서 저장해둔 listView 형식에 맞게 띄움
//                            adapter = new ScheduleAdapter(PhotoalbumAddActivity.this, scheduleArrayList);
//
//                            recyclerView.setAdapter(adapter);
//                        } else {
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ScheduleList> call, Throwable t) {
//
//                    }
//                });

            }
        });


        // 등록하기 버튼
//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(PhotoalbumAddRekogActivity.this,PhotoalbumListActivity.class);
//                startActivity(intent);
//            }
//        });


        // 작성일 선택 버튼
        // - 달력 띄워서 선택 날짜 가져오기
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 달력 띄우기
                Calendar calendar = Calendar.getInstance();
                int year1 = calendar.get(Calendar.YEAR);
                int month1 = calendar.get(Calendar.MONTH);
                int day1 = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(PhotoalbumRekogActivity.this,
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



        // 원아 선택하기 버튼
        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        // 원아 프로필 사진 이미지 뷰에 나타내기
        // 이미지뷰를 클릭하면 리사이클러뷰가 나타나서 원아를 선택할 수 있음.

        //imgPhotoAdd1



        // 이미지 뷰 클릭해서 사진 선택
        imgPhotoAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //함수 호출
                showDialog();

                //
                imgPhotoAdd2.setImageBitmap(photo);
                imgPhotoAdd2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
        });


        
        
        
        // 얼굴인식 실행하기 버튼
        btnRekog1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
        
        
        



        // -- -- -- -- -- 공통 버튼 -- -- -- -- -- //
        // 최상단 헤더 버튼
        // 회원가입 버튼
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotoalbumRekogActivity.this,RegisterSelectActivity.class);
                startActivity(intent);
            }
        });


        // 로그인 버튼
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotoalbumRekogActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        // 번역 버튼




        // 하단 바로가기 메뉴 버튼
        // 홈 바로가기
        btnBottomHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotoalbumRekogActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        // 공지사항 바로가기
        btnBottomNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotoalbumRekogActivity.this, NoticeListActivity.class);
                startActivity(intent);
            }
        });


        // 알림장 바로가기
        btnBottomDailyNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotoalbumRekogActivity.this, DailynoteListActivity.class);
                startActivity(intent);
            }
        });


        // 안심등하원 바로가기
        btnBottomSchoolbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 선생님화면
                Intent intent = new Intent(PhotoalbumRekogActivity.this, SchoolbusListActivity.class);
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
                Intent intent = new Intent(PhotoalbumRekogActivity.this, SettingListActivity.class);
                startActivity(intent);
            }
        });


        // -- -- -- -- -- 공통 버튼  끝 -- -- -- -- -- //




    }



    // -- -- -- -- -- -- 앨범에서 사진 선택하기 위한 함수 -- -- -- -- -- -- //
    // 알러트 다이얼로그
    // alert_title 부분은 리소스폴더 - 벨류 - 스트링스 xml에서 복사해온다
    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoalbumRekogActivity.this);
        builder.setTitle(R.string.alert_title);
        builder.setItems(R.array.alert_photo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i==0){
                    // 첫번째 항목을 눌렀을 때
                    // 카메라로 사진찍기 실행

                    // 6.
                    camera();

                }else if(i==1){
                    // 두번째 항목을 눌렀을 때
                    // 앨범 선택 했을 때

                    // 7.
                    album();

                }
            }
        });
        builder.show();

    }





    // 카메라 함수
    private void camera(){
        int permissionCheck = ContextCompat.checkSelfPermission(
                PhotoalbumRekogActivity.this, android.Manifest.permission.CAMERA);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(PhotoalbumRekogActivity.this,
                    new String[]{android.Manifest.permission.CAMERA} ,
                    1000);
            Toast.makeText(PhotoalbumRekogActivity.this, "카메라 권한 필요합니다.",
                    Toast.LENGTH_SHORT).show();
            return;
        } else { // 권한 허가했다면
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(i.resolveActivity(PhotoalbumRekogActivity.this.getPackageManager())  != null  ){

                // 사진의 파일명을 만들기
                String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                photoFile = getPhotoFile(fileName);

                Uri fileProvider = FileProvider.getUriForFile(PhotoalbumRekogActivity.this,
                        // todo : 메니페스트파일에서 안드로이드:어쏘리티즈(authorities) = '' 의 내용과 아래 "" 부분이 같아야 함. + 추가함(김하연)
                        "com.bpdev.hellokids.fileprovider", photoFile);
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                startActivityForResult(i, 100);

            } else{ //카메라가 없다면
                Toast.makeText(PhotoalbumRekogActivity.this, "카메라 앱이 없습니다.",
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







    // 앨범 선택했을때 실행
    // 체크 퍼미션 함수 가져오기
    private void album(){
        if(checkPermission()){
            displayFileChoose();
        }else{
            requestPermission();
        }
    }






    // 체크퍼미션 함수
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(PhotoalbumRekogActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_DENIED){
            return false;
        }else{
            return true;
        }
    }





    // 리퀘스트 퍼미션
    private void requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(PhotoalbumRekogActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Log.i("DEBUGGING5", "true");
            Toast.makeText(PhotoalbumRekogActivity.this, "권한 수락이 필요합니다.",
                    Toast.LENGTH_SHORT).show();
        }else{
            Log.i("DEBUGGING6", "false");
            ActivityCompat.requestPermissions(PhotoalbumRekogActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 500);
        }
    }





    private void displayFileChoose() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "SELECT IMAGE"), 300);
    }






    // 권한 설정 알림창(권한 주겠습니까? 뜨는 창)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(PhotoalbumRekogActivity.this, "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PhotoalbumRekogActivity.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 500: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(PhotoalbumRekogActivity.this, "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PhotoalbumRekogActivity.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }

            }

        }
    }




    // 카메라앱을 실행했으면 카메라를, 또는 앨범을 선택했으면 앨범을 화면에 보여주기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100 && resultCode == RESULT_OK){

            // 원본 : Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

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

            imgPhotoAdd1.setImageBitmap(photo);
            imgPhotoAdd1.setScaleType(ImageView.ScaleType.CENTER_CROP);

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



//                //임시파일 생성
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

                // todo : 사진 여러장 화면에 보여주기 코드 추가하기

                imgPhotoAdd2.setImageBitmap(photo);
                imgPhotoAdd2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

//                imageView.setImageBitmap( getBitmapAlbum( imageView, albumUri ) );

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
    // 멤버변수
    Dialog dialog;

    //다이얼로그 띄우기
    //- 데이터베이스에 집어 넣는 동안 다른 행동 못 하도록 다이얼로그 띄워서 터치 막기
    void showProgress(){
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(this));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    //다이얼로그 없애기
    void dismissProgress(){
        dialog.dismiss();
    }
}