package ltd.xiaomizha.xuyou.common.utils.bank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 银行卡信息检测结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankCardInfo {

    /**
     * 是否验证通过
     */
    private Boolean validated;

    /**
     * 错误码
     * <p>
     * 01-找不到该银行卡号
     * <p>
     * 02-银行卡号格式错误
     */
    private String errorCode;

    /**
     * 错误/成功消息
     */
    private String message;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行代码
     * <p>
     * 如: "ICBC"、"CCB"、"ABC"
     */
    private String bankCode;

    /**
     * 卡 Bin, 银行卡号前缀
     */
    private String cardBin;

    /**
     * 卡类型代码
     * DC-借记卡(储蓄卡)
     * CC-信用卡
     * SCC-准贷记卡
     * PC-预付费卡
     */
    private String cardType;

    /**
     * 卡类型名称
     */
    private String cardTypeName;

    /**
     * 卡号长度
     */
    private Integer cardLength;

    /**
     * 成功
     */
    public static BankCardInfo success(String bankName, String bankCode, String cardType, String cardTypeName) {
        BankCardInfo info = new BankCardInfo();
        info.setValidated(true);
        info.setErrorCode("00");
        info.setMessage("匹配成功");
        info.setBankName(bankName);
        info.setBankCode(bankCode);
        info.setCardType(cardType);
        info.setCardTypeName(cardTypeName);
        return info;
    }

    /**
     * 失败
     */
    public static BankCardInfo fail(String errorCode, String message) {
        BankCardInfo info = new BankCardInfo();
        info.setValidated(false);
        info.setErrorCode(errorCode);
        info.setMessage(message);
        return info;
    }

}
