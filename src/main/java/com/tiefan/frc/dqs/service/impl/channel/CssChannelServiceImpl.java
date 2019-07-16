package com.tiefan.frc.dqs.service.impl.channel;

import com.tiefan.frc.dqs.dto.ResponseVo;
import com.tiefan.frc.dqs.dto.dsc.MobileGuishudi;
import com.tiefan.frc.dqs.service.channel.ICssChannelService;
import com.tiefan.frc.dqs.support.utils.JsonUtil;
import com.tiefan.frc.dqs.support.utils.http.FSPClientUtil;
import com.tiefan.frc.dqs.zeus.ZeusAppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class CssChannelServiceImpl implements ICssChannelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CssChannelServiceImpl.class);
    @Resource
    private ZeusAppConfig zeusAppConfig;

    @Resource
    private FSPClientUtil fspUtil;

    @Override
    public ResponseVo addresslistNotify(String memberId, String mobile) {
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", mobile);
        params.put("memberId", memberId);
        params.put("bizType", "XHY");
        params.put("source", "XHY");
        try {
            int i = 0;
            while (i < 3) {
                String result = fspUtil.invokeTspService("CSP.CSS.AddressListController.notify", JsonUtil.toString(params),
                        zeusAppConfig.getZeusConfigBean().getConnectTimeout());
                ResponseVo response = JsonUtil.toBean(result, ResponseVo.class);
                if (response != null && response.isSuccess()) {
                    return response;
                }
                i++;
            }
            LOGGER.warn("通知css通讯录来到调用失败{}", mobile);
            return null;
        } catch (Exception e) {
            LOGGER.error("通知css通讯录来到调用失败", e);
            return null;
        }
    }
}
