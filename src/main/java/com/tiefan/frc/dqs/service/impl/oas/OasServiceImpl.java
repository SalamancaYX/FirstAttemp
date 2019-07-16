package com.tiefan.frc.dqs.service.impl.oas;


import com.tiefan.frc.dqs.dto.ResponseVo;
import com.tiefan.frc.dqs.dto.oas.ShumeiSms;
import com.tiefan.frc.dqs.dto.syn.UserSms;
import com.tiefan.frc.dqs.dto.syn.UserSmsOas;
import com.tiefan.frc.dqs.service.oas.IOasService;
import com.tiefan.frc.dqs.support.utils.JsonUtil;
import com.tiefan.frc.dqs.support.utils.http.FSPClientUtil;
import com.tiefan.frc.dqs.zeus.ZeusAppConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class OasServiceImpl implements IOasService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OasServiceImpl.class);
    @Resource
    private ZeusAppConfig zeusAppConfig;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC_SMS = "sms";

    @Resource
    private ThreadPoolTaskExecutor kafkaExecutor;
    @Resource
    private FSPClientUtil fspClientUtil;

    @Override
    public void sendSmsGamble(UserSms userSms, String md5) {
        kafkaExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    UserSmsOas oasSms = new UserSmsOas(userSms);
                    oasSms.setMd5(md5);
                    String params = JsonUtil.toString(oasSms);
                    if (StringUtils.isNotBlank(params)) {
                        LOGGER.info("sms 发送kafka,入参:" + userSms.getMemberId());
                        kafkaTemplate.send(TOPIC_SMS, params);
                        LOGGER.info("sms 发送kafka成功"+ userSms.getMemberId());
                    }
                } catch (Exception e) {
                    LOGGER.error("sms 发送kafka异常", e);
                }
            }
        });

    }

    @Override
    public ShumeiSms queryShumeiSms(String memberId) {
        if (StringUtils.isNotBlank(memberId)) {
            Map<String, String> param = new HashMap<>(1);
            param.put("memberId", memberId);
            String result = fspClientUtil.invokeTspService("FRC.OAS.ShumeiSmsController.selectByMemberId", JsonUtil.toString(param), zeusAppConfig.getZeusConfigBean().getConnectTimeout());
            ResponseVo responseVo = JsonUtil.toBean(result, ResponseVo.class);
            if (responseVo != null && responseVo.isSuccess()) {
                return JsonUtil.toBean(responseVo.getData(),ShumeiSms.class);
            } else {
                LOGGER.warn("调用oas短信接口异常", memberId);
                return null;
            }
        }
        LOGGER.error("调用oas短信接口参数为空", memberId);
        return null;
    }

}
