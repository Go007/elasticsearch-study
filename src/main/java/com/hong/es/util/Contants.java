package com.hong.es.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Configuration
public class Contants {
    @Autowired
    private Environment _environment;

    //邮件配置信息
    public static String PROTOCOL = "smtp";
    public static String SSL_PROTOCOL = "smtps";
    public static String MAIL_HOST;
    public static String MAIL_USER;
    public static String MAIL_PSW;
    public static String MAIL_SSL_PORT;

    public static int REDIS_TIMEOUT = 5;
    public static TimeUnit REDIS_TIMEUNIT = TimeUnit.MINUTES;
    public static final String UTF_8 = "utf-8";
    public static String NEWS_SERVER_URL;

    //获得companyId
    public static String HBASE_COMPANYID_FROM_NAME_URL;

    //es索引名称-企业高级搜索
    public static String ES_INDEX_NAME_COMPANY_INFO_SEARCH;
    //es索引名称-企业风险搜索
    public static String ES_INDEX_NAME_ENTERPRISE_RISK_SEARCH;

    //企业高级搜索solr服务URL
    public static String SOLR_SERVICE_COMPANY_URL;
    //风险企业高级搜索solr服务URL
    public static String SOLR_SERVICE_RISK_URL;
    //私募机构高级搜索solr服务URL
    public static String SOLR_SERVICE_PFUND_URL;
    
    
    //高级搜搜solr服务url
    public static String SOLR_SERVICE_URL35;
    public static String SOLR_SERVICE_URL36;
    public static String SOLR_SERVICE_URL37;
    //高级搜索solr服务方法名称
    public static String SOLR_SERVICE_COMPANY_METHOD;//企业高级搜索
    public static String SOLR_SERVICE_RISK_METHOD;//风险高级搜索
    public static String SOLR_SERVICE_PFUND_METHOD;//私募高级搜索
    public static String SOLR_SERVICE_SENTIMENT_METHOD;//新闻舆情高级搜索
    public static String SOLR_SERVICE_BOND_METHOD;//发债企业高级搜索
    public static String SOLR_SERVICE_PERSON_METHOD;//人名搜索
    public static String SOLR_SERVICE_CREDIT_METHOD;//资信高级搜索

    //受益所有人接口地址
    public static String BENEFICIALOWNER_URL;

    // 校验验证码
    public static String SERVER_SENDCODE_URL;
    // 校验验证码
    public static String SERVER_VERIFY_CODE;
    // 验证码配置信息
    public static String APP_KEY;
    public static String APP_SECRET;
    public static String NONCE;
    public static String MOULD_ID;
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded;charset=utf-8";

    //FTP server
    public static String FTP_ADDR;
    public static String FTP_PORT;
    public static String FTP_USER;
    public static String FTP_PASS;
    public static String FTP_SNAPSHOT_PATH;
    public static String FTP_PICTURE_PATH;

    //找关系接口
    public static String CHART_URL;

    //标准图谱接口
    public static String STANDARD_CHART_URL;

    //评分_关联风险
    public static String DAASCOMPANYSEARCH_URL;

    //展台HBase接口
    //企业基础信息
    public static String HBASE_COMPANY_BASICINFO_URL;//工商信息
    public static String HBASE_COMPANY_RISK_URL;//风险提示
    public static String HBASE_COMPANY_MANAGELEVEL_URL;//管理层对外投资对外任职
    public static String HBASE_COMPANY_LPPOSITION_LPINVEST_URL;//管理层对外投资对外任职
    public static String HBASE_COMPANY_SHAREHOLDER_URL;//股东信息-十大股东
    public static String HBASE_COMPANY_SHAREHDINVEST_URL;//股东信息-发起人股东
    public static String HBASE_COMPANY_SHAREHDINVESTNB_URL;//股东信息-年报披露股东及出资信息
    public static String HBASE_COMPANY_INVESTMENT_URL;//对外投资-对外投资企业
    public static String HBASE_COMPANY_COMPYCHANGE_URL;//变更记录
    public static String HBASE_COMPANY_HOLDINGS_URL;//对外投资-参控企业
    public static String HBASE_COMPANY_BRANCH_URL;//分支机构
    public static String HBASE_COMPANY_SPOTCHECK_URL;//抽查检查
    public static String HBASE_COMPANY_SHAREHDINVESTPUB_URL;//企业自主公示股东

    public static String ES_COMPANY_OPTMSHAREHOLDERS_URL;//优化股东信息

    //企业风险
    public static String HBASE_COMPANY_RISK_BONDVIOLATION_URL;//债券违约
    public static String HBASE_COMPANY_RISK_CREDITCHANGE_URL;//主体评级下调
    public static String HBASE_COMPANY_RISK_BOND_CREDITCHANGE_URL;//债券评级下调
    public static String HBASE_COMPANY_RISK_FINANCEALARM_URL;//财务风险
    public static String HBASE_COMPANY_RISK_FROZENSHARE_URL;//股权冻结

