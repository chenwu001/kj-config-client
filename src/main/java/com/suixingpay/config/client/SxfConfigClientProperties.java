package com.suixingpay.config.client;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.core.env.Environment;

import com.suixingpay.config.client.exception.UnSetApplicationNameException;
import com.suixingpay.config.client.exception.UnSetProfileException;

import lombok.Data;

/**
 * @author: qiujiayu[qiu_jy@suixingpay.com]
 * @date: 2017年9月8日 下午4:04:20
 * @version: V1.0
 * @review: qiujiayu[qiu_jy@suixingpay.com]/2017年9月8日 下午4:04:20
 */
@Data
public class SxfConfigClientProperties {

    public static final String PREFIX = "kj.config";

    /**
     * 应用名称
     */
    private String name;

    /**
     * 运行环境
     */
    private String profile;

    /**
     * 是否启用配置中心. 默认 true;
     */
    private boolean enabled = true;

    /**
     * 配置中心URI
     */
    private List<String> uris;

    /**
     * 配置文件本地缓存路径
     */
    private String cachePath = "./config";

    /**
     * 本地缓存过期时间(单位：秒),如果小于等于0时，一直有效
     */
    private int cacheTimeOut = 0;

    /**
     * 快速失败
     */
    private boolean failFast = false;

    /**
     * 用户名
     **/
    private String username;

    /**
     * 密码
     **/
    private String password = "";

    /**
     * 本机IP
     */
    private String ipAddress;

    /**
     * Management Port
     */
    private String managementPort;

    /**
     * Management Context Path
     */
    private String managementContextPath;

    private Environment environment;

    private InetUtils inetUtils;
    /**
      * 覆盖非系统属性　true不覆盖
    */
   private boolean overrideNone = false;
    /**
     * 覆盖系统属性 false不覆盖 true 覆盖
     */
    private boolean overrideSystemProperties = true;

    public SxfConfigClientProperties(Environment environment, InetUtils inetUtils) {
        this.environment = environment;
        this.inetUtils = inetUtils;
    }

    @PostConstruct
    public void init() {
        if (null == profile || profile.isEmpty()) {
            String[] profiles = environment.getActiveProfiles();
            if (profiles.length == 1) {
                this.setProfile(profiles[0]);
            }
            if (null == profile || profile.isEmpty()) {
                throw new UnSetProfileException("profile is empty!");
            }
        }
        if (null == name || name.isEmpty()) {
            String applicationName = environment.getProperty("spring.application.name");
            this.setName(applicationName);
        }
        if (null == name || name.isEmpty()) {
            throw new UnSetApplicationNameException("application name is empty!");
        }
        if ("bootstrap".equals(name) || "application".equals(name)) {
            throw new UnSetApplicationNameException("application name can't be \"bootstrap\" and \"application\"!");
        }
        if (null == ipAddress || ipAddress.isEmpty()) {
            ipAddress = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
        }
        String envManagementPort = environment.getProperty("management.server.port");
        if (null == this.managementPort) {
            String managementPortStr = envManagementPort;
            if (null == managementPortStr || managementPortStr.isEmpty()) {
                managementPortStr = environment.getProperty("server.port");
            }
            if (null != managementPortStr && !managementPortStr.isEmpty()) {
                this.managementPort = managementPortStr;
            }
        }
        if (null == this.managementPort) {
            this.managementPort = "8080";
        }
        if (null == managementContextPath || managementContextPath.isEmpty()) {
            String contextPath;
            if (null == envManagementPort || envManagementPort.isEmpty()) {
                contextPath = environment.getProperty("server.servlet.context-path", "");
            } else {
                contextPath = environment.getProperty("management.server.servlet.context-path", "");
            }
            managementContextPath = contextPath
                    + environment.getProperty("management.endpoints.web.base-path", "/actuator");
        }
    }
}