package com.bpdev.hellokids.api;

import com.bpdev.hellokids.model.ChildList;
import com.bpdev.hellokids.model.ClassList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface SettingApi {

    @GET("/setting/class/list")
    Call<ClassList> classListView(@Header("Authorization") String token); // 선생님이 속한 반 리스트 조회

    @GET("/setting/children/list")
    Call<ChildList> childListView(@Header("Authorization") String token); // 선생님이 속한 반의 원아 리스트 조회



}