    public static String HBASE_COMPANY_RISK_ANNOUNCEMENT_URL;//法院公告
    public static String HBASE_COMPANY_RISK_LITIGANT_URL;//被执行人
    public static String HBASE_COMPANY_RISK_ISHONEST_URL; //失信人
    public static String HBASE_COMPANY_RISK_OPEREXCEPT_URL;//经营异常
    public static String HBASE_COMPANY_RISK_ADMINPENALTY_URL;//行政处罚
    public static String HBASE_COMPANY_RISK_SERIVIOLAT_URL;//严重违法
    public static String HBASE_COMPANY_RISK_EQUITYPLEDGE_URL;//股权出质
    public static String HBASE_COMPANY_RISK_CHATTELREG_URL;//动产抵押
    public static String HBASE_COMPANY_RISK_RISKRELA_URL;//风险影响企业
    public static String HBASE_COMPANY_RISK_COURTNOTICE_URL;//开庭公告
    public static String HBASE_COMPANY_RISK_JUDASSIT_URL;//司法协助
    public static String HBASE_COMPANY_RISK_RELATEDPART_URL;//关联方



    public static String ES_COMPANY_RISK_VIOLATIONINFO_URL;//违规
    public static String ES_COMPANY_RISK_TAXOVERDUE_URL;//欠税
    public static String ES_COMPANY_RISK_HONESTYINFO_URL;//诚信信息

    //企业资信状况
    public static String HBASE_COMPANY_CREDIT_CREDITRATING_URL;//主体评级
    public static String HBASE_COMPANY_CREDIT_CSCSCREDIT_URL;//信用评估-风险评估
    public static String HBASE_COMPANY_CREDIT_EXPOSURE_URL;//信用评估-敞口类型
    public static String HBASE_COMPANY_CREDIT_CSCSCREDITDETAIL_URL;//信用评估-风险分布
    public static String HBASE_COMPANY_CREDIT_BASICINFONB_URL;//财务情况_企业年报
    public static String HBASE_COMPANY_CREDIT_QUANFACTOR_URL;//财务分析-指标筛选
    public static String HBASE_COMPANY_CREDIT_FINANCEFACTOR_URL;//财务分析-指标显示
    public static String HBASE_COMPANY_CREDIT_FINANCEFACTORRANKIND_URL;//财务分析-指标行业排名
    public static String HBASE_COMPANY_CREDIT_FINANCEFACTORTREND_URL;//财务分析-财务指标数据历史走势
    public static String HBASE_COMPANY_CREDIT_OPERATIONFACTOR_URL;//财务分析-经营情况
    public static String HBASE_COMPANY_CREDIT_FROZENPLEDGE_URL;//股权质押
    public static String HBASE_COMPANY_CREDIT_BONDINPERIOD_URL;//债务及偿还情况-存续期债券
    public static String HBASE_COMPANY_CREDIT_BONDISSUEHIST_URL;//债务及偿还情况-历史发行债券
    public static String HBASE_COMPANY_CREDIT_BONDCASHHIST_URL;//债务及偿还情况-债务偿还统计表
    public static String HBASE_COMPANY_CREDIT_COMPYCREDIT_URL;//授信
    public static String HBASE_COMPANY_CREDIT_COMPYGUARANTEE_URL;//担保-东财
    public static String HBASE_COMPANY_CREDIT_COMPYGUARANTEEGS_URL;//担保-工商披露年报信息
    public static String HBASE_COMPANY_CREDIT_ANNOUNCEINFO_URL;//公司公告

    //关联风险
    public static String HBASE_COMPANY_RELATEDRISK_SHAREHDRELATION_URL;//股东关系
    public static String HBASE_COMPANY_RELATEDRISK_GUARANTEETO_URL;//担保关系 - 为当前公司担保的企业
    public static String HBASE_COMPANY_RELATEDRISK_GUARANTEEOUT_URL;//担保关系 - 当前公司对外担保的企业
    public static String HBASE_COMPANY_RELATEDRISK_GUARANTEEOUTOFCHILD_URL;//担保关系 - 当前公司子公司对外担保的企业
    public static String HBASE_COMPANY_RELATEDRISK_INVESTRELATION_URL;//投资关系
    public static String HBASE_COMPANY_RELATEDRISK_TOPCUSTOMER_URL;//主要客户
    public static String HBASE_COMPANY_RELATEDRISK_TOPSUPPLIER_URL;//主要供应商
    public static String HBASE_COMPANY_RELATEDRISK_SAMELEGPERSON_URL;//重要关联人 - 同一法定代表人
    public static String HBASE_COMPANY_RELATEDRISK_SAMEACTUALMANAGER_URL;//重要关联人 - 同一实际控制人
    public static String HBASE_COMPANY_RELATEDRISK_SAMECHAIRMAN_URL;//重要关联人 - 同一董事长
    public static String HBASE_COMPANY_RELATEDRISK_SAMEEXECUTIVECHAIRMAN_URL;//重要关联人 - 同一执行董事

    //展台 - 新闻舆情
    public static String HBASE_COMPANY_NEWS_NEGATIVENEWSSTATIS_URL;//负面新闻统计
    public static String HBASE_COMPANY_NEWS_NEGATIVENEWSWARNINGCODE_URL;//负面新闻命中的预警指标
    public static String HBASE_COMPANY_NEWS_NEGATIVENEWSCOMPANY_URL;//负面新闻提到的企业
    public static String HBASE_COMPANY_NEWS_NEGATIVENEWS_URL;//负面新闻
    public static String HBASE_COMPANY_NEWS_ANNOUNCESTATISTICS_URL;//公告分析 - 标签统计
    public static String HBASE_COMPANY_NEWS_ANNOUNCE_URL;//公告分析 - 公告分析
    public static String HBASE_COMPANY_NEWS_TRENDNEWS_URL;//热度趋势-舆情趋势
    public static String HBASE_COMPANY_NEWS_WORDSFUZZY_URL;//热度趋势-词云
    public static String HBASE_COMPANY_NEWS_TRENDNEWSLIST_URL;//热度趋势-新闻详情
    public static String HBASE_COMPANY_NEWS_RELATEDSTATISTICS_URL;//关联方资讯 - 关系
    public static String HBASE_COMPANY_NEWS_RELATEDNEWSLIST_URL;//关联方资讯-新闻详情
    public static String HBASE_COMPANY_NEWS_DETAIL_URL;//新闻详情(新闻详情页)

