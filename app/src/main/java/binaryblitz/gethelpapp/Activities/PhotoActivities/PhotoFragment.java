package binaryblitz.gethelpapp.Activities.PhotoActivities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import binaryblitz.gethelpapp.R;
import binaryblitz.gethelpapp.Server.GettHelpServerRequest;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoFragment extends Fragment {
    private ImageView photo;
    private String link;
    DisplayImageOptions options;

    private PhotoViewAttacher mAttacher;

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.photo_item, container, false);

        photo = (ImageView) rootView.findViewById(R.id.photo);
        mAttacher = new PhotoViewAttacher(photo);

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(getActivity());
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
                .resetViewBeforeLoading(true)
                .build();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        ImageLoader.getInstance()
                .displayImage(link,
                        photo,
                        options,
                        new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String s, View view) {
                            }

                            @Override
                            public void onLoadingFailed(String s, View view, FailReason failReason) {
                                ImageLoader.getInstance().displayImage("file:///" + link, photo, options, new ImageLoadingListener() {
                                    @Override
                                    public void onLoadingStarted(String s, View view) {
                                    }

                                    @Override
                                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                                    }

                                    @Override
                                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                        if (bitmap != null) {
                                            ImageView imageView = (ImageView) view;
                                            FadeInBitmapDisplayer.animate(imageView, 500);
                                            mAttacher.update();
                                        }
                                    }

                                    @Override
                                    public void onLoadingCancelled(String s, View view) {

                                    }
                                });
                            }

                            @Override
                            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                if (bitmap != null) {
                                    ImageView imageView = (ImageView) view;
                                    FadeInBitmapDisplayer.animate(imageView, 500);
                                    mAttacher.update();
                                }
                            }

                            @Override
                            public void onLoadingCancelled(String s, View view) {

                            }
                        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAttacher.cleanup();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}