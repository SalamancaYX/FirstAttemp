package com.tiefan.frc.dqs.service.impl.channel;

import com.tiefan.frc.dqs.dto.ResponseVo;
import com.tiefan.frc.dqs.service.channel.ILmsChannelService;
import com.tiefan.frc.dqs.support.constants.ResponseCode;
import com.tiefan.frc.dqs.support.utils.JsonUtil;
import com.tiefan.frc.dqs.support.utils.ResponseUtil;
import com.tiefan.frc.dqs.support.utils.http.FSPClientUtil;
import com.tiefan.frc.dqs.zeus.ZeusAppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiafeilu on 2018/2/9.
 */
@Service
public class LmsChannelServiceImpl implements ILmsChannelService {
    private final static Logger LOGGER = LoggerFactory.getLogger(LmsChannelServiceImpl.class);

    /**
     * 引入zeus配置类
     **/
    @Resource
    private ZeusAppConfig zeusAppConfig;

    @Resource
    private FSPClientUtil fspUtil;
    @Override
    public ResponseVo queryCustomerOverdueInfo(String memberId, String productType, Integer dataType) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("memberId", memberId);
        paramMap.put("productType", productType);
        paramMap.put("dataType", dataType);

        try {
            String params = JsonUtil.toString(paramMap);
            String result = fspUtil.invokeFspService("FRC.LMS.CommonController.queryCustomerOverdueInfo", params, zeusAppConfig.getZeusConfigBean().getConnectTimeout(), true, true);
            return JsonUtil.toBean(result, ResponseVo.class);
        } catch (Exception e) {
            LOGGER.error("客户借款逾期情况查询接口调用异常", e);
            return ResponseUtil.getResponseVo(ResponseCode.CODE_1000001);
        }
    }

    @Override
    public ResponseVo queryNotSettledOrderInfo(String memberId) {
        try {
            Map<String, String> param = new HashMap<>();
            param.put("memberId", memberId);
            String result = fspUtil.invokeTspService("FRC.LMS.CommonController.queryNotSettledOrderInfo", JsonUtil.toString(param), zeusAppConfig.getZeusConfigBean().getConnectTimeout());
            LOGGER.info("未结清订单期数查询接口,request={},response={}", memberId, result);
            ResponseVo responseVo = JsonUtil.toBean(result, ResponseVo.class);
            if (responseVo != null && responseVo.isSuccess()) {
                return responseVo;
            } else {
                LOGGER.error("未结清订单期数查询接口异常，memberId={}", memberId);
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("未结清订单期数查询接口异常，memberId={},error={}", memberId, e);
        }
        return null;
    }

    @Override
    public ResponseVo queryUpCreditInfo(String memberId, BigDecimal credit) {
        Map<String,Object> map = new HashMap<>(2);
        map.put("memberId",memberId);
        map.put("credit",credit);
        String result = fspUtil.invokeTspService("FRC.LMS.CommonController.getUpCreditInfo", JsonUtil.toString(map), zeusAppConfig.getZeusConfigBean().getConnectTimeout());
        ResponseVo responseVo = JsonUtil.toBean(result, ResponseVo.class);
        if (responseVo != null && responseVo.isSuccess()) {
            return responseVo;
        } else {
            LOGGER.error("调额准入条件查询异常，memberId={}", memberId);
            return null;
        }
    }

    @Override
    public ResponseVo queryChangeQuotaWhite(String memberId) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("memberId", memberId);
        String result = fspUtil.invokeTspService("FRC.LMS.WhiteListController.getIsWhiteList", JsonUtil.toString(map), zeusAppConfig.getZeusConfigBean().getConnectTimeout());
        LOGGER.info("提额白名单查询接口,request={}", memberId);
        ResponseVo responseVo = JsonUtil.toBean(result, ResponseVo.class);
        if (responseVo != null && responseVo.isSuccess()) {
            return responseVo;
        } else {
            LOGGER.error("提额白名单查询异常，memberId={}", memberId);
            return null;
        }
    }

    @Override
    public ResponseVo cancelChangeQuotaWhite(String memberId, String batchNo) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("memberId", memberId);
        map.put("batchNo", batchNo);
        String result = fspUtil.invokeTspService("FRC.LMS.WhiteListController.updateWhiteList", JsonUtil.toString(map), zeusAppConfig.getZeusConfigBean().getConnectTimeout());
        LOGGER.info("删除提额白名单接口,request={}", memberId);
        ResponseVo responseVo = JsonUtil.toBean(result, ResponseVo.class);
        if (responseVo != null && responseVo.isSuccess()) {
            return responseVo;
        } else {
            LOGGER.error("删除提额白名单异常，memberId={}", memberId);
            return null;
        }
    }
    @Override
    public ResponseVo getIsFrozenByMobile(String mobile) {
        try {
            Map<String, String> param = new HashMap<>();
            param.put("mobile", mobile);
            String result = fspUtil.invokeTspService("FRC.LMS.AccountFrozenController.getIsFrozenByMobile", JsonUtil.toString(param), zeusAppConfig.getZeusConfigBean().getConnectTimeout());
            LOGGER.info("账户冻结状态查询接口,request={},response={}", mobile, result);
            ResponseVo responseVo = JsonUtil.toBean(result, ResponseVo.class);
            if (responseVo != null && responseVo.isSuccess()) {
                return responseVo;
            } else {
                LOGGER.error("账户冻结状态查询接口，mobile={}", mobile);
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("账户冻结状态查询接口，mobile={},error={}", mobile, e);
        }
        return null;
    }

    @Override
    public ResponseVo queryOverdueInfo(Map<String, Object> params) {
        try {
            String reseult = fspUtil.invokeTspService("FRC.LMS.CommonController.queryOverdueInfo", JsonUtil.toString(params), zeusAppConfig.getZeusConfigBean().getConnectTimeout());
            return JsonUtil.toBean(reseult, ResponseVo.class);
        } catch (Exception e) {
            LOGGER.error("FRC.LMS.CommonController.queryOverdueInfo调用异常", e);
            return null;
        }
    }
}
