
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        Client.main(args);

        String[] serverArgs = {"22222"};
        Server.main(serverArgs);
    }

}
