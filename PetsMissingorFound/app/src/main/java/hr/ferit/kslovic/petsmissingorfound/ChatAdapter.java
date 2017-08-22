package hr.ferit.kslovic.petsmissingorfound;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

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
    public void deleteAt(int position) {
        this.mMessages.remove(position);
        this.notifyItemRemoved(position);

    }
}