    //展台私募信息
    public static String HBASE_COMPANY_PFUND_RECORDINFO_URL;//机构备案基本信息
    public static String HBASE_COMPANY_PFUND_LEGREPRESENTLIST_URL;//法定代表人
    public static String HBASE_COMPANY_PFUND_EXECUTIVESLIST_URL;//高管
    public static String HBASE_COMPANY_PFUND_SHAREHOLDERLIST_URL;//股东
    public static String HBASE_COMPANY_PFUND_PRODUCTLIST_URL;//私募机构产品信息
    public static String HBASE_COMPANY_PFUND_CREDIBILITYINFO_URL;//机构诚信信息
    public static String HBASE_COMPANY_PFUND_DATAEXCEPTION_URL;//数据异常
    
    //企业高级搜索
    public static String HBASE_COMPANY_SEARCH_COMPANYTYPE_URL;//企业类型
    public static String HBASE_COMPANY_SEARCH_INDUSTRY_URL;//行业
    public static String HBASE_COMPANY_SEARCH_REGREGION_URL;//注册地
    public static String HBASE_COMPANY_SEARCH_COMPANYST_URL;//企业状态
    
    //风险信息高级搜索
    public static String HBASE_RISK_SEARCH_RISKYTYPE_URL;//风险类型
    
    //发债企业高级搜索
    public static String HBASE_BOND_COMPANY_SEARCH_BONDDEFAULTSSTA_URL;//债券违约统计
    public static String HBASE_BOND_COMPANY_SEARCH_BONDDEFAULTSSTALIST_URL;//债券违约统计企业列表
    public static String HBASE_BOND_COMPANY_SEARCH_RATINGDOWNSTA_URL;//主体评级下调统计
    public static String HBASE_BOND_COMPANY_SEARCH_RATINGDOWNSTALIST_URL;//主体评级下调统计企业列表
    public static String HBASE_BOND_COMPANY_SEARCH_FINANCIALRISKSTA_URL;//财务风险-非标准审计意见统计
    public static String HBASE_BOND_COMPANY_SEARCH_FINANCIALRISKSTALIST_URL;//财务风险-非标准审计意见统计企业列表
    public static String HBASE_BOND_COMPANY_SEARCH_BONDISSUERCURRENT_URL;//债券发行人统计-现债券融资额统计
    public static String HBASE_BOND_COMPANY_SEARCH_BONDISSUERWITHINYEAR_URL;//债券发行人统计-年内应归还债券额统计
    public static String HBASE_BOND_COMPANY_SEARCH_BONDISSUERDURATION_URL;//债券发行人统计-存续期债券数
    public static String HBASE_BOND_COMPANY_SEARCH_BONDMARKETPUBLISHSTA_URL;//债券市场规模-债券市场上月发行情况
    public static String HBASE_BOND_COMPANY_SEARCH_BONDMARKETFINANCINGSTA_URL;//债券市场规模-信用债市场净融资情况-按月
    public static String HBASE_BOND_COMPANY_SEARCH_BONDAREAPUBLISHTOP10_URL;//债券区域发行分布-区域top10
    public static String HBASE_BOND_COMPANY_SEARCH_MAXPROVINCE_URL;//债券区域发行分布-数量最多的省份
    public static String HBASE_BOND_COMPANY_SEARCH_PUBLISHPROVINCE_URL;//债券区域发行分布-按省份查询
    public static String HBASE_BOND_COMPANY_SEARCH_INVESTGRADE_URL;//最新投资等级列表
    public static String HBASE_BOND_COMPANY_SEARCH_LASTRATING_URL;//最新主体评级列表
    public static String HBASE_BOND_COMPANY_SEARCH_RATINGOUTLOOK_URL;//最新评级展望列表
    public static String HBASE_BOND_COMPANY_SEARCH_BONDTYPE_URL;//发债类型列表
    public static String HBASE_BOND_COMPANY_SEARCH_AUDITOPINION_URL;//审计意见列表
    public static String HBASE_BOND_COMPANY_SEARCH_INDUSTRYLIST_URL;//行业列表
    public static String HBASE_BOND_COMPANY_SEARCH_FINANCIALCHANGE_URL;//重要财务指标不利变化列表
    
    //私募首页
    public static String HBASE_PFCOMPY_PFTIPINFO_URL;//特别提示-诚信信息
    public static String HBASE_PFCOMPY_PFWARNINGS_URL;//特别提示-中基协提示事项
    public static String HBASE_PFCOMPY_REVOKELIST_URL;//特别提示-取消备案机构

