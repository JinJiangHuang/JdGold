package Notification;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;

import Context.Context;

public class NotificationDingTalk implements INotification {
	private static String webhook = "https://oapi.dingtalk.com/robot/send?access_token=4xxx";
	private static String sercetKey = "SExxxx";
	private static final int NotifyInterval = 173 * 1000;
	private long lastNotifyTime = 0;

	@Override
	public boolean notify(Context context) {
		final long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis - lastNotifyTime < NotifyInterval) {
			return false;
		}
		messagePush(webhook, sercetKey, context);
		this.lastNotifyTime = currentTimeMillis;
		return true;
	}

	/**
	 * @Description: 推送iz*one 冷知识消息
	 * @Param: [webhook,sercetKey] 创建机器人的时候生成的机器人webhook和密钥如果是别的安全设置则参考别的方式
	 * @return: void
	 * @Author: Nipppppp
	 * @Date: 2021/2/26 14:13
	 */
	public void messagePush(String webhook, String sercetKey, Context context) {
		
//		// 文本内容组织
//		StringBuilder text = new StringBuilder();
//		// 图片
//		text.append("![screenshot](https://img-blog.csdnimg.cn/20210226132655473.png");
//		// 换行
//		text.append("\n");
//		// 文本
//		text.append(context.get(Context.ContextType.NotifyContent, ""));
		// 钉钉actionCard推送对象
//		OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
//		// 推送透出首页标题 （你收到这条推送的时候会显示的初内容
//		actionCard.setTitle(context.get(Context.ContextType.NotifyTitle, ""));
//		// 放入你组织好的内容
//		actionCard.setText(text.toString());
		//OapiRobotSendResponse resp = robotPushActionCardMessage(webhook, sercetKey, actionCard);
		
		String markFormat="#### %s @18050193664 \n>  %s \n ![screenshot](https://img95.699pic.com/element/40093/7284.png_860.png)\n> ###### %tc";
		String markText=String.format(markFormat,context.get(Context.ContextType.NotifyTitle, ""), context.get(Context.ContextType.NotifyContent, ""),new Date());
		OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
		markdown.setTitle(context.get(Context.ContextType.NotifyTitle, ""));
        markdown.setText(markText);
		// 做推送
        robotPushMarkdownMessage(webhook, sercetKey, markdown);
		// TODO 有需要的话可以保存一下发送的历史记录啥的
	}

	
	public OapiRobotSendResponse robotPushMarkdownMessage(String webhook, String sercetKey,
			OapiRobotSendRequest.Markdown markdown) {
		try {
			DingTalkClient client = getDingTalkClient(webhook, sercetKey);
			OapiRobotSendRequest request = new OapiRobotSendRequest();
			request.setMsgtype("markdown");
			request.setMarkdown(markdown);
			OapiRobotSendResponse response = client.execute(request);
			if (null != response && response.isSuccess()) {
				System.out.println("钉钉机器人推送markdown成功:{}" + markdown.getText());
			} else {
				System.out.println("钉钉机器人推送markdown失败:{},{}" + markdown.getText() + response.getErrmsg());
			}
			return response;
		} catch (NoSuchAlgorithmException e) {
			System.out.println("=======钉钉机器人推送markdown异常:{}======" + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			System.out.println("=======钉钉机器人推送markdown异常:{}========" + e.getMessage());
		} catch (InvalidKeyException e) {
			System.out.println("=======钉钉机器人推送markdown异常:{}=======" + e.getMessage());
		} catch (ApiException e) {
			System.out.println("=======钉钉机器人推送markdown异常:{}=====" + e.getErrMsg());
		}
		return null;
	}
	/**
	 * @Description: 推送actionCard消息模版
	 * @Param: [webhook,sercetKey,actionCard]
	 * @return: void
	 * @Author: Nipppppp
	 * @Date: 2021/2/26 14:15
	 */
	public OapiRobotSendResponse robotPushActionCardMessage(String webhook, String sercetKey,
			OapiRobotSendRequest.Actioncard actionCard) {
		try {
			DingTalkClient client = getDingTalkClient(webhook, sercetKey);
			OapiRobotSendRequest request = new OapiRobotSendRequest();
			request.setMsgtype("actionCard");
			request.setActionCard(actionCard);
			OapiRobotSendResponse response = client.execute(request);
			if (null != response && response.isSuccess()) {
				System.out.println("钉钉机器人推送actionCard成功:{}" + actionCard.getText());
			} else {
				System.out.println("钉钉机器人推送actionCard失败:{},{}" + actionCard.getText() + response.getErrmsg());
			}
			return response;
		} catch (NoSuchAlgorithmException e) {
			System.out.println("=======钉钉机器人推送actionCard异常:{}======" + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			System.out.println("=======钉钉机器人推送actionCard异常:{}========" + e.getMessage());
		} catch (InvalidKeyException e) {
			System.out.println("=======钉钉机器人推送actionCard异常:{}=======" + e.getMessage());
		} catch (ApiException e) {
			System.out.println("=======钉钉机器人推送actionCard异常:{}=====" + e.getErrMsg());
		}
		return null;
	}

	/**
	 * 获取钉钉对象 机器人仅设置密钥
	 *
	 * @param webHook   机器人webHook
	 * @param secretKey 机器人密钥
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws UnsupportedEncodingException
	 */
	private DingTalkClient getDingTalkClient(String webHook, String secretKey)
			throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
		Map<String, String> sign = sign(secretKey);

		String aceRobotUrl = webHook + ("&timestamp=" + sign.get("timestamp") + "&sign=" + sign.get("sign"));
		DingTalkClient client = new DefaultDingTalkClient(aceRobotUrl);
		return client;
	}

	/**
	 * 计算签名
	 *
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidKeyException
	 */
	public static Map<String, String> sign(String robotSecret)
			throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
		Long timestamp = System.currentTimeMillis();
		// 机器人加签密钥
		String stringToSign = timestamp + "\n" + robotSecret;
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(robotSecret.getBytes("UTF-8"), "HmacSHA256"));
		byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
		String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
		Map<String, String> map = new HashMap<>(2);
		map.put("timestamp", timestamp.toString());
		map.put("sign", sign);
		return map;
	}

//	public static void main(String[] args) {
//		NotificationDingTalk notificationDingTalk=new NotificationDingTalk();
//		notificationDingTalk.notify(new Context());
//	}
}
