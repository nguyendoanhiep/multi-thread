package com.example.demo.response;

import lombok.Data;

@Data
public class DataResponse {
    String success;
    Integer rf_score;
    String err_code;
    String err_msg;
    Boolean is_rf;
    String camera_type;
}