    //私募机构高级搜索
    public static String HBASE_PRIVATE_EQUITY_SEARCH_RISKGRADE_URL;//风险等级列表
    public static String HBASE_PRIVATE_EQUITY_SEARCH_RISKTYPE_URL;//风险类型列表
    public static String HBASE_PRIVATE_EQUITY_SEARCH_FUNDRECORD_URL;//基金备案阶段列表
    public static String HBASE_PRIVATE_EQUITY_SEARCH_ORGTYPE_URL;//机构类型列表
    public static String HBASE_PRIVATE_EQUITY_SEARCH_MANAGETYPE_URL;//管理类型列表
    public static String HBASE_PRIVATE_EQUITY_SEARCH_OPERATIONSTATUS_URL;//运作状态列表
    public static String HBASE_PRIVATE_EQUITY_SEARCH_INTEGRITYINFO_URL;//诚信信息列表
    public static String HBASE_PRIVATE_EQUITY_SEARCH_TIPS_URL;//提示事项列表
    
    //找咨询/新闻舆情高级搜索
    public static String HBASE_NEWS_SEARCH_TYPE24H_URL;//获取24小时负面舆情-风险标签（风险二级标签）
    public static String HBASE_NEWS_SEARCH_NEGATIVE24H_URL;//获取24小时负面舆情-新闻列表
    public static String HBASE_NEWS_SEARCH_NEGATIVETOP10_URL1;//企业舆情排行榜: （1日以内，3日以内，1周以内，1月以内）
    public static String HBASE_NEWS_SEARCH_NEGATIVETOP10_URL3;//企业舆情排行榜: （1日以内，3日以内，1周以内，1月以内）
    public static String HBASE_NEWS_SEARCH_NEGATIVETOP10_URL7;//企业舆情排行榜: （1日以内，3日以内，1周以内，1月以内）
    public static String HBASE_NEWS_SEARCH_NEGATIVETOP10_URL30;//企业舆情排行榜: （1日以内，3日以内，1周以内，1月以内）
    public static String HBASE_NEWS_SEARCH_MEDIATYPE_URL;//获取媒体类型
    public static String HBASE_NEWS_SEARCH_PUBLISHTIME_URL;//获取发布时间
    public static String HBASE_NEWS_SEARCH_RISKTYPELABEL_URL;//风险类型二级标签列表
    public static String HBASE_NEWS_SEARCH_NEWSDETAIL_URL;//新闻详情页
    public static String HBASE_NEWS_SEARCH_RELATECOMPANY_URL;//新闻关联企业列表

    //定时器FLAG
    public static String SCHEDULED_FLAG;
    //traceList
    public static String USER_TRACE_LIST;

    //es索引名称-新闻高级搜索
    public static String ES_INDEX_NEWS_NAME;

    //裁判文书
    public static String ES_COMPANY_RISK_DOCUMENTINFO_URL;

