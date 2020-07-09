package com.nhom08.doanlaptrinhandroid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.nhom08.doanlaptrinhandroid.BLL.UserLikePostBLL;
import com.nhom08.doanlaptrinhandroid.BLL.Wp_comment_BLL;
import com.nhom08.doanlaptrinhandroid.BLL.Wp_user_BLL;
import com.nhom08.doanlaptrinhandroid.DTO.UserLikePost;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_post;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_user;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;
import com.nhom08.doanlaptrinhandroid.Modulds.PostAndProgressBarAndTextView;
import com.nhom08.doanlaptrinhandroid.R;
import com.nhom08.doanlaptrinhandroid.ViewDetailPostActivity;

import java.util.ArrayList;

public class ModuleForDevAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Wp_post> posts, posts_tmp;
    private Context context;
    private LayoutInflater inflater;
    private Wp_user userWasLogin;
    private boolean isBusy;

    public ModuleForDevAdapter(Context context, ArrayList<Wp_post> posts){
        this.context = context;
        this.posts = posts;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        userWasLogin = FunctionsStatic.newInstance().getUserWasLogin(context);
        isBusy = false;
        posts_tmp = new ArrayList<>();
        posts_tmp.addAll(posts);
    }

    @Override
    public long getItemId(int position) {
        return posts.get(position).getID();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_item_moduls_for_dev, parent, false);

        return new newViewHolder(view);
    }

    //<important>

    //--------------- 1
    static class newViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout linearLayoutItem;
        private TextView tvTitle;
        private TextView tvPostDay;
        private ImageView imgAvatar;
        private TextView tvAuthor;
        private TextView tvCommentCount;
        private Button btnTangLike;
        private Button btnGiamLike;
        private ProgressBar progressBar;
        private TextView tvSoLuongLike;

        newViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutItem = itemView.findViewById(R.id.linearLayoutItemModuleForDev);
            tvTitle = itemView.findViewById(R.id.tvTitleItemModuleForDev);
            tvPostDay = itemView.findViewById(R.id.tvPostDayItemModuleForDev);
            imgAvatar = itemView.findViewById(R.id.imageViewAvatarItemModuleForDev);
            tvAuthor = itemView.findViewById(R.id.tvAuthorItemModuleForDev);
            tvCommentCount = itemView.findViewById(R.id.tvSoLuongComment);
            btnTangLike = itemView.findViewById(R.id.btnTangLikeItemModuleForDev);
            btnGiamLike = itemView.findViewById(R.id.btnGiamLikeItemModuleForDev);
            progressBar = itemView.findViewById(R.id.progressBarItemModuleForDev);
            tvSoLuongLike = itemView.findViewById(R.id.tvSoLuongLikeItemModuleForDev);
        }
    }

    //--------------- 2
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        newViewHolder newViewHolder = (newViewHolder)holder;
        Wp_post post = posts.get(position);

        newViewHolder.linearLayoutItem.setTag(post);
        newViewHolder.linearLayoutItem.setOnClickListener(linearClicked);

        newViewHolder.tvTitle.setText(post.getPost_title());

        newViewHolder.tvPostDay.setText(post.getPost_modified());

        Picasso.with(context).load(context.getString(R.string.url_image_user_male_50)).fit().into(newViewHolder.imgAvatar);

        newViewHolder.btnTangLike.setTag(new PostAndProgressBarAndTextView(post, newViewHolder.progressBar, newViewHolder.tvSoLuongLike));
        newViewHolder.btnTangLike.setOnClickListener(btnTangLikeClicked);

        newViewHolder.btnGiamLike.setTag(new PostAndProgressBarAndTextView(post, newViewHolder.progressBar, newViewHolder.tvSoLuongLike));
        newViewHolder.btnGiamLike.setOnClickListener(btnGiamLikeClicked);

        //load author;
        final TextView tvAuthor = newViewHolder.tvAuthor;
        final String author = "anonymous",
                strAPI = String.format(context.getString(R.string.url_wp_user_by_id), post.getPost_author());
        new Wp_user_BLL().toArrayWp_users(strAPI, new OnMyFinishListener<ArrayList<Wp_user>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish(ArrayList<Wp_user> result) {
                if (result.size() == 0)
                    tvAuthor.setText("@Hurier: "+author);
                else
                    tvAuthor.setText("@Hufier: "+result.get(0).getDisplay_name());
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                tvAuthor.setText("@HurierID: "+author);
            }
        });

        //load comment count
        final TextView tvCommentCount = newViewHolder.tvCommentCount;
        String strAPI1 = String.format(context.getString(R.string.url_count_comment_of_post), post.getID());
        new Wp_comment_BLL().countCommentOfPost(strAPI1, new OnMyFinishListener<Integer>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish(Integer count) {
                tvCommentCount.setText(context.getString(R.string.chu_so_luong_comment_bai_viet_nay)+count);
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                tvCommentCount.setText(R.string.chu_loi_ket_noi_toi_internet);
            }
        });

        //Load so luong like
        loadSoLuongLike(newViewHolder.tvSoLuongLike, post);
    }

    //--------------- 3
    private View.OnClickListener linearClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Wp_post post = (Wp_post)v.getTag();
            Intent intent = new Intent(context, ViewDetailPostActivity.class);
            intent.putExtra("wp_post_id", post.getID());
            intent.putExtra("wp_post_content", post.getPost_content());
            intent.putExtra("guid", post.getGuid());
            context.startActivity(intent);
        }
    };

    //</important>

    @SuppressLint("SetTextI18n")
    private void loadSoLuongLike(final TextView tvSoLuongLike, Wp_post post){
        String strAPI2 = String.format(context.getString(R.string.url_sum_like_post), post.getID());
        new UserLikePostBLL().getSumLike(strAPI2, new OnMyFinishListener<Integer>() {
            @Override
            public void onFinish(Integer sumLike) {
                if (sumLike > 0)
                    tvSoLuongLike.setText("+"+sumLike);
                else
                    tvSoLuongLike.setText(String.valueOf(sumLike));
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                tvSoLuongLike.setText("ERR");
            }
        });
    }

    private View.OnClickListener btnTangLikeClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isBusy){
                Toast.makeText(context, R.string.chu_tieng_trinh_dang_ban, Toast.LENGTH_SHORT).show();
                return;
            }
            if (userWasLogin == null){
                FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_can_dang_nhap_de_tiep_tuc));
                return;
            }

            isBusy = true;
            final PostAndProgressBarAndTextView postAndProgressBar = (PostAndProgressBarAndTextView)v.getTag();
            final Wp_post post = postAndProgressBar.getPost();
            final ProgressBar progressBar = postAndProgressBar.getProgressBar();
            progressBar.setVisibility(View.VISIBLE);
            String strAPI = String.format(context.getString(R.string.url_user_like_post), post.getID(), userWasLogin.getID());
            final UserLikePostBLL userLikePostBLL = new UserLikePostBLL();
            userLikePostBLL.getLike(strAPI, new OnMyFinishListener<Integer>() {
                @Override
                public void onFinish(Integer like) {
                    UserLikePost userLikePost = new UserLikePost();
                    userLikePost.setPost_id(post.getID());
                    userLikePost.setUser_id(userWasLogin.getID());

                    if (like == -2) { //user chua like -> insert
                        userLikePost.setLike(1);
                        userLikePostBLL.addOrUpdateUserLikePost(context.getString(R.string.url_insert_user_like_post), userLikePost, new OnMyFinishListener<Boolean>() {
                            @Override
                            public void onFinish(Boolean isLike) {
                                if (isLike) {
                                    loadSoLuongLike(postAndProgressBar.getTvSoLuongLike(), post);
                                }
                                else {
                                    FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_loi_ket_noi_toi_internet));
                                }
                                isBusy = false;
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Throwable error, Object bonusOfCoder) {
                                FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_loi_ket_noi_toi_internet));
                                isBusy = false;
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    else if (like == -1){//user dislike => update like
                        userLikePost.setLike(1);
                        userLikePostBLL.addOrUpdateUserLikePost(context.getString(R.string.url_update_like_post), userLikePost, new OnMyFinishListener<Boolean>() {
                            @Override
                            public void onFinish(Boolean isLike) {
                                if (isLike) {
                                    loadSoLuongLike(postAndProgressBar.getTvSoLuongLike(), post);
                                }
                                else {
                                    FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_loi_ket_noi_toi_internet));
                                }
                                isBusy = false;
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Throwable error, Object bonusOfCoder) {
                                FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_loi_ket_noi_toi_internet));
                                isBusy = false;
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    else{
                        FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_hanh_dong_khong_chap_nhan));
                        isBusy = false;
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(Throwable error, Object bonusOfCoder) {
                    FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_loi_ket_noi_toi_internet));
                    isBusy = false;
                    progressBar.setVisibility(View.GONE);
                }
            });

        }
    };

    private View.OnClickListener btnGiamLikeClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isBusy){
                Toast.makeText(context, R.string.chu_tieng_trinh_dang_ban, Toast.LENGTH_SHORT).show();
                return;
            }

            if (userWasLogin == null){
                FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_can_dang_nhap_de_tiep_tuc));
                return;
            }

            isBusy = true;
            final PostAndProgressBarAndTextView postAndProgressBar = (PostAndProgressBarAndTextView)v.getTag();
            final Wp_post post = postAndProgressBar.getPost();
            final ProgressBar progressBar = postAndProgressBar.getProgressBar();
            progressBar.setVisibility(View.VISIBLE);
            String strAPI = String.format(context.getString(R.string.url_user_like_post), post.getID(), userWasLogin.getID());
            final UserLikePostBLL userLikePostBLL = new UserLikePostBLL();
            userLikePostBLL.getLike(strAPI, new OnMyFinishListener<Integer>() {
                @Override
                public void onFinish(Integer like) {
                    UserLikePost userLikePost = new UserLikePost();
                    userLikePost.setPost_id(post.getID());
                    userLikePost.setUser_id(userWasLogin.getID());

                    if (like == -2) { //user chua like -> insert
                        userLikePost.setLike(-1);
                        userLikePostBLL.addOrUpdateUserLikePost(context.getString(R.string.url_insert_user_like_post), userLikePost, new OnMyFinishListener<Boolean>() {
                            @Override
                            public void onFinish(Boolean isLike) {
                                if (isLike) {
                                    loadSoLuongLike(postAndProgressBar.getTvSoLuongLike(), post);
                                }
                                else {
                                    FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_loi_ket_noi_toi_internet));
                                }
                                isBusy = false;
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Throwable error, Object bonusOfCoder) {
                                FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_loi_ket_noi_toi_internet));
                                isBusy = false;
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    else if (like == 1){//user like => update dislike
                        userLikePost.setLike(-1);
                        userLikePostBLL.addOrUpdateUserLikePost(context.getString(R.string.url_update_like_post), userLikePost, new OnMyFinishListener<Boolean>() {
                            @Override
                            public void onFinish(Boolean isLike) {
                                if (isLike) {
                                    loadSoLuongLike(postAndProgressBar.getTvSoLuongLike(), post);
                                }
                                else {
                                    FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_loi_ket_noi_toi_internet));
                                }
                                isBusy = false;
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Throwable error, Object bonusOfCoder) {
                                FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_loi_ket_noi_toi_internet));
                                isBusy = false;
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    else{
                        FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_hanh_dong_khong_chap_nhan));
                        isBusy = false;
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(Throwable error, Object bonusOfCoder) {
                    FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_loi_ket_noi_toi_internet));
                    isBusy = false;
                    progressBar.setVisibility(View.GONE);
                }
            });

        }
    };

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void filter(String query){
        posts.clear();
        if (query.isEmpty())
            posts.addAll(posts_tmp);
        else {
            String target;
            query = FunctionsStatic.VNCharacterToEnglishCharacter(query);
            for (final Wp_post post : posts_tmp) {
                //merge title and content of post => change it into English character.

                String stringBuilder = post.getPost_title() +
                        post.getPost_content();
                target = FunctionsStatic.VNCharacterToEnglishCharacter(stringBuilder);

                if (target.toLowerCase().contains(query.toLowerCase()))
                    posts.add(post);
            }
        }

        notifyDataSetChanged();
    }
}