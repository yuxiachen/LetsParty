package com.example.android.letsparty.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.letsparty.R;
import com.example.android.letsparty.model.Event;

import java.util.ArrayList;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventItemViewHolder> implements View.OnClickListener {

    private OnEventItemClickedListener mListener;
    private ArrayList<Event> mEventList;
    private ArrayList<String> mEventKeys;

    public EventListAdapter(ArrayList<Event> events, ArrayList<String> keys, OnEventItemClickedListener listener){
        mEventList = events;
        mEventKeys = keys;
        mListener = listener;
    }

    @NonNull
    @Override
    public EventItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_event, viewGroup, false);
        EventItemViewHolder holder = new EventItemViewHolder(view);
        holder.itemView.setOnClickListener(this);
        return holder;
    }

    public void onBindViewHolder(EventItemViewHolder eventItemViewHolder, int i){
        Event currEvent = mEventList.get(i);
        eventItemViewHolder.updateUI(currEvent);
    }

    public int getItemCount(){
        return mEventList.size();
    }

    public void onClick(View v){
        int position = ((RecyclerView) v.getParent()).getChildAdapterPosition(v);
        mListener.OnEventItemClicked(mEventKeys.get(position));
    }

    public static class EventItemViewHolder extends RecyclerView.ViewHolder{
        private TextView eventTitleTextView;
        private TextView eventTimeTextView;
        private TextView eventLocationTextView;

        public EventItemViewHolder(View itemView){
            super(itemView);
            eventTitleTextView = itemView.findViewById(R.id.title_item_event);
            eventTimeTextView = itemView.findViewById(R.id.time_item_event);
            eventLocationTextView = itemView.findViewById(R.id.location_item_event);
        }
        public void updateUI(Event event){
            eventTitleTextView.setText(event.getTitle());
            eventTimeTextView.setText(event.getTime().toDate().toString());
            eventLocationTextView.setText(event.getLocation().getAddressLine());
        }
    }

    public interface OnEventItemClickedListener{
        void OnEventItemClicked(String key);
    }
}
