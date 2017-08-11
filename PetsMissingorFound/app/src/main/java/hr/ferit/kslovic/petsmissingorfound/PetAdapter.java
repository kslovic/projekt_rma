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

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.ViewHolder> {
    ArrayList<PetAdd> mPets;
    Context context;
    public PetAdapter(ArrayList<PetAdd> pets, Context context) {
        mPets = pets;
        this.context=context;
    }
    @Override
    public PetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View petView = inflater.inflate(R.layout.item_add, parent, false);
        ViewHolder petViewHolder = new ViewHolder(petView);
        return petViewHolder;
    }
    @Override
    public void onBindViewHolder(final PetAdapter.ViewHolder holder, int position) {
        final PetAdd pet = this.mPets.get(position);
        holder.tvPetName.setText(pet.getpName());
        holder.tvBreed.setText(pet.getpBreed());
        holder.tvStatus.setText(pet.getpStatus());
        holder.rvItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in
                    String uid = user.getUid();
                    DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference("pets").child(uid).child(pet.getPid());
                    deleteRef.removeValue();

                }
                else {
                    //redirect
                }
                deleteAt(holder.getAdapterPosition());
                return false;
            }
        });
        if(pet.getpPic()!=null)
        Glide.with(context).load(pet.getpPic()).into(holder.ivPetAdd);
        Log.d("Kristina", pet.getpName() + pet.getpBreed() + pet.getpStatus() +pet.getpPic() );

    }
    @Override
    public int getItemCount() {
        return this.mPets.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPetName, tvBreed, tvStatus;
        public RelativeLayout rvItem;
        ImageView ivPetAdd;
        public ViewHolder(View itemView) {
            super(itemView);
            this.tvPetName = (TextView) itemView.findViewById(R.id.tvPetName);
            this.tvBreed = (TextView) itemView.findViewById(R.id.tvBreed);
            this.tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            this.ivPetAdd = (ImageView) itemView.findViewById(R.id.ivPetAdd);
            this.rvItem = (RelativeLayout) itemView.findViewById(R.id.rvItem);

        }


    }
    public void deleteAt(int position) {
        this.mPets.remove(position);
        this.notifyItemRemoved(position);

    }
}
