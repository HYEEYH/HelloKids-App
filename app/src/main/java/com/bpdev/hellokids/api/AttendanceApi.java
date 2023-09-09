package com.bpdev.hellokids.api;

import com.bpdev.hellokids.model.Attendance;
import com.bpdev.hellokids.model.AttendanceRes;
import com.bpdev.hellokids.model.AttendanceResList;
import com.bpdev.hellokids.model.Bus;
import com.bpdev.hellokids.model.BusInfoList;
import com.bpdev.hellokids.model.BusRes;
import com.bpdev.hellokids.model.ClassChildList;
import com.bpdev.hellokids.model.Result;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AttendanceApi {
    @GET("/attendance/class/children")
    Call<ClassChildList> classChildList(@Header("Authorization") String token); // 선생님이 속한 반의 반 정보와 원아 정보 조회

    @POST("/attendance/add/{childId}")
    Call<Result> attendanceAdd(@Path("childId") int childId, @Header("Authorization") String token, @Body Attendance attendance); // 출석 체크 생성

    @GET("/attendance/class/{id}")
    Call<AttendanceResList> attendanceList(@Path("id") int id, @Header("Authorization") String token); // 선생님이 속한 반의 반의 출석부 목록 조회

}
