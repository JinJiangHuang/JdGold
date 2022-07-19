package Monitor;

import Context.Context;
import Utils.Util;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 监控金价震荡
 */
public class MonitorShake implements IGoldMonitor
{
    private static final String contentFormat = "金价震荡预警！%d秒内%s%.2f元，当前值：%.2f, 前值：%.2f";

    private static final String nameFormat="金价%s预警";
    /**
     *金价震荡阈值
     */
    private final float priceShakeThreshold;
    
    private String name="金价预警";
    
    /**
     * 金价监控周期（秒）
     */
    private final int monitorIntervalSecond;

    public MonitorShake(JSONObject param)
    {
        this.priceShakeThreshold = param.getFloat("priceShakeThreshold");
        this.monitorIntervalSecond = param.getInteger("monitorIntervalSecond");
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String monitor(Context context)
    {
        final JSONArray priceArray = context.get(Context.ContextType.PriceArray, new JSONArray());
        final JSONObject jsonPriceNew = priceArray.getJSONObject(0);
        final Float priceCurrent = Util.getPrice(jsonPriceNew);
        final JSONArray priceToday = context.get(Context.ContextType.PriceArray, new JSONArray());
        final JSONObject priceJson = Util.getLastSecondData(priceToday, monitorIntervalSecond);
        final Float priceLast = priceJson.getJSONArray("value").getFloat(1);
        final float priceShake = Math.abs(priceCurrent - priceLast);

        if (priceShake >= priceShakeThreshold) {
            final String s = priceCurrent > priceLast ? "上升" : "下降";
            name=String.format(nameFormat, s);
            return String.format(contentFormat, monitorIntervalSecond, s, priceShake, priceCurrent, priceLast);
        }

        return null;
    }
}
