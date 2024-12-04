package com.company.medicine;

import io.jmix.autoconfigure.data.JmixLiquibaseCreator;
import io.jmix.core.JmixModules;
import io.jmix.core.Resources;
import io.jmix.data.impl.JmixEntityManagerFactoryBean;
import io.jmix.data.impl.JmixTransactionManager;
import io.jmix.data.persistence.DbmsSpecifics;
import jakarta.persistence.EntityManagerFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

@Configuration
public class MedicinedetailsStoreConfiguration {

    @Bean
    @ConfigurationProperties("medicinedetails.datasource")
    DataSourceProperties medicinedetailsDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "medicinedetails.datasource.hikari")
    DataSource medicinedetailsDataSource(@Qualifier("medicinedetailsDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean
    LocalContainerEntityManagerFactoryBean medicinedetailsEntityManagerFactory(
            @Qualifier("medicinedetailsDataSource") DataSource dataSource,
            JpaVendorAdapter jpaVendorAdapter,
            DbmsSpecifics dbmsSpecifics,
            JmixModules jmixModules,
            Resources resources
    ) {
        return new JmixEntityManagerFactoryBean("medicinedetails", dataSource, jpaVendorAdapter, dbmsSpecifics, jmixModules, resources);
    }

    @Bean
    JpaTransactionManager medicinedetailsTransactionManager(@Qualifier("medicinedetailsEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JmixTransactionManager("medicinedetails", entityManagerFactory);
    }

    @Bean("medicinedetailsLiquibaseProperties")
    @ConfigurationProperties(prefix = "medicinedetails.liquibase")
    public LiquibaseProperties medicinedetailsLiquibaseProperties() {
        return new LiquibaseProperties();
    }

    @Bean("medicinedetailsLiquibase")
    public SpringLiquibase medicinedetailsLiquibase(@Qualifier("medicinedetailsDataSource") DataSource dataSource,
                                                    @Qualifier("medicinedetailsLiquibaseProperties") LiquibaseProperties liquibaseProperties) {
        return JmixLiquibaseCreator.create(dataSource, liquibaseProperties);
    }
}
