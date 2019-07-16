package com.tiefan.frc.dqs.service.impl.capital;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.tiefan.frc.dqs.dao.*;
import com.tiefan.frc.dqs.dao.behavior.IBehaviorAnalysisDao;
import com.tiefan.frc.dqs.dao.lrr.IUserAddressbookDao;
import com.tiefan.frc.dqs.dao.lrr.IUserCallinfoDao;
import com.tiefan.frc.dqs.domain.MemberBaseInfo;
import com.tiefan.frc.dqs.domain.RiskCustomClassValue;
import com.tiefan.frc.dqs.domain.TradeOrderDetail;
import com.tiefan.frc.dqs.domain.XhyCardloanCreditQuota;
import com.tiefan.frc.dqs.domain.bas.BehaviorAnalysisDto;
import com.tiefan.frc.dqs.domain.lrr.UserAppSmsFavList;
import com.tiefan.frc.dqs.dto.ResponseVo;
import com.tiefan.frc.dqs.dto.capital.CapitalPushDataDto;
import com.tiefan.frc.dqs.dto.capital.RaindropsDataDto;
import com.tiefan.frc.dqs.dto.capital.YooliPushDataDto;
import com.tiefan.frc.dqs.dto.dsc.IdNoGuishudi;
import com.tiefan.frc.dqs.dto.dsc.MobileGuishudi;
import com.tiefan.frc.dqs.dto.fcm.FcmCallInfo;
import com.tiefan.frc.dqs.dto.fcm.FcmCallRcord;
import com.tiefan.frc.dqs.dto.fcm.FcmTelInfo;
import com.tiefan.frc.dqs.dto.lms.QueryCustomerOverdueInfoResp;
import com.tiefan.frc.dqs.dto.oas.ShumeiSms;
import com.tiefan.frc.dqs.dto.syn.Contract;
import com.tiefan.frc.dqs.dto.syn.PhoneNo;
import com.tiefan.frc.dqs.service.capital.IChannelPushService;
import com.tiefan.frc.dqs.service.channel.IDscChannelService;
import com.tiefan.frc.dqs.service.channel.ILmsChannelService;
import com.tiefan.frc.dqs.service.oas.IOasService;
import com.tiefan.frc.dqs.support.cache.CacheUtil;
import com.tiefan.frc.dqs.support.constants.CapitalEnum;
import com.tiefan.frc.dqs.support.constants.DqsConstant;
import com.tiefan.frc.dqs.support.constants.LmsProductType;
import com.tiefan.frc.dqs.support.utils.DateTimeUtil;
import com.tiefan.frc.dqs.support.utils.JsonUtil;
import com.tiefan.frc.dqs.support.utils.http.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author skybao
 */
