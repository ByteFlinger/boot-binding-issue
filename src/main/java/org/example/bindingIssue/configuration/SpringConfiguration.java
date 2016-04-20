package org.example.bindingIssue.configuration;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.example.bindingIssue.service.OtherService;
import org.example.bindingIssue.service.SomeService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.transaction.compensating.manager.ContextSourceTransactionManager;
import org.springframework.ldap.transaction.compensating.support.DifferentSubtreeTempEntryRenamingStrategy;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

@Log4j2
@Data
@Configuration
@ConfigurationProperties(prefix = "issue")
public class SpringConfiguration {

    @NotNull
    private String url;
    private String password;
    private String baseDn;

    @NotNull
    private String userDn;

    @Bean
    public SomeService service() {
        return new SomeService(contextSource());
    }

    @Bean
    public OtherService otherService() {
        OtherService otherService = new OtherService();
        otherService.setService(service());
        return otherService;
    }

    @Bean
    public ContextSource contextSource() {

        log.info("USERDN: '{}'", userDn);

        LdapContextSource contextSource = new LdapContextSource();

        contextSource.setUrl(url);
        contextSource.setReferral("follow");

        if (StringUtils.hasLength(baseDn)) {
            contextSource.setBase(baseDn);
        }

        contextSource.setUserDn(userDn);

        if (StringUtils.hasLength(password)) {
            contextSource.setPassword(password);
        }

        return contextSource;
    }

    @Bean
    public ContextSourceTransactionManager ldapTransactionManager() {
        ContextSourceTransactionManager tm = new ContextSourceTransactionManager();
        tm.setRenamingStrategy(new DifferentSubtreeTempEntryRenamingStrategy("ou=tempEntries"));
        return tm;
    }

    @Bean
    public TransactionProxyFactoryBean proxyFactoryBean()
            throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        TransactionProxyFactoryBean proxyBean = new TransactionProxyFactoryBean();
        proxyBean.setTransactionManager(ldapTransactionManager());
        proxyBean.setTarget(otherService());

        Properties prop = new Properties();
        prop.put("*", PROPAGATION_REQUIRES_NEW);
        proxyBean.setTransactionAttributes(prop);
        return proxyBean;
    }
}
