package hr.ferit.kslovic.petsmissingorfound.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import hr.ferit.kslovic.petsmissingorfound.Models.Message;
import hr.ferit.kslovic.petsmissingorfound.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    ArrayList<Message> mMessages;
    Context context;
    public ChatAdapter(ArrayList<Message> messages, Context context) {
        mMessages = messages;
        this.context=context;
    }
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View messageView = inflater.inflate(R.layout.chat_item, parent, false);
        ViewHolder messagesViewHolder = new ViewHolder(messageView);
        return messagesViewHolder;
    }
    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position) {
        Message message = this.mMessages.get(position);
        holder.tvUser.setText(message.getUname());
        holder.tvTime.setText(message.getTime());
        holder.tvMessage.setText(message.getText());

    }
    @Override
    public int getItemCount() {
        return this.mMessages.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUser, tvTime, tvMessage;
        public ViewHolder(View itemView) {
            super(itemView);
            this.tvUser = (TextView) itemView.findViewById(R.id.tvUser);
            this.tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            this.tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);

        }


    }
}
