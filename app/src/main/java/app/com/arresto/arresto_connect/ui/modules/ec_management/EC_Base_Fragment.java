package app.com.arresto.arresto_connect.ui.modules.ec_management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.EC_project_Model;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.parsh_Double;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

public abstract class EC_Base_Fragment extends Base_Fragment {
    double yong;
    double user;
    double shock_absorber;
    double fall_clearance;
    double max_load;
    double diameter;
    double pretention;
    double post, crn;
    double to_feet = 3.281;
    int to_pound = 225;
    int temprature = 0;
    String length_type = " Mtr";
    String load_type = " kN";
    public String project_id;
    public String site_id;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        new AppLocationService(getActivity(), new AppLocationService.OnLocationChange() {
//            @Override
//            public void locationChange(Location location, double latitude, double longitude) {
//                curr_lat = latitude;
//                curr_lng = longitude;
//            }
//        });
        if (DataHolder_Model.getInstance().getSlctd_ec_project() != null) {
            project_id = DataHolder_Model.getInstance().getSlctd_ec_project().getEcp_id();
            site_id = DataHolder_Model.getInstance().getSlctd_site().getId();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanseState) {
        return FragmentView(inflater, parent, savedInstanseState);
    }

//    public abstract View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState);

    public void calculate_data(EC_project_Model ec_project_model, LinearLayout segment_root) {
        yong = getYongs(ec_project_model.getConstruction_of_wire());
        user = Double.parseDouble(ec_project_model.getUsers());
        if (ec_project_model.getConstant_force_post().equalsIgnoreCase("yes"))
            shock_absorber = 0;
        else
            shock_absorber = Double.parseDouble(ec_project_model.getAbsorber());

        if (!ec_project_model.getClearance().isEmpty())
            fall_clearance = parsh_Double(ec_project_model.getClearance());
        if (ec_project_model.getMaximum_load() != null && !ec_project_model.getMaximum_load().isEmpty())
            max_load = parsh_Double(ec_project_model.getMaximum_load());
        if (!ec_project_model.getTention().isEmpty())
            pretention = parsh_Double(ec_project_model.getTention());

        if (!ec_project_model.getTemperature().isEmpty())
            temprature = Integer.parseInt(ec_project_model.getTemperature().replaceAll("[^0-9]", ""));

        if (ec_project_model.isMeter()) {
            length_type = " Mtr.";
            load_type = " kN.";
            to_feet = 1;
            to_pound = 1;
            diameter = Double.parseDouble(ec_project_model.getDiameter());
        } else {
            length_type = " Ft.";
            load_type = " Pounds";
            to_feet = 3.28;
            to_pound = 225;
            int index = Arrays.asList(getResources().getStringArray(R.array.diameter_arr_imp)).indexOf(ec_project_model.getDiameter());
            diameter = Double.parseDouble(getResources().getStringArray(R.array.diameter_arr)[index]);
            temprature = (temprature - 32) * 5 / 9;
        }

        max_load = max_load / to_pound;
        pretention = pretention / to_pound;

        if (ec_project_model.getStructure_type().equalsIgnoreCase("ROOF SHEETS")) {
            post = 0.26;
        } else {
            post = 0;
        }

        if (!ec_project_model.getCorner().equalsIgnoreCase("0")) {
            crn = 0.30 * Integer.parseInt(ec_project_model.getCorner());
        } else {
            crn = 0;
        }

        if (ec_project_model.getSegment_1() != null) {
            calculate_deflection(ec_project_model.getSegment_1());
            calculate_cusp(ec_project_model.getSegment_1());
            add_segment_view(ec_project_model.getSegment_1(), segment_root, "Segment 1");
        }
        if (ec_project_model.getSegment_2() != null) {
            calculate_deflection(ec_project_model.getSegment_2());
            calculate_cusp(ec_project_model.getSegment_2());
            add_segment_view(ec_project_model.getSegment_2(), segment_root, "Segment 2");
        }
        if (ec_project_model.getSegment_3() != null) {
            calculate_deflection(ec_project_model.getSegment_3());
            calculate_cusp(ec_project_model.getSegment_3());
            add_segment_view(ec_project_model.getSegment_3(), segment_root, "Segment 3");
        }
        if (ec_project_model.getSegment_4() != null) {
            calculate_deflection(ec_project_model.getSegment_4());
            calculate_cusp(ec_project_model.getSegment_4());
            add_segment_view(ec_project_model.getSegment_4(), segment_root, "Segment 4");
        }
    }

    public int getIntermediates(String s1, String s2) {
        s2 = s2.replaceAll("[^0-9]", "");
        if (s1 != null && !s1.equals("") && !s2.equals("")) {
            double i1 = Double.parseDouble(s1);
            double i2 = Double.parseDouble(s2);
            if (i1 > 0) {
                return (int) Math.round(((i1 / i2) - 0.51)); // horizontal  line
            } else {
                return 0;
            }
        } else {
            return 0;
        }
//        int round = (int) Math.round(((i1 % i2) - 1) + 0.49);
//         Add 0.49 to get next whole number
    }

    public int getV_Intermediates(String s1, String s2) {
        s2 = s2.replaceAll("[^0-9]", "");
        if (s1 != null && !s1.equals("") && !s2.equals("")) {
            double i1 = Double.parseDouble(s1);
            double i2 = Double.parseDouble(s2);
            if (i1 > 0) {
                return (int) Math.round(((i1 / i2) + 0.49));
            } else {
                return 0;
            }
        } else {
            return 0;
        }
//        int round = (int) Math.round(((i1 % i2) - 1) + 0.49);
//         Add 0.49 to get next whole number
    }


    public double calculate_elongation(double total, double span, double diameter, double numberofusers, double youngs, double pre, double k) {
        double a = 3.14 * diameter * diameter / 4;
        double weight = 420 + 100 * numberofusers + 100 * pre;
        for (double l1 = 0; l1 <= 1; l1 = l1 + 0.0001) {
            double sin = Math.sqrt((span + l1 + k) * (span + l1 + k) / 4 - span * span / 4) / ((span + l1 + k) / 2);
            double ans = (weight / (2 * sin)) / (youngs * a * l1 / total);
            if (ans <= 1 && ans >= .0999) {
                return l1;
            }
        }
        return 0;
    }

    public int getYongs(String wire_constrcsn) {
        switch (wire_constrcsn) {
            case "7x7":
                return 5730;
            case "1x19":
                return 10750;
            case "7x19":
            default:
                return 4750;
        }
    }

    public double getK(double no_ofuser, boolean is_single_span) {
        if (is_single_span) {
            switch ((int) no_ofuser) {
                case 1:
                    return 0.546;
                case 2:
                    return 0.7;
                case 3:
                    return 0.795;
                case 4:
                default:
                    return 0.95;
            }
        } else {
//            for multispan
            switch ((int) no_ofuser) {
                case 1:
                    return 0.75;
                case 2:
                    return 0.823;
                case 3:
                    return 0.785;
                case 4:
                default:
                    return 0.948;
            }
        }
    }

    public void calculate_deflection(EC_project_Model.Segment_data segment_data) {
        double total_length = Double.parseDouble(segment_data.getLength()) / to_feet;
        double max_span = Double.parseDouble(segment_data.getSpan().replaceAll("[^0-9]", "")) / to_feet;
        double k = getK(user, total_length == max_span) * shock_absorber;
        double elongation = calculate_elongation(total_length, max_span, diameter, user, yong, pretention, k);
        segment_data.setElongation(elongation);
        double total_elongation = elongation + k + post + crn;
        long f = 500 + (int) user * 140;
        double d = Math.sqrt(Math.pow((total_elongation + max_span) / 2, 2) - Math.pow((max_span / 2), 2));
        segment_data.setDeflection(round(d, 2));
        double R71 = d / ((total_elongation + max_span) / 2);
//        Log.e(" wire deflection ", " is " + d);
        double t = (f / (R71 * 2)) / 100;
//        Log.e(" wire tension ", " is " + t);
        segment_data.setTention(round(t, 2));
        segment_data.setSafety_factor(round(segment_data.getMaximum_strength() / t, 2));
        int angle = (int) Math.toDegrees(Math.asin(R71));
//        Log.e(" wire angle ", " is " + t);
        segment_data.setAngle(angle);
        if ((d + 1.8) > fall_clearance) {
            segment_data.setClearance_status("Low");
        } else {
            segment_data.setClearance_status("Ok");
        }
        if (t > max_load) {
            segment_data.setForce_status("high");
        } else {
            segment_data.setForce_status("Ok");
        }
    }

    private void calculate_cusp(EC_project_Model.Segment_data segment) {
        double du = get_Du();
        double Pr = pretention * 1000;
        double max_span = Double.parseDouble(segment.getSpan().replaceAll("[^0-9]", "")) / to_feet;
        double cs = Pr / du * (Math.cosh(max_span / (2 * Pr / du)) - 1);
        segment.setCusp_sagSpan(round(cs, 3));
        double ct = (max_span * 0.000016) * (60 - temprature);
        segment.setCusp_sagTemp(round(ct, 2));
    }

    public double get_Du() {
        if (diameter == 8) {
            return 2.38;
        } else if (diameter == 10) {
            return 3.78;
        } else {
            return 5.36;
        }
    }

    public static double round(double value, int places) {
        return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }


    public void add_segment_view(EC_project_Model.Segment_data segment_data, LinearLayout segment_root, String heading) {
        View view1 = getActivity().getLayoutInflater().inflate(R.layout.segment_item, segment_root, false);
        ImageView indicator_img = view1.findViewById(R.id.indicator_img);
        TextView heading_tv = view1.findViewById(R.id.heading);
        heading_tv.setText(heading);
        TextView status_tv = view1.findViewById(R.id.status_tv);
        TextView deflection_tv = view1.findViewById(R.id.deflection_tv);
        TextView tention_tv = view1.findViewById(R.id.tention_tv);
        TextView angle_tv = view1.findViewById(R.id.angle_tv);
        TextView sc_tv = view1.findViewById(R.id.sc_tv);
        deflection_tv.setText(round(segment_data.getDeflection() * to_feet, 2) + length_type);
        tention_tv.setText(round(segment_data.getTention() * to_pound, 2) + load_type);
        sc_tv.setText(round(segment_data.getCusp_sagSpan() * to_feet, 2) + length_type);
        angle_tv.setText(segment_data.getAngle() + "\u00B0");
        if (segment_data.getClearance_status().equalsIgnoreCase("ok") && segment_data.getForce_status().equalsIgnoreCase("ok")) {
            indicator_img.setImageResource(R.drawable.light_green);
            status_tv.setText("Ok");
        } else if (!segment_data.getForce_status().equalsIgnoreCase("ok")) {
            indicator_img.setImageResource(R.drawable.light_red);
            status_tv.setText("Force is high");
        } else {
            indicator_img.setImageResource(R.drawable.light_red);
            status_tv.setText("Fall Clearance Low");
        }
        segment_root.addView(view1);
    }


    public void delete_boq(String project_id, String site_id, String boq_id) {
        AppController.getInstance().getDatabase().getProject_Boq_Dao().deleteBOQ(user_id, project_id, site_id, boq_id);
        AppController.getInstance().getDatabase().getCategory_Table_Dao().deleteCategory(user_id, client_id, boq_id);
        AppController.getInstance().getDatabase().getEC_products_Dao().deleteProduct(user_id, client_id, boq_id);
    }

}
