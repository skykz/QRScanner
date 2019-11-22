package com.example.qrscanner.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.qrscanner.R;
import com.example.qrscanner.model.Transactions;

import java.util.List;

public class listViewAdapter extends BaseAdapter {

    public List<Transactions> productList;
    Activity activity;

    public listViewAdapter(Activity activity, List<Transactions> productList) {
        super();
        this.activity = activity;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView txtDate,txtPrice,txtSum,txtOil,txtLogin,txtLiter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_view, null);

            holder = new ViewHolder();
            holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            holder.txtPrice = (TextView) convertView.findViewById(R.id.txtPrice);
            holder.txtSum = (TextView) convertView.findViewById(R.id.txtSum);
            holder.txtOil = (TextView) convertView.findViewById(R.id.txtOil);
            holder.txtLogin = (TextView) convertView.findViewById(R.id.txtLogin);
            holder.txtLiter = (TextView) convertView.findViewById(R.id.txtLiter);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Transactions item = productList.get(position);
        holder.txtDate.setText(item.getDate());
        holder.txtPrice.setText(String.valueOf(item.getPrice()));
        holder.txtSum.setText(item.getBalance());
        holder.txtOil.setText(item.getGas());
        holder.txtLogin.setText(item.getLogin());
        holder.txtLiter.setText(String.valueOf(item.getLiters()));
        return convertView;
    }
}
