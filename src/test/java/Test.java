import fr.neutronstars.database.api.annotation.Column;
import fr.neutronstars.database.api.annotation.Table;

@Table
public class Test
{
    @Column(
        type = "integer(10)",
        key = Column.Key.PRIMARY,
        autoIncrement = true
    )
    private int id;

    @Column
    private String name;

    public int getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
