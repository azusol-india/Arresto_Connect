/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.data.models;

public class EC_project_Model {
    private Segment_data segment_1;
    private Segment_data segment_2;
    private Segment_data segment_3;
    private Segment_data segment_4;
    private String clearance;
    private String temperature;
    private String tention;
    private String total_sgment;
    private String diameter;
    private String corner;
    private String users;
    private String intermediate_post;
    private String absorber;
    private String construction_of_wire;
    private String maximum_load;
    private String foundation_material;
    private String extremity_post;
    private String structure_type;
    private String rung_clamp;
    private String system_type;
    private String connecting_element;
    private String number_of_line;
    private String type;
    private boolean meter;
    private String currency;
    private String customer_name;
    private String application;
    private String constant_force_post;
    private String isService = "yes";
    private String isPPE = "yes";

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getConstant_force_post() {
        return constant_force_post;
    }

    public void setConstant_force_post(String constant_force_post) {
        this.constant_force_post = constant_force_post;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isMeter() {
        return meter;
    }

    public void setMeter(boolean meter) {
        this.meter = meter;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getNumber_of_line() {
        return number_of_line;
    }

    public void setNumber_of_line(String number_of_line) {
        this.number_of_line = number_of_line;
    }

    public String getConnecting_element() {
        return connecting_element;
    }

    public void setConnecting_element(String connecting_element) {
        this.connecting_element = connecting_element;
    }

    public String getStructure_type() {
        return structure_type;
    }

    public void setStructure_type(String structure_type) {
        this.structure_type = structure_type;
    }

    public String getSystem_type() {
        return system_type;
    }

    public void setSystem_type(String system_type) {
        this.system_type = system_type;
    }

    public String getMaximum_load() {
        return maximum_load;
    }

    public String getFoundation_material() {
        return foundation_material;
    }

    public void setFoundation_material(String foundation_material) {
        this.foundation_material = foundation_material;
    }

    public void setMaximum_load(String maximum_load) {
        this.maximum_load = maximum_load;
    }

    public String getExtremity_post() {
        return extremity_post;
    }

    public void setExtremity_post(String extremity_pos) {
        extremity_post = extremity_pos;
    }

    public String getRung_clamp() {
        return rung_clamp;
    }

    public void setRung_clamp(String rung_clamp) {
        this.rung_clamp = rung_clamp;
    }

    public String getIntermediate_post() {
        return intermediate_post;
    }

    public void setIntermediate_post(String intermediate_post) {
        this.intermediate_post = intermediate_post;
    }

    public Segment_data getSegment_1() {
        return segment_1;
    }

    public void setSegment_1(Segment_data segment_1) {
        this.segment_1 = segment_1;
    }

    public Segment_data getSegment_2() {
        return segment_2;
    }

    public void setSegment_2(Segment_data segment_2) {
        this.segment_2 = segment_2;
    }

    public Segment_data getSegment_3() {
        return segment_3;
    }

    public void setSegment_3(Segment_data segment_3) {
        this.segment_3 = segment_3;
    }

    public Segment_data getSegment_4() {
        return segment_4;
    }

    public void setSegment_4(Segment_data segment_4) {
        this.segment_4 = segment_4;
    }

    public String getClearance() {
        return clearance;
    }

    public void setClearance(String clearance) {
        this.clearance = clearance;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTention() {
        return tention;
    }

    public void setTention(String tention) {
        this.tention = tention;
    }

    public String getTotal_sgment() {
        return total_sgment;
    }

    public void setTotal_sgment(String total_sgment) {
        this.total_sgment = total_sgment;
    }

    public String getDiameter() {
        return diameter;
    }

    public void setDiameter(String diameter) {
        this.diameter = diameter;
    }

    public String getCorner() {
        return corner;
    }

    public void setCorner(String corner) {
        this.corner = corner;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getAbsorber() {
        return absorber;
    }

    public void setAbsorber(String absorber) {
        this.absorber = absorber;
    }

    public String getConstruction_of_wire() {
        return construction_of_wire;
    }

    public void setConstruction_of_wire(String construction_of_wire) {
        this.construction_of_wire = construction_of_wire;
    }

    public String getIsPPE() {
        return isPPE;
    }

    public void setIsPPE(String isPPE) {
        this.isPPE = isPPE;
    }

    public String getIsService() {
        return isService;
    }

    public void setIsService(String isService) {
        this.isService = isService;
    }

    public static class Segment_data {
        double elongation;
        double deflection;
        double tention;
        int angle;
        double safety_factor;
        double cusp_sagSpan;
        double cusp_sagTemp;
        String length;
        String span;
        String clearance_status;
        String force_status;
        private int maximum_strength = 28;

        public String getForce_status() {
            return force_status;
        }

        public void setForce_status(String force_status) {
            this.force_status = force_status;
        }

        public double getSafety_factor() {
            return safety_factor;
        }

        public void setSafety_factor(double safety_factor) {
            this.safety_factor = safety_factor;
        }

        public int getMaximum_strength() {
            return maximum_strength;
        }

        public void setMaximum_strength(int maximum_strength) {
            this.maximum_strength = maximum_strength;
        }

        public double getElongation() {
            return elongation;
        }

        public void setElongation(double elongation) {
            this.elongation = elongation;
        }

        public double getDeflection() {
            return deflection;
        }

        public void setDeflection(double deflection) {
            this.deflection = deflection;
        }

        public double getTention() {
            return tention;
        }

        public void setTention(double tention) {
            this.tention = tention;
        }

        public int getAngle() {
            return angle;
        }

        public void setAngle(int angle) {
            this.angle = angle;
        }


        public String getLength() {
            return length;
        }

        public void setLength(String length) {
            this.length = length;
        }

        public String getSpan() {
            return span;
        }

        public void setSpan(String span) {
            this.span = span;
        }

        public String getClearance_status() {
            return clearance_status;
        }

        public void setClearance_status(String clearance_status) {
            this.clearance_status = clearance_status;
        }

        public double getCusp_sagSpan() {
            return cusp_sagSpan;
        }

        public void setCusp_sagSpan(double cusp_sagSpan) {
            this.cusp_sagSpan = cusp_sagSpan;
        }

        public double getCusp_sagTemp() {
            return cusp_sagTemp;
        }

        public void setCusp_sagTemp(double cusp_sagTemp) {
            this.cusp_sagTemp = cusp_sagTemp;
        }
    }

    //  Vertical param

    //    private String cable;
//    private String bottom_termination;
//    private String top_termination;
//    private String tensioner;
//    private String rope_grab_fall_arrestor;
    private String extension;
    private String junction;
    private String mounting_nut;
    private String mounting_brackets;
    private String mounting_post;
    private String chemical_fasteners;
    private String folding_aluminum;
//    private String trolley;

    private String life_lineLength;
    private String intermediate_spacing;
    private String extension_arm;
    private String section_rung;
    private String rung_dimension;
    private String rung_distance;
    private String section_ladder;
    private String section_size;
    private String top_connection;
    private String left_rungs;
    private String right_rungs;
    private String full_rungs;

    private String isPost;
    private String isChemical_fastener;
    private String isHalf_rung;
    private String isFolding_aluminum;

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getJunction() {
        return junction;
    }

    public void setJunction(String junction) {
        this.junction = junction;
    }

    public String getMounting_nut() {
        return mounting_nut;
    }

    public void setMounting_nut(String mounting_nut) {
        this.mounting_nut = mounting_nut;
    }

    public String getMounting_brackets() {
        return mounting_brackets;
    }

    public void setMounting_brackets(String mounting_brackets) {
        this.mounting_brackets = mounting_brackets;
    }

//    public String getTrolley() {
//        return trolley;
//    }
//
//    public void setTrolley(String trolley) {
//        this.trolley = trolley;
//    }

    public String getLife_lineLength() {
        return life_lineLength;
    }

    public void setLife_lineLength(String life_lineLength) {
        this.life_lineLength = life_lineLength;
    }

    public String getIntermediate_spacing() {
        return intermediate_spacing;
    }

    public void setIntermediate_spacing(String intermediate_spacing) {
        this.intermediate_spacing = intermediate_spacing;
    }

    public String getExtension_arm() {
        return extension_arm;
    }

    public void setExtension_arm(String extension_arm) {
        this.extension_arm = extension_arm;
    }

    public String getSection_rung() {
        return section_rung;
    }

    public void setSection_rung(String section_rung) {
        this.section_rung = section_rung;
    }

    public String getRung_dimension() {
        return rung_dimension;
    }

    public void setRung_dimension(String rung_dimension) {
        this.rung_dimension = rung_dimension;
    }

    public String getRung_distance() {
        return rung_distance;
    }

    public void setRung_distance(String rung_distance) {
        this.rung_distance = rung_distance;
    }

    public String getSection_ladder() {
        return section_ladder;
    }

    public void setSection_ladder(String section_ladder) {
        this.section_ladder = section_ladder;
    }

    public String getSection_size() {
        return section_size;
    }

    public void setSection_size(String section_size) {
        this.section_size = section_size;
    }

    public String getTop_connection() {
        return top_connection;
    }

    public void setTop_connection(String top_connection) {
        this.top_connection = top_connection;
    }

    public String getIsPost() {
        return isPost;
    }

    public void setIsPost(String isPost) {
        this.isPost = isPost;
    }

    public String getIsChemical_fastener() {
        return isChemical_fastener;
    }

    public void setIsChemical_fastener(String isChemical_fastener) {
        this.isChemical_fastener = isChemical_fastener;
    }

    public String getLeft_rungs() {
        return left_rungs;
    }

    public void setLeft_rungs(String left_rungs) {
        this.left_rungs = left_rungs;
    }

    public String getRight_rungs() {
        return right_rungs;
    }

    public void setRight_rungs(String right_rungs) {
        this.right_rungs = right_rungs;
    }

    public String getFull_rungs() {
        return full_rungs;
    }

    public void setFull_rungs(String full_rungs) {
        this.full_rungs = full_rungs;
    }

    public String getIsHalf_rung() {
        return isHalf_rung;
    }

    public void setIsHalf_rung(String isHalf_rung) {
        this.isHalf_rung = isHalf_rung;
    }

    public String getIsFolding_aluminum() {
        return isFolding_aluminum;
    }

    public void setIsFolding_aluminum(String isfolding_aluminum) {
        this.isFolding_aluminum = isfolding_aluminum;
    }

    public String getMounting_post() {
        return mounting_post;
    }

    public void setMounting_post(String mounting_post) {
        this.mounting_post = mounting_post;
    }

    public String getChemical_fasteners() {
        return chemical_fasteners;
    }

    public void setChemical_fasteners(String chemical_fasteners) {
        this.chemical_fasteners = chemical_fasteners;
    }

    public String getFolding_aluminum() {
        return folding_aluminum;
    }

    public void setFolding_aluminum(String folding_aluminum) {
        this.folding_aluminum = folding_aluminum;
    }
}
