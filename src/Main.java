
import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        Client client = new Client();
        client.setVisible(true);

        String[] serverArgs = {"22222"};
        Server.main(serverArgs);
    }

}
