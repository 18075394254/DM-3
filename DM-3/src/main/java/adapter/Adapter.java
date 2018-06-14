package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.user.dm_3.R;

import java.util.List;

import model.ItemBean;


/**
 * Created by Administrator on 2017/1/9 0009.
 */
public class Adapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<ItemBean> list;
    private OnShowItemClickListener onShowItemClickListener;

    public Adapter(List<ItemBean> list, Context context) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.cb_item, null);
            holder.image=(ImageView) convertView.findViewById(R.id.icon2);
            holder.msg = (TextView) convertView.findViewById(R.id.listview_tv);
            holder.cb = (CheckBox) convertView.findViewById(R.id.listview_select_cb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ItemBean bean = list.get(position);
        // 是否是多选状态
        if (bean.isShow()) {
            holder.cb.setVisibility(View.VISIBLE);
        } else {
            holder.cb.setVisibility(View.GONE);
        }
        holder.image.setImageResource(bean.getImageId());
        holder.msg.setText(bean.getMsg());
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bean.setChecked(true);
                } else {
                    bean.setChecked(false);
                }
                // 回调方法，将Item加入已选
                onShowItemClickListener.onShowItemClick(bean);
              //  Log.i("cyy123","选测项目 = "+bean.getMsg());
            }
        });
        // 必须放在监听后面
        holder.cb.setChecked(bean.isChecked());
        return convertView;
    }

    static class ViewHolder {
        ImageView image;
        TextView msg;
        CheckBox cb;
    }

    public interface OnShowItemClickListener {
        void onShowItemClick(ItemBean bean);
    }

    public void setOnShowItemClickListener(OnShowItemClickListener onShowItemClickListener) {
        this.onShowItemClickListener = onShowItemClickListener;
    }
}