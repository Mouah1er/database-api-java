import fr.neutronstars.database.api.Database;
import fr.neutronstars.database.core.DatabaseImpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

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
        Consumer<Test> consumer = test -> System.out.println(
            test.getId() + " " + test.getName() + " [" + (test.getId() == 0 ? "NO " : "") + "PERSISTED]"
        );
        try {
            Database<Connection, ResultSet> database = DatabaseImpl
                    .create("127.0.0.1", "3307", "test", "root", "")
                    .connect();

            TestRepository repository = new TestRepository(database);
            System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*");
            repository
                .findAll()
                .forEach(consumer);
            System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*");
            repository
                .find(2)
                .ifPresent(consumer);
            System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*");
            int checkId = 4;
            Test test = repository.find(checkId)
                .orElse(new Test());
            if (test.getId() != checkId) {
                test.setName("Mike Brown");
            } else {
                test.setName("Michel");
            }
            consumer.accept(test);
            repository.persist(test);
            System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*");
            repository
                .find(checkId)
                .ifPresent(consumer);
            System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*");
            repository
                .findAll()
                .forEach(consumer);
            System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
