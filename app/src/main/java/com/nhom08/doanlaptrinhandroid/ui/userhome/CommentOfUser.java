package com.nhom08.doanlaptrinhandroid.ui.userhome;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nhom08.doanlaptrinhandroid.BLL.Wp_comment_BLL;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_comment;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_user;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;
import com.nhom08.doanlaptrinhandroid.R;
import com.nhom08.doanlaptrinhandroid.adapter.Wp_commentsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentOfUser extends Fragment {

    private ArrayList<Wp_comment> comments;
    private Wp_commentsAdapter adapter;
    private String strUrl;

    public static Fragment newInstance(){
        CommentOfUser commentOfUser = new CommentOfUser();
        return commentOfUser;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_comment_of_user, container, false);
        Wp_user userWasLogin = FunctionsStatic.newInstance().getUserWasLogin(root.getContext());
        strUrl = String.format(getString(R.string.url_wp_comment_by_user), userWasLogin.getID());

        processDialog = FunctionsStatic.createProcessDialog(getContext());
        lvCommentOfUser = root.findViewById(R.id.lvCommentOfUser);
        reLoad(getContext(), strUrl);

        return root;
    }

    private void reLoad(final Context context, String strUrl){
        Wp_comment_BLL wp_comment_bll = new Wp_comment_BLL();
        wp_comment_bll.toArrayWp_comment(strUrl, new OnMyFinishListener<ArrayList<Wp_comment>>() {
            @Override
            public void onFinish(ArrayList<Wp_comment> comments) {
                CommentOfUser.this.comments = comments;
                adapter = new Wp_commentsAdapter(context, comments);
                lvCommentOfUser.setAdapter(adapter);
                lvCommentOfUser.setDividerHeight(20);
                lvCommentOfUser.setBackgroundColor(Color.WHITE);
                lvCommentOfUser.setOnItemClickListener(lvCommentOfUserClicked);
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                Toast.makeText(getContext(), R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private AdapterView.OnItemClickListener lvCommentOfUserClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
            final AlertDialog.Builder alBuilder = new AlertDialog.Builder(parent.getContext());
            alBuilder.setTitle(R.string.chu_chon_tac_vu);

            final Wp_comment cmt = (Wp_comment) parent.getItemAtPosition(position);

            alBuilder.setSingleChoiceItems(new String[]{getString(R.string.chu_xoa_binh_luan_nay), getString(R.string.chu_cap_nhat_binh_luan_nay)}, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogFather, int index) {
                    if (index == 0){
                        AlertDialog.Builder alBuilder2 = new AlertDialog.Builder(parent.getContext());
                        alBuilder2.setTitle(R.string.chu_can_than);
                        alBuilder2.setMessage(R.string.chu_ban_co_chac_muon_xoa);
                        alBuilder2.setPositiveButton(R.string.chu_dong_y, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                //Xoa Comment.
                                FunctionsStatic.showDialog(processDialog);
                                String strUrl = getString(R.string.url_delete_wp_comment);
                                int comment_ID = (int)parent.getItemIdAtPosition(position);
                                new Wp_comment_BLL().deleteWpComment(strUrl, comment_ID, new OnMyFinishListener<Boolean>() {
                                    @Override
                                    public void onFinish(Boolean isDeleted) {
                                        if (isDeleted){
                                            comments.remove(position);
                                            adapter.notifyDataSetChanged();
                                            dialogFather.cancel();
                                        }else
                                            FunctionsStatic.hienThiThongBaoDialog(parent.getContext(), getString(R.string.chu_thong_bao), getString(R.string.chu_loi_ket_noi_toi_internet));
                                        dialog.cancel();
                                        FunctionsStatic.cancelDialog(processDialog);
                                    }

                                    @Override
                                    public void onError(Throwable error, Object bonusOfCoder) {
                                        FunctionsStatic.hienThiThongBaoDialog(parent.getContext(), getString(R.string.chu_thong_bao), getString(R.string.chu_loi_ket_noi_toi_internet));
                                        dialog.cancel();
                                        FunctionsStatic.cancelDialog(processDialog);
                                    }
                                });
                            }
                        });
                        alBuilder2.setNegativeButton(R.string.chu_thoat, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });alBuilder2.create().show();
                    }else{
                        //FunctionsStatic.hienThiThongBaoDialog(parent.getContext(), getString(R.string.chu_thong_bao), getString(R.string.chu_de_thuc_hien_thao_tac_nay_ban_can_vao_website));
                        final AlertDialog.Builder diaBuilder = new AlertDialog.Builder(parent.getContext());
                        LayoutInflater inflater = CommentOfUser.this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialog_update_comment_of_user,null);
                        diaBuilder.setView(dialogView);
                        edt_comment_of_user = (EditText) dialogView.findViewById(R.id.tV_comment);
                        btn_huy_update_cmt = dialogView.findViewById(R.id.btn_huy_update_cmt);
                        btn_update_cmt = dialogView.findViewById(R.id.btn_update_cmt);
                        //
                        edt_comment_of_user.setText(cmt.getComment_content());
                        //
                        final AlertDialog alertDialog = diaBuilder.create();
                        alertDialog.show();
                        btn_huy_update_cmt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.cancel();
                            }
                        });
                        btn_update_cmt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                final String update_cmt = edt_comment_of_user.getText().toString();
                                final int cmt_id = cmt.getComment_ID();

                                if(update_cmt.isEmpty()){
                                    FunctionsStatic.hienThiThongBaoDialog(v.getContext(), "Thông báo", "Không thể cập nhật thành rỗng!");
                                    return;
                                }
                                if(update_cmt.equals(cmt.getComment_content())){
                                    FunctionsStatic.hienThiThongBaoDialog(v.getContext(), "Thông báo", "Nội dung không có gì thay đổi!");
                                }
                                else { //thao tác hợp lệ -> thực hiện cập
                                    FunctionsStatic.showDialog(processDialog);
                                    RequestQueue requestQueue = Volley.newRequestQueue(v.getContext());
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.url_update_comment), new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            FunctionsStatic.cancelDialog(processDialog);
                                            //FunctionsStatic.hienThiThongBaoDialog(v.getContext(), "Thông báo", "Vui lòng restart lại để thấy thay đổi");
                                            if(response.equals("1")){
                                                reLoad(getContext(), strUrl);
                                                alertDialog.cancel();
                                                dialogFather.cancel();
                                            }
                                            else
                                                FunctionsStatic.hienThiThongBaoDialog(v.getContext(), "Thông báo","Khong thanh cong");

                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                            FunctionsStatic.cancelDialog(processDialog);
                                            Toast.makeText(v.getContext(), "Loi Update Comment ", Toast.LENGTH_SHORT).show();
                                        }
                                    }){
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> map = new HashMap<>();
                                            map.put("cID", cmt_id+"");
                                            map.put("cContent", update_cmt);
                                            //Log.i("cnt", update_cmt +" |"+ cmt_id );
                                            return map;
                                        }
                                    };
                                    requestQueue.add(stringRequest);
                                }
                            }
                        });
                    }
                }
            });
            alBuilder.create().show();
        }


    };

    private EditText edt_comment_of_user;
    private Button btn_update_cmt, btn_huy_update_cmt;
    private ListView lvCommentOfUser;
    private AlertDialog processDialog;

}