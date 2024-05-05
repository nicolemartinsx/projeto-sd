
public class AuthenticationModel {

    private static AuthenticationModel instance;
    
    private Boolean candidato;
    private String email;
    private String token;

    private AuthenticationModel() {
    }

    public static synchronized AuthenticationModel getInstance() {
        if (instance == null) {
            instance = new AuthenticationModel();
        }
        return instance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getCandidato() {
        return candidato;
    }

    public void setCandidato(Boolean candidato) {
        this.candidato = candidato;
    }
}
