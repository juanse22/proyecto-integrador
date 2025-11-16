import java.sql.*;
import java.util.*;

/**
 * Programa para escanear y analizar la estructura completa de las bases de datos
 */
public class EscanearBaseDatos {
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "missgarro234";

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("   ESCANER DE ESTRUCTURA DE BASES DE DATOS - SISTEMA AREMI     ");
        System.out.println("================================================================\n");

        // Escanear ambas bases de datos
        String[] databases = {"salon_aremi", "gestion_clientes"};

        for (String dbName : databases) {
            escanearBaseDatos(dbName);
        }

        System.out.println("\n================================================================");
        System.out.println("                  ESCANEO COMPLETADO                            ");
        System.out.println("================================================================");
    }

    private static void escanearBaseDatos(String dbName) {
        String url = "jdbc:mysql://127.0.0.1:3306/" + dbName;

        System.out.println("\n======================================================================");
        System.out.println("  BASE DE DATOS: " + dbName.toUpperCase());
        System.out.println("======================================================================");

        try (Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD)) {
            DatabaseMetaData metaData = conn.getMetaData();

            // Obtener todas las tablas
            ResultSet tables = metaData.getTables(dbName, null, "%", new String[]{"TABLE"});

            List<String> tableNames = new ArrayList<>();
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
            tables.close();

            if (tableNames.isEmpty()) {
                System.out.println("\n  [!] No se encontraron tablas en esta base de datos\n");
                return;
            }

            System.out.println("\n  Total de tablas encontradas: " + tableNames.size());
            System.out.println();

            // Analizar cada tabla
            for (String tableName : tableNames) {
                analizarTabla(conn, metaData, dbName, tableName);
            }

        } catch (SQLException e) {
            System.err.println("[X] Error al conectar a " + dbName + ": " + e.getMessage());
        }
    }

    private static void analizarTabla(Connection conn, DatabaseMetaData metaData, String dbName, String tableName) throws SQLException {
        System.out.println("+--------------------------------------------------------------------+");
        System.out.println("|  TABLA: " + String.format("%-58s", tableName.toUpperCase()) + "|");
        System.out.println("+--------------------------------------------------------------------+");

        // Obtener columnas
        ResultSet columns = metaData.getColumns(dbName, null, tableName, null);

        System.out.println("|  COLUMNAS:                                                         |");
        System.out.println("|  " + String.format("%-25s %-20s %-10s", "NOMBRE", "TIPO", "NULLABLE") + "     |");
        System.out.println("|  ----------------------------------------------------------------  |");

        List<ColumnInfo> columnList = new ArrayList<>();
        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String columnType = columns.getString("TYPE_NAME");
            int columnSize = columns.getInt("COLUMN_SIZE");
            String isNullable = columns.getString("IS_NULLABLE");
            String columnDefault = columns.getString("COLUMN_DEF");
            String autoIncrement = columns.getString("IS_AUTOINCREMENT");
            String extra = columns.getString("REMARKS");

            String typeDisplay = columnType;
            if (columnType.equals("VARCHAR") || columnType.equals("CHAR")) {
                typeDisplay = columnType + "(" + columnSize + ")";
            } else if (columnType.equals("DECIMAL")) {
                int decimalDigits = columns.getInt("DECIMAL_DIGITS");
                typeDisplay = columnType + "(" + columnSize + "," + decimalDigits + ")";
            }

            ColumnInfo colInfo = new ColumnInfo();
            colInfo.name = columnName;
            colInfo.type = typeDisplay;
            colInfo.isNullable = isNullable.equals("YES");
            colInfo.defaultValue = columnDefault;
            colInfo.isAutoIncrement = autoIncrement.equals("YES");
            columnList.add(colInfo);

            String nullStr = isNullable.equals("YES") ? "NULL" : "NOT NULL";
            String aiStr = autoIncrement.equals("YES") ? " AUTO_INC" : "";

            System.out.println("|  " + String.format("%-25s %-20s %-10s",
                columnName, typeDisplay, nullStr) + aiStr + "  |");
        }
        columns.close();

        // Obtener claves primarias
        ResultSet primaryKeys = metaData.getPrimaryKeys(dbName, null, tableName);
        List<String> pkList = new ArrayList<>();
        while (primaryKeys.next()) {
            pkList.add(primaryKeys.getString("COLUMN_NAME"));
        }
        primaryKeys.close();

        if (!pkList.isEmpty()) {
            System.out.println("|                                                                    |");
            System.out.println("|  [PK] PRIMARY KEY: " + String.format("%-47s", String.join(", ", pkList)) + "|");
        }

        // Obtener claves foráneas (importadas)
        ResultSet importedKeys = metaData.getImportedKeys(dbName, null, tableName);
        List<String> fkList = new ArrayList<>();
        while (importedKeys.next()) {
            String fkColumn = importedKeys.getString("FKCOLUMN_NAME");
            String pkTable = importedKeys.getString("PKTABLE_NAME");
            String pkColumn = importedKeys.getString("PKCOLUMN_NAME");
            fkList.add(fkColumn + " -> " + pkTable + "(" + pkColumn + ")");
        }
        importedKeys.close();

        if (!fkList.isEmpty()) {
            System.out.println("|                                                                    |");
            System.out.println("|  [FK] FOREIGN KEYS:                                                |");
            for (String fk : fkList) {
                System.out.println("|     " + String.format("%-64s", fk) + "|");
            }
        }

        // Obtener índices
        ResultSet indexes = metaData.getIndexInfo(dbName, null, tableName, false, false);
        Map<String, List<String>> indexMap = new LinkedHashMap<>();
        while (indexes.next()) {
            String indexName = indexes.getString("INDEX_NAME");
            if (indexName != null && !indexName.equals("PRIMARY")) {
                String columnName = indexes.getString("COLUMN_NAME");
                indexMap.computeIfAbsent(indexName, k -> new ArrayList<>()).add(columnName);
            }
        }
        indexes.close();

        if (!indexMap.isEmpty()) {
            System.out.println("|                                                                    |");
            System.out.println("|  [IDX] INDICES:                                                    |");
            for (Map.Entry<String, List<String>> entry : indexMap.entrySet()) {
                String indexInfo = entry.getKey() + " (" + String.join(", ", entry.getValue()) + ")";
                if (indexInfo.length() > 64) indexInfo = indexInfo.substring(0, 61) + "...";
                System.out.println("|     " + String.format("%-64s", indexInfo) + "|");
            }
        }

        // Contar registros
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("|                                                                    |");
                System.out.println("|  Total de registros: " + String.format("%-46s", count) + "|");
            }
        } catch (SQLException e) {
            // Ignorar errores de conteo
        }

        System.out.println("+--------------------------------------------------------------------+");
        System.out.println();
    }

    static class ColumnInfo {
        String name;
        String type;
        boolean isNullable;
        String defaultValue;
        boolean isAutoIncrement;
    }
}