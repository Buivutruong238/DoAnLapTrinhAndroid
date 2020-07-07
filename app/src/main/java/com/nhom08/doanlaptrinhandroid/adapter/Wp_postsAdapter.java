package com.nhom08.doanlaptrinhandroid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.Html;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.nhom08.doanlaptrinhandroid.BLL.Wp_user_BLL;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_post;
import com.nhom08.doanlaptrinhandroid.DTO.Wp_user;
import com.nhom08.doanlaptrinhandroid.Interface_enum.OnMyFinishListener;
import com.nhom08.doanlaptrinhandroid.Modulds.FunctionsStatic;
import com.nhom08.doanlaptrinhandroid.R;

import java.util.ArrayList;

public class Wp_postsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Wp_post> wp_posts, wp_posts_tmp;
    private LayoutInflater inflater;

    public Wp_postsAdapter(Context context, ArrayList<Wp_post> wp_posts){
        this.context = context;
        this.wp_posts = wp_posts;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wp_posts_tmp =  new ArrayList<>();
        wp_posts_tmp.addAll(wp_posts);
    }

    @Override
    public int getCount() {
        return wp_posts.size();
    }

    @Override
    public Object getItem(int position) {
        return wp_posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return wp_posts.get(position).getID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.layout_wp_post_item_listview, parent, false);
        final Wp_post wp_post = wp_posts.get(position);
        TextView tvTitle = convertView.findViewById(R.id.tvTitleItemWpPostListView);
        tvTitle.setText(wp_posts.get(position).getPost_title().toUpperCase());
        final ImageView imgHinh = convertView.findViewById(R.id.imgHinhItemWpPostListView);

        FunctionsStatic.getImage(wp_post.getPost_content(), new OnMyFinishListener<String>() {
            @Override
            public void onFinish(String result) {
                if (result.isEmpty())
                    Picasso.with(context).load(context.getString(R.string.url_image_notFound)).fit().into(imgHinh);
                else
                    Picasso.with(context).load(result).resize(400, 200).centerCrop().into(imgHinh);
            }

            @Override
            public void onError(Throwable error, Object bonusOfCoder) {
                FunctionsStatic.getTermID_from_wp_post_id(context.getString(R.string.url_get_termID_from_wp_post)+wp_post.getID(), new OnMyFinishListener<Integer>() {
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

        final TextView tvContent = convertView.findViewById(R.id.tvContentItemWpPostListView);
        FunctionsStatic.getValueOfTagHTML_params(wp_post.getPost_content(), new OnMyFinishListener<String>() {
            @Override
            public void onFinish(String result) {
                if (result != null && result.length() > 400) {
                    result = result.substring(0, 400);
                    result += "<strong> <big style=\"font-weight: bold; color: red;\">...READ MORE</big></strong>";
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

        ImageView imgAvatar = convertView.findViewById(R.id.imageViewAvatarPostItemListView);
        Picasso.with(context).load(context.getString(R.string.url_image_user_male_50)).fit().into(imgAvatar);

        final TextView tvAuthor = convertView.findViewById(R.id.tvAuthorPostItemListView);
        TextView tvPostDay = convertView.findViewById(R.id.tvPostDayItemListView);
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

        tvPostDay.setText(wp_post.getPost_modified());

        return convertView;
    }

    public void filter(String query){
        wp_posts.clear();
        if (query.isEmpty())
            wp_posts.addAll(wp_posts_tmp);
        else {
            String target = "";
            query = FunctionsStatic.VNCharacterToEnglishCharacter(query);
            for (final Wp_post post : wp_posts_tmp) {
                //merge title and content of post => change it into English character.
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(post.getPost_title());
                stringBuilder.append(post.getPost_content());

                target = FunctionsStatic.VNCharacterToEnglishCharacter(stringBuilder.toString());

                if (target.toLowerCase().contains(query.toLowerCase()))
                    wp_posts.add(post);
            }
        }

        notifyDataSetChanged();
    }
}