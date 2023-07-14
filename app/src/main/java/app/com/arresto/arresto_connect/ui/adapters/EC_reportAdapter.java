package app.com.arresto.arresto_connect.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.com.arresto.arresto_connect.data.models.EC_report_Model;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.ui.fragments.Report_webview;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;

public class EC_reportAdapter extends BaseExpandableListAdapter {
    private Activity mContext;
    private List<EC_report_Model> mListDataHeader;
    ExpandableListView expandList;

    public EC_reportAdapter(Activity context, List<EC_report_Model> listDataHeader, ExpandableListView mView) {
        this.mContext = context;
        this.mListDataHeader = listDataHeader;
        this.expandList = mView;
    }

    @Override
    public int getGroupCount() {
        return this.mListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int childCount = 0;
        if (this.mListDataHeader.get(groupPosition).getLine_reports() != null) {
            childCount = this.mListDataHeader.get(groupPosition).getLine_reports().size();
        }
        return childCount;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
//        Log.d("CHILD", mListDataChild.get(this.mListDataHeader.get(groupPosition)).get(childPosition));
        return this.mListDataHeader.get(groupPosition).getLine_reports().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        EC_report_Model ec_report = (EC_report_Model) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.ec_list, null);
        }
        TextView lblListHeader = convertView.findViewById(R.id.submenu);

        ImageView headerIcon = convertView.findViewById(R.id.iconimage);
        lblListHeader.setText("Project No. : "+ec_report.getJobcard_no()+"\nRevision : " + ec_report.getRevision_no());
        if (ec_report.getLine_reports() == null || ec_report.getLine_reports().size() < 1)
            headerIcon.setVisibility(View.GONE);
        else
            headerIcon.setVisibility(View.VISIBLE);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final EC_report_Model.Data child = (EC_report_Model.Data) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.ec_list_item, null);
        }
        TextView txtListChild = convertView.findViewById(R.id.submenu);
        txtListChild.setText(child.getRemark());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Report_webview report_webview = new Report_webview();
                Bundle bundle = new Bundle();
                bundle.putString("url", child.getReport_file());
                bundle.putString("type", "EC");
                report_webview.setArguments(bundle);
                LoadFragment.replace(report_webview,  mContext, getResString("lbl_reports"));
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
