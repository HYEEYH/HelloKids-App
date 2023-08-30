package com.bpdev.hellokids.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bpdev.hellokids.R;
import com.bpdev.hellokids.SchoolbusLocationActivity;
import com.bpdev.hellokids.model.BusDailyRecord;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.ViewHolder>{

    Context context;

    ArrayList<BusDailyRecord> busArrayList;

    /* 리스너 인터페이스 구현부 */
    public interface CheckBoxClickListener {
        void onClickCheckBox(int flag, int pos);
    }

    /* 체크박스 리스너 */
    private CheckBoxClickListener mCheckBoxClickListener;

    public BusAdapter(Context context, ArrayList<BusDailyRecord> busArrayList) {
        this.context = context;
        this.busArrayList = busArrayList;
    }

    @NonNull
    @Override
    public BusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_schoolbus,parent,false);
        return new BusAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusAdapter.ViewHolder holder, int position) {

        BusDailyRecord bus = busArrayList.get(position); // position는 해당 위치를 나타낸다

        holder.textBusName.setText(bus.getShuttleName());
        holder.textDriveStart.setText(bus.getShuttleStart());
        holder.textDriveEnd.setText(bus.getShuttleStop());

        holder.btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SchoolbusLocationActivity.class);
                context.startActivity(intent);
            }
        });

        /* 체크박스 리스너 */
        holder.checkBoxBus.setOnClickListener(v -> {
            if(holder.checkBoxBus.isChecked()) {
                // 체크가 되어 있음
                //mCheckBoxClickListener.onClickCheckBox(1, position);
                Toast.makeText(context, "짧게 출력 Hello World!", Toast.LENGTH_SHORT).show();
            }
            else {
                // 체크가 되어있지 않음
                mCheckBoxClickListener.onClickCheckBox(0, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return busArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textBusName;
        TextView textBusNum;
        TextView textBusTime;
        TextView textDriver;
        TextView textDriverNum;
        TextView textDriveStart;
        TextView textDriveOk;
        TextView textDriveEnd;
        CardView cardView;
        ImageView imgBus;
        Button btnLocation;
        CheckBox checkBoxBus;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);

            textBusName = itemView.findViewById(R.id.txtBusName);
            textBusNum = itemView.findViewById(R.id.txtBusNum);
//            textBusTime = itemView.findViewById(R.id.txtBusTime);
            textDriveStart = itemView.findViewById(R.id.txtDriveStart);
            textDriveEnd = itemView.findViewById(R.id.txtDriveEnd);
            textDriveOk = itemView.findViewById(R.id.txtDriveOk);

            imgBus = itemView.findViewById(R.id.imgBus);
            btnLocation = itemView.findViewById(R.id.btnLocation);
            checkBoxBus = itemView.findViewById(R.id.checkBoxBus);




        }
    }
    /* 리스너 메소드 */
    public void setOnCheckBoxClickListener(CheckBoxClickListener clickCheckBoxListener) {
        this.mCheckBoxClickListener = clickCheckBoxListener;
    }

}
