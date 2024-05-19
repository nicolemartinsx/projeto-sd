
import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        
        Client.main(args);

        String[] serverArgs = {"22222"};
        Server.main(serverArgs);
    }

}
