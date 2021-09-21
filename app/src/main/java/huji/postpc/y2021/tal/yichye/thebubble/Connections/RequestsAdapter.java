package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import huji.postpc.y2021.tal.yichye.thebubble.R;
import huji.postpc.y2021.tal.yichye.thebubble.TheBubbleApplication;

public class RequestsAdapter  extends RecyclerView.Adapter<RequestHolder> {

    private static final String TAG =  "ADAPTER";

    interface OnXClicked { void onClickedX(Request request); }
    public OnXClicked XclickedCallable = null;

    interface OnVClicked { void onClickedV(Request request); }
    public OnVClicked VclickedCallable = null;


    SharedPreferences sp = TheBubbleApplication.getInstance().getSP();
    ArrayList<Request> userRequests;


    public RequestsAdapter(){
        super();
    }


    public void setUserRequests(ArrayList<Request> requestsArr){
        userRequests = requestsArr;
        Collections.sort(userRequests);
        notifyDataSetChanged();
        }



    @NonNull
    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.bubble_in_item, parent, false);
        return new RequestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestHolder holder, int position) {
        Request request = userRequests.get(position);
        //todo set sender image
        holder.getReqUserName().setText(request.getReqUserName());

        if (request.inRequest){
            //todo set sender image
            holder.getCancelRequestButton().setVisibility(View.GONE);
            holder.getApproveIcon().setVisibility(View.VISIBLE);
            holder.getRejectIcon().setVisibility(View.VISIBLE);

            holder.getRejectIcon().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XclickedCallable.onClickedX(request);
                }
            });

            holder.getApproveIcon().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VclickedCallable.onClickedV(request);
                }
            });
        }

        else
        {
            holder.cancelRequestButton.setVisibility(View.VISIBLE);
            holder.approveIcon.setVisibility(View.GONE);
            holder.rejectIcon.setVisibility(View.GONE);

            holder.getCancelRequestButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XclickedCallable.onClickedX(request);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return userRequests.size();
    }
}
