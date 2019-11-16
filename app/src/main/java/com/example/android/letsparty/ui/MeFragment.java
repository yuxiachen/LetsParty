package com.example.android.letsparty.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.android.letsparty.R;
import com.example.android.letsparty.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MeFragment extends Fragment {
    private Button logOut;
    private View view;
    private TextView userUsernameTextView, userEmailTextView;
    private ImageView userImageView;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private ImageView profile_label_image, password_label_image, friends_label_image;
    private TextView editProfile_item_user, changePwd_item_user, friends_item_user;
    private CardView profile_card_view, password_card_view, friends_card_view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, null);

        userUsernameTextView = view.findViewById(R.id.username_item_user);
        userEmailTextView = view.findViewById(R.id.email_item_user);
        userImageView = view.findViewById(R.id.imageView_item_user);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = firebaseDatabase.getReference("users/"+firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userUsernameTextView.setText("Username: " + user.getUserName());
                userEmailTextView.setText("Email: " + user.getEmail());
                if(user.getProfileImageUrl()==null){
                    userImageView.setImageResource(R.drawable.default_profile_logo);
                }
                else{
                    Picasso.get().load(user.getProfileImageUrl())
                            .fit()
                            .into(userImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        editProfile_item_user = view.findViewById(R.id.editProfile_item_user);
        changePwd_item_user = view.findViewById(R.id.changePwd_item_user);
        friends_item_user = view.findViewById(R.id.friends_item_user);
        profile_label_image = view.findViewById(R.id.profile_label_image);
        password_label_image = view.findViewById(R.id.password_label_image);
        friends_label_image = view.findViewById(R.id.friends_label_image);

        editProfile_item_user.setText("Edit Profile");
        changePwd_item_user.setText("Change Password");
        friends_item_user.setText("Friends");
        profile_label_image.setImageResource(R.drawable.profile_logo);
        password_label_image.setImageResource(R.drawable.key_logo);
        friends_label_image.setImageResource(R.drawable.friends_logo);

        profile_card_view = view.findViewById(R.id.profile_card_view);
        profile_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
        password_card_view = view.findViewById(R.id.password_card_view);
        password_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PasswordResetActivity.class);
                startActivity(intent);
            }
        });
        friends_card_view = view.findViewById(R.id.friends_card_view);
        friends_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendActivity.class);
                startActivity(intent);
            }
        });

        logOut = (Button) view.findViewById(R.id.btn_logout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LogInActivity.class));
            }
        });

        return view;
    }


}
