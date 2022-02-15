import fr.neutronstars.database.api.Database;
import fr.neutronstars.database.api.Query;
import fr.neutronstars.database.core.DatabaseImpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Random;

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

public class DatabaseTest
{
    private static Object rand(Random random, Object o) {
        return (o instanceof Object[]) ? rand(random, ((Object[]) o)[random.nextInt(((Object[]) o).length)]) : o;
    }

    public static void main(String[] args)
    {
        try {
            Database<Connection, ResultSet> database = DatabaseImpl.create("yugioh", "root", "")
                    .connect();

            System.out.println(
                    database.query("cards", "c")
                            .select("c.*")
                            .join("cards_translate", "t", "t.id=c.id")
                            .where(
                                    "c.id",
                                    Query.WhereCondition.NOT_IN,
                                    database.query("players", "p").select("p.id")
                            )
                            .where("c.rarity=:rarity", Query.Where.AND)
                            .groupBy("c.type")
                            .orderBy("c.level", Query.Order.DESC)
                            .limit(15, 45)
                            .build()
            );

            System.out.println("-------------------");

            System.out.println(
                    database.query("players")
                            .insertInto(Arrays.asList("pseudo", "created_at"), ":pseudo", "NOW()")
                            .onDuplicateKey("pseudo=:pseudo")
                            .build()
            );

            System.out.println("-------------------");

            System.out.println(
                    database.query("players")
                            .insertInto(
                                    Arrays.asList("pseudo", "created_at"),
                                    Arrays.asList(":pseudo1", "NOW()"),
                                    Arrays.asList(":pseudo2", "NOW()"),
                                    Arrays.asList(":pseudo3", "NOW()"),
                                    Arrays.asList(":pseudo4", "NOW()"),
                                    Arrays.asList(":pseudo5", "NOW()")
                            )
                            .build()
            );

            System.out.println("-------------------");

            System.out.println(
                    database.query("players", "p")
                            .update("p.pseudo=:pseudo", "p.points=:points")
                            .where("p.id", Query.WhereCondition.IS, ":id")
                            .build()
            );

            System.out.println("-------------------");

            System.out.println(
                    database.query("players", "p")
                            .delete()
                            .where("p.id=:id")
                            .build()
            );

            System.out.println("-------------------");

            System.out.println(
                    database.query("players", "p")
                        .select("*")
                        .join("vehicles v", "p.vehicle = v.id")
                        .union("vehicles v")
                        .select("*")
                        .join("players p", "p.vehicle = v.id")
                        .build()
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
