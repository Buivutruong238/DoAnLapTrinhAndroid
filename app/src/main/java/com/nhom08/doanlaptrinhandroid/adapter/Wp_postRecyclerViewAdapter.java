package com.nhom08.doanlaptrinhandroid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Wp_postRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Wp_post> wp_posts, wp_posts_tmp;
    private boolean isBusy;
    private Wp_user userWasLogin;

    public Wp_postRecyclerViewAdapter(List<Wp_post> wp_posts){
        this.wp_posts = wp_posts;
        isBusy = false;
        wp_posts_tmp =  new ArrayList<>();
        wp_posts_tmp.addAll(wp_posts);
    }

    public static class VH extends RecyclerView.ViewHolder{
        TextView tvTitlePost, tvAuthor, tvPostDay, tvContent, tvSoLuongLike, tvSoLuongComment;
        ImageView imgHinh, imgAvatar;
        ImageView btnTangLike, btnGiamLike;
        ProgressBar progressBar;
        Button btnXemThem;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitlePost = itemView.findViewById(R.id.tvTitleItemWpPostRecy);
            imgHinh = itemView.findViewById(R.id.imgHinhItemWpPostRecy);
            imgAvatar = itemView.findViewById(R.id.imageViewAvatarPostItemRecy);
            tvAuthor = itemView.findViewById(R.id.tvAuthorPostItemRecy);
            tvPostDay = itemView.findViewById(R.id.tvPostDayItemRecy);
            tvContent = itemView.findViewById(R.id.tvContentItemWpPostRecy);
            btnTangLike = itemView.findViewById(R.id.imageButtonLikeRecy);
            btnGiamLike = itemView.findViewById(R.id.imageButtonDisLikeRecy);
            tvSoLuongLike = itemView.findViewById(R.id.tvSoLuongLikeRecy);
            progressBar = itemView.findViewById(R.id.progressBarItemRecy);
            tvSoLuongComment = itemView.findViewById(R.id.tvSoLuongCommentRecy);
            btnXemThem = itemView.findViewById(R.id.btnDocThemRecy);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_wp_post_item_recyclerview, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VH vh = (VH)holder;
        Wp_post post = wp_posts.get(position);
        vh.tvTitlePost.setText(post.getPost_title().toUpperCase());
        Context context = vh.tvTitlePost.getContext();

        loadHinhDaiDienPost(context, vh.imgHinh, post);

        Picasso.with(context).load(context.getString(R.string.url_image_user_male_50)).fit().into(vh.imgAvatar);

        vh.tvPostDay.setText(post.getPost_modified());

        loadAuthor(context, vh.tvAuthor, post);

        loadContent(vh.tvContent, post);

        vh.btnTangLike.setTag(new PostAndProgressBarAndTextView(post, vh.progressBar, vh.tvSoLuongLike));
        vh.btnTangLike.setOnClickListener(btnTangLikeClicked);

        vh.btnGiamLike.setTag(new PostAndProgressBarAndTextView(post, vh.progressBar, vh.tvSoLuongLike));
        vh.btnGiamLike.setOnClickListener(btnGiamLikeClicked);

        //Load so luong like
        loadSoLuongLike(vh.tvSoLuongLike, post);

        loadCommentCount(context, vh.tvSoLuongComment, post);

        vh.btnXemThem.setTag(post);
        vh.btnXemThem.setOnClickListener(btnXemThemClicked);
    }

    //region event
    private View.OnClickListener btnXemThemClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Wp_post post = (Wp_post)v.getTag();
            Intent intent = new Intent(v.getContext(), ViewDetailPostActivity.class);
            intent.putExtra("wp_post_id", post.getID());
            intent.putExtra("wp_post_content", post.getPost_content());
            intent.putExtra("guid", post.getGuid());
            v.getContext().startActivity(intent);
        }
    };

    private View.OnClickListener btnTangLikeClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Context context = v.getContext();
            if (isBusy){
                Toast.makeText(context, R.string.chu_tieng_trinh_dang_ban, Toast.LENGTH_SHORT).show();
                return;
            }

            userWasLogin = FunctionsStatic.newInstance().getUserWasLogin(context);
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
                                    isBusy = false;
                                    progressBar.setVisibility(View.GONE);
                                }
                                else {
                                    FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_loi_ket_noi_toi_internet));
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

                    else if (like == -1){//user dislike => update like
                        userLikePost.setLike(1);
                        userLikePostBLL.addOrUpdateUserLikePost(context.getString(R.string.url_update_like_post), userLikePost, new OnMyFinishListener<Boolean>() {
                            @Override
                            public void onFinish(Boolean isLike) {
                                if (isLike) {
                                    loadSoLuongLike(postAndProgressBar.getTvSoLuongLike(), post);
                                    isBusy = false;
                                    progressBar.setVisibility(View.GONE);
                                }
                                else {
                                    FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_loi_ket_noi_toi_internet));
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
            final Context context = v.getContext();
            if (isBusy){
                Toast.makeText(context, R.string.chu_tieng_trinh_dang_ban, Toast.LENGTH_SHORT).show();
                return;
            }

            userWasLogin = FunctionsStatic.newInstance().getUserWasLogin(context);
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
                                    isBusy = false;
                                    progressBar.setVisibility(View.GONE);
                                }
                                else {
                                    FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_loi_ket_noi_toi_internet));
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

                    else if (like == 1){//user like => update dislike
                        userLikePost.setLike(-1);
                        userLikePostBLL.addOrUpdateUserLikePost(context.getString(R.string.url_update_like_post), userLikePost, new OnMyFinishListener<Boolean>() {
                            @Override
                            public void onFinish(Boolean isLike) {
                                if (isLike) {
                                    loadSoLuongLike(postAndProgressBar.getTvSoLuongLike(), post);
                                    isBusy = false;
                                    progressBar.setVisibility(View.GONE);
                                }
                                else {
                                    FunctionsStatic.hienThiThongBaoDialog(context, context.getString(R.string.chu_thong_bao), context.getString(R.string.chu_loi_ket_noi_toi_internet));
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
    //endregion

    //region Support Method
    private void loadCommentCount(final Context context, final TextView tvCommentCount, Wp_post post){
        String strAPI1 = String.format(context.getString(R.string.url_count_comment_of_post), post.getID());
        new Wp_comment_BLL().countCommentOfPost(strAPI1, new OnMyFinishListener<Integer>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish(Integer count) {
                tvCommentCount.setText(context.getString(R.string.chu_so_luong_comment_bai_viet_nay) +" "+count);
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                tvCommentCount.setText(R.string.chu_loi_ket_noi_toi_internet);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void loadSoLuongLike(final TextView tvSoLuongLike, Wp_post post){
        String strAPI2 = String.format(tvSoLuongLike.getContext().getString(R.string.url_sum_like_post), post.getID());
        new UserLikePostBLL().getSumLike(strAPI2, new OnMyFinishListener<Integer>() {
            @Override
            public void onFinish(Integer sumLike) {
                if (sumLike > 0)
                    tvSoLuongLike.setText(tvSoLuongLike.getContext().getString(R.string.chu_thich)+" +"+sumLike);
                else
                    tvSoLuongLike.setText(tvSoLuongLike.getContext().getString(R.string.chu_thich)+" "+String.valueOf(sumLike));
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                tvSoLuongLike.setText("ERR");
            }
        });
    }

    private void loadContent(final TextView tvContent, Wp_post wp_post){
        FunctionsStatic.getValueOfTagHTML_params(wp_post.getPost_content(), new OnMyFinishListener<String>() {
            @Override
            public void onFinish(String result) {
                if (result != null && result.length() > 400) {
                    result = result.substring(0, 400);
                    result += " ... ";
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tvContent.setText(Html.fromHtml(result, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    tvContent.setText(Html.fromHtml(result));
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                tvContent.setText("CONTENT_NOT_FOUND!");
            }
        }, "h1","h2","h3","h4","h5","h6","p");
    }

    private void loadAuthor(Context context, final TextView tvAuthor, Wp_post wp_post){
        final String author = "anonymous",
                strAPI = String.format(context.getString(R.string.url_wp_user_by_id), wp_post.getPost_author());
        new Wp_user_BLL().toArrayWp_users(strAPI, new OnMyFinishListener<ArrayList<Wp_user>>() {
            @Override
            public void onFinish(ArrayList<Wp_user> result) {
                if (result.size() == 0)
                    tvAuthor.setText("@Hurier: "+author);
                else
                    tvAuthor.setText("@Hufier: "+result.get(0).getDisplay_name());
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                tvAuthor.setText("@HurierID: "+author);
            }
        });
    }

    private void loadHinhDaiDienPost(final Context context, final ImageView imgHinh, final Wp_post post_current){
        FunctionsStatic.getImage(post_current.getPost_content(), new OnMyFinishListener<String>() {
            @Override
            public void onFinish(String result) {
                if (result.isEmpty())
                    Picasso.with(context).load(context.getString(R.string.url_image_notFound)).fit().into(imgHinh);
                else
                    Picasso.with(context).load(result).resize(400, 200).centerCrop().into(imgHinh);
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                FunctionsStatic.getTermID_from_wp_post_id(context.getString(R.string.url_get_termID_from_wp_post)+post_current.getID(), new OnMyFinishListener<Integer>() {
                    @Override
                    public void onFinish(Integer result) {
                        if (result == -1)
                            Picasso.with(context).load(context.getString(R.string.url_image_notFound)).fit().into(imgHinh);
                        else{
                            switch (result){
                                case 42: Picasso.with(context).load(context.getString(R.string.url_image_logo_module_for_dev)).fit().into(imgHinh); break;
                                case 3: Picasso.with(context).load(context.getString(R.string.url_image_chuyenmuc_hufiexam)).fit().into(imgHinh); break;
                                case 4: Picasso.with(context).load(context.getString(R.string.url_image_chuyenmuc_khoahoc)).fit().into(imgHinh); break;
                                case 5:
                                case 30:
                                    Picasso.with(context).load(context.getString(R.string.url_image_chuyenmuc_0xdhth)).fit().into(imgHinh); break;
                                default: Picasso.with(context).load(context.getString(R.string.url_image_notFound)).fit().into(imgHinh); break;
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable error, Object bonusOfCoder) {
                        Picasso.with(context).load(context.getString(R.string.url_image_notFound)).fit().into(imgHinh);
                    }
                });
            }
        });
    }
    //endregion

    @Override
    public int getItemCount() {
        return wp_posts.size();
    }

    /*
        Loc bài viết theo một đoạn text được truyền vào.
     */
    public void filter(String query){
        wp_posts.clear();
        if (query.isEmpty())
            wp_posts.addAll(wp_posts_tmp);
        else {
            String target = "";
            query = FunctionsStatic.VNCharacterToEnglishCharacter(query);
            for (final Wp_post post : wp_posts_tmp) {
                //merge title and content of post => change it into English character.

                String stringBuilder = post.getPost_title() +
                        post.getPost_content();
                target = FunctionsStatic.VNCharacterToEnglishCharacter(stringBuilder);

                if (target.toLowerCase().contains(query.toLowerCase()))
                    wp_posts.add(post);
            }
        }

        notifyDataSetChanged();
    }
}