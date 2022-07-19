package Monitor;

import Context.Context;
import Utils.Util;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 监控金价高于或低于设定的阈值
 */
public class MonitorLimit implements IGoldMonitor
{
    private static final String contentFormat = "金价自定义%s限预警！设定值:%.2f，当前：%.2f";
    private static final String nameFormat = "自定义金价%s限预警";
    private String name="自定义金价预警";
    private final boolean upThreshold;
    private final float priceThreshold;

    public MonitorLimit(final JSONObject param) {
        this.upThreshold = param.getBoolean("upThreshold");
        this.priceThreshold = param.getFloat("priceThreshold");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String monitor(Context context) {

        final JSONArray priceArray = context.get(Context.ContextType.PriceArray, new JSONArray());
        final JSONObject jsonPriceNew = priceArray.getJSONObject(0);
        final Float priceCurrent = Util.getPrice(jsonPriceNew);

        if ((upThreshold && priceCurrent >= priceThreshold) || (!upThreshold && priceCurrent <= priceThreshold)) {
            final String s = upThreshold ? "上" : "下";
            name=String.format(nameFormat, s);
            return String.format(contentFormat, s, priceThreshold, priceCurrent);
        }

        return null;
    }
}
