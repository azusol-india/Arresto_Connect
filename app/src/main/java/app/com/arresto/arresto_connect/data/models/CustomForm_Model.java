/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.data.models;

import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AZUSOL-PC-02 on 4/12/2019.
 */
public class CustomForm_Model {
    private String cf_id;
    private String client_fk;
    private String form_name;
    private String form_visibility;
    private String form_id;
    private String form_occurrence;
    @SerializedName(value = "field_set",alternate = {"form"})
    private Field_set field_set;


    public String getCreated_date() {
        return created_date;
    }

    private String created_date;

    public String getForm_id() {
        return form_id;
    }

    public String getForm_occurrence() {
        return form_occurrence;
    }

    public String getCf_id() {
        return cf_id;
    }

    public String getClient_fk() {
        return client_fk;
    }

    public String getForm_name() {
        return form_name;
    }

    public String getForm_visibility() {
        return form_visibility;
    }


    public Field_set getField_set() {
        return field_set;
    }

    @Override
    public String toString() {
        return form_name;
    }

    public class Field_set {
        ArrayList<FieldData> single_set, repeater_set;

        public ArrayList<FieldData> getSingle_set() {
            return single_set;
        }

        public ArrayList<FieldData> getRepeater_set() {
            return repeater_set;

        }
    }

    public class FieldData {
        String grp_heading;
        String field_label;
        String field_name;
        String field_type;
        Condition condition;
        ArrayList<Data> enum_list;

        List<String> field_val;

        public String getGrp_heading() {
            return grp_heading;
        }

        public void setGrp_heading(String grp_heading) {
            this.grp_heading = grp_heading;
        }

        public List<String> getField_value() {
            return field_val;
        }

        public void setField_value(List<String> field_value) {
            this.field_val = field_value;
        }

        public String getField_label() {
            return field_label;
        }

        public String getField_name() {
            return field_name;
        }

        public String getField_type() {
            return field_type;
        }

        public ArrayList<Data> getEnum_list() {
            return enum_list;
        }

        public Condition getCondition() {
            return condition;
        }

    }


    public class Condition {
        String multiselect;
        String map_field;
        String depend;

        public String getMultiselect() {
            return multiselect;
        }

        public String getMap_field() {
            return map_field;
        }

        public String getDepend() {
            return depend;
        }
    }

    public class Data {
        public String getHf_id() {
            return hf_id;
        }

        public String getHf_pid() {
            return hf_pid;
        }

        public String getHf_catname() {
            return hf_catname;
        }

        @Override
        public String toString() {
            return hf_catname;
        }

        String hf_id, hf_pid, hf_catname;
    }

    public static class Custom_Form_Deserial implements JsonDeserializer<FieldData> {

        @Override
        public FieldData deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws
                JsonParseException {
            try {
                JsonObject decodeObj = arg0.getAsJsonObject();
                Gson gson = new Gson();
                FieldData decode = gson.fromJson(arg0, FieldData.class);
                List<String> values;
                if (decodeObj.get("field_value").isJsonArray()) {
                    values = gson.fromJson(decodeObj.get("field_value"), new TypeToken<List<String>>() { }.getType());
                } else {
                    String single = gson.fromJson(decodeObj.get("field_value"), String.class);
                    values = new ArrayList<>();
                    values.add(single.trim());
                }
                decode.setField_value(values);
                return decode;
            } catch (Exception e) {
                return null;
            }
        }
    }

//    public static class Custom_Form_Deserial implements JsonDeserializer<CustomForm_Model.Field_set> {
//
//        @Override
//        public CustomForm_Model.Field_set deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws
//                JsonParseException {
//            JsonObject decodeObj = arg0.getAsJsonObject();
//            Gson gson = new Gson();
//            CustomForm_Model.Field_set decoder = gson.fromJson(arg0, CustomForm_Model.Field_set.class);
//            ArrayList values;
//            if (decodeObj.has("enum_list")) {
//                if (decodeObj.get("enum_list").isJsonArray()) {
//                    values = gson.fromJson(decodeObj.get("enum_list"), new TypeToken<List<data>>() {
//                    }.getType());
//                } else {
//                    String single = gson.fromJson(decodeObj.get("enum_list"), String.class);
//                    values = new ArrayList<>();
//                    values.add(single.trim());
//                }
//                decoder.setEnum_lst(values);
//            }
//            return decoder;
//        }
//    }
}
