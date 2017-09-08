package hr.ferit.kslovic.petsmissingorfound.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import hr.ferit.kslovic.petsmissingorfound.Activities.ChatActivity;
import hr.ferit.kslovic.petsmissingorfound.Models.Message;
import hr.ferit.kslovic.petsmissingorfound.R;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {
    ArrayList<Message> mMail;
    Context context;
    public InboxAdapter(ArrayList<Message> mail, Context context) {
        mMail = mail;
        this.context=context;
    }
    @Override
    public InboxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View mView = inflater.inflate(R.layout.chat_item, parent, false);
        ViewHolder mViewHolder = new ViewHolder(mView);
        return mViewHolder;
    }
    @Override
    public void onBindViewHolder(final InboxAdapter.ViewHolder holder, int position) {
        final Message mail = this.mMail.get(position);
        holder.tvMail.setText(mail.getEmail());
        holder.tvTime.setText(mail.getTime());
        holder.tvMessage.setText(mail.getText());
        holder.rvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Kristina",mail.getuPid());
                Intent intentMessage = new Intent(context, ChatActivity.class);
                intentMessage.putExtra("pUid",mail.getuPid());
                context.startActivity(intentMessage);
            }
        });
        if(!mail.getRead())
            holder.rvItem.setBackgroundColor(Color.GREEN);

    }
    @Override
    public int getItemCount() {
        return this.mMail.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMail, tvTime, tvMessage;
        RelativeLayout rvItem;
        public ViewHolder(View itemView) {
            super(itemView);
            this.tvMail = (TextView) itemView.findViewById(R.id.tvUser);
            this.tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            this.tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
            this.rvItem = (RelativeLayout) itemView.findViewById(R.id.rvItem);



        }


    }
}
