import fr.neutronstars.database.api.Database;
import fr.neutronstars.database.core.AbstractRepository;

import java.sql.Connection;
import java.sql.ResultSet;

public class TestRepository extends AbstractRepository<Test>
{
    public TestRepository(Database<Connection, ResultSet> database)
    {
        super(database);
    }
}
