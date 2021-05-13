import fr.neutronstars.database.api.Database;
import fr.neutronstars.database.core.DatabaseImpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

/**
 * Copyright 2021 NeutronStars
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class DatabaseTestTwo
{
    public static void main(String[] args)
    {

        try {
            Database<Connection, ResultSet> database = DatabaseImpl
                    .create("query_java_test", "root", "")
                    .connect();

            database.query("players")
                    .insertInto(
                            new String[]{"name", "created_at"},
                            new String[]{"?", "?"}
                    )
                    .execute("Mike Brown", Calendar.getInstance().getTime());

            ResultSet resultSet = database.query("players")
                    .select("*")
                    .getResult();

            while (resultSet.next()) {
                System.out.println(
                        resultSet.getString("name")
                        + " "
                        + resultSet.getDate("created_at")
                );
            }

            database.close(resultSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
