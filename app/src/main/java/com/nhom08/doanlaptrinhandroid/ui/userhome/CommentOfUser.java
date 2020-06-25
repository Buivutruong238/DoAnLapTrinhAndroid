package com.nhom08.doanlaptrinhandroid.ui.userhome;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.nhom08.doanlaptrinhandroid.BLL.Wp_comment_BLL;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_comment;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_user;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;
import com.nhom08.doanlaptrinhandroid.R;
import com.nhom08.doanlaptrinhandroid.adapter.Wp_commentsAdapter;

import java.util.ArrayList;

public class CommentOfUser extends Fragment {

    private ArrayList<Wp_comment> comments;
    private Wp_commentsAdapter adapter;

    public static Fragment newInstance(){
        CommentOfUser commentOfUser = new CommentOfUser();
        return commentOfUser;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_comment_of_user, container, false);
        Wp_comment_BLL wp_comment_bll = new Wp_comment_BLL();
        Wp_user userWasLogin = FunctionsStatic.newInstance().getUserWasLogin(root.getContext());
        String strUrl = String.format(getString(R.string.url_wp_comment_by_user), userWasLogin.getID());
        wp_comment_bll.toArrayWp_comment(strUrl, new OnMyFinishListener<ArrayList<Wp_comment>>() {
            @Override
            public void onFinish(ArrayList<Wp_comment> comments) {
                CommentOfUser.this.comments = comments;
                adapter = new Wp_commentsAdapter(root.getContext(), comments);
                ListView lvCommentOfUser = root.findViewById(R.id.lvCommentOfUser);
                lvCommentOfUser.setAdapter(adapter);
                lvCommentOfUser.setDividerHeight(20);
                lvCommentOfUser.setBackgroundColor(Color.WHITE);
                lvCommentOfUser.setOnItemClickListener(lvCommentOfUserClicked);
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                Toast.makeText(root.getContext(), R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    private AdapterView.OnItemClickListener lvCommentOfUserClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
            final AlertDialog.Builder alBuilder = new AlertDialog.Builder(parent.getContext());
            alBuilder.setTitle(R.string.chu_chon_tac_vu);
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
                                final AlertDialog processDialog = FunctionsStatic.createProcessDialog(parent.getContext());
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
                        FunctionsStatic.hienThiThongBaoDialog(parent.getContext(), getString(R.string.chu_thong_bao), getString(R.string.chu_de_thuc_hien_thao_tac_nay_ban_can_vao_website));
                    }
                }
            });
            alBuilder.create().show();
        }
    };
}