    @PostConstruct
    public void initialization() {
        NEWS_SERVER_URL = _environment.getProperty("cscs.portal.solr.news.query");

        BENEFICIALOWNER_URL = _environment.getProperty("cscs.portal.api.server.enterprise.beneficialowner");

        USER_TRACE_LIST = _environment.getProperty("userTrace.urllist");

        //获得companyId
        HBASE_COMPANYID_FROM_NAME_URL = _environment.getProperty("hbase.company.name.get.id.url");

        //邮件
        MAIL_HOST = _environment.getProperty("mail.server.host");
        MAIL_USER = _environment.getProperty("mail.server.user");
        MAIL_PSW = _environment.getProperty("mail.server.password");
        MAIL_SSL_PORT = _environment.getProperty("mail.server.sslport");

        //sms server
        SERVER_SENDCODE_URL = _environment.getProperty("cscs.portal.sms.server.sendCode");
        SERVER_VERIFY_CODE = _environment.getProperty("cscs.portal.sms.server.verifyCode");
        APP_KEY = _environment.getProperty("cscs.portal.sms.server.code.appKey");
        APP_SECRET = _environment.getProperty("cscs.portal.sms.server.code.appSecret");
        NONCE = _environment.getProperty("cscs.portal.sms.server.code.nonce");
        MOULD_ID = _environment.getProperty("cscs.portal.sms.server.code.mouldId");

        //ftp server
        FTP_ADDR = _environment.getProperty("cscs.portal.ftp.addr");
        FTP_PORT = _environment.getProperty("cscs.portal.ftp.port");
        FTP_USER = _environment.getProperty("cscs.portal.ftp.user");
        FTP_PASS = _environment.getProperty("cscs.portal.ftp.password");
        FTP_SNAPSHOT_PATH = _environment.getProperty("cscs.portal.ftp.snapshotPath");
        FTP_PICTURE_PATH = _environment.getProperty("cscs.portal.ftp.picturePath");

        //找关系接口
        CHART_URL = _environment.getProperty("cscs.portal.chart.server");

        //标准图谱接口
        STANDARD_CHART_URL = _environment.getProperty("cscs.portal.standard.chart.server");

        DAASCOMPANYSEARCH_URL = _environment.getProperty("cscs.portal.DAASCompanySearch.server");

        //HBase接口
        //企业基础信息
        HBASE_COMPANY_BASICINFO_URL = _environment.getProperty("hbase.company.base.info.basicinfo.url");
        HBASE_COMPANY_RISK_URL = _environment.getProperty("hbase.company.base.info.risk.url");
        HBASE_COMPANY_MANAGELEVEL_URL = _environment.getProperty("hbase.company.base.info.managelevel.url");
        HBASE_COMPANY_LPPOSITION_LPINVEST_URL = _environment.getProperty("hbase.company.base.info.lpposition.lpinvest.url");
        HBASE_COMPANY_SHAREHOLDER_URL = _environment.getProperty("hbase.company.base.info.shareholder.url");
        HBASE_COMPANY_SHAREHDINVEST_URL = _environment.getProperty("hbase.company.base.info.sharehdinvest.url");
        HBASE_COMPANY_SHAREHDINVESTNB_URL = _environment.getProperty("hbase.company.base.info.sharehdinvestnb.url");
        HBASE_COMPANY_INVESTMENT_URL = _environment.getProperty("hbase.company.base.info.investment.url");
        HBASE_COMPANY_COMPYCHANGE_URL = _environment.getProperty("hbase.company.base.info.compyChange.url");
        HBASE_COMPANY_HOLDINGS_URL = _environment.getProperty("hbase.company.base.info.holdings.url");
        HBASE_COMPANY_BRANCH_URL = _environment.getProperty("hbase.company.base.info.branch.url");
        HBASE_COMPANY_SPOTCHECK_URL = _environment.getProperty("hbase.company.base.info.spotcheck.url");
        HBASE_COMPANY_SHAREHDINVESTPUB_URL = _environment.getProperty("hbase.company.base.info.sharehdinvestpub.url");

        ES_COMPANY_OPTMSHAREHOLDERS_URL = _environment.getProperty("es.company.base.info.optmshareholders.url");

        //企业风险
        HBASE_COMPANY_RISK_BONDVIOLATION_URL = _environment.getProperty("hbase.company.risk.bondviolation.url");
        HBASE_COMPANY_RISK_CREDITCHANGE_URL = _environment.getProperty("hbase.company.risk.creditchange.url");
        HBASE_COMPANY_RISK_BOND_CREDITCHANGE_URL = _environment.getProperty("hbase.company.risk.bond.creditchange.url");
        HBASE_COMPANY_RISK_FINANCEALARM_URL = _environment.getProperty("hbase.company.risk.financealarm.url");
        HBASE_COMPANY_RISK_FROZENSHARE_URL = _environment.getProperty("hbase.company.risk.frozenshare.url");
        HBASE_COMPANY_RISK_ANNOUNCEMENT_URL = _environment.getProperty("hbase.company.risk.announcement.url");
        HBASE_COMPANY_RISK_LITIGANT_URL = _environment.getProperty("hbase.company.risk.litigant.url");
        HBASE_COMPANY_RISK_ISHONEST_URL = _environment.getProperty("hbase.company.risk.dishonest.url");
        HBASE_COMPANY_RISK_OPEREXCEPT_URL = _environment.getProperty("hbase.company.risk.operexcept.url");
        HBASE_COMPANY_RISK_ADMINPENALTY_URL = _environment.getProperty("hbase.company.risk.adminpenalty.url");
        HBASE_COMPANY_RISK_SERIVIOLAT_URL = _environment.getProperty("hbase.company.risk.seriviolat.url");
        HBASE_COMPANY_RISK_EQUITYPLEDGE_URL = _environment.getProperty("hbase.company.risk.equitypledge.url");
        HBASE_COMPANY_RISK_CHATTELREG_URL = _environment.getProperty("hbase.company.risk.chattelreg.url");
        HBASE_COMPANY_RISK_RISKRELA_URL = _environment.getProperty("hbase.company.risk.riskrela.url");
        //HBASE_COMPANY_RISK_RISKANALO_URL = _environment.getProperty("hbase.company.risk.riskanalo.url");

        ES_COMPANY_RISK_VIOLATIONINFO_URL = _environment.getProperty("es.company.risk.violationinfo.url");
        ES_COMPANY_RISK_TAXOVERDUE_URL = _environment.getProperty("es.company.risk.taxoverdue.url");
        ES_COMPANY_RISK_HONESTYINFO_URL = _environment.getProperty("es.company.risk.honestyinfo.url");

        //企业资信状况
        HBASE_COMPANY_CREDIT_CREDITRATING_URL = _environment.getProperty("hbase.company.credit.creditrating.url");
        HBASE_COMPANY_CREDIT_CSCSCREDIT_URL = _environment.getProperty("hbase.company.credit.cscscredit.url");
        HBASE_COMPANY_CREDIT_CSCSCREDITDETAIL_URL = _environment.getProperty("hbase.company.credit.cscscreditdetail.url");
        HBASE_COMPANY_CREDIT_EXPOSURE_URL = _environment.getProperty("hbase.company.credit.exposure.url");
        HBASE_COMPANY_CREDIT_BASICINFONB_URL = _environment.getProperty("hbase.company.credit.basicinfonb.url");
        HBASE_COMPANY_CREDIT_QUANFACTOR_URL = _environment.getProperty("hbase.company.credit.quanfactor.url");
        HBASE_COMPANY_CREDIT_FINANCEFACTOR_URL = _environment.getProperty("hbase.company.credit.financefactor.url");
        HBASE_COMPANY_CREDIT_FINANCEFACTORRANKIND_URL = _environment.getProperty("hbase.company.credit.financefactorrankind.url");
        HBASE_COMPANY_CREDIT_FINANCEFACTORTREND_URL = _environment.getProperty("hbase.company.credit.financefactortrend.url");
        HBASE_COMPANY_CREDIT_OPERATIONFACTOR_URL = _environment.getProperty("hbase.company.credit.operationfactor.url");
        HBASE_COMPANY_CREDIT_FROZENPLEDGE_URL = _environment.getProperty("hbase.company.credit.frozenpledge.url");
        HBASE_COMPANY_CREDIT_BONDINPERIOD_URL = _environment.getProperty("hbase.company.credit.bondinperiod.url");
        HBASE_COMPANY_CREDIT_BONDISSUEHIST_URL = _environment.getProperty("hbase.company.credit.bondissuehist.url");
        HBASE_COMPANY_CREDIT_BONDCASHHIST_URL = _environment.getProperty("hbase.company.credit.bondcashhist.url");
        HBASE_COMPANY_CREDIT_COMPYCREDIT_URL = _environment.getProperty("hbase.company.credit.compycredit.url");
        HBASE_COMPANY_CREDIT_COMPYGUARANTEE_URL = _environment.getProperty("hbase.company.credit.compyguarantee.url");
        HBASE_COMPANY_CREDIT_COMPYGUARANTEEGS_URL = _environment.getProperty("hbase.company.credit.compyguaranteegs.url");
        HBASE_COMPANY_CREDIT_ANNOUNCEINFO_URL = _environment.getProperty("hbase.company.credit.announceinfo.url");

        //关联风险
        HBASE_COMPANY_RELATEDRISK_SHAREHDRELATION_URL = _environment.getProperty("hbase.company.relatedRisk.sharehdrelation.url");
        HBASE_COMPANY_RELATEDRISK_GUARANTEETO_URL = _environment.getProperty("hbase.company.relatedRisk.guaranteeTo.url");
        HBASE_COMPANY_RELATEDRISK_GUARANTEEOUT_URL = _environment.getProperty("hbase.company.relatedRisk.guaranteeOut.url");
        HBASE_COMPANY_RELATEDRISK_GUARANTEEOUTOFCHILD_URL = _environment.getProperty("hbase.company.relatedRisk.guaranteeOutOfChild.url");
        HBASE_COMPANY_RELATEDRISK_INVESTRELATION_URL = _environment.getProperty("hbase.company.relatedRisk.investrelation.url");
        HBASE_COMPANY_RELATEDRISK_TOPCUSTOMER_URL = _environment.getProperty("hbase.company.relatedRisk.topcustomer.url");
        HBASE_COMPANY_RELATEDRISK_TOPSUPPLIER_URL = _environment.getProperty("hbase.company.relatedRisk.topsupplier.url");
        HBASE_COMPANY_RELATEDRISK_SAMELEGPERSON_URL = _environment.getProperty("hbase.company.relatedRisk.samelegperson.url");
        HBASE_COMPANY_RELATEDRISK_SAMEACTUALMANAGER_URL = _environment.getProperty("hbase.company.relatedRisk.sameactualmanager.url");
        HBASE_COMPANY_RELATEDRISK_SAMECHAIRMAN_URL = _environment.getProperty("hbase.company.relatedRisk.samechairman.url");
        HBASE_COMPANY_RELATEDRISK_SAMEEXECUTIVECHAIRMAN_URL = _environment.getProperty("hbase.company.relatedRisk.sameexecutivechairman.url");

        //展台 - 新闻舆情
        HBASE_COMPANY_NEWS_NEGATIVENEWSSTATIS_URL = _environment.getProperty("hbase.company.news.negativenewsstatis.url");
        HBASE_COMPANY_NEWS_NEGATIVENEWSWARNINGCODE_URL = _environment.getProperty("hbase.company.news.negativenewswarningcode.url");
        HBASE_COMPANY_NEWS_NEGATIVENEWSCOMPANY_URL = _environment.getProperty("hbase.company.news.negativenewscompany.url");
        HBASE_COMPANY_NEWS_NEGATIVENEWS_URL = _environment.getProperty("hbase.company.news.negativenews.url");
        HBASE_COMPANY_NEWS_ANNOUNCESTATISTICS_URL = _environment.getProperty("hbase.company.news.announcestatistics.url");
        HBASE_COMPANY_NEWS_ANNOUNCE_URL = _environment.getProperty("hbase.company.news.announce.url");
        HBASE_COMPANY_NEWS_TRENDNEWS_URL = _environment.getProperty("hbase.company.news.trendnews.url");
        HBASE_COMPANY_NEWS_WORDSFUZZY_URL = _environment.getProperty("hbase.company.news.wordsfuzzy.url");
        HBASE_COMPANY_NEWS_TRENDNEWSLIST_URL = _environment.getProperty("hbase.company.news.trendnewslist.url");
        HBASE_COMPANY_NEWS_RELATEDSTATISTICS_URL = _environment.getProperty("hbase.company.news.relatedstatistics.url");
        HBASE_COMPANY_NEWS_RELATEDNEWSLIST_URL = _environment.getProperty("hbase.company.news.relatednewslist.url");
        HBASE_COMPANY_NEWS_DETAIL_URL = _environment.getProperty("hbase.company.news.detail.url");

        //展台私募信息
        HBASE_COMPANY_PFUND_RECORDINFO_URL = _environment.getProperty("hbase.company.pfund.recordinfo.url");
        HBASE_COMPANY_PFUND_LEGREPRESENTLIST_URL = _environment.getProperty("hbase.company.pfund.legrepresentlist.url");
        HBASE_COMPANY_PFUND_EXECUTIVESLIST_URL = _environment.getProperty("hbase.company.pfund.executiveslist.url");
        HBASE_COMPANY_PFUND_SHAREHOLDERLIST_URL = _environment.getProperty("hbase.company.pfund.shareholderlist.url");
        HBASE_COMPANY_PFUND_PRODUCTLIST_URL = _environment.getProperty("hbase.company.pfund.productlist.url");
        HBASE_COMPANY_PFUND_CREDIBILITYINFO_URL = _environment.getProperty("hbase.company.pfund.credibilityInfo.url");
        HBASE_COMPANY_PFUND_DATAEXCEPTION_URL = _environment.getProperty("hbase.company.pfund.dataexception.url");

        //企业高级搜索
        HBASE_COMPANY_SEARCH_COMPANYTYPE_URL = _environment.getProperty("hbase.company.search.companytype.url");
        HBASE_COMPANY_SEARCH_INDUSTRY_URL = _environment.getProperty("hbase.company.search.industry.url");
        HBASE_COMPANY_SEARCH_REGREGION_URL = _environment.getProperty("hbase.company.search.regregion.url");
        HBASE_COMPANY_SEARCH_COMPANYST_URL = _environment.getProperty("hbase.company.search.companyst.url");
        
        //风险信息高级搜索
        HBASE_RISK_SEARCH_RISKYTYPE_URL = _environment.getProperty("hbase.risk.search.risktype.url");
        
        //发债企业高级搜索
        HBASE_BOND_COMPANY_SEARCH_BONDDEFAULTSSTA_URL = _environment.getProperty("hbase.bondcompany.search.bonddefaultssta.url");
        HBASE_BOND_COMPANY_SEARCH_BONDDEFAULTSSTALIST_URL = _environment.getProperty("hbase.bondcompany.search.bonddefaultsstalist.url");
        HBASE_BOND_COMPANY_SEARCH_RATINGDOWNSTA_URL = _environment.getProperty("hbase.bondcompany.search.ratingdownsta.url");
        HBASE_BOND_COMPANY_SEARCH_RATINGDOWNSTALIST_URL = _environment.getProperty("hbase.bondcompany.search.ratingdownstalist.url");
        HBASE_BOND_COMPANY_SEARCH_FINANCIALRISKSTA_URL = _environment.getProperty("hbase.bondcompany.search.financialrisksta.url");
        HBASE_BOND_COMPANY_SEARCH_FINANCIALRISKSTALIST_URL = _environment.getProperty("hbase.bondcompany.search.financialriskstalist.url");
        HBASE_BOND_COMPANY_SEARCH_BONDISSUERCURRENT_URL = _environment.getProperty("hbase.bondcompany.search.bondissuercurrent.url");
        HBASE_BOND_COMPANY_SEARCH_BONDISSUERWITHINYEAR_URL = _environment.getProperty("hbase.bondcompany.search.bondissuerwithinyear.url");
        HBASE_BOND_COMPANY_SEARCH_BONDISSUERDURATION_URL = _environment.getProperty("hbase.bondcompany.search.bondissuerduration.url");
        HBASE_BOND_COMPANY_SEARCH_BONDMARKETPUBLISHSTA_URL = _environment.getProperty("hbase.bondcompany.search.bondmarketpublishsta.url");
        HBASE_BOND_COMPANY_SEARCH_BONDMARKETFINANCINGSTA_URL = _environment.getProperty("hbase.bondcompany.search.bondmarketfinancingsta.url");
        HBASE_BOND_COMPANY_SEARCH_BONDAREAPUBLISHTOP10_URL = _environment.getProperty("hbase.bondcompany.search.bondareapublishtop10.url");
        HBASE_BOND_COMPANY_SEARCH_MAXPROVINCE_URL = _environment.getProperty("hbase.bondcompany.search.maxprovince.url");
        HBASE_BOND_COMPANY_SEARCH_PUBLISHPROVINCE_URL = _environment.getProperty("hbase.bondcompany.search.publishprovince.url");
        HBASE_BOND_COMPANY_SEARCH_INVESTGRADE_URL = _environment.getProperty("hbase.bondcompany.search.investgrade.url");
        HBASE_BOND_COMPANY_SEARCH_LASTRATING_URL = _environment.getProperty("hbase.bondcompany.search.lastrating.url");
        HBASE_BOND_COMPANY_SEARCH_RATINGOUTLOOK_URL = _environment.getProperty("hbase.bondcompany.search.ratingoutlook.url");
        HBASE_BOND_COMPANY_SEARCH_BONDTYPE_URL = _environment.getProperty("hbase.bondcompany.search.bondtype.url");
        HBASE_BOND_COMPANY_SEARCH_AUDITOPINION_URL = _environment.getProperty("hbase.bondcompany.search.auditopinion.url");
        HBASE_BOND_COMPANY_SEARCH_INDUSTRYLIST_URL = _environment.getProperty("hbase.bondcompany.search.industrylist.url");
        HBASE_BOND_COMPANY_SEARCH_FINANCIALCHANGE_URL = _environment.getProperty("hbase.bondcompany.search.financialchange.url");
        
        //私募首页
        HBASE_PFCOMPY_PFTIPINFO_URL = _environment.getProperty("hbase.pfcompy.pftipinfo.url");
        HBASE_PFCOMPY_PFWARNINGS_URL = _environment.getProperty("hbase.pfcompy.pfwarnings.url");
        HBASE_PFCOMPY_REVOKELIST_URL = _environment.getProperty("hbase.pfcompy.revokelist.url");

        //私募机构高级搜索
        HBASE_PRIVATE_EQUITY_SEARCH_RISKGRADE_URL = _environment.getProperty("hbase.privateequity.search.riskgrade.url");
        HBASE_PRIVATE_EQUITY_SEARCH_RISKTYPE_URL = _environment.getProperty("hbase.privateequity.search.risktype.url");
        HBASE_PRIVATE_EQUITY_SEARCH_FUNDRECORD_URL = _environment.getProperty("hbase.privateequity.search.fundrecord.url");
        HBASE_PRIVATE_EQUITY_SEARCH_ORGTYPE_URL = _environment.getProperty("hbase.privateequity.search.orgtype.url");
        HBASE_PRIVATE_EQUITY_SEARCH_MANAGETYPE_URL = _environment.getProperty("hbase.privateequity.search.managetype.url");
        HBASE_PRIVATE_EQUITY_SEARCH_OPERATIONSTATUS_URL = _environment.getProperty("hbase.privateequity.search.operationstatus.url");
        HBASE_PRIVATE_EQUITY_SEARCH_INTEGRITYINFO_URL = _environment.getProperty("hbase.privateequity.search.integrityinfo.url");
        HBASE_PRIVATE_EQUITY_SEARCH_TIPS_URL = _environment.getProperty("hbase.privateequity.search.tips.url");
        
        //找咨询/新闻舆情高级搜索
        HBASE_NEWS_SEARCH_TYPE24H_URL = _environment.getProperty("hbase.news.search.type24h.url");
        HBASE_NEWS_SEARCH_NEGATIVE24H_URL = _environment.getProperty("hbase.news.search.negative24h.url");
        HBASE_NEWS_SEARCH_NEGATIVETOP10_URL1 = _environment.getProperty("hbase.news.search.negativetop10.url1");//企业舆情排行榜: （1日以内，3日以内，1周以内，1月以内）
        HBASE_NEWS_SEARCH_NEGATIVETOP10_URL3 = _environment.getProperty("hbase.news.search.negativetop10.url3");//企业舆情排行榜: （1日以内，3日以内，1周以内，1月以内）
        HBASE_NEWS_SEARCH_NEGATIVETOP10_URL7 = _environment.getProperty("hbase.news.search.negativetop10.url7");//企业舆情排行榜: （1日以内，3日以内，1周以内，1月以内）
        HBASE_NEWS_SEARCH_NEGATIVETOP10_URL30 = _environment.getProperty("hbase.news.search.negativetop10.url30");//企业舆情排行榜: （1日以内，3日以内，1周以内，1月以内）
        HBASE_NEWS_SEARCH_MEDIATYPE_URL = _environment.getProperty("hbase.news.search.mediatype.url");
        HBASE_NEWS_SEARCH_PUBLISHTIME_URL = _environment.getProperty("hbase.news.search.publishtime.url");
        HBASE_NEWS_SEARCH_RISKTYPELABEL_URL = _environment.getProperty("hbase.news.search.risktypelabel.url");
        HBASE_NEWS_SEARCH_NEWSDETAIL_URL = _environment.getProperty("hbase.news.search.newsdetail.url");
        HBASE_NEWS_SEARCH_RELATECOMPANY_URL = _environment.getProperty("hbase.news.search.relatecompany.url");
        
        //企业高级搜索solr查询接口
        SOLR_SERVICE_COMPANY_URL = _environment.getProperty("cscs.portal.solr.company.search");
        //风险信息高级搜索solr查询接口
        SOLR_SERVICE_RISK_URL = _environment.getProperty("cscs.portal.solr.risk.search");
        //私募机构高级搜索solr查询接口
        SOLR_SERVICE_PFUND_URL = _environment.getProperty("cscs.portal.solr.pfund.search");
        
        
        //高级搜搜solr服务url
        SOLR_SERVICE_URL35 = _environment.getProperty("cscs.portal.solr.url35");
        SOLR_SERVICE_URL36 = _environment.getProperty("cscs.portal.solr.url36");
        SOLR_SERVICE_URL37 = _environment.getProperty("cscs.portal.solr.url37");
        
        //高级搜索solr服务方法名称
        SOLR_SERVICE_COMPANY_METHOD = _environment.getProperty("cscs.portal.solr.company.method");
        SOLR_SERVICE_RISK_METHOD = _environment.getProperty("cscs.portal.solr.risk.method");
        SOLR_SERVICE_PFUND_METHOD = _environment.getProperty("cscs.portal.solr.pfund.method");
        SOLR_SERVICE_SENTIMENT_METHOD = _environment.getProperty("cscs.portal.solr.sentiment.method");
        SOLR_SERVICE_BOND_METHOD = _environment.getProperty("cscs.portal.solr.bond.method");
        SOLR_SERVICE_PERSON_METHOD = _environment.getProperty("cscs.portal.solr.person.method");
        SOLR_SERVICE_CREDIT_METHOD = _environment.getProperty("cscs.portal.solr.credit.method");


        //es索引名称-企业高级搜索
        ES_INDEX_NAME_COMPANY_INFO_SEARCH = _environment.getProperty("es.index.name.company.info.search");
        //es索引名称-风险高级搜索
        ES_INDEX_NAME_ENTERPRISE_RISK_SEARCH = _environment.getProperty("es.index.name.enterprise.risk.search");

        //es索引名称-新闻高级搜索
        ES_INDEX_NEWS_NAME = _environment.getProperty("es.index.news.name");

        ES_COMPANY_RISK_DOCUMENTINFO_URL = _environment.getProperty("es.company.risk.documentinfo.url");

        HBASE_COMPANY_RISK_COURTNOTICE_URL= _environment.getProperty("hbase.company.risk.courtnotice.url");
        HBASE_COMPANY_RISK_JUDASSIT_URL= _environment.getProperty("hbase.company.risk.judassit.url");
        HBASE_COMPANY_RISK_RELATEDPART_URL= _environment.getProperty("hbase.company.risk.relatedpart.url");


    }
}

