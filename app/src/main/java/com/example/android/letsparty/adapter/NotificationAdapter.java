package com.example.android.letsparty.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.letsparty.R;
import com.example.android.letsparty.model.Notification;
import com.example.android.letsparty.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.locks.Condition;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationItemViewHolder> implements View.OnClickListener{
    private List<Notification> notifications;
    private List<String> notificationKey;
    private OnNotificationItemClickedListener listener;

    public NotificationAdapter(List<Notification> notifications, List<String> notificationKey, OnNotificationItemClickedListener listener) {
        this.notifications = notifications;
        this.notificationKey = notificationKey;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        NotificationItemViewHolder holder = new NotificationItemViewHolder(view);
        holder.itemView.setOnClickListener(this);
        return holder;

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationItemViewHolder holder, int position) {
        Notification currNotification = notifications.get(position);
        holder.updateUi(currNotification);
    }

    @Override
    public int getItemCount() {
        return notificationKey.size();
    }

    @Override
    public void onClick(View v) {
        int pos = ((RecyclerView) v.getParent()).getChildAdapterPosition(v);
        listener.onNotificationItemClicked(notifications.get(pos), notificationKey.get(pos));
    }

    static class NotificationItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;
        private ImageView ivPhoto;

        NotificationItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            ivPhoto = itemView.findViewById(R.id.iv_item_img);
        }

        void updateUi(Notification notification) {
            String type = notification.getNotificationType();
            String imgUrl = null;
            String message = null;
            switch (type) {
                case Constants.FRIEND_REQUEST_NOTIFICATION:
                    message = notification.getSender().getUserName() + Constants.FRIEND_REQUEST_NOTIFICATION_MESSAGE;
                    if (notification.getSender().getProfileImageUrl() != null) imgUrl = notification.getSender().getProfileImageUrl();
                    break;
                case Constants.FRIEND_REQUEST_ACCEPTED_NOTIFICATION:
                    message = notification.getSender().getUserName() + Constants.FRIEND_REQUEST_ACCEPTED_NOTIFICATION_MESSAGE;
                    if (notification.getSender().getProfileImageUrl() != null) imgUrl = notification.getSender().getProfileImageUrl();
                    break;
                case Constants.EVENT_JOIN_NOTIFICATION:
                    message = notification.getSender().getUserName() + Constants.EVENT_JOIN_NOTIFICATION_MESSAGE + notification.getEvent().getTitle();
                    if (notification.getSender().getProfileImageUrl() != null) imgUrl = notification.getSender().getProfileImageUrl();
                    break;
                case Constants.EVENT_QUIT_NOTIFICATION:
                    message = notification.getSender().getUserName() + Constants.EVENT_QUIT_NOTIFICATION_MESSAGE + notification.getEvent().getTitle();
                    if (notification.getSender().getProfileImageUrl() != null) imgUrl = notification.getSender().getProfileImageUrl();
                    break;
                case Constants.EVENT_CANCEL_NOTIFICATION:
                    message = Constants.EVENT_PREFIX_NOTIFICATION_MESSAGE + notification.getEvent().getTitle() + Constants.EVENT_CANCEL_NOTIFICATION_MESSAGE;
                    if (notification.getEvent().getImgUrl() != null) imgUrl = notification.getEvent().getImgUrl();
                    break;
                case Constants.EVENT_SET_NOTIFICATION:
                    message = Constants.EVENT_PREFIX_NOTIFICATION_MESSAGE + notification.getEvent().getTitle() + Constants.EVENT_SET_NOTIFICATION_MESSAGE;
                    if (notification.getEvent().getImgUrl() != null) imgUrl = notification.getEvent().getImgUrl();
                    break;
                case Constants.EVENT_PENDING_NOTIFICATION:
                    message = Constants.EVENT_PREFIX_NOTIFICATION_MESSAGE + notification.getEvent().getTitle() + Constants.EVENT_PENDING_NOTIFICATION_MESSAGE;
                    if (notification.getEvent().getImgUrl() != null) imgUrl = notification.getEvent().getImgUrl();
                    break;
                case Constants.EVENT_INVITATION_NOTIFICATION:
                    message = notification.getSender().getUserName() + Constants.EVENT_INVITATION_NOTIFICATION_MESSAGE + notification.getEvent().getTitle();
                    if (notification.getSender().getProfileImageUrl() != null) imgUrl = notification.getSender().getProfileImageUrl();
                    break;
                default:
                    break;
            }
            tvMessage.setText(message);
            if (imgUrl != null) Picasso.get().load(imgUrl).transform(new CircleTransform()).into(ivPhoto);
        }
    }

    public interface OnNotificationItemClickedListener{
        void onNotificationItemClicked(Notification notification, String notificationKey);
    }
}

