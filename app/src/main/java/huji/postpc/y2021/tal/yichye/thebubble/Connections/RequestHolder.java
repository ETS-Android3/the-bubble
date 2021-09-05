package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import huji.postpc.y2021.tal.yichye.thebubble.R;

public class RequestHolder extends RecyclerView.ViewHolder {

    TextView requestSenderName;
    ImageView requestSenderImg;
    ImageView approveIcon;
    ImageView rejectIcon;
    Button cancelRequestButton;


    public RequestHolder(@NonNull View itemView) {
        super(itemView);
        requestSenderName = itemView.findViewById(R.id.userReqName);
        requestSenderImg = itemView.findViewById(R.id.userReqIcon);
        approveIcon = itemView.findViewById(R.id.approve);
        rejectIcon = itemView.findViewById(R.id.reject);
        cancelRequestButton = itemView.findViewById(R.id.cancelRequestButton);
    }

    public ImageView getApproveIcon() {
        return approveIcon;
    }

    public ImageView getRejectIcon() {
        return rejectIcon;
    }

    public ImageView getReqUserImg() {
        return requestSenderImg;
    }

    public TextView getReqUserName() {
        return requestSenderName;
    }

    public Button getCancelRequestButton() {
        return cancelRequestButton;
    }
}
