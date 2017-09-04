package hr.ferit.kslovic.petsmissingorfound.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hr.ferit.kslovic.petsmissingorfound.Activities.ProfileActivity;
import hr.ferit.kslovic.petsmissingorfound.Models.Pet;
import hr.ferit.kslovic.petsmissingorfound.Models.Users;
import hr.ferit.kslovic.petsmissingorfound.R;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    private ArrayList<Users> mUsers;
    Context context;
    String mActivity;
    public UserAdapter(ArrayList<Users> users, Context context, String activity) {
        mUsers = users;
        this.context=context;
        mActivity = activity;

    }
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View userView = inflater.inflate(R.layout.user_item, parent, false);
        UserAdapter.ViewHolder userViewHolder = new UserAdapter.ViewHolder(userView);
        return userViewHolder;
    }
    @Override
    public void onBindViewHolder(final UserAdapter.ViewHolder holder, int position) {
        final Users user = this.mUsers.get(position);
        holder.tvEmail.setText(user.getEmail());
        holder.tvPhone.setText(user.getPhone());
        if (mActivity.equals("admin")) {
            holder.rvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(context, view);

                    // This activity implements OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.open:
                                    Intent intent = new Intent(context, ProfileActivity.class);
                                    intent.putExtra("uid", user.getUid());
                                    context.startActivity(intent);
                                    return true;
                                case R.id.delete:
                                    deletePets(user.getUid());
                                    DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                                    deleteRef.removeValue();
                                    deleteAt(holder.getAdapterPosition());
                                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                                    if (fUser != null) {
                                        String uid = fUser.getUid();
                                        loadUser(uid, user);
                                    }

                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.inflate(R.menu.open_delete);
                    popup.show();
                            /*        Intent intent = new Intent(context, ProfileActivity.class);
                                    intent.putExtra("uid", user.getUid());
                                    context.startActivity(intent);*/
                }
            });
        }
    }

    private void deletePets(final String uid) {
        DatabaseReference petRef = FirebaseDatabase.getInstance().getReference("pets");
        petRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Pet pet = snapshot.getValue(Pet.class);

                    String pUid = pet.getUid();
                    if(pUid.equals(uid)) {
                        String pid = pet.getPid();
                        DatabaseReference deletePetRef = FirebaseDatabase.getInstance().getReference("pets").child(pid);
                        deletePetRef.removeValue();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
    }

    public void login(String email, String uPsw, final String aMail, final String aPsw) {
        Log.d("Kristina","login");
        FirebaseAuth.getInstance().signOut();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, uPsw)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Kristina", "signInWithEmail:failed", task.getException());
                            //Toast.makeText(EmailPasswordActivity.this, R.string.auth_failed,
                            //      Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Log.d("Kristina", "hhhsignInWithEmail:onComplete:" + task.isSuccessful());
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (user != null) {

                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("Kristina", "User account deleted.");
                                                    FirebaseAuth.getInstance().signOut();
                                                    FirebaseAuth.getInstance().signInWithEmailAndPassword(aMail, aPsw);
                                                }
                                            }
                                        });
                            }
                        }


                        // ...
                    }
                });
    }

    @Override
    public int getItemCount() {
        return this.mUsers.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEmail, tvPhone;
        public RelativeLayout rvItem;
        public ViewHolder(View itemView) {
            super(itemView);
            this.tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
            this.tvPhone = (TextView) itemView.findViewById(R.id.tvPhoneNum);
            this.rvItem = (RelativeLayout) itemView.findViewById(R.id.rvItem);

        }


    }
    public void deleteAt(int position) {
        this.mUsers.remove(position);
        this.notifyItemRemoved(position);

    }
    public void loadUser(String uid, final Users fuser){

        DatabaseReference mapRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mapRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("Kristina","loadUser");

                Users user =dataSnapshot.getValue(Users.class);
                String email = user.getEmail();
                String psw = user.getPsw();
                login(fuser.getEmail(),fuser.getPsw(),email,psw);



            }
            @Override
            public void onCancelled (DatabaseError error){
                // Failed to read value

            }
        });
    }

}
