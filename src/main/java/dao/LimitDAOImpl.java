package dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class LimitDAOImpl implements LimitDAO, Serializable {
    Properties properties;

    private static final String findMinQuery = "SELECT l.limit_value FROM traffic_limits.limits_per_hour l WHERE l.limit_name = 'min' ORDER BY l.effective_date DESC";
    private static final String findMaxQuery = "SELECT l.limit_value FROM traffic_limits.limits_per_hour l WHERE l.limit_name = 'max' ORDER BY l.effective_date DESC";

    public LimitDAOImpl(Properties properties) {
        this.properties = properties;
    }


    @Override
    public Long findMin() {
        try {
            Connection connection = DriverManager.getConnection(properties.getPropertyByName("URL"), properties.getPropertyByName("USER"), properties.getPropertyByName("PASSWORD"));
            PreparedStatement statement = connection.prepareStatement(findMinQuery);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            long min = resultSet.getLong("limit_value");
            statement.close();
            resultSet.close();
            connection.close();
            return min;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Long findMax() {
        try {
            Connection connection = DriverManager.getConnection(properties.getPropertyByName("URL"), properties.getPropertyByName("USER"), properties.getPropertyByName("PASSWORD"));
            PreparedStatement statement = connection.prepareStatement(findMaxQuery);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            long max = resultSet.getLong("limit_value");
            statement.close();
            resultSet.close();
            connection.close();
            return max;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}