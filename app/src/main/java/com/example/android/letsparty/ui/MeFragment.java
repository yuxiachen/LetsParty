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
import com.example.android.letsparty.adapter.CircleTransform;
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

        DatabaseReference databaseReference = firebaseDatabase.getReference(getString(R.string.db_user) + "/" + firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userUsernameTextView.setText(user.getUserName());
                userEmailTextView.setText("Email: " + user.getEmail());
                if(user.getProfileImageUrl() != null) {
                    Picasso.get().load(user.getProfileImageUrl())
                            .transform(new CircleTransform())
                            .into(userImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

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
                Intent intent = new Intent(getActivity(), MyFriendActivity.class);
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
