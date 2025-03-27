package com.example.demo.response;

import lombok.Getter;

@Getter
public enum ResultCode {
    BASE("", ""),

    SUCCESS("0", "SUCCESS"),

    DATA_NOT_FOUND("050", "DATA NOT FOUND"),
    DATA_NOT_VALID("051", "DATA NOT VALID"),

    INVALID_DOC_NUMBER("200", "Invalid document number"),
    VALIDATE_ERROR("201", "Parameter error"),
    VALIDATE_PARAM_ERROR("202", "Parameter error:[%s]"),

    VALIDATE_FAILD("201", "Invalid Parameter"),
    VALID_ID("202", "Invalid Id"),
    ILLEGAL_DATA("203", "Illegal Data"),

    //Card error messages

    FAILED_TO_GET_READERS_LIST("200", "FAILED TO GET READERS LIST"),
    READER_NOT_FOUND("201", "READER NOT FOUND"),
    NO_CARD("202", "NO CARD"),
    CARD_TIMEOUT("203", "CARD TIMEOUT"),
    NOT_MATCHED("204", "NOT MATCHED"),
    SAM_READER_NOT_FOUND("205", "SAM READER NOT FOUND"),
    CARD_EXCEPTION("206", "CARD EXCEPTION"),
    READER_BUSY("207", "SPECIFIC READER IS STILL IN USE"),
    READER_NOT_REGISTERED("208","READER IS NOT REGISTERED"),
    FAILED_TO_GET_DEVICES_LIST("209","FAILED TO GET DEVICES LIST"),
    CARD_NOT_FOUND("210", "CARD NOT FOUND"),

    // MOC
    VERIFY_MOC_FAILED("211","VERIFY MOC FAILED"),
    VERIFY_MOC_ERROR("212","VERIFY MOC ERROR"),
    CREATE_TEMPLATE_ERROR("213","CREATE TEMPLATE ERROR"),

    IDNUMBER_ALREADY_EXISTED("305", "IDNumber already existed."),
    IDNUMBER_BIOMETRIC_ALREADY_EXISTED("306", "Biometric already existed."),

    //    Perso
    PERSO_INVALID_PRESET("311", "Invalid Presetting"),
    PERSO_INVALID_FACIALIMAGE("312", "Invalid Facial Image"),
    PERSO_INVALID_SOD("313", "Generate SOF File Error"),
    PERSO_INVALID_BAC_HASH("314", "Generate BAC Hash Error"),
    PERSO_OUTSIZE_FICIALIMAGE("315", "%s file up to %s, currently %s"),
    PERSO_INVALID_FINGERIMAGE("316", "Invalid Finger Image"),
    PERSO_INVALID_SIGNATUREIMAGE("317", "Invalid Signature Image"),


    EXPIRED_TOKEN("401", "Access token expired."),
    NO_ACCESS_RIGHT("402", "No right"),


    //File error messages

    DIRECTORY_NOT_FOUND("500", "DIRECTORY NOT FOUND"),
    FILE_NOT_FOUND("501", "FILE NOT FOUND"),
    FAILED_TO_CREATE_DIRECTORY("502", "FAILED TO CREATE DIRECTORY"),
    FAILED_TO_CREATE_FILE("503", "FAILED TO CREATE FILE"),
    FAILED_TO_WRITE_FILE("504", "FAILED TO WRITE FILE"),


    USER_NOT_FOUND("901", "User is not found."),
    INCORRECT_PASSWORD("902", "Password is incorrect."),

    SERVER_ERROR("996", "SERVER ERROR"),
    IO_ERROR("997", "IO ERROR"),
    DATABASE_ERROR("998", "Database Error"),
    UNKNOWN_ERROR("999", "Unknown Error"),


    //Liveness
    LIVENESS_CHECK_FAILED("1100", "Check liveness failed"),
    LIVENESS_FACE_NOT_DETECTED("1101", "Face not detected"),
    LIVENESS_IMAGE_NOT_PORTRAIT("1102", "Image ratio error, smartphone portrait image must have height >= width"),
    LIVENESS_IMAGE_BLUR("1103", "Image is blurred"),
    LIVENESS_FACE_TOO_SMALL_ABS("1104", "Face is too small"),
    LIVENESS_FACE_TOO_SMALL_REL("1105", "The ratio of the face and image is too small"),
    LIVENESS_FACE_MULTI_DETECTION("1106", "Multi faces detection - There is more than 1 face in the picture"),
    LIVENESS_FACE_TOO_DARK("1107", "Face too dark"),
    LIVENESS_EYE_OCCLUDED("1108", "Eye occluded"),
    LIVENESS_MOUTH_OCCLUDED("1109", "Mouth occluded"),
    LIVENESS_FACE_NOT_REAL("1110", "Face is not real"),

    //TEMPLATE ERROR
    FAILED_TO_CREATE_TEMPLATE("3000","FAILED TO CREATE TEMPLATE"),
    CRETPL_OBJECT_NOT_FOUND("3001", "OBJECT NOT FOUND"),
    CRETPL_BAD_LIGHTING("3002", "BAD LIGHTING"),
    CRETPL_OCCLUSION("3003", "OCCLUSION"),
    CRETPL_BAD_POSE("3004", "BAD POSE"),
    CRETPL_BAD_EXPOSURE("3005", "BAD EXPOSURE"),
    CRETPL_BAD_SHARPNESS("3006", "BAD SHARPNESS"),
    CRETPL_TOO_NOISY("3007", "TOO NOISY"),
    CRETPL_BAD_OBJECT("3008", "BAD OBJECT"),
    CRETPL_TWO_FEW_FEATURES("3009", "TOO FEW FEATURES"),
    FAILED_TO_ENROLL("3010", "FAILED TO ENROLL"),
    FAILED_TO_IDENTIFY("3011", "FAILED TO IDENTIFY"),
    DUPLICATE_FOUND("3012", "DUPLICATE FOUND"),
    CUSTOMER_ALREADY_ENROLL("3013", "Customer already enrolled"),
    FACE_WAS_REGISTERED_IN_OTHER_DEVICE("3014", "face was registered in other device"),
    FACE_DARK_GLASSES("3015", "DARK GLASSES"),
    FACE_EYES_CLOSE("3016", "EYES CLOSE"),
    FACE_HEAD_COVERING("3017", "HEAD COVERING"),
    FACE_MASK("3018", "Face with mask"),
    FACE_TILTED("3019", "Tilted Face"),
    FACE_OCCLUSION("3020", "Face is covered"),
    FACE_POSE("3021", "Face is not straight"),
    FACE_LIGHT("3022", "The light is not good"),
    ;


    private String code;
    private String description;
//    private String extraMessage;

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExtraMessage(String extraMessage) {
        this.description = this.description + " " + extraMessage;
    }

    private ResultCode(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