@Service
public class ChannelPushServiceImpl implements IChannelPushService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelPushServiceImpl.class);
    @Resource
    private IOasService oasService;
    @Resource
    private ILmsChannelService lmsChannelService;
    @Resource
    private IMemberBaseInfoDao memberBaseInfoDao;
    @Resource
    private IXhyCardloanCreditQuotaDao xhyCardloanCreditQuotaDao;
    @Resource
    private ITradeOrderDetailDao tradeOrderDetailDao;
    @Resource
    private IBehaviorAnalysisDao behaviorAnalysisDao;
    @Resource
    private IDscChannelService dscChannelService;
    @Resource
    private IUserAddressbookDao addressbookDao;
    @Resource
    private IUserCallinfoDao callinfoDao;
    @Resource
    private CacheUtil cacheUtil;
    private static final String CAPITAL_PUSHDATA_KEY = "CAPITAL_PUSHDATA_";
    private static final String YOOLI_PUSHDATA_KEY = "YOOLI_PUSHDATA_";
    private static final String CHANNELS_KEY = "frc_dqs_app_channels";
    @Value(value = "${device.url}")
    private String deviceUrl;

    @Resource
    private IRiskCustomClassValueDao riskCustomClassValueDao;
    private static final int EXPIRE_TIME = 3600;

    @Override
    public CapitalPushDataDto query(String memberId) {
        String key = CAPITAL_PUSHDATA_KEY + memberId;
        CapitalPushDataDto capitalCache = cacheUtil.getCache(key, CapitalPushDataDto.class);
        if (capitalCache != null) {
            return capitalCache;
        }
        MemberBaseInfo memberBaseInfo = memberBaseInfoDao.selectByMemberId2(memberId);
        if (memberBaseInfo == null) {
            LOGGER.info("没有查询到会员数据,memberId={}", memberId);
            return null;
        }
        String idNo = memberBaseInfo.getIdNoSecret();
        String emergency1 = memberBaseInfo.getEmergencyContactMobile().replaceAll(DqsConstant.REGULAR_MOBILE_REPLACE, "");
        String emergency2 = memberBaseInfo.getEmergencyContactMobile2nd().replaceAll(DqsConstant.REGULAR_MOBILE_REPLACE, "");
        CapitalPushDataDto capitalPushDataDto = new CapitalPushDataDto();
        //历史逾期次数
        capitalPushDataDto.setOverdueCount(getOverdueCount(memberId));
        //工作单位地址和手机归属地匹配    缺-工作单位地址
        //工作单位地址和申请地匹配        缺-工作单位地址

        //申请时间
        TradeOrderDetail tradeOrderDetail = tradeOrderDetailDao.selectFirst(memberId);
        if (tradeOrderDetail != null) {
            capitalPushDataDto.setReuqestTime(DateTimeUtil.getFormatDate(tradeOrderDetail.getAddTime(), DateTimeUtil.TIME_FORMAT_PUBLIC));
        }else{
            XhyCardloanCreditQuota xhyCardloanCreditQuota = xhyCardloanCreditQuotaDao.queryLatest(memberId);
            if (xhyCardloanCreditQuota != null) {
                capitalPushDataDto.setReuqestTime(DateTimeUtil.getFormatDate(xhyCardloanCreditQuota.getAddTime(), DateTimeUtil.TIME_FORMAT_PUBLIC));
            }
        }
        BehaviorAnalysisDto behaviorAnalysisDto = behaviorAnalysisDao.queryCreditData(memberId);
        if (behaviorAnalysisDto != null) {
            String province = behaviorAnalysisDto.getLocateProvince();
            String city = behaviorAnalysisDto.getLocateCity();
            //身份证归属地省市地址与申请省市地址匹配
            capitalPushDataDto.setIdNoProvinceLocalProvince(getIdNoProvinceMatchLocalProvince(province, city, idNo));
            //申请地和手机归属地匹配
            capitalPushDataDto.setMobileHomeIsMatchApplyAddress(getMobileHomeIsMatchApplyAddress(province, city, memberBaseInfo.getAccount()));
        }
        //手机通讯录人数

        List<Contract> contracts = getContracts(memberId);
        if (CollectionUtils.isNotEmpty(contracts)) {
            capitalPushDataDto.setContractsNumOfAddressbook(contracts.size());
        }

        //紧急联系人互动情况
        UserAppSmsFavList callinfo = callinfoDao.getCallinfoByMemberId(memberId);
        FcmTelInfo telInfo = null;
        if (callinfo != null) {
            telInfo = JsonUtil.toBean(callinfo.getDetail(), FcmTelInfo.class);
        }
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        capitalPushDataDto.setNetAge(sdf.format(now.getTime()));
        if (telInfo != null) {
            //入网时长
            now.add(Calendar.MONTH, -Double.valueOf(Math.ceil(telInfo.getNetAge())).intValue());

            try {
                capitalPushDataDto.setNetAge(sdf.format(now.getTime()));
            } catch (Exception e) {
                LOGGER.error("日期转换异常", e);
            }

            capitalPushDataDto.setAveMonthBill(telInfo.getAveMonthBill());
            List<FcmCallInfo> list = telInfo.getCallInfo();
            if (CollectionUtils.isNotEmpty(list)) {
                List<FcmCallRcord> callRcords = list.stream().flatMap(fcmCallInfo -> fcmCallInfo.getCallRecord().stream()).collect(Collectors.toList());
                Set<String> called = new HashSet<>(128);
                Set<String> calling = new HashSet<>(128);
                Set<String> months = new HashSet<>(8);
                int totalCallTime = 0;
                boolean callEmergency1 = false;
                boolean callEmergency2 = false;
                String otherNumber = null;
                for (FcmCallRcord callRcord : callRcords) {
                    totalCallTime += NumberUtils.toInt(callRcord.getCallTime());
                    otherNumber = callRcord.getCallOtherNumber();
                    if ("主叫".equals(callRcord.getCallTypeName())) {
                        calling.add(otherNumber);
                    } else {
                        called.add(otherNumber);
                    }
                    if (emergency1.equals(otherNumber)) {
                        callEmergency1 = true;
                    }
                    if (emergency2.equals(otherNumber)) {
                        callEmergency2 = true;
                    }
                    if (StringUtils.isNotBlank(callRcord.getCallStartTime()) && callRcord.getCallStartTime().length() > 7) {
                        months.add(callRcord.getCallStartTime().substring(0, 7));
                    }
                }
                if (months.size() != 0) {
                    capitalPushDataDto.setCallTotalTime1Month(Double.valueOf(Math.ceil(totalCallTime / months.size() / 60d)).intValue());
                }
                if (callEmergency1 && callEmergency2) {
                    //均有联系记录
                    capitalPushDataDto.setEmergencyStatus(1);
                } else if (callEmergency1 || callEmergency2) {
                    //其中一无联系记录
                    capitalPushDataDto.setEmergencyStatus(2);
                } else {
                    //无联系记录
                    capitalPushDataDto.setEmergencyStatus(3);
                }
                //统计互通电话的数量
                long count = called.stream().filter(mobile -> calling.contains(mobile)).count();
                capitalPushDataDto.setCalledingCount((int) count);
            }
        }
        cacheUtil.setCache(key, capitalPushDataDto, EXPIRE_TIME);
        return capitalPushDataDto;
    }

    @Override
    public YooliPushDataDto queryYooliData(String memberId) {
        YooliPushDataDto yooliPushDataDto = new YooliPushDataDto();
        String cache = cacheUtil.getCache(YOOLI_PUSHDATA_KEY + memberId);
        if (StringUtils.isNotBlank(cache)) {
            yooliPushDataDto = JsonUtil.toBean(cache, YooliPushDataDto.class);
            return yooliPushDataDto;
        }
        List<RiskCustomClassValue> riskCustomClassValues = null;
        String channels = cacheUtil.getCache(CHANNELS_KEY);
        if (StringUtils.isNotBlank(channels)) {
            riskCustomClassValues = JsonUtil.toList(channels, RiskCustomClassValue.class);
        } else {
            riskCustomClassValues = riskCustomClassValueDao.queryAppChannels();
            if (CollectionUtils.isNotEmpty(riskCustomClassValues)) {
                cacheUtil.setCache(CHANNELS_KEY, riskCustomClassValues, EXPIRE_TIME);
            }
        }

        MemberBaseInfo memberBaseInfo = memberBaseInfoDao.selectByMemberId2(memberId);
        if (memberBaseInfo == null) {
            LOGGER.info("没有查询到会员数据,memberId={}", memberId);
            return null;
        }
        String emergency1 = memberBaseInfo.getEmergencyContactMobile().replaceAll(DqsConstant.REGULAR_MOBILE_REPLACE, "");
        String emergency2 = memberBaseInfo.getEmergencyContactMobile2nd().replaceAll(DqsConstant.REGULAR_MOBILE_REPLACE, "");
        //紧急联系人情况
        UserAppSmsFavList callinfo = callinfoDao.getCallinfoByMemberId(memberId);
        FcmTelInfo telInfo = null;
        if (callinfo != null) {
            telInfo = JsonUtil.toBean(callinfo.getDetail(), FcmTelInfo.class);
        }
        if (telInfo != null) {
            //入网时长
            yooliPushDataDto.setNetAge(telInfo.getNetAge());
            yooliPushDataDto.setAveMonthBill(telInfo.getAveMonthBill());
            List<FcmCallInfo> list = telInfo.getCallInfo();
            if (CollectionUtils.isNotEmpty(list)) {
                List<FcmCallRcord> callRcords = list.stream().flatMap(fcmCallInfo -> fcmCallInfo.getCallRecord().stream()).collect(Collectors.toList());
                TreeMap<String, Integer> emer1Map = new TreeMap();
                TreeMap<String, Integer> emer2Map = new TreeMap();
                for (FcmCallRcord callRcord : callRcords) {
                    if (StringUtils.isNotBlank(callRcord.getCallStartTime()) && callRcord.getCallStartTime().length() > 7) {
                        String month = callRcord.getCallStartTime().substring(0, 7);
                        getEmerCallCount(emergency1, emer1Map, callRcord.getCallOtherNumber(), month);
                        getEmerCallCount(emergency2, emer2Map, callRcord.getCallOtherNumber(), month);
                    }
                }
                int emer11 = 0;
                int emer12 = 0;
                int emer13 = 0;
                if (MapUtils.isNotEmpty(emer1Map)) {
                    Map.Entry<String, Integer> lastOneEntry = emer1Map.pollLastEntry();
                    //最近的1个月
                    emer11 = lastOneEntry.getValue();
                    //最近的第2个月
                    lastOneEntry = emer1Map.pollLastEntry();
                    if (lastOneEntry != null) {
                        emer12 = emer11 + lastOneEntry.getValue();
                    }
                    //最近的第3个月
                    lastOneEntry = emer1Map.pollLastEntry();
                    if (lastOneEntry != null) {
                        emer13 = emer12 + lastOneEntry.getValue();
                    }
                }
                int emer21 = 0;
                int emer22 = 0;
                int emer23 = 0;
                if (MapUtils.isNotEmpty(emer2Map)) {
                    Map.Entry<String, Integer> lastOneEntry = emer2Map.pollLastEntry();
                    emer21 = lastOneEntry.getValue();
                    lastOneEntry = emer2Map.pollLastEntry();
                    if (lastOneEntry != null) {
                        emer22 = emer21 + lastOneEntry.getValue();
                    }
                    lastOneEntry = emer2Map.pollLastEntry();
                    if (lastOneEntry != null) {
                        emer23 = emer22 + lastOneEntry.getValue();
                    }
                }
                yooliPushDataDto.setEmer1CallCountOf1Month(emer11);
                yooliPushDataDto.setEmer1CallCountOf2Month(emer12);
                yooliPushDataDto.setEmer1CallCountOf3Month(emer13);
                yooliPushDataDto.setEmer2CallCountOf1Month(emer21);
                yooliPushDataDto.setEmer2CallCountOf2Month(emer22);
                yooliPushDataDto.setEmer2CallCountOf3Month(emer23);
            }
        }
        //手机通讯录人数
        List<Contract> contracts = getContracts(memberId);
        if (CollectionUtils.isNotEmpty(contracts)) {
            yooliPushDataDto.setContractsCount(contracts.size());
            boolean emer1InContracts = false;
            boolean emer2InContracts = false;
            for (Contract contract : contracts) {
                if (emer1InContracts && emer2InContracts) {
                    break;
                }
                PhoneNo phoneNo = contract.getPhoneNo();
                String cell = phoneNo.getCellPhone();
                String fixedPhone = phoneNo.getFixedPhone();
                if (StringUtils.isNotBlank(cell)) {
                    cell = cell.replaceAll(DqsConstant.REGULAR_MOBILE_REPLACE, "");
                }
                if (StringUtils.isNotBlank(fixedPhone)) {
                    fixedPhone = fixedPhone.replaceAll(DqsConstant.REGULAR_MOBILE_REPLACE, "");
                }
                if (!emer1InContracts) {
                    if (emergency1.equals(cell) || emergency1.equals(fixedPhone)) {
                        emer1InContracts = true;
                    }
                }
                if (!emer2InContracts) {
                    if (emergency2.equals(cell) || emergency2.equals(fixedPhone)) {
                        emer2InContracts = true;
                    }
                }
            }
            yooliPushDataDto.setEmer1InContracts(emer1InContracts);
            yooliPushDataDto.setEmer2InContracts(emer2InContracts);
        }
        //手机号归属地
        yooliPushDataDto.setMobileAttr(getMobileAttr(memberBaseInfo.getAccount()));
        //最近一次代你还交易记录
        BehaviorAnalysisDto behaviorAnalysisDto = behaviorAnalysisDao.queryLastOneTradeDataOfDnh(memberId);
        if (behaviorAnalysisDto != null) {
            yooliPushDataDto.setDeviceId(behaviorAnalysisDto.getDeviceId());
            yooliPushDataDto.setSystem(behaviorAnalysisDto.getSystemNo());
            yooliPushDataDto.setSystemVersion(behaviorAnalysisDto.getTermSysVersion());
            yooliPushDataDto.setTermModel(behaviorAnalysisDto.getTermModel());

            String channel = behaviorAnalysisDto.getAppChannel();
            if (StringUtils.isNotBlank(channel)) {
                yooliPushDataDto.setAppChannel(getAppChannelName(riskCustomClassValues, channel));
            }
            if (StringUtils.isBlank(yooliPushDataDto.getAppChannel())) {
                yooliPushDataDto.setAppChannel("自然渠道");
            }
            String deviceResult = HttpUtils.doGet(deviceUrl + behaviorAnalysisDto.getDeviceId());
            Map map = JsonUtil.toMap(deviceResult);
            if (MapUtils.isNotEmpty(map)) {
                Set<Map.Entry> set = map.entrySet();
                for (Map.Entry entry : set) {
                    Map<String, Object> json = (HashMap) entry.getValue();
                    //当前设备的网络类型	networkType
                    String netType = String.valueOf(json.get("networkType"));
                    if ("WIFI".equalsIgnoreCase(netType)) {
                        yooliPushDataDto.setNetworkType("2");
                    } else {
                        yooliPushDataDto.setNetworkType("1");
                    }
                    //越狱标识	rooted
                    yooliPushDataDto.setRooted(json.get("rooted").toString());
                    //屏幕亮度	brightness
                    yooliPushDataDto.setScreenBrightness(json.get("brightness").toString());
                    //电池状态以及电量	battery
                    String battery = json.get("battery").toString();
                    //平台 platform
                    String platform = json.get("platform").toString();
                    try {
                        if ("IOS".equals(platform)) {
                            if (StringUtils.isNotBlank(battery)) {
                                String[] tmp = battery.replace("]", "").replace("[", "").split("-");
                                if ("charging".equals(tmp[0])) {
                                    //充电状态
                                    yooliPushDataDto.setBatteryChargingStatus("1");
                                } else {
                                    yooliPushDataDto.setBatteryChargingStatus("0");
                                }
                                yooliPushDataDto.setBatteryPower(tmp[1]);
                            }
                        } else {
                            if (StringUtils.isNotBlank(battery)) {
                                JSONArray array = JSON.parseArray(battery);
                                int a = NumberUtils.toInt(array.getString(0));
                                if (a == 2) {
                                    //充电状态
                                    yooliPushDataDto.setBatteryChargingStatus("1");
                                } else {
                                    yooliPushDataDto.setBatteryChargingStatus("0");
                                }
                                double b = NumberUtils.toDouble(array.getString(1));
                                yooliPushDataDto.setBatteryPower(new DecimalFormat("0.00").format(b / 100));
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("有利网推送数据电量异常,battery={}", battery);
                    }
                }
            }
        }
        //短信数据
        ShumeiSms shumeiSms = oasService.queryShumeiSms(memberId);
        if (shumeiSms != null) {
            //信用卡申请机构数
            Integer creditLoanApplications = shumeiSms.getCreditLoanApplications();
            //短信网贷平台申请数量
            Integer itfinLoanApplications = shumeiSms.getItfinLoanApplications();
            if (creditLoanApplications != null) {
                yooliPushDataDto.setCreditCardNumsOfSms(creditLoanApplications);
            }
            if (itfinLoanApplications != null) {
                yooliPushDataDto.setNetLoanNumsOfSms(itfinLoanApplications);
            }
        }
        cacheUtil.setCache(YOOLI_PUSHDATA_KEY + memberId, yooliPushDataDto, EXPIRE_TIME);
        return yooliPushDataDto;
    }

    @Override
    public RaindropsDataDto queryLittleRaindropsData(String memberId) {
        BehaviorAnalysisDto behaviorAnalysisDto = behaviorAnalysisDao.queryLastOneTradeDataOfDnh(memberId);
        if (behaviorAnalysisDto == null) {
            return null;
        }
        RaindropsDataDto raindropsDataDto = new RaindropsDataDto();
        raindropsDataDto.setIp(behaviorAnalysisDto.getClientIp());
        raindropsDataDto.setLat(behaviorAnalysisDto.getLat());
        raindropsDataDto.setLon(behaviorAnalysisDto.getLon());
        return raindropsDataDto;
    }

    @Override
    public ResponseVo unifiedQuery(String memberId, String capital) {
        ResponseVo responseVo = new ResponseVo();
        responseVo.setSuccess(true);
        if (CapitalEnum.DNH_MNW.name().equals(capital)) {
            responseVo.setData(query(memberId));
        } else if (CapitalEnum.YOOLI.name().equals(capital)) {
            responseVo.setData(queryYooliData(memberId));
        } else if (CapitalEnum.XYD.name().equals(capital)) {
            responseVo.setData(queryLittleRaindropsData(memberId));
        }
        return responseVo;
    }

    private String getAppChannelName(List<RiskCustomClassValue> list, String appChannel) {
        if (CollectionUtils.isNotEmpty(list) && StringUtils.isNotBlank(appChannel)) {
            RiskCustomClassValue riskCustomClassValue = null;
            for (int i = 0; i < list.size(); i++) {
                riskCustomClassValue = list.get(i);
                if (appChannel.equals(riskCustomClassValue.getValue())) {
                    return riskCustomClassValue.getName();
                }
            }
        }
        return null;
    }


    /**
     * 获取手机号归属地
     *
     * @param mobile
     * @return
     */
    private String getMobileAttr(String mobile) {
        MobileGuishudi mobileGuishudi = dscChannelService.queryMobileGuishudi(mobile);
        if (mobileGuishudi != null) {
            StringBuffer tmp = new StringBuffer();
            if (StringUtils.isNotBlank(mobileGuishudi.getProvinceName())) {
                tmp.append(mobileGuishudi.getProvinceName());
            }
            if (StringUtils.isNotBlank(mobileGuishudi.getCityName())) {
                tmp.append(mobileGuishudi.getCityName());
            }
            return tmp.toString();
        }
        return null;
    }


    private List<Contract> getContracts(String memberId) {
        //手机通讯录人数
        UserAppSmsFavList addressbook = addressbookDao.getAddressbookByMemberId(memberId);
        if (addressbook != null) {
            List<Contract> contracts = JsonUtil.toList(addressbook.getDetail(), Contract.class);
            return contracts;
        }
        return null;
    }

    private void getEmerCallCount(String emergency1, Map<String, Integer> emer1Map, String callOtherNumber, String month) {
        if (!emer1Map.containsKey(month)) {
            emer1Map.put(month, 0);
        }
        int value = emer1Map.get(month);
        if (callOtherNumber.equals(emergency1)) {
            emer1Map.put(month, ++value);
        }
    }

    private int getMobileHomeIsMatchApplyAddress(String province, String city, String mobile) {
        if (StringUtils.isBlank(province) || StringUtils.isBlank(city)
                || "未知".equals(province) || "未知".equals(city)
                || StringUtils.isBlank(mobile)) {
            return 1;
        }
        MobileGuishudi mobileGuishudi = dscChannelService.queryMobileGuishudi(mobile);
        if (mobileGuishudi != null && mobileGuishudi.getStatusCode() != null && mobileGuishudi.getStatusCode() == 0) {
            boolean sameProvince = province.startsWith(mobileGuishudi.getProvinceName());
            boolean sameCity = city.startsWith(mobileGuishudi.getCityName());
            if (sameProvince && sameCity) {
                return 1;
            } else if (sameProvince) {
                return 2;
            } else {
                return 3;
            }
        }
        return 1;
    }

    private int getIdNoProvinceMatchLocalProvince(String province, String city, String idNo) {
        if (StringUtils.isBlank(province) || StringUtils.isBlank(city)
                || "未知".equals(province) || "未知".equals(city)
                || StringUtils.isBlank(idNo)) {
            return 1;
        }
        IdNoGuishudi idNoGuishudi = dscChannelService.queryIdnoGuishudi(idNo);
        if (idNoGuishudi != null && idNoGuishudi.getStatusCode() != null && idNoGuishudi.getStatusCode() == 0) {
            if (province.startsWith(idNoGuishudi.getProvinceName()) && city.startsWith(idNoGuishudi.getCityName())) {
                return 1;
            } else {
                return 2;
            }
        }
        return 1;
    }

    private int getOverdueCount(String memberId) {
        ResponseVo responseVo = lmsChannelService.queryCustomerOverdueInfo(memberId, LmsProductType.ALL.name(), 4);
        int overdueCount = 0;
        if (responseVo != null && responseVo.isSuccess()) {
            List<QueryCustomerOverdueInfoResp> list = JsonUtil.toList(responseVo.getData(), QueryCustomerOverdueInfoResp.class);
            if (CollectionUtils.isNotEmpty(list)) {
                for (QueryCustomerOverdueInfoResp customerOverdueInfoResp : list) {
                    Integer ovedue = customerOverdueInfoResp.getOverdueTenor();
                    if (ovedue != null) {
                        overdueCount += ovedue;
                    }
                }
            }
        }
        return overdueCount;
    }
}
