package binaryblitz.gethelpapp.Fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import binaryblitz.gethelpapp.Activities.OrderActivity;
import binaryblitz.gethelpapp.Adapters.ChatAdapter;
import binaryblitz.gethelpapp.CodeTimer;
import binaryblitz.gethelpapp.Data.Message;
import binaryblitz.gethelpapp.R;
import binaryblitz.gethelpapp.Server.GettHelpServerRequest;
import binaryblitz.gethelpapp.Server.OnRequestPerformedListener;

public class ChatFragment extends Fragment implements SwipyRefreshLayout.OnRefreshListener {
    ChatAdapter adapter;
    private SwipyRefreshLayout layout;
    RecyclerView view;
    private static CountDownTimer timer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        view = (RecyclerView) getView().findViewById(R.id.recyclerView);
        view.setItemAnimator(new DefaultItemAnimator());
        view.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatAdapter(getActivity());
        view.setAdapter(adapter);

        layout = (SwipyRefreshLayout) getView().findViewById(R.id.refresh);
        layout.setOnRefreshListener(this);

        load();

        timer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                load();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        timer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    public void addMessage(String text, String photo) {
        adapter.add(new Message("-2", text, Calendar.getInstance(), photo, true, photo));
    }

    public void scroll() {
        view.scrollToPosition(adapter.getItemCount() - 1);
    }

    public void end() {
        layout.setRefreshing(false);
    }

    public void load() {
        GettHelpServerRequest.with(getContext())
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        Log.e("qwerty", "loaded");
                        layout.setRefreshing(false);
                        try {
                            JSONArray array = (JSONArray) objects[0];

                            if (adapter.getItemCount() == array.length() + 1) {
                                timer.cancel();
                                timer.start();
                                return;
                            } else {
                                adapter.clear();

                                adapter.add(new Message("-1",
                                        "Ваш заказ рассматривается. Оператор ответит Вам в ближайшее время. Пишите, если у Вас какие - то вопросы.",
                                        OrderActivity.order.getCreationTime(),
                                        null,
                                        false,
                                        null));

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);

                                    Calendar start = Calendar.getInstance();

                                    try {
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                                        format.setTimeZone(TimeZone.getTimeZone("UTC"));
                                        Date date = format.parse(object.getString("created_at"));
                                        start.setTime(date);
                                    } catch (Exception ignored) {

                                    }

                                    adapter.add(new Message(object.getString("id"),
                                            object.getString("content"),
                                            start,
                                            object.isNull("image_url") ? null : object.getString("image_url"),
                                            object.getString("category").equals("user"),
                                            object.isNull("image_thumb_url") ? null : object.getString("image_thumb_url")));
                                }
                            }
                        } catch (Exception ignored) {
                        }

                        scroll();
                        timer.cancel();
                        timer.start();
                    }
                })
                .messages(OrderActivity.order.getId())
                .perform();

    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        load();
    }
}