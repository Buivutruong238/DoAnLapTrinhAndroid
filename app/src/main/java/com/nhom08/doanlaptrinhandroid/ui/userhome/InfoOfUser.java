package com.nhom08.doanlaptrinhandroid.ui.userhome;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_user;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;
import com.nhom08.doanlaptrinhandroid.R;

public class InfoOfUser extends Fragment {

    public static Fragment newInstance(){
        InfoOfUser infoOfUser = new InfoOfUser();
        return infoOfUser;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragmemt_info_of_user, container, false);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(root.getContext(), android.R.layout.simple_list_item_single_choice);
        Wp_user userWasLogin = FunctionsStatic.newInstance().getUserWasLogin(root.getContext());
        adapter.add(String.format("ID: %d", userWasLogin.getID()));
        adapter.add(String.format("USER NAME: %s", userWasLogin.getUser_login()));
        adapter.add(String.format("FULL NAME: %s", userWasLogin.getDisplay_name()));
        adapter.add(String.format("EMAIL ADDRESS: %s", userWasLogin.getUser_email()));
        adapter.add(String.format("USER URL: %s", userWasLogin.getUser_url() == null || userWasLogin.getUser_url().isEmpty() ? "Not Found" : userWasLogin.getUser_url()));
        adapter.add(String.format("PASSWORD: %s", userWasLogin.getUser_pass()));
        ListView lvInfo = root.findViewById(R.id.lvInfoOfUser);
        lvInfo.setAdapter(adapter);
        lvInfo.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        return root;
    }
}
