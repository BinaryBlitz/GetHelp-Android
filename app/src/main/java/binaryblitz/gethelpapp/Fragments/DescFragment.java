package binaryblitz.gethelpapp.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import binaryblitz.gethelpapp.Activities.OrderActivity;
import binaryblitz.gethelpapp.R;

public class DescFragment extends Fragment {

    private SwipeRefreshLayout layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_desc_layout, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(OrderActivity.order.isHomeWork()) {
            ((TextView) getView().findViewById(R.id.textView15)).setText("Домашнее задание");
            ((TextView) getView().findViewById(R.id.textView17)).setText("Дата: " + OrderActivity.order.getEndDate());
        } else {
            ((TextView) getView().findViewById(R.id.textView15)).setText("Срочная помощь");
            ((TextView) getView().findViewById(R.id.textView17)).setText("Дата: " + OrderActivity.order.getStartDate());
        }

        ((TextView) getView().findViewById(R.id.textView16)).setText(OrderActivity.order.getCourse());
        ((TextView) getView().findViewById(R.id.textView18)).setText(OrderActivity.order.getUniversity() + ", " +
                OrderActivity.order.getFaculty() + ", " + OrderActivity.order.getGrade() + " курс");
        ((TextView) getView().findViewById(R.id.textView19)).setText("E-mail: " + OrderActivity.order.getEmail());

        switch (OrderActivity.order.getStatus()) {
            case InReview:
                ((TextView) getView().findViewById(R.id.textView20)).setText("Рассматривается");
                ((TextView) getView().findViewById(R.id.textView20)).setBackgroundColor(Color.rgb(95, 190, 212));
                break;
            case WaitingForPayment:
                ((TextView) getView().findViewById(R.id.textView20)).setText("Ожидает оплаты");
                ((TextView) getView().findViewById(R.id.textView20)).setBackgroundColor(Color.rgb(250, 201, 95));
                break;
            case Accepted:
                ((TextView) getView().findViewById(R.id.textView20)).setText("Принят");
                ((TextView) getView().findViewById(R.id.textView20)).setBackgroundColor(Color.rgb(146, 199, 103));
                break;
            case Refunded:
                ((TextView) getView().findViewById(R.id.textView20)).setText("Возвращен");
                ((TextView) getView().findViewById(R.id.textView20)).setBackgroundColor(Color.rgb(222, 102, 91));
                break;
            case Rejected:
                ((TextView) getView().findViewById(R.id.textView20)).setText("Отклонен");
                ((TextView) getView().findViewById(R.id.textView20)).setBackgroundColor(Color.rgb(222, 102, 91));
                break;
            default:
                ((TextView) getView().findViewById(R.id.textView20)).setText("Рассматривается");
                ((TextView) getView().findViewById(R.id.textView20)).setBackgroundColor(Color.rgb(95, 190, 212));
                break;
        }
    }
}