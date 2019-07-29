package zeng.fanda.com.rxjava2demo.rxbinding;

/**
 * 登录检查结果
 *
 * @author 曾凡达
 * @date 2019/7/19
 */
public class CheckResult {
    // 是否能够登录
    public boolean flag;
    // 提示信息
    public String message;

    public CheckResult() {
        this.flag = true;
        this.message = "";
    }
}
