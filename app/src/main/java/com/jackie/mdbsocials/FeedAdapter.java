/** Represents the Feed Adapter for the RecyclerView in FeedActivity.class.
 * @author: Jacqueline Zhang
 * @date: 02/24/2019
 */
package com.jackie.mdbsocials;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.SocialViewHolder> {
    private Context _context;
    private ArrayList<Social> _socials;

    public FeedAdapter(Context context, ArrayList<Social> socials) {
        _context = context;
        _socials = socials;
    }

    @Override
    public SocialViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(_context).inflate(R.layout.social_row, viewGroup, false);
        return new SocialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SocialViewHolder socialViewHolder, int i) {
        socialViewHolder.bind(i);
    }

    @Override
    public int getItemCount() {
        return _socials.size();
    }

    class SocialViewHolder extends RecyclerView.ViewHolder {
        private ImageView _eventImg;
        private TextView _eventName;
        private TextView _organizer;
        private TextView _interested;

        public SocialViewHolder(@NonNull View itemView) {
            super(itemView);
            _eventImg = itemView.findViewById(R.id.eventImg);
            _eventName = itemView.findViewById(R.id.eventNameTitle);
            _organizer = itemView.findViewById(R.id.organizerText);
            _interested = itemView.findViewById(R.id.interestedText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailActivity.class);
                    Social clickedSocial = _socials.get(getAdapterPosition());
                    intent.putExtra("Social", clickedSocial);
                    v.getContext().startActivity(intent);
                    context.startActivity(intent);
                }
            });

        }

        void bind(int position) {
            Social currSocial = _socials.get(position);
            String eventName = currSocial.getEventName();
            int interestedCount = currSocial.getInterested();
            String organizer = currSocial.getEmail();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imgRef = storageRef.child(currSocial.getUID() + ".jpg");

            _eventName.setText(eventName);
            _interested.setText(interestedCount + " interested");
            _organizer.setText("Organizer: " + organizer);

            // https://github.com/firebase/FirebaseUI-Android/tree/master/storage
            // https://stackoverflow.com/questions/46652380/getting-image-from-firebase-storage-using-glide
            Glide.with(_context).load(imgRef).centerCrop().into(_eventImg);


        }
    }

}
