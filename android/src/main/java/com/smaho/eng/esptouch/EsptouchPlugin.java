package com.smaho.eng.esptouch;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.espressif.iot.esptouch.task.EsptouchTaskParameter;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;

/** EsptouchPlugin */
public class EsptouchPlugin implements FlutterPlugin, EventChannel.StreamHandler {
    private static final String TAG = "EsptouchPlugin";
    private static final String CHANNEL_NAME = "eng.smaho.com/esptouch_plugin/results";

    private Context context;
    private EspTouchTaskUtil taskUtil;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        this.context = flutterPluginBinding.getApplicationContext();
        final EventChannel eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), CHANNEL_NAME);
        eventChannel.setStreamHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        this.context = null;
    }

    private EsptouchTaskParameter buildTaskParameter(Map<String, Integer> m) {
        EsptouchTaskParameter.Builder b = new EsptouchTaskParameter.Builder();
        b.setIntervalGuideCodeMillisecond(m.get("intervalGuideCodeMillisecond"));
        b.setIntervalDataCodeMillisecond(m.get("intervalDataCodeMillisecond"));
        b.setTimeoutGuideCodeMillisecond(m.get("timeoutGuideCodeMillisecond"));
        b.setTimeoutDataCodeMillisecond(m.get("timeoutDataCodeMillisecond"));
        b.setTotalRepeatTime(m.get("totalRepeatTime"));
        b.setEsptouchResultOneLen(m.get("esptouchResultOneLen"));
        b.setEsptouchResultMacLen(m.get("esptouchResultMacLen"));
        b.setEsptouchResultIpLen(m.get("esptouchResultIpLen"));
        b.setEsptouchResultTotalLen(m.get("esptouchResultTotalLen"));
        b.setPortListening(m.get("portListening"));
        b.setTargetPort(m.get("targetPort"));
        b.setWaitUdpReceivingMilliseond(m.get("waitUdpReceivingMillisecond"));
        b.setWaitUdpSendingMillisecond(m.get("waitUdpSendingMillisecond"));
        b.setThresholdSucBroadcastCount(m.get("thresholdSucBroadcastCount"));
        b.setExpectTaskResultCount(m.get("expectTaskResultCount"));
        return new EsptouchTaskParameter(b);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onListen(Object o, EventChannel.EventSink eventSink) {
        Log.d(TAG, "Event Listener is triggered");
        Map<String, Object> map = (Map<String, Object>) o;
        String ssid = (String) map.get("ssid");
        String bssid = (String) map.get("bssid");
        String password = (String) map.get("password");
        boolean packet = "1".equals(map.get("packet"));
        Map<String, Integer> taskParameterMap = (Map<String, Integer>) map.get("taskParameter");

        Log.d(TAG, String.format("Received stream configuration: SSID: %s, BSSID: %s, Password: %s, Packet: %b", ssid, bssid, password, packet));

        EsptouchTaskParameter taskParameter = buildTaskParameter(taskParameterMap);
        taskUtil = new EspTouchTaskUtil(context, ssid, bssid, password, packet, taskParameter);
        taskUtil.listen(eventSink);
    }

    @Override
    public void onCancel(Object o) {
        Log.d(TAG, "Cancelling stream with configuration: " + o);
        if (taskUtil != null) {
            taskUtil.cancel();
        }
    }
}
