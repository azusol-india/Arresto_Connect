package app.com.arresto.arresto_connect.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.GroupUsers;
import app.com.arresto.arresto_connect.third_party.recycler_fast_scroller.Helpers;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.fragments.UserDetailsFragment;

public class InspectionUserAdapter extends RecyclerView.Adapter<InspectionUserAdapter.ViewHolder> implements SectionIndexer {

    BaseActivity baseActivity;
    ArrayList<GroupUsers> groupUsers;

    public InspectionUserAdapter(BaseActivity baseActivity, ArrayList<GroupUsers> groupUsers) {
        this.baseActivity = baseActivity;
        this.groupUsers = groupUsers;
    }

    @Override
    public InspectionUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_group_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupUsers user = groupUsers.get(position);
        holder.name_text.setText("" + user.getUacc_username());
        holder.email_text.setText("" + user.getGroup_name());
        holder.dsc_text.setText("" + user.getGroup_desc());

        AppUtils.load_profile(user.getUpro_image(), holder.profil_pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadFragment.replace(UserDetailsFragment.newInstance(user), baseActivity, "User inspection details");
            }
        });
    }

    @Override
    public int getItemCount() {
        if (groupUsers != null)
            return groupUsers.size();
        return 0;
    }

    public void update_list(ArrayList<GroupUsers> groupUsers) {
        this.groupUsers = groupUsers;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profil_pic;
        TextView name_text, email_text, dsc_text;
//        RelativeLayout root_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profil_pic = itemView.findViewById(R.id.profil_img);
            name_text = itemView.findViewById(R.id.name_text);
            email_text = itemView.findViewById(R.id.email_text);
            dsc_text = itemView.findViewById(R.id.dsc_text);
//            root_view = itemView.findViewById(R.id.root_view);
//            root_view.setBackgroundResource(R.drawable.round_border);
        }
    }


    private String mSections = "ABCDEFGHIJKLMNOPQRSTUVWXYZ#";
    private HashMap<Integer, Integer> sectionsTranslator = new HashMap<>();
    private ArrayList<Integer> mSectionPositions;

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionsTranslator.get(sectionIndex));
    }

    @Override
    public Object[] getSections() {
        ArrayList<String> sections = new ArrayList<>(27);
        ArrayList<String> alphabetFull = new ArrayList<>();

        mSectionPositions = new ArrayList<>();
        for (int i = 0, size = groupUsers.size(); i < size; i++) {
            if (!groupUsers.get(i).toString().equals("")) {
                String section = String.valueOf(groupUsers.get(i).toString().charAt(0)).toUpperCase();
                if (!sections.contains(section)) {
                    sections.add(section);
                    mSectionPositions.add(i);
                }
            }
        }
        for (int i = 0; i < mSections.length(); i++) {
            alphabetFull.add(String.valueOf(mSections.charAt(i)));
        }
        sectionsTranslator = Helpers.sectionsHelper(sections, alphabetFull);
        return alphabetFull.toArray(new String[0]);
    }
}
