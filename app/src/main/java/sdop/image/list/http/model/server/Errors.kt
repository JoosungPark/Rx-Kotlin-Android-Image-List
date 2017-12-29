package sdop.image.list.http.model.server

/**
 *
 * Created by jei.park on 2017. 12. 28..
 */
enum class ErrorCode(val code: String) {
    INVALID_QUERY("SE01"),
    INVALID_DISPLAY("SE02"),
    INVALID_START("SE03"),
    INVALID_SORT("SE04"),
    INVALID_API("SE05"),
    INVALID_ENCODING("SE06"),
    SYSTEM_ERROR("SE99"),

    INVALID("invalid")
    ;

    companion object {
        fun from(code: String?): ErrorCode = ErrorCode.values().firstOrNull { it.code == code } ?: ErrorCode.INVALID
    }

}