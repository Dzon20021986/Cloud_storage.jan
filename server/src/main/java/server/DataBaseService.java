package server;

import java.sql.*;

public class DataBaseService {
    // Объект, в котором будет храниться соединение с БД
    private static Connection connection;
    // Statement используется для того, чтобы выполнить postgresql-запрос
    private static Statement stmt;

    static void connect() throws SQLException {   // соединение
        try {
            Class.forName("org.postgresql.Driver");
            //Выполняем подключение к базе данных.
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cloud_db", "postgres", "admin");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    static void disconnect() {  // разъединить
        try {
            connection.close();  // закрыть соединение
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static String getIdByLoginAndPass(String login, String pass) throws SQLException {  // получить id по логину и паролю
        String postgresql = String.format("SELECT id FROM users where " +
                "user_name = '%s' and user_password = '%s'", login, pass);
        // В resultSet будет храниться результат нашего запроса,
        // который выполняется командой statement.executeQuery()
        ResultSet rs = stmt.executeQuery(postgresql);

        if (rs.next()) {
            return rs.getString(1);
        }
        return null;
    }
}
