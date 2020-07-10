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

import com.nhom08.doanlaptrinhandroid.BLL.Wp_post_BLL;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_post;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_user;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;
import com.nhom08.doanlaptrinhandroid.R;
import com.nhom08.doanlaptrinhandroid.adapter.Wp_postsAdapter;

import java.util.ArrayList;

public class PostOfUser extends Fragment {

    private ArrayList<Wp_post> posts;
    private Wp_postsAdapter adapter;

    public static Fragment newInstance(){
        return new PostOfUser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_post_of_user, container, false);

        Wp_post_BLL wp_post_bll = new Wp_post_BLL();
        Wp_user userWasLogin = FunctionsStatic.newInstance().getUserWasLogin(root.getContext());
        String strUrl = String.format(getString(R.string.url_wp_post_by_user), userWasLogin.getID());
        wp_post_bll.toArrayWp_posts(strUrl, new OnMyFinishListener<ArrayList<Wp_post>>() {
            @Override
            public void onFinish(ArrayList<Wp_post> posts) {
                PostOfUser.this.posts = posts;
                adapter = new Wp_postsAdapter(root.getContext(), posts);
                ListView lvPostOfUser = root.findViewById(R.id.lvPostOfUser);
                lvPostOfUser.setAdapter(adapter);
                lvPostOfUser.setDividerHeight(20);
                lvPostOfUser.setBackgroundColor(Color.WHITE);
                lvPostOfUser.setOnItemClickListener(lvPostOfUserClicked);
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                Toast.makeText(root.getContext(), R.string.chu_loi_ket_noi_toi_internet, Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    private AdapterView.OnItemClickListener lvPostOfUserClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
            final AlertDialog.Builder alBuilder = new AlertDialog.Builder(parent.getContext());
            alBuilder.setTitle(R.string.chu_chon_tac_vu);
            alBuilder.setSingleChoiceItems(new String[]{getString(R.string.chu_xoa_bai_viet_nay), getString(R.string.chu_cap_nhat_bai_viet_nay)}, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogFather, int index) {
                    if (index == 0){
                        AlertDialog.Builder alBuilder2 = new AlertDialog.Builder(parent.getContext());
                        alBuilder2.setTitle(R.string.chu_can_than);
                        alBuilder2.setMessage(R.string.chu_ban_co_chac_muon_xoa);
                        alBuilder2.setPositiveButton(R.string.chu_dong_y, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                //Xoa Bai Viet
                                final AlertDialog processDialog = FunctionsStatic.createProcessDialog(parent.getContext());
                                FunctionsStatic.showDialog(processDialog);
                                String strUrl = getString(R.string.url_delete_wp_post);
                                int ID = (int)parent.getItemIdAtPosition(position);
                                new Wp_post_BLL().deleteWpPost(strUrl, ID, new OnMyFinishListener<Boolean>() {
                                    @Override
                                    public void onFinish(Boolean isDeleted) {
                                        if (isDeleted){
                                            posts.remove(position);
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