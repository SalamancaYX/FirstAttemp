package com.tiefan.frc.dqs.service.impl.channel;

import com.tiefan.frc.dqs.dto.ResponseVo;
import com.tiefan.frc.dqs.dto.dsc.IdNoGuishudi;
import com.tiefan.frc.dqs.dto.dsc.MobileGuishudi;
import com.tiefan.frc.dqs.service.channel.IDscChannelService;
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
public class DscChannelServiceImpl implements IDscChannelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DscChannelServiceImpl.class);
    @Resource
    private ZeusAppConfig zeusAppConfig;

    @Resource
    private FSPClientUtil fspUtil;
    @Override
    public MobileGuishudi queryMobileGuishudi(String mobile) {
        Map<String, Object> params = new HashMap<>();
        params.put("phone", mobile);
        MobileGuishudi mobileGuishudi = null;
        try {
            String result = fspUtil.invokeTspService("CSP.DSC.PhoneController.queryMobileLocation", JsonUtil.toString(params),
                    zeusAppConfig.getZeusConfigBean().getConnectTimeout());
            ResponseVo response = JsonUtil.toBean(result, ResponseVo.class);
            if (null == response || !response.isSuccess()) {
                LOGGER.warn("手机归属地调用失败{}", mobile);
            }else {
                mobileGuishudi = JsonUtil.toBean(response.getData(),MobileGuishudi.class);
            }
            return mobileGuishudi;
        } catch (Exception e) {
            LOGGER.error("调用手机归属地异常", e);
            return null;
        }
    }

    @Override
    public IdNoGuishudi queryIdnoGuishudi(String idno) {
        Map<String, Object> params = new HashMap<>();
        params.put("cardNo", idno);
        IdNoGuishudi idNoGuishudi = null;
        try {
            String result = fspUtil.invokeTspService("CSP.DSC.IdCardController.location", JsonUtil.toString(params),
                    zeusAppConfig.getZeusConfigBean().getConnectTimeout());
            ResponseVo response = JsonUtil.toBean(result, ResponseVo.class);
            if (null == response || !response.isSuccess()) {
                LOGGER.warn("身份证归属地调用失败{}", idno);
            }else {
                idNoGuishudi = JsonUtil.toBean(response.getData(),IdNoGuishudi.class);
            }
            return idNoGuishudi;
        } catch (Exception e) {
            LOGGER.error("调用身份证归属地异常", e);
            return null;
        }
    }
}
