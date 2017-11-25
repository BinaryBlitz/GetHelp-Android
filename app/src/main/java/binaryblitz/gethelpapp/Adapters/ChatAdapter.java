package binaryblitz.gethelpapp.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import binaryblitz.gethelpapp.Activities.PhoneActivity;
import binaryblitz.gethelpapp.Activities.PhotoActivities.PhotoActivity;
import binaryblitz.gethelpapp.Data.Message;
import binaryblitz.gethelpapp.R;
import binaryblitz.gethelpapp.Server.GettHelpServerRequest;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;
    ArrayList<Message> collection = new ArrayList<>();
    DisplayImageOptions options;

    public ChatAdapter(Activity context) {
        this.context = context;

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(false)
                .build();

    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public void setCollection(ArrayList<Message> collection) {
        this.collection = collection;
    }

    public void clear() {
        collection.clear();
        notifyDataSetChanged();
    }

    public void add(Message message) {
        if(message.getId().equals("-1")) {
            for (int i = 0; i < collection.size(); i++) {
                if(collection.get(i).getId().equals("-1")) {
                    return;
                }
            }
        }
        collection.add(message);
        notifyItemInserted(collection.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0) {
            final View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.my_message_card, parent, false);
            return new NewsViewHolder(itemView);
        } else {
            final View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.operator_message_card, parent, false);
            return new NewsViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final NewsViewHolder holder = (NewsViewHolder) viewHolder;
        final Message message = collection.get(position);

        if(message.getPhotoUrl() == null) {
            holder.image.setVisibility(View.GONE);
            holder.text.setVisibility(View.VISIBLE);

            holder.text.setText(message.getText());
        } else {
            holder.image.setVisibility(View.VISIBLE);
            holder.text.setVisibility(View.GONE);

            ImageLoader.getInstance().displayImage(message.getPhotoThumb(), holder.image, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ImageLoader.getInstance().displayImage("file:///" + message.getPhotoUrl(), holder.image, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            holder.image.setImageBitmap(null);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            ImageView imageView = (ImageView) view;
                            FadeInBitmapDisplayer.animate(imageView, 500);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ImageView imageView = (ImageView) view;
                    FadeInBitmapDisplayer.animate(imageView, 500);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<Message> photos = new ArrayList<>();

                    for (int i = 0; i < collection.size(); i++) {
                        if(collection.get(i).getPhotoUrl() != null) {
                            photos.add(collection.get(i));
                        }
                    }

                    for (int i = 0; i < photos.size(); i++) {
                        if(photos.get(i).getPhotoUrl().equals(collection.get(position).getPhotoUrl())) {
                            PhotoActivity.index = i;
                            break;
                        }
                    }

                    PhotoActivity.photos = photos;
                    Intent intent = new Intent(context, PhotoActivity.class);
                    context.startActivity(intent);
                }
            });
        }

        holder.time.setText(message.getDate());
    }

    @Override
    public int getItemViewType(int position) {
        return collection.get(position).isMyMessage() ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return collection.size();
    }

    private class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView text;
        private ImageView image;
        private TextView time;

        public NewsViewHolder(final View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.textView22);
            text = (TextView) itemView.findViewById(R.id.textView21);
            image = (ImageView) itemView.findViewById(R.id.imageView5);
        }
    }
}