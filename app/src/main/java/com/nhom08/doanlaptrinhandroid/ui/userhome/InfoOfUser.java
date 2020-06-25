package com.nhom08.doanlaptrinhandroid.ui.userhome;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_user;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;
import com.nhom08.doanlaptrinhandroid.R;

import java.util.HashMap;
import java.util.Map;

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

        lvInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                if (position == 2){
                    String strAPI = "http://192.168.56.1/android_app/url_update_test.php";
                    RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
                    StringRequest request = new StringRequest(Request.Method.POST, strAPI, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(view.getContext(), response, Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(view.getContext(), "Error Unknown!", Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<>();
                            map.put("post_id", "49");
                            map.put("user_id", "2");
                            map.put("like", "5");

                            return map;
                        }
                    };

                    requestQueue.add(request);
                }
            }
        });

        return root;
    }
}
