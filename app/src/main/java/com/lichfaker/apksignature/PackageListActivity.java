package com.lichfaker.apksignature;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PackageListActivity extends ListActivity {

    PackageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("应用列表");

        List<ApplicationInfo> apps = getPackageManager().getInstalledApplications(0);
        List<ApplicationInfo> mData = new ArrayList<>();
        // 过滤系统应用
        for (ApplicationInfo info : apps) {
            if ((info.flags & info.FLAG_SYSTEM) == 0) {
                mData.add(info);
            }
        }

        mAdapter = new PackageAdapter(this, mData);

        setListAdapter(mAdapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent data = new Intent();
        data.putExtra("packageName", mAdapter.getItem(position).packageName);
        setResult(0, data);
        finish();
    }

    static class PackageAdapter extends BaseAdapter {

        List<ApplicationInfo> mData;
        Context applicationContext;

        public PackageAdapter(Context context, List<ApplicationInfo> data) {
            this.mData = data;
            applicationContext = context.getApplicationContext();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public ApplicationInfo getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(applicationContext).inflate(R.layout.package_list_item, null, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ApplicationInfo info = getItem(position);
            holder.appIcon.setImageDrawable(info.loadIcon(applicationContext.getPackageManager()));
            holder.appPackageName.setText(info.packageName);
            return convertView;
        }

        class ViewHolder {
            ImageView appIcon;
            TextView appPackageName;

            ViewHolder(View v) {
                appIcon = (ImageView) v.findViewById(R.id.appIcon);
                appPackageName = (TextView) v.findViewById(R.id.appPackageName);
            }
        }
    }
}
