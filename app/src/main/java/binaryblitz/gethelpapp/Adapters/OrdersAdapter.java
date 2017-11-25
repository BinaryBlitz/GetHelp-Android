package binaryblitz.gethelpapp.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import binaryblitz.gethelpapp.Activities.OrderActivity;
import binaryblitz.gethelpapp.Activities.WebActivity;
import binaryblitz.gethelpapp.Custom.ProgressDialog;
import binaryblitz.gethelpapp.Data.Order;
import binaryblitz.gethelpapp.R;
import binaryblitz.gethelpapp.Server.GettHelpServerRequest;
import binaryblitz.gethelpapp.Server.OnRequestPerformedListener;
import binaryblitz.gethelpapp.Utils;

public class OrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;
    ArrayList<Order> collection;

    public OrdersAdapter(Activity context) {
        this.context = context;
        collection = new ArrayList<>();
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public void setCollection(ArrayList<Order> collection) {
        this.collection = collection;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.order_card, parent, false);

        return new NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final NewsViewHolder holder = (NewsViewHolder) viewHolder;

        final Order order = collection.get(position);

        if(order.isHomeWork()) {
            holder.type.setText("Домашнее задание");
            holder.time.setText(order.getEndDate());
        } else {
            holder.type.setText("Срочная помощь");
            holder.time.setText(order.getStartDate());
        }

        holder.id.setText("#" + order.getId());
        holder.sub.setText(order.getCourse());
        holder.button.setVisibility(View.GONE);
        holder.sum.setVisibility(View.GONE);

        if(order.isViewed()) {
            holder.itemView.findViewById(R.id.indicator).setVisibility(View.GONE);
        } else {
            holder.itemView.findViewById(R.id.indicator).setVisibility(View.VISIBLE);
        }

        switch (order.getStatus()) {
            case InReview:
                holder.status.setText("Рассматривается");
                holder.status.setTextColor(Color.rgb(95, 190, 212));
                break;
            case WaitingForPayment:
                holder.sum.setVisibility(View.VISIBLE);
                holder.sum.setText(order.getSum() + " " + Html.fromHtml("<html>&#x20bd</html>").toString());
                holder.status.setText("Ожидает оплаты");
                holder.status.setTextColor(Color.rgb(250, 201, 95));
                holder.button.setVisibility(View.VISIBLE);
                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!Utils.isConnected(context)) {
                            Snackbar.make(context.findViewById(R.id.main), "Отсутствует интернет подключение.", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        final ProgressDialog dialog = new ProgressDialog();
                        dialog.show(context.getFragmentManager(), "getthelpapp");
                        GettHelpServerRequest.with(context)
                                .authorize()
                                .listener(new OnRequestPerformedListener() {
                                    @Override
                                    public void onRequestPerformedListener(Object... objects) {
                                        dialog.dismiss();

                                        if(objects[0].equals("Error")) {
                                            return;
                                        }

                                        Intent intent = new Intent(context, WebActivity.class);
                                        try {
                                            intent.putExtra("url", ((JSONObject) objects[0]).getString("url"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        context.startActivity(intent);

                                    }
                                })
                                .payment(order.getId())
                                .perform();
                    }
                });
                break;
            case Accepted:
                holder.status.setText("Принят");
                holder.status.setTextColor(Color.rgb(146, 199, 103));
                break;
            case Refunded:
                holder.status.setText("Возвращен");
                holder.status.setTextColor(Color.rgb(222, 102, 91));
                break;
            case Rejected:
                holder.status.setText("Отклонен");
                holder.status.setTextColor(Color.rgb(222, 102, 91));
                break;
            default:
                holder.status.setText("Рассматривается");
                holder.status.setTextColor(Color.rgb(95, 190, 212));
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderActivity.class);
                OrderActivity.order = order;
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return collection.size();
    }

    private class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView id;
        private TextView sub;
        private TextView type;
        private TextView time;
        private TextView sum;
        private TextView status;

        private Button button;

        public NewsViewHolder(final View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.textView8);
            sub = (TextView) itemView.findViewById(R.id.textView6);
            type = (TextView) itemView.findViewById(R.id.textView7);
            sum = (TextView) itemView.findViewById(R.id.textView9);
            id = (TextView) itemView.findViewById(R.id.textView4);
            status = (TextView) itemView.findViewById(R.id.textView5);

            button = (Button) itemView.findViewById(R.id.button);
        }
    }
}