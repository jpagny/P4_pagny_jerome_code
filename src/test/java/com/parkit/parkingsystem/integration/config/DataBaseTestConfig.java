package com.parkit.parkingsystem.integration.config;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.util.PropertyUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataBaseTestConfig extends DataBaseConfig {

    private static final Logger logger = LogManager.getLogger("DataBaseTestConfig");
    private Properties property;

    @Override
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        logger.info("Create DB connection");
        Class.forName("com.mysql.cj.jdbc.Driver");
        property = PropertyUtil.loadConfProperty();
        return DriverManager.getConnection(
                property.getProperty("db.test.url"), property.getProperty("db.test.user"), property.getProperty("db.test.password"));
    }
}